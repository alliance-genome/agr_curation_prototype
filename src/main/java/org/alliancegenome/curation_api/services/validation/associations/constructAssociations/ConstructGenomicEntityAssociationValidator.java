package org.alliancegenome.curation_api.services.validation.associations.constructAssociations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.ConstructDAO;
import org.alliancegenome.curation_api.dao.GenomicEntityDAO;
import org.alliancegenome.curation_api.dao.associations.constructAssociations.ConstructGenomicEntityAssociationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.constructAssociations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.notes.NoteIdentityHelper;
import org.alliancegenome.curation_api.services.validation.NoteValidator;
import org.alliancegenome.curation_api.services.validation.associations.EvidenceAssociationValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ConstructGenomicEntityAssociationValidator extends EvidenceAssociationValidator<ConstructGenomicEntityAssociation> {

	@Inject ConstructDAO constructDAO;
	@Inject GenomicEntityDAO genomicEntityDAO;
	@Inject ConstructGenomicEntityAssociationDAO constructGenomicEntityAssociationDAO;
	@Inject NoteValidator noteValidator;

	private String errorMessage;

	public ObjectResponse<ConstructGenomicEntityAssociation> validateConstructGenomicEntityAssociation(ConstructGenomicEntityAssociation uiEntity) {
		ConstructGenomicEntityAssociation geAssociation = validateConstructGenomicEntityAssociation(uiEntity, false, false);
		response.setEntity(geAssociation);
		return response;
	}

	public ConstructGenomicEntityAssociation validateConstructGenomicEntityAssociation(ConstructGenomicEntityAssociation uiEntity, Boolean throwError, Boolean validateConstruct) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not create/update Construct GenomicEntity Association: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		ConstructGenomicEntityAssociation dbEntity = null;
		if (id != null) {
			dbEntity = constructGenomicEntityAssociationDAO.find(id);
			if (dbEntity == null) {
				addMessageResponse("Could not find ConstructGenomicEntityAssociation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new ConstructGenomicEntityAssociation();
		}

		dbEntity = (ConstructGenomicEntityAssociation) validateEvidenceAssociationFields(uiEntity, dbEntity);

		if (validateConstruct) {
			Construct subject = validateSubjectReagent(uiEntity, dbEntity);
			dbEntity.setConstructAssociationSubject(subject);
		}

		GenomicEntity object = validateObject(uiEntity, dbEntity);
		dbEntity.setConstructGenomicEntityAssociationObject(object);

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation", VocabularyConstants.CONSTRUCT_GENOMIC_ENTITY_RELATION_VOCABULARY_TERM_SET, dbEntity.getRelation(), uiEntity.getRelation());
		dbEntity.setRelation(relation);

		List<Note> relatedNotes = validateRelatedNotes(uiEntity, dbEntity);
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
				response.setErrorMessage(errorMessage);
				throw new ApiErrorException(response);
			} else {
				return null;
			}
		}

		return dbEntity;
	}

	private Construct validateSubjectReagent(ConstructGenomicEntityAssociation uiEntity, ConstructGenomicEntityAssociation dbEntity) {
		String field = "constructAssociationSubject";
		if (ObjectUtils.isEmpty(uiEntity.getConstructAssociationSubject())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		Construct subjectEntity = null;
		if (uiEntity.getConstructAssociationSubject().getId() != null) {
			subjectEntity = constructDAO.find(uiEntity.getConstructAssociationSubject().getId());
		}
		if (subjectEntity == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (subjectEntity.getObsolete() && (dbEntity.getConstructAssociationSubject() == null || !subjectEntity.getId().equals(dbEntity.getConstructAssociationSubject().getId()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return subjectEntity;

	}

	private GenomicEntity validateObject(ConstructGenomicEntityAssociation uiEntity, ConstructGenomicEntityAssociation dbEntity) {
		String field = "constructGenomicEntityAssociationObject";
		if (ObjectUtils.isEmpty(uiEntity.getConstructGenomicEntityAssociationObject())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		GenomicEntity objectEntity = null;
		if (uiEntity.getConstructGenomicEntityAssociationObject().getId() != null) {
			objectEntity = genomicEntityDAO.find(uiEntity.getConstructGenomicEntityAssociationObject().getId());
		}
		if (objectEntity == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (objectEntity.getObsolete() && (dbEntity.getConstructGenomicEntityAssociationObject() == null || !objectEntity.getId().equals(dbEntity.getConstructGenomicEntityAssociationObject().getId()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return objectEntity;

	}

	public List<Note> validateRelatedNotes(ConstructGenomicEntityAssociation uiEntity, ConstructGenomicEntityAssociation dbEntity) {
		String field = "relatedNotes";

		List<Note> validatedNotes = new ArrayList<Note>();
		Set<String> validatedNoteIdentities = new HashSet<>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getRelatedNotes())) {
			for (int ix = 0; ix < uiEntity.getRelatedNotes().size(); ix++) {
				Note note = uiEntity.getRelatedNotes().get(ix);
				ObjectResponse<Note> noteResponse = noteValidator.validateNote(note, VocabularyConstants.CONSTRUCT_COMPONENT_NOTE_TYPES_VOCABULARY_TERM_SET);
				if (noteResponse.getEntity() == null) {
					allValid = false;
					response.addErrorMessages(field, ix, noteResponse.getErrorMessages());
				} else {
					note = noteResponse.getEntity();

					String noteIdentity = NoteIdentityHelper.noteIdentity(note);
					if (validatedNoteIdentities.contains(noteIdentity)) {
						allValid = false;
						Map<String, String> duplicateError = new HashMap<>();
						duplicateError.put("freeText", ValidationConstants.DUPLICATE_MESSAGE + " (" + noteIdentity + ")");
						response.addErrorMessages(field, ix, duplicateError);
					} else {
						validatedNoteIdentities.add(noteIdentity);
						validatedNotes.add(note);
					}
				}
			}
		}
		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedNotes)) {
			return null;
		}

		return validatedNotes;
	}
}
