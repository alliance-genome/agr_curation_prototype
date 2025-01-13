package org.alliancegenome.curation_api.services.validation.slotAnnotations.geneSlotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.NameSlotAnnotationValidator;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneFullNameSlotAnnotationValidator extends NameSlotAnnotationValidator<GeneFullNameSlotAnnotation> {

	@Inject GeneFullNameSlotAnnotationDAO geneFullNameDAO;
	@Inject GeneDAO geneDAO;

	public ObjectResponse<GeneFullNameSlotAnnotation> validateGeneFullNameSlotAnnotation(GeneFullNameSlotAnnotation uiEntity) {
		GeneFullNameSlotAnnotation fullName = validateGeneFullNameSlotAnnotation(uiEntity, false, false);
		response.setEntity(fullName);
		return response;
	}

	public GeneFullNameSlotAnnotation validateGeneFullNameSlotAnnotation(GeneFullNameSlotAnnotation uiEntity, Boolean throwError, Boolean validateGene) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update GeneFullNameSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		GeneFullNameSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = geneFullNameDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find GeneFullNameSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new GeneFullNameSlotAnnotation();
			newEntity = true;
		}
		dbEntity = (GeneFullNameSlotAnnotation) validateNameSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		VocabularyTerm nameType = validateRequiredTermInVocabularyTermSet("nameType", VocabularyConstants.FULL_NAME_TYPE_TERM_SET, dbEntity.getNameType(), uiEntity.getNameType());
		dbEntity.setNameType(nameType);

		if (validateGene) {
			Gene singleGene = validateSingleGene(uiEntity.getSingleGene(), dbEntity.getSingleGene());
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
