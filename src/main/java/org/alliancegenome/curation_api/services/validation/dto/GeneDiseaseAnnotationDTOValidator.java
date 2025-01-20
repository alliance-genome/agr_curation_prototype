package org.alliancegenome.curation_api.services.validation.dto;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.GeneDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.AffectedGenomicModelService;
import org.alliancegenome.curation_api.services.helpers.UniqueIdentifierHelper;
import org.alliancegenome.curation_api.services.helpers.annotations.AnnotationRetrievalHelper;
import org.alliancegenome.curation_api.services.helpers.annotations.AnnotationUniqueIdHelper;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneDiseaseAnnotationDTOValidator extends DiseaseAnnotationDTOValidator<GeneDiseaseAnnotation, GeneDiseaseAnnotationDTO> {

	@Inject GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
	@Inject AffectedGenomicModelService affectedGenomicModelService;

	public GeneDiseaseAnnotation validateGeneDiseaseAnnotationDTO(GeneDiseaseAnnotationDTO dto, BackendBulkDataProvider dataProvider) throws ObjectValidationException {
		response = new ObjectResponse<GeneDiseaseAnnotation>();
		
		GeneDiseaseAnnotation annotation = new GeneDiseaseAnnotation();
		Gene gene = validateRequiredIdentifier(geneService, "gene_identifier", dto.getGeneIdentifier());
		
		Reference reference = validateRequiredReference(dto.getReferenceCurie());
		String refCurie = reference == null ? null : reference.getCurie();

		if (gene != null) {
			String uniqueId = AnnotationUniqueIdHelper.getDiseaseAnnotationUniqueId(dto, dto.getGeneIdentifier(), refCurie);
			String annotationId = UniqueIdentifierHelper.setAnnotationIdentifiers(dto, annotation, uniqueId);
			String identifyingField = UniqueIdentifierHelper.getIdentifyingField(dto);

			SearchResponse<GeneDiseaseAnnotation> annotationList = geneDiseaseAnnotationDAO.findByField(identifyingField, annotationId);
			annotation = AnnotationRetrievalHelper.getCurrentAnnotation(annotation, annotationList);
			annotation.setUniqueId(uniqueId);
			annotation.setDiseaseAnnotationSubject(gene);
			UniqueIdentifierHelper.setObsoleteAndInternal(dto, annotation);

			if (dataProvider != null
					&& (dataProvider.name().equals("RGD") || dataProvider.name().equals("HUMAN"))
					&& (!gene.getTaxon().getCurie().equals(dataProvider.canonicalTaxonCurie) || !dataProvider.sourceOrganization.equals(gene.getDataProvider().getAbbreviation()))) {
				response.addErrorMessage("allele_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGeneIdentifier() + ") for " + dataProvider.name() + " load");
			}
		}
		annotation.setSingleReference(reference);

		annotation = validateDiseaseAnnotationDTO(annotation, dto);

		AffectedGenomicModel sgdStrainBackground = validateIdentifier(affectedGenomicModelService, "sgd_strain_background_identifier", dto.getSgdStrainBackgroundIdentifier());
		if (sgdStrainBackground != null && !sgdStrainBackground.getTaxon().getName().startsWith("Saccharomyces cerevisiae")) {
			response.addErrorMessage("sgd_strain_background_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getSgdStrainBackgroundIdentifier() + ")");
		}
		annotation.setSgdStrainBackground(sgdStrainBackground);

		VocabularyTerm diseaseRelation = validateRequiredTermInVocabularyTermSet("disease_relation_name", dto.getDiseaseRelationName(), VocabularyConstants.GENE_DISEASE_RELATION_VOCABULARY_TERM_SET);
		annotation.setRelation(diseaseRelation);
		
		if (response.hasErrors()) {
			throw new ObjectValidationException(dto, response.errorMessagesString());
		}

		return annotation;
	}

}
