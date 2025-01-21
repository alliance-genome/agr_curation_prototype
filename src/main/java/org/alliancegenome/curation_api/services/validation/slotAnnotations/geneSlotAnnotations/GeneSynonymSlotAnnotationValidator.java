package org.alliancegenome.curation_api.services.validation.slotAnnotations.geneSlotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.NameSlotAnnotationValidator;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneSynonymSlotAnnotationValidator extends NameSlotAnnotationValidator<GeneSynonymSlotAnnotation> {

	@Inject GeneSynonymSlotAnnotationDAO geneSynonymDAO;
	@Inject GeneDAO geneDAO;

	public ObjectResponse<GeneSynonymSlotAnnotation> validateGeneSynonymSlotAnnotation(GeneSynonymSlotAnnotation uiEntity) {
		GeneSynonymSlotAnnotation synonym = validateGeneSynonymSlotAnnotation(uiEntity, false, false);
		response.setEntity(synonym);
		return response;
	}

	public GeneSynonymSlotAnnotation validateGeneSynonymSlotAnnotation(GeneSynonymSlotAnnotation uiEntity, Boolean throwError, Boolean validateGene) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update GeneSynonymSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		GeneSynonymSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = geneSynonymDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find GeneSynonymSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new GeneSynonymSlotAnnotation();
			newEntity = true;
		}
		dbEntity = (GeneSynonymSlotAnnotation) validateNameSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		VocabularyTerm nameType = validateRequiredTermInVocabulary("nameType", VocabularyConstants.NAME_TYPE_VOCABULARY, uiEntity.getNameType(), dbEntity.getNameType());
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
