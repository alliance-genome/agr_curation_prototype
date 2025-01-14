package org.alliancegenome.curation_api.services.validation.slotAnnotations.constructSlotAnnotations;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.ConstructDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.SlotAnnotationValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ConstructComponentSlotAnnotationValidator extends SlotAnnotationValidator<ConstructComponentSlotAnnotation> {

	@Inject ConstructComponentSlotAnnotationDAO constructComponentDAO;
	@Inject ConstructDAO constructDAO;

	public ObjectResponse<ConstructComponentSlotAnnotation> validateConstructComponentSlotAnnotation(ConstructComponentSlotAnnotation uiEntity) {
		ConstructComponentSlotAnnotation component = validateConstructComponentSlotAnnotation(uiEntity, false, false);
		response.setEntity(component);
		return response;
	}

	public ConstructComponentSlotAnnotation validateConstructComponentSlotAnnotation(ConstructComponentSlotAnnotation uiEntity, Boolean throwError, Boolean validateConstruct) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update ConstructComponentSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		ConstructComponentSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = constructComponentDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find ConstructComponentSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new ConstructComponentSlotAnnotation();
			newEntity = true;
		}

		dbEntity = (ConstructComponentSlotAnnotation) validateSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		if (validateConstruct) {
			Construct singleConstruct = validateRequiredEntity(constructDAO, "singleConstruct", uiEntity.getSingleConstruct(), dbEntity.getSingleConstruct());
			dbEntity.setSingleConstruct(singleConstruct);
		}

		String componentSymbol = validateComponentSymbol(uiEntity);
		dbEntity.setComponentSymbol(componentSymbol);

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation", VocabularyConstants.CONSTRUCT_GENOMIC_ENTITY_RELATION_VOCABULARY_TERM_SET, uiEntity.getRelation(), dbEntity.getRelation());
		dbEntity.setRelation(relation);

		NCBITaxonTerm taxon = validateTaxon(uiEntity.getTaxon(), dbEntity.getTaxon());
		dbEntity.setTaxon(taxon);

		String taxonText = handleStringField(uiEntity.getTaxonText());
		dbEntity.setTaxonText(taxonText);

		List<Note> relatedNotes = validateRelatedNotes(uiEntity.getRelatedNotes(), VocabularyConstants.CONSTRUCT_COMPONENT_NOTE_TYPES_VOCABULARY_TERM_SET);
		if (dbEntity.getRelatedNotes() != null) {
			dbEntity.getRelatedNotes().clear();
		}
		if (relatedNotes != null) {
			if (dbEntity.getRelatedNotes() == null) {
				dbEntity.setRelatedNotes(new ArrayList<>());
			}
			dbEntity.getRelatedNotes().addAll(relatedNotes);
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

	public String validateComponentSymbol(ConstructComponentSlotAnnotation uiEntity) {
		String field = "componentSymbol";
		if (StringUtils.isBlank(uiEntity.getComponentSymbol())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		return uiEntity.getComponentSymbol();
	}

}
