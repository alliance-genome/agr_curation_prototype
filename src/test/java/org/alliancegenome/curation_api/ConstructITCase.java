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
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.constructAssociations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructSynonymSlotAnnotation;
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
@DisplayName("307 - ConstructITCase")
@Order(307)
public class ConstructITCase extends BaseITCase {

	private static final String CONSTRUCT = "Construct:0001";
	
	private Vocabulary noteTypeVocabulary;
	private VocabularyTerm noteType;
	private VocabularyTerm noteType2;
	private VocabularyTerm obsoleteNoteType;
	private Vocabulary alleleNoteTypeVocabulary;
	private VocabularyTerm alleleNoteType;
	private Reference reference;
	private Reference reference2;
	private Reference obsoleteReference;
	private NCBITaxonTerm taxon;
	private NCBITaxonTerm taxon2;
	private NCBITaxonTerm obsoleteTaxon;
	private Note relatedNote;
	private Note relatedNote2;
	private OffsetDateTime datetime;
	private OffsetDateTime datetime2;
	private DataProvider dataProvider;
	private DataProvider dataProvider2;
	private DataProvider obsoleteDataProvider;
	private Person person;
	private Vocabulary nameTypeVocabulary;
	private Vocabulary synonymScopeVocabulary;
	private Vocabulary relationVocabulary;
	private VocabularyTerm symbolNameType;
	private VocabularyTerm fullNameType;
	private VocabularyTerm obsoleteNameType;
	private VocabularyTerm obsoleteSymbolNameType;
	private VocabularyTerm obsoleteFullNameType;
	private VocabularyTerm systematicNameType;
	private VocabularyTerm exactSynonymScope;
	private VocabularyTerm broadSynonymScope;
	private VocabularyTerm obsoleteSynonymScope;
	private VocabularyTerm isRegulatedByRelation;
	private VocabularyTerm targetsRelation;
	private VocabularyTerm obsoleteRelation;
	private ConstructComponentSlotAnnotation constructComponent;
	private ConstructSymbolSlotAnnotation constructSymbol;
	private ConstructFullNameSlotAnnotation constructFullName;
	private ConstructSynonymSlotAnnotation constructSynonym;
	private Gene gene;
	private VocabularyTerm geAssociationRelation;
	
	private void loadRequiredEntities() {
		noteTypeVocabulary = getVocabulary(VocabularyConstants.NOTE_TYPE_VOCABULARY);
		alleleNoteTypeVocabulary = getVocabulary(VocabularyConstants.NOTE_TYPE_VOCABULARY);
		noteType = getVocabularyTerm(noteTypeVocabulary, "test_construct_component_note");
		noteType2 = getVocabularyTerm(noteTypeVocabulary, "test_construct_component_summary");
		obsoleteNoteType = addVocabularyTermToSet(VocabularyConstants.CONSTRUCT_COMPONENT_NOTE_TYPES_VOCABULARY_TERM_SET, "obsolete_type", noteTypeVocabulary, true);
		alleleNoteType = getVocabularyTerm(alleleNoteTypeVocabulary, "notes_on_origin");
		relationVocabulary = getVocabulary(VocabularyConstants.CONSTRUCT_RELATION_VOCABULARY);
		isRegulatedByRelation = getVocabularyTerm(relationVocabulary, "is_regulated_by");
		targetsRelation = getVocabularyTerm(relationVocabulary, "targets");
		obsoleteRelation = addVocabularyTermToSet(VocabularyConstants.CONSTRUCT_GENOMIC_ENTITY_RELATION_VOCABULARY_TERM_SET, "obsolete_relation", relationVocabulary, true);
		taxon = getNCBITaxonTerm("NCBITaxon:6239");
		taxon2 = getNCBITaxonTerm("NCBITaxon:9606");
		obsoleteTaxon = getNCBITaxonTerm("NCBITaxon:0000");
		reference = createReference("AGRKB:000020003", false);
		reference2 = createReference("AGRKB:000020005", false);
		relatedNote = createNote(noteType, "Test text", false, reference);
		relatedNote2 = createNote(noteType2, "Test text 2", false, reference2);
		datetime = OffsetDateTime.parse("2022-03-09T22:10:12+00:00");
		datetime2 = OffsetDateTime.parse("2022-04-10T22:10:11+00:00");
		obsoleteReference = createReference("AGRKB:000020000", true);
		dataProvider = createDataProvider("CNST_TEST", false);
		dataProvider2 = createDataProvider("CNST_TEST2", false);
		obsoleteDataProvider = createDataProvider("CNST_ODP", true);
		person = createPerson("TEST:ConstructPerson0001");
		nameTypeVocabulary = getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY);
		synonymScopeVocabulary = getVocabulary(VocabularyConstants.SYNONYM_SCOPE_VOCABULARY);
		symbolNameType = getVocabularyTerm(nameTypeVocabulary, "nomenclature_symbol");
		fullNameType = getVocabularyTerm(nameTypeVocabulary, "full_name");
		systematicNameType = getVocabularyTerm(nameTypeVocabulary, "systematic_name");
		obsoleteNameType = getVocabularyTerm(nameTypeVocabulary, "obsolete_name");
		obsoleteSymbolNameType = getVocabularyTerm(nameTypeVocabulary, "obsolete_symbol_name");
		obsoleteFullNameType = getVocabularyTerm(nameTypeVocabulary, "obsolete_full_name");
		exactSynonymScope = getVocabularyTerm(synonymScopeVocabulary, "exact");
		broadSynonymScope = getVocabularyTerm(synonymScopeVocabulary, "broad");
		obsoleteSynonymScope = getVocabularyTerm(synonymScopeVocabulary, "obsolete");
		constructComponent = createConstructComponentSlotAnnotation(isRegulatedByRelation, List.of(reference), "cc1", taxon, "Caenorhabditis elegans", List.of(relatedNote));
		constructSymbol = createConstructSymbolSlotAnnotation(List.of(reference), "Test symbol", symbolNameType, exactSynonymScope, "https://test.org");
		constructFullName = createConstructFullNameSlotAnnotation(List.of(reference), "Test name", fullNameType, exactSynonymScope, "https://test.org");
		constructSynonym = createConstructSynonymSlotAnnotation(List.of(reference), "Test synonym", systematicNameType, exactSynonymScope, "https://test.org");
		VocabularyTerm symbolNameType = getVocabularyTerm(nameTypeVocabulary, "nomenclature_symbol");
		gene = createGene("TEST:AssociatedGenomicEntity1", "NCBITaxon:6239", symbolNameType, false);
		Vocabulary relationVocabulary = getVocabulary(VocabularyConstants.CONSTRUCT_RELATION_VOCABULARY);
		geAssociationRelation = getVocabularyTerm(relationVocabulary, "is_regulated_by");
	}
	
	@Test
	@Order(1)
	public void createValidConstruct() {
		loadRequiredEntities();
		
		Construct construct = new Construct();
		construct.setModEntityId(CONSTRUCT);
		construct.setConstructSymbol(constructSymbol);
		construct.setConstructFullName(constructFullName);
		construct.setConstructSynonyms(List.of(constructSynonym));
		construct.setReferences(List.of(reference));
		construct.setDateCreated(datetime);
		construct.setConstructComponents(List.of(constructComponent));
		construct.setDataProvider(dataProvider);
		construct.setSecondaryIdentifiers(List.of("secondaryIdTest"));
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			post("/api/construct").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get("/api/construct/" + CONSTRUCT).
			then().
			statusCode(200).
			body("entity.modEntityId", is(CONSTRUCT)).
			body("entity.constructSymbol.displayText", is(constructSymbol.getDisplayText())).
			body("entity.constructSymbol.formatText", is(constructSymbol.getFormatText())).
			body("entity.constructSymbol.nameType.name", is(constructSymbol.getNameType().getName())).
			body("entity.constructSymbol.synonymScope.name", is(constructSymbol.getSynonymScope().getName())).
			body("entity.constructSymbol.synonymUrl", is(constructSymbol.getSynonymUrl())).
			body("entity.constructSymbol.evidence[0].curie", is(constructSymbol.getEvidence().get(0).getCurie())).
			body("entity.constructFullName.displayText", is(constructFullName.getDisplayText())).
			body("entity.constructFullName.formatText", is(constructFullName.getFormatText())).
			body("entity.constructFullName.nameType.name", is(constructFullName.getNameType().getName())).
			body("entity.constructFullName.synonymScope.name", is(constructFullName.getSynonymScope().getName())).
			body("entity.constructFullName.synonymUrl", is(constructFullName.getSynonymUrl())).
			body("entity.constructFullName.evidence[0].curie", is(constructFullName.getEvidence().get(0).getCurie())).
			body("entity.constructSynonyms[0].displayText", is(constructSynonym.getDisplayText())).
			body("entity.constructSynonyms[0].formatText", is(constructSynonym.getFormatText())).
			body("entity.constructSynonyms[0].nameType.name", is(constructSynonym.getNameType().getName())).
			body("entity.constructSynonyms[0].synonymScope.name", is(constructSynonym.getSynonymScope().getName())).
			body("entity.constructSynonyms[0].synonymUrl", is(constructSynonym.getSynonymUrl())).
			body("entity.constructSynonyms[0].evidence[0].curie", is(constructSynonym.getEvidence().get(0).getCurie())).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.references[0].curie", is(reference.getCurie())).
			body("entity.dateCreated", is(datetime.toString())).
			body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.dataProvider.sourceOrganization.abbreviation", is(dataProvider.getSourceOrganization().getAbbreviation())).
			body("entity.constructComponents", hasSize(1)).
			body("entity.constructComponents[0].componentSymbol", is("cc1")).
			body("entity.constructComponents[0].relation.name", is(isRegulatedByRelation.getName())).
			body("entity.constructComponents[0].taxon.curie", is(taxon.getCurie())).
			body("entity.constructComponents[0].taxonText", is("Caenorhabditis elegans")).
			body("entity.constructComponents[0].relatedNotes", hasSize(1)).
			body("entity.constructComponents[0].relatedNotes[0].noteType.name", is(relatedNote.getNoteType().getName())).
			body("entity.constructComponents[0].relatedNotes[0].freeText", is(relatedNote.getFreeText())).
			body("entity.constructComponents[0].relatedNotes[0].internal", is(false)).
			body("entity.constructComponents[0].relatedNotes[0].obsolete", is(false)).
			body("entity.constructComponents[0].relatedNotes[0].references", hasSize(1)).
			body("entity.constructComponents[0].relatedNotes[0].references[0].curie", is(reference.getCurie())).
			body("entity.constructComponents[0].evidence", hasSize(1)).
			body("entity.constructComponents[0].evidence[0].curie", is(reference.getCurie())).
			body("entity.secondaryIdentifiers", hasSize(1)).
			body("entity.secondaryIdentifiers[0]", is("secondaryIdTest"));
	}

	@Test
	@Order(2)
	public void editConstruct() {
		Construct construct = getConstruct(CONSTRUCT);
		construct.setCreatedBy(person);
		construct.setReferences(List.of(reference2));
		construct.setDateCreated(datetime2);
		construct.setDataProvider(dataProvider2);
		construct.setInternal(true);
		construct.setObsolete(true);
		construct.setSecondaryIdentifiers(List.of("secondaryIdTestUpdate"));
	
		
		ConstructComponentSlotAnnotation component = construct.getConstructComponents().get(0);
		component.setComponentSymbol("cc2");
		component.setRelation(targetsRelation);
		component.setTaxon(taxon2);
		component.setTaxonText("Homo sapiens");
		component.setEvidence(List.of(reference2));
		component.setRelatedNotes(List.of(relatedNote2));
		construct.setConstructComponents(List.of(component));
		
		ConstructSymbolSlotAnnotation editedSymbol = construct.getConstructSymbol();
		editedSymbol.setDisplayText("EditedDisplay");
		editedSymbol.setFormatText("EditedFormat");
		editedSymbol.setNameType(systematicNameType);
		editedSymbol.setSynonymScope(broadSynonymScope);
		editedSymbol.setSynonymUrl("https://test2.org");
		editedSymbol.setEvidence(List.of(reference2));
		construct.setConstructSymbol(editedSymbol);
		
		ConstructFullNameSlotAnnotation editedFullName = construct.getConstructFullName();
		editedFullName.setDisplayText("EditedDisplay");
		editedFullName.setFormatText("EditedFormat");
		editedFullName.setSynonymScope(broadSynonymScope);
		editedFullName.setSynonymUrl("https://test2.org");
		editedFullName.setEvidence(List.of(reference2));
		construct.setConstructFullName(editedFullName);
		
		ConstructSynonymSlotAnnotation editedSynonym = construct.getConstructSynonyms().get(0);
		editedSynonym.setDisplayText("EditedDisplay");
		editedSynonym.setFormatText("EditedFormat");
		editedSynonym.setNameType(fullNameType);
		editedSynonym.setSynonymScope(broadSynonymScope);
		editedSynonym.setSynonymUrl("https://test2.org");
		editedSynonym.setEvidence(List.of(reference2));
		construct.setConstructSynonyms(List.of(editedSynonym));
		
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			put("/api/construct").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get("/api/construct/" + CONSTRUCT).
			then().
			statusCode(200).
			body("entity.modEntityId", is(CONSTRUCT)).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.references[0].curie", is(reference2.getCurie())).
			body("entity.dateCreated", is(datetime2.toString())).
			body("entity.createdBy.uniqueId", is(person.getUniqueId())).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.dataProvider.sourceOrganization.abbreviation", is(dataProvider2.getSourceOrganization().getAbbreviation())).
			body("entity.constructSymbol.displayText", is(editedSymbol.getDisplayText())).
			body("entity.constructSymbol.formatText", is(editedSymbol.getFormatText())).
			body("entity.constructSymbol.nameType.name", is(editedSymbol.getNameType().getName())).
			body("entity.constructSymbol.synonymScope.name", is(editedSymbol.getSynonymScope().getName())).
			body("entity.constructSymbol.synonymUrl", is(editedSymbol.getSynonymUrl())).
			body("entity.constructSymbol.evidence[0].curie", is(editedSymbol.getEvidence().get(0).getCurie())).
			body("entity.constructFullName.displayText", is(editedFullName.getDisplayText())).
			body("entity.constructFullName.formatText", is(editedFullName.getFormatText())).
			body("entity.constructFullName.nameType.name", is(editedFullName.getNameType().getName())).
			body("entity.constructFullName.synonymScope.name", is(editedFullName.getSynonymScope().getName())).
			body("entity.constructFullName.synonymUrl", is(editedFullName.getSynonymUrl())).
			body("entity.constructFullName.evidence[0].curie", is(editedFullName.getEvidence().get(0).getCurie())).
			body("entity.constructSynonyms[0].displayText", is(editedSynonym.getDisplayText())).
			body("entity.constructSynonyms[0].formatText", is(editedSynonym.getFormatText())).
			body("entity.constructSynonyms[0].nameType.name", is(editedSynonym.getNameType().getName())).
			body("entity.constructSynonyms[0].synonymScope.name", is(editedSynonym.getSynonymScope().getName())).
			body("entity.constructSynonyms[0].synonymUrl", is(editedSynonym.getSynonymUrl())).
			body("entity.constructSynonyms[0].evidence[0].curie", is(editedSynonym.getEvidence().get(0).getCurie())).
			body("entity.constructComponents", hasSize(1)).
			body("entity.constructComponents[0].componentSymbol", is("cc2")).
			body("entity.constructComponents[0].relation.name", is(targetsRelation.getName())).
			body("entity.constructComponents[0].taxon.curie", is(taxon2.getCurie())).
			body("entity.constructComponents[0].taxonText", is("Homo sapiens")).
			body("entity.constructComponents[0].relatedNotes", hasSize(1)).
			body("entity.constructComponents[0].relatedNotes[0].noteType.name", is(relatedNote2.getNoteType().getName())).
			body("entity.constructComponents[0].relatedNotes[0].freeText", is(relatedNote2.getFreeText())).
			body("entity.constructComponents[0].relatedNotes[0].internal", is(false)).
			body("entity.constructComponents[0].relatedNotes[0].obsolete", is(false)).
			body("entity.constructComponents[0].relatedNotes[0].references", hasSize(1)).
			body("entity.constructComponents[0].relatedNotes[0].references[0].curie", is(reference2.getCurie())).
			body("entity.constructComponents[0].evidence", hasSize(1)).
			body("entity.constructComponents[0].evidence[0].curie", is(reference2.getCurie())).
			body("entity.secondaryIdentifiers", hasSize(1)).
			body("entity.secondaryIdentifiers[0]", is("secondaryIdTestUpdate"));
	}
	
	@Test
	@Order(3)
	public void createConstructWithMissingRequiredFieldsLevel1() {
		Construct construct = new Construct();
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			post("/api/construct").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(2))).
			body("errorMessages.modInternalId", is(ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + "modEntityId")).
			body("errorMessages.constructSymbol", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(4)
	public void editConstructWithMissingRequiredFieldsLevel1() {
		Construct construct = getConstruct(CONSTRUCT);
		construct.setConstructSymbol(null);
		construct.setDataProvider(null);
		construct.setModEntityId(null);
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			put("/api/construct").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(3))).
			body("errorMessages.constructSymbol", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.modInternalId", is(ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + "modEntityId")).
			body("errorMessages.dataProvider", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(5)
	public void createConstructWithMissingRequiredFieldsLevel2() {
		Construct construct = new Construct();
		construct.setDataProvider(dataProvider);
		construct.setModEntityId("Construct:0005");
		
		ConstructComponentSlotAnnotation invalidComponent = new ConstructComponentSlotAnnotation();
		ConstructSymbolSlotAnnotation invalidSymbol = new ConstructSymbolSlotAnnotation();
		ConstructFullNameSlotAnnotation invalidFullName = new ConstructFullNameSlotAnnotation();
		ConstructSynonymSlotAnnotation invalidSynonym = new ConstructSynonymSlotAnnotation();
		construct.setConstructComponents(List.of(invalidComponent));
		construct.setConstructSymbol(invalidSymbol);
		construct.setConstructFullName(invalidFullName);
		construct.setConstructSynonyms(List.of(invalidSynonym));
		
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			post("/api/construct").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(4))).
			body("errorMessages.constructComponents", is(String.join(" | ", List.of(
					"componentSymbol - " + ValidationConstants.REQUIRED_MESSAGE,
					"relation - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.constructSymbol", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE,
					"nameType - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.constructFullName", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE,
					"nameType - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.constructSynonyms", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE,
					"nameType - " + ValidationConstants.REQUIRED_MESSAGE))));
	}
	
	@Test
	@Order(6)
	public void editConstructWithMissingRequiredFieldsLevel2() {
		Construct construct = getConstruct(CONSTRUCT);
		ConstructComponentSlotAnnotation invalidComponent = construct.getConstructComponents().get(0);
		invalidComponent.setComponentSymbol(null);
		invalidComponent.setRelation(null);
		ConstructSymbolSlotAnnotation invalidSymbol = construct.getConstructSymbol();
		invalidSymbol.setDisplayText(null);
		invalidSymbol.setFormatText(null);
		invalidSymbol.setNameType(null);
		ConstructFullNameSlotAnnotation invalidFullName = construct.getConstructFullName();
		invalidFullName.setDisplayText(null);
		invalidFullName.setFormatText(null);
		invalidFullName.setNameType(null);
		ConstructSynonymSlotAnnotation invalidSynonym = construct.getConstructSynonyms().get(0);
		invalidSynonym.setDisplayText(null);
		invalidSynonym.setFormatText(null);
		invalidSynonym.setNameType(null);
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			put("/api/construct").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(4))).
			body("errorMessages.constructComponents", is(String.join(" | ", List.of(
					"componentSymbol - " + ValidationConstants.REQUIRED_MESSAGE,
					"relation - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.constructSymbol", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE,
					"nameType - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.constructFullName", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE,
					"nameType - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.constructSynonyms", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE,
					"nameType - " + ValidationConstants.REQUIRED_MESSAGE))));
	}
	
	@Test
	@Order(7)
	public void createConstructWithEmptyRequiredFields() {
		Construct construct = new Construct();
		
		construct.setModInternalId("");
		construct.setDataProvider(dataProvider);
		ConstructComponentSlotAnnotation invalidComponent = createConstructComponentSlotAnnotation(isRegulatedByRelation, List.of(reference), "", taxon, "C. elegans", List.of(relatedNote));
		construct.setConstructComponents(List.of(invalidComponent));
		ConstructSymbolSlotAnnotation invalidSymbol = createConstructSymbolSlotAnnotation(null, "", symbolNameType, null, null);
		construct.setConstructSymbol(invalidSymbol);
		ConstructFullNameSlotAnnotation invalidFullName = createConstructFullNameSlotAnnotation(null, "", fullNameType, null, null);
		construct.setConstructFullName(invalidFullName);
		ConstructSynonymSlotAnnotation invalidSynonym = createConstructSynonymSlotAnnotation(null, "", systematicNameType, null, null);
		construct.setConstructSynonyms(List.of(invalidSynonym));
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			post("/api/construct").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(5))).
			body("errorMessages.modInternalId", is(ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + "modEntityId")).
			body("errorMessages.constructSymbol", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.constructFullName", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.constructSynonyms", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.constructComponents", is("componentSymbol - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(8)
	public void editConstructWithEmptyRequiredFields() {
		Construct construct = getConstruct(CONSTRUCT);
		construct.setModEntityId("");
		construct.setModInternalId("");
		ConstructComponentSlotAnnotation component = construct.getConstructComponents().get(0);
		component.setComponentSymbol("");
		construct.setConstructComponents(List.of(component));
		ConstructSymbolSlotAnnotation invalidSymbol = construct.getConstructSymbol();
		invalidSymbol.setDisplayText("");
		invalidSymbol.setFormatText("");
		ConstructFullNameSlotAnnotation invalidFullName = construct.getConstructFullName();
		invalidFullName.setDisplayText("");
		invalidFullName.setFormatText("");
		ConstructSynonymSlotAnnotation invalidSynonym = construct.getConstructSynonyms().get(0);
		invalidSynonym.setDisplayText("");
		invalidSynonym.setFormatText("");
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			put("/api/construct").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(5))).
			body("errorMessages.modInternalId", is(ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + "modEntityId")).
			body("errorMessages.constructSymbol", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.constructFullName", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.constructSynonyms", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.constructComponents", is("componentSymbol - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(9)
	public void createConstructWithInvalidFields() {
		NCBITaxonTerm nonPersistedTaxon = new NCBITaxonTerm();
		nonPersistedTaxon.setCurie("NCBITaxon:Invalid");
		Reference nonPersistedReference = new Reference();
		nonPersistedReference.setCurie("AGRKB:Invalid");
		Organization nonPersistedOrganization = new Organization();
		nonPersistedOrganization.setAbbreviation("INV");
		DataProvider invalidDataProvider = new DataProvider();
		invalidDataProvider.setSourceOrganization(nonPersistedOrganization);
		
		Note invalidNote = new Note();
		invalidNote.setNoteType(alleleNoteType);
		invalidNote.setReferences(List.of(nonPersistedReference));
		invalidNote.setFreeText("invalid");
		
		ConstructComponentSlotAnnotation invalidComponent = createConstructComponentSlotAnnotation(fullNameType, List.of(nonPersistedReference), "invComp", nonPersistedTaxon, null, List.of(invalidNote));
		ConstructSymbolSlotAnnotation invalidSymbol = createConstructSymbolSlotAnnotation(List.of(nonPersistedReference), "Test symbol", fullNameType, alleleNoteType, "https://test.org");
		ConstructFullNameSlotAnnotation invalidFullName = createConstructFullNameSlotAnnotation(List.of(nonPersistedReference), "Test name", symbolNameType, alleleNoteType, "https://test.org");
		ConstructSynonymSlotAnnotation invalidSynonym = createConstructSynonymSlotAnnotation(List.of(nonPersistedReference), "Test synonym", alleleNoteType, alleleNoteType, "https://test.org");
		
		
		Construct construct = new Construct();
		construct.setModEntityId("Construct:0009");
		construct.setReferences(List.of(nonPersistedReference));
		construct.setDateCreated(datetime);
		construct.setDataProvider(invalidDataProvider);
		construct.setConstructComponents(List.of(invalidComponent));
		construct.setConstructSymbol(invalidSymbol);
		construct.setConstructFullName(invalidFullName);
		construct.setConstructSynonyms(List.of(invalidSynonym));
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			post("/api/construct").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.dataProvider", is("sourceOrganization - " + ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.references", is("curie - " + ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.constructComponents", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"relatedNotes - " + String.join(" | ", List.of(
							"noteType - " + ValidationConstants.INVALID_MESSAGE,
							"references - " + ValidationConstants.INVALID_MESSAGE
							)),
					"relation - " + ValidationConstants.INVALID_MESSAGE,
					"taxon - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.constructSymbol", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nameType - " + ValidationConstants.INVALID_MESSAGE,
					"synonymScope - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.constructFullName", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nameType - " + ValidationConstants.INVALID_MESSAGE,
					"synonymScope - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.constructSynonyms", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nameType - " + ValidationConstants.INVALID_MESSAGE,
					"synonymScope - " + ValidationConstants.INVALID_MESSAGE))));
	}
	
	@Test
	@Order(10)
	public void editConstructWithInvalidFields() {
		NCBITaxonTerm nonPersistedTaxon = new NCBITaxonTerm();
		nonPersistedTaxon.setCurie("NCBITaxon:Invalid");
		Reference nonPersistedReference = new Reference();
		nonPersistedReference.setCurie("AGRKB:Invalid");
		Organization nonPersistedOrganization = new Organization();
		nonPersistedOrganization.setAbbreviation("INV");
		DataProvider invalidDataProvider = new DataProvider();
		invalidDataProvider.setSourceOrganization(nonPersistedOrganization);
		
		Note invalidNote = new Note();
		invalidNote.setNoteType(alleleNoteType);
		invalidNote.setReferences(List.of(nonPersistedReference));
		invalidNote.setFreeText("invalid");
		
		Construct construct = getConstruct(CONSTRUCT);
		construct.setReferences(List.of(nonPersistedReference));
		construct.setDataProvider(invalidDataProvider);
		ConstructComponentSlotAnnotation component = construct.getConstructComponents().get(0);
		component.setRelation(alleleNoteType);
		component.setEvidence(List.of(nonPersistedReference));
		component.setRelatedNotes(List.of(invalidNote));
		component.setTaxon(nonPersistedTaxon);
		construct.setConstructComponents(List.of(component));
		ConstructSymbolSlotAnnotation invalidSymbol = construct.getConstructSymbol();
		invalidSymbol.setEvidence(List.of(nonPersistedReference));
		invalidSymbol.setNameType(fullNameType);
		invalidSymbol.setSynonymScope(alleleNoteType);
		construct.setConstructSymbol(invalidSymbol);
		ConstructFullNameSlotAnnotation invalidFullName = construct.getConstructFullName();
		invalidFullName.setEvidence(List.of(nonPersistedReference));
		invalidFullName.setNameType(symbolNameType);
		invalidFullName.setSynonymScope(noteType);
		construct.setConstructFullName(invalidFullName);
		ConstructSynonymSlotAnnotation invalidSynonym = construct.getConstructSynonyms().get(0);
		invalidSynonym.setEvidence(List.of(nonPersistedReference));
		invalidSynonym.setNameType(alleleNoteType);
		invalidSynonym.setSynonymScope(symbolNameType);
		construct.setConstructSynonyms(List.of(invalidSynonym));
		
		RestAssured.given().
		contentType("application/json").
		body(construct).
		when().
		put("/api/construct").
		then().
		statusCode(400).
		body("errorMessages", is(aMapWithSize(6))).
		body("errorMessages.dataProvider", is("sourceOrganization - " + ValidationConstants.INVALID_MESSAGE)).
		body("errorMessages.references", is("curie - " + ValidationConstants.INVALID_MESSAGE)).
		body("errorMessages.constructComponents", is(String.join(" | ", List.of(
				"evidence - " + ValidationConstants.INVALID_MESSAGE,
				"relatedNotes - " + String.join(" | ", List.of(
						"noteType - " + ValidationConstants.INVALID_MESSAGE,
						"references - " + ValidationConstants.INVALID_MESSAGE
						)),
				"relation - " + ValidationConstants.INVALID_MESSAGE,
				"taxon - " + ValidationConstants.INVALID_MESSAGE)))).
		body("errorMessages.constructSymbol", is(String.join(" | ", List.of(
				"evidence - " + ValidationConstants.INVALID_MESSAGE,
				"nameType - " + ValidationConstants.INVALID_MESSAGE,
				"synonymScope - " + ValidationConstants.INVALID_MESSAGE)))).
		body("errorMessages.constructFullName", is(String.join(" | ", List.of(
				"evidence - " + ValidationConstants.INVALID_MESSAGE,
				"nameType - " + ValidationConstants.INVALID_MESSAGE,
				"synonymScope - " + ValidationConstants.INVALID_MESSAGE)))).
		body("errorMessages.constructSynonyms", is(String.join(" | ", List.of(
				"evidence - " + ValidationConstants.INVALID_MESSAGE,
				"nameType - " + ValidationConstants.INVALID_MESSAGE,
				"synonymScope - " + ValidationConstants.INVALID_MESSAGE))));
	}
	
	@Test
	@Order(11)
	public void createConstructWithObsoleteFields() {
		
		Note obsoleteNote = new Note();
		obsoleteNote.setFreeText("obsolete note type");
		obsoleteNote.setNoteType(obsoleteNoteType);
		
		ConstructComponentSlotAnnotation obsoleteComponent = createConstructComponentSlotAnnotation(obsoleteRelation, List.of(obsoleteReference), "invComp", obsoleteTaxon, null, List.of(obsoleteNote));
		ConstructSymbolSlotAnnotation obsoleteSymbol = createConstructSymbolSlotAnnotation(List.of(obsoleteReference), "Test symbol", obsoleteSymbolNameType, obsoleteSynonymScope, "https://test.org");
		ConstructFullNameSlotAnnotation obsoleteFullName = createConstructFullNameSlotAnnotation(List.of(obsoleteReference), "Test name", obsoleteFullNameType, obsoleteSynonymScope, "https://test.org");
		ConstructSynonymSlotAnnotation obsoleteSynonym = createConstructSynonymSlotAnnotation(List.of(obsoleteReference), "Test synonym", obsoleteNameType, obsoleteSynonymScope, "https://test.org");
		
		Construct construct = new Construct();
		construct.setModEntityId("Construct:0011");
		construct.setReferences(List.of(obsoleteReference));
		construct.setDateCreated(datetime);
		construct.setDataProvider(obsoleteDataProvider);
		construct.setConstructComponents(List.of(obsoleteComponent));
		construct.setConstructSymbol(obsoleteSymbol);
		construct.setConstructFullName(obsoleteFullName);
		construct.setConstructSynonyms(List.of(obsoleteSynonym));
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			post("/api/construct").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.dataProvider", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.references", is("curie - " + ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.constructComponents", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"relatedNotes - noteType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"relation - " + ValidationConstants.OBSOLETE_MESSAGE,
					"taxon - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.constructSymbol", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"nameType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"synonymScope - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.constructFullName", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"nameType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"synonymScope - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.constructSynonyms", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"nameType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"synonymScope - " + ValidationConstants.OBSOLETE_MESSAGE))));
	}
	
	@Test
	@Order(12)
	public void editConstructWithObsoleteFields() {
		Note obsoleteNote = new Note();
		obsoleteNote.setFreeText("obsolete note type");
		obsoleteNote.setNoteType(obsoleteNoteType);
		
		Construct construct = getConstruct(CONSTRUCT);
		construct.setReferences(List.of(obsoleteReference));
		construct.setDataProvider(obsoleteDataProvider);
		
		ConstructComponentSlotAnnotation component = construct.getConstructComponents().get(0);
		component.setRelation(obsoleteRelation);
		component.setEvidence(List.of(obsoleteReference));
		component.setRelatedNotes(List.of(obsoleteNote));
		component.setTaxon(obsoleteTaxon);
		ConstructSymbolSlotAnnotation obsoleteSymbol = construct.getConstructSymbol();
		obsoleteSymbol.setEvidence(List.of(obsoleteReference));
		obsoleteSymbol.setNameType(obsoleteSymbolNameType);
		obsoleteSymbol.setSynonymScope(obsoleteSynonymScope);
		ConstructFullNameSlotAnnotation obsoleteFullName = construct.getConstructFullName();
		obsoleteFullName.setEvidence(List.of(obsoleteReference));
		obsoleteFullName.setNameType(obsoleteFullNameType);
		obsoleteFullName.setSynonymScope(obsoleteSynonymScope);
		ConstructSynonymSlotAnnotation obsoleteSynonym = construct.getConstructSynonyms().get(0);
		obsoleteSynonym.setEvidence(List.of(obsoleteReference));
		obsoleteSynonym.setNameType(obsoleteNameType);
		obsoleteSynonym.setSynonymScope(obsoleteSynonymScope);
		
		
		construct.setConstructComponents(List.of(component));
		construct.setConstructSymbol(obsoleteSymbol);
		construct.setConstructFullName(obsoleteFullName);
		construct.setConstructSynonyms(List.of(obsoleteSynonym));
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			put("/api/construct").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.dataProvider", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.references", is("curie - " + ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.constructComponents", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"relatedNotes - noteType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"relation - " + ValidationConstants.OBSOLETE_MESSAGE,
					"taxon - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.constructSymbol", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"nameType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"synonymScope - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.constructFullName", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"nameType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"synonymScope - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.constructSynonyms", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"nameType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"synonymScope - " + ValidationConstants.OBSOLETE_MESSAGE))));
	}
	
	@Test
	@Order(13)
	public void editConstructWithNullNonRequiredFieldsLevel2() {
		// Level 2 done before 1 to avoid having to restore nulled fields
		Construct construct = getConstruct(CONSTRUCT);
		
		ConstructComponentSlotAnnotation component = construct.getConstructComponents().get(0);
		component.setEvidence(null);
		component.setTaxon(null);
		component.setTaxonText(null);
		component.setRelatedNotes(null);
		
		ConstructSymbolSlotAnnotation editedSymbol = construct.getConstructSymbol();
		editedSymbol.setEvidence(null);
		editedSymbol.setSynonymScope(null);
		editedSymbol.setSynonymUrl(null);
		
		ConstructFullNameSlotAnnotation editedFullName = construct.getConstructFullName();
		editedFullName.setEvidence(null);
		editedFullName.setSynonymScope(null);
		editedFullName.setSynonymUrl(null);
		
		ConstructSynonymSlotAnnotation editedSynonym = construct.getConstructSynonyms().get(0);
		editedSynonym.setEvidence(null);
		editedSynonym.setSynonymScope(null);
		editedSynonym.setSynonymUrl(null);
		
		construct.setConstructComponents(List.of(component));
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			put("/api/construct").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get("/api/construct/" + CONSTRUCT).
			then().
			statusCode(200).
			body("entity", hasKey("constructComponents")).
			body("entity.constructComponents[0]", not(hasKey("evidence"))).
			body("entity.constructComponents[0]", not(hasKey("taxon"))).
			body("entity.constructComponents[0]", not(hasKey("taxonText"))).
			body("entity.constructComponents[0]", not(hasKey("relatedNotes"))).
			body("entity.constructSymbol", not(hasKey("evidence"))).
			body("entity.constructSymbol", not(hasKey("synonymScope"))).
			body("entity.constructSymbol", not(hasKey("synonymUrl"))).
			body("entity.constructFullName", not(hasKey("evidence"))).
			body("entity.constructFullName", not(hasKey("synonymScope"))).
			body("entity.constructFullName", not(hasKey("synonymUrl"))).
			body("entity.constructSynonyms[0]", not(hasKey("evidence"))).
			body("entity.constructSynonyms[0]", not(hasKey("synonymScope"))).
			body("entity.constructSynonyms[0]", not(hasKey("synonymUrl")));
			
	}
	
	@Test
	@Order(14)
	public void editConstructWithNullNonRequiredFieldsLevel1() {
		Construct construct = getConstruct(CONSTRUCT);
		
		construct.setConstructComponents(null);
		construct.setReferences(null);
		construct.setConstructFullName(null);
		construct.setConstructSynonyms(null);
		construct.setSecondaryIdentifiers(null);
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			put("/api/construct").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get("/api/construct/" + CONSTRUCT).
			then().
			statusCode(200).
			body("entity", not(hasKey("constructComponents"))).
			body("entity", not(hasKey("constructFullName"))).
			body("entity", not(hasKey("constructSynonyms"))).
			body("entity", not(hasKey("references"))).
			body("entity", not(hasKey("secondaryIdentifiers")));
	}
	
	@Test
	@Order(15)
	public void createConstructWithOnlyRequiredFieldsLevel1() {
		Construct construct = new Construct();
		construct.setConstructSymbol(constructSymbol);
		construct.setDataProvider(dataProvider);
		construct.setModEntityId("Construct:0015");
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			post("/api/construct").
			then().
			statusCode(200);
	}
	
	@Test
	@Order(16)
	public void createConstructWithOnlyRequiredFieldsLevel2() {
		Construct construct = new Construct();
		construct.setConstructSymbol(constructSymbol);
		construct.setDataProvider(dataProvider);
		construct.setModEntityId("Construct:0016");
		
		ConstructComponentSlotAnnotation minimalComponent = createConstructComponentSlotAnnotation(isRegulatedByRelation, null, "minimalCmp", null, null, null);
		ConstructSymbolSlotAnnotation minimalConstructSymbol = createConstructSymbolSlotAnnotation(null, "Test symbol", symbolNameType, null, null);
		ConstructFullNameSlotAnnotation minimalConstructFullName = createConstructFullNameSlotAnnotation(null, "Test name", fullNameType, null, null);
		ConstructSynonymSlotAnnotation minimalConstructSynonym = createConstructSynonymSlotAnnotation(null, "Test synonym", systematicNameType, null, null);
		
		construct.setConstructComponents(List.of(minimalComponent));
		construct.setConstructSymbol(minimalConstructSymbol);
		construct.setConstructFullName(minimalConstructFullName);
		construct.setConstructSynonyms(List.of(minimalConstructSynonym));
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			post("/api/construct").
			then().
			statusCode(200);
	}
	
	@Test
	@Order(17)
	public void createConstructWithDuplicateNote() {
		Construct construct = new Construct();
		construct.setConstructSymbol(constructSymbol);
		construct.setDataProvider(dataProvider);
		construct.setModEntityId("Construct:0017");
		
		ConstructComponentSlotAnnotation component = createConstructComponentSlotAnnotation(isRegulatedByRelation, null, "dnCmp", null, null, List.of(relatedNote, relatedNote));
		construct.setConstructComponents(List.of(component));
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			post("/api/construct").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.constructComponents", is("relatedNotes - " + ValidationConstants.DUPLICATE_MESSAGE + " (Test text|AGRKB:000020003|test_construct_component_note|false|false)"));
		
	}
	
	@Test
	@Order(18)
	public void updateConstructWithNewAssociations() {
		Construct construct = getConstruct(CONSTRUCT);
		
		ConstructGenomicEntityAssociation geAssociation = new ConstructGenomicEntityAssociation();
		geAssociation.setConstructGenomicEntityAssociationObject(gene);
		geAssociation.setRelation(geAssociationRelation);
		construct.setConstructGenomicEntityAssociations(List.of(geAssociation));
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			put("/api/construct").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get("/api/construct/" + CONSTRUCT).
			then().
			statusCode(200).
			body("entity", hasKey("constructGenomicEntityAssociations")).
			body("entity.constructGenomicEntityAssociations[0].constructGenomicEntityAssociationObject.modEntityId", is(gene.getModEntityId()));
	}
	
	@Test
	@Order(19)
	public void updateConstructRemoveAssociations() {
		Construct construct = getConstruct(CONSTRUCT);
		
		construct.setConstructGenomicEntityAssociations(null);
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			put("/api/construct").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get("/api/construct/" + CONSTRUCT).
			then().
			statusCode(200).
			body("entity", not(hasKey("constructGenomicEntityAssociations")));
	}
	
	@Test
	@Order(20)
	public void updateConstructWithInvalidAssociations() {
		Construct construct = getConstruct(CONSTRUCT);
		
		ConstructGenomicEntityAssociation geneAssociation = new ConstructGenomicEntityAssociation();
		geneAssociation.setConstructGenomicEntityAssociationObject(gene);
		geneAssociation.setRelation(systematicNameType);
		construct.setConstructGenomicEntityAssociations(List.of(geneAssociation));
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			put("/api/construct").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.constructGenomicEntityAssociations", is("relation - " + ValidationConstants.INVALID_MESSAGE));
	}
	
	private ConstructComponentSlotAnnotation createConstructComponentSlotAnnotation(VocabularyTerm relation, List<InformationContentEntity> evidence, String symbol, NCBITaxonTerm taxon, String taxonText, List<Note> notes) {
		ConstructComponentSlotAnnotation cc = new ConstructComponentSlotAnnotation();
		cc.setComponentSymbol(symbol);
		cc.setRelation(relation);
		cc.setEvidence(evidence);
		cc.setRelatedNotes(notes);
		cc.setTaxon(taxon);
		cc.setTaxonText(taxonText);
		return cc;
	}
	
	private ConstructSymbolSlotAnnotation createConstructSymbolSlotAnnotation(List<InformationContentEntity> evidence, String name, VocabularyTerm nameType, VocabularyTerm synonymScope, String synonymUrl) {
		ConstructSymbolSlotAnnotation symbol = new ConstructSymbolSlotAnnotation();
		symbol.setEvidence(evidence);
		symbol.setDisplayText(name);
		symbol.setFormatText(name);
		symbol.setNameType(nameType);
		symbol.setSynonymScope(synonymScope);
		symbol.setSynonymUrl(synonymUrl);
		
		return symbol;
	}

	private ConstructFullNameSlotAnnotation createConstructFullNameSlotAnnotation(List<InformationContentEntity> evidence, String name, VocabularyTerm nameType, VocabularyTerm synonymScope, String synonymUrl) {
		ConstructFullNameSlotAnnotation fullName = new ConstructFullNameSlotAnnotation();
		fullName.setEvidence(evidence);
		fullName.setDisplayText(name);
		fullName.setFormatText(name);
		fullName.setNameType(nameType);
		fullName.setSynonymScope(synonymScope);
		fullName.setSynonymUrl(synonymUrl);
		
		return fullName;
	}

	private ConstructSynonymSlotAnnotation createConstructSynonymSlotAnnotation(List<InformationContentEntity> evidence, String name, VocabularyTerm nameType, VocabularyTerm synonymScope, String synonymUrl) {
		ConstructSynonymSlotAnnotation synonym = new ConstructSynonymSlotAnnotation();
		synonym.setEvidence(evidence);
		synonym.setDisplayText(name);
		synonym.setFormatText(name);
		synonym.setNameType(nameType);
		synonym.setSynonymScope(synonymScope);
		synonym.setSynonymUrl(synonymUrl);
		
		return synonym;
	}

}
