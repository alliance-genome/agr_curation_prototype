package org.alliancegenome.curation_api;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.junit.jupiter.api.*;

import java.time.OffsetDateTime;

import static org.hamcrest.Matchers.*;


@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("1001 - AffectedGenomicModel AffectedGenomicModel Associations bulk upload")
@Order(1001)
public class AgmAgmAssociationBulkUploadITCase extends BaseITCase {
	
	private AffectedGenomicModel agmSubject;
	private AffectedGenomicModel agmObject;
	private String agmSubjectCurie = "AMGTEST:AffectedGenomicModel0010";
	private String agmObjectCurie = "AMGTEST:AffectedGenomicModel0020";
	private String relationName = "has_parental_population";

	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 60000)
					.setParam("http.connection.timeout", 60000));
	}

	private final String agmAgmAssociationBulkPostEndpoint = "/api/agmagmassociation/bulk/ZFIN/associationFile";
	private final String agmAgmAssociationGetEndpoint = "/api/agmagmassociation/findBy";
	private final String agmAgmAssociationTestFilePath = "src/test/resources/bulk/AGMA01_agm_agm_association/";
	private final String agmGetEndpoint = "/api/agm/";

	private void loadRequiredEntities() {
		Organization dataProvider = getOrganization("ZFIN");
		agmSubject = createAffectedGenomicModel(agmSubjectCurie, "test name subject", "NCBITaxon:7955", "fish", false, dataProvider);
		agmObject = createAffectedGenomicModel(agmObjectCurie, "test name object", "NCBITaxon:7955", "fish", false, dataProvider);
	}
	
	@Test
	@Order(1)
	public void agmAgmAssociationBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();
		
		checkSuccessfulBulkLoad(agmAgmAssociationBulkPostEndpoint, agmAgmAssociationTestFilePath + "AF_01_all_fields.json");

		String s = agmAgmAssociationGetEndpoint + "?agmSubjectId=" + agmSubject.getId() + "&relationName=" + relationName + "&agmObjectId=" + agmObject.getId();
		RestAssured.given().
			when().
			get(s).
			then().
			statusCode(200).
			body("entity.relation.name", is(relationName)).
			body("entity.agmAssociationSubject.primaryExternalId", is(agmSubjectCurie)).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.createdBy.uniqueId", is("AGMTEST:Person0001")).
			body("entity.updatedBy.uniqueId", is("AGMTEST:Person0002")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12+00:00").toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12+00:00").toString()));
		
		RestAssured.given().
			when().
			get(agmGetEndpoint + agmSubjectCurie).
			then().
			statusCode(200).
			body("entity.parentalPopulations", hasSize(1)).
			body("entity.parentalPopulations[0].relation.name", is(relationName)).
			body("entity.parentalPopulations[0].agmAssociationSubject.primaryExternalId", is(agmSubjectCurie)).
			body("entity.parentalPopulations[0].agmAssociationSubject", not(hasKey("agmAgmAssociationObject")));

	}

	@Test
	@Order(2)
	public void agmAgmAssociationBulkUploadUpdateCheckFields() throws Exception {
		checkSuccessfulBulkLoad(agmAgmAssociationBulkPostEndpoint, agmAgmAssociationTestFilePath + "UD_01_update_all_except_default_fields.json");

		RestAssured.given().
				when().
				get(agmAgmAssociationGetEndpoint + "?agmSubjectId=" + agmSubject.getId() + "&relationName=" + relationName + "&agmObjectId=" + agmObject.getId()).
				then().
				statusCode(200).
				body("entity.relation.name", is(relationName)).
				body("entity.agmAssociationSubject.primaryExternalId", is(agmSubjectCurie)).
				body("entity.internal", is(true)).
				body("entity.obsolete", is(true)).
				body("entity.createdBy.uniqueId", is("AGMTEST:Person0001")).
				body("entity.updatedBy.uniqueId", is("AGMTEST:Person0002")).
				body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12+00:00").toString())).
				body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12+00:00").toString()));


		RestAssured.given().
				when().
				get(agmGetEndpoint + agmSubjectCurie).
				then().
				statusCode(200).
				body("entity.parentalPopulations", hasSize(1));
	}

	@Test
	@Order(3)
	public void agmStrAssociationBulkUploadMissingRequiredFields() throws Exception {
		checkFailedBulkLoad(agmAgmAssociationBulkPostEndpoint, agmAgmAssociationTestFilePath + "MR_01_no_subject.json");
		checkFailedBulkLoad(agmAgmAssociationBulkPostEndpoint, agmAgmAssociationTestFilePath + "MR_02_no_relation.json");
		checkFailedBulkLoad(agmAgmAssociationBulkPostEndpoint, agmAgmAssociationTestFilePath + "MR_03_no_object.json");
	}

	@Test
	@Order(4)
	public void agmStrAssociationBulkUploadEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(agmAgmAssociationBulkPostEndpoint, agmAgmAssociationTestFilePath + "ER_01_empty_subject.json");
		checkFailedBulkLoad(agmAgmAssociationBulkPostEndpoint, agmAgmAssociationTestFilePath + "ER_02_empty_relation.json");
		checkFailedBulkLoad(agmAgmAssociationBulkPostEndpoint, agmAgmAssociationTestFilePath + "ER_03_empty_object.json");
	}

	@Test
	@Order(5)
	public void agmStrAssociationBulkUploadInvalidFields() throws Exception {
		checkFailedBulkLoad(agmAgmAssociationBulkPostEndpoint, agmAgmAssociationTestFilePath + "IV_01_invalid_subject.json");
		checkFailedBulkLoad(agmAgmAssociationBulkPostEndpoint, agmAgmAssociationTestFilePath + "IV_02_invalid_relation.json");
		checkFailedBulkLoad(agmAgmAssociationBulkPostEndpoint, agmAgmAssociationTestFilePath + "IV_03_invalid_object.json");
		checkFailedBulkLoad(agmAgmAssociationBulkPostEndpoint, agmAgmAssociationTestFilePath + "IV_04_invalid_date_created.json");
		checkFailedBulkLoad(agmAgmAssociationBulkPostEndpoint, agmAgmAssociationTestFilePath + "IV_05_invalid_date_updated.json");
	}
}
