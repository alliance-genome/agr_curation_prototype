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

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("612 - HTPExpressionDatasetSampleAnnotation bulk upload - FMS")
@Order(612)
public class HTPExpressionDatasetSampleAnnotationFmsITCase extends BaseITCase {

	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
						.setParam("http.socket.timeout", 100000)
						.setParam("http.connection.timeout", 100000));
	}

	private final String htpDatasetSampleBulkPostEndpoint = "/api/htpexpressiondatasetsampleannotation/bulk/FB/htpexpressiondatasetsampleannotationfile";
	private final String htpDatasetSampleTestFilePath = "src/test/resources/bulk/fms/12_htp_dataset_sample/";
	private final String htpDatasetSampleFindEndpoint = "/api/htpexpressiondatasetsampleannotation/find?limit=100&page=0";
	private final String mmoTerm = "HTPMMO:assay001";
	private final String obiTermCurie = "HTPOBI:sample001";


	private void loadRequiredEntities() throws Exception {
		createMmoTerm(mmoTerm, "assay001");
		createObiTerm(obiTermCurie, "sample001");
	}

	@Test
	@Order(1)
	public void htpDatasetSampleBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();
		checkSuccessfulBulkLoad(htpDatasetSampleBulkPostEndpoint, htpDatasetSampleTestFilePath + "AF_01_all_fields.json");

		RestAssured.given().when().header("Content-Type", "application/json").body("{}")
				.post(htpDatasetSampleFindEndpoint)
				.then().statusCode(200).body("totalResults", is(1)).body("results", hasSize(1))
				.body("results[0].htpExpressionSample.curie", is("GEO:GSE38764"))
				.body("results[0].datasetIds", hasSize(1))
				.body("results[0].datasetIds[0].curie", is("GEO:GSE38764"))
				.body("results[0].htpExpressionSampleTitle", is("TEST TITLE"))
				.body("results[0].expressionAssayUsed.curie", is("HTPMMO:assay001"))
				.body("results[0].htpExpressionSampleType.curie", is("HTPOBI:sample001"))
				.body("results[0].htpExpressionSampleAge.stage.developmentalStageStart.curie", is("ZFS:001"))
				.body("results[0].genomicInformation.bioSampleAllele.modEntityId", is("AGA:Allele0001"))
				.body("results[0].microarraySampleDetails.channelId", is("WB:[cgc4349]:fem-3:A"))
				.body("results[0].microarraySampleDetails.channelNumber", is(1))
				.body("results[0].geneticSex.name", is("hermaphrodite"))
				.body("results[0].sequencingFormat.name", is("single"))
				.body("results[0].htpExpressionSampleLocations[0].anatomicalStructure.curie", is("ANAT:001"))
				.body("results[0].htpExpressionSampleLocations[0].anatomicalSubstructure.curie", is("ANAT:002"))
				.body("results[0].htpExpressionSampleLocations[0].cellularComponentTerm.curie", is("GOTEST:0012"))
				.body("results[0].htpExpressionSampleLocations[0].cellularComponentRibbonTerm.curie", is("GOSLIMTEST:0012"))
				.body("results[0].taxon.curie", is("NCBITaxon:6239"))
				.body("results[0].abundance", is("abundance test"))
				.body("results[0].assemblyVersions[0]", is("Zv9"));
	}

	@Test
	@Order(2)
	public void htpDatasetSampleBulkUploadMissingRequiredFields() throws Exception {

		checkFailedBulkLoad(htpDatasetSampleBulkPostEndpoint, htpDatasetSampleTestFilePath + "MR_01_no_sample_id_and_sample_title.json");
		checkFailedBulkLoad(htpDatasetSampleBulkPostEndpoint, htpDatasetSampleTestFilePath + "MR_02_no_sample_type.json");
		checkFailedBulkLoad(htpDatasetSampleBulkPostEndpoint, htpDatasetSampleTestFilePath + "MR_03_no_biosample_id_and_biosample_text.json");
		checkFailedBulkLoad(htpDatasetSampleBulkPostEndpoint, htpDatasetSampleTestFilePath + "MR_04_no_assay_type.json");
		checkFailedBulkLoad(htpDatasetSampleBulkPostEndpoint, htpDatasetSampleTestFilePath + "MR_05_no_dataset_ids.json");
	}

	@Test
	@Order(3)
	public void htpDatasetSampleBulkUploadEmptyRequiredFields() throws Exception {

		checkFailedBulkLoad(htpDatasetSampleBulkPostEndpoint, htpDatasetSampleTestFilePath + "ER_01_empty_sample_id_and_sample_title.json");
		checkFailedBulkLoad(htpDatasetSampleBulkPostEndpoint, htpDatasetSampleTestFilePath + "ER_02_empty_sample_type.json");
		checkFailedBulkLoad(htpDatasetSampleBulkPostEndpoint, htpDatasetSampleTestFilePath + "ER_03_empty_biosample_id_and_biosample_text.json");
		checkFailedBulkLoad(htpDatasetSampleBulkPostEndpoint, htpDatasetSampleTestFilePath + "ER_04_empty_assay_type.json");
		checkFailedBulkLoad(htpDatasetSampleBulkPostEndpoint, htpDatasetSampleTestFilePath + "ER_05_empty_dataset_ids.json");
	}

	@Test
	@Order(4)
	public void htpDatasetSampleBulkUploadInvalidFields() throws Exception {

		checkFailedBulkLoad(htpDatasetSampleBulkPostEndpoint, htpDatasetSampleTestFilePath + "IV_01_invalid_sex.json");
		checkFailedBulkLoad(htpDatasetSampleBulkPostEndpoint, htpDatasetSampleTestFilePath + "IV_02_invalid_assay_type.json");
		checkFailedBulkLoad(htpDatasetSampleBulkPostEndpoint, htpDatasetSampleTestFilePath + "IV_03_invalid_sample_type.json");
		checkFailedBulkLoad(htpDatasetSampleBulkPostEndpoint, htpDatasetSampleTestFilePath + "IV_04_invalid_taxon_id.json");
		checkFailedBulkLoad(htpDatasetSampleBulkPostEndpoint, htpDatasetSampleTestFilePath + "IV_05_invalid_genomic_information_biosample_id.json");
	}

	@Test
	@Order(5)
	public void htpDatasetSampleBulkUploadUpdateMissingNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(htpDatasetSampleBulkPostEndpoint, htpDatasetSampleTestFilePath + "UM_01_update_no_non_required_fields.json");

		RestAssured.given().when().header("Content-Type", "application/json").body("{}")
				.post(htpDatasetSampleFindEndpoint).then().statusCode(200).body("totalResults", is(1))
				.body("results", hasSize(1))
				.body("results[0]", not(hasKey("abundance")))
				.body("results[0]", not(hasKey("htpExpressionSampleTitle")))
				.body("results[0]", not(hasKey("taxon")))
				.body("results[0]", not(hasKey("geneticSex")))
				.body("results[0]", not(hasKey("sequencingFormat")))
				.body("results[0]", not(hasKey("microarraySampleDetails")))
				.body("results[0]", not(hasKey("htpExpressionSampleLocations")));
	}

	@Test
	@Order(6)
	public void htpDatasetSampleBulkUploadUpdateEmptyNonRequiredFields() throws Exception {

		checkSuccessfulBulkLoad(htpDatasetSampleBulkPostEndpoint, htpDatasetSampleTestFilePath + "UE_01_update_empty_non_required_fields.json");

		RestAssured.given().when().header("Content-Type", "application/json").body("{}")
				.post(htpDatasetSampleFindEndpoint).then().statusCode(200).body("totalResults", is(1))
				.body("results", hasSize(1))
				.body("results[0]", not(hasKey("htpExpressionSampleAge")))
				.body("results[0]", not(hasKey("abundance")))
				.body("results[0]", not(hasKey("htpExpressionSampleTitle")))
				.body("results[0]", not(hasKey("taxon")))
				.body("results[0]", not(hasKey("geneticSex")))
				.body("results[0]", not(hasKey("sequencingFormat")))
				.body("results[0]", not(hasKey("microarraySampleDetails")))
				.body("results[0]", not(hasKey("htpExpressionSampleLocations")));
	}


}
