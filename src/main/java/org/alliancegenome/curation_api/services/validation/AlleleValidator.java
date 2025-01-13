package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleDatabaseStatusSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFunctionalImpactSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleNomenclatureEventSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.associations.AssociationIdentityHelper;
import org.alliancegenome.curation_api.services.helpers.notes.NoteIdentityHelper;
import org.alliancegenome.curation_api.services.validation.associations.alleleAssociations.AlleleGeneAssociationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleDatabaseStatusSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleFunctionalImpactSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleNomenclatureEventSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotationValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleValidator extends GenomicEntityValidator<Allele> {

	@Inject AlleleDAO alleleDAO;
	@Inject AlleleMutationTypeSlotAnnotationValidator alleleMutationTypeValidator;
	@Inject AlleleInheritanceModeSlotAnnotationValidator alleleInheritanceModeValidator;
	@Inject AlleleGermlineTransmissionStatusSlotAnnotationValidator alleleGermlineTransmissionStatusValidator;
	@Inject AlleleNomenclatureEventSlotAnnotationValidator alleleNomenclatureEventValidator;
	@Inject AlleleSymbolSlotAnnotationValidator alleleSymbolValidator;
	@Inject AlleleFullNameSlotAnnotationValidator alleleFullNameValidator;
	@Inject AlleleSynonymSlotAnnotationValidator alleleSynonymValidator;
	@Inject AlleleSecondaryIdSlotAnnotationValidator alleleSecondaryIdValidator;
	@Inject AlleleFunctionalImpactSlotAnnotationValidator alleleFunctionalImpactValidator;
	@Inject AlleleDatabaseStatusSlotAnnotationValidator alleleDatabaseStatusValidator;
	@Inject AlleleGeneAssociationValidator alleleGeneAssociationValidator;
	@Inject NoteValidator noteValidator;
	@Inject ReferenceValidator referenceValidator;
	
	private String errorMessage;

	public Allele validateAlleleUpdate(Allele uiEntity, Boolean updateAllAssociations) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update Allele: [" + uiEntity.getIdentifier() + "]";

		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No Allele ID provided");
			throw new ApiErrorException(response);
		}

		Allele dbEntity = alleleDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("id", ValidationConstants.INVALID_MESSAGE);
			throw new ApiErrorException(response);
		}

		dbEntity = (Allele) validateAuditedObjectFields(uiEntity, dbEntity, false);

		return validateAllele(uiEntity, dbEntity, updateAllAssociations);
	}

	public Allele validateAlleleCreate(Allele uiEntity) {
		response = new ObjectResponse<>();
		errorMessage = "Could not create Allele";

		Allele dbEntity = new Allele();
		String curie = validateCurie(uiEntity);
		dbEntity.setCurie(curie);

		dbEntity = (Allele) validateAuditedObjectFields(uiEntity, dbEntity, true);

		return validateAllele(uiEntity, dbEntity, true);
	}

	public Allele validateAllele(Allele uiEntity, Allele dbEntity, Boolean updateAllAssociations) {

		dbEntity = (Allele) validateGenomicEntityFields(uiEntity, dbEntity);

		List<Reference> references = validateReferences(uiEntity, dbEntity);
		dbEntity.setReferences(references);

		VocabularyTerm inCollection = validateTermInVocabulary("inCollection", VocabularyConstants.ALLELE_COLLECTION_VOCABULARY, dbEntity.getInCollection(), uiEntity.getInCollection());
		dbEntity.setInCollection(inCollection);

		if (uiEntity.getIsExtinct() != null) {
			dbEntity.setIsExtinct(uiEntity.getIsExtinct());
		} else {
			dbEntity.setIsExtinct(null);
		}

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

		AlleleSymbolSlotAnnotation symbol = validateAlleleSymbol(uiEntity, dbEntity);
		AlleleFullNameSlotAnnotation fullName = validateAlleleFullName(uiEntity, dbEntity);
		AlleleGermlineTransmissionStatusSlotAnnotation germlineTransmissionStatus = validateAlleleGermlineTransmissionStatus(uiEntity, dbEntity);
		AlleleDatabaseStatusSlotAnnotation databaseStatus = validateAlleleDatabaseStatus(uiEntity, dbEntity);
		List<AlleleSynonymSlotAnnotation> synonyms = validateAlleleSynonyms(uiEntity, dbEntity);
		List<AlleleSecondaryIdSlotAnnotation> secondaryIds = validateAlleleSecondaryIds(uiEntity, dbEntity);
		List<AlleleInheritanceModeSlotAnnotation> inheritanceModes = validateAlleleInheritanceModes(uiEntity, dbEntity);
		List<AlleleFunctionalImpactSlotAnnotation> functionalImpacts = validateAlleleFunctionalImpacts(uiEntity, dbEntity);
		List<AlleleMutationTypeSlotAnnotation> mutationTypes = validateAlleleMutationTypes(uiEntity, dbEntity);
		List<AlleleNomenclatureEventSlotAnnotation> nomenclatureEvents = validateAlleleNomenclatureEvents(uiEntity, dbEntity);

		List<AlleleGeneAssociation> geneAssociations = null;
		if (updateAllAssociations) { // This should contain logic for all fields only returned in AlleleDetailView
			geneAssociations = validateAlleleGeneAssociations(uiEntity, dbEntity);
		}

		response.convertErrorMessagesToMap();

		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		dbEntity = alleleDAO.persist(dbEntity);

		if (symbol != null) {
			symbol.setSingleAllele(dbEntity);
		}
		dbEntity.setAlleleSymbol(symbol);

		if (fullName != null) {
			fullName.setSingleAllele(dbEntity);
		}
		dbEntity.setAlleleFullName(fullName);

		if (germlineTransmissionStatus != null) {
			germlineTransmissionStatus.setSingleAllele(dbEntity);
		}
		dbEntity.setAlleleGermlineTransmissionStatus(germlineTransmissionStatus);

		if (databaseStatus != null) {
			databaseStatus.setSingleAllele(dbEntity);
		}
		dbEntity.setAlleleDatabaseStatus(databaseStatus);

		if (dbEntity.getAlleleSynonyms() != null) {
			dbEntity.getAlleleSynonyms().clear();
		}
		if (synonyms != null) {
			if (dbEntity.getAlleleSynonyms() == null) {
				dbEntity.setAlleleSynonyms(new ArrayList<>());
			}
			dbEntity.getAlleleSynonyms().addAll(synonyms);
		}

		if (dbEntity.getAlleleSecondaryIds() != null) {
			dbEntity.getAlleleSecondaryIds().clear();
		}
		if (secondaryIds != null) {
			if (dbEntity.getAlleleSecondaryIds() == null) {
				dbEntity.setAlleleSecondaryIds(new ArrayList<>());
			}
			dbEntity.getAlleleSecondaryIds().addAll(secondaryIds);
		}

		if (dbEntity.getAlleleInheritanceModes() != null) {
			dbEntity.getAlleleInheritanceModes().clear();
		}
		if (inheritanceModes != null) {
			if (dbEntity.getAlleleInheritanceModes() == null) {
				dbEntity.setAlleleInheritanceModes(new ArrayList<>());
			}
			for (AlleleInheritanceModeSlotAnnotation im : inheritanceModes) {
				im.setSingleAllele(dbEntity);
			}
			dbEntity.getAlleleInheritanceModes().addAll(inheritanceModes);
		}

		if (dbEntity.getAlleleFunctionalImpacts() != null) {
			dbEntity.getAlleleFunctionalImpacts().clear();
		}
		if (functionalImpacts != null) {
			if (dbEntity.getAlleleFunctionalImpacts() == null) {
				dbEntity.setAlleleFunctionalImpacts(new ArrayList<>());
			}
			for (AlleleFunctionalImpactSlotAnnotation fi : functionalImpacts) {
				fi.setSingleAllele(dbEntity);
			}
			dbEntity.getAlleleFunctionalImpacts().addAll(functionalImpacts);
		}

		if (dbEntity.getAlleleMutationTypes() != null) {
			dbEntity.getAlleleMutationTypes().clear();
		}
		if (mutationTypes != null) {
			if (dbEntity.getAlleleMutationTypes() == null) {
				dbEntity.setAlleleMutationTypes(new ArrayList<>());
			}
			for (AlleleMutationTypeSlotAnnotation mt : mutationTypes) {
				mt.setSingleAllele(dbEntity);
			}
			dbEntity.getAlleleMutationTypes().addAll(mutationTypes);
		}

		if (dbEntity.getAlleleNomenclatureEvents() != null) {
			dbEntity.getAlleleNomenclatureEvents().clear();
		}
		if (nomenclatureEvents != null) {
			if (dbEntity.getAlleleNomenclatureEvents() == null) {
				dbEntity.setAlleleNomenclatureEvents(new ArrayList<>());
			}
			for (AlleleNomenclatureEventSlotAnnotation nm : nomenclatureEvents) {
				nm.setSingleAllele(dbEntity);
			}
			dbEntity.getAlleleNomenclatureEvents().addAll(nomenclatureEvents);
		}

		if (updateAllAssociations) { // This should contain logic for all fields only returned in AlleleDetailView
			if (dbEntity.getAlleleGeneAssociations() != null) {
				dbEntity.getAlleleGeneAssociations().clear();
			}
			if (geneAssociations != null) {
				if (dbEntity.getAlleleGeneAssociations() == null) {
					dbEntity.setAlleleGeneAssociations(new ArrayList<>());
				}
				dbEntity.getAlleleGeneAssociations().addAll(geneAssociations);
			}
		}
		return dbEntity;
	}

	private ObjectResponse<Reference> validateReference(Reference uiEntity, List<String> previousCuries) {
		ObjectResponse<Reference> singleRefResponse = referenceValidator.validateReference(uiEntity);
		if (singleRefResponse.getEntity() == null) {
			return singleRefResponse;
		}

		if (singleRefResponse.getEntity().getObsolete() && (CollectionUtils.isEmpty(previousCuries) || !previousCuries.contains(singleRefResponse.getEntity().getCurie()))) {
			singleRefResponse.setEntity(null);
			singleRefResponse.addErrorMessage("curie", ValidationConstants.OBSOLETE_MESSAGE);
		}

		return singleRefResponse;
	}

	public List<Reference> validateReferences(Allele uiEntity, Allele dbEntity) {
		String field = "references";

		List<Reference> validatedReferences = new ArrayList<Reference>();
		List<String> previousReferenceCuries = new ArrayList<String>();
		if (CollectionUtils.isNotEmpty(dbEntity.getReferences())) {
			previousReferenceCuries = dbEntity.getReferences().stream().map(Reference::getCurie).collect(Collectors.toList());
		}

		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getReferences())) {
			for (int ix = 0; ix < uiEntity.getReferences().size(); ix++) {
				ObjectResponse<Reference> refResponse = validateReference(uiEntity.getReferences().get(ix), previousReferenceCuries);
				if (refResponse.getEntity() == null) {
					allValid = false;
					response.addErrorMessages(field, ix, refResponse.getErrorMessages());
				} else {
					validatedReferences.add(refResponse.getEntity());
				}
			}
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedReferences)) {
			return null;
		}

		return validatedReferences;
	}

	public List<Note> validateRelatedNotes(Allele uiEntity, Allele dbEntity) {
		String field = "relatedNotes";

		List<Note> validatedNotes = new ArrayList<Note>();
		Set<String> validatedNoteIdentities = new HashSet<>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getRelatedNotes())) {
			for (int ix = 0; ix < uiEntity.getRelatedNotes().size(); ix++) {
				Note note = uiEntity.getRelatedNotes().get(ix);
				ObjectResponse<Note> noteResponse = noteValidator.validateNote(note, VocabularyConstants.ALLELE_NOTE_TYPES_VOCABULARY_TERM_SET);
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

	private List<AlleleMutationTypeSlotAnnotation> validateAlleleMutationTypes(Allele uiEntity, Allele dbEntity) {
		String field = "alleleMutationTypes";

		List<AlleleMutationTypeSlotAnnotation> validatedMutationTypes = new ArrayList<AlleleMutationTypeSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getAlleleMutationTypes())) {
			for (int ix = 0; ix < uiEntity.getAlleleMutationTypes().size(); ix++) {
				AlleleMutationTypeSlotAnnotation mt = uiEntity.getAlleleMutationTypes().get(ix);
				ObjectResponse<AlleleMutationTypeSlotAnnotation> mtResponse = alleleMutationTypeValidator.validateAlleleMutationTypeSlotAnnotation(mt);
				if (mtResponse.getEntity() == null) {
					allValid = false;
					response.addErrorMessages(field, ix, mtResponse.getErrorMessages());
				} else {
					validatedMutationTypes.add(mtResponse.getEntity());
				}
			}
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedMutationTypes)) {
			return null;
		}

		return validatedMutationTypes;
	}

	private List<AlleleInheritanceModeSlotAnnotation> validateAlleleInheritanceModes(Allele uiEntity, Allele dbEntity) {
		String field = "alleleInheritanceModes";

		List<AlleleInheritanceModeSlotAnnotation> validatedInheritanceModes = new ArrayList<AlleleInheritanceModeSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getAlleleInheritanceModes())) {
			for (int ix = 0; ix < uiEntity.getAlleleInheritanceModes().size(); ix++) {
				AlleleInheritanceModeSlotAnnotation im = uiEntity.getAlleleInheritanceModes().get(ix);
				ObjectResponse<AlleleInheritanceModeSlotAnnotation> imResponse = alleleInheritanceModeValidator.validateAlleleInheritanceModeSlotAnnotation(im);
				if (imResponse.getEntity() == null) {
					response.addErrorMessages(field, ix, imResponse.getErrorMessages());
					allValid = false;
				} else {
					validatedInheritanceModes.add(imResponse.getEntity());
				}
			}
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedInheritanceModes)) {
			return null;
		}

		return validatedInheritanceModes;
	}

	private AlleleGermlineTransmissionStatusSlotAnnotation validateAlleleGermlineTransmissionStatus(Allele uiEntity, Allele dbEntity) {
		if (uiEntity.getAlleleGermlineTransmissionStatus() == null) {
			return null;
		}

		String field = "alleleGermlineTransmissionStatus";

		ObjectResponse<AlleleGermlineTransmissionStatusSlotAnnotation> agtsResponse = alleleGermlineTransmissionStatusValidator.validateAlleleGermlineTransmissionStatusSlotAnnotation(uiEntity.getAlleleGermlineTransmissionStatus());
		if (agtsResponse.getEntity() == null) {
			addMessageResponse(field, agtsResponse.errorMessagesString());
			response.addErrorMessages(field, agtsResponse.getErrorMessages());
			return null;
		}

		return agtsResponse.getEntity();
	}

	private AlleleDatabaseStatusSlotAnnotation validateAlleleDatabaseStatus(Allele uiEntity, Allele dbEntity) {
		if (uiEntity.getAlleleDatabaseStatus() == null) {
			return null;
		}

		String field = "alleleDatabaseStatus";

		ObjectResponse<AlleleDatabaseStatusSlotAnnotation> adsResponse = alleleDatabaseStatusValidator.validateAlleleDatabaseStatusSlotAnnotation(uiEntity.getAlleleDatabaseStatus());
		if (adsResponse.getEntity() == null) {
			addMessageResponse(field, adsResponse.errorMessagesString());
			response.addErrorMessages(field, adsResponse.getErrorMessages());
			return null;
		}

		return adsResponse.getEntity();
	}

	private List<AlleleNomenclatureEventSlotAnnotation> validateAlleleNomenclatureEvents(Allele uiEntity, Allele dbEntity) {
		String field = "alleleNomenclatureEvents";

		List<AlleleNomenclatureEventSlotAnnotation> validatedNomenclatureEvents = new ArrayList<AlleleNomenclatureEventSlotAnnotation>();
		if (CollectionUtils.isNotEmpty(uiEntity.getAlleleNomenclatureEvents())) {
			for (int ix = 0; ix < uiEntity.getAlleleNomenclatureEvents().size(); ix++) {
				AlleleNomenclatureEventSlotAnnotation ne = uiEntity.getAlleleNomenclatureEvents().get(ix);
				ObjectResponse<AlleleNomenclatureEventSlotAnnotation> neResponse = alleleNomenclatureEventValidator.validateAlleleNomenclatureEventSlotAnnotation(ne);
				if (neResponse.getEntity() == null) {
					response.addErrorMessages(field, ix, neResponse.getErrorMessages());
					addMessageResponse(field, neResponse.errorMessagesString());
				} else {
					validatedNomenclatureEvents.add(neResponse.getEntity());
				}
			}
		}

		if (CollectionUtils.isEmpty(validatedNomenclatureEvents)) {
			return null;
		}

		return validatedNomenclatureEvents;
	}

	private AlleleSymbolSlotAnnotation validateAlleleSymbol(Allele uiEntity, Allele dbEntity) {
		String field = "alleleSymbol";

		if (uiEntity.getAlleleSymbol() == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		ObjectResponse<AlleleSymbolSlotAnnotation> symbolResponse = alleleSymbolValidator.validateAlleleSymbolSlotAnnotation(uiEntity.getAlleleSymbol());
		if (symbolResponse.getEntity() == null) {
			addMessageResponse(field, symbolResponse.errorMessagesString());
			response.addErrorMessages(field, symbolResponse.getErrorMessages());
			return null;
		}

		return symbolResponse.getEntity();
	}

	private AlleleFullNameSlotAnnotation validateAlleleFullName(Allele uiEntity, Allele dbEntity) {
		if (uiEntity.getAlleleFullName() == null) {
			return null;
		}

		String field = "alleleFullName";

		ObjectResponse<AlleleFullNameSlotAnnotation> nameResponse = alleleFullNameValidator.validateAlleleFullNameSlotAnnotation(uiEntity.getAlleleFullName());
		if (nameResponse.getEntity() == null) {
			addMessageResponse(field, nameResponse.errorMessagesString());
			response.addErrorMessages(field, nameResponse.getErrorMessages());
			return null;
		}

		return nameResponse.getEntity();
	}

	private List<AlleleSynonymSlotAnnotation> validateAlleleSynonyms(Allele uiEntity, Allele dbEntity) {
		String field = "alleleSynonyms";

		List<AlleleSynonymSlotAnnotation> validatedSynonyms = new ArrayList<AlleleSynonymSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getAlleleSynonyms())) {
			for (int ix = 0; ix < uiEntity.getAlleleSynonyms().size(); ix++) {
				AlleleSynonymSlotAnnotation syn = uiEntity.getAlleleSynonyms().get(ix);
				ObjectResponse<AlleleSynonymSlotAnnotation> synResponse = alleleSynonymValidator.validateAlleleSynonymSlotAnnotation(syn);
				if (synResponse.getEntity() == null) {
					response.addErrorMessages(field, ix, synResponse.getErrorMessages());
					allValid = false;
				} else {
					syn = synResponse.getEntity();
					syn.setSingleAllele(dbEntity);
					validatedSynonyms.add(syn);
				}
			}
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedSynonyms)) {
			return null;
		}

		return validatedSynonyms;
	}

	private List<AlleleSecondaryIdSlotAnnotation> validateAlleleSecondaryIds(Allele uiEntity, Allele dbEntity) {
		String field = "alleleSecondaryIds";

		List<AlleleSecondaryIdSlotAnnotation> validatedSecondaryIds = new ArrayList<AlleleSecondaryIdSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getAlleleSecondaryIds())) {
			for (int ix = 0; ix < uiEntity.getAlleleSecondaryIds().size(); ix++) {
				AlleleSecondaryIdSlotAnnotation sid = uiEntity.getAlleleSecondaryIds().get(ix);
				ObjectResponse<AlleleSecondaryIdSlotAnnotation> sidResponse = alleleSecondaryIdValidator.validateAlleleSecondaryIdSlotAnnotation(sid);
				if (sidResponse.getEntity() == null) {
					response.addErrorMessages(field, ix, sidResponse.getErrorMessages());
					allValid = false;
				} else {
					sid = sidResponse.getEntity();
					sid.setSingleAllele(dbEntity);
					validatedSecondaryIds.add(sid);
				}
			}
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedSecondaryIds)) {
			return null;
		}

		return validatedSecondaryIds;
	}

	private List<AlleleFunctionalImpactSlotAnnotation> validateAlleleFunctionalImpacts(Allele uiEntity, Allele dbEntity) {
		String field = "alleleFunctionalImpacts";

		List<AlleleFunctionalImpactSlotAnnotation> validatedFunctionalImpacts = new ArrayList<AlleleFunctionalImpactSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getAlleleFunctionalImpacts())) {
			for (int ix = 0; ix < uiEntity.getAlleleFunctionalImpacts().size(); ix++) {
				AlleleFunctionalImpactSlotAnnotation fi = uiEntity.getAlleleFunctionalImpacts().get(ix);
				ObjectResponse<AlleleFunctionalImpactSlotAnnotation> fiResponse = alleleFunctionalImpactValidator.validateAlleleFunctionalImpactSlotAnnotation(fi);
				if (fiResponse.getEntity() == null) {
					response.addErrorMessages(field, ix, fiResponse.getErrorMessages());
					allValid = false;
				} else {
					validatedFunctionalImpacts.add(fiResponse.getEntity());
				}
			}
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedFunctionalImpacts)) {
			return null;
		}

		return validatedFunctionalImpacts;
	}

	private List<AlleleGeneAssociation> validateAlleleGeneAssociations(Allele uiEntity, Allele dbEntity) {
		String field = "alleleGeneAssociations";

		List<AlleleGeneAssociation> uiAssociations = uiEntity.getAlleleGeneAssociations();

		List<AlleleGeneAssociation> validatedGeneAssociations = new ArrayList<AlleleGeneAssociation>();
		Boolean allValid = true;
		Boolean hasIsAlleleOfRelation = false;
		Set<String> validatedAssociationIdentities = new HashSet<>();
		if (CollectionUtils.isNotEmpty(uiAssociations)) {
			for (int ix = 0; ix < uiAssociations.size(); ix++) {
				AlleleGeneAssociation ga = uiAssociations.get(ix);
				ObjectResponse<AlleleGeneAssociation> gaResponse = alleleGeneAssociationValidator.validateAlleleGeneAssociation(ga);
				if (gaResponse.getEntity() == null) {
					allValid = false;
					response.addErrorMessages(field, ix, gaResponse.getErrorMessages());
				} else {
					ga = gaResponse.getEntity();
					if (ga.getRelation().getName().equals(VocabularyConstants.ALLELE_OF_VOCABULARY_TERM)) {
						if (hasIsAlleleOfRelation) {
							Map<String, String> duplicateRelationError = new HashMap<>();
							duplicateRelationError.put("relation", ValidationConstants.DUPLICATE_RELATION_PREFIX + VocabularyConstants.ALLELE_OF_VOCABULARY_TERM);
							response.addErrorMessages(field, ix, duplicateRelationError);
							allValid = false;
							continue;
						}
						hasIsAlleleOfRelation = true;
					}
					ga.setAlleleAssociationSubject(dbEntity);
					String associationIdentity = AssociationIdentityHelper.alleleGeneAssociationIdentity(ga);
					if (validatedAssociationIdentities.contains(associationIdentity)) {
						Map<String, String> duplicateError = new HashMap<>();
						duplicateError.put("relation", ValidationConstants.DUPLICATE_MESSAGE + " (" + associationIdentity + ")");
						response.addErrorMessages(field, ix, duplicateError);
						allValid = false;
						continue;
					}
					validatedAssociationIdentities.add(associationIdentity);
					validatedGeneAssociations.add(ga);
				}
			}
		}
		List<AlleleGeneAssociation> dbAssociations = dbEntity.getAlleleGeneAssociations();
		if (CollectionUtils.isNotEmpty(dbAssociations)) {
			Set<Long> idsToDelete = new HashSet<>(dbAssociations.stream().map(AlleleGeneAssociation::getId).collect(Collectors.toList()));
			if (uiAssociations != null) {
				Set<Long> uiIDs = new HashSet<>(uiAssociations.stream().map(AlleleGeneAssociation::getId).filter(id -> id != null).collect(Collectors.toList()));
				idsToDelete.removeAll(uiIDs);
				for (AlleleGeneAssociation ga : dbAssociations) {
					if (idsToDelete.contains(ga.getId())) {
						Gene gene = ga.getAlleleGeneAssociationObject();
						List<AlleleGeneAssociation> geneAssociations = gene.getAlleleGeneAssociations();
						geneAssociations.removeIf(geneAGA -> {
							return idsToDelete.contains(geneAGA.getId());
						});
					}
				}
			}
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedGeneAssociations)) {
			return null;
		}

		return validatedGeneAssociations;
	}
}
