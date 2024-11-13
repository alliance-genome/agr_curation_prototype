package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

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
@DisplayName("612 - Biogrid Orc bulk upload - FMS")
@Order(612)
public class BiogridOrcBulkUploadFmsITCase extends BaseITCase {

	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
						.setParam("http.socket.timeout", 100000)
						.setParam("http.connection.timeout", 100000));
	}

	private final String biogridOrcBulkPostEndpoint = "/api/cross-reference/bulk/FB/biogridfile";
	private final String biogridOrcTestFilePath = "src/test/resources/bulk/fms/12_biogrid/";
	private final String biogridOrcFindEndpoint = "/api/cross-reference/find?limit=100&page=0";

	@Test
	@Order(1)
	public void biogridOrcBulkUploadCheckFields() throws Exception {
		
		checkSuccessfulBulkLoad(biogridOrcBulkPostEndpoint, biogridOrcTestFilePath + "AF_01_all_fields.json", 1);

		RestAssured.given().
				when().
				header("Content-Type", "application/json").
				body("{}").
				post(biogridOrcFindEndpoint).
				then().
				statusCode(200).
				body("totalResults", is(1)).
				body("results", hasSize(1)).
				body("results[0].referencedCurie", is("NCBI_Gene:108101")).
				body("results[0].displayName", is("BioGRID CRISPR Screen Cell Line Phenotypes"));
	}

}
