package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasKey;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.apache.commons.codec.digest.DigestUtils;
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
@DisplayName("611 - VEP data bulk upload - FMS")
@Order(611)
public class VepFmsITCase extends BaseITCase {

	// These tests require: GeneBulkUploadITCase and VocabularyTermITCase
	
	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 100000)
					.setParam("http.connection.timeout", 100000));
	}

	private final String vepFmsTestFilePath = "src/test/resources/bulk/fms/11_vep/";
	private final String vepTranscriptFmsBulkPostEndpoint = "/api/predictedvariantconsequence/bulk/WB/transcriptConsequenceFile";
	private final String vepGeneFmsBulkPostEndpoint = "/api/predictedvariantconsequence/bulk/WB/geneConsequenceFile";
	private final String variantHgvs = "NC_003279.8:g.1A>T";
	private final String variantId = DigestUtils.md5Hex(variantHgvs);
	private final String variantGetEndpoint = "/api/variant/";
	
	private void loadRequiredEntities() throws Exception {
		createSoTerm("SO:0001574", "splice_acceptor_variant", false);
		createSoTerm("SO:0001630", "splice_donor_5th_base_variant", false);
	}
	
	@Test
	@Order(1)
	public void vepTranscriptFmsBulkUpload() throws Exception {
		loadRequiredEntities();
		
		checkSuccessfulBulkLoad(vepTranscriptFmsBulkPostEndpoint, vepFmsTestFilePath + "AF_01_all_fields.json");
		
		RestAssured.given().
			when().
			get(variantGetEndpoint + variantId).
			then().
			statusCode(200).
			body("entity.modInternalId", is(variantId)).
			body("entity.curatedVariantGenomicLocations[0].hgvs", is("NC_003279.8:g.1A>T")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences", hasSize(1)).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].variantTranscript.modInternalId", is("WB:Y74C9A.2a.1")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].vepImpact.name", is("MODERATE")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].vepConsequences", hasSize(1)).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].vepConsequences[0].name", is("splice_acceptor_variant")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].polyphenPrediction.name", is("probably_damaging")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].polyphenScore", is(0.993F)).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].siftPrediction.name", is("tolerated")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].siftScore", is(0F)).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].aminoAcidReference", is("T")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].aminoAcidVariant", is("I")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].codonReference", is("aCc")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].codonVariant", is("aTc")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].calculatedCdnaStart", is(3)).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].calculatedCdnaEnd", is(800)).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].calculatedCdsStart", is(1)).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].calculatedCdsEnd", is(600)).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].calculatedProteinStart", is(246)).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0]", not(hasKey("calculatedProteinEnd"))).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].hgvsProteinNomenclature", is("WB:CE49439:p.Thr10Ile")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].hgvsCodingNomenclature", is("WB:Y74C9A.2a.1:c.29T>I")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].geneLevelConsequence", is(false));
	}
	
	@Test
	@Order(2)
	public void vepGeneBulkUpload() throws Exception {
		checkSuccessfulBulkLoad(vepGeneFmsBulkPostEndpoint, vepFmsTestFilePath + "AF_01_all_fields.json");
		
		RestAssured.given().
			when().
			get(variantGetEndpoint + variantId).
			then().
			statusCode(200).
			body("entity.modInternalId", is(variantId)).
			body("entity.curatedVariantGenomicLocations[0].hgvs", is("NC_003279.8:g.1A>T")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].variantTranscript.modInternalId", is("WB:Y74C9A.2a.1")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].vepImpact.name", is("MODERATE")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].vepConsequences", hasSize(1)).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].vepConsequences[0].name", is("splice_acceptor_variant")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].polyphenPrediction.name", is("probably_damaging")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].polyphenScore", is(0.993F)).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].siftPrediction.name", is("tolerated")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].siftScore", is(0F)).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].aminoAcidReference", is("T")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].aminoAcidVariant", is("I")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].codonReference", is("aCc")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].codonVariant", is("aTc")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].calculatedCdnaStart", is(3)).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].calculatedCdnaEnd", is(800)).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].calculatedCdsStart", is(1)).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].calculatedCdsEnd", is(600)).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].calculatedProteinStart", is(246)).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0]", not(hasKey("calculatedProteinEnd"))).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].hgvsProteinNomenclature", is("WB:CE49439:p.Thr10Ile")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].hgvsCodingNomenclature", is("WB:Y74C9A.2a.1:c.29T>I")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].geneLevelConsequence", is(true));
	}
	
	@Test
	@Order(3)
	public void vepTranscriptMissingRequiredFields() throws Exception {
		checkFailedBulkLoad(vepTranscriptFmsBulkPostEndpoint, vepFmsTestFilePath + "MR_01_no_uploaded_variation.json");
		checkFailedBulkLoad(vepTranscriptFmsBulkPostEndpoint, vepFmsTestFilePath + "MR_02_no_feature.json");
		checkFailedBulkLoad(vepTranscriptFmsBulkPostEndpoint, vepFmsTestFilePath + "MR_03_no_consequence.json");
		checkFailedBulkLoad(vepTranscriptFmsBulkPostEndpoint, vepFmsTestFilePath + "MR_04_no_impact.json");
	}
	
	@Test
	@Order(4)
	public void vepTranscriptEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(vepTranscriptFmsBulkPostEndpoint, vepFmsTestFilePath + "ER_01_empty_uploaded_variation.json");
		checkFailedBulkLoad(vepTranscriptFmsBulkPostEndpoint, vepFmsTestFilePath + "ER_02_empty_feature.json");
		checkFailedBulkLoad(vepTranscriptFmsBulkPostEndpoint, vepFmsTestFilePath + "ER_03_empty_consequence.json");
		checkFailedBulkLoad(vepTranscriptFmsBulkPostEndpoint, vepFmsTestFilePath + "ER_04_empty_impact.json");
	}
	
	@Test
	@Order(5)
	public void vepTranscriptInvalidFields() throws Exception {
		checkFailedBulkLoad(vepTranscriptFmsBulkPostEndpoint, vepFmsTestFilePath + "IV_01_invalid_uploaded_variation.json");
		checkFailedBulkLoad(vepTranscriptFmsBulkPostEndpoint, vepFmsTestFilePath + "IV_02_invalid_feature.json");
		checkFailedBulkLoad(vepTranscriptFmsBulkPostEndpoint, vepFmsTestFilePath + "IV_03_invalid_consequence.json");
		checkFailedBulkLoad(vepTranscriptFmsBulkPostEndpoint, vepFmsTestFilePath + "IV_04_invalid_cdna_position.json");
		checkFailedBulkLoad(vepTranscriptFmsBulkPostEndpoint, vepFmsTestFilePath + "IV_05_invalid_cds_position.json");
		checkFailedBulkLoad(vepTranscriptFmsBulkPostEndpoint, vepFmsTestFilePath + "IV_06_invalid_protein_position.json");
		checkFailedBulkLoad(vepTranscriptFmsBulkPostEndpoint, vepFmsTestFilePath + "IV_07_invalid_amino_acids.json");
		checkFailedBulkLoad(vepTranscriptFmsBulkPostEndpoint, vepFmsTestFilePath + "IV_08_invalid_codons.json");
		checkFailedBulkLoad(vepTranscriptFmsBulkPostEndpoint, vepFmsTestFilePath + "IV_09_invalid_impact.json");
		checkFailedBulkLoad(vepTranscriptFmsBulkPostEndpoint, vepFmsTestFilePath + "IV_10_invalid_polyphen.json");
		checkFailedBulkLoad(vepTranscriptFmsBulkPostEndpoint, vepFmsTestFilePath + "IV_11_invalid_sift.json");
		checkFailedBulkLoad(vepGeneFmsBulkPostEndpoint, vepFmsTestFilePath + "IV_12_invalid_variant_transcript_pair.json");
	}
	
	@Test
	@Order(6)
	public void vepTranscriptUpdate() throws Exception {
		checkSuccessfulBulkLoad(vepTranscriptFmsBulkPostEndpoint, vepFmsTestFilePath + "UD_01_update.json");
		
		RestAssured.given().
			when().
			get(variantGetEndpoint + variantId).
			then().
			statusCode(200).
			body("entity.modInternalId", is(variantId)).
			body("entity.curatedVariantGenomicLocations[0].hgvs", is("NC_003279.8:g.1A>T")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences", hasSize(1)).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].variantTranscript.modInternalId", is("WB:Y74C9A.2a.1")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].vepImpact.name", is("MODIFIER")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].vepConsequences", hasSize(1)).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].vepConsequences[0].name", is("splice_donor_5th_base_variant")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].polyphenPrediction.name", is("possibly_damaging")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].polyphenScore", is(0.8F)).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].siftPrediction.name", is("deleterious_low_confidence")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].siftScore", is(0.767F)).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].aminoAcidReference", is("M")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].aminoAcidVariant", is("N")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].codonReference", is("aCt")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].codonVariant", is("aTt")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].calculatedCdnaStart", is(2)).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].calculatedCdnaEnd", is(900)).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].calculatedCdsStart", is(3)).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].calculatedCdsEnd", is(500)).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].calculatedProteinStart", is(247)).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].calculatedProteinEnd", is(250)).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].hgvsProteinNomenclature", is("WB:CE49439:p.Met10Neo")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].hgvsCodingNomenclature", is("WB:Y74C9A.2a.1:c.29M>N")).
			body("entity.curatedVariantGenomicLocations[0].predictedVariantConsequences[0].geneLevelConsequence", is(true));
	}
	
	@Test
	@Order(7)
	public void vepSkipIntergenic() throws Exception {
		checkSkippedBulkLoad(vepTranscriptFmsBulkPostEndpoint, vepFmsTestFilePath + "US_01_unsupported_intergenic.json");
		checkSkippedBulkLoad(vepGeneFmsBulkPostEndpoint, vepFmsTestFilePath + "US_01_unsupported_intergenic.json");
	}
	
}
