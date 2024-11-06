package org.alliancegenome.curation_api;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;

@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("610 - HTPExpressionDatasetAnnotation bulk upload - FMS")
@Order(610)
public class HTPExpressionDatasetAnnotationFmsITCase extends BaseITCase {

	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
						.setParam("http.socket.timeout", 100000)
						.setParam("http.connection.timeout", 100000));
	}

	private final String htpDatasetBulkPostEndpoint = "/api/htpexpressiondatasetannotation/bulk/FB/htpexpressiondatasetannotationfile";
	private final String htpDatasetTestFilePath = "src/test/resources/bulk/fms/09_htp_dataset/";
	private final String htpDatasetFindEndpoint = "/api/htpexpressiondatasetannotation/find?limit=100&page=0";

	@Test
	@Order(1)
	public void htpDatasetBulkUploadCheckFields() throws Exception {
		checkSuccessfulBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "AF_01_all_fields.json");

		RestAssured.given().when().header("Content-Type", "application/json").body("{}").post(htpDatasetFindEndpoint)
			.then().statusCode(200).body("totalResults", is(1)).body("results", hasSize(1))
			.body("results[0].htpExpressionDataset.curie", is("FB:FBlc0003342"))
			.body("results[0].htpExpressionDataset.crossReferences", hasSize(2))
			.body("results[0].htpExpressionDataset.crossReferences[0].referencedCurie", is("TEST:TestMol00000001"))
			.body("results[0].htpExpressionDataset.preferredCrossReference.referencedCurie", is("TEST:TestMol00000001"))
			.body("results[0].htpExpressionDataset.secondaryIdentifiers", hasSize(1))
			.body("results[0].htpExpressionDataset.secondaryIdentifiers[0]", is("GEO:GSE38764"))
			.body("results[0].references", hasSize(2))
			.body("results[0].references[0].crossReferences[0].referencedCurie", is("PMID:25920554"))
			.body("results[0].subSeries", hasSize(1))
			.body("results[0].subSeries[0].curie", is("FB:FBlc0003342"))
			.body("results[0].subSeries[0].crossReferences", hasSize(2))
			.body("results[0].numberOfChannels", is(2))
			.body("results[0].name", is("TEST TITLE"))
			.body("results[0].relatedNote.freeText", is("TEST_SUMMARY"))
			.body("results[0].categoryTags[0].name", is("anatomical structure"));
	}

	@Test
	@Order(2)
	public void htpDatasetBulkUploadMissingRequiredFields() throws Exception {

		checkFailedBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "MR_01_no_dataset_id.json");
		checkFailedBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "MR_02_no_title.json");
		checkFailedBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "MR_03_no_category_tags.json");
		checkFailedBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "MR_04_no_primary_id.json");
	}

	@Test
	@Order(3)
	public void htpDatasetBulkUploadEmptyRequiredFields() throws Exception {

		checkFailedBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "ER_01_empty_dataset_id.json");
		checkFailedBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "ER_02_empty_title.json");
		checkFailedBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "ER_03_empty_category_tags.json");
		checkFailedBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "ER_04_empty_primary_id.json");
	}

	@Test
	@Order(4)
	public void htpDatasetBulkUploadInvalidFields() throws Exception {

		checkFailedBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "IV_01_invalid_publication.json");
		checkFailedBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "IV_02_invalid_number_of_channels.json");
		checkFailedBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "IV_03_invalid_sub_series.json");
		checkFailedBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "IV_04_invalid_category_tag.json");
	}

	@Test
	@Order(5)
	public void htpDatasetBulkUploadUpdateMissingNonRequiredFields() throws Exception {

		checkSuccessfulBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "UM_01_update_no_non_required_fields.json");

		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post(htpDatasetFindEndpoint).
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0]", not(hasKey("references"))).
			body("results[0]", not(hasKey("subSeries"))).
			body("results[0]", not(hasKey("numberOfChannels"))).
			body("results[0]", not(hasKey("relatedNote")));
	}

	@Test
	@Order(6)
	public void htpDatasetBulkUploadUpdateEmptyNonRequiredFields() throws Exception {

		checkSuccessfulBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "UE_01_update_empty_non_required_fields.json");

		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post(htpDatasetFindEndpoint).
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0]", not(hasKey("references"))).
			body("results[0]", not(hasKey("subSeries"))).
			body("results[0]", not(hasKey("relatedNote")));
	}
}