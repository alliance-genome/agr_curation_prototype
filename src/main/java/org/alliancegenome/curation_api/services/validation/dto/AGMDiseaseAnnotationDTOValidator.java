package org.alliancegenome.curation_api.services.validation.dto;

import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AGMDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.AGMDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.AffectedGenomicModelService;
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.services.helpers.UniqueIdentifierHelper;
import org.alliancegenome.curation_api.services.helpers.annotations.AnnotationRetrievalHelper;
import org.alliancegenome.curation_api.services.helpers.annotations.AnnotationUniqueIdHelper;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AGMDiseaseAnnotationDTOValidator extends DiseaseAnnotationDTOValidator<AGMDiseaseAnnotation, AGMDiseaseAnnotationDTO> {

	@Inject
	AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
	@Inject
	AffectedGenomicModelService agmService;
	@Inject
	GeneService geneService;
	@Inject
	AlleleService alleleService;

	public AGMDiseaseAnnotation validateAGMDiseaseAnnotationDTO(AGMDiseaseAnnotationDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		response = new ObjectResponse<AGMDiseaseAnnotation>();
		
		AGMDiseaseAnnotation annotation = new AGMDiseaseAnnotation();
		AffectedGenomicModel agm = validateRequiredIdentifier(agmService, "agm_identifier", dto.getAgmIdentifier());

		Reference reference = validateRequiredReference(dto.getReferenceCurie());
		String refCurie = reference == null ? null : reference.getCurie();

		
		if (agm != null) {
			String uniqueId = AnnotationUniqueIdHelper.getDiseaseAnnotationUniqueId(dto, dto.getAgmIdentifier(), refCurie);
			String annotationId = UniqueIdentifierHelper.setAnnotationIdentifiers(dto, annotation, uniqueId);
			String identifyingField = UniqueIdentifierHelper.getIdentifyingField(dto);

			SearchResponse<AGMDiseaseAnnotation> annotationList = agmDiseaseAnnotationDAO.findByField(identifyingField, annotationId);
			annotation = AnnotationRetrievalHelper.getCurrentAnnotation(annotation, annotationList);
			annotation.setUniqueId(uniqueId);
			annotation.setDiseaseAnnotationSubject(agm);
			UniqueIdentifierHelper.setObsoleteAndInternal(dto, annotation);

			if (dataProvider != null
					&& (dataProvider.name().equals("RGD") || dataProvider.name().equals("HUMAN"))
					&& (!agm.getTaxon().getCurie().equals(dataProvider.canonicalTaxonCurie) || !dataProvider.sourceOrganization.equals(agm.getDataProvider().getAbbreviation()))) {
				response.addErrorMessage("agm_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAgmIdentifier() + ") for " + dataProvider.name() + " load");	
			}
		}
		annotation.setSingleReference(reference);

		annotation = validateDiseaseAnnotationDTO(annotation, dto);
	
		VocabularyTerm diseaseRelation = validateRequiredTermInVocabularyTermSet("disease_relation_name", dto.getDiseaseRelationName(), VocabularyConstants.AGM_DISEASE_RELATION_VOCABULARY_TERM_SET);
		annotation.setRelation(diseaseRelation);
		
		Gene inferredGene = validateIdentifier(geneService, "inferred_gene_identifier", dto.getInferredGeneIdentifier());
		annotation.setInferredGene(inferredGene);
		
		Allele inferredAllele = validateIdentifier(alleleService, "inferred_allele_identifier", dto.getInferredAlleleIdentifier());
		annotation.setInferredAllele(inferredAllele);
		
		List<Gene> assertedGenes = validateIdentifiers(geneService, "asserted_gene_identifiers", dto.getAssertedGeneIdentifiers());
		annotation.setAssertedGenes(assertedGenes);
		
		Allele assertedAllele = validateIdentifier(alleleService, "asserted_allele_identifier", dto.getAssertedAlleleIdentifier());
		annotation.setAssertedAllele(assertedAllele);
		
		if (response.hasErrors()) {
			throw new ObjectValidationException(dto, response.errorMessagesString());
		}

		return annotation;
	}
}
