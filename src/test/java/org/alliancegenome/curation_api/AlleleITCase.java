package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.time.OffsetDateTime;
import java.util.List;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.entities.ontology.MPTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.PhenotypeTerm;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
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
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;

@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("302 - AlleleITCase")
@Order(302)
public class AlleleITCase extends BaseITCase {

	private static final String ALLELE = "Allele:0001";
	
	private Vocabulary inheritanceModeVocabulary;
	private Vocabulary germlineTransmissionStatusVocabulary;
	private Vocabulary inCollectionVocabulary;
	private Vocabulary nameTypeVocabulary;
	private Vocabulary synonymScopeVocabulary;
	private Vocabulary functionalImpactVocabulary;
	private Vocabulary noteTypeVocabulary;
	private Vocabulary databaseStatusVocabulary;
	private Vocabulary nomenclatureEventVocabulary;
	private VocabularyTerm dominantInheritanceMode;
	private VocabularyTerm recessiveInheritanceMode;
	private VocabularyTerm obsoleteInheritanceModeTerm;
	private VocabularyTerm hypermorphicFunctionalImpact;
	private VocabularyTerm neomorphicFunctionalImpact;
	private VocabularyTerm obsoleteFunctionalImpact;
	private VocabularyTerm mmpInCollection;
	private VocabularyTerm wgsInCollection;
	private VocabularyTerm obsoleteCollection;
	private VocabularyTerm symbolNameType;
	private VocabularyTerm fullNameType;
	private VocabularyTerm obsoleteNameType;
	private VocabularyTerm obsoleteSymbolNameType;
	private VocabularyTerm obsoleteFullNameType;
	private VocabularyTerm systematicNameType;
	private VocabularyTerm exactSynonymScope;
	private VocabularyTerm broadSynonymScope;
	private VocabularyTerm obsoleteSynonymScope;
	private VocabularyTerm cellLineGTS;
	private VocabularyTerm germlineGTS;
	private VocabularyTerm obsoleteGTS;
	private VocabularyTerm noteType;
	private VocabularyTerm noteType2;
	private VocabularyTerm obsoleteNoteType;
	private VocabularyTerm approvedDatabaseStatus;
	private VocabularyTerm reservedDatabaseStatus;
	private VocabularyTerm obsoleteDatabaseStatus;
	private VocabularyTerm dataMergedEvent;
	private VocabularyTerm symbolUpdatedEvent;
	private VocabularyTerm obsoleteNomenclatureEvent;
	private Reference reference;
	private Reference reference2;
	private Reference obsoleteReference;
	private NCBITaxonTerm taxon;
	private NCBITaxonTerm taxon2;
	private NCBITaxonTerm obsoleteTaxon;
	private Person person;
	private OffsetDateTime datetime;
	private OffsetDateTime datetime2;
	private SOTerm soTerm;
	private SOTerm soTerm2;
	private SOTerm obsoleteSoTerm;
	private MPTerm mpTerm;
	private MPTerm mpTerm2;
	private MPTerm obsoleteMpTerm;
	private Note relatedNote;
	private AlleleMutationTypeSlotAnnotation alleleMutationType;
	private AlleleInheritanceModeSlotAnnotation alleleInheritanceMode;
	private AlleleGermlineTransmissionStatusSlotAnnotation alleleGermlineTransmissionStatus;
	private AlleleSymbolSlotAnnotation alleleSymbol;
	private AlleleFullNameSlotAnnotation alleleFullName;
	private AlleleSynonymSlotAnnotation alleleSynonym;
	private AlleleSecondaryIdSlotAnnotation alleleSecondaryId;
	private AlleleFunctionalImpactSlotAnnotation alleleFunctionalImpact;
	private AlleleDatabaseStatusSlotAnnotation alleleDatabaseStatus;
	private AlleleNomenclatureEventSlotAnnotation alleleNomenclatureEvent;
	private DataProvider dataProvider;
	private DataProvider dataProvider2;
	private DataProvider obsoleteDataProvider;
	private Organization nonPersistedOrganization;
	private Gene gene;
	private Gene gene2;
	private VocabularyTerm geneAssociationRelation;
	private VocabularyTerm geneAssociationRelation2;
	
	
	private void loadRequiredEntities() {
		inheritanceModeVocabulary = getVocabulary(VocabularyConstants.ALLELE_INHERITANCE_MODE_VOCABULARY);
		germlineTransmissionStatusVocabulary = getVocabulary(VocabularyConstants.GERMLINE_TRANSMISSION_STATUS_VOCABULARY);
		inCollectionVocabulary = getVocabulary(VocabularyConstants.ALLELE_COLLECTION_VOCABULARY);
		nameTypeVocabulary = getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY);
		synonymScopeVocabulary = getVocabulary(VocabularyConstants.SYNONYM_SCOPE_VOCABULARY);
		functionalImpactVocabulary = getVocabulary(VocabularyConstants.ALLELE_FUNCTIONAL_IMPACT_VOCABULARY);
		noteTypeVocabulary = getVocabulary(VocabularyConstants.NOTE_TYPE_VOCABULARY);
		databaseStatusVocabulary = getVocabulary(VocabularyConstants.ALLELE_DATABASE_STATUS_VOCABULARY);
		nomenclatureEventVocabulary = getVocabulary(VocabularyConstants.ALLELE_NOMENCLATURE_EVENT_VOCABULARY);
		dominantInheritanceMode = getVocabularyTerm(inheritanceModeVocabulary, "dominant");
		recessiveInheritanceMode = getVocabularyTerm(inheritanceModeVocabulary, "recessive");
		obsoleteInheritanceModeTerm = createVocabularyTerm(inheritanceModeVocabulary, "obsolete_mode", true);
		cellLineGTS = getVocabularyTerm(germlineTransmissionStatusVocabulary, "cell_line");
		germlineGTS = getVocabularyTerm(germlineTransmissionStatusVocabulary, "germline");
		obsoleteGTS = createVocabularyTerm(germlineTransmissionStatusVocabulary, "obsolete_status", true);
		approvedDatabaseStatus = getVocabularyTerm(databaseStatusVocabulary, "approved");
		reservedDatabaseStatus = getVocabularyTerm(databaseStatusVocabulary, "reserved");
		obsoleteDatabaseStatus = createVocabularyTerm(databaseStatusVocabulary, "obsolete_status", true);
		hypermorphicFunctionalImpact = getVocabularyTerm(functionalImpactVocabulary, "hypermorphic");
		neomorphicFunctionalImpact = getVocabularyTerm(functionalImpactVocabulary, "neomorphic");
		obsoleteFunctionalImpact = createVocabularyTerm(functionalImpactVocabulary, "obsolete_impact", true);
		dataMergedEvent = getVocabularyTerm(nomenclatureEventVocabulary, "data_merged");
		symbolUpdatedEvent = getVocabularyTerm(nomenclatureEventVocabulary, "symbol_updated");
		obsoleteNomenclatureEvent = createVocabularyTerm(nomenclatureEventVocabulary, "obsolete_event", true);
		mmpInCollection = getVocabularyTerm(inCollectionVocabulary, "Million_mutations_project");
		wgsInCollection = getVocabularyTerm(inCollectionVocabulary, "WGS_Hobert");
		obsoleteCollection = createVocabularyTerm(inCollectionVocabulary, "obsolete_collection", true);
		symbolNameType = getVocabularyTerm(nameTypeVocabulary, "nomenclature_symbol");
		fullNameType = getVocabularyTerm(nameTypeVocabulary, "full_name");
		systematicNameType = getVocabularyTerm(nameTypeVocabulary, "systematic_name");
		obsoleteNameType = getVocabularyTerm(nameTypeVocabulary, "obsolete_name");
		obsoleteSymbolNameType = getVocabularyTerm(nameTypeVocabulary, "obsolete_symbol_name");
		obsoleteFullNameType = getVocabularyTerm(nameTypeVocabulary, "obsolete_full_name");
		exactSynonymScope = getVocabularyTerm(synonymScopeVocabulary, "exact");
		broadSynonymScope = getVocabularyTerm(synonymScopeVocabulary, "broad");
		obsoleteSynonymScope = getVocabularyTerm(synonymScopeVocabulary, "obsolete");
		reference = createReference("AGRKB:000000003", false);
		reference2 = createReference("AGRKB:000000005", false);
		obsoleteReference = createReference("AGRKB:000010000", true);
		taxon = getNCBITaxonTerm("NCBITaxon:10090");
		taxon2 = getNCBITaxonTerm("NCBITaxon:9606");
		obsoleteTaxon = getNCBITaxonTerm("NCBITaxon:0000");
		person = createPerson("TEST:AllelePerson0001");
		datetime = OffsetDateTime.parse("2022-03-09T22:10:12+00:00");
		datetime2 = OffsetDateTime.parse("2022-04-10T22:10:11+00:00");
		soTerm = getSoTerm("SO:00001");
		soTerm2 = getSoTerm("SO:00002");
		obsoleteSoTerm = createSoTerm("SO:00000", "obsoleteTest", true);
		mpTerm = getMpTerm("MP:00001");
		mpTerm2 = getMpTerm("MP:00002");
		obsoleteMpTerm = createMpTerm("MP:00000", true);
		noteType = getVocabularyTerm(noteTypeVocabulary, "comment");
		noteType2 = getVocabularyTerm(noteTypeVocabulary, "user_submitted_note");
		obsoleteNoteType = addVocabularyTermToSet(VocabularyConstants.ALLELE_NOTE_TYPES_VOCABULARY_TERM_SET, "obsolete_type", noteTypeVocabulary, true);
		relatedNote = createNote(noteType, "Test text", false, reference);
		alleleMutationType = createAlleleMutationTypeSlotAnnotation(List.of(reference), List.of(soTerm));
		alleleGermlineTransmissionStatus = createAlleleGermlineTransmissionStatusSlotAnnotation(List.of(reference), cellLineGTS);
		alleleDatabaseStatus = createAlleleDatabaseStatusSlotAnnotation(List.of(reference), approvedDatabaseStatus);
		alleleInheritanceMode = createAlleleInheritanceModeSlotAnnotation(List.of(reference), dominantInheritanceMode, mpTerm, "Phenotype statement");
		alleleSymbol = createAlleleSymbolSlotAnnotation(List.of(reference), "Test symbol", symbolNameType, exactSynonymScope, "https://test.org");
		alleleFullName = createAlleleFullNameSlotAnnotation(List.of(reference), "Test name", fullNameType, exactSynonymScope, "https://test.org");
		alleleSynonym = createAlleleSynonymSlotAnnotation(List.of(reference), "Test synonym", systematicNameType, exactSynonymScope, "https://test.org");
		alleleSecondaryId = createAlleleSecondaryIdSlotAnnotation(List.of(reference), "TEST:Secondary");
		alleleFunctionalImpact = createAlleleFunctionalImpactSlotAnnotation(List.of(reference), List.of(hypermorphicFunctionalImpact), mpTerm, "Phenotype statement");
		alleleNomenclatureEvent = createAlleleNomenclatureEventSlotAnnotation(List.of(reference), dataMergedEvent);
		dataProvider = createDataProvider("TEST", false);
		dataProvider2 = createDataProvider("TEST2", false);
		obsoleteDataProvider = createDataProvider("ODP", true);
		nonPersistedOrganization = new Organization();
		nonPersistedOrganization.setAbbreviation("INV");
		VocabularyTerm symbolNameType = getVocabularyTerm(nameTypeVocabulary, "nomenclature_symbol");
		gene = createGene("TEST:AssociatedGene1", "NCBITaxon:6239", symbolNameType, false);
		gene2 = createGene("TEST:AssociatedGene2", "NCBITaxon:6239", symbolNameType, false);
		Vocabulary relationVocabulary = getVocabulary(VocabularyConstants.ALLELE_RELATION_VOCABULARY);
		geneAssociationRelation = getVocabularyTerm(relationVocabulary, "is_allele_of");
		geneAssociationRelation2 = getVocabularyTerm(relationVocabulary, "duplication");
		
	}
	
	@Test
	@Order(1)
	public void createValidAllele() {
		loadRequiredEntities();
		
		Allele allele = new Allele();
		allele.setModEntityId(ALLELE);
		allele.setTaxon(taxon);
		allele.setInCollection(mmpInCollection);
		allele.setReferences(List.of(reference));
		allele.setIsExtinct(false);
		allele.setDateCreated(datetime);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleGermlineTransmissionStatus(alleleGermlineTransmissionStatus);
		allele.setAlleleDatabaseStatus(alleleDatabaseStatus);
		allele.setAlleleInheritanceModes(List.of(alleleInheritanceMode));
		allele.setAlleleSymbol(alleleSymbol);
		allele.setAlleleFullName(alleleFullName);
		allele.setAlleleSynonyms(List.of(alleleSynonym));
		allele.setAlleleSecondaryIds(List.of(alleleSecondaryId));
		allele.setAlleleFunctionalImpacts(List.of(alleleFunctionalImpact));
		allele.setAlleleNomenclatureEvents(List.of(alleleNomenclatureEvent));
		allele.setDataProvider(dataProvider);
		allele.setRelatedNotes(List.of(relatedNote));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get("/api/allele/" + ALLELE).
			then().
			statusCode(200).
			body("entity.modEntityId", is(ALLELE)).
			body("entity.taxon.curie", is(taxon.getCurie())).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.inCollection.name", is(mmpInCollection.getName())).
			body("entity.isExtinct", is(false)).
			body("entity.references[0].curie", is(reference.getCurie())).
			body("entity.dateCreated", is(datetime.toString())).
			body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.relatedNotes", hasSize(1)).
			body("entity.relatedNotes[0].noteType.name", is(relatedNote.getNoteType().getName())).
			body("entity.relatedNotes[0].freeText", is(relatedNote.getFreeText())).
			body("entity.relatedNotes[0].internal", is(false)).
			body("entity.relatedNotes[0].references[0].curie", is(reference.getCurie())).
			body("entity.alleleMutationTypes[0].evidence[0].curie", is(reference.getCurie())).
			body("entity.alleleMutationTypes[0].mutationTypes[0].curie", is(soTerm.getCurie())).
			body("entity.alleleInheritanceModes[0].evidence[0].curie", is(reference.getCurie())).
			body("entity.alleleInheritanceModes[0].inheritanceMode.name", is(dominantInheritanceMode.getName())).
			body("entity.alleleInheritanceModes[0].phenotypeTerm.curie", is(mpTerm.getCurie())).
			body("entity.alleleInheritanceModes[0].phenotypeStatement", is("Phenotype statement")).
			body("entity.alleleGermlineTransmissionStatus.evidence[0].curie", is(reference.getCurie())).
			body("entity.alleleGermlineTransmissionStatus.germlineTransmissionStatus.name", is(cellLineGTS.getName())).
			body("entity.alleleDatabaseStatus.evidence[0].curie", is(reference.getCurie())).
			body("entity.alleleDatabaseStatus.databaseStatus.name", is(approvedDatabaseStatus.getName())).
			body("entity.alleleSymbol.displayText", is(alleleSymbol.getDisplayText())).
			body("entity.alleleSymbol.formatText", is(alleleSymbol.getFormatText())).
			body("entity.alleleSymbol.nameType.name", is(alleleSymbol.getNameType().getName())).
			body("entity.alleleSymbol.synonymScope.name", is(alleleSymbol.getSynonymScope().getName())).
			body("entity.alleleSymbol.synonymUrl", is(alleleSymbol.getSynonymUrl())).
			body("entity.alleleSymbol.evidence[0].curie", is(alleleSymbol.getEvidence().get(0).getCurie())).
			body("entity.alleleFullName.displayText", is(alleleFullName.getDisplayText())).
			body("entity.alleleFullName.formatText", is(alleleFullName.getFormatText())).
			body("entity.alleleFullName.nameType.name", is(alleleFullName.getNameType().getName())).
			body("entity.alleleFullName.synonymScope.name", is(alleleFullName.getSynonymScope().getName())).
			body("entity.alleleFullName.synonymUrl", is(alleleFullName.getSynonymUrl())).
			body("entity.alleleFullName.evidence[0].curie", is(alleleFullName.getEvidence().get(0).getCurie())).
			body("entity.alleleSynonyms[0].displayText", is(alleleSynonym.getDisplayText())).
			body("entity.alleleSynonyms[0].formatText", is(alleleSynonym.getFormatText())).
			body("entity.alleleSynonyms[0].nameType.name", is(alleleSynonym.getNameType().getName())).
			body("entity.alleleSynonyms[0].synonymScope.name", is(alleleSynonym.getSynonymScope().getName())).
			body("entity.alleleSynonyms[0].synonymUrl", is(alleleSynonym.getSynonymUrl())).
			body("entity.alleleSynonyms[0].evidence[0].curie", is(alleleSynonym.getEvidence().get(0).getCurie())).
			body("entity.alleleSecondaryIds[0].secondaryId", is(alleleSecondaryId.getSecondaryId())).
			body("entity.alleleSecondaryIds[0].evidence[0].curie", is(alleleSecondaryId.getEvidence().get(0).getCurie())).
			body("entity.alleleFunctionalImpacts[0].evidence[0].curie", is(reference.getCurie())).
			body("entity.alleleFunctionalImpacts[0].functionalImpacts[0].name", is(hypermorphicFunctionalImpact.getName())).
			body("entity.alleleFunctionalImpacts[0].phenotypeTerm.curie", is(mpTerm.getCurie())).
			body("entity.alleleFunctionalImpacts[0].phenotypeStatement", is("Phenotype statement")).
			body("entity.alleleNomenclatureEvents[0].evidence[0].curie", is(reference.getCurie())).
			body("entity.alleleNomenclatureEvents[0].nomenclatureEvent.name", is(dataMergedEvent.getName())).
			body("entity.dataProvider.sourceOrganization.abbreviation", is(dataProvider.getSourceOrganization().getAbbreviation()));
	}

	@Test
	@Order(2)
	public void editAllele() {
		Allele allele = getAllele(ALLELE);
		allele.setCreatedBy(person);
		allele.setModEntityId(ALLELE);
		allele.setTaxon(taxon2);
		allele.setInCollection(wgsInCollection);
		allele.setReferences(List.of(reference2));
		allele.setIsExtinct(true);
		allele.setInternal(true);
		allele.setObsolete(true);
		allele.setDateCreated(datetime2);
		allele.setDataProvider(dataProvider2);
		
		Note editedNote = allele.getRelatedNotes().get(0);
		editedNote.setNoteType(noteType2);
		editedNote.setFreeText("Edited text");
		editedNote.setInternal(true);
		editedNote.setReferences(List.of(reference2));
		allele.setRelatedNotes(List.of(editedNote));
		
		AlleleMutationTypeSlotAnnotation editedMutationType = allele.getAlleleMutationTypes().get(0);
		editedMutationType.setMutationTypes(List.of(soTerm2));
		editedMutationType.setEvidence(List.of(reference2));
		allele.setAlleleMutationTypes(List.of(editedMutationType));
		
		AlleleInheritanceModeSlotAnnotation editedInheritanceMode = allele.getAlleleInheritanceModes().get(0);
		editedInheritanceMode.setInheritanceMode(recessiveInheritanceMode);
		editedInheritanceMode.setPhenotypeTerm(mpTerm2);
		editedInheritanceMode.setPhenotypeStatement("Edited phenotype statment");
		editedInheritanceMode.setEvidence(List.of(reference2));
		allele.setAlleleInheritanceModes(List.of(editedInheritanceMode));
		
		AlleleSymbolSlotAnnotation editedSymbol = allele.getAlleleSymbol();
		editedSymbol.setDisplayText("EditedDisplay");
		editedSymbol.setFormatText("EditedFormat");
		editedSymbol.setNameType(systematicNameType);
		editedSymbol.setSynonymScope(broadSynonymScope);
		editedSymbol.setSynonymUrl("https://test2.org");
		editedSymbol.setEvidence(List.of(reference2));
		allele.setAlleleSymbol(editedSymbol);
		
		AlleleFullNameSlotAnnotation editedFullName = allele.getAlleleFullName();
		editedFullName.setDisplayText("EditedDisplay");
		editedFullName.setFormatText("EditedFormat");
		editedFullName.setSynonymScope(broadSynonymScope);
		editedFullName.setSynonymUrl("https://test2.org");
		editedFullName.setEvidence(List.of(reference2));
		allele.setAlleleFullName(editedFullName);
		
		AlleleSynonymSlotAnnotation editedSynonym = allele.getAlleleSynonyms().get(0);
		editedSynonym.setDisplayText("EditedDisplay");
		editedSynonym.setFormatText("EditedFormat");
		editedSynonym.setNameType(fullNameType);
		editedSynonym.setSynonymScope(broadSynonymScope);
		editedSynonym.setSynonymUrl("https://test2.org");
		editedSynonym.setEvidence(List.of(reference2));
		allele.setAlleleSynonyms(List.of(editedSynonym));
		
		AlleleSecondaryIdSlotAnnotation editedSecondaryId = allele.getAlleleSecondaryIds().get(0);
		editedSecondaryId.setSecondaryId("TEST:Secondary2");
		editedSecondaryId.setEvidence(List.of(reference2));
		allele.setAlleleSecondaryIds(List.of(editedSecondaryId));
		
		AlleleGermlineTransmissionStatusSlotAnnotation editedGTS = allele.getAlleleGermlineTransmissionStatus();
		editedGTS.setGermlineTransmissionStatus(germlineGTS);
		editedGTS.setEvidence(List.of(reference2));
		allele.setAlleleGermlineTransmissionStatus(editedGTS);
		
		AlleleDatabaseStatusSlotAnnotation editedDatabaseStatus = allele.getAlleleDatabaseStatus();
		editedDatabaseStatus.setDatabaseStatus(reservedDatabaseStatus);
		editedDatabaseStatus.setEvidence(List.of(reference2));
		allele.setAlleleDatabaseStatus(editedDatabaseStatus);
		
		AlleleFunctionalImpactSlotAnnotation editedFunctionalImpact = allele.getAlleleFunctionalImpacts().get(0);
		editedFunctionalImpact.setFunctionalImpacts(List.of(neomorphicFunctionalImpact));
		editedFunctionalImpact.setPhenotypeTerm(mpTerm2);
		editedFunctionalImpact.setPhenotypeStatement("Edited phenotype statement");
		editedFunctionalImpact.setEvidence(List.of(reference2));
		allele.setAlleleFunctionalImpacts(List.of(editedFunctionalImpact));
		
		AlleleNomenclatureEventSlotAnnotation editedNomenclatureEvent = allele.getAlleleNomenclatureEvents().get(0);
		editedNomenclatureEvent.setNomenclatureEvent(symbolUpdatedEvent);
		editedNomenclatureEvent.setEvidence(List.of(reference2));
		allele.setAlleleNomenclatureEvents(List.of(editedNomenclatureEvent));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(200);

		RestAssured.given().
			when().
			get("/api/allele/" + ALLELE).
			then().
			statusCode(200).
			body("entity.modEntityId", is(ALLELE)).
			body("entity.inCollection.name", is(wgsInCollection.getName())).
			body("entity.references[0].curie", is(reference2.getCurie())).
			body("entity.dateCreated", is(datetime2.toString())).
			body("entity.createdBy.uniqueId", is(person.getUniqueId())).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.taxon.curie", is(taxon2.getCurie())).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.isExtinct", is(true)).
			body("entity.createdBy.uniqueId", is(person.getUniqueId())).
			body("entity.relatedNotes", hasSize(1)).
			body("entity.relatedNotes[0].noteType.name", is(editedNote.getNoteType().getName())).
			body("entity.relatedNotes[0].freeText", is(editedNote.getFreeText())).
			body("entity.relatedNotes[0].internal", is(true)).
			body("entity.relatedNotes[0].references[0].curie", is(reference2.getCurie())).
			body("entity.alleleMutationTypes[0].mutationTypes[0].curie", is(editedMutationType.getMutationTypes().get(0).getCurie())).
			body("entity.alleleMutationTypes[0].evidence[0].curie", is(editedMutationType.getEvidence().get(0).getCurie())).
			body("entity.alleleGermlineTransmissionStatus.evidence[0].curie", is(reference2.getCurie())).
			body("entity.alleleGermlineTransmissionStatus.germlineTransmissionStatus.name", is(germlineGTS.getName())).
			body("entity.alleleDatabaseStatus.evidence[0].curie", is(reference2.getCurie())).
			body("entity.alleleDatabaseStatus.databaseStatus.name", is(reservedDatabaseStatus.getName())).
			body("entity.alleleInheritanceModes[0].evidence[0].curie", is(editedInheritanceMode.getEvidence().get(0).getCurie())).
			body("entity.alleleInheritanceModes[0].inheritanceMode.name", is(editedInheritanceMode.getInheritanceMode().getName())).
			body("entity.alleleInheritanceModes[0].phenotypeTerm.curie", is(editedInheritanceMode.getPhenotypeTerm().getCurie())).
			body("entity.alleleInheritanceModes[0].phenotypeStatement", is(editedInheritanceMode.getPhenotypeStatement())).
			body("entity.alleleSymbol.displayText", is(editedSymbol.getDisplayText())).
			body("entity.alleleSymbol.formatText", is(editedSymbol.getFormatText())).
			body("entity.alleleSymbol.nameType.name", is(editedSymbol.getNameType().getName())).
			body("entity.alleleSymbol.synonymScope.name", is(editedSymbol.getSynonymScope().getName())).
			body("entity.alleleSymbol.synonymUrl", is(editedSymbol.getSynonymUrl())).
			body("entity.alleleSymbol.evidence[0].curie", is(editedSymbol.getEvidence().get(0).getCurie())).
			body("entity.alleleFullName.displayText", is(editedFullName.getDisplayText())).
			body("entity.alleleFullName.formatText", is(editedFullName.getFormatText())).
			body("entity.alleleFullName.nameType.name", is(editedFullName.getNameType().getName())).
			body("entity.alleleFullName.synonymScope.name", is(editedFullName.getSynonymScope().getName())).
			body("entity.alleleFullName.synonymUrl", is(editedFullName.getSynonymUrl())).
			body("entity.alleleFullName.evidence[0].curie", is(editedFullName.getEvidence().get(0).getCurie())).
			body("entity.alleleSynonyms[0].displayText", is(editedSynonym.getDisplayText())).
			body("entity.alleleSynonyms[0].formatText", is(editedSynonym.getFormatText())).
			body("entity.alleleSynonyms[0].nameType.name", is(editedSynonym.getNameType().getName())).
			body("entity.alleleSynonyms[0].synonymScope.name", is(editedSynonym.getSynonymScope().getName())).
			body("entity.alleleSynonyms[0].synonymUrl", is(editedSynonym.getSynonymUrl())).
			body("entity.alleleSynonyms[0].evidence[0].curie", is(editedSynonym.getEvidence().get(0).getCurie())).
			body("entity.alleleSecondaryIds[0].secondaryId", is("TEST:Secondary2")).
			body("entity.alleleSecondaryIds[0].evidence[0].curie", is(editedSecondaryId.getEvidence().get(0).getCurie())).
			body("entity.alleleFunctionalImpacts[0].evidence[0].curie", is(reference2.getCurie())).
			body("entity.alleleFunctionalImpacts[0].functionalImpacts[0].name", is(neomorphicFunctionalImpact.getName())).
			body("entity.alleleFunctionalImpacts[0].phenotypeTerm.curie", is(mpTerm2.getCurie())).
			body("entity.alleleFunctionalImpacts[0].phenotypeStatement", is("Edited phenotype statement")).
			body("entity.alleleNomenclatureEvents[0].evidence[0].curie", is(reference2.getCurie())).
			body("entity.alleleNomenclatureEvents[0].nomenclatureEvent.name", is(symbolUpdatedEvent.getName())).
			body("entity.dataProvider.sourceOrganization.abbreviation", is(dataProvider2.getSourceOrganization().getAbbreviation()));
	}
	
	@Test
	@Order(3)
	public void createAlleleWithMissingRequiredFieldsLevel1() {
		Allele allele = new Allele();
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(3))).
			body("errorMessages.modInternalId", is(ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + "modEntityId")).
			body("errorMessages.taxon", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.alleleSymbol", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(4)
	public void editAlleleWithMissingModEntityId() {
		Allele allele = getAllele(ALLELE);
		allele.setModEntityId(null);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.modInternalId", is(ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + "modEntityId"));
	}
	
	@Test
	@Order(5)
	public void editAlleleWithMissingRequiredFieldsLevel1() {
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(null);
		allele.setAlleleSymbol(null);
		allele.setDataProvider(null);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(3))).
			body("errorMessages.taxon", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.alleleSymbol", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(6)
	public void createAlleleWithEmptyRequiredFields() {
		Allele allele = new Allele();
		allele.setModEntityId("");
		allele.setTaxon(taxon);
		allele.setInCollection(mmpInCollection);
		allele.setReferences(List.of(reference));
		allele.setIsExtinct(false);
		allele.setDateCreated(datetime);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		allele.setAlleleFullName(alleleFullName);
		allele.setAlleleSynonyms(List.of(alleleSynonym));
		allele.setAlleleSecondaryIds(List.of(alleleSecondaryId));
		allele.setAlleleInheritanceModes(List.of(alleleInheritanceMode));
		allele.setAlleleGermlineTransmissionStatus(alleleGermlineTransmissionStatus);
		allele.setAlleleFunctionalImpacts(List.of(alleleFunctionalImpact));
		allele.setAlleleDatabaseStatus(alleleDatabaseStatus);
		allele.setAlleleNomenclatureEvents(List.of(alleleNomenclatureEvent));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.modInternalId", is(ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + "modEntityId"));
	}
	
	@Test
	@Order(7)
	public void editAlleleWithEmptyModEntityId() {
		Allele allele = getAllele(ALLELE);
		allele.setModEntityId("");
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.modInternalId", is(ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + "modEntityId"));
	}
	
	@Test
	@Order(8)
	public void createAlleleWithMissingRequiredFieldsLevel2() {
		Allele allele = new Allele();
		allele.setModEntityId("Allele:0008");
		allele.setTaxon(taxon);
		
		AlleleMutationTypeSlotAnnotation invalidMutationType = new AlleleMutationTypeSlotAnnotation();
		AlleleInheritanceModeSlotAnnotation invalidInheritanceMode = new AlleleInheritanceModeSlotAnnotation();
		AlleleSymbolSlotAnnotation invalidSymbol = new AlleleSymbolSlotAnnotation();
		AlleleFullNameSlotAnnotation invalidFullName = new AlleleFullNameSlotAnnotation();
		AlleleSynonymSlotAnnotation invalidSynonym = new AlleleSynonymSlotAnnotation();
		AlleleSecondaryIdSlotAnnotation invalidSecondaryId = new AlleleSecondaryIdSlotAnnotation();
		AlleleGermlineTransmissionStatusSlotAnnotation invalidGTS = new AlleleGermlineTransmissionStatusSlotAnnotation();
		AlleleFunctionalImpactSlotAnnotation invalidFunctionalImpact = new AlleleFunctionalImpactSlotAnnotation();
		AlleleDatabaseStatusSlotAnnotation invalidDatabaseStatus = new AlleleDatabaseStatusSlotAnnotation();
		AlleleNomenclatureEventSlotAnnotation invalidNomenclatureEvent = new AlleleNomenclatureEventSlotAnnotation();
		Note invalidNote = new Note();
		
		allele.setAlleleMutationTypes(List.of(invalidMutationType));
		allele.setAlleleInheritanceModes(List.of(invalidInheritanceMode));
		allele.setAlleleGermlineTransmissionStatus(invalidGTS);
		allele.setAlleleDatabaseStatus(invalidDatabaseStatus);
		allele.setAlleleSymbol(invalidSymbol);
		allele.setAlleleFullName(invalidFullName);
		allele.setAlleleSynonyms(List.of(invalidSynonym));
		allele.setAlleleSecondaryIds(List.of(invalidSecondaryId));
		allele.setAlleleFunctionalImpacts(List.of(invalidFunctionalImpact));
		allele.setAlleleNomenclatureEvents(List.of(invalidNomenclatureEvent));
		allele.setRelatedNotes(List.of(invalidNote));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(11))).
			body("errorMessages.alleleMutationTypes", is("mutationTypes - " + ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.alleleInheritanceModes", is("inheritanceMode - " + ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.alleleSymbol", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE,
					"nameType - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.alleleFullName", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE,
					"nameType - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.alleleSynonyms", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE,
					"nameType - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.alleleSecondaryIds", is("secondaryId - " + ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.alleleFunctionalImpacts", is("functionalImpacts - " + ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.alleleGermlineTransmissionStatus", is("germlineTransmissionStatus - " + ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.alleleDatabaseStatus", is("databaseStatus - " + ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.alleleNomenclatureEvents", is("nomenclatureEvent - " + ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"freeText - " + ValidationConstants.REQUIRED_MESSAGE,
					"noteType - " + ValidationConstants.REQUIRED_MESSAGE))));
	}
	
	@Test
	@Order(9)
	public void editAlleleWithMissingRequiredFieldsLevel2() {
		Allele allele = getAllele(ALLELE);
		
		AlleleMutationTypeSlotAnnotation invalidMutationType = allele.getAlleleMutationTypes().get(0);
		invalidMutationType.setMutationTypes(null);
		AlleleInheritanceModeSlotAnnotation invalidInheritanceMode = allele.getAlleleInheritanceModes().get(0);
		invalidInheritanceMode.setInheritanceMode(null);
		AlleleSymbolSlotAnnotation invalidSymbol = allele.getAlleleSymbol();
		invalidSymbol.setDisplayText(null);
		invalidSymbol.setFormatText(null);
		invalidSymbol.setNameType(null);
		AlleleFullNameSlotAnnotation invalidFullName = allele.getAlleleFullName();
		invalidFullName.setDisplayText(null);
		invalidFullName.setFormatText(null);
		invalidFullName.setNameType(null);
		AlleleSynonymSlotAnnotation invalidSynonym = allele.getAlleleSynonyms().get(0);
		invalidSynonym.setDisplayText(null);
		invalidSynonym.setFormatText(null);
		invalidSynonym.setNameType(null);
		AlleleSecondaryIdSlotAnnotation invalidSecondaryId = allele.getAlleleSecondaryIds().get(0);
		invalidSecondaryId.setSecondaryId(null);
		AlleleFunctionalImpactSlotAnnotation invalidFunctionalImpact = allele.getAlleleFunctionalImpacts().get(0);
		invalidFunctionalImpact.setFunctionalImpacts(null);
		AlleleGermlineTransmissionStatusSlotAnnotation invalidGTS = allele.getAlleleGermlineTransmissionStatus();
		invalidGTS.setGermlineTransmissionStatus(null);
		AlleleDatabaseStatusSlotAnnotation invalidDatabaseStatus = allele.getAlleleDatabaseStatus();
		invalidDatabaseStatus.setDatabaseStatus(null);
		AlleleNomenclatureEventSlotAnnotation invalidNomenclatureEvent = allele.getAlleleNomenclatureEvents().get(0);
		invalidNomenclatureEvent.setNomenclatureEvent(null);
		Note invalidNote = allele.getRelatedNotes().get(0);
		invalidNote.setNoteType(null);
		invalidNote.setFreeText(null);
		allele.setRelatedNotes(List.of(invalidNote));
		
		allele.setAlleleMutationTypes(List.of(invalidMutationType));
		allele.setAlleleSymbol(invalidSymbol);
		allele.setAlleleFullName(invalidFullName);
		allele.setAlleleSynonyms(List.of(invalidSynonym));
		allele.setAlleleSecondaryIds(List.of(invalidSecondaryId));
		allele.setAlleleFunctionalImpacts(List.of(invalidFunctionalImpact));
		allele.setAlleleGermlineTransmissionStatus(invalidGTS);
		allele.setAlleleDatabaseStatus(invalidDatabaseStatus);
		allele.setAlleleNomenclatureEvents(List.of(invalidNomenclatureEvent));
		allele.setRelatedNotes(List.of(invalidNote));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(11))).
			body("errorMessages.alleleMutationTypes", is("mutationTypes - " + ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.alleleInheritanceModes", is("inheritanceMode - " + ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.alleleSymbol", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE,
					"nameType - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.alleleFullName", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE,
					"nameType - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.alleleSynonyms", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE,
					"nameType - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.alleleSecondaryIds", is("secondaryId - " + ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.alleleFunctionalImpacts", is("functionalImpacts - " + ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.alleleGermlineTransmissionStatus", is("germlineTransmissionStatus - " + ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.alleleDatabaseStatus", is("databaseStatus - " + ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.alleleNomenclatureEvents", is("nomenclatureEvent - " + ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"freeText - " + ValidationConstants.REQUIRED_MESSAGE,
					"noteType - " + ValidationConstants.REQUIRED_MESSAGE))));
	}
	
	@Test
	@Order(10)
	public void createAlleleWithEmptyRequiredFieldsLevel2() {
		Allele allele = new Allele();
		allele.setModEntityId("Allele:0010");
		allele.setTaxon(taxon);
		
		AlleleSymbolSlotAnnotation invalidSymbol = createAlleleSymbolSlotAnnotation(null, "", symbolNameType, null, null);
		AlleleFullNameSlotAnnotation invalidFullName = createAlleleFullNameSlotAnnotation(null, "", fullNameType, null, null);
		AlleleSynonymSlotAnnotation invalidSynonym = createAlleleSynonymSlotAnnotation(null, "", systematicNameType, null, null);
		AlleleSecondaryIdSlotAnnotation invalidSecondaryId = createAlleleSecondaryIdSlotAnnotation(null, "");
		
		Note invalidNote = new Note();
		invalidNote.setNoteType(noteType);
		invalidNote.setFreeText("");
		allele.setRelatedNotes(List.of(invalidNote));
		
		allele.setAlleleSymbol(invalidSymbol);
		allele.setAlleleFullName(invalidFullName);
		allele.setAlleleSynonyms(List.of(invalidSynonym));
		allele.setAlleleSecondaryIds(List.of(invalidSecondaryId));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(5))).
			body("errorMessages.alleleSymbol", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.alleleFullName", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.alleleSynonyms", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.alleleSecondaryIds", is("secondaryId - " + ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.relatedNotes", is("freeText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(11)
	public void editAlleleWithEmptyRequiredFieldsLevel2() {
		Allele allele = getAllele(ALLELE);
		
		AlleleSymbolSlotAnnotation invalidSymbol = allele.getAlleleSymbol();
		invalidSymbol.setDisplayText("");
		invalidSymbol.setFormatText("");
		AlleleFullNameSlotAnnotation invalidFullName = allele.getAlleleFullName();
		invalidFullName.setDisplayText("");
		invalidFullName.setFormatText("");
		AlleleSynonymSlotAnnotation invalidSynonym = allele.getAlleleSynonyms().get(0);
		invalidSynonym.setDisplayText("");
		invalidSynonym.setFormatText("");
		AlleleSecondaryIdSlotAnnotation invalidSecondaryId = allele.getAlleleSecondaryIds().get(0);
		invalidSecondaryId.setSecondaryId("");
		Note invalidNote = allele.getRelatedNotes().get(0);
		invalidNote.setFreeText("");
		
		allele.setAlleleSymbol(invalidSymbol);
		allele.setAlleleFullName(invalidFullName);
		allele.setAlleleSynonyms(List.of(invalidSynonym));
		allele.setAlleleSecondaryIds(List.of(invalidSecondaryId));
		allele.setRelatedNotes(List.of(invalidNote));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(5))).
			body("errorMessages.alleleSymbol", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.alleleFullName", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.alleleSynonyms", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.alleleSecondaryIds", is("secondaryId - " + ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.relatedNotes", is("freeText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(12)
	public void createAlleleWithInvalidFields() {
		NCBITaxonTerm nonPersistedTaxon = new NCBITaxonTerm();
		nonPersistedTaxon.setCurie("NCBITaxon:Invalid");
		Reference nonPersistedReference = new Reference();
		nonPersistedReference.setCurie("AGRKB:Invalid");
		SOTerm nonPersistedSoTerm = new SOTerm();
		nonPersistedSoTerm.setCurie("SO:Invalid");
		MPTerm nonPersistedMpTerm = new MPTerm();
		nonPersistedMpTerm.setCurie("MP:Invalid");
		DataProvider invalidDataProvider = new DataProvider();
		invalidDataProvider.setSourceOrganization(nonPersistedOrganization);
		
		Note invalidNote = new Note();
		invalidNote.setNoteType(dominantInheritanceMode);
		invalidNote.setReferences(List.of(nonPersistedReference));
		invalidNote.setFreeText("Invalid");
		
		Allele allele = new Allele();
		allele.setModEntityId("Allele:0012");
		allele.setTaxon(nonPersistedTaxon);
		allele.setInCollection(dominantInheritanceMode);
		allele.setReferences(List.of(nonPersistedReference));
		allele.setIsExtinct(false);
		allele.setDateCreated(datetime);
		allele.setDataProvider(invalidDataProvider);
		allele.setRelatedNotes(List.of(invalidNote));
		
		AlleleMutationTypeSlotAnnotation invalidMutationType = createAlleleMutationTypeSlotAnnotation(List.of(nonPersistedReference), List.of(nonPersistedSoTerm));
		AlleleInheritanceModeSlotAnnotation invalidInheritanceMode = createAlleleInheritanceModeSlotAnnotation(List.of(nonPersistedReference), fullNameType, nonPersistedMpTerm, "Invalid");
		AlleleSymbolSlotAnnotation invalidSymbol = createAlleleSymbolSlotAnnotation(List.of(nonPersistedReference), "Test symbol", fullNameType, dominantInheritanceMode, "https://test.org");
		AlleleFullNameSlotAnnotation invalidFullName = createAlleleFullNameSlotAnnotation(List.of(nonPersistedReference), "Test name", symbolNameType, dominantInheritanceMode, "https://test.org");
		AlleleSynonymSlotAnnotation invalidSynonym = createAlleleSynonymSlotAnnotation(List.of(nonPersistedReference), "Test synonym", mmpInCollection, dominantInheritanceMode, "https://test.org");
		AlleleSecondaryIdSlotAnnotation invalidSecondaryId = createAlleleSecondaryIdSlotAnnotation(List.of(nonPersistedReference), "TEST:Secondary");
		AlleleFunctionalImpactSlotAnnotation invalidFunctionalImpact = createAlleleFunctionalImpactSlotAnnotation(List.of(nonPersistedReference), List.of(dominantInheritanceMode), nonPersistedMpTerm, "Invalid");
		AlleleGermlineTransmissionStatusSlotAnnotation invalidGTS = createAlleleGermlineTransmissionStatusSlotAnnotation(List.of(nonPersistedReference), mmpInCollection);
		AlleleDatabaseStatusSlotAnnotation invalidDatabaseStatus = createAlleleDatabaseStatusSlotAnnotation(List.of(nonPersistedReference), cellLineGTS);
		AlleleNomenclatureEventSlotAnnotation invalidNomenclatureEvent = createAlleleNomenclatureEventSlotAnnotation(List.of(nonPersistedReference), symbolNameType);
		
		allele.setAlleleMutationTypes(List.of(invalidMutationType));
		allele.setAlleleInheritanceModes(List.of(invalidInheritanceMode));
		allele.setAlleleSymbol(invalidSymbol);
		allele.setAlleleFullName(invalidFullName);
		allele.setAlleleSynonyms(List.of(invalidSynonym));
		allele.setAlleleSecondaryIds(List.of(invalidSecondaryId));
		allele.setAlleleFunctionalImpacts(List.of(invalidFunctionalImpact));
		allele.setAlleleGermlineTransmissionStatus(invalidGTS);
		allele.setAlleleDatabaseStatus(invalidDatabaseStatus);
		allele.setAlleleNomenclatureEvents(List.of(invalidNomenclatureEvent));

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(15))).
			body("errorMessages.taxon", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.inCollection", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.references", is("curie - " + ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.alleleMutationTypes", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"mutationTypes - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.alleleInheritanceModes", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"inheritanceMode - " + ValidationConstants.INVALID_MESSAGE,
					"phenotypeTerm - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.alleleSymbol", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nameType - " + ValidationConstants.INVALID_MESSAGE,
					"synonymScope - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.alleleFullName", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nameType - " + ValidationConstants.INVALID_MESSAGE,
					"synonymScope - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.alleleSynonyms", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nameType - " + ValidationConstants.INVALID_MESSAGE,
					"synonymScope - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.alleleSecondaryIds", is("evidence - " + ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.dataProvider", is("sourceOrganization - " + ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.alleleFunctionalImpacts", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"functionalImpacts - " + ValidationConstants.INVALID_MESSAGE,
					"phenotypeTerm - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.alleleGermlineTransmissionStatus", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"germlineTransmissionStatus - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.alleleDatabaseStatus", is(String.join(" | ", List.of(
					"databaseStatus - " + ValidationConstants.INVALID_MESSAGE,
					"evidence - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.alleleNomenclatureEvents", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nomenclatureEvent - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.INVALID_MESSAGE,
					"references - " + ValidationConstants.INVALID_MESSAGE))));
	}

	@Test
	@Order(13)
	public void editAlleleWithInvalidFields() {
		NCBITaxonTerm nonPersistedTaxon = new NCBITaxonTerm();
		nonPersistedTaxon.setCurie("NCBITaxon:Invalid");
		Reference nonPersistedReference = new Reference();
		nonPersistedReference.setCurie("AGRKB:Invalid");
		SOTerm nonPersistedSoTerm = new SOTerm();
		nonPersistedSoTerm.setCurie("SO:Invalid");
		MPTerm nonPersistedMpTerm = new MPTerm();
		nonPersistedMpTerm.setCurie("MP:Invalid");
		DataProvider invalidDataProvider = new DataProvider();
		invalidDataProvider.setSourceOrganization(nonPersistedOrganization);
		
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(nonPersistedTaxon);
		allele.setInCollection(dominantInheritanceMode);
		allele.setReferences(List.of(nonPersistedReference));
		allele.setIsExtinct(false);
		allele.setDateCreated(datetime);
		allele.setDataProvider(invalidDataProvider);
		
		AlleleMutationTypeSlotAnnotation invalidMutationType = allele.getAlleleMutationTypes().get(0);
		invalidMutationType.setEvidence(List.of(nonPersistedReference));
		invalidMutationType.setMutationTypes(List.of(nonPersistedSoTerm));
		AlleleInheritanceModeSlotAnnotation invalidInheritanceMode = allele.getAlleleInheritanceModes().get(0);
		invalidInheritanceMode.setEvidence(List.of(nonPersistedReference));
		invalidInheritanceMode.setInheritanceMode(fullNameType);
		invalidInheritanceMode.setPhenotypeTerm(nonPersistedMpTerm);
		AlleleSymbolSlotAnnotation invalidSymbol = allele.getAlleleSymbol();
		invalidSymbol.setEvidence(List.of(nonPersistedReference));
		invalidSymbol.setNameType(fullNameType);
		invalidSymbol.setSynonymScope(dominantInheritanceMode);
		AlleleFullNameSlotAnnotation invalidFullName = allele.getAlleleFullName();
		invalidFullName.setEvidence(List.of(nonPersistedReference));
		invalidFullName.setNameType(symbolNameType);
		invalidFullName.setSynonymScope(dominantInheritanceMode);
		AlleleSynonymSlotAnnotation invalidSynonym = allele.getAlleleSynonyms().get(0);
		invalidSynonym.setEvidence(List.of(nonPersistedReference));
		invalidSynonym.setNameType(mmpInCollection);
		invalidSynonym.setSynonymScope(dominantInheritanceMode);
		AlleleSecondaryIdSlotAnnotation invalidSecondaryId = allele.getAlleleSecondaryIds().get(0);
		invalidSecondaryId.setEvidence(List.of(nonPersistedReference));
		AlleleFunctionalImpactSlotAnnotation invalidFunctionalImpact = allele.getAlleleFunctionalImpacts().get(0);
		invalidFunctionalImpact.setEvidence(List.of(nonPersistedReference));
		invalidFunctionalImpact.setFunctionalImpacts(List.of(dominantInheritanceMode));
		invalidFunctionalImpact.setPhenotypeTerm(nonPersistedMpTerm);
		AlleleGermlineTransmissionStatusSlotAnnotation invalidGTS = allele.getAlleleGermlineTransmissionStatus();
		invalidGTS.setEvidence(List.of(nonPersistedReference));
		invalidGTS.setGermlineTransmissionStatus(dominantInheritanceMode);
		AlleleDatabaseStatusSlotAnnotation invalidDatabaseStatus = allele.getAlleleDatabaseStatus();
		invalidDatabaseStatus.setEvidence(List.of(nonPersistedReference));
		invalidDatabaseStatus.setDatabaseStatus(hypermorphicFunctionalImpact);
		AlleleNomenclatureEventSlotAnnotation invalidNomenclatureEvent = allele.getAlleleNomenclatureEvents().get(0);
		invalidNomenclatureEvent.setEvidence(List.of(nonPersistedReference));
		invalidNomenclatureEvent.setNomenclatureEvent(fullNameType);
		
		Note invalidNote = allele.getRelatedNotes().get(0);
		invalidNote.setReferences(List.of(nonPersistedReference));
		invalidNote.setNoteType(mmpInCollection);
		
		allele.setAlleleMutationTypes(List.of(invalidMutationType));
		allele.setAlleleInheritanceModes(List.of(invalidInheritanceMode));
		allele.setAlleleSymbol(invalidSymbol);
		allele.setAlleleFullName(invalidFullName);
		allele.setAlleleSynonyms(List.of(invalidSynonym));
		allele.setAlleleSecondaryIds(List.of(invalidSecondaryId));
		allele.setAlleleFunctionalImpacts(List.of(invalidFunctionalImpact));
		allele.setAlleleGermlineTransmissionStatus(invalidGTS);
		allele.setAlleleDatabaseStatus(invalidDatabaseStatus);
		allele.setAlleleNomenclatureEvents(List.of(invalidNomenclatureEvent));
		allele.setRelatedNotes(List.of(invalidNote));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(15))).
			body("errorMessages.taxon", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.inCollection", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.references", is("curie - " + ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.alleleMutationTypes", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"mutationTypes - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.alleleInheritanceModes", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"inheritanceMode - " + ValidationConstants.INVALID_MESSAGE,
					"phenotypeTerm - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.alleleSymbol", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nameType - " + ValidationConstants.INVALID_MESSAGE,
					"synonymScope - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.alleleFullName", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nameType - " + ValidationConstants.INVALID_MESSAGE,
					"synonymScope - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.alleleSynonyms", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nameType - " + ValidationConstants.INVALID_MESSAGE,
					"synonymScope - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.alleleSecondaryIds", is("evidence - " + ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.dataProvider", is("sourceOrganization - " + ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.alleleFunctionalImpacts", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"functionalImpacts - " + ValidationConstants.INVALID_MESSAGE,
					"phenotypeTerm - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.alleleGermlineTransmissionStatus", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"germlineTransmissionStatus - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.alleleDatabaseStatus", is(String.join(" | ", List.of(
					"databaseStatus - " + ValidationConstants.INVALID_MESSAGE,
					"evidence - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.alleleNomenclatureEvents", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nomenclatureEvent - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.INVALID_MESSAGE,
					"references - " + ValidationConstants.INVALID_MESSAGE))));
	}

	@Test
	@Order(14)
	public void createAlleleWithObsoleteFields() {
		Allele allele = new Allele();
		allele.setModEntityId("Allele:0012");
		allele.setTaxon(obsoleteTaxon);
		allele.setInCollection(obsoleteCollection);
		allele.setReferences(List.of(obsoleteReference));
		allele.setDataProvider(obsoleteDataProvider);
		
		Note obsoleteNote = new Note();
		obsoleteNote.setNoteType(obsoleteNoteType);
		obsoleteNote.setReferences(List.of(obsoleteReference));
		obsoleteNote.setFreeText("Obsolete");
		allele.setRelatedNotes(List.of(obsoleteNote));
		
		AlleleMutationTypeSlotAnnotation obsoleteMutationType = createAlleleMutationTypeSlotAnnotation(List.of(obsoleteReference), List.of(obsoleteSoTerm));
		AlleleInheritanceModeSlotAnnotation obsoleteInheritanceMode = createAlleleInheritanceModeSlotAnnotation(List.of(obsoleteReference), obsoleteInheritanceModeTerm, obsoleteMpTerm, "Obsolete");
		AlleleSymbolSlotAnnotation obsoleteSymbol = createAlleleSymbolSlotAnnotation(List.of(obsoleteReference), "Test symbol", obsoleteSymbolNameType, obsoleteSynonymScope, "https://test.org");
		AlleleFullNameSlotAnnotation obsoleteFullName = createAlleleFullNameSlotAnnotation(List.of(obsoleteReference), "Test name", obsoleteFullNameType, obsoleteSynonymScope, "https://test.org");
		AlleleSynonymSlotAnnotation obsoleteSynonym = createAlleleSynonymSlotAnnotation(List.of(obsoleteReference), "Test synonym", obsoleteNameType, obsoleteSynonymScope, "https://test.org");
		AlleleSecondaryIdSlotAnnotation obsoleteSecondaryId = createAlleleSecondaryIdSlotAnnotation(List.of(obsoleteReference), "TEST:Secondary");
		AlleleFunctionalImpactSlotAnnotation obsoleteFunctionalImpactSA = createAlleleFunctionalImpactSlotAnnotation(List.of(obsoleteReference), List.of(obsoleteFunctionalImpact), obsoleteMpTerm, "Obsolete");
		AlleleGermlineTransmissionStatusSlotAnnotation obsoleteGTSSA = createAlleleGermlineTransmissionStatusSlotAnnotation(List.of(obsoleteReference), obsoleteGTS);
		AlleleDatabaseStatusSlotAnnotation obsoleteAlleleDatabaseStatus = createAlleleDatabaseStatusSlotAnnotation(List.of(obsoleteReference), obsoleteDatabaseStatus);
		AlleleNomenclatureEventSlotAnnotation obsoleteNomenclatureEventSA = createAlleleNomenclatureEventSlotAnnotation(List.of(obsoleteReference), obsoleteNomenclatureEvent);
		
		allele.setAlleleMutationTypes(List.of(obsoleteMutationType));
		allele.setAlleleInheritanceModes(List.of(obsoleteInheritanceMode));
		allele.setAlleleSymbol(obsoleteSymbol);
		allele.setAlleleFullName(obsoleteFullName);
		allele.setAlleleSynonyms(List.of(obsoleteSynonym));
		allele.setAlleleSecondaryIds(List.of(obsoleteSecondaryId));
		allele.setAlleleFunctionalImpacts(List.of(obsoleteFunctionalImpactSA));
		allele.setAlleleGermlineTransmissionStatus(obsoleteGTSSA);
		allele.setAlleleDatabaseStatus(obsoleteAlleleDatabaseStatus);
		allele.setAlleleNomenclatureEvents(List.of(obsoleteNomenclatureEventSA));

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(15))).
			body("errorMessages.taxon", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.inCollection", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.references", is("curie - " + ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.alleleMutationTypes", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"mutationTypes - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.alleleInheritanceModes", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"inheritanceMode - " + ValidationConstants.OBSOLETE_MESSAGE,
					"phenotypeTerm - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.alleleSymbol", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"nameType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"synonymScope - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.alleleFullName", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"nameType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"synonymScope - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.alleleSynonyms", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"nameType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"synonymScope - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.alleleSecondaryIds", is("evidence - " + ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.alleleFunctionalImpacts", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"functionalImpacts - " + ValidationConstants.OBSOLETE_MESSAGE,
					"phenotypeTerm - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.alleleGermlineTransmissionStatus", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"germlineTransmissionStatus - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.alleleDatabaseStatus", is(String.join(" | ", List.of(
					"databaseStatus - " + ValidationConstants.OBSOLETE_MESSAGE,
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.alleleNomenclatureEvents", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"nomenclatureEvent - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"references - " + ValidationConstants.OBSOLETE_MESSAGE))));
	}

	@Test
	@Order(15)
	public void editAlleleWithObsoleteFields() {
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(obsoleteTaxon);
		allele.setInCollection(obsoleteCollection);
		allele.setReferences(List.of(obsoleteReference));
		allele.setDataProvider(obsoleteDataProvider);
		
		Note obsoleteNote = allele.getRelatedNotes().get(0);
		obsoleteNote.setNoteType(obsoleteNoteType);
		obsoleteNote.setReferences(List.of(obsoleteReference));
		allele.setRelatedNotes(List.of(obsoleteNote));
		
		AlleleMutationTypeSlotAnnotation obsoleteMutationType = allele.getAlleleMutationTypes().get(0);
		obsoleteMutationType.setEvidence(List.of(obsoleteReference));
		obsoleteMutationType.setMutationTypes(List.of(obsoleteSoTerm));
		AlleleInheritanceModeSlotAnnotation obsoleteInheritanceMode = allele.getAlleleInheritanceModes().get(0);
		obsoleteInheritanceMode.setEvidence(List.of(obsoleteReference));
		obsoleteInheritanceMode.setInheritanceMode(obsoleteInheritanceModeTerm);
		obsoleteInheritanceMode.setPhenotypeTerm(obsoleteMpTerm);
		AlleleSymbolSlotAnnotation obsoleteSymbol = allele.getAlleleSymbol();
		obsoleteSymbol.setEvidence(List.of(obsoleteReference));
		obsoleteSymbol.setNameType(obsoleteSymbolNameType);
		obsoleteSymbol.setSynonymScope(obsoleteSynonymScope);
		AlleleFullNameSlotAnnotation obsoleteFullName = allele.getAlleleFullName();
		obsoleteFullName.setEvidence(List.of(obsoleteReference));
		obsoleteFullName.setNameType(obsoleteFullNameType);
		obsoleteFullName.setSynonymScope(obsoleteSynonymScope);
		AlleleSynonymSlotAnnotation obsoleteSynonym = allele.getAlleleSynonyms().get(0);
		obsoleteSynonym.setEvidence(List.of(obsoleteReference));
		obsoleteSynonym.setNameType(obsoleteNameType);
		obsoleteSynonym.setSynonymScope(obsoleteSynonymScope);
		AlleleSecondaryIdSlotAnnotation obsoleteSecondaryId = allele.getAlleleSecondaryIds().get(0);
		obsoleteSecondaryId.setEvidence(List.of(obsoleteReference));
		AlleleFunctionalImpactSlotAnnotation obsoleteFunctionalImpactSA = allele.getAlleleFunctionalImpacts().get(0);
		obsoleteFunctionalImpactSA.setFunctionalImpacts(List.of(obsoleteFunctionalImpact));
		obsoleteFunctionalImpactSA.setPhenotypeTerm(obsoleteMpTerm);
		obsoleteFunctionalImpactSA.setEvidence(List.of(obsoleteReference));
		AlleleGermlineTransmissionStatusSlotAnnotation obsoleteGTSSA = allele.getAlleleGermlineTransmissionStatus();
		obsoleteGTSSA.setEvidence(List.of(obsoleteReference));
		obsoleteGTSSA.setGermlineTransmissionStatus(obsoleteGTS);
		AlleleDatabaseStatusSlotAnnotation obsoleteDS = allele.getAlleleDatabaseStatus();
		obsoleteDS.setEvidence(List.of(obsoleteReference));
		obsoleteDS.setDatabaseStatus(obsoleteDatabaseStatus);
		AlleleNomenclatureEventSlotAnnotation obsoleteNomenclatureEventSA = allele.getAlleleNomenclatureEvents().get(0);
		obsoleteNomenclatureEventSA.setEvidence(List.of(obsoleteReference));
		obsoleteNomenclatureEventSA.setNomenclatureEvent(obsoleteNomenclatureEvent);
		allele.setAlleleMutationTypes(List.of(obsoleteMutationType));
		allele.setAlleleSymbol(obsoleteSymbol);
		allele.setAlleleFullName(obsoleteFullName);
		allele.setAlleleSynonyms(List.of(obsoleteSynonym));
		allele.setAlleleSecondaryIds(List.of(obsoleteSecondaryId));
		allele.setAlleleFunctionalImpacts(List.of(obsoleteFunctionalImpactSA));
		allele.setAlleleGermlineTransmissionStatus(obsoleteGTSSA);

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(15))).
			body("errorMessages.taxon", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.inCollection", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.references", is("curie - " + ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.alleleMutationTypes", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"mutationTypes - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.alleleInheritanceModes", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"inheritanceMode - " + ValidationConstants.OBSOLETE_MESSAGE,
					"phenotypeTerm - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.alleleSymbol", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"nameType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"synonymScope - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.alleleFullName", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"nameType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"synonymScope - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.alleleSynonyms", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"nameType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"synonymScope - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.alleleSecondaryIds", is("evidence - " + ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.alleleFunctionalImpacts", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"functionalImpacts - " + ValidationConstants.OBSOLETE_MESSAGE,
					"phenotypeTerm - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.alleleGermlineTransmissionStatus", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"germlineTransmissionStatus - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.alleleDatabaseStatus", is(String.join(" | ", List.of(
					"databaseStatus - " + ValidationConstants.OBSOLETE_MESSAGE,
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.alleleNomenclatureEvents", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"nomenclatureEvent - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"references - " + ValidationConstants.OBSOLETE_MESSAGE))));
	}

	@Test
	@Order(17)
	public void editAlleleWithNullNonRequiredFieldsLevel2() {
		// Level 2 done before 1 to avoid having to restore nulled fields
		Allele allele = getAllele(ALLELE);

		AlleleMutationTypeSlotAnnotation editedMutationType = allele.getAlleleMutationTypes().get(0);
		editedMutationType.setEvidence(null);
		
		AlleleInheritanceModeSlotAnnotation editedInheritanceMode = allele.getAlleleInheritanceModes().get(0);
		editedInheritanceMode.setEvidence(null);
		editedInheritanceMode.setPhenotypeTerm(null);
		editedInheritanceMode.setPhenotypeStatement(null);
		
		AlleleSymbolSlotAnnotation editedSymbol = allele.getAlleleSymbol();
		editedSymbol.setEvidence(null);
		editedSymbol.setSynonymScope(null);
		editedSymbol.setSynonymUrl(null);
		
		AlleleFullNameSlotAnnotation editedFullName = allele.getAlleleFullName();
		editedFullName.setEvidence(null);
		editedFullName.setSynonymScope(null);
		editedFullName.setSynonymUrl(null);
		
		AlleleSynonymSlotAnnotation editedSynonym = allele.getAlleleSynonyms().get(0);
		editedSynonym.setEvidence(null);
		editedSynonym.setSynonymScope(null);
		editedSynonym.setSynonymUrl(null);
		
		AlleleSecondaryIdSlotAnnotation editedSecondaryId = allele.getAlleleSecondaryIds().get(0);
		editedSecondaryId.setEvidence(null);
		
		AlleleFunctionalImpactSlotAnnotation editedFunctionalImpact = allele.getAlleleFunctionalImpacts().get(0);
		editedFunctionalImpact.setEvidence(null);
		editedFunctionalImpact.setPhenotypeTerm(null);
		editedFunctionalImpact.setPhenotypeStatement(null);
		
		AlleleGermlineTransmissionStatusSlotAnnotation editedGTS = allele.getAlleleGermlineTransmissionStatus();
		editedGTS.setEvidence(null);
		
		AlleleDatabaseStatusSlotAnnotation editedDatabaseStatus = allele.getAlleleDatabaseStatus();
		editedDatabaseStatus.setEvidence(null);
		
		AlleleNomenclatureEventSlotAnnotation editedNomenclatureEvent = allele.getAlleleNomenclatureEvents().get(0);
		editedNomenclatureEvent.setEvidence(null);
		
		Note editedNote = allele.getRelatedNotes().get(0);
		editedNote.setReferences(null);
		
		allele.setAlleleMutationTypes(List.of(editedMutationType));
		allele.setAlleleInheritanceModes(List.of(editedInheritanceMode));
		allele.setAlleleSymbol(editedSymbol);
		allele.setAlleleFullName(editedFullName);
		allele.setAlleleSynonyms(List.of(editedSynonym));
		allele.setAlleleSecondaryIds(List.of(editedSecondaryId));
		allele.setAlleleFunctionalImpacts(List.of(editedFunctionalImpact));
		allele.setAlleleGermlineTransmissionStatus(editedGTS);
		allele.setAlleleDatabaseStatus(editedDatabaseStatus);
		allele.setAlleleNomenclatureEvents(List.of(editedNomenclatureEvent));
		allele.setRelatedNotes(List.of(editedNote));

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get("/api/allele/" + ALLELE).
			then().
			statusCode(200).
			body("entity", hasKey("alleleMutationTypes")).
			body("entity", hasKey("alleleInheritanceModes")).
			body("entity", hasKey("alleleSymbol")).
			body("entity", hasKey("alleleFullName")).
			body("entity", hasKey("alleleSynonyms")).
			body("entity", hasKey("alleleSecondaryIds")).
			body("entity", hasKey("alleleFunctionalImpacts")).
			body("entity", hasKey("alleleGermlineTransmissionStatus")).
			body("entity", hasKey("alleleDatabaseStatus")).
			body("entity.alleleMutationTypes[0]", not(hasKey("evidence"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("evidence"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("phenotypeTerm"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("phenotypeStatement"))).
			body("entity.alleleSymbol", not(hasKey("evidence"))).
			body("entity.alleleSymbol", not(hasKey("synonymScope"))).
			body("entity.alleleSymbol", not(hasKey("synonymUrl"))).
			body("entity.alleleFullName", not(hasKey("evidence"))).
			body("entity.alleleFullName", not(hasKey("synonymScope"))).
			body("entity.alleleFullName", not(hasKey("synonymUrl"))).
			body("entity.alleleSynonyms[0]", not(hasKey("evidence"))).
			body("entity.alleleSynonyms[0]", not(hasKey("synonymScope"))).
			body("entity.alleleSynonyms[0]", not(hasKey("synonymUrl"))).
			body("entity.alleleSecondaryIds[0]", not(hasKey("evidence"))).
			body("entity.alleleFunctionalImpacts[0]", not(hasKey("evidence"))).
			body("entity.alleleFunctionalImpacts[0]", not(hasKey("phenotypeTerm"))).
			body("entity.alleleFunctionalImpacts[0]", not(hasKey("phenotypeStatement"))).
			body("entity.alleleGermlineTransmissionStatus", not(hasKey("evidence"))).
			body("entity.alleleDatabaseStatus", not(hasKey("evidence"))).
			body("entity.alleleNomenclatureEvents[0]", not(hasKey("evidence"))).
			body("entity.relatedNotes[0]", not(hasKey("references")));
	}

	@Test
	@Order(18)
	public void editAlleleWithNullNonRequiredFieldsLevel1() {
		Allele allele = getAllele(ALLELE);

		allele.setInCollection(null);
		allele.setReferences(null);
		allele.setIsExtinct(null);
		allele.setDateCreated(null);
		allele.setAlleleMutationTypes(null);
		allele.setAlleleFullName(null);
		allele.setAlleleSynonyms(null);
		allele.setAlleleSecondaryIds(null);
		allele.setAlleleInheritanceModes(null);
		allele.setAlleleFunctionalImpacts(null);
		allele.setAlleleGermlineTransmissionStatus(null);
		allele.setAlleleDatabaseStatus(null);
		allele.setAlleleNomenclatureEvents(null);
		allele.setRelatedNotes(null);

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get("/api/allele/" + ALLELE).
			then().
			statusCode(200).
			body("entity", not(hasKey("inCollection"))).
			body("entity", not(hasKey("references"))).
			body("entity", not(hasKey("isExtinct"))).
			body("entity", not(hasKey("dateCreated"))).
			body("entity", not(hasKey("alleleMutationTypes"))).
			body("entity", not(hasKey("alleleInheritanceModes"))).
			body("entity", not(hasKey("alleleFullName"))).
			body("entity", not(hasKey("alleleSynonyms"))).
			body("entity", not(hasKey("alleleSecondaryIds"))).
			body("entity", not(hasKey("alleleFunctionalImpacts"))).
			body("entity", not(hasKey("alleleGermlineTransmissionStatus"))).
			body("entity", not(hasKey("alleleDatabaseStatus"))).
			body("entity", not(hasKey("alleleNomenclatureEvents"))).
			body("entity", not(hasKey("relatedNotes")));
	}
	
	@Test
	@Order(19)
	public void createAlleleWithOnlyRequiredFieldsLevel1() {
		Allele allele = new Allele();
		allele.setModEntityId("ALLELE:0019");
		allele.setTaxon(taxon);
		allele.setAlleleSymbol(alleleSymbol);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(200);
	}
	
	@Test
	@Order(20)
	public void createAlleleWithOnlyRequiredFieldsLevel2() {
		Allele allele = new Allele();
		allele.setModEntityId("ALLELE:0020");
		allele.setTaxon(taxon);

		AlleleMutationTypeSlotAnnotation minimalAlleleMutationType = createAlleleMutationTypeSlotAnnotation(null, List.of(soTerm));
		AlleleInheritanceModeSlotAnnotation minimalAlleleInheritanceMode = createAlleleInheritanceModeSlotAnnotation(null, dominantInheritanceMode, null, null);
		AlleleSymbolSlotAnnotation minimalAlleleSymbol = createAlleleSymbolSlotAnnotation(null, "Test symbol", symbolNameType, null, null);
		AlleleFullNameSlotAnnotation minimalAlleleFullName = createAlleleFullNameSlotAnnotation(null, "Test name", fullNameType, null, null);
		AlleleSynonymSlotAnnotation minimalAlleleSynonym = createAlleleSynonymSlotAnnotation(null, "Test synonym", systematicNameType, null, null);
		AlleleSecondaryIdSlotAnnotation minimalAlleleSecondaryId = createAlleleSecondaryIdSlotAnnotation(null, "TEST:Secondary");
		AlleleFunctionalImpactSlotAnnotation minimalFunctionalImpact = createAlleleFunctionalImpactSlotAnnotation(null, List.of(hypermorphicFunctionalImpact), null, null);
		AlleleGermlineTransmissionStatusSlotAnnotation minimalGTS = createAlleleGermlineTransmissionStatusSlotAnnotation(null, cellLineGTS);
		AlleleDatabaseStatusSlotAnnotation minimalDatabaseStatus = createAlleleDatabaseStatusSlotAnnotation(null, approvedDatabaseStatus);
		AlleleNomenclatureEventSlotAnnotation minimalNomenclatureEvent = createAlleleNomenclatureEventSlotAnnotation(null, dataMergedEvent);
		
		Note minimalNote = createNote(noteType, "Test text", false, null);
		
		allele.setAlleleSymbol(minimalAlleleSymbol);
		allele.setAlleleFullName(minimalAlleleFullName);
		allele.setAlleleSynonyms(List.of(minimalAlleleSynonym));
		allele.setAlleleSecondaryIds(List.of(minimalAlleleSecondaryId));
		allele.setAlleleMutationTypes(List.of(minimalAlleleMutationType));
		allele.setAlleleInheritanceModes(List.of(minimalAlleleInheritanceMode));
		allele.setAlleleFunctionalImpacts(List.of(minimalFunctionalImpact));
		allele.setAlleleGermlineTransmissionStatus(minimalGTS);
		allele.setAlleleDatabaseStatus(minimalDatabaseStatus);
		allele.setAlleleNomenclatureEvents(List.of(minimalNomenclatureEvent));
		allele.setRelatedNotes(List.of(minimalNote));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(200);
	}
	
	@Test
	@Order(21)
	public void createAlleleWithDuplicateNote() {
		Allele allele = new Allele();
		allele.setModEntityId("ALLELE:0021");
		allele.setTaxon(taxon);
		
		AlleleSymbolSlotAnnotation alleleSymbol = createAlleleSymbolSlotAnnotation(null, "Test symbol", symbolNameType, null, null);
		allele.setAlleleSymbol(alleleSymbol);
		
		Note note1 = createNote(noteType, "Test text", false, null);
		Note note2 = createNote(noteType, "Test text", false, null);
		allele.setRelatedNotes(List.of(note1, note2));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.relatedNotes", is("freeText - " + ValidationConstants.DUPLICATE_MESSAGE + " (Test text|comment|false|false)"));
	}
	
	@Test
	@Order(22)
	public void updateAlleleDetailWithNewAssociations() {
		Allele allele = getAllele(ALLELE);
		
		AlleleGeneAssociation geneAssociation = new AlleleGeneAssociation();
		geneAssociation.setAlleleGeneAssociationObject(gene);
		geneAssociation.setRelation(geneAssociationRelation);
		Reference reference = createReference("AGRKB:AssocTest1", false);
		geneAssociation.setEvidence(List.of(reference));
		allele.setAlleleGeneAssociations(List.of(geneAssociation));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele/updateDetail").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get("/api/allele/" + ALLELE).
			then().
			statusCode(200).
			body("entity", hasKey("alleleGeneAssociations")).
			body("entity.alleleGeneAssociations[0].alleleGeneAssociationObject.modEntityId", is(gene.getModEntityId()));
	}
	
	@Test
	@Order(23)
	public void updateAlleleDetailRemoveAssociations() {
		Allele allele = getAllele(ALLELE);
		
		allele.setAlleleGeneAssociations(null);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele/updateDetail").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get("/api/allele/" + ALLELE).
			then().
			statusCode(200).
			body("entity", not(hasKey("alleleGeneAssociations")));
	}
	
	@Test
	@Order(24)
	public void updateAlleleDetailWithInvalidAssociations() {
		Allele allele = getAllele(ALLELE);
		
		AlleleGeneAssociation geneAssociation = new AlleleGeneAssociation();
		geneAssociation.setAlleleGeneAssociationObject(gene);
		geneAssociation.setRelation(dominantInheritanceMode);
		allele.setAlleleGeneAssociations(List.of(geneAssociation));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele/updateDetail").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleGeneAssociations", is("relation - " + ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(25)
	public void updateAlleleDetailsWithMultipleIsAlleleOfAssociations() {
		Allele allele = getAllele(ALLELE);
		
		AlleleGeneAssociation geneAssociation = new AlleleGeneAssociation();
		geneAssociation.setAlleleGeneAssociationObject(gene);
		geneAssociation.setRelation(geneAssociationRelation);
		
		AlleleGeneAssociation geneAssociation2 = new AlleleGeneAssociation();
		geneAssociation2.setAlleleGeneAssociationObject(gene2);
		geneAssociation2.setRelation(geneAssociationRelation);
		
		allele.setAlleleGeneAssociations(List.of(geneAssociation, geneAssociation2));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele/updateDetail").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleGeneAssociations", is("relation - " + ValidationConstants.DUPLICATE_RELATION_PREFIX + VocabularyConstants.ALLELE_OF_VOCABULARY_TERM));
	}
	
	@Test
	@Order(26)
	public void updateAlleleDetailsWithDuplicateAssociation() {
		Allele allele = getAllele(ALLELE);
		
		AlleleGeneAssociation geneAssociation = new AlleleGeneAssociation();
		geneAssociation.setAlleleGeneAssociationObject(gene);
		geneAssociation.setRelation(geneAssociationRelation);
		
		AlleleGeneAssociation geneAssociation2 = new AlleleGeneAssociation();
		geneAssociation2.setAlleleGeneAssociationObject(gene2);
		geneAssociation2.setRelation(geneAssociationRelation2);
		
		AlleleGeneAssociation geneAssociation3 = new AlleleGeneAssociation();
		geneAssociation3.setAlleleGeneAssociationObject(gene2);
		geneAssociation3.setRelation(geneAssociationRelation2);
		
		allele.setAlleleGeneAssociations(List.of(geneAssociation, geneAssociation2, geneAssociation3));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele/updateDetail").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleGeneAssociations", is("relation - " + ValidationConstants.DUPLICATE_MESSAGE + " (Allele:0001|duplication|TEST:AssociatedGene2)"));
	}

	@Test
	@Order(27)
	public void deleteAllele() {

		RestAssured.given().
				when().
				delete("/api/allele/" + ALLELE).
				then().
				statusCode(200);
	}
	
	private AlleleMutationTypeSlotAnnotation createAlleleMutationTypeSlotAnnotation(List<InformationContentEntity> evidence, List<SOTerm> mutationTypes) {
		AlleleMutationTypeSlotAnnotation amt = new AlleleMutationTypeSlotAnnotation();
		amt.setEvidence(evidence);
		amt.setMutationTypes(mutationTypes);
		
		return amt;
	}
	
	private AlleleGermlineTransmissionStatusSlotAnnotation createAlleleGermlineTransmissionStatusSlotAnnotation(List<InformationContentEntity> evidence, VocabularyTerm status) {
		AlleleGermlineTransmissionStatusSlotAnnotation agts = new AlleleGermlineTransmissionStatusSlotAnnotation();
		agts.setEvidence(evidence);
		agts.setGermlineTransmissionStatus(status);
		
		return agts;
	}
	
	private AlleleDatabaseStatusSlotAnnotation createAlleleDatabaseStatusSlotAnnotation(List<InformationContentEntity> evidence, VocabularyTerm status) {
		AlleleDatabaseStatusSlotAnnotation ads = new AlleleDatabaseStatusSlotAnnotation();
		ads.setEvidence(evidence);
		ads.setDatabaseStatus(status);
		
		return ads;
	}

	private AlleleInheritanceModeSlotAnnotation createAlleleInheritanceModeSlotAnnotation(List<InformationContentEntity> evidence, VocabularyTerm inheritanceMode, PhenotypeTerm phenotypeTerm, String phenotypeStatement) {
		AlleleInheritanceModeSlotAnnotation imAnnotation = new AlleleInheritanceModeSlotAnnotation();
		imAnnotation.setEvidence(evidence);
		imAnnotation.setInheritanceMode(inheritanceMode);
		imAnnotation.setPhenotypeTerm(phenotypeTerm);
		imAnnotation.setPhenotypeStatement(phenotypeStatement);
		
		return imAnnotation;
	}
		
	private AlleleSymbolSlotAnnotation createAlleleSymbolSlotAnnotation(List<InformationContentEntity> evidence, String name, VocabularyTerm nameType, VocabularyTerm synonymScope, String synonymUrl) {
		AlleleSymbolSlotAnnotation symbol = new AlleleSymbolSlotAnnotation();
		symbol.setEvidence(evidence);
		symbol.setDisplayText(name);
		symbol.setFormatText(name);
		symbol.setNameType(nameType);
		symbol.setSynonymScope(synonymScope);
		symbol.setSynonymUrl(synonymUrl);
		
		return symbol;
	}

	private AlleleFullNameSlotAnnotation createAlleleFullNameSlotAnnotation(List<InformationContentEntity> evidence, String name, VocabularyTerm nameType, VocabularyTerm synonymScope, String synonymUrl) {
		AlleleFullNameSlotAnnotation fullName = new AlleleFullNameSlotAnnotation();
		fullName.setEvidence(evidence);
		fullName.setDisplayText(name);
		fullName.setFormatText(name);
		fullName.setNameType(nameType);
		fullName.setSynonymScope(synonymScope);
		fullName.setSynonymUrl(synonymUrl);
		
		return fullName;
	}

	private AlleleSynonymSlotAnnotation createAlleleSynonymSlotAnnotation(List<InformationContentEntity> evidence, String name, VocabularyTerm nameType, VocabularyTerm synonymScope, String synonymUrl) {
		AlleleSynonymSlotAnnotation synonym = new AlleleSynonymSlotAnnotation();
		synonym.setEvidence(evidence);
		synonym.setDisplayText(name);
		synonym.setFormatText(name);
		synonym.setNameType(nameType);
		synonym.setSynonymScope(synonymScope);
		synonym.setSynonymUrl(synonymUrl);
		
		return synonym;
	}

	private AlleleSecondaryIdSlotAnnotation createAlleleSecondaryIdSlotAnnotation(List<InformationContentEntity> evidence, String id) {
		AlleleSecondaryIdSlotAnnotation secondaryId = new AlleleSecondaryIdSlotAnnotation();
		secondaryId.setSecondaryId(id);
		secondaryId.setEvidence(evidence);
		
		return secondaryId;
	}
	
	private AlleleFunctionalImpactSlotAnnotation createAlleleFunctionalImpactSlotAnnotation(List<InformationContentEntity> evidence, List<VocabularyTerm> functionalImpacts, PhenotypeTerm phenotypeTerm, String phenotypeStatement) {
		AlleleFunctionalImpactSlotAnnotation functionalImpact = new AlleleFunctionalImpactSlotAnnotation();
		functionalImpact.setEvidence(evidence);
		functionalImpact.setFunctionalImpacts(functionalImpacts);
		functionalImpact.setPhenotypeTerm(phenotypeTerm);
		functionalImpact.setPhenotypeStatement(phenotypeStatement);
		
		return functionalImpact;
	}
	
	private AlleleNomenclatureEventSlotAnnotation createAlleleNomenclatureEventSlotAnnotation(List<InformationContentEntity> evidence, VocabularyTerm event) {
		AlleleNomenclatureEventSlotAnnotation ane = new AlleleNomenclatureEventSlotAnnotation();
		ane.setEvidence(evidence);
		ane.setNomenclatureEvent(event);
		
		return ane;
	}

}
