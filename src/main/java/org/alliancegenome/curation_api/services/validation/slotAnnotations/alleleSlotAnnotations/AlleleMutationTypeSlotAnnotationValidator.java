package org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations;

import java.util.List;

import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.ontology.SoTermDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.SlotAnnotationValidator;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleMutationTypeSlotAnnotationValidator extends SlotAnnotationValidator<AlleleMutationTypeSlotAnnotation> {

	@Inject AlleleMutationTypeSlotAnnotationDAO alleleMutationTypeDAO;
	@Inject AlleleDAO alleleDAO;
	@Inject SoTermDAO soTermDAO;

	public ObjectResponse<AlleleMutationTypeSlotAnnotation> validateAlleleMutationTypeSlotAnnotation(AlleleMutationTypeSlotAnnotation uiEntity) {
		AlleleMutationTypeSlotAnnotation mutationType = validateAlleleMutationTypeSlotAnnotation(uiEntity, false, false);
		response.setEntity(mutationType);
		return response;
	}

	public AlleleMutationTypeSlotAnnotation validateAlleleMutationTypeSlotAnnotation(AlleleMutationTypeSlotAnnotation uiEntity, Boolean throwError, Boolean validateAllele) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update AlleleMutationTypeSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		AlleleMutationTypeSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = alleleMutationTypeDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find AlleleMutationTypeSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new AlleleMutationTypeSlotAnnotation();
			newEntity = true;
		}

		dbEntity = (AlleleMutationTypeSlotAnnotation) validateSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		if (validateAllele) {
			Allele singleAllele = validateRequiredEntity(alleleDAO, "singleAllele", uiEntity.getSingleAllele(), dbEntity.getSingleAllele());
			dbEntity.setSingleAllele(singleAllele);
		}

		List<SOTerm> mutationTypes = validateRequiredEntities(soTermDAO, "mutationTypes", uiEntity.getMutationTypes(), dbEntity.getMutationTypes());
		dbEntity.setMutationTypes(mutationTypes);

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
