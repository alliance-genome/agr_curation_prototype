package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.time.OffsetDateTime;
import java.util.List;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
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
@DisplayName("303 - AffectedGenomicModelITCase")
@Order(303)
public class AffectedGenomicModelITCase extends BaseITCase {

	private static final String AGM = "AGM:0001";
	
	private NCBITaxonTerm taxon;
	private NCBITaxonTerm taxon2;
	private NCBITaxonTerm obsoleteTaxon;
	private OffsetDateTime datetime;
	private OffsetDateTime datetime2;
	private Person person;
	private VocabularyTerm subtype;
	private VocabularyTerm subtype2;
	private VocabularyTerm obsoleteSubtype;
	private DataProvider dataProvider;
	private DataProvider dataProvider2;
	private DataProvider obsoleteDataProvider;
	private Organization nonPersistedOrganization;
	
	private void loadRequiredEntities() {
		taxon = getNCBITaxonTerm("NCBITaxon:10090");
		taxon2 = getNCBITaxonTerm("NCBITaxon:9606");
		datetime = OffsetDateTime.parse("2022-03-09T22:10:12+00:00");
		datetime2 = OffsetDateTime.parse("2022-04-10T22:10:11+00:00");
		person = createPerson("TEST:AGMPerson0001");
		Vocabulary subtypeVocabulary = getVocabulary(VocabularyConstants.AGM_SUBTYPE_VOCABULARY);
		subtype = getVocabularyTerm(subtypeVocabulary, "fish");
		subtype2 = getVocabularyTerm(subtypeVocabulary, "genotype");
		obsoleteSubtype = createVocabularyTerm(subtypeVocabulary, "obsolete", true);
		obsoleteTaxon = getNCBITaxonTerm("NCBITaxon:0000");
		dataProvider = createDataProvider("TEST", false);
		dataProvider2 = createDataProvider("TEST2", false);
		obsoleteDataProvider = createDataProvider("ODP", true);
		nonPersistedOrganization = new Organization();
		nonPersistedOrganization.setAbbreviation("INV");
	}
	
	@Test
	@Order(1)
	public void createValidAGM() {
		loadRequiredEntities();
		
		AffectedGenomicModel agm = new AffectedGenomicModel();
		agm.setPrimaryExternalId(AGM);
		agm.setTaxon(taxon);
		agm.setName("Test AGM");
		agm.setDateCreated(datetime);
		agm.setSubtype(subtype);
		agm.setDataProvider(dataProvider);
		List<String> synonyms = List.of("Syn 1", "Syn 2");
		agm.setSynonyms(synonyms);

		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			post("/api/agm").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get("/api/agm/" + AGM).
			then().
			statusCode(200).
			body("entity.primaryExternalId", is(AGM)).
			body("entity.name", is("Test AGM")).
			body("entity.subtype.name", is(subtype.getName())).
			body("entity.taxon.curie", is(taxon.getCurie())).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.synonyms", is(synonyms)).
			body("entity.dateCreated", is(datetime.toString())).
			body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.dataProvider.sourceOrganization.abbreviation", is(dataProvider.getSourceOrganization().getAbbreviation()));
	}

	@Test
	@Order(2)
	public void editAGM() {
		AffectedGenomicModel agm = getAffectedGenomicModel(AGM);
		agm.setName("AGM edited");
		agm.setTaxon(taxon2);
		agm.setInternal(true);
		agm.setObsolete(true);
		agm.setDateCreated(datetime2);
		agm.setCreatedBy(person);
		agm.setSubtype(subtype2);
		agm.setDataProvider(dataProvider2);

		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			put("/api/agm").
			then().
			statusCode(200);

		RestAssured.given().
			when().
			get("/api/agm/" + AGM).
			then().
			statusCode(200).
			body("entity.primaryExternalId", is(AGM)).
			body("entity.name", is("AGM edited")).
			body("entity.subtype.name", is(subtype2.getName())).
			body("entity.taxon.curie", is(taxon2.getCurie())).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.dateCreated", is(datetime2.toString())).
			body("entity.createdBy.uniqueId", is(person.getUniqueId())).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.dataProvider.sourceOrganization.abbreviation", is(dataProvider2.getSourceOrganization().getAbbreviation()));
	}
	
	@Test
	@Order(3)
	public void createAGMWithMissingRequiredFields() {
		AffectedGenomicModel agm = new AffectedGenomicModel();
		
		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			post("/api/agm").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(3))).
			body("errorMessages.modInternalId", is(ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + "primaryExternalId")).
			body("errorMessages.taxon", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.subtype", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(4)
	public void editAGMWithMissingPrimaryExternalId() {
		AffectedGenomicModel agm = getAffectedGenomicModel(AGM);
		agm.setPrimaryExternalId(null);
		
		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			put("/api/agm").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.modInternalId", is(ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + "primaryExternalId"));
	}
	
	@Test
	@Order(5)
	public void editAGMWithMissingRequiredFields() {
		AffectedGenomicModel agm = getAffectedGenomicModel(AGM);
		agm.setTaxon(null);
		agm.setSubtype(null);
		agm.setDataProvider(null);
		
		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			put("/api/agm").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(3))).
			body("errorMessages.taxon", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.subtype", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(6)
	public void createAGMWithEmptyRequiredFields() {
		AffectedGenomicModel agm = new AffectedGenomicModel();
		agm.setPrimaryExternalId("");
		agm.setTaxon(taxon);
		agm.setSubtype(subtype);
		
		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			post("/api/agm").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.modInternalId", is(ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + "primaryExternalId"));
	}

	@Test
	@Order(7)
	public void editAGMWithEmptyPrimaryExternalId() {
		AffectedGenomicModel agm = getAffectedGenomicModel(AGM);
		agm.setPrimaryExternalId("");

		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			put("/api/agm").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.modInternalId", is(ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + "primaryExternalId"));
	}
	
	@Test
	@Order(8)
	public void createAGMWithInvalidFields() {
		NCBITaxonTerm nonPersistedTaxon = new NCBITaxonTerm();
		nonPersistedTaxon.setCurie("NCBITaxon:Invalid");
		VocabularyTerm nonPersistedTerm = new VocabularyTerm();
		nonPersistedTerm.setName("invalid");
		DataProvider invalidDataProvider = new DataProvider();
		invalidDataProvider.setSourceOrganization(nonPersistedOrganization);
		
		AffectedGenomicModel agm = new AffectedGenomicModel();
		agm.setPrimaryExternalId("AGM:0008");
		agm.setTaxon(nonPersistedTaxon);
		agm.setSubtype(nonPersistedTerm);
		agm.setDataProvider(invalidDataProvider);
		
		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			post("/api/agm").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(3))).
			body("errorMessages.taxon", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.subtype", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.dataProvider", is("sourceOrganization - " + ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(9)
	public void editAGMWithInvalidFields() {
		NCBITaxonTerm nonPersistedTaxon = new NCBITaxonTerm();
		nonPersistedTaxon.setCurie("NCBITaxon:Invalid");
		VocabularyTerm nonPersistedTerm = new VocabularyTerm();
		nonPersistedTerm.setName("invalid");
		DataProvider invalidDataProvider = new DataProvider();
		invalidDataProvider.setSourceOrganization(nonPersistedOrganization);
		
		AffectedGenomicModel agm = getAffectedGenomicModel(AGM);
		agm.setTaxon(nonPersistedTaxon);
		agm.setSubtype(nonPersistedTerm);
		agm.setDataProvider(invalidDataProvider);
		
		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			put("/api/agm").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(3))).
			body("errorMessages.taxon", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.subtype", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.dataProvider", is("sourceOrganization - " + ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(10)
	public void createAGMWithObsoleteFields() {
		AffectedGenomicModel agm = new AffectedGenomicModel();
		agm.setPrimaryExternalId("AGM:0010");
		agm.setTaxon(obsoleteTaxon);
		agm.setSubtype(obsoleteSubtype);
		agm.setDataProvider(obsoleteDataProvider);
		
		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			post("/api/agm").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(3))).
			body("errorMessages.taxon", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.subtype", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(11)
	public void editAGMWithObsoleteFields() {
		AffectedGenomicModel agm = getAffectedGenomicModel(AGM);
		agm.setTaxon(obsoleteTaxon);
		agm.setSubtype(obsoleteSubtype);
		agm.setDataProvider(obsoleteDataProvider);
		
		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			put("/api/agm").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(3))).
			body("errorMessages.taxon", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.subtype", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(12)
	public void editAGMWithNullNonRequiredFields() {
		AffectedGenomicModel agm = getAffectedGenomicModel(AGM);
		agm.setName(null);
		
		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			put("/api/agm").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get("/api/agm/" + AGM).
			then().
			statusCode(200).
			body("entity", not(hasKey("name")));
		
	}
	
	@Test
	@Order(13)
	public void createAGMWithOnlyRequiredFields() {
		AffectedGenomicModel agm = new AffectedGenomicModel();
		agm.setPrimaryExternalId("AGM:0015");
		agm.setTaxon(taxon);
		agm.setSubtype(subtype);
		
		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			post("/api/agm").
			then().
			statusCode(200);
	}
	
	@Test
	@Order(14)
	public void deleteAGM() {

		RestAssured.given().
				when().
				delete("/api/agm/" + AGM).
				then().
				statusCode(200);
	}
}
