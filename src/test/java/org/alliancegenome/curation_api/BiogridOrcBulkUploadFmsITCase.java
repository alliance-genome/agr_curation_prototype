package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
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

	ResourceDescriptorPage resourceDescriptorPage;

	private final String biogridOrcBulkPostEndpoint = "/api/biogrid-orc/bulk/WB/biogridfile";
	private final String biogridOrcTestFilePath = "src/test/resources/bulk/fms/12_biogrid/";
	private final String biogridOrcFindEndpoint = "/api/cross-reference/find?limit=100&page=0";

	private void loadRequiredEntities() throws Exception {
		Vocabulary nameTypeVocabulary = getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY);
		VocabularyTerm symbolTerm = getVocabularyTerm(nameTypeVocabulary, "nomenclature_symbol");
		Organization dataProvider = getOrganization("WB");
		createGeneWithXref("WB:XrefTest01", "NCBITaxon:6239", symbolTerm, false, dataProvider, "NCBI_Gene:108101");
		createGeneWithXref("WB:XrefTest02", "NCBITaxon:6239", symbolTerm, false, dataProvider, "NCBI_Gene:100001");
		ResourceDescriptor rd = createResourceDescriptor("NCBI_Gene");
		createResourceDescriptorPage("biogrid/orcs", "http://test.org", rd);
	}

	@Test
	@Order(1)
	public void biogridOrcBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();
		checkSuccessfulBulkLoad(biogridOrcBulkPostEndpoint, biogridOrcTestFilePath + "AF_01_all_fields.json");

		RestAssured.given()
				.when()
				.header("Content-Type", "application/json")
				.body("{\"referencedCurie\": \"NCBI_Gene:108101\", \"displayName\": \"BioGRID CRISPR Screen Cell Line Phenotypes\"}")
				.post(biogridOrcFindEndpoint)
				.then().statusCode(200)
				.body("totalResults", is(1))
				.body("results", hasSize(1));
	}

	@Test
	@Order(2)
	public void biogridOrcBulkUploadDuplicateEntries() throws Exception {
		checkSuccessfulBulkLoad(biogridOrcBulkPostEndpoint, biogridOrcTestFilePath + "DE_01_duplicate_entries.json");

		RestAssured.given()
				.when()
				.header("Content-Type", "application/json")
				.body("{\"referencedCurie\": \"NCBI_Gene:100001\", \"displayName\": \"BioGRID CRISPR Screen Cell Line Phenotypes\"}")
				.post(biogridOrcFindEndpoint)
				.then().statusCode(200)
				.body("totalResults", is(1))
				.body("results", hasSize(1));
	}

}
