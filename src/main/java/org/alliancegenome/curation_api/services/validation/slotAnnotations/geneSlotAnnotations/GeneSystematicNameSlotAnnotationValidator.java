package org.alliancegenome.curation_api.services.validation.slotAnnotations.geneSlotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.NameSlotAnnotationValidator;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneSystematicNameSlotAnnotationValidator extends NameSlotAnnotationValidator<GeneSystematicNameSlotAnnotation> {

	@Inject GeneSystematicNameSlotAnnotationDAO geneSystematicNameDAO;
	@Inject GeneDAO geneDAO;

	public ObjectResponse<GeneSystematicNameSlotAnnotation> validateGeneSystematicNameSlotAnnotation(GeneSystematicNameSlotAnnotation uiEntity) {
		GeneSystematicNameSlotAnnotation systematicName = validateGeneSystematicNameSlotAnnotation(uiEntity, false, false);
		response.setEntity(systematicName);
		return response;
	}

	public GeneSystematicNameSlotAnnotation validateGeneSystematicNameSlotAnnotation(GeneSystematicNameSlotAnnotation uiEntity, Boolean throwError, Boolean validateGene) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update GeneSystematicNameSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		GeneSystematicNameSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = geneSystematicNameDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find GeneSystematicNameSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new GeneSystematicNameSlotAnnotation();
			newEntity = true;
		}
		dbEntity = (GeneSystematicNameSlotAnnotation) validateNameSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		VocabularyTerm nameType = validateRequiredTermInVocabularyTermSet("nameType", VocabularyConstants.SYSTEMATIC_NAME_TYPE_TERM_SET, uiEntity.getNameType(), dbEntity.getNameType());
		dbEntity.setNameType(nameType);

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
