package org.alliancegenome.curation_api.services.validation.slotAnnotations.constructSlotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.ConstructDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations.ConstructSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructSymbolSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.NameSlotAnnotationValidator;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ConstructSymbolSlotAnnotationValidator extends NameSlotAnnotationValidator<ConstructSymbolSlotAnnotation> {

	@Inject ConstructSymbolSlotAnnotationDAO constructSymbolDAO;
	@Inject ConstructDAO constructDAO;

	public ObjectResponse<ConstructSymbolSlotAnnotation> validateConstructSymbolSlotAnnotation(ConstructSymbolSlotAnnotation uiEntity) {
		ConstructSymbolSlotAnnotation symbol = validateConstructSymbolSlotAnnotation(uiEntity, false, false);
		response.setEntity(symbol);
		return response;
	}

	public ConstructSymbolSlotAnnotation validateConstructSymbolSlotAnnotation(ConstructSymbolSlotAnnotation uiEntity, Boolean throwError, Boolean validateConstruct) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update ConstructSymbolSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		ConstructSymbolSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = constructSymbolDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find ConstructSymbolSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new ConstructSymbolSlotAnnotation();
			newEntity = true;
		}
		dbEntity = (ConstructSymbolSlotAnnotation) validateNameSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		VocabularyTerm nameType = validateRequiredTermInVocabularyTermSet("nameType", VocabularyConstants.SYMBOL_NAME_TYPE_TERM_SET, dbEntity.getNameType(), uiEntity.getNameType());
		dbEntity.setNameType(nameType);

		if (validateConstruct) {
			Construct singleConstruct = validateSingleConstruct(uiEntity.getSingleConstruct(), dbEntity.getSingleConstruct());
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
