package org.alliancegenome.curation_api.services.validation.slotAnnotations.constructSlotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.ConstructDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations.ConstructSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructSynonymSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.NameSlotAnnotationValidator;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ConstructSynonymSlotAnnotationValidator extends NameSlotAnnotationValidator<ConstructSynonymSlotAnnotation> {

	@Inject ConstructSynonymSlotAnnotationDAO constructSynonymDAO;
	@Inject ConstructDAO constructDAO;

	public ObjectResponse<ConstructSynonymSlotAnnotation> validateConstructSynonymSlotAnnotation(ConstructSynonymSlotAnnotation uiEntity) {
		ConstructSynonymSlotAnnotation synonym = validateConstructSynonymSlotAnnotation(uiEntity, false, false);
		response.setEntity(synonym);
		return response;
	}

	public ConstructSynonymSlotAnnotation validateConstructSynonymSlotAnnotation(ConstructSynonymSlotAnnotation uiEntity, Boolean throwError, Boolean validateConstruct) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update ConstructSynonymSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		ConstructSynonymSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = constructSynonymDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find ConstructSynonymSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new ConstructSynonymSlotAnnotation();
			newEntity = true;
		}
		dbEntity = (ConstructSynonymSlotAnnotation) validateNameSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		VocabularyTerm nameType = validateRequiredTermInVocabulary("nameType", VocabularyConstants.NAME_TYPE_VOCABULARY, uiEntity.getNameType(), dbEntity.getNameType());
		dbEntity.setNameType(nameType);

		if (validateConstruct) {
			Construct singleConstruct = validateRequiredEntity(constructDAO, "singleConstruct", uiEntity.getSingleConstruct(), dbEntity.getSingleConstruct());
			dbEntity.setSingleConstruct(singleConstruct);
		}

		if (response.hasErrors()) {
			if (throwError) {
				response.setErrorMessage(errorTitle);
				throw new ApiErrorException(response);
			} else {
				return null;
			}
		}

		return dbEntity;
	}

}
