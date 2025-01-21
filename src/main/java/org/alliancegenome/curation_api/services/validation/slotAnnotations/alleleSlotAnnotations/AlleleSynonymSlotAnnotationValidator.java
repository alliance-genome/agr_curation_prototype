package org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.NameSlotAnnotationValidator;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleSynonymSlotAnnotationValidator extends NameSlotAnnotationValidator<AlleleSynonymSlotAnnotation> {

	@Inject AlleleSynonymSlotAnnotationDAO alleleSynonymDAO;
	@Inject AlleleDAO alleleDAO;

	public ObjectResponse<AlleleSynonymSlotAnnotation> validateAlleleSynonymSlotAnnotation(AlleleSynonymSlotAnnotation uiEntity) {
		AlleleSynonymSlotAnnotation synonym = validateAlleleSynonymSlotAnnotation(uiEntity, false, false);
		response.setEntity(synonym);
		return response;
	}

	public AlleleSynonymSlotAnnotation validateAlleleSynonymSlotAnnotation(AlleleSynonymSlotAnnotation uiEntity, Boolean throwError, Boolean validateAllele) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update AlleleSynonymSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		AlleleSynonymSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = alleleSynonymDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find AlleleSynonymSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new AlleleSynonymSlotAnnotation();
			newEntity = true;
		}
		dbEntity = (AlleleSynonymSlotAnnotation) validateNameSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		VocabularyTerm nameType = validateRequiredTermInVocabulary("nameType", VocabularyConstants.NAME_TYPE_VOCABULARY, uiEntity.getNameType(), dbEntity.getNameType());
		dbEntity.setNameType(nameType);

		if (validateAllele) {
			Allele singleAllele = validateRequiredEntity(alleleDAO, "singleAllele", uiEntity.getSingleAllele(), dbEntity.getSingleAllele());
			dbEntity.setSingleAllele(singleAllele);
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
