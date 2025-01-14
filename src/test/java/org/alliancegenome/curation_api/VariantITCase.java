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
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
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
@DisplayName("309 - VariantITCase")
@Order(309)
public class VariantITCase extends BaseITCase {

	private static final String VARIANT = "Variant:0001";
	
	private Vocabulary variantStatusVocabulary;
	private Vocabulary noteTypeVocabulary;
	private VocabularyTerm variantStatus;
	private VocabularyTerm variantStatus2;
	private VocabularyTerm obsoleteVariantStatus;
	private VocabularyTerm noteType;
	private VocabularyTerm noteType2;
	private VocabularyTerm obsoleteNoteType;
	private Reference reference;
	private Reference reference2;
	private Reference obsoleteReference;
	private NCBITaxonTerm taxon;
	private NCBITaxonTerm taxon2;
	private NCBITaxonTerm obsoleteTaxon;
	private Person person;
	private OffsetDateTime datetime;
	private OffsetDateTime datetime2;
	private SOTerm variantTypeTerm;
	private SOTerm variantTypeTerm2;
	private SOTerm sgcTerm;
	private SOTerm sgcTerm2;
	private SOTerm obsoleteSoTerm;
	private Note relatedNote;
	private Organization dataProvider;
	private Organization dataProvider2;
	private Organization obsoleteDataProvider;
	private Organization nonPersistedOrganization;
	
	
	private void loadRequiredEntities() {
		variantStatusVocabulary = getVocabulary(VocabularyConstants.VARIANT_STATUS_VOCABULARY);
		noteTypeVocabulary = getVocabulary(VocabularyConstants.NOTE_TYPE_VOCABULARY);
		variantStatus = getVocabularyTerm(variantStatusVocabulary, "status_test");
		variantStatus2 = getVocabularyTerm(variantStatusVocabulary, "status_test_2");
		obsoleteVariantStatus = createVocabularyTerm(variantStatusVocabulary, "obsolete_status", true);
		noteType = getVocabularyTerm(noteTypeVocabulary, "comment");
		noteType2 = getVocabularyTerm(noteTypeVocabulary, "test_comment");
		obsoleteNoteType = addVocabularyTermToSet(VocabularyConstants.VARIANT_NOTE_TYPES_VOCABULARY_TERM_SET, "obsolete_type", noteTypeVocabulary, true);
		reference = createReference("AGRKB:000000083", false);
		reference2 = createReference("AGRKB:000000085", false);
		obsoleteReference = createReference("AGRKB:000010080", true);
		taxon = getNCBITaxonTerm("NCBITaxon:10090");
		taxon2 = getNCBITaxonTerm("NCBITaxon:9606");
		obsoleteTaxon = getNCBITaxonTerm("NCBITaxon:0000");
		person = createPerson("TEST:VariantPerson0001");
		datetime = OffsetDateTime.parse("2022-03-09T22:10:12+00:00");
		datetime2 = OffsetDateTime.parse("2022-04-10T22:10:11+00:00");
		variantTypeTerm = getSoTerm("SO:VT001");
		variantTypeTerm2 = getSoTerm("SO:VT002");
		sgcTerm = getSoTerm("SO:SGC001");
		sgcTerm2 = getSoTerm("SO:SGC002");
		obsoleteSoTerm = getSoTerm("SO:00000");
		relatedNote = createNote(noteType, "Test text", false, reference);
		dataProvider = createOrganization("VARTEST", false);
		dataProvider2 = createOrganization("VARTEST2", false);
		obsoleteDataProvider = createOrganization("VARODP", true);
		nonPersistedOrganization = new Organization();
		nonPersistedOrganization.setAbbreviation("INV");
	}
	
	@Test
	@Order(1)
	public void createValidVariant() {
		loadRequiredEntities();
		
		Variant variant = new Variant();
		variant.setPrimaryExternalId(VARIANT);
		variant.setTaxon(taxon);
		variant.setVariantType(variantTypeTerm);
		variant.setVariantStatus(variantStatus);
		variant.setSourceGeneralConsequence(sgcTerm);
		variant.setDateCreated(datetime);
		variant.setDataProvider(dataProvider);
		variant.setRelatedNotes(List.of(relatedNote));
		
		RestAssured.given().
			contentType("application/json").
			body(variant).
			when().
			post("/api/variant").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get("/api/variant/" + VARIANT).
			then().
			statusCode(200).
			body("entity.primaryExternalId", is(VARIANT)).
			body("entity.taxon.curie", is(taxon.getCurie())).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.variantType.curie", is(variantTypeTerm.getCurie())).
			body("entity.variantStatus.name", is(variantStatus.getName())).
			body("entity.sourceGeneralConsequence.curie", is(sgcTerm.getCurie())).
			body("entity.dateCreated", is(datetime.toString())).
			body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.relatedNotes", hasSize(1)).
			body("entity.relatedNotes[0].noteType.name", is(relatedNote.getNoteType().getName())).
			body("entity.relatedNotes[0].freeText", is(relatedNote.getFreeText())).
			body("entity.relatedNotes[0].internal", is(false)).
			body("entity.relatedNotes[0].references[0].curie", is(reference.getCurie())).
			body("entity.dataProvider.abbreviation", is(dataProvider.getAbbreviation()));
	}

	@Test
	@Order(2)
	public void editVariant() {
		Variant variant = getVariant(VARIANT);
		variant.setCreatedBy(person);
		variant.setPrimaryExternalId(VARIANT);
		variant.setTaxon(taxon2);
		variant.setVariantType(variantTypeTerm2);
		variant.setVariantStatus(variantStatus2);
		variant.setSourceGeneralConsequence(sgcTerm2);
		variant.setInternal(true);
		variant.setObsolete(true);
		variant.setDateCreated(datetime2);
		variant.setDataProvider(dataProvider2);
		
		Note editedNote = variant.getRelatedNotes().get(0);
		editedNote.setNoteType(noteType2);
		editedNote.setFreeText("Edited text");
		editedNote.setInternal(true);
		editedNote.setReferences(List.of(reference2));
		variant.setRelatedNotes(List.of(editedNote));
		
		RestAssured.given().
			contentType("application/json").
			body(variant).
			when().
			put("/api/variant").
			then().
			statusCode(200);

		RestAssured.given().
			when().
			get("/api/variant/" + VARIANT).
			then().
			statusCode(200).
			body("entity.primaryExternalId", is(VARIANT)).
			body("entity.variantType.curie", is(variantTypeTerm2.getCurie())).
			body("entity.variantStatus.name", is(variantStatus2.getName())).
			body("entity.sourceGeneralConsequence.curie", is(sgcTerm2.getCurie())).
			body("entity.dateCreated", is(datetime2.toString())).
			body("entity.createdBy.uniqueId", is(person.getUniqueId())).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.taxon.curie", is(taxon2.getCurie())).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.createdBy.uniqueId", is(person.getUniqueId())).
			body("entity.relatedNotes", hasSize(1)).
			body("entity.relatedNotes[0].noteType.name", is(editedNote.getNoteType().getName())).
			body("entity.relatedNotes[0].freeText", is(editedNote.getFreeText())).
			body("entity.relatedNotes[0].internal", is(true)).
			body("entity.relatedNotes[0].references[0].curie", is(reference2.getCurie())).
			body("entity.dataProvider.abbreviation", is(dataProvider2.getAbbreviation()));
	}
	
	@Test
	@Order(3)
	public void createVariantWithMissingRequiredFieldsLevel1() {
		Variant variant = new Variant();
		
		RestAssured.given().
			contentType("application/json").
			body(variant).
			when().
			post("/api/variant").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(3))).
			body("errorMessages.modInternalId", is(ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + "primaryExternalId")).
			body("errorMessages.taxon", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.variantType", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(4)
	public void editVariantWithMissingPrimaryExternalId() {
		Variant variant = getVariant(VARIANT);
		variant.setPrimaryExternalId(null);
		
		RestAssured.given().
			contentType("application/json").
			body(variant).
			when().
			put("/api/variant").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.modInternalId", is(ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + "primaryExternalId"));
	}
	
	@Test
	@Order(5)
	public void editVariantWithMissingRequiredFieldsLevel1() {
		Variant variant = getVariant(VARIANT);
		variant.setTaxon(null);
		variant.setVariantType(null);
		variant.setDataProvider(null);
		
		RestAssured.given().
			contentType("application/json").
			body(variant).
			when().
			put("/api/variant").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(3))).
			body("errorMessages.taxon", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.variantType", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(6)
	public void createVariantWithEmptyRequiredFields() {
		Variant variant = new Variant();
		variant.setPrimaryExternalId("");
		variant.setTaxon(taxon);
		variant.setVariantType(variantTypeTerm);
		RestAssured.given().
			contentType("application/json").
			body(variant).
			when().
			post("/api/variant").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.modInternalId", is(ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + "primaryExternalId"));
	}
	
	@Test
	@Order(7)
	public void editVariantWithEmptyPrimaryExternalId() {
		Variant variant = getVariant(VARIANT);
		variant.setPrimaryExternalId("");
		
		RestAssured.given().
			contentType("application/json").
			body(variant).
			when().
			put("/api/variant").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.modInternalId", is(ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + "primaryExternalId"));
	}
	
	@Test
	@Order(8)
	public void createVariantWithMissingRequiredFieldsLevel2() {
		Variant variant = new Variant();
		variant.setPrimaryExternalId("Variant:0008");
		variant.setTaxon(taxon);
		variant.setVariantType(variantTypeTerm);
		
		Note invalidNote = new Note();
		
		variant.setRelatedNotes(List.of(invalidNote));
		
		RestAssured.given().
			contentType("application/json").
			body(variant).
			when().
			post("/api/variant").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"freeText - " + ValidationConstants.REQUIRED_MESSAGE,
					"noteType - " + ValidationConstants.REQUIRED_MESSAGE))));
	}
	
	@Test
	@Order(9)
	public void editVariantWithMissingRequiredFieldsLevel2() {
		Variant variant = getVariant(VARIANT);
		
		Note invalidNote = variant.getRelatedNotes().get(0);
		invalidNote.setNoteType(null);
		invalidNote.setFreeText(null);
		variant.setRelatedNotes(List.of(invalidNote));
		
		RestAssured.given().
			contentType("application/json").
			body(variant).
			when().
			put("/api/variant").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"freeText - " + ValidationConstants.REQUIRED_MESSAGE,
					"noteType - " + ValidationConstants.REQUIRED_MESSAGE))));
	}
	
	@Test
	@Order(10)
	public void createVariantWithEmptyRequiredFieldsLevel2() {
		Variant variant = new Variant();
		variant.setPrimaryExternalId("Variant:0010");
		variant.setTaxon(taxon);
		variant.setVariantType(variantTypeTerm);
		
		Note invalidNote = new Note();
		invalidNote.setNoteType(noteType);
		invalidNote.setFreeText("");
		variant.setRelatedNotes(List.of(invalidNote));
		
		RestAssured.given().
			contentType("application/json").
			body(variant).
			when().
			post("/api/variant").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.relatedNotes", is("freeText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(11)
	public void editVariantWithEmptyRequiredFieldsLevel2() {
		Variant variant = getVariant(VARIANT);
		
		Note invalidNote = variant.getRelatedNotes().get(0);
		invalidNote.setFreeText("");
		
		RestAssured.given().
			contentType("application/json").
			body(variant).
			when().
			post("/api/variant").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.relatedNotes", is("freeText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(12)
	public void createVariantWithInvalidFields() {
		NCBITaxonTerm nonPersistedTaxon = new NCBITaxonTerm();
		nonPersistedTaxon.setCurie("NCBITaxon:Invalid");
		SOTerm nonPersistedSoTerm = new SOTerm();
		nonPersistedSoTerm.setCurie("SO:Invalid");
		Reference nonPersistedReference = new Reference();
		nonPersistedReference.setCurie("AGRKB:Invalid");
		
		Note invalidNote = new Note();
		invalidNote.setNoteType(variantStatus);
		invalidNote.setFreeText("Invalid");
		invalidNote.setReferences(List.of(nonPersistedReference));
		
		Variant variant = new Variant();
		variant.setPrimaryExternalId("Variant:0012");
		variant.setTaxon(nonPersistedTaxon);
		variant.setVariantType(nonPersistedSoTerm);
		variant.setVariantStatus(noteType);
		variant.setSourceGeneralConsequence(nonPersistedSoTerm);
		variant.setDateCreated(datetime);
		variant.setDataProvider(nonPersistedOrganization);
		variant.setRelatedNotes(List.of(invalidNote));
		
		RestAssured.given().
			contentType("application/json").
			body(variant).
			when().
			post("/api/variant").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.taxon", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.variantType", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.variantStatus", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.sourceGeneralConsequence", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.INVALID_MESSAGE,
					"references - " + ValidationConstants.INVALID_MESSAGE))));
	}

	@Test
	@Order(13)
	public void editVariantWithInvalidFields() {
		NCBITaxonTerm nonPersistedTaxon = new NCBITaxonTerm();
		nonPersistedTaxon.setCurie("NCBITaxon:Invalid");
		SOTerm nonPersistedSoTerm = new SOTerm();
		nonPersistedSoTerm.setCurie("SO:Invalid");
		Reference nonPersistedReference = new Reference();
		nonPersistedReference.setCurie("AGRKB:Invalid");
		
		Variant variant = getVariant(VARIANT);
		variant.setTaxon(nonPersistedTaxon);
		variant.setVariantType(nonPersistedSoTerm);
		variant.setVariantStatus(noteType);
		variant.setSourceGeneralConsequence(nonPersistedSoTerm);
		variant.setDateCreated(datetime);
		variant.setDataProvider(nonPersistedOrganization);
		
		Note invalidNote = variant.getRelatedNotes().get(0);
		invalidNote.setReferences(List.of(nonPersistedReference));
		invalidNote.setNoteType(variantStatus);
		variant.setRelatedNotes(List.of(invalidNote));
		
		RestAssured.given().
			contentType("application/json").
			body(variant).
			when().
			put("/api/variant").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.taxon", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.variantType", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.variantStatus", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.sourceGeneralConsequence", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.INVALID_MESSAGE,
					"references - " + ValidationConstants.INVALID_MESSAGE))));
	}

	@Test
	@Order(14)
	public void createVariantWithObsoleteFields() {
		Variant variant = new Variant();
		variant.setPrimaryExternalId("Variant:0012");
		variant.setTaxon(obsoleteTaxon);
		variant.setVariantType(obsoleteSoTerm);
		variant.setVariantStatus(obsoleteVariantStatus);
		variant.setSourceGeneralConsequence(obsoleteSoTerm);
		variant.setDataProvider(obsoleteDataProvider);
		
		Note obsoleteNote = new Note();
		obsoleteNote.setNoteType(obsoleteNoteType);
		obsoleteNote.setReferences(List.of(obsoleteReference));
		obsoleteNote.setFreeText("Obsolete");
		variant.setRelatedNotes(List.of(obsoleteNote));
		
		RestAssured.given().
			contentType("application/json").
			body(variant).
			when().
			post("/api/variant").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.taxon", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.variantType", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.variantStatus", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.sourceGeneralConsequence", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"references - " + ValidationConstants.OBSOLETE_MESSAGE))));
	}

	@Test
	@Order(15)
	public void editVariantWithObsoleteFields() {
		Variant variant = getVariant(VARIANT);
		variant.setTaxon(obsoleteTaxon);
		variant.setVariantType(obsoleteSoTerm);
		variant.setVariantStatus(obsoleteVariantStatus);
		variant.setSourceGeneralConsequence(obsoleteSoTerm);
		variant.setDataProvider(obsoleteDataProvider);
		
		Note obsoleteNote = variant.getRelatedNotes().get(0);
		obsoleteNote.setNoteType(obsoleteNoteType);
		obsoleteNote.setReferences(List.of(obsoleteReference));
		variant.setRelatedNotes(List.of(obsoleteNote));
		
		RestAssured.given().
			contentType("application/json").
			body(variant).
			when().
			put("/api/variant").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.taxon", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.variantType", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.variantStatus", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.sourceGeneralConsequence", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"references - " + ValidationConstants.OBSOLETE_MESSAGE))));
	}

	@Test
	@Order(17)
	public void editVariantWithNullNonRequiredFieldsLevel2() {
		// Level 2 done before 1 to avoid having to restore nulled fields
		Variant variant = getVariant(VARIANT);

		Note editedNote = variant.getRelatedNotes().get(0);
		editedNote.setReferences(null);
		variant.setRelatedNotes(List.of(editedNote));

		RestAssured.given().
			contentType("application/json").
			body(variant).
			when().
			put("/api/variant").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get("/api/variant/" + VARIANT).
			then().
			statusCode(200).
			body("entity", hasKey("relatedNotes")).
			body("entity.relatedNotes[0]", not(hasKey("references")));
	}

	@Test
	@Order(18)
	public void editVariantWithNullNonRequiredFieldsLevel1() {
		Variant variant = getVariant(VARIANT);

		variant.setVariantStatus(null);
		variant.setSourceGeneralConsequence(null);
		variant.setDateCreated(null);
		variant.setRelatedNotes(null);
		variant.setDataProviderCrossReference(null);

		RestAssured.given().
			contentType("application/json").
			body(variant).
			when().
			put("/api/variant").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get("/api/variant/" + VARIANT).
			then().
			statusCode(200).
			body("entity", not(hasKey("variantStatus"))).
			body("entity", not(hasKey("sourceGeneralConsequence"))).
			body("entity", not(hasKey("dateCreated"))).
			body("entity", not(hasKey("relatedNotes"))).
			body("entity", not(hasKey("dataProviderCrossReference")));
	}
	
	@Test
	@Order(19)
	public void createVariantWithOnlyRequiredFieldsLevel1() {
		Variant variant = new Variant();
		variant.setPrimaryExternalId("VARIANT:0019");
		variant.setTaxon(taxon);
		variant.setVariantType(variantTypeTerm);
		
		RestAssured.given().
			contentType("application/json").
			body(variant).
			when().
			post("/api/variant").
			then().
			statusCode(200);
	}
	
	@Test
	@Order(20)
	public void createVariantWithOnlyRequiredFieldsLevel2() {
		Variant variant = new Variant();
		variant.setPrimaryExternalId("VARIANT:0020");
		variant.setTaxon(taxon);
		variant.setVariantType(variantTypeTerm);

		Note minimalNote = createNote(noteType, "Test text", false, null);
		variant.setRelatedNotes(List.of(minimalNote));
		
		RestAssured.given().
			contentType("application/json").
			body(variant).
			when().
			post("/api/variant").
			then().
			statusCode(200);
	}
	
	@Test
	@Order(21)
	public void createVariantWithDuplicateNote() {
		Variant variant = new Variant();
		variant.setPrimaryExternalId("VARIANT:0021");
		variant.setTaxon(taxon);
		variant.setVariantType(variantTypeTerm);
		
		Note note1 = createNote(noteType, "Test text", false, null);
		Note note2 = createNote(noteType, "Test text", false, null);
		variant.setRelatedNotes(List.of(note1, note2));
		
		RestAssured.given().
			contentType("application/json").
			body(variant).
			when().
			post("/api/variant").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.relatedNotes", is("freeText - " + ValidationConstants.DUPLICATE_MESSAGE + " (Test text|comment|false|false)"));
	}

	@Test
	@Order(22)
	public void deleteVariant() {

		RestAssured.given().
				when().
				delete("/api/variant/" + VARIANT).
				then().
				statusCode(200);
	}

}
