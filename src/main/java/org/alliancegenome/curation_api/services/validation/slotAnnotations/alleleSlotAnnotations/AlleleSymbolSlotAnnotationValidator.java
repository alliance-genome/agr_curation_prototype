package org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.NameSlotAnnotationValidator;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleSymbolSlotAnnotationValidator extends NameSlotAnnotationValidator<AlleleSymbolSlotAnnotation> {

	@Inject AlleleSymbolSlotAnnotationDAO alleleSymbolDAO;
	@Inject AlleleDAO alleleDAO;

	public ObjectResponse<AlleleSymbolSlotAnnotation> validateAlleleSymbolSlotAnnotation(AlleleSymbolSlotAnnotation uiEntity) {
		AlleleSymbolSlotAnnotation symbol = validateAlleleSymbolSlotAnnotation(uiEntity, false, false);
		response.setEntity(symbol);
		return response;
	}

	public AlleleSymbolSlotAnnotation validateAlleleSymbolSlotAnnotation(AlleleSymbolSlotAnnotation uiEntity, Boolean throwError, Boolean validateAllele) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update AlleleSymbolSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		AlleleSymbolSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = alleleSymbolDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find AlleleSymbolSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new AlleleSymbolSlotAnnotation();
			newEntity = true;
		}
		dbEntity = (AlleleSymbolSlotAnnotation) validateNameSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		VocabularyTerm nameType = validateRequiredTermInVocabularyTermSet("nameType", VocabularyConstants.SYMBOL_NAME_TYPE_TERM_SET, dbEntity.getNameType(), uiEntity.getNameType());
		dbEntity.setNameType(nameType);

		if (validateAllele) {
			Allele singleAllele = validateSingleAllele(uiEntity.getSingleAllele(), dbEntity.getSingleAllele());
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
