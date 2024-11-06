package org.alliancegenome.curation_api.services.validation.dto;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.dao.GeneDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.AffectedGenomicModelService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.UniqueIdentifierHelper;
import org.alliancegenome.curation_api.services.helpers.annotations.AnnotationRetrievalHelper;
import org.alliancegenome.curation_api.services.helpers.annotations.AnnotationUniqueIdHelper;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneDiseaseAnnotationDTOValidator extends DiseaseAnnotationDTOValidator {

	@Inject GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
	@Inject AffectedGenomicModelService affectedGenomicModelService;
	@Inject NoteDAO noteDAO;
	@Inject ConditionRelationDAO conditionRelationDAO;
	@Inject VocabularyTermService vocabularyTermService;

	public GeneDiseaseAnnotation validateGeneDiseaseAnnotationDTO(GeneDiseaseAnnotationDTO dto, BackendBulkDataProvider dataProvider) throws ObjectValidationException {
		GeneDiseaseAnnotation annotation = new GeneDiseaseAnnotation();
		Gene gene;
		ObjectResponse<GeneDiseaseAnnotation> gdaResponse = new ObjectResponse<>();

		ObjectResponse<GeneDiseaseAnnotation> refResponse = validateReference(annotation, dto);
		gdaResponse.addErrorMessages(refResponse.getErrorMessages());
		Reference validatedReference = refResponse.getEntity().getSingleReference();

		String refCurie = validatedReference == null ? null : validatedReference.getCurie();

		if (StringUtils.isBlank(dto.getGeneIdentifier())) {
			gdaResponse.addErrorMessage("gene_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			gene = geneService.findByIdentifierString(dto.getGeneIdentifier());
			if (gene == null) {
				gdaResponse.addErrorMessage("gene_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGeneIdentifier() + ")");
			} else {
				String uniqueId = AnnotationUniqueIdHelper.getDiseaseAnnotationUniqueId(dto, dto.getGeneIdentifier(), refCurie);
				String annotationId = UniqueIdentifierHelper.setAnnotationID(dto, annotation, uniqueId);
				String identifyingField = UniqueIdentifierHelper.getIdentifyingField(dto);

				SearchResponse<GeneDiseaseAnnotation> annotationList = geneDiseaseAnnotationDAO.findByField(identifyingField, annotationId);
				annotation = AnnotationRetrievalHelper.getCurrentAnnotation(annotation, annotationList);
				annotation.setUniqueId(uniqueId);
				annotation.setDiseaseAnnotationSubject(gene);
				UniqueIdentifierHelper.setObsoleteAndInternal(dto, annotation);


				if (dataProvider != null && (dataProvider.name().equals("RGD") || dataProvider.name().equals("HUMAN")) && !gene.getTaxon().getCurie().equals(dataProvider.canonicalTaxonCurie)
					|| !dataProvider.sourceOrganization.equals(gene.getDataProvider().getSourceOrganization().getAbbreviation())) {
					gdaResponse.addErrorMessage("gene_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGeneIdentifier() + ") for " + dataProvider.name() + " load");
				}
			}
		}

		annotation.setSingleReference(validatedReference);

		AffectedGenomicModel sgdStrainBackground = null;
		if (StringUtils.isNotBlank(dto.getSgdStrainBackgroundIdentifier())) {
			sgdStrainBackground = affectedGenomicModelService.findByIdentifierString(dto.getSgdStrainBackgroundIdentifier());
			if (sgdStrainBackground == null || !sgdStrainBackground.getTaxon().getName().startsWith("Saccharomyces cerevisiae")) {
				gdaResponse.addErrorMessage("sgd_strain_background_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getSgdStrainBackgroundIdentifier() + ")");
			}
		}
		annotation.setSgdStrainBackground(sgdStrainBackground);

		ObjectResponse<GeneDiseaseAnnotation> daResponse = validateDiseaseAnnotationDTO(annotation, dto);
		annotation = daResponse.getEntity();
		gdaResponse.addErrorMessages(daResponse.getErrorMessages());

		if (StringUtils.isNotEmpty(dto.getDiseaseRelationName())) {
			VocabularyTerm diseaseRelation = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.GENE_DISEASE_RELATION_VOCABULARY_TERM_SET, dto.getDiseaseRelationName()).getEntity();
			if (diseaseRelation == null) {
				gdaResponse.addErrorMessage("disease_relation_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getDiseaseRelationName() + ")");
			}
			annotation.setRelation(diseaseRelation);
		} else {
			gdaResponse.addErrorMessage("disease_relation_name", ValidationConstants.REQUIRED_MESSAGE);
		}

		if (gdaResponse.hasErrors()) {
			throw new ObjectValidationException(dto, gdaResponse.errorMessagesString());
		}

		return annotation;
	}

}
