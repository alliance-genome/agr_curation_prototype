package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;

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
@DisplayName("610 - Variant data bulk upload - FMS")
@Order(610)
public class VariantFmsITCase extends BaseITCase {

	// These tests require: GeneBulkUploadITCase and VocabularyTermITCase
	
	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 100000)
					.setParam("http.connection.timeout", 100000));
	}

	private final String variantFmsTestFilePath = "src/test/resources/bulk/fms/10_variant/";
	private final String variantFmsBulkPostEndpoint = "/api/variant/bulk/WB/fmsvariants";
	private final String variantGetEndpoint = "/api/variant/";
	private final String allele = "WB:AlleleWithVar1";
	private final String assemblyComponent = "RefSeq:NC_001.1";
	private final String variantId = "var";
	
	private void loadRequiredEntities() throws Exception {
		createSoTerm("SO:1000002", "substitution", false);
		createSoTerm("SO:0000667", "insertion", false);
		createSoTerm("SO:0002007", "MNV", false);
		createSoTerm("SO:1000008", "point_mutation", false);
		createSoTerm("SO:0000159", "deletion", false);
		createSoTerm("SO:1000032", "delins", false);
		createSoTerm("SO:0001587", "stop_gained", false);
		Vocabulary nameTypeVocabulary = getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY);
		VocabularyTerm symbolTerm = getVocabularyTerm(nameTypeVocabulary, "nomenclature_symbol");
		DataProvider dataProvider = createDataProvider("WB", false);
		createAllele(allele, "TestAlleleWithVariant", "NCBITaxon:6239", symbolTerm, false, dataProvider);
		createAssemblyComponent(assemblyComponent, "Test1", dataProvider);
		
	}
	
	@Test
	@Order(1)
	public void variantFmsBulkUpload() throws Exception {
		loadRequiredEntities();

		HashMap<String, HashMap<String, Integer>> params = new HashMap<>();
		params.put("Entities", createCountParams(1, 0, 1, 0));
		params.put("Locations", createCountParams(1, 0, 1, 0));
		params.put("Associations", createCountParams(1, 0, 1, 0));

		checkBulkLoadRecordCounts(variantFmsBulkPostEndpoint, variantFmsTestFilePath + "AF_01_all_fields.json", params);
		
		RestAssured.given().
			when().
			get(variantGetEndpoint + variantId).
			then().
			statusCode(200).
			body("entity.modInternalId", is(variantId)).
			body("entity.taxon.curie", is("NCBITaxon:6239")).
			body("entity.dataProvider.sourceOrganization.abbreviation", is("WB")).
			body("entity.transcriptType.curie", is("SO:0000234")).
			body("entity.curatedVariantGenomicLocationAssociations", hasSize(1)).
			body("entity.curatedVariantGenomicLocationAssociations[0].relation.name", is("located_on")).
			body("entity.curatedVariantGenomicLocationAssociations[0].variantGenomicLocationAssociationObject.name", is("Test1")).
			body("entity.curatedVariantGenomicLocationAssociations[0].start", is(1)).
			body("entity.curatedVariantGenomicLocationAssociations[0].end", is(1000)).
			body("entity.alleleVariantAssociations", hasSize(1)).
			body("entity.alleleVariantAssociations[0].relation.name", is("has_variant")).
			body("entity.alleleVariantAssociations[0].alleleAssociationSubject.modEntityId", is("WB:AlleleWithVar1"));

	}

}
