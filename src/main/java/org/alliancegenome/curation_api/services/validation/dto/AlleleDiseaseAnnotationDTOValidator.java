package org.alliancegenome.curation_api.services.validation.dto;

import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AlleleDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.services.helpers.UniqueIdentifierHelper;
import org.alliancegenome.curation_api.services.helpers.annotations.AnnotationRetrievalHelper;
import org.alliancegenome.curation_api.services.helpers.annotations.AnnotationUniqueIdHelper;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleDiseaseAnnotationDTOValidator extends DiseaseAnnotationDTOValidator<AlleleDiseaseAnnotation, AlleleDiseaseAnnotationDTO> {

	@Inject AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
	@Inject AlleleService alleleService;
	@Inject GeneService geneService;

	public AlleleDiseaseAnnotation validateAlleleDiseaseAnnotationDTO(AlleleDiseaseAnnotationDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		response = new ObjectResponse<AlleleDiseaseAnnotation>();
		
		AlleleDiseaseAnnotation annotation = new AlleleDiseaseAnnotation();
		Allele allele = validateRequiredIdentifier(alleleService, "allele_identifier", dto.getAlleleIdentifier());
		
		Reference reference = validateRequiredReference(dto.getReferenceCurie());
		String refCurie = reference == null ? null : reference.getCurie();

		if (allele != null) {
			String uniqueId = AnnotationUniqueIdHelper.getDiseaseAnnotationUniqueId(dto, dto.getAlleleIdentifier(), refCurie);
			String annotationId = UniqueIdentifierHelper.setAnnotationIdentifiers(dto, annotation, uniqueId);
			String identifyingField = UniqueIdentifierHelper.getIdentifyingField(dto);

			SearchResponse<AlleleDiseaseAnnotation> annotationList = alleleDiseaseAnnotationDAO.findByField(identifyingField, annotationId);
			annotation = AnnotationRetrievalHelper.getCurrentAnnotation(annotation, annotationList);
			annotation.setUniqueId(uniqueId);
			annotation.setDiseaseAnnotationSubject(allele);
			UniqueIdentifierHelper.setObsoleteAndInternal(dto, annotation);

			if (dataProvider != null
					&& (dataProvider.name().equals("RGD") || dataProvider.name().equals("HUMAN"))
					&& (!allele.getTaxon().getCurie().equals(dataProvider.canonicalTaxonCurie) || !dataProvider.sourceOrganization.equals(allele.getDataProvider().getAbbreviation()))) {
				response.addErrorMessage("allele_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAlleleIdentifier() + ") for " + dataProvider.name() + " load");
			}
		}
		annotation.setSingleReference(reference);

		annotation = validateDiseaseAnnotationDTO(annotation, dto);
		
		VocabularyTerm diseaseRelation = validateRequiredTermInVocabularyTermSet("disease_relation_name", dto.getDiseaseRelationName(), VocabularyConstants.ALLELE_DISEASE_RELATION_VOCABULARY_TERM_SET);
		annotation.setRelation(diseaseRelation);
		
		Gene inferredGene = validateIdentifier(geneService, "inferred_gene_identifier", dto.getInferredGeneIdentifier());
		annotation.setInferredGene(inferredGene);
		
		List<Gene> assertedGenes = validateIdentifiers(geneService, "asserted_gene_identifiers", dto.getAssertedGeneIdentifiers());
		annotation.setAssertedGenes(assertedGenes);
		
		
		if (response.hasErrors()) {
			throw new ObjectValidationException(dto, response.errorMessagesString());
		}

		return annotation;
	}

}
