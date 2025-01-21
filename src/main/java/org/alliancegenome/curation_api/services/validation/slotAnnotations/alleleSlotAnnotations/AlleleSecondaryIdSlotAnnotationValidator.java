package org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.SecondaryIdSlotAnnotationValidator;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleSecondaryIdSlotAnnotationValidator extends SecondaryIdSlotAnnotationValidator<AlleleSecondaryIdSlotAnnotation> {

	@Inject AlleleSecondaryIdSlotAnnotationDAO alleleSecondaryIdDAO;
	@Inject AlleleDAO alleleDAO;

	public ObjectResponse<AlleleSecondaryIdSlotAnnotation> validateAlleleSecondaryIdSlotAnnotation(AlleleSecondaryIdSlotAnnotation uiEntity) {
		AlleleSecondaryIdSlotAnnotation secondaryId = validateAlleleSecondaryIdSlotAnnotation(uiEntity, false, false);
		response.setEntity(secondaryId);
		return response;
	}

	public AlleleSecondaryIdSlotAnnotation validateAlleleSecondaryIdSlotAnnotation(AlleleSecondaryIdSlotAnnotation uiEntity, Boolean throwError, Boolean validateAllele) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update AlleleSecondaryIdSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		AlleleSecondaryIdSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = alleleSecondaryIdDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find AlleleSecondaryIdSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new AlleleSecondaryIdSlotAnnotation();
			newEntity = true;
		}

		dbEntity = (AlleleSecondaryIdSlotAnnotation) validateSecondaryIdSlotAnnotationFields(uiEntity, dbEntity, newEntity);

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
