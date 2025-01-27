package org.alliancegenome.curation_api.services.validation.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.auth.AuthenticatedUser;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.OrganizationDAO;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.CrossReferenceService;
import org.alliancegenome.curation_api.services.OrganizationService;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.notes.NoteIdentityHelper;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.services.validation.CrossReferenceValidator;
import org.alliancegenome.curation_api.services.validation.NoteValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Objects;

import jakarta.inject.Inject;

public class BaseValidator<E extends Object> {

	@Inject
	@AuthenticatedUser protected Person authenticatedPerson;

	@Inject CrossReferenceDAO crossReferenceDAO;
	@Inject CrossReferenceService crossReferenceService;
	@Inject CrossReferenceValidator crossReferenceValidator;
	@Inject NcbiTaxonTermService ncbiTaxonTermService;
	@Inject OrganizationDAO organizationDAO;
	@Inject OrganizationService organizationService;
	@Inject PersonService personService;
	@Inject VocabularyTermService vocabularyTermService;
	@Inject NoteValidator noteValidator;
	
	protected ObjectResponse<E> response;

	protected String handleStringField(String string) {
		if (!StringUtils.isBlank(string)) {
			return string;
		}
		return null;
	}

	protected List<E> handleListField(List<E> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			return list;
		}
		return null;
	}

	protected void addMessageResponse(String message) {
		response.setErrorMessage(message);
	}

	protected void addMessageResponse(String fieldName, String message) {
		response.addErrorMessage(fieldName, message);
	}

	protected void convertMapToErrorMessages(String fieldName) {
		response.convertMapToErrorMessages(fieldName);
	}

	protected Organization validateDataProvider(Organization uiDataProvider, Organization dbDataProvider, boolean newEntity) {
		String field = "dataProvider";

		if (uiDataProvider == null) {
			if (newEntity) {
				return organizationDAO.getOrCreateOrganization("Alliance");
			} else {
				addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
				return null;
			}
		}
		
		Organization dataProvider = null;
		if (uiDataProvider.getId() != null) {
			dataProvider = organizationService.getById(uiDataProvider.getId()).getEntity();
		} else if (StringUtils.isNotBlank(uiDataProvider.getAbbreviation())) {
			dataProvider = organizationService.getByAbbr(uiDataProvider.getAbbreviation()).getEntity();
		}
		
		if (dataProvider == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (dataProvider.getObsolete() && (dbDataProvider == null || !dataProvider.getId().equals(dbDataProvider.getId()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return dataProvider;
	}
	
	protected CrossReference validateDataProviderCrossReference(CrossReference uiXref, CrossReference dbXref) {
		return validateDataProviderCrossReference(uiXref, dbXref, false);
	}

	protected CrossReference validateDataProviderCrossReference(CrossReference uiXref, CrossReference dbXref, boolean isSecondaryProvider) {
		String fieldName = isSecondaryProvider ? "secondaryDataProviderCrossReference" : "dataProviderCrossReference";
		
		CrossReference xref = null;
		String dbXrefUniqueId = null;
		String uiXrefUniqueId = null;
		if (dbXref != null) {
			dbXrefUniqueId = crossReferenceService.getCrossReferenceUniqueId(dbXref);
		}
		
		if (ObjectUtils.isNotEmpty(uiXref)) {
			ObjectResponse<CrossReference> xrefResponse = crossReferenceValidator.validateCrossReference(uiXref, false);
			if (xrefResponse.hasErrors()) {
				addMessageResponse(fieldName, xrefResponse.errorMessagesString());
			} else {
				uiXrefUniqueId = crossReferenceService.getCrossReferenceUniqueId(xrefResponse.getEntity());
				if (dbXrefUniqueId == null || !dbXrefUniqueId.equals(uiXrefUniqueId)) {
					xref = crossReferenceDAO.persist(xrefResponse.getEntity());
				} else if (dbXrefUniqueId != null && dbXrefUniqueId.equals(uiXrefUniqueId)) {
					xref = crossReferenceService.updateCrossReference(dbXref, uiXref);
				}
			}
		}
		
		return xref;
	}
	
	protected <N extends AuditedObject, D extends BaseSQLDAO<N>> N validateEntity(D dao, String field, N uiEntity, N dbEntity) {
		return validateEntity(dao, field, uiEntity, dbEntity, false);
	}
	
	protected <N extends AuditedObject, D extends BaseSQLDAO<N>> N validateRequiredEntity(D dao, String field, N uiEntity, N dbEntity) {
		return validateEntity(dao, field, uiEntity, dbEntity, true);
	}
	
	protected <N extends AuditedObject, D extends BaseSQLDAO<N>> N validateEntity(D dao, String field, N uiEntity, N dbEntity, boolean isRequired) {
		if (uiEntity == null) {
			if (isRequired) {
				addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			}
			return null;
		}
		
		N entity = null;
		if (uiEntity.getId() != null) {
			entity = dao.find(uiEntity.getId());
		}
		if (entity == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		
		if (entity.getObsolete() && (dbEntity == null || !entity.getId().equals(dbEntity.getId()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		
		return entity;
	}
	
	protected <N extends AuditedObject, D extends BaseSQLDAO<N>> List<N> validateEntities(D dao, String field, List<N> uiEntities, List<N> dbEntities) {
		return validateEntities(dao, field, uiEntities, dbEntities, false);
	}
	
	protected <N extends AuditedObject, D extends BaseSQLDAO<N>> List<N> validateRequiredEntities(D dao, String field, List<N> uiEntities, List<N> dbEntities) {
		return validateEntities(dao, field, uiEntities, dbEntities, true);
	}
	
	protected <N extends AuditedObject, D extends BaseSQLDAO<N>> List<N> validateEntities(D dao, String field, List<N> uiEntities, List<N> dbEntities, boolean isRequired) {
		if (CollectionUtils.isEmpty(uiEntities)) {
			if (isRequired) {
				addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			}
			return null;
		}
		
		List<N> entities = new ArrayList<N>();
		List<Long> previousIds = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(dbEntities)) {
			previousIds = dbEntities.stream().map(N::getId).collect(Collectors.toList());
		}
		for (N uiEntity : uiEntities) {
			N entity = null;
			if (uiEntity.getId() != null) {
				entity = dao.find(uiEntity.getId());
			}
			if (entity == null) {
				addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			}
		
			if (entity.getObsolete() && !previousIds.contains(entity.getId())) {
				addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}
			entities.add(entity);
		}
		
		return entities;
	}
	
	protected NCBITaxonTerm validateTaxon(NCBITaxonTerm uiTaxon, NCBITaxonTerm dbTaxon) {
		return validateTaxon(uiTaxon, dbTaxon, false, "taxon");
	}
	
	protected NCBITaxonTerm validateRequiredTaxon(NCBITaxonTerm uiTaxon, NCBITaxonTerm dbTaxon) {
		return validateTaxon(uiTaxon, dbTaxon, true, "taxon");
	}
	
	protected NCBITaxonTerm validateTaxon(NCBITaxonTerm uiTaxon, NCBITaxonTerm dbTaxon, String field) {
		return validateTaxon(uiTaxon, dbTaxon, false, field);
	}

	protected NCBITaxonTerm validateTaxon(NCBITaxonTerm uiTaxon, NCBITaxonTerm dbTaxon, boolean isRequired, String field) {
		if (uiTaxon == null) {
			if (isRequired) {
				addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			}
			return null;
		}

		NCBITaxonTerm taxon = null;
		if (StringUtils.isNotBlank(uiTaxon.getCurie())) {
			ObjectResponse<NCBITaxonTerm> taxonResponse = ncbiTaxonTermService.getByCurie(uiTaxon.getCurie());
			if (taxonResponse == null || taxonResponse.getEntity() == null) {
				addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			}

			taxon = taxonResponse.getEntity();
			if (taxon.getObsolete() && (dbTaxon == null || !taxon.getCurie().equals(dbTaxon.getCurie()))) {
				addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}
		}

		return taxon;
	}
	
	protected VocabularyTerm validateTermInVocabulary(String field, String vocabularyName, VocabularyTerm uiTerm, VocabularyTerm dbTerm) {
		return validateVocabularyTerm(field, vocabularyName, uiTerm, dbTerm, false, false);
	}
	
	protected VocabularyTerm validateRequiredTermInVocabulary(String field, String vocabularyName, VocabularyTerm uiTerm, VocabularyTerm dbTerm) {
		return validateVocabularyTerm(field, vocabularyName, uiTerm, dbTerm, true, false);
	}
	
	protected VocabularyTerm validateTermInVocabularyTermSet(String field, String vocabularyName, VocabularyTerm uiTerm, VocabularyTerm dbTerm) {
		return validateVocabularyTerm(field, vocabularyName, uiTerm, dbTerm, false, true);
	}
	
	protected VocabularyTerm validateRequiredTermInVocabularyTermSet(String field, String vocabularyName, VocabularyTerm uiTerm, VocabularyTerm dbTerm) {
		return validateVocabularyTerm(field, vocabularyName, uiTerm, dbTerm, true, true);
	}
	
	protected VocabularyTerm validateVocabularyTerm(String field, String vocabularyOrSetName, VocabularyTerm uiTerm, VocabularyTerm dbTerm, boolean isRequired, boolean isTermSet) {
		if (uiTerm == null) {
			if (isRequired) {
				addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			}
			return null;
		}

		VocabularyTerm term = null;
		if (isTermSet) {
			term = vocabularyTermService.getTermInVocabularyTermSet(vocabularyOrSetName, uiTerm.getName()).getEntity();
		} else {
			term = vocabularyTermService.getTermInVocabulary(vocabularyOrSetName, uiTerm.getName()).getEntity();
		}
		
		if (term == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (term.getObsolete() && (dbTerm == null || !term.getName().equals(dbTerm.getName()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return term;
	}
	
	protected List<VocabularyTerm> validateTermsInVocabulary(String field, String vocabularyName, List<VocabularyTerm> uiTerms, List<VocabularyTerm> dbTerms) {
		return validateVocabularyTerms(field, vocabularyName, uiTerms, dbTerms, false, false);
	}
	
	protected List<VocabularyTerm> validateRequiredTermsInVocabulary(String field, String vocabularyName, List<VocabularyTerm> uiTerms, List<VocabularyTerm> dbTerms) {
		return validateVocabularyTerms(field, vocabularyName, uiTerms, dbTerms, true, false);
	}
	
	protected List<VocabularyTerm> validateTermsInVocabularyTermSet(String field, String vocabularyTermSetName, List<VocabularyTerm> uiTerms, List<VocabularyTerm> dbTerms) {
		return validateVocabularyTerms(field, vocabularyTermSetName, uiTerms, dbTerms, false, true);
	}
	
	protected List<VocabularyTerm> validateRequiredTermsInVocabularyTermSet(String field, String vocabularyTermSetName, List<VocabularyTerm> uiTerms, List<VocabularyTerm> dbTerms) {
		return validateVocabularyTerms(field, vocabularyTermSetName, uiTerms, dbTerms, true, true);
	}
	
	protected List<VocabularyTerm> validateVocabularyTerms(String field, String vocabularyOrSetName, List<VocabularyTerm> uiTerms, List<VocabularyTerm> dbTerms, boolean isRequired, boolean isTermSet) {
		if (CollectionUtils.isEmpty(uiTerms)) {
			if (isRequired) {
				addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			}
			return null;
		}
		
		List<VocabularyTerm> validTerms = new ArrayList<>();
		List<Long> previousIds = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(dbTerms)) {
			previousIds = dbTerms.stream().map(VocabularyTerm::getId).collect(Collectors.toList());
		}
		for (VocabularyTerm uiTerm : uiTerms) {
			VocabularyTerm term = null;
			if (isTermSet) {
				term = vocabularyTermService.getTermInVocabularyTermSet(vocabularyOrSetName, uiTerm.getName()).getEntity();
			} else {
				term = vocabularyTermService.getTermInVocabulary(vocabularyOrSetName, uiTerm.getName()).getEntity();
			}
			
			if (term == null) {
				addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			}
			
			if (term.getObsolete() && !previousIds.contains(term.getId())) {
				addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}
			
			validTerms.add(term);
		}
		
		return validTerms;
	}

	protected Note validateRelatedNote(Note uiNote, String noteTypeVocabularyTermSet) {
		String field = "relatedNote";

		if (uiNote == null) {
			return null;
		}

		ObjectResponse<Note> noteResponse = noteValidator.validateNote(uiNote, noteTypeVocabularyTermSet);
		if (noteResponse.getEntity() == null) {
			addMessageResponse(field, noteResponse.errorMessagesString());
			return null;
		}
		return noteResponse.getEntity();
	}
	
	protected List<Note> validateRelatedNotes(List<Note> uiNotes) {
		return validateRelatedNotes(uiNotes, null, null);
	}
	
	protected List<Note> validateRelatedNotes(List<Note> uiNotes, String noteTypeVocabularyTermSet) {
		return validateRelatedNotes(uiNotes, noteTypeVocabularyTermSet, null);
	}
	
	protected List<Note> validateRelatedNotes(List<Note> uiNotes, String noteTypeVocabularyTermSet, Reference expectedReference) {
		String field = "relatedNotes";

		List<Note> validatedNotes = new ArrayList<Note>();
		Set<String> validatedNoteIdentities = new HashSet<>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiNotes)) {
			for (int ix = 0; ix < uiNotes.size(); ix++) {
				Note note = uiNotes.get(ix);
				ObjectResponse<Note> noteResponse = noteValidator.validateNote(note, noteTypeVocabularyTermSet);
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
						boolean expectedRefs = true;
						if (expectedReference != null && expectedReference.getCurie() != null && CollectionUtils.isNotEmpty(note.getReferences())) {
							for (Reference noteRef : note.getReferences()) {
								if (!Objects.equal(noteRef.getCurie(), expectedReference.getCurie())) {
									Map<String, String> noteRefErrorMessages = new HashMap<>();
									noteRefErrorMessages.put("references", ValidationConstants.INVALID_MESSAGE + " (" + noteRef + ")");
									response.addErrorMessages("relatedNotes", ix, noteRefErrorMessages);
									allValid = false;
									expectedRefs = false;
									break;
								}
							}
						}
						if (expectedRefs) {
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

}
