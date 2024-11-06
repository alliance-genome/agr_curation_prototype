package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.alliancegenome.curation_api.base.BaseITCase;
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
@DisplayName("605 - Sequence Targeting Reagent bulk upload - FMS")
@Order(605)
public class SequenceTargetingReagentBulkUploadFmsITCase extends BaseITCase {

	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 100000)
					.setParam("http.connection.timeout", 100000));
	}

	private final String sqtrBulkPostEndpoint = "/api/sqtr/bulk/ZFIN/sqtrfile";
	private final String sqtrTestFilePath = "src/test/resources/bulk/fms/05_sqtr/";
	private final String sqtrFindEndpoint = "/api/sqtr/find?limit=100&page=0";
	
	@Test
	@Order(1)
	public void sqtrBulkUploadCheckFields() throws Exception {
		
		checkSuccessfulBulkLoad(sqtrBulkPostEndpoint, sqtrTestFilePath + "AF_01_all_fields.json", 2);

		RestAssured.given().
				when().
				header("Content-Type", "application/json").
				body("{}").
				post(sqtrFindEndpoint).
				then().
				statusCode(200).
				body("totalResults", is(1)).
				body("results", hasSize(1)).
				body("results[0].modEntityId", is("ZFIN:ZDB-TALEN-180503-1")).
				body("results[0].name", is("TALEN-inhbaa")).
				body("results[0].taxon.curie", is("NCBITaxon:7955")).
				body("results[0].synonyms", hasSize(1)).
				body("results[0].secondaryIdentifiers", hasSize(1));
	}

	@Test
	@Order(2)
	public void sqtrBulkUploadMissingRequiredFields() throws Exception {
		
		checkBulkLoadRecordCounts(sqtrBulkPostEndpoint, sqtrTestFilePath + "MR_01_no_name.json", "Records", 2, 1, 1, 0);
		checkBulkLoadRecordCounts(sqtrBulkPostEndpoint, sqtrTestFilePath + "MR_02_no_taxon.json", "Records", 2, 1, 1, 0);
	}

	@Test
	@Order(3)
	public void sqtrBulkUploadEmptyRequiredFields() throws Exception {

		checkBulkLoadRecordCounts(sqtrBulkPostEndpoint, sqtrTestFilePath + "ER_01_empty_name.json", "Records", 2, 1, 1, 0);
		checkBulkLoadRecordCounts(sqtrBulkPostEndpoint, sqtrTestFilePath + "ER_02_empty_taxon.json", "Records", 2, 1, 1, 0);
		
	}

	@Test
	@Order(4)
	public void sqtrBulkUploadUpdateMissingNonRequiredFields() throws Exception {

		checkSuccessfulBulkLoad(sqtrBulkPostEndpoint, sqtrTestFilePath + "UM_01_update_no_non_required_fields.json", 2);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post(sqtrFindEndpoint).
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0]", not(hasKey("soTermId"))).
			body("results[0]", not(hasKey("synonyms"))).
			body("results[0]", not(hasKey("secondaryIds")));
	}

	@Test
	@Order(5)
	public void sqtrBulkUploadUpdateEmptyNonRequiredFields() throws Exception {

		checkSuccessfulBulkLoad(sqtrBulkPostEndpoint, sqtrTestFilePath + "AF_01_all_fields.json", 2);

		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post(sqtrFindEndpoint).
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results", hasSize(1)).
			body("results[0]" , hasKey("modEntityId")).
			body("results[0]" , hasKey("name")).
			body("results[0]" , hasKey("taxon")).
			body("results[0]" , hasKey("synonyms")).
			body("results[0]" , hasKey("secondaryIdentifiers"));

		checkSuccessfulBulkLoad(sqtrBulkPostEndpoint, sqtrTestFilePath + "UE_01_update_empty_non_required_fields.json", 2);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post(sqtrFindEndpoint).
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0]", not(hasKey("soTerm"))).
			body("results[0]", not(hasKey("synonyms"))).
			body("results[0]", not(hasKey("secondaryIdentifiers")));
	}

}
