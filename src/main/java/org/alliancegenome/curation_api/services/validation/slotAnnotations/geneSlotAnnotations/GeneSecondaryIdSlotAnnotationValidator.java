package org.alliancegenome.curation_api.services.validation.slotAnnotations.geneSlotAnnotations;

import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneSecondaryIdSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.SecondaryIdSlotAnnotationValidator;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneSecondaryIdSlotAnnotationValidator extends SecondaryIdSlotAnnotationValidator<GeneSecondaryIdSlotAnnotation> {
	
	@Inject GeneSecondaryIdSlotAnnotationDAO geneSecondaryIdDAO;
	@Inject GeneDAO geneDAO;

	public ObjectResponse<GeneSecondaryIdSlotAnnotation> validateGeneSecondaryIdSlotAnnotation(GeneSecondaryIdSlotAnnotation uiEntity) {
		GeneSecondaryIdSlotAnnotation secondaryId = validateGeneSecondaryIdSlotAnnotation(uiEntity, false, false);
		response.setEntity(secondaryId);
		return response;
	}

	public GeneSecondaryIdSlotAnnotation validateGeneSecondaryIdSlotAnnotation(GeneSecondaryIdSlotAnnotation uiEntity, Boolean throwError, Boolean validateGene) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update GeneSecondaryIdSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		GeneSecondaryIdSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = geneSecondaryIdDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find GeneSecondaryIdSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new GeneSecondaryIdSlotAnnotation();
			newEntity = true;
		}

		dbEntity = (GeneSecondaryIdSlotAnnotation) validateSecondaryIdSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		if (validateGene) {
			Gene singleGene = validateRequiredEntity(geneDAO, "singleGene", uiEntity.getSingleGene(), dbEntity.getSingleGene());
			dbEntity.setSingleGene(singleGene);
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
