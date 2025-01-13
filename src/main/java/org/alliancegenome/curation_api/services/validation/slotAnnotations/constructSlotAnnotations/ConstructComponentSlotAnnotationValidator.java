package org.alliancegenome.curation_api.services.validation.slotAnnotations.constructSlotAnnotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.notes.NoteIdentityHelper;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.services.validation.NoteValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.SlotAnnotationValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ConstructComponentSlotAnnotationValidator extends SlotAnnotationValidator<ConstructComponentSlotAnnotation> {

	@Inject ConstructComponentSlotAnnotationDAO constructComponentDAO;
	@Inject NcbiTaxonTermService ncbiTaxonTermService;
	@Inject NoteValidator noteValidator;
	@Inject NoteDAO noteDAO;

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
			Construct singleConstruct = validateSingleConstruct(uiEntity.getSingleConstruct(), dbEntity.getSingleConstruct());
			dbEntity.setSingleConstruct(singleConstruct);
		}

		String componentSymbol = validateComponentSymbol(uiEntity);
		dbEntity.setComponentSymbol(componentSymbol);

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation", VocabularyConstants.CONSTRUCT_GENOMIC_ENTITY_RELATION_VOCABULARY_TERM_SET, dbEntity.getRelation(), uiEntity.getRelation());
		dbEntity.setRelation(relation);

		NCBITaxonTerm taxon = validateTaxon(uiEntity, dbEntity);
		dbEntity.setTaxon(taxon);

		if (StringUtils.isNotBlank(uiEntity.getTaxonText())) {
			dbEntity.setTaxonText(uiEntity.getTaxonText());
		} else {
			dbEntity.setTaxonText(null);
		}

		List<Note> relatedNotes = validateRelatedNotes(uiEntity, dbEntity);
		dbEntity.setRelatedNotes(relatedNotes);

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

	public NCBITaxonTerm validateTaxon(ConstructComponentSlotAnnotation uiEntity, ConstructComponentSlotAnnotation dbEntity) {
		String field = "taxon";
		if (uiEntity.getTaxon() == null || StringUtils.isBlank(uiEntity.getTaxon().getCurie())) {
			return null;
		}

		NCBITaxonTerm taxon = ncbiTaxonTermService.getByCurie(uiEntity.getTaxon().getCurie()).getEntity();
		if (taxon == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (taxon.getObsolete() && (dbEntity.getTaxon() == null || !taxon.getCurie().equals(dbEntity.getTaxon().getCurie()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return taxon;
	}

	public List<Note> validateRelatedNotes(ConstructComponentSlotAnnotation uiEntity, ConstructComponentSlotAnnotation dbEntity) {
		String field = "relatedNotes";

		List<Note> validatedNotes = new ArrayList<Note>();
		Set<String> validatedNoteIdentities = new HashSet<>();
		if (CollectionUtils.isNotEmpty(uiEntity.getRelatedNotes())) {
			for (Note note : uiEntity.getRelatedNotes()) {
				ObjectResponse<Note> noteResponse = noteValidator.validateNote(note, VocabularyConstants.CONSTRUCT_COMPONENT_NOTE_TYPES_VOCABULARY_TERM_SET);
				if (noteResponse.getEntity() == null) {
					addMessageResponse(field, noteResponse.errorMessagesString());
					return null;
				}
				note = noteResponse.getEntity();

				String noteIdentity = NoteIdentityHelper.noteIdentity(note);
				if (validatedNoteIdentities.contains(noteIdentity)) {
					addMessageResponse(field, ValidationConstants.DUPLICATE_MESSAGE + " (" + noteIdentity + ")");
					return null;
				}
				validatedNoteIdentities.add(noteIdentity);
				validatedNotes.add(note);
			}
		}

		List<Long> previousNoteIds = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(dbEntity.getRelatedNotes())) {
			previousNoteIds = dbEntity.getRelatedNotes().stream().map(Note::getId).collect(Collectors.toList());
		}
		List<Long> validatedNoteIds = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(validatedNotes)) {
			validatedNoteIds = validatedNotes.stream().map(Note::getId).collect(Collectors.toList());
		}
		for (Note validatedNote : validatedNotes) {
			if (!previousNoteIds.contains(validatedNote.getId())) {
				noteDAO.persist(validatedNote);
			}
		}
		List<Long> idsToRemove = ListUtils.subtract(previousNoteIds, validatedNoteIds);
		for (Long id : idsToRemove) {
			constructComponentDAO.deleteAttachedNote(id);
		}

		if (CollectionUtils.isEmpty(validatedNotes)) {
			return null;
		}

		return validatedNotes;
	}

}
