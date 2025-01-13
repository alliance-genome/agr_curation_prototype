package org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleNomenclatureEventSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleNomenclatureEventSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.SlotAnnotationValidator;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleNomenclatureEventSlotAnnotationValidator extends SlotAnnotationValidator<AlleleNomenclatureEventSlotAnnotation> {

	@Inject AlleleNomenclatureEventSlotAnnotationDAO alleleNomenclatureEventDAO;

	public ObjectResponse<AlleleNomenclatureEventSlotAnnotation> validateAlleleNomenclatureEventSlotAnnotation(AlleleNomenclatureEventSlotAnnotation uiEntity) {
		AlleleNomenclatureEventSlotAnnotation nomenclatureEvent = validateAlleleNomenclatureEventSlotAnnotation(uiEntity, false, false);
		response.setEntity(nomenclatureEvent);
		return response;
	}

	public AlleleNomenclatureEventSlotAnnotation validateAlleleNomenclatureEventSlotAnnotation(AlleleNomenclatureEventSlotAnnotation uiEntity, Boolean throwError, Boolean validateAllele) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update AlleleNomenclatureEventSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		AlleleNomenclatureEventSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = alleleNomenclatureEventDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find AlleleNomenclatureEventSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new AlleleNomenclatureEventSlotAnnotation();
			newEntity = true;
		}

		dbEntity = (AlleleNomenclatureEventSlotAnnotation) validateSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		if (validateAllele) {
			Allele singleAllele = validateSingleAllele(uiEntity.getSingleAllele(), dbEntity.getSingleAllele());
			dbEntity.setSingleAllele(singleAllele);
		}

		VocabularyTerm nomenclatureEvent = validateRequiredTermInVocabulary("nomenclatureEvent", VocabularyConstants.ALLELE_NOMENCLATURE_EVENT_VOCABULARY, dbEntity.getNomenclatureEvent(), uiEntity.getNomenclatureEvent());
		dbEntity.setNomenclatureEvent(nomenclatureEvent);

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
