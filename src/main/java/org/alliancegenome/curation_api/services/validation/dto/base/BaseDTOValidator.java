package org.alliancegenome.curation_api.services.validation.dto.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.base.BaseEntityDAO;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.base.SubmittedObject;
import org.alliancegenome.curation_api.model.entities.ontology.MITerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.DataProviderDTO;
import org.alliancegenome.curation_api.model.ingest.dto.NoteDTO;
import org.alliancegenome.curation_api.model.ingest.dto.base.SubmittedObjectDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.OrganizationService;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.helpers.interactions.InteractionStringHelper;
import org.alliancegenome.curation_api.services.helpers.notes.NoteIdentityHelper;
import org.alliancegenome.curation_api.services.ontology.MiTermService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.services.validation.dto.CrossReferenceDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.NoteDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import jakarta.inject.Inject;

public class BaseDTOValidator<E extends Object> {

	@Inject OrganizationService organizationService;
	@Inject CrossReferenceDTOValidator crossReferenceDtoValidator;
	@Inject MiTermService miTermService;
	@Inject ReferenceService referenceService;
	@Inject VocabularyTermService vocabularyTermService;
	@Inject NoteDTOValidator noteDtoValidator;
	@Inject NoteDAO noteDAO;
	@Inject NcbiTaxonTermService ncbiTaxonTermService;

	protected ObjectResponse<E> response;
	
	protected HashMap<String, String> miCurieCache = new HashMap<>();
	protected HashMap<String, MITerm> miTermCache = new HashMap<>();
	
	protected HashMap<String, HashMap<String, VocabularyTerm>> vocabularyTermCache = new HashMap<>();
	protected HashMap<String, HashMap<String, OntologyTerm>> ontologyTermCache = new HashMap<>();

	protected String getCurieFromCache(String psiMiFormat) {
		if (miCurieCache.containsKey(psiMiFormat)) {
			return miCurieCache.get(psiMiFormat);
		} else {
			String curie = InteractionStringHelper.extractCurieFromPsiMiFormat(psiMiFormat);
			if (curie != null) {
				miCurieCache.put(psiMiFormat, curie);
				return curie;
			}
		}
		return null;
	}

	protected MITerm getTermFromCache(String curie) {
		if (miTermCache.containsKey(curie)) {
			return miTermCache.get(curie);
		} else {
			if (curie != null) {
				MITerm miTerm = miTermService.findByCurie(curie);
				if (miTerm != null) {
					miTermCache.put(curie, miTerm);
					return miTerm;
				}
			}
		}
		return null;
	}

	protected String handleStringField(String string) {
		if (StringUtils.isNotBlank(string)) {
			return string;
		}

		return null;
	}

	protected List<String> handleStringListField(List<String> list) {
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		return list;
	}
	
	protected <N extends AuditedObject, D extends BaseSQLDAO<N>> N findDatabaseObject(D dao, String field, String identifier) {
		return findDatabaseObject(dao, field, field, identifier);
	}
	
	protected <N extends AuditedObject, D extends BaseSQLDAO<N>> N findDatabaseObject(D dao, String dbField, String dtoField, String identifier) {
		N entity = null;
		if (StringUtils.isNotBlank(identifier)) {
			SearchResponse<N> response = dao.findByField(dbField, identifier);
			if (response != null && response.getSingleResult() != null) {
				entity = response.getSingleResult();
			}
		} else {
			response.addErrorMessage(dtoField, ValidationConstants.REQUIRED_MESSAGE);
		}

		return entity;
	}
	
	protected <N extends SubmittedObject, D extends BaseSQLDAO<N>, T extends SubmittedObjectDTO, S extends SubmittedObjectCrudService<N, T, D>> N validateIdentifier(S service, String field, String identifier) {
		return validateIdentifier(service, field, identifier, false);
	}
	
	protected <N extends SubmittedObject, D extends BaseSQLDAO<N>, T extends SubmittedObjectDTO, S extends SubmittedObjectCrudService<N, T, D>> N validateRequiredIdentifier(S service, String field, String identifier) {
		return validateIdentifier(service, field, identifier, true);
	}
	
	protected <N extends SubmittedObject, D extends BaseSQLDAO<N>, T extends SubmittedObjectDTO, S extends SubmittedObjectCrudService<N, T, D>> N validateIdentifier(S service, String field, String identifier, boolean isRequired) {
		if (StringUtils.isBlank(identifier)) {
			if (isRequired) {
				response.addErrorMessage(field, ValidationConstants.REQUIRED_MESSAGE);
			}
			return null;
		}
		
		N submittedObject = service.findByIdentifierString(identifier);
		if (submittedObject == null) {
			response.addErrorMessage(field, ValidationConstants.INVALID_MESSAGE + " (" + identifier + ")");
		}
		
		return submittedObject;
	}
	
	protected <N extends SubmittedObject, D extends BaseSQLDAO<N>, T extends SubmittedObjectDTO, S extends SubmittedObjectCrudService<N, T, D>> List<N> validateIdentifiers(S service, String field, List<String> identifiers) {
		return validateIdentifiers(service, field, identifiers, false);
	}
	
	protected <N extends SubmittedObject, D extends BaseSQLDAO<N>, T extends SubmittedObjectDTO, S extends SubmittedObjectCrudService<N, T, D>> List<N> validateRequiredIdentifiers(S service, String field, List<String> identifiers) {
		return validateIdentifiers(service, field, identifiers, true)
	;}
	
	protected <N extends SubmittedObject, D extends BaseSQLDAO<N>, T extends SubmittedObjectDTO, S extends SubmittedObjectCrudService<N, T, D>> List<N> validateIdentifiers(S service, String field, List<String> identifiers, boolean isRequired) {
		if (CollectionUtils.isEmpty(identifiers)) {
			if (isRequired) {
				response.addErrorMessage(field, ValidationConstants.REQUIRED_MESSAGE);
			}
			return null;
		}
		List<N> submittedObjects = new ArrayList<>();
		for (String identifier : identifiers) {
			N submittedObject = service.findByIdentifierString(identifier);
			if (submittedObject == null) {
				response.addErrorMessage(field, ValidationConstants.INVALID_MESSAGE + " (" + identifier + ")");
				return null;
			}
			submittedObjects.add(submittedObject);
		}
		
		return submittedObjects;
	}
	
	protected ObjectResponse<ImmutablePair<Organization, CrossReference>> validateDataProviderDTO(DataProviderDTO dto, CrossReference dbDataProviderXref) {
		Organization dataProvider = null;
		ObjectResponse<ImmutablePair<Organization, CrossReference>> dpResponse = new ObjectResponse<>();
		CrossReference dataProviderXref = null;
		if (StringUtils.isBlank(dto.getSourceOrganizationAbbreviation())) {
			dpResponse.addErrorMessage("source_organization_abbreviation", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<Organization> soResponse = organizationService.getByAbbr(dto.getSourceOrganizationAbbreviation());
			if (soResponse == null || soResponse.getEntity() == null) {
				dpResponse.addErrorMessage("source_organization_abbreviation", ValidationConstants.INVALID_MESSAGE + " (" + dto.getSourceOrganizationAbbreviation() + ")");
			} else {
				dataProvider = soResponse.getEntity();
			}
		}

		if (dto.getCrossReferenceDto() != null) {
			ObjectResponse<CrossReference> crResponse = crossReferenceDtoValidator.validateCrossReferenceDTO(dto.getCrossReferenceDto(), dbDataProviderXref);
			if (crResponse.hasErrors()) {
				dpResponse.addErrorMessage("cross_reference_dto", crResponse.errorMessagesString());
			} else {
				dataProviderXref = crResponse.getEntity();
			}
		}

		dpResponse.setEntity(new ImmutablePair<Organization, CrossReference>(dataProvider, dataProviderXref));
		return dpResponse;
	}
	
	protected Reference validateReference(String referenceCurie) {
		return validateReference(referenceCurie, false);
	}
	
	protected Reference validateRequiredReference(String referenceCurie) {
		return validateReference(referenceCurie, true);
	}
	
	protected Reference validateReference(String referenceCurie, boolean isRequired) {
		String field = "reference_curie";
		if (StringUtils.isBlank(referenceCurie)) {
			if (isRequired) {
				response.addErrorMessage(field, ValidationConstants.REQUIRED_MESSAGE);
			}
			return null;
		}
		
		Reference reference = referenceService.retrieveFromDbOrLiteratureService(referenceCurie);
		if (reference == null) {
			response.addErrorMessage(field, ValidationConstants.INVALID_MESSAGE + " (" + referenceCurie + ")");
		}
		
		return reference;
	}
	
	protected List<Reference> validateReferences(List<String> referenceCuries) {
		return validateReferences("reference_curies", referenceCuries, false);
	}
	
	protected List<Reference> validateRequiredReferences(List<String> referenceCuries) {
		return validateReferences("reference_curies", referenceCuries, true);
	}
	
	
	protected List<Reference> validateReferences(String field, List<String> referenceCuries) {
		return validateReferences(field, referenceCuries, false);
	}
	
	protected List<Reference> validateRequiredReferences(String field, List<String> referenceCuries) {
		return validateReferences(field, referenceCuries, true);
	}
	
	protected List<Reference> validateReferences(String field, List<String> referenceCuries, boolean isRequired) {
		if (CollectionUtils.isEmpty(referenceCuries)) {
			if (isRequired) {
				response.addErrorMessage(field, ValidationConstants.REQUIRED_MESSAGE);
			}
			return null;
		}
		
		List<Reference> references = new ArrayList<>();
		for (String referenceCurie : referenceCuries) {
			Reference reference = referenceService.retrieveFromDbOrLiteratureService(referenceCurie);
			if (reference == null) {
				response.addErrorMessage(field, ValidationConstants.INVALID_MESSAGE + " (" + referenceCurie + ")");
				return null;
			}
			references.add(reference);
		}
		
		return references;
	}
	
	protected <N extends OntologyTerm, D extends BaseEntityDAO<N>, S extends BaseOntologyTermService<N, D>> N validateOntologyTerm (S service, String field, String curie) {
		return validateOntologyTerm(service, field, curie, false);
	}
	
	protected <N extends OntologyTerm, D extends BaseEntityDAO<N>, S extends BaseOntologyTermService<N, D>> N validateRequiredOntologyTerm (S service, String field, String curie) {
		return validateOntologyTerm(service, field, curie, true);
	}
	
	protected <N extends OntologyTerm, D extends BaseEntityDAO<N>, S extends BaseOntologyTermService<N, D>> N validateOntologyTerm (S service, String field, String curie, boolean isRequired) {
		if (StringUtils.isBlank(curie)) {
			if (isRequired) {
				response.addErrorMessage(field, ValidationConstants.REQUIRED_MESSAGE);
			}
			return null;
		}
		
		N ontologyTerm = null;
		if (!ontologyTermCache.containsKey(service.getClass().getName())) {
			ontologyTermCache.put(service.getClass().getName(), new HashMap<String, OntologyTerm>());
		} else {
			ontologyTerm = (N) ontologyTermCache.get(service.getClass().getName()).get(curie);
		}
		if (ontologyTerm == null) {
			ontologyTerm = service.findByCurieOrSecondaryId(curie);
			
			if (ontologyTerm == null) {
				response.addErrorMessage(field, ValidationConstants.INVALID_MESSAGE + " (" + curie + ")");
				return null;
			}
			
			ontologyTerm.getSecondaryIdentifiers().size();
			ontologyTerm.getSynonyms().size();
			ontologyTermCache.get(service.getClass().getName()).put(curie, ontologyTerm);
		}
		
		return ontologyTerm;
	}
	
	protected <N extends OntologyTerm, D extends BaseEntityDAO<N>, S extends BaseOntologyTermService<N, D>> List<N> validateOntologyTerms (S service, String field, List<String> curies) {
		return validateOntologyTerms(service, field, curies, false);
	}
	
	protected <N extends OntologyTerm, D extends BaseEntityDAO<N>, S extends BaseOntologyTermService<N, D>> List<N> validateRequiredOntologyTerms (S service, String field, List<String> curies) {
		return validateOntologyTerms(service, field, curies, true);
	}
	
	protected <N extends OntologyTerm, D extends BaseEntityDAO<N>, S extends BaseOntologyTermService<N, D>> List<N> validateOntologyTerms (S service, String field, List<String> curies, boolean isRequired) {
		if (CollectionUtils.isEmpty(curies)) {
			if (isRequired) {
				response.addErrorMessage(field, ValidationConstants.REQUIRED_MESSAGE);
			}
			return null;
		}
		
		List<N> ontologyTerms = new ArrayList<>();
		if (!ontologyTermCache.containsKey(service.getClass().getName())) {
			ontologyTermCache.put(service.getClass().getName(), new HashMap<String, OntologyTerm>());
		}
		for (String curie : curies) {
			N ontologyTerm = (N) ontologyTermCache.get(service.getClass().getName()).get(curie);
			if (ontologyTerm == null) {
				ontologyTerm = service.findByCurieOrSecondaryId(curie);
				
				if (ontologyTerm == null) {
					response.addErrorMessage(field, ValidationConstants.INVALID_MESSAGE + " (" + curie + ")");
					return null;
				}
				
				ontologyTerm.getSecondaryIdentifiers().size();
				ontologyTerm.getSynonyms().size();
				ontologyTermCache.get(service.getClass().getName()).put(curie, ontologyTerm);
			}
			ontologyTerms.add(ontologyTerm);
		}
		
		return ontologyTerms;
	}
	
	protected VocabularyTerm validateTermInVocabulary(String field, String termName, String vocabularyName) {
		return validateVocabularyTerm(field, termName, vocabularyName, false, false);
	}
	
	protected VocabularyTerm validateRequiredTermInVocabulary(String field, String termName, String vocabularyName) {
		return validateVocabularyTerm(field, termName, vocabularyName, true, false);
	}
	
	protected VocabularyTerm validateTermInVocabularyTermSet(String field, String termName, String vocabularyTermSetName) {
		return validateVocabularyTerm(field, termName, vocabularyTermSetName, false, true);
	}
	
	protected VocabularyTerm validateRequiredTermInVocabularyTermSet(String field, String termName, String vocabularyTermSetName) {
		return validateVocabularyTerm(field, termName, vocabularyTermSetName, true, true);
	}
	
	protected VocabularyTerm validateVocabularyTerm(String field, String termName, String vocabularyOrSetName, boolean isRequired, boolean isTermSet) {
		if (StringUtils.isBlank(termName)) {
			if (isRequired) {
				response.addErrorMessage(field, ValidationConstants.REQUIRED_MESSAGE);
			}
			return null;
		}

		VocabularyTerm term = null;
		if (!vocabularyTermCache.containsKey(vocabularyOrSetName)) {
			vocabularyTermCache.put(vocabularyOrSetName, new HashMap<String, VocabularyTerm>());
		} else {
			term = vocabularyTermCache.get(vocabularyOrSetName).get(termName);
		}
		
		if (term == null) {
			if (isTermSet) {
				term = vocabularyTermService.getTermInVocabularyTermSet(vocabularyOrSetName, termName).getEntity();
			} else {
				term = vocabularyTermService.getTermInVocabulary(vocabularyOrSetName, termName).getEntity();
			}
			
			if (term == null) {
				response.addErrorMessage(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			}
			
			term.getSynonyms().size();
			vocabularyTermCache.get(vocabularyOrSetName).put(termName, term);
		}
		
		return term;
	}
	
	protected List<VocabularyTerm> validateTermsInVocabulary(String field, List<String> termNames, String vocabularyName) {
		return validateVocabularyTerms(field, termNames, vocabularyName, false, false);
	}
	
	protected List<VocabularyTerm> validateRequiredTermsInVocabulary(String field, List<String> termNames, String vocabularyName) {
		return validateVocabularyTerms(field, termNames, vocabularyName, true, false);
	}
	
	protected List<VocabularyTerm> validateTermsInVocabularyTermSet(String field, List<String> termNames, String vocabularyTermSetName) {
		return validateVocabularyTerms(field, termNames, vocabularyTermSetName, false, true);
	}
	
	protected List<VocabularyTerm> validateRequiredTermsInVocabularyTermSet(String field, List<String> termNames, String vocabularyTermSetName) {
		return validateVocabularyTerms(field, termNames, vocabularyTermSetName, true, true);
	}
	
	protected List<VocabularyTerm> validateVocabularyTerms(String field, List<String> termNames, String vocabularyOrSetName, boolean isRequired, boolean isTermSet) {
		if (CollectionUtils.isEmpty(termNames)) {
			if (isRequired) {
				response.addErrorMessage(field, ValidationConstants.REQUIRED_MESSAGE);
			}
			return null;
		}

		List<VocabularyTerm> terms = new ArrayList<>();
		if (!vocabularyTermCache.containsKey(vocabularyOrSetName)) {
			vocabularyTermCache.put(vocabularyOrSetName, new HashMap<String, VocabularyTerm>());
		}
		for (String termName : termNames) {
			VocabularyTerm term = vocabularyTermCache.get(vocabularyOrSetName).get(termName);
			if (term == null) {
				if (isTermSet) {
					term = vocabularyTermService.getTermInVocabularyTermSet(vocabularyOrSetName, termName).getEntity();
				} else {
					term = vocabularyTermService.getTermInVocabulary(vocabularyOrSetName, termName).getEntity();
				}
			
				if (term == null) {
					response.addErrorMessage(field, ValidationConstants.INVALID_MESSAGE);
					return null;
				} 
				
				term.getSynonyms().size();
				vocabularyTermCache.get(vocabularyOrSetName).put(termName, term);
			}
			terms.add(term);
		}

		return terms;
	}
	
	protected Note validateNote(NoteDTO dto, String noteTypeSet) {
		if (dto == null) {
			return null;
		}
		
		ObjectResponse<Note> noteResponse = noteDtoValidator.validateNoteDTO(dto, noteTypeSet);
		if (noteResponse.hasErrors()) {
			response.addErrorMessage("note_dto", noteResponse.errorMessagesString());
			return null;
		}
		
		return noteDAO.persist(noteResponse.getEntity());
	}
	
	protected List<Note> validateNotes(List<NoteDTO> dtos, String noteTypeSet) {
		return validateNotes(dtos, noteTypeSet, null);
	}
	
	protected List<Note> validateNotes(List<NoteDTO> dtos, String noteTypeSet, String expectedReference) {
		if (CollectionUtils.isEmpty(dtos)) {
			return null;
		}
		
		List<Note> validatedNotes = new ArrayList<Note>();
		List<String> noteIdentities = new ArrayList<String>();
		Boolean allNotesValid = true;
		
		for (int ix = 0; ix < dtos.size(); ix++) {
			ObjectResponse<Note> noteResponse = noteDtoValidator.validateNoteDTO(dtos.get(ix), noteTypeSet);
			if (noteResponse.hasErrors()) {
				allNotesValid = false;
				response.addErrorMessages("note_dtos", ix, noteResponse.getErrorMessages());
			} else {
				if (StringUtils.isNotBlank(expectedReference)) {
					if (CollectionUtils.isNotEmpty(dtos.get(ix).getEvidenceCuries())) {
						for (String noteRef : dtos.get(ix).getEvidenceCuries()) {
							if (!Objects.equals(noteRef, expectedReference)) {
								Map<String, String> noteRefErrorMessages = new HashMap<>();
								noteRefErrorMessages.put("evidence_curies", ValidationConstants.INVALID_MESSAGE + " (" + noteRef + ")");
								response.addErrorMessages("note_dtos", ix, noteRefErrorMessages);
								allNotesValid = false;
							}
						}
					}
				}
				String noteIdentity = NoteIdentityHelper.noteDtoIdentity(dtos.get(ix));
				if (!noteIdentities.contains(noteIdentity)) {
					noteIdentities.add(noteIdentity);
					validatedNotes.add(noteDAO.persist(noteResponse.getEntity()));
				}
			}
		}

		if (!allNotesValid) {
			response.convertMapToErrorMessages("note_dtos");
			return null;
		}
		
		return validatedNotes;
	}
	
	protected NCBITaxonTerm validateTaxon(String field, String curie) {
		return  validateTaxon(field, curie, false);
	}
	
	protected NCBITaxonTerm validateRequiredTaxon(String field, String curie) {
		return  validateTaxon(field, curie, true);
	}
	
	protected NCBITaxonTerm validateTaxon(String field, String curie, boolean isRequired) {
		if (StringUtils.isBlank(curie)) {
			if (isRequired) {
				response.addErrorMessage(field, ValidationConstants.REQUIRED_MESSAGE);
			}
			return null;
		}
		
		ObjectResponse<NCBITaxonTerm> taxonResponse = ncbiTaxonTermService.getByCurie(curie);
		if (taxonResponse.getEntity() == null) {
			response.addErrorMessage(field, ValidationConstants.INVALID_MESSAGE + " (" + curie + ")");
		}
		
		return taxonResponse.getEntity();
	}

}
