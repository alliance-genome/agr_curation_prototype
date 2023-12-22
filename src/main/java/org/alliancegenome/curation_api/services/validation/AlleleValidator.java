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
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
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
import org.alliancegenome.curation_api.services.VocabularyTermService;
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

import io.quarkus.logging.Log;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleValidator extends GenomicEntityValidator {

	@Inject
	AlleleDAO alleleDAO;
	@Inject
	AlleleMutationTypeSlotAnnotationValidator alleleMutationTypeValidator;
	@Inject
	AlleleInheritanceModeSlotAnnotationValidator alleleInheritanceModeValidator;
	@Inject
	AlleleGermlineTransmissionStatusSlotAnnotationValidator alleleGermlineTransmissionStatusValidator;
	@Inject
	AlleleNomenclatureEventSlotAnnotationValidator alleleNomenclatureEventValidator;
	@Inject
	AlleleSymbolSlotAnnotationValidator alleleSymbolValidator;
	@Inject
	AlleleFullNameSlotAnnotationValidator alleleFullNameValidator;
	@Inject
	AlleleSynonymSlotAnnotationValidator alleleSynonymValidator;
	@Inject
	AlleleSecondaryIdSlotAnnotationValidator alleleSecondaryIdValidator;
	@Inject
	AlleleFunctionalImpactSlotAnnotationValidator alleleFunctionalImpactValidator;
	@Inject
	AlleleDatabaseStatusSlotAnnotationValidator alleleDatabaseStatusValidator;
	@Inject
	AlleleGeneAssociationValidator alleleGeneAssociationValidator;
	@Inject
	NoteValidator noteValidator;
	@Inject
	VocabularyTermService vocabularyTermService;
	@Inject
	ReferenceValidator referenceValidator;
	@Inject
	CrossReferenceDAO crossReferenceDAO;

	private String errorMessage;

	public Allele validateAlleleUpdate(Allele uiEntity, Boolean updateAllAssociations) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update Allele: [" + uiEntity.getCurie() + "]";

		String curie = validateCurie(uiEntity);
		if (curie == null) {
			throw new ApiErrorException(response);
		}

		Allele dbEntity = alleleDAO.find(curie);
		if (dbEntity == null) {
			addMessageResponse("curie", ValidationConstants.INVALID_MESSAGE);
			throw new ApiErrorException(response);
		}

		dbEntity = (Allele) validateAuditedObjectFields(uiEntity, dbEntity, false);

		return validateAllele(uiEntity, dbEntity, updateAllAssociations);
	}

	public Allele validateAlleleCreate(Allele uiEntity) {
		response = new ObjectResponse<>();
		errorMessage = "Could not create Allele: [" + uiEntity.getCurie() + "]";

		Allele dbEntity = new Allele();
		String curie = validateCurie(uiEntity);
		dbEntity.setCurie(curie);

		dbEntity = (Allele) validateAuditedObjectFields(uiEntity, dbEntity, true);

		return validateAllele(uiEntity, dbEntity, true);
	}

	public Allele validateAllele(Allele uiEntity, Allele dbEntity, Boolean updateAllAssociations) {

		NCBITaxonTerm taxon = validateTaxon(uiEntity, dbEntity);
		dbEntity.setTaxon(taxon);
		
		DataProvider dataProvider = validateDataProvider(uiEntity, dbEntity);
		dbEntity.setDataProvider(dataProvider);

		List<Reference> references = validateReferences(uiEntity, dbEntity);
		dbEntity.setReferences(references);

		VocabularyTerm inCollection = validateInCollection(uiEntity, dbEntity);
		dbEntity.setInCollection(inCollection);

		if (uiEntity.getIsExtinct() != null) {
			dbEntity.setIsExtinct(uiEntity.getIsExtinct());
		} else {
			dbEntity.setIsExtinct(null);
		}
		
		List<Note> relatedNotes = validateRelatedNotes(uiEntity, dbEntity);
		if (dbEntity.getRelatedNotes() != null)
			dbEntity.getRelatedNotes().clear();
		if (relatedNotes != null) {
			if (dbEntity.getRelatedNotes() == null)
				dbEntity.setRelatedNotes(new ArrayList<>());
			dbEntity.getRelatedNotes().addAll(relatedNotes);
		}

		List<Long> currentXrefIds;
		if (dbEntity.getCrossReferences() == null) {
			currentXrefIds = new ArrayList<>();
		} else {
			currentXrefIds = dbEntity.getCrossReferences().stream().map(CrossReference::getId).collect(Collectors.toList());
		}
		
		List<CrossReference> crossReferences = validateCrossReferences(uiEntity, dbEntity);
		dbEntity.setCrossReferences(crossReferences);
		List<Long> mergedIds = crossReferences == null ? new ArrayList<>() :
			crossReferences.stream().map(CrossReference::getId).collect(Collectors.toList());
		for (Long currentXrefId : currentXrefIds) {
			if (!mergedIds.contains(currentXrefId)) {
				crossReferenceDAO.remove(currentXrefId);
			}
		}
		
		AlleleSymbolSlotAnnotation symbol = validateAlleleSymbol(uiEntity, dbEntity);
		AlleleFullNameSlotAnnotation fullName = validateAlleleFullName(uiEntity, dbEntity);
		AlleleGermlineTransmissionStatusSlotAnnotation germlineTransmissionStatus = validateAlleleGermlineTransmissionStatus(uiEntity, dbEntity);
		AlleleDatabaseStatusSlotAnnotation databaseStatus = validateAlleleDatabaseStatus(uiEntity, dbEntity);
		List<AlleleNomenclatureEventSlotAnnotation> nomenclatureEvents = validateAlleleNomenclatureEvents(uiEntity, dbEntity);
		List<AlleleSynonymSlotAnnotation> synonyms = validateAlleleSynonyms(uiEntity, dbEntity);
		List<AlleleSecondaryIdSlotAnnotation> secondaryIds = validateAlleleSecondaryIds(uiEntity, dbEntity);
		List<AlleleMutationTypeSlotAnnotation> mutationTypes = validateAlleleMutationTypes(uiEntity, dbEntity);
		List<AlleleInheritanceModeSlotAnnotation> inheritanceModes = validateAlleleInheritanceModes(uiEntity, dbEntity);
		List<AlleleFunctionalImpactSlotAnnotation> functionalImpacts = validateAlleleFunctionalImpacts(uiEntity, dbEntity);

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
		
		if (symbol != null)
			symbol.setSingleAllele(dbEntity);
		dbEntity.setAlleleSymbol(symbol);
		
		if (fullName != null)
			fullName.setSingleAllele(dbEntity);
		dbEntity.setAlleleFullName(fullName);
		
		if (germlineTransmissionStatus != null)
			germlineTransmissionStatus.setSingleAllele(dbEntity);
		dbEntity.setAlleleGermlineTransmissionStatus(germlineTransmissionStatus);
		
		if (databaseStatus != null)
			databaseStatus.setSingleAllele(dbEntity);
		dbEntity.setAlleleDatabaseStatus(databaseStatus);
		
		if (dbEntity.getAlleleNomenclatureEvents() != null)
			dbEntity.getAlleleNomenclatureEvents().clear();
		if (nomenclatureEvents != null) {
			if (dbEntity.getAlleleNomenclatureEvents() == null)
				dbEntity.setAlleleNomenclatureEvents(new ArrayList<>());
			dbEntity.getAlleleNomenclatureEvents().addAll(nomenclatureEvents);
		}
		
		if (dbEntity.getAlleleSynonyms() != null)
			dbEntity.getAlleleSynonyms().clear();
		if (synonyms != null) {
			if (dbEntity.getAlleleSynonyms() == null)
				dbEntity.setAlleleSynonyms(new ArrayList<>());
			dbEntity.getAlleleSynonyms().addAll(synonyms);
		}
		
		if (dbEntity.getAlleleSecondaryIds() != null)
			dbEntity.getAlleleSecondaryIds().clear();
		if (secondaryIds != null) {
			if (dbEntity.getAlleleSecondaryIds() == null)
				dbEntity.setAlleleSecondaryIds(new ArrayList<>());
			dbEntity.getAlleleSecondaryIds().addAll(secondaryIds);
		}
		
		if (dbEntity.getAlleleMutationTypes() != null)
			dbEntity.getAlleleMutationTypes().clear();
		if (mutationTypes != null) {
			if (dbEntity.getAlleleMutationTypes() == null)
				dbEntity.setAlleleMutationTypes(new ArrayList<>());
			dbEntity.getAlleleMutationTypes().addAll(mutationTypes);
		}
		
		if (dbEntity.getAlleleInheritanceModes() != null)
			dbEntity.getAlleleInheritanceModes().clear();
		if (inheritanceModes != null) {
			if (dbEntity.getAlleleInheritanceModes() == null)
				dbEntity.setAlleleInheritanceModes(new ArrayList<>());
			dbEntity.getAlleleInheritanceModes().addAll(inheritanceModes);
		}
		
		if (dbEntity.getAlleleFunctionalImpacts() != null)
			dbEntity.getAlleleFunctionalImpacts().clear();
		if (functionalImpacts != null) {
			if (dbEntity.getAlleleFunctionalImpacts() == null)
				dbEntity.setAlleleFunctionalImpacts(new ArrayList<>());
			dbEntity.getAlleleFunctionalImpacts().addAll(functionalImpacts);
		}
		
		if (updateAllAssociations) { // This should contain logic for all fields only returned in AlleleDetailView
			if (dbEntity.getAlleleGeneAssociations() != null)
				dbEntity.getAlleleGeneAssociations().clear();
			if (geneAssociations != null) {
				if (dbEntity.getAlleleGeneAssociations() == null)
					dbEntity.setAlleleGeneAssociations(new ArrayList<>());
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
		if (CollectionUtils.isNotEmpty(dbEntity.getReferences()))
			previousReferenceCuries = dbEntity.getReferences().stream().map(Reference::getCurie).collect(Collectors.toList());
		
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
		
		if (CollectionUtils.isEmpty(validatedReferences))
			return null;

		return validatedReferences;
	}

	private VocabularyTerm validateInCollection(Allele uiEntity, Allele dbEntity) {
		String field = "inCollection";

		if (uiEntity.getInCollection() == null)
			return null;

		VocabularyTerm inCollection = vocabularyTermService.getTermInVocabulary(VocabularyConstants.ALLELE_COLLECTION_VOCABULARY, uiEntity.getInCollection().getName()).getEntity();
		if (inCollection == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (inCollection.getObsolete() && (dbEntity.getInCollection() == null || !inCollection.getName().equals(dbEntity.getInCollection().getName()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		return inCollection;
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
		
		if (CollectionUtils.isEmpty(validatedNotes))
			return null;

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
					mt = mtResponse.getEntity();
					mt.setSingleAllele(dbEntity);
					validatedMutationTypes.add(mt);
				}
			}
		}
		
		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedMutationTypes))
			return null;

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
					im = imResponse.getEntity();
					im.setSingleAllele(dbEntity);
					validatedInheritanceModes.add(im);
				}
			}
		}
		
		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedInheritanceModes))
			return null;

		return validatedInheritanceModes;
	}
	
	private AlleleGermlineTransmissionStatusSlotAnnotation validateAlleleGermlineTransmissionStatus(Allele uiEntity, Allele dbEntity) {
		if (uiEntity.getAlleleGermlineTransmissionStatus() == null)
			return null;
		
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
		if (uiEntity.getAlleleDatabaseStatus() == null)
			return null;
		
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
					ne = neResponse.getEntity();
					ne.setSingleAllele(dbEntity);
					validatedNomenclatureEvents.add(ne);
				}
			}
		}

		if (CollectionUtils.isEmpty(validatedNomenclatureEvents))
			return null;

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
		if (uiEntity.getAlleleFullName() == null)
			return null;

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

		if (CollectionUtils.isEmpty(validatedSynonyms))
			return null;

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

		if (CollectionUtils.isEmpty(validatedSecondaryIds))
			return null;

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
					fi = fiResponse.getEntity();
					fi.setSingleAllele(dbEntity);
					validatedFunctionalImpacts.add(fi);
				}
			}
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedFunctionalImpacts))
			return null;

		return validatedFunctionalImpacts;
	}

	private List<AlleleGeneAssociation> validateAlleleGeneAssociations(Allele uiEntity, Allele dbEntity) {
		String field = "alleleGeneAssociations";

		List<AlleleGeneAssociation> validatedGeneAssociations = new ArrayList<AlleleGeneAssociation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getAlleleGeneAssociations())) {
			for (int ix = 0; ix < uiEntity.getAlleleGeneAssociations().size(); ix++) {
				AlleleGeneAssociation ga = uiEntity.getAlleleGeneAssociations().get(ix);
				ObjectResponse<AlleleGeneAssociation> gaResponse = alleleGeneAssociationValidator.validateAlleleGeneAssociation(ga);
				if (gaResponse.getEntity() == null) {
					allValid = false;
					response.addErrorMessages(field, ix, gaResponse.getErrorMessages());
				} else {
					ga = gaResponse.getEntity();
					ga.setSubject(dbEntity);
					validatedGeneAssociations.add(ga);
				}
			}
		}
		
		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedGeneAssociations))
			return null;

		return validatedGeneAssociations;
	}
}
