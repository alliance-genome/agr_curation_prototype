package org.alliancegenome.curation_api.services.validation.slotAnnotations.geneSlotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.NameSlotAnnotationValidator;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneSymbolSlotAnnotationValidator extends NameSlotAnnotationValidator<GeneSymbolSlotAnnotation> {

	@Inject GeneSymbolSlotAnnotationDAO geneSymbolDAO;
	@Inject GeneDAO geneDAO;

	public ObjectResponse<GeneSymbolSlotAnnotation> validateGeneSymbolSlotAnnotation(GeneSymbolSlotAnnotation uiEntity) {
		GeneSymbolSlotAnnotation symbol = validateGeneSymbolSlotAnnotation(uiEntity, false, false);
		response.setEntity(symbol);
		return response;
	}

	public GeneSymbolSlotAnnotation validateGeneSymbolSlotAnnotation(GeneSymbolSlotAnnotation uiEntity, Boolean throwError, Boolean validateGene) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update GeneSymbolSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		GeneSymbolSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = geneSymbolDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find GeneSymbolSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new GeneSymbolSlotAnnotation();
			newEntity = true;
		}

		dbEntity = (GeneSymbolSlotAnnotation) validateNameSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		VocabularyTerm nameType = validateRequiredTermInVocabularyTermSet("nameType", VocabularyConstants.SYMBOL_NAME_TYPE_TERM_SET, dbEntity.getNameType(), uiEntity.getNameType());
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
