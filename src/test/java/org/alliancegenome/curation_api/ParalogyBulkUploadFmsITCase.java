package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
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
@DisplayName("607 - Paralogy bulk upload - FMS")
@Order(607)
public class ParalogyBulkUploadFmsITCase extends BaseITCase {

	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
						.setParam("http.socket.timeout", 100000)
						.setParam("http.connection.timeout", 100000));
	}

	private final String paralogyBulkPostEndpoint = "/api/paralogy/bulk/WB/paralogyfile";
	private final String paralogyTestFilePath = "src/test/resources/bulk/fms/05_paralogy/";
	private final String paralogyFindEndpoint = "/api/paralogy/find?limit=100&page=0";
	private String gene1 = "PARATEST:Gene000100";
	private String gene2 = "PARATEST:Gene000200";

	private void loadRequiredEntities() throws Exception {
		Vocabulary nameTypeVocabulary = getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY);
		VocabularyTerm symbolTerm = getVocabularyTerm(nameTypeVocabulary, "nomenclature_symbol");
		DataProvider dataProvider = createDataProvider("WB", false);
		createGenes(List.of(gene1, gene2), "NCBITaxon:6239", symbolTerm, false, dataProvider);
	}

	@Test
	@Order(1)
	public void paralogyBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();

		checkSuccessfulBulkLoad(paralogyBulkPostEndpoint, paralogyTestFilePath + "AF_01_all_fields.json");

		RestAssured.given().when().header("Content-Type", "application/json").body("{}").post(paralogyFindEndpoint)
				.then().statusCode(200).body("totalResults", is(1)).body("results", hasSize(1))
				.body("results[0].confidence.name", is("moderate"))
				.body("results[0].subjectGene.modEntityId", is("PARATEST:Gene000100"))
				.body("results[0].objectGene.modEntityId", is("PARATEST:Gene000200"))
				.body("results[0].identity", is(65))
				.body("results[0].length", is(466))
				.body("results[0].predictionMethodsMatched", hasSize(1))
				.body("results[0].predictionMethodsMatched[0].name", is("OrthoFinder"))
				.body("results[0].predictionMethodsNotMatched", hasSize(4))
				.body("results[0].predictionMethodsNotCalled", hasSize(2))
				.body("results[0].rank", is(2))
				.body("results[0].similarity", is(78))
				.body("results[0].subjectGene.taxon.curie", is("NCBITaxon:6239"))
				.body("results[0].objectGene.taxon.curie", is("NCBITaxon:6239"));
	}


	@Test
	@Order(2)
	public void paralogyBulkUploadMissingRequiredFields() throws Exception {

		checkFailedBulkLoad(paralogyBulkPostEndpoint, paralogyTestFilePath + "MR_01_no_gene1.json");
		checkFailedBulkLoad(paralogyBulkPostEndpoint, paralogyTestFilePath + "MR_02_no_gene2.json");
		checkFailedBulkLoad(paralogyBulkPostEndpoint, paralogyTestFilePath + "MR_03_no_species.json");
		checkFailedBulkLoad(paralogyBulkPostEndpoint, paralogyTestFilePath + "MR_04_no_confidence.json");
		checkFailedBulkLoad(paralogyBulkPostEndpoint, paralogyTestFilePath + "MR_07_no_rank.json");
	}

	@Test
	@Order(3)
	public void paralogyBulkUploadEmptyRequiredFields() throws Exception {

		checkFailedBulkLoad(paralogyBulkPostEndpoint, paralogyTestFilePath + "ER_01_empty_gene1.json");
		checkFailedBulkLoad(paralogyBulkPostEndpoint, paralogyTestFilePath + "ER_02_empty_gene2.json");
		checkFailedBulkLoad(paralogyBulkPostEndpoint, paralogyTestFilePath + "ER_03_empty_confidence.json");

	}

	@Test
	@Order(4)
	public void paralogyBulkUploadInvalidFields() throws Exception {

		checkFailedBulkLoad(paralogyBulkPostEndpoint, paralogyTestFilePath + "IV_01_invalid_gene1.json");
		checkFailedBulkLoad(paralogyBulkPostEndpoint, paralogyTestFilePath + "IV_02_invalid_gene2.json");
		checkFailedBulkLoad(paralogyBulkPostEndpoint, paralogyTestFilePath + "IV_03_invalid_species.json");
		checkFailedBulkLoad(paralogyBulkPostEndpoint, paralogyTestFilePath + "IV_04_invalid_confidence.json");
		checkFailedBulkLoad(paralogyBulkPostEndpoint, paralogyTestFilePath + "IV_05_invalid_predictionMethodsMatched.json");
		checkFailedBulkLoad(paralogyBulkPostEndpoint, paralogyTestFilePath + "IV_06_invalid_predictionMethodsNotMatched.json");
		checkFailedBulkLoad(paralogyBulkPostEndpoint, paralogyTestFilePath + "IV_07_invalid_predictionMethodsNotCalled.json");
		checkFailedBulkLoad(paralogyBulkPostEndpoint, paralogyTestFilePath + "IV_08_invalid_species_for_genes.json");

	}

	@Test
	@Order(5)
	public void paralogyBulkUploadUpdateMissingNonRequiredFields() throws Exception {

		checkSuccessfulBulkLoad(paralogyBulkPostEndpoint, paralogyTestFilePath + "UM_01_update_no_non_required_fields.json");

		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post(paralogyFindEndpoint).
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0]", not(hasKey("predictionMethodsMatched"))).
			body("results[0]", not(hasKey("predictionMethodsNotMatched"))).
			body("results[0]", not(hasKey("predictionMethodsNotCalled")));
	}

	@Test
	@Order(6)
	public void paralogyBulkUploadUpdateEmptyNonRequiredFields() throws Exception {

		checkSuccessfulBulkLoad(paralogyBulkPostEndpoint, paralogyTestFilePath + "AF_01_all_fields.json");

		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post(paralogyFindEndpoint).
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0]", hasKey("predictionMethodsMatched")).
			body("results[0]", hasKey("predictionMethodsNotMatched")).
			body("results[0]", hasKey("predictionMethodsNotCalled"));
		
		checkSuccessfulBulkLoad(paralogyBulkPostEndpoint, paralogyTestFilePath + "UE_01_update_empty_non_required_fields.json");

		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post(paralogyFindEndpoint).
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0]", not(hasKey("predictionMethodsMatched"))).
			body("results[0]", not(hasKey("predictionMethodsNotMatched"))).
			body("results[0]", not(hasKey("predictionMethodsNotCalled")));
	}

}
