package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.AnnotationDAO;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.model.entities.Annotation;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.DataProviderService;
import org.alliancegenome.curation_api.services.helpers.notes.NoteIdentityHelper;
import org.alliancegenome.curation_api.services.validation.base.AuditedObjectValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.inject.Inject;

public class AnnotationValidator extends AuditedObjectValidator<Annotation> {

	@Inject ReferenceValidator referenceValidator;
	@Inject NoteValidator noteValidator;
	@Inject NoteDAO noteDAO;
	@Inject ConditionRelationValidator conditionRelationValidator;
	@Inject ConditionRelationDAO conditionRelationDAO;
	@Inject AnnotationDAO annotationDAO;
	@Inject DataProviderService dataProviderService;
	@Inject DataProviderValidator dataProviderValidator;

	public DataProvider validateDataProvider(Annotation uiEntity, Annotation dbEntity) {
		String field = "dataProvider";

		if (uiEntity.getDataProvider() == null) {
			if (dbEntity.getId() == null) {
				uiEntity.setDataProvider(dataProviderService.createAffiliatedModDataProvider());
			}
			if (uiEntity.getDataProvider() == null) {
				addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
				return null;
			}
		}

		DataProvider uiDataProvider = uiEntity.getDataProvider();
		DataProvider dbDataProvider = dbEntity.getDataProvider();

		ObjectResponse<DataProvider> dpResponse = dataProviderValidator.validateDataProvider(uiDataProvider, dbDataProvider, false);
		if (dpResponse.hasErrors()) {
			addMessageResponse(field, dpResponse.errorMessagesString());
			return null;
		}

		DataProvider validatedDataProvider = dpResponse.getEntity();
		if (validatedDataProvider.getObsolete() && (dbDataProvider == null || !validatedDataProvider.getId().equals(dbDataProvider.getId()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return validatedDataProvider;
	}

	public List<Note> validateRelatedNotes(Annotation uiEntity, Annotation dbEntity, String noteTypeSet) {
		String field = "relatedNotes";

		List<Note> validatedNotes = new ArrayList<Note>();
		Set<String> validatedNoteIdentities = new HashSet<>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getRelatedNotes())) {
			for (int ix = 0; ix < uiEntity.getRelatedNotes().size(); ix++) {
				Note note = uiEntity.getRelatedNotes().get(ix);
				ObjectResponse<Note> noteResponse = noteValidator.validateNote(note, noteTypeSet);
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
						Boolean matchingRefs = true;
						if (CollectionUtils.isNotEmpty(note.getReferences())) {
							for (Reference noteRef : note.getReferences()) {
								if (!noteRef.getCurie().equals(dbEntity.getSingleReference().getCurie())) {
									Map<String, String> noteRefErrorMessages = new HashMap<>();
									noteRefErrorMessages.put("references", ValidationConstants.INVALID_MESSAGE + " (" + noteRef + ")");
									response.addErrorMessages("relatedNotes", ix, noteRefErrorMessages);
									allValid = false;
									matchingRefs = false;
								}
							}
						}
						if (matchingRefs) {
							validatedNoteIdentities.add(noteIdentity);
							validatedNotes.add(note);
						}
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

	public List<ConditionRelation> validateConditionRelations(Annotation uiEntity, Annotation dbEntity) {
		if (CollectionUtils.isEmpty(uiEntity.getConditionRelations())) {
			return null;
		}

		List<ConditionRelation> validatedConditionRelations = new ArrayList<>();
		List<Long> previousConditionRelationIds = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dbEntity.getConditionRelations())) {
			previousConditionRelationIds = dbEntity.getConditionRelations().stream().map(ConditionRelation::getId).collect(Collectors.toList());
		}

		for (ConditionRelation conditionRelation : uiEntity.getConditionRelations()) {
			if (uiEntity.getSingleReference() != null && !StringUtils.isBlank(uiEntity.getSingleReference().getCurie()) && conditionRelation.getSingleReference() != null && !StringUtils.isBlank(conditionRelation.getSingleReference().getCurie())
				&& !conditionRelation.getSingleReference().getCurie().equals(uiEntity.getSingleReference().getCurie())) {
				addMessageResponse("conditionRelations", "singleReference - " + ValidationConstants.INVALID_MESSAGE);
			}

			if (conditionRelation.getObsolete() && !previousConditionRelationIds.contains(conditionRelation.getId())) {
				addMessageResponse("conditionRelations", ValidationConstants.OBSOLETE_MESSAGE);
			}

			ObjectResponse<ConditionRelation> crResponse = conditionRelationValidator.validateConditionRelation(conditionRelation);
			conditionRelation = crResponse.getEntity();
			if (conditionRelation == null) {
				addMessageResponse("conditionRelations", crResponse.errorMessagesString());
				return null;
			}

			// reuse existing condition relation
			SearchResponse<ConditionRelation> crSearchResponse = conditionRelationDAO.findByField("uniqueId", conditionRelation.getUniqueId());
			if (crSearchResponse != null && crSearchResponse.getSingleResult() != null) {
				conditionRelation.setId(crSearchResponse.getSingleResult().getId());
				conditionRelation = conditionRelationDAO.merge(conditionRelation);
			} else if (conditionRelation.getId() == null) {
				conditionRelation = conditionRelationDAO.persist(crResponse.getEntity());
			}
			validatedConditionRelations.add(crResponse.getEntity());
		}
		return validatedConditionRelations;
	}

	public Reference validateSingleReference(Annotation uiEntity, Annotation dbEntity) {
		String field = "singleReference";
		if (ObjectUtils.isEmpty(uiEntity.getSingleReference()) || StringUtils.isBlank(uiEntity.getSingleReference().getCurie())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		ObjectResponse<Reference> singleRefResponse = referenceValidator.validateReference(uiEntity.getSingleReference());
		if (singleRefResponse.getEntity() == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (singleRefResponse.getEntity().getObsolete() && (dbEntity.getSingleReference() == null || !singleRefResponse.getEntity().getCurie().equals(dbEntity.getSingleReference().getCurie()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return singleRefResponse.getEntity();
	}

	public Annotation validateCommonAnnotationFields(Annotation uiEntity, Annotation dbEntity, String noteTypeSet) {
		Boolean newEntity = false;
		if (dbEntity.getId() == null) {
			newEntity = true;
		}
		dbEntity = validateAuditedObjectFields(uiEntity, dbEntity, newEntity);

		String primaryExternalId = StringUtils.isNotBlank(uiEntity.getPrimaryExternalId()) ? uiEntity.getPrimaryExternalId() : null;
		dbEntity.setPrimaryExternalId(primaryExternalId);

		String modInternalId = StringUtils.isNotBlank(uiEntity.getModInternalId()) ? uiEntity.getModInternalId() : null;
		dbEntity.setModInternalId(modInternalId);

		Reference singleReference = validateSingleReference(uiEntity, dbEntity);
		dbEntity.setSingleReference(singleReference);

		DataProvider dataProvider = validateDataProvider(uiEntity, dbEntity);
		dbEntity.setDataProvider(dataProvider);

		List<ConditionRelation> conditionRelations = validateConditionRelations(uiEntity, dbEntity);
		dbEntity.setConditionRelations(conditionRelations);

		List<Note> relatedNotes = validateRelatedNotes(uiEntity, dbEntity, noteTypeSet);
		if (dbEntity.getRelatedNotes() != null) {
			dbEntity.getRelatedNotes().clear();
		}
		if (relatedNotes != null) {
			if (dbEntity.getRelatedNotes() == null) {
				dbEntity.setRelatedNotes(new ArrayList<>());
			}
			dbEntity.getRelatedNotes().addAll(relatedNotes);
		}

		return dbEntity;
	}
}
