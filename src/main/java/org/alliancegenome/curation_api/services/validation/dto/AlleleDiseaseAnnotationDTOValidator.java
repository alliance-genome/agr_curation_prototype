package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
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
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.UniqueIdentifierHelper;
import org.alliancegenome.curation_api.services.helpers.annotations.AnnotationRetrievalHelper;
import org.alliancegenome.curation_api.services.helpers.annotations.AnnotationUniqueIdHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleDiseaseAnnotationDTOValidator extends DiseaseAnnotationDTOValidator {

	@Inject AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
	@Inject AlleleService alleleService;
	@Inject GeneService geneService;
	@Inject VocabularyTermService vocabularyTermService;

	public AlleleDiseaseAnnotation validateAlleleDiseaseAnnotationDTO(AlleleDiseaseAnnotationDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		AlleleDiseaseAnnotation annotation = new AlleleDiseaseAnnotation();
		Allele allele;

		ObjectResponse<AlleleDiseaseAnnotation> adaResponse = new ObjectResponse<AlleleDiseaseAnnotation>();

		ObjectResponse<AlleleDiseaseAnnotation> refResponse = validateReference(annotation, dto);
		adaResponse.addErrorMessages(refResponse.getErrorMessages());
		Reference validatedReference = refResponse.getEntity().getSingleReference();
		String refCurie = validatedReference == null ? null : validatedReference.getCurie();

		if (StringUtils.isBlank(dto.getAlleleIdentifier())) {
			adaResponse.addErrorMessage("allele_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			allele = alleleService.findByIdentifierString(dto.getAlleleIdentifier());
			if (allele == null) {
				adaResponse.addErrorMessage("allele_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAlleleIdentifier() + ")");
			} else {
				String uniqueId = AnnotationUniqueIdHelper.getDiseaseAnnotationUniqueId(dto, dto.getAlleleIdentifier(), refCurie);
				String annotationId = UniqueIdentifierHelper.setAnnotationID(dto, annotation, uniqueId);
				String identifyingField = UniqueIdentifierHelper.getIdentifyingField(dto);

				SearchResponse<AlleleDiseaseAnnotation> annotationList = alleleDiseaseAnnotationDAO.findByField(identifyingField, annotationId);
				annotation = AnnotationRetrievalHelper.getCurrentAnnotation(annotation, annotationList);
				annotation.setUniqueId(uniqueId);
				annotation.setDiseaseAnnotationSubject(allele);
				UniqueIdentifierHelper.setObsoleteAndInternal(dto, annotation);

				if (dataProvider != null
					&& (dataProvider.name().equals("RGD") || dataProvider.name().equals("HUMAN"))
					&& (!allele.getTaxon().getCurie().equals(dataProvider.canonicalTaxonCurie) || !dataProvider.sourceOrganization.equals(allele.getDataProvider().getSourceOrganization().getAbbreviation()))) {
					adaResponse.addErrorMessage("allele_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAlleleIdentifier() + ") for " + dataProvider.name() + " load");
				}
			}
		}
		annotation.setSingleReference(validatedReference);

		ObjectResponse<AlleleDiseaseAnnotation> daResponse = validateDiseaseAnnotationDTO(annotation, dto);
		annotation = daResponse.getEntity();
		adaResponse.addErrorMessages(daResponse.getErrorMessages());

		if (StringUtils.isNotEmpty(dto.getDiseaseRelationName())) {
			VocabularyTerm diseaseRelation = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.ALLELE_DISEASE_RELATION_VOCABULARY_TERM_SET, dto.getDiseaseRelationName()).getEntity();
			if (diseaseRelation == null) {
				adaResponse.addErrorMessage("disease_relation_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getDiseaseRelationName() + ")");
			}
			annotation.setRelation(diseaseRelation);
		} else {
			adaResponse.addErrorMessage("disease_relation_name", ValidationConstants.REQUIRED_MESSAGE);
		}

		if (StringUtils.isNotBlank(dto.getInferredGeneIdentifier())) {
			Gene inferredGene = geneService.findByIdentifierString(dto.getInferredGeneIdentifier());
			if (inferredGene == null) {
				adaResponse.addErrorMessage("inferred_gene_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getInferredGeneIdentifier() + ")");
			}
			annotation.setInferredGene(inferredGene);
		} else {
			annotation.setInferredGene(null);
		}

		if (CollectionUtils.isNotEmpty(dto.getAssertedGeneIdentifiers())) {
			List<Gene> assertedGenes = new ArrayList<>();
			for (String assertedGeneIdentifier : dto.getAssertedGeneIdentifiers()) {
				Gene assertedGene = geneService.findByIdentifierString(assertedGeneIdentifier);
				if (assertedGene == null) {
					adaResponse.addErrorMessage("asserted_gene_identifiers", ValidationConstants.INVALID_MESSAGE + " (" + assertedGeneIdentifier + ")");
				} else {
					assertedGenes.add(assertedGene);
				}
			}
			annotation.setAssertedGenes(assertedGenes);
		} else {
			annotation.setAssertedGenes(null);
		}

		if (adaResponse.hasErrors()) {
			throw new ObjectValidationException(dto, adaResponse.errorMessagesString());
		}

		return annotation;
	}

}
