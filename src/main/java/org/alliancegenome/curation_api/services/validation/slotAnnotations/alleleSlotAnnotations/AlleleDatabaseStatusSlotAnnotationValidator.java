package org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleDatabaseStatusSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleDatabaseStatusSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.SlotAnnotationValidator;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleDatabaseStatusSlotAnnotationValidator extends SlotAnnotationValidator<AlleleDatabaseStatusSlotAnnotation> {

	@Inject AlleleDatabaseStatusSlotAnnotationDAO alleleDatabaseStatusDAO;
	@Inject AlleleDAO alleleDAO;

	public ObjectResponse<AlleleDatabaseStatusSlotAnnotation> validateAlleleDatabaseStatusSlotAnnotation(AlleleDatabaseStatusSlotAnnotation uiEntity) {
		AlleleDatabaseStatusSlotAnnotation mutationType = validateAlleleDatabaseStatusSlotAnnotation(uiEntity, false, false);
		response.setEntity(mutationType);
		return response;
	}

	public AlleleDatabaseStatusSlotAnnotation validateAlleleDatabaseStatusSlotAnnotation(AlleleDatabaseStatusSlotAnnotation uiEntity, Boolean throwError, Boolean validateAllele) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update AlleleDatabaseStatusSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		AlleleDatabaseStatusSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = alleleDatabaseStatusDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find AlleleDatabaseStatusSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new AlleleDatabaseStatusSlotAnnotation();
			newEntity = true;
		}

		dbEntity = (AlleleDatabaseStatusSlotAnnotation) validateSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		if (validateAllele) {
			Allele singleAllele = validateRequiredEntity(alleleDAO, "singleAllele", uiEntity.getSingleAllele(), dbEntity.getSingleAllele());
			dbEntity.setSingleAllele(singleAllele);
		}

		VocabularyTerm databaseStatus = validateRequiredTermInVocabulary("databaseStatus", VocabularyConstants.ALLELE_DATABASE_STATUS_VOCABULARY, uiEntity.getDatabaseStatus(), dbEntity.getDatabaseStatus());
		dbEntity.setDatabaseStatus(databaseStatus);

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
