package org.alliancegenome.curation_api.services.validation.slotAnnotations.constructSlotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.ConstructDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations.ConstructFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructFullNameSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.NameSlotAnnotationValidator;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ConstructFullNameSlotAnnotationValidator extends NameSlotAnnotationValidator<ConstructFullNameSlotAnnotation> {

	@Inject ConstructFullNameSlotAnnotationDAO constructFullNameDAO;
	@Inject ConstructDAO constructDAO;

	public ObjectResponse<ConstructFullNameSlotAnnotation> validateConstructFullNameSlotAnnotation(ConstructFullNameSlotAnnotation uiEntity) {
		ConstructFullNameSlotAnnotation fullName = validateConstructFullNameSlotAnnotation(uiEntity, false, false);
		response.setEntity(fullName);
		return response;
	}

	public ConstructFullNameSlotAnnotation validateConstructFullNameSlotAnnotation(ConstructFullNameSlotAnnotation uiEntity, Boolean throwError, Boolean validateConstruct) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update ConstructFullNameSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		ConstructFullNameSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = constructFullNameDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find ConstructFullNameSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new ConstructFullNameSlotAnnotation();
			newEntity = true;
		}
		dbEntity = (ConstructFullNameSlotAnnotation) validateNameSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		VocabularyTerm nameType = validateRequiredTermInVocabularyTermSet("nameType", VocabularyConstants.FULL_NAME_TYPE_TERM_SET, uiEntity.getNameType(), dbEntity.getNameType());
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
