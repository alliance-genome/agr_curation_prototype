package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.GenomeAssembly;
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
	private final String allele2 = "WB:AlleleWithVar2";
	private final String variantId = "var_NC_003279.8:g.1A>T";
	private final String reference = "AGRKB:000000001";
	private final String reference2 = "AGRKB:000000021";
	
	private void loadRequiredEntities() throws Exception {
		createSoTerm("SO:1000002", "substitution", false);
		createSoTerm("SO:0000667", "insertion", false);
		createSoTerm("SO:0002007", "MNV", false);
		createSoTerm("SO:1000008", "point_mutation", false);
		createSoTerm("SO:0000159", "deletion", false);
		createSoTerm("SO:1000032", "delins", false);
		createSoTerm("SO:0001587", "stop_gained", false);
		createSoTerm("SO:0001578", "stop_lost", false);
		Vocabulary nameTypeVocabulary = getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY);
		VocabularyTerm symbolTerm = getVocabularyTerm(nameTypeVocabulary, "nomenclature_symbol");
		DataProvider dataProvider = createDataProvider("WB", false);
		createAllele(allele, "TestAlleleWithVariant", "NCBITaxon:6239", symbolTerm, false, dataProvider);
		createAllele(allele2, "TestAlleleWithVariant2", "NCBITaxon:6239", symbolTerm, false, dataProvider);
		
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
			body("entity.variantType.curie", is("SO:1000002")).
			body("entity.sourceGeneralConsequence.curie", is("SO:0001587")).
			body("entity.curatedVariantGenomicLocations", hasSize(1)).
			body("entity.curatedVariantGenomicLocations[0].hgvs", is("NC_003279.8:g.1A>T")).
			body("entity.curatedVariantGenomicLocations[0].relation.name", is("located_on")).
			body("entity.curatedVariantGenomicLocations[0].variantGenomicLocationAssociationObject.name", is("I")).
			body("entity.curatedVariantGenomicLocations[0].start", is(1)).
			body("entity.curatedVariantGenomicLocations[0].end", is(1000)).
			body("entity.alleleVariantAssociations", hasSize(1)).
			body("entity.alleleVariantAssociations[0].relation.name", is("has_variant")).
			body("entity.alleleVariantAssociations[0].alleleAssociationSubject.modEntityId", is("WB:AlleleWithVar1")).
			body("entity.relatedNotes", hasSize(1)).
			body("entity.relatedNotes[0].internal", is(false)).
			body("entity.relatedNotes[0].freeText", is("This is a test note.")).
			body("entity.relatedNotes[0].noteType.name", is("comment")).
			body("entity.relatedNotes[0].references[0].curie", is(reference)).
			body("entity.crossReferences", hasSize(1)).
			body("entity.crossReferences[0].referencedCurie", is("TEST:WBVar00252636")).
			body("entity.crossReferences[0].displayName", is("TEST:WBVar00252636")).
			body("entity.crossReferences[0].resourceDescriptorPage.name", is("homepage"));

	}
	
	@Test
	@Order(2)
	public void variantFmsBulkUploadUpdate() throws Exception {
		HashMap<String, HashMap<String, Integer>> params = new HashMap<>();
		params.put("Entities", createCountParams(1, 0, 1, 0));
		params.put("Locations", createCountParams(1, 0, 1, 0));
		params.put("Associations", createCountParams(1, 0, 1, 0));

		checkBulkLoadRecordCounts(variantFmsBulkPostEndpoint, variantFmsTestFilePath + "UD_01_update_variant.json", params);
		
		RestAssured.given().
			when().
			get(variantGetEndpoint + variantId).
			then().
			statusCode(200).
			body("entity.modInternalId", is(variantId)).
			body("entity.taxon.curie", is("NCBITaxon:6239")).
			body("entity.dataProvider.sourceOrganization.abbreviation", is("WB")).
			body("entity.variantType.curie", is("SO:1000008")).
			body("entity.sourceGeneralConsequence.curie", is("SO:0001578")).
			body("entity.curatedVariantGenomicLocations", hasSize(1)).
			body("entity.curatedVariantGenomicLocations[0].hgvs", is("NC_003279.8:g.1A>T")).
			body("entity.curatedVariantGenomicLocations[0].relation.name", is("located_on")).
			body("entity.curatedVariantGenomicLocations[0].variantGenomicLocationAssociationObject.name", is("I")).
			body("entity.curatedVariantGenomicLocations[0].start", is(1)).
			body("entity.curatedVariantGenomicLocations[0].end", is(1000)).
			body("entity.alleleVariantAssociations", hasSize(2)).
			body("entity.alleleVariantAssociations[0].relation.name", is("has_variant")).
			body("entity.alleleVariantAssociations[0].alleleAssociationSubject.modEntityId", is("WB:AlleleWithVar1")).
			body("entity.alleleVariantAssociations[1].relation.name", is("has_variant")).
			body("entity.alleleVariantAssociations[1].alleleAssociationSubject.modEntityId", is("WB:AlleleWithVar2")).
			body("entity.relatedNotes", hasSize(1)).
			body("entity.relatedNotes[0].internal", is(false)).
			body("entity.relatedNotes[0].freeText", is("This is an updated test note.")).
			body("entity.relatedNotes[0].noteType.name", is("comment")).
			body("entity.relatedNotes[0].references[0].curie", is(reference2)).
			body("entity.crossReferences", hasSize(1)).
			body("entity.crossReferences[0].referencedCurie", is("TEST:WBVar00252637")).
			body("entity.crossReferences[0].displayName", is("TEST:WBVar00252637")).
			body("entity.crossReferences[0].resourceDescriptorPage.name", is("homepage"));

	}
	
	@Test
	@Order(3)
	public void variantFmsBulkUploadMissingRequiredFields() throws Exception {
		HashMap<String, HashMap<String, Integer>> params = new HashMap<>();
		params.put("Entities", createCountParams(1, 1, 0, 0));
		params.put("Locations", createCountParams(1, 1, 0, 0));
		params.put("Associations", createCountParams(1, 1, 0, 0));
		
		checkBulkLoadRecordCounts(variantFmsBulkPostEndpoint, variantFmsTestFilePath + "MR_01_no_start.json", params);
		checkBulkLoadRecordCounts(variantFmsBulkPostEndpoint, variantFmsTestFilePath + "MR_02_no_end.json", params);
		checkBulkLoadRecordCounts(variantFmsBulkPostEndpoint, variantFmsTestFilePath + "MR_03_no_sequence_of_reference_accession_number.json", params);
		checkBulkLoadRecordCounts(variantFmsBulkPostEndpoint, variantFmsTestFilePath + "MR_04_no_type.json", params);
		checkBulkLoadRecordCounts(variantFmsBulkPostEndpoint, variantFmsTestFilePath + "MR_05_no_genomic_reference_sequence.json", params);
		checkBulkLoadRecordCounts(variantFmsBulkPostEndpoint, variantFmsTestFilePath + "MR_06_no_genomic_variant_sequence.json", params);

		params.put("Entities", createCountParams(1, 0, 1, 0));
		params.put("Locations", createCountParams(1, 0, 1, 0));
		checkBulkLoadRecordCounts(variantFmsBulkPostEndpoint, variantFmsTestFilePath + "MR_07_no_allele_id.json", params);	
	}
	
	@Test
	@Order(4)
	public void variantFmsBulkUploadEmptyRequiredFields() throws Exception {
		HashMap<String, HashMap<String, Integer>> params = new HashMap<>();
		params.put("Entities", createCountParams(1, 1, 0, 0));
		params.put("Locations", createCountParams(1, 1, 0, 0));
		params.put("Associations", createCountParams(1, 1, 0, 0));
		
		checkBulkLoadRecordCounts(variantFmsBulkPostEndpoint, variantFmsTestFilePath + "ER_01_empty_sequence_of_reference_accession_number.json", params);
		checkBulkLoadRecordCounts(variantFmsBulkPostEndpoint, variantFmsTestFilePath + "ER_02_empty_type.json", params);
		checkBulkLoadRecordCounts(variantFmsBulkPostEndpoint, variantFmsTestFilePath + "ER_03_empty_genomic_reference_sequence.json", params);
		checkBulkLoadRecordCounts(variantFmsBulkPostEndpoint, variantFmsTestFilePath + "ER_04_empty_genomic_variant_sequence.json", params);

		params.put("Entities", createCountParams(1, 0, 1, 0));
		params.put("Locations", createCountParams(1, 0, 1, 0));
		checkBulkLoadRecordCounts(variantFmsBulkPostEndpoint, variantFmsTestFilePath + "ER_05_empty_allele_id.json", params);	
	}
	
	@Test
	@Order(3)
	public void variantFmsBulkUploadInvalidFields() throws Exception {
		HashMap<String, HashMap<String, Integer>> params = new HashMap<>();
		params.put("Entities", createCountParams(1, 1, 0, 0));
		params.put("Locations", createCountParams(1, 1, 0, 0));
		params.put("Associations", createCountParams(1, 1, 0, 0));
		checkBulkLoadRecordCounts(variantFmsBulkPostEndpoint, variantFmsTestFilePath + "IV_01_invalid_type.json", params);
		checkBulkLoadRecordCounts(variantFmsBulkPostEndpoint, variantFmsTestFilePath + "IV_02_invalid_type_for_fms_submissions.json", params);
		checkBulkLoadRecordCounts(variantFmsBulkPostEndpoint, variantFmsTestFilePath + "IV_03_invalid_consequence.json", params);

		params.put("Entities", createCountParams(1, 0, 1, 0));
		params.put("Associations", createCountParams(1, 0, 1, 0));
		
		checkBulkLoadRecordCounts(variantFmsBulkPostEndpoint, variantFmsTestFilePath + "IV_04_invalid_sequence_of_reference_accession_number.json", params);
		
		params.put("Locations", createCountParams(1, 0, 1, 0));
		params.put("Associations", createCountParams(1, 1, 0, 0));
		checkBulkLoadRecordCounts(variantFmsBulkPostEndpoint, variantFmsTestFilePath + "IV_05_invalid_allele_id.json", params);	
	}

}
