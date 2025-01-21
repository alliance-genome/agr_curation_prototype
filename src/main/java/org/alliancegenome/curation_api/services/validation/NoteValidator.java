package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.validation.base.AuditedObjectValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class NoteValidator extends AuditedObjectValidator<Note> {

	@Inject NoteDAO noteDAO;
	@Inject ReferenceService referenceService;
	@Inject ReferenceValidator referenceValidator;

	public ObjectResponse<Note> validateNote(Note uiEntity, String noteVocabularySetName) {
		Note note = validateNote(uiEntity, noteVocabularySetName, false);
		response.setEntity(note);
		return response;
	}

	public Note validateNote(Note uiEntity, String noteVocabularySetName, Boolean throwError) {
		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not update Note: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		Note dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = noteDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find Note with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new Note();
			newEntity = true;
		}
		dbEntity = (Note) validateAuditedObjectFields(uiEntity, dbEntity, newEntity);

		VocabularyTerm noteType = validateNoteType(uiEntity, dbEntity, noteVocabularySetName);
		dbEntity.setNoteType(noteType);

		String freeText = validateFreeText(uiEntity);
		dbEntity.setFreeText(freeText);

		List<String> previousReferenceCuries = new ArrayList<String>();
		if (CollectionUtils.isNotEmpty(dbEntity.getReferences())) {
			previousReferenceCuries = dbEntity.getReferences().stream().map(Reference::getCurie).collect(Collectors.toList());
		}
		if (CollectionUtils.isNotEmpty(uiEntity.getReferences())) {
			List<Reference> references = new ArrayList<Reference>();
			for (Reference uiReference : uiEntity.getReferences()) {
				Reference reference = validateReference(uiReference, previousReferenceCuries);
				if (reference != null) {
					references.add(reference);
				}
			}
			dbEntity.setReferences(references);
		} else {
			dbEntity.setReferences(null);
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

	private Reference validateReference(Reference uiEntity, List<String> previousCuries) {
		ObjectResponse<Reference> singleRefResponse = referenceValidator.validateReference(uiEntity);
		if (singleRefResponse.getEntity() == null) {
			addMessageResponse("references", ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (singleRefResponse.getEntity().getObsolete() && !previousCuries.contains(singleRefResponse.getEntity().getCurie())) {
			addMessageResponse("references", ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return singleRefResponse.getEntity();
	}

	public VocabularyTerm validateNoteType(Note uiEntity, Note dbEntity, String noteVocabularySetName) {
		String field = "noteType";
		
		if (StringUtils.isBlank(noteVocabularySetName)) {
			return validateRequiredTermInVocabulary(field, VocabularyConstants.NOTE_TYPE_VOCABULARY, uiEntity.getNoteType(), dbEntity.getNoteType());
		}
		
		return validateRequiredTermInVocabularyTermSet(field, noteVocabularySetName, uiEntity.getNoteType(), dbEntity.getNoteType());
	}
	
	public String validateFreeText(Note uiEntity) {
		String field = "freeText";
		if (StringUtils.isBlank(uiEntity.getFreeText())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		return uiEntity.getFreeText();
	}
}
