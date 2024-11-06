package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AGMDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
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
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.UniqueIdentifierHelper;
import org.alliancegenome.curation_api.services.helpers.annotations.AnnotationRetrievalHelper;
import org.alliancegenome.curation_api.services.helpers.annotations.AnnotationUniqueIdHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AGMDiseaseAnnotationDTOValidator extends DiseaseAnnotationDTOValidator {

	@Inject
	AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
	@Inject
	VocabularyTermService vocabularyTermService;
	@Inject
	AffectedGenomicModelService agmService;
	@Inject
	GeneService geneService;
	@Inject
	AlleleService alleleService;

	public AGMDiseaseAnnotation validateAGMDiseaseAnnotationDTO(AGMDiseaseAnnotationDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		AGMDiseaseAnnotation annotation = new AGMDiseaseAnnotation();
		AffectedGenomicModel agm;

		ObjectResponse<AGMDiseaseAnnotation> adaResponse = new ObjectResponse<AGMDiseaseAnnotation>();

		ObjectResponse<AGMDiseaseAnnotation> refResponse = validateReference(annotation, dto);
		adaResponse.addErrorMessages(refResponse.getErrorMessages());
		Reference validatedReference = refResponse.getEntity().getSingleReference();
		String refCurie = validatedReference == null ? null : validatedReference.getCurie();

		if (StringUtils.isBlank(dto.getAgmIdentifier())) {
			adaResponse.addErrorMessage("agm_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			agm = agmService.findByIdentifierString(dto.getAgmIdentifier());
			if (agm == null) {
				adaResponse.addErrorMessage("agm_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAgmIdentifier() + ")");
			} else {
				String uniqueId = AnnotationUniqueIdHelper.getDiseaseAnnotationUniqueId(dto, dto.getAgmIdentifier(), refCurie);
				String annotationId = UniqueIdentifierHelper.setAnnotationID(dto, annotation, uniqueId);
				String identifyingField = UniqueIdentifierHelper.getIdentifyingField(dto);

				SearchResponse<AGMDiseaseAnnotation> annotationList = agmDiseaseAnnotationDAO.findByField(identifyingField, annotationId);
				annotation = AnnotationRetrievalHelper.getCurrentAnnotation(annotation, annotationList);
				annotation.setUniqueId(uniqueId);
				annotation.setDiseaseAnnotationSubject(agm);
				UniqueIdentifierHelper.setObsoleteAndInternal(dto, annotation);

				if (dataProvider != null
					&& (dataProvider.name().equals("RGD") || dataProvider.name().equals("HUMAN"))
					&& (!agm.getTaxon().getCurie().equals(dataProvider.canonicalTaxonCurie) || !dataProvider.sourceOrganization.equals(agm.getDataProvider().getSourceOrganization().getAbbreviation()))) {
					adaResponse.addErrorMessage("agm_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAgmIdentifier() + ") for " + dataProvider.name() + " load");
				}
			}
		}
		annotation.setSingleReference(validatedReference);

		ObjectResponse<AGMDiseaseAnnotation> daResponse = validateDiseaseAnnotationDTO(annotation, dto);
		annotation = daResponse.getEntity();
		adaResponse.addErrorMessages(daResponse.getErrorMessages());

		if (StringUtils.isNotEmpty(dto.getDiseaseRelationName())) {
			VocabularyTerm diseaseRelation = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.AGM_DISEASE_RELATION_VOCABULARY_TERM_SET, dto.getDiseaseRelationName()).getEntity();
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

		if (StringUtils.isNotBlank(dto.getInferredAlleleIdentifier())) {
			Allele inferredAllele = alleleService.findByIdentifierString(dto.getInferredAlleleIdentifier());
			if (inferredAllele == null) {
				adaResponse.addErrorMessage("inferred_allele_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getInferredAlleleIdentifier() + ")");
			}
			annotation.setInferredAllele(inferredAllele);
		} else {
			annotation.setInferredAllele(null);
		}

		if (StringUtils.isNotBlank(dto.getAssertedAlleleIdentifier())) {
			Allele assertedAllele = alleleService.findByIdentifierString(dto.getAssertedAlleleIdentifier());
			if (assertedAllele == null) {
				adaResponse.addErrorMessage("asserted_allele_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAssertedAlleleIdentifier() + ")");
			}
			annotation.setAssertedAllele(assertedAllele);
		} else {
			annotation.setAssertedAllele(null);
		}

		if (adaResponse.hasErrors()) {
			throw new ObjectValidationException(dto, adaResponse.errorMessagesString());
		}

		return annotation;
	}
}
