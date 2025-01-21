package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.time.OffsetDateTime;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;


@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("202 - Construct Genomic Entity Associations bulk upload")
@Order(202)
public class ConstructGenomicEntityAssociationBulkUploadITCase extends BaseITCase {
	
	private String constructPrimaryExternalId = "WB:Construct0001";
	private String relationName = "is_regulated_by";
	private String genePrimaryExternalId = "GENETEST:Gene0001";
	private String reference = "AGRKB:000000001";
	private String reference2 = "AGRKB:000000021";
	private String noteType = "comment";
	private String noteType2 = "test_construct_component_summary";
	private Construct construct;
	private Gene gene;
	
	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 60000)
					.setParam("http.connection.timeout", 60000));
	}

	private final String constructGenomicEntityAssociationBulkPostEndpoint = "/api/constructgenomicentityassociation/bulk/WB/associationFile";
	private final String constructGenomicEntityAssociationGetEndpoint = "/api/constructgenomicentityassociation/findBy";
	private final String constructGenomicEntityAssociationTestFilePath = "src/test/resources/bulk/CA01_construct_genomic_entity_association/";
	private final String constructGetEndpoint = "/api/construct/";
	private final String geneGetEndpoint = "/api/gene/";

	private void loadRequiredEntities() throws Exception {
		gene = getGene(genePrimaryExternalId);
		construct = getConstruct(constructPrimaryExternalId);
	}
	
	@Test
	@Order(1)
	public void constructGenomicEntityAssociationBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();
		
		checkSuccessfulBulkLoad(constructGenomicEntityAssociationBulkPostEndpoint, constructGenomicEntityAssociationTestFilePath + "AF_01_all_fields.json");
	
		RestAssured.given().
			when().
			get(constructGenomicEntityAssociationGetEndpoint + "?constructId=" + construct.getId() + "&relationName=" + relationName + "&genomicEntityId=" + gene.getId()).
			then().
			statusCode(200).
			body("entity.relation.name", is(relationName)).
			body("entity.constructGenomicEntityAssociationObject.primaryExternalId", is(genePrimaryExternalId)).
			body("entity.constructAssociationSubject.primaryExternalId", is(constructPrimaryExternalId)).
			body("entity.evidence", hasSize(1)).
			body("entity.evidence[0].curie", is(reference)).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.relatedNotes[0].internal", is(false)).
			body("entity.relatedNotes[0].obsolete", is(true)).
			body("entity.relatedNotes[0].updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.relatedNotes[0].createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.relatedNotes[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.relatedNotes[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.relatedNotes[0].freeText", is("Test note")).
			body("entity.relatedNotes[0].noteType.name", is(noteType)).
			body("entity.relatedNotes[0].references[0].curie", is(reference));
		
		RestAssured.given().
			when().
			get(constructGetEndpoint + constructPrimaryExternalId).
			then().
			statusCode(200).
			body("entity.primaryExternalId", is(constructPrimaryExternalId)).
			body("entity.constructGenomicEntityAssociations", hasSize(1)).
			body("entity.constructGenomicEntityAssociations[0].relation.name", is(relationName)).
			body("entity.constructGenomicEntityAssociations[0].constructGenomicEntityAssociationObject.primaryExternalId", is(genePrimaryExternalId)).
			body("entity.constructGenomicEntityAssociations[0].constructAssociationSubject", not(hasKey("constructGenomicEntityAssociations")));
		
		RestAssured.given().
			when().
			get(geneGetEndpoint + genePrimaryExternalId).
			then().
			statusCode(200).
			body("entity.constructGenomicEntityAssociations", hasSize(1)).
			body("entity.constructGenomicEntityAssociations[0].relation.name", is(relationName)).
			body("entity.constructGenomicEntityAssociations[0].constructGenomicEntityAssociationObject.primaryExternalId", is(genePrimaryExternalId)).
			body("entity.constructGenomicEntityAssociations[0].constructGenomicEntityAssociationObject", not(hasKey("constructGenomicEntityAssociations")));
	}
	
	@Test
	@Order(2)
	public void constructGenomicEntityAssociationBulkUploadUpdateCheckFields() throws Exception {
		checkSuccessfulBulkLoad(constructGenomicEntityAssociationBulkPostEndpoint, constructGenomicEntityAssociationTestFilePath + "UD_01_update_all_except_default_fields.json");
	
		RestAssured.given().
			when().
			get(constructGenomicEntityAssociationGetEndpoint + "?constructId=" + construct.getId() + "&relationName=" + relationName + "&genomicEntityId=" + gene.getId()).
			then().
			statusCode(200).
			body("entity.relation.name", is(relationName)).
			body("entity.constructGenomicEntityAssociationObject.primaryExternalId", is(genePrimaryExternalId)).
			body("entity.evidence", hasSize(1)).
			body("entity.evidence[0].curie", is(reference2)).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.createdBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.updatedBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString())).
			body("entity.relatedNotes[0].internal", is(true)).
			body("entity.relatedNotes[0].obsolete", is(false)).
			body("entity.relatedNotes[0].updatedBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.relatedNotes[0].createdBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.relatedNotes[0].dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString())).
			body("entity.relatedNotes[0].dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
			body("entity.relatedNotes[0].freeText", is("Test note 2")).
			body("entity.relatedNotes[0].noteType.name", is(noteType2)).
			body("entity.relatedNotes[0].references[0].curie", is(reference2));
		
		RestAssured.given().
			when().
			get(constructGetEndpoint + constructPrimaryExternalId).
			then().
			statusCode(200).
			body("entity.constructGenomicEntityAssociations", hasSize(1));
		
		RestAssured.given().
			when().
			get(geneGetEndpoint + genePrimaryExternalId).
			then().
			statusCode(200).
			body("entity.constructGenomicEntityAssociations", hasSize(1));
	}
	
	@Test
	@Order(3)
	public void constructGenomicEntityAssociationBulkUploadMissingRequiredFields() throws Exception {
		checkFailedBulkLoad(constructGenomicEntityAssociationBulkPostEndpoint, constructGenomicEntityAssociationTestFilePath + "MR_01_no_subject.json");
		checkFailedBulkLoad(constructGenomicEntityAssociationBulkPostEndpoint, constructGenomicEntityAssociationTestFilePath + "MR_02_no_relation.json");
		checkFailedBulkLoad(constructGenomicEntityAssociationBulkPostEndpoint, constructGenomicEntityAssociationTestFilePath + "MR_03_no_object.json");
		checkFailedBulkLoad(constructGenomicEntityAssociationBulkPostEndpoint, constructGenomicEntityAssociationTestFilePath + "MR_04_no_related_note_note_type.json");
		checkFailedBulkLoad(constructGenomicEntityAssociationBulkPostEndpoint, constructGenomicEntityAssociationTestFilePath + "MR_05_no_related_note_free_text.json");
	}
	
	@Test
	@Order(4)
	public void constructGenomicEntityAssociationBulkUploadEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(constructGenomicEntityAssociationBulkPostEndpoint, constructGenomicEntityAssociationTestFilePath + "ER_01_empty_subject.json");
		checkFailedBulkLoad(constructGenomicEntityAssociationBulkPostEndpoint, constructGenomicEntityAssociationTestFilePath + "ER_02_empty_relation.json");
		checkFailedBulkLoad(constructGenomicEntityAssociationBulkPostEndpoint, constructGenomicEntityAssociationTestFilePath + "ER_03_empty_object.json");
		checkFailedBulkLoad(constructGenomicEntityAssociationBulkPostEndpoint, constructGenomicEntityAssociationTestFilePath + "ER_04_empty_related_note_note_type.json");
		checkFailedBulkLoad(constructGenomicEntityAssociationBulkPostEndpoint, constructGenomicEntityAssociationTestFilePath + "ER_05_empty_related_note_free_text.json");
	}
	
	@Test
	@Order(5)
	public void constructGenomicEntityAssociationBulkUploadInvalidFields() throws Exception {
		checkFailedBulkLoad(constructGenomicEntityAssociationBulkPostEndpoint, constructGenomicEntityAssociationTestFilePath + "IV_01_invalid_subject.json");
		checkFailedBulkLoad(constructGenomicEntityAssociationBulkPostEndpoint, constructGenomicEntityAssociationTestFilePath + "IV_02_invalid_relation.json");
		checkFailedBulkLoad(constructGenomicEntityAssociationBulkPostEndpoint, constructGenomicEntityAssociationTestFilePath + "IV_03_invalid_object.json");
		checkFailedBulkLoad(constructGenomicEntityAssociationBulkPostEndpoint, constructGenomicEntityAssociationTestFilePath + "IV_04_invalid_date_created.json");
		checkFailedBulkLoad(constructGenomicEntityAssociationBulkPostEndpoint, constructGenomicEntityAssociationTestFilePath + "IV_05_invalid_date_updated.json");
		checkFailedBulkLoad(constructGenomicEntityAssociationBulkPostEndpoint, constructGenomicEntityAssociationTestFilePath + "IV_06_invalid_evidence.json");
		checkFailedBulkLoad(constructGenomicEntityAssociationBulkPostEndpoint, constructGenomicEntityAssociationTestFilePath + "IV_07_invalid_related_note_note_type.json");
	}
	
	@Test
	@Order(6)
	public void constructGenomicEntityAssociationBulkUploadUpdateMissingNonRequiredFieldsLevel1() throws Exception {
		checkSuccessfulBulkLoad(constructGenomicEntityAssociationBulkPostEndpoint, constructGenomicEntityAssociationTestFilePath + "UD_01_update_all_except_default_fields.json");
		checkSuccessfulBulkLoad(constructGenomicEntityAssociationBulkPostEndpoint, constructGenomicEntityAssociationTestFilePath + "UM_01_update_no_non_required_fields_level_1.json");
		
		RestAssured.given().
			when().
			get(constructGenomicEntityAssociationGetEndpoint + "?constructId=" + construct.getId() + "&relationName=" + relationName + "&genomicEntityId=" + gene.getId()).
			then().
			statusCode(200).
			body("entity", not(hasKey("createdBy"))).
			body("entity", not(hasKey("updatedBy"))).
			body("entity", not(hasKey("evidence"))).
			body("entity", not(hasKey("relatedNotes")));
	}
	
	@Test
	@Order(7)
	public void constructGenomicEntityAssociationBulkUploadUpdateMissingNonRequiredFieldsLevel2() throws Exception {
		checkSuccessfulBulkLoad(constructGenomicEntityAssociationBulkPostEndpoint, constructGenomicEntityAssociationTestFilePath + "UD_01_update_all_except_default_fields.json");
		checkSuccessfulBulkLoad(constructGenomicEntityAssociationBulkPostEndpoint, constructGenomicEntityAssociationTestFilePath + "UM_02_update_no_non_required_fields_level_2.json");
		
		RestAssured.given().
			when().
			get(constructGenomicEntityAssociationGetEndpoint + "?constructId=" + construct.getId() + "&relationName=" + relationName + "&genomicEntityId=" + gene.getId()).
			then().
			statusCode(200).
			body("entity.relatedNotes[0]", not(hasKey("createdBy"))).
			body("entity.relatedNotes[0]", not(hasKey("updatedBy"))).
			body("entity.relatedNotes[0]", not(hasKey("evidence")));
	}
	
	@Test
	@Order(8)
	public void constructGenomicEntityAssociationBulkUploadUpdateEmptyNonRequiredFieldsLevel2() throws Exception {
		checkSuccessfulBulkLoad(constructGenomicEntityAssociationBulkPostEndpoint, constructGenomicEntityAssociationTestFilePath + "UD_01_update_all_except_default_fields.json");
		checkSuccessfulBulkLoad(constructGenomicEntityAssociationBulkPostEndpoint, constructGenomicEntityAssociationTestFilePath + "UE_01_update_empty_non_required_fields.json");
		
		RestAssured.given().
			when().
			get(constructGenomicEntityAssociationGetEndpoint + "?constructId=" + construct.getId() + "&relationName=" + relationName + "&genomicEntityId=" + gene.getId()).
			then().
			statusCode(200).
			body("entity", not(hasKey("createdBy"))).
			body("entity", not(hasKey("updatedBy"))).
			body("entity", not(hasKey("evidence"))).
			body("entity.relatedNotes[0]", not(hasKey("createdBy"))).
			body("entity.relatedNotes[0]", not(hasKey("updatedBy"))).
			body("entity.relatedNotes[0]", not(hasKey("evidence")));
	}
	
}
