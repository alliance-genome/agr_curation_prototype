package org.alliancegenome.curation_api.services.validation;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.GeneDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.lang3.ObjectUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneDiseaseAnnotationValidator extends DiseaseAnnotationValidator {

	@Inject GeneDAO geneDAO;
	@Inject AffectedGenomicModelDAO agmDAO;
	@Inject GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
	
	private String errorMessage;

	public GeneDiseaseAnnotation validateAnnotationUpdate(GeneDiseaseAnnotation uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update Gene Disease Annotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No Gene Disease Annotation ID provided");
			throw new ApiErrorException(response);
		}
		GeneDiseaseAnnotation dbEntity = geneDiseaseAnnotationDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("Could not find Gene Disease Annotation with ID: [" + id + "]");
			throw new ApiErrorException(response);
			// do not continue validation for update if Disease Annotation ID has not been
			// found
		}

		return validateAnnotation(uiEntity, dbEntity);
	}

	public GeneDiseaseAnnotation validateAnnotationCreate(GeneDiseaseAnnotation uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Cound not create Gene Disease Annotation";

		GeneDiseaseAnnotation dbEntity = new GeneDiseaseAnnotation();

		return validateAnnotation(uiEntity, dbEntity);
	}

	public GeneDiseaseAnnotation validateAnnotation(GeneDiseaseAnnotation uiEntity, GeneDiseaseAnnotation dbEntity) {

		Gene subject = validateSubject(uiEntity, dbEntity);
		dbEntity.setDiseaseAnnotationSubject(subject);

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation", VocabularyConstants.GENE_DISEASE_RELATION_VOCABULARY_TERM_SET, dbEntity.getRelation(), uiEntity.getRelation());
		dbEntity.setRelation(relation);

		AffectedGenomicModel sgdStrainBackground = validateSgdStrainBackground(uiEntity, dbEntity);
		dbEntity.setSgdStrainBackground(sgdStrainBackground);

		dbEntity = (GeneDiseaseAnnotation) validateCommonDiseaseAnnotationFields(uiEntity, dbEntity);

		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		return dbEntity;
	}

	private Gene validateSubject(GeneDiseaseAnnotation uiEntity, GeneDiseaseAnnotation dbEntity) {
		String field = "diseaseAnnotationSubject";
		if (ObjectUtils.isEmpty(uiEntity.getDiseaseAnnotationSubject())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		Gene subjectEntity = null;
		if (uiEntity.getDiseaseAnnotationSubject().getId() != null) {
			subjectEntity = geneDAO.find(uiEntity.getDiseaseAnnotationSubject().getId());
		}
		if (subjectEntity == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (subjectEntity.getObsolete() && (dbEntity.getDiseaseAnnotationSubject() == null || !subjectEntity.getId().equals(dbEntity.getDiseaseAnnotationSubject().getId()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return subjectEntity;

	}

	private AffectedGenomicModel validateSgdStrainBackground(GeneDiseaseAnnotation uiEntity, GeneDiseaseAnnotation dbEntity) {
		String field = "sgdStrainBackground";
		if (ObjectUtils.isEmpty(uiEntity.getSgdStrainBackground())) {
			return null;
		}

		AffectedGenomicModel sgdStrainBackground = null;
		if (uiEntity.getSgdStrainBackground().getId() != null) {
			sgdStrainBackground = agmDAO.find(uiEntity.getSgdStrainBackground().getId());
		}
		if (sgdStrainBackground == null || !sgdStrainBackground.getTaxon().getName().startsWith("Saccharomyces cerevisiae")) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (sgdStrainBackground.getObsolete() && (dbEntity.getSgdStrainBackground() == null || !sgdStrainBackground.getId().equals(dbEntity.getSgdStrainBackground().getId()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return sgdStrainBackground;
	}
}
