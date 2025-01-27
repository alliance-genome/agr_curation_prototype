package org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.ontology.PhenotypeTermDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.PhenotypeTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.SlotAnnotationValidator;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleInheritanceModeSlotAnnotationValidator extends SlotAnnotationValidator<AlleleInheritanceModeSlotAnnotation> {

	@Inject AlleleInheritanceModeSlotAnnotationDAO alleleInheritanceModeDAO;
	@Inject AlleleDAO alleleDAO;
	@Inject PhenotypeTermDAO phenotypeTermDAO;

	public ObjectResponse<AlleleInheritanceModeSlotAnnotation> validateAlleleInheritanceModeSlotAnnotation(AlleleInheritanceModeSlotAnnotation uiEntity) {
		AlleleInheritanceModeSlotAnnotation mutationType = validateAlleleInheritanceModeSlotAnnotation(uiEntity, false, false);
		response.setEntity(mutationType);
		return response;
	}

	public AlleleInheritanceModeSlotAnnotation validateAlleleInheritanceModeSlotAnnotation(AlleleInheritanceModeSlotAnnotation uiEntity, Boolean throwError, Boolean validateAllele) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update AlleleInheritanceModeSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		AlleleInheritanceModeSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = alleleInheritanceModeDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find AlleleInheritanceModeSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new AlleleInheritanceModeSlotAnnotation();
			newEntity = true;
		}

		dbEntity = (AlleleInheritanceModeSlotAnnotation) validateSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		if (validateAllele) {
			Allele singleAllele = validateRequiredEntity(alleleDAO, "singleAllele", uiEntity.getSingleAllele(), dbEntity.getSingleAllele());
			dbEntity.setSingleAllele(singleAllele);
		}

		VocabularyTerm inheritanceMode = validateRequiredTermInVocabulary("inheritanceMode", VocabularyConstants.ALLELE_INHERITANCE_MODE_VOCABULARY, uiEntity.getInheritanceMode(), dbEntity.getInheritanceMode());
		dbEntity.setInheritanceMode(inheritanceMode);

		PhenotypeTerm phenotypeTerm = validateEntity(phenotypeTermDAO, "phenotypeTerm", uiEntity.getPhenotypeTerm(), dbEntity.getPhenotypeTerm());
		dbEntity.setPhenotypeTerm(phenotypeTerm);

		String phenotypeStatement = handleStringField(uiEntity.getPhenotypeStatement());
		dbEntity.setPhenotypeStatement(phenotypeStatement);

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
