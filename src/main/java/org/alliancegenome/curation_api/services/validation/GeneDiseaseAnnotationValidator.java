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

		Gene subject = validateRequiredEntity(geneDAO, "diseaseAnnotationSubject", uiEntity.getDiseaseAnnotationSubject(), dbEntity.getDiseaseAnnotationSubject());
		dbEntity.setDiseaseAnnotationSubject(subject);

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation", VocabularyConstants.GENE_DISEASE_RELATION_VOCABULARY_TERM_SET, uiEntity.getRelation(), dbEntity.getRelation());
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

	private AffectedGenomicModel validateSgdStrainBackground(GeneDiseaseAnnotation uiEntity, GeneDiseaseAnnotation dbEntity) {
		String field = "sgdStrainBackground";
		if (uiEntity.getSgdStrainBackground() == null) {
			return null;
		}

		AffectedGenomicModel sgdStrainBackground = validateEntity(agmDAO, field, uiEntity.getSgdStrainBackground(), dbEntity.getSgdStrainBackground());
		if (sgdStrainBackground != null && !sgdStrainBackground.getTaxon().getName().startsWith("Saccharomyces cerevisiae")) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		return sgdStrainBackground;
	}
}
