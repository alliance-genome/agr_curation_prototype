package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.time.OffsetDateTime;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
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
@DisplayName("106 - Variant bulk upload")
@Order(106)
public class VariantBulkUploadITCase extends BaseITCase {
	
	private String variantType = "SO:VT001";
	private String variantType2 = "SO:VT002";
	private String variantStatus = "status_test";
	private String variantStatus2 = "status_test_2";
	private String sourceGeneralConsequence = "SO:SGC001";
	private String sourceGeneralConsequence2 = "SO:SGC002";
	private String reference = "AGRKB:000000001";
	private String reference2 = "AGRKB:000000021";
	private String noteType = "comment";
	private String noteType2 = "test_comment";
	
	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 60000)
					.setParam("http.connection.timeout", 60000));
	}

	private final String variantBulkPostEndpoint = "/api/variant/bulk/WB/variants";
	private final String variantBulkPostEndpointRGD = "/api/variant/bulk/RGD/variants";
	private final String variantGetEndpoint = "/api/variant/";
	private final String variantTestFilePath = "src/test/resources/bulk/06_variant/";

	private void loadRequiredEntities() throws Exception {
		createSoTerm(variantType, "Test variant type SOTerm", false);
		createSoTerm(variantType2, "Second test variant type SOTerm", false);
		createSoTerm(sourceGeneralConsequence, "Test source general consequence SOTerm", false);
		createSoTerm(sourceGeneralConsequence2, "Second test source general consequence SOTerm", false);
		Vocabulary variantStatusVocabulary = getVocabulary(VocabularyConstants.VARIANT_STATUS_VOCABULARY);
		createVocabularyTerm(variantStatusVocabulary, variantStatus, false);
		createVocabularyTerm(variantStatusVocabulary, variantStatus2, false);
		Vocabulary noteTypeVocab = getVocabulary("note_type");
		createVocabularyTerm(noteTypeVocab, noteType2, false);
		addVocabularyTermToSet(VocabularyConstants.VARIANT_NOTE_TYPES_VOCABULARY_TERM_SET, noteType2, noteTypeVocab, false);
	}
	
	@Test
	@Order(1)
	public void variantBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();
		
		checkSuccessfulBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "AF_01_all_fields.json");
	
		RestAssured.given().
			when().
			get(variantGetEndpoint + "VARIANTTEST:Variant0001").
			then().
			statusCode(200).
			body("entity.modEntityId", is("VARIANTTEST:Variant0001")).
			body("entity.taxon.curie", is("NCBITaxon:6239")).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.createdBy.uniqueId", is("VARIANTTEST:Person0001")).
			body("entity.updatedBy.uniqueId", is("VARIANTTEST:Person0002")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.variantType.curie", is(variantType)).
			body("entity.variantStatus.name", is(variantStatus)).
			body("entity.sourceGeneralConsequence.curie", is(sourceGeneralConsequence)).
			body("entity.relatedNotes", hasSize(1)).
			body("entity.relatedNotes[0].internal", is(false)).
			body("entity.relatedNotes[0].obsolete", is(true)).
			body("entity.relatedNotes[0].updatedBy.uniqueId", is("VARIANTTEST:Person0002")).
			body("entity.relatedNotes[0].createdBy.uniqueId", is("VARIANTTEST:Person0001")).
			body("entity.relatedNotes[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.relatedNotes[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.relatedNotes[0].freeText", is("Test note")).
			body("entity.relatedNotes[0].noteType.name", is(noteType)).
			body("entity.relatedNotes[0].references[0].curie", is(reference)).
			body("entity.dataProvider.sourceOrganization.abbreviation", is("WB")).
			body("entity.dataProvider.crossReference.referencedCurie", is("TEST:0001")).
			body("entity.dataProvider.crossReference.displayName", is("TEST:0001")).
			body("entity.dataProvider.crossReference.resourceDescriptorPage.name", is("homepage"));
	}
	
	@Test
	@Order(2)
	public void variantBulkUploadUpdateCheckFields() throws Exception {
		checkSuccessfulBulkLoad(variantBulkPostEndpointRGD, variantTestFilePath + "UD_01_update_all_except_default_fields.json");
	
		RestAssured.given().
			when().
			get(variantGetEndpoint + "VARIANTTEST:Variant0001").
			then().
			statusCode(200).
			body("entity.modEntityId", is("VARIANTTEST:Variant0001")).
			body("entity.taxon.curie", is("NCBITaxon:10116")).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.createdBy.uniqueId", is("VARIANTTEST:Person0002")).
			body("entity.updatedBy.uniqueId", is("VARIANTTEST:Person0001")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString())).
			body("entity.variantType.curie", is(variantType2)).
			body("entity.variantStatus.name", is(variantStatus2)).
			body("entity.sourceGeneralConsequence.curie", is(sourceGeneralConsequence2)).
			body("entity.relatedNotes", hasSize(1)).
			body("entity.relatedNotes[0].internal", is(true)).
			body("entity.relatedNotes[0].obsolete", is(false)).
			body("entity.relatedNotes[0].updatedBy.uniqueId", is("VARIANTTEST:Person0001")).
			body("entity.relatedNotes[0].createdBy.uniqueId", is("VARIANTTEST:Person0002")).
			body("entity.relatedNotes[0].dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString())).
			body("entity.relatedNotes[0].dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
			body("entity.relatedNotes[0].freeText", is("Test note 2")).
			body("entity.relatedNotes[0].noteType.name", is(noteType2)).
			body("entity.relatedNotes[0].references[0].curie", is(reference2)).
			body("entity.dataProvider.sourceOrganization.abbreviation", is("RGD")).
			body("entity.dataProvider.crossReference.referencedCurie", is("TEST2:0001")).
			body("entity.dataProvider.crossReference.displayName", is("TEST2:0001")).
			body("entity.dataProvider.crossReference.resourceDescriptorPage.name", is("homepage2"));
	}
	
	@Test
	@Order(3)
	public void variantBulkUploadMissingRequiredFields() throws Exception {
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "MR_01_no_mod_ids.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "MR_02_no_taxon.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "MR_03_no_variant_type.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "MR_04_no_data_provider.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "MR_05_no_data_provider_source_organization_abbreviation.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "MR_06_no_data_provider_cross_reference_referenced_curie.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "MR_07_no_data_provider_cross_reference_display_name.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "MR_08_no_data_provider_cross_reference_prefix.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "MR_09_no_data_provider_cross_reference_page_area.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "MR_10_no_related_notes_note_type.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "MR_11_no_related_notes_free_text.json");
	}
	
	@Test
	@Order(4)
	public void variantBulkUploadEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "ER_01_empty_mod_ids.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "ER_02_empty_taxon.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "ER_03_empty_variant_type.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "ER_04_empty_data_provider_source_organization_abbreviation.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "ER_05_empty_data_provider_cross_reference_referenced_curie.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "ER_06_empty_data_provider_cross_reference_display_name.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "ER_07_empty_data_provider_cross_reference_prefix.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "ER_08_empty_data_provider_cross_reference_page_area.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "ER_09_empty_related_notes_note_type.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "ER_10_empty_related_notes_free_text.json");
	}
	
	@Test
	@Order(5)
	public void variantBulkUploadInvalidFields() throws Exception {
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "IV_01_invalid_date_created.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "IV_02_invalid_date_updated.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "IV_03_invalid_taxon.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "IV_04_invalid_variant_type.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "IV_05_invalid_variant_status.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "IV_06_invalid_source_general_consequence.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "IV_07_invalid_data_provider_source_organization_abbreviation.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "IV_08_invalid_data_provider_cross_reference_prefix.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "IV_09_invalid_data_provider_cross_reference_page_area.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "IV_10_invalid_related_notes_note_type.json");
		checkFailedBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "IV_11_invalid_related_notes_evidence.json");
	}
	
	@Test
	@Order(6)
	public void variantBulkUploadUpdateMissingNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "AF_01_all_fields.json");
		checkSuccessfulBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "UM_01_update_no_non_required_fields_level_1.json");
		
		RestAssured.given().
			when().
			get(variantGetEndpoint + "VARIANTTEST:Variant0001").
			then().
			statusCode(200).
			body("entity.modEntityId", is("VARIANTTEST:Variant0001")).
			body("entity", not(hasKey("createdBy"))).
			body("entity", not(hasKey("updatedBy"))).
			body("entity", not(hasKey("dateCreated"))).
			body("entity", not(hasKey("dateUpdated"))).
			body("entity", not(hasKey("variantStatus"))).
			body("entity", not(hasKey("sourceGeneralConsequence"))).
			body("entity", not(hasKey("relatedNotes")));
	}
	
	@Test
	@Order(7)
	public void variantBulkUploadUpdateMissingNonRequiredFieldsLevel2() throws Exception {
		checkSuccessfulBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "AF_01_all_fields.json");
		checkSuccessfulBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "UM_02_update_no_non_required_fields_level_2.json");
		
		RestAssured.given().
			when().
			get(variantGetEndpoint + "VARIANTTEST:Variant0001").
			then().
			statusCode(200).
			body("entity.modEntityId", is("VARIANTTEST:Variant0001")).
			body("entity.relatedNotes[0]", not(hasKey("evidence"))).
			body("entity.relatedNotes[0]", not(hasKey("createdBy"))).
			body("entity.relatedNotes[0]", not(hasKey("updatedBy"))).
			body("entity.relatedNotes[0]", not(hasKey("dateCreated"))).
			body("entity.relatedNotes[0]", not(hasKey("dateUpdated")));
	}
	
	@Test
	@Order(8)
	public void variantBulkUploadUpdateEmptyNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "AF_01_all_fields.json");
		checkSuccessfulBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "UE_01_update_empty_non_required_fields.json");
		
		RestAssured.given().
			when().
			get(variantGetEndpoint + "VARIANTTEST:Variant0001").
			then().
			statusCode(200).
			body("entity.modEntityId", is("VARIANTTEST:Variant0001")).
			body("entity", not(hasKey("createdBy"))).
			body("entity", not(hasKey("updatedBy"))).
			body("entity", not(hasKey("dateCreated"))).
			body("entity", not(hasKey("dateUpdated"))).
			body("entity", not(hasKey("variantStatus"))).
			body("entity", not(hasKey("sourceGeneralConsequence"))).
			body("entity.relatedNotes[0]", not(hasKey("evidence"))).
			body("entity.relatedNotes[0]", not(hasKey("createdBy"))).
			body("entity.relatedNotes[0]", not(hasKey("updatedBy"))).
			body("entity.relatedNotes[0]", not(hasKey("dateCreated"))).
			body("entity.relatedNotes[0]", not(hasKey("dateUpdated")));
	}
	
	@Test
	@Order(9)
	public void variantBulkUploadMissingNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "MN_01_no_non_required_fields_level_1.json");
		checkSuccessfulBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "MN_02_no_non_required_fields_level_2.json");
	}

	@Test
	@Order(10)
	public void variantBulkUploadEmptyNonRequiredFieldsLevel() throws Exception {
		checkSuccessfulBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "EN_01_empty_non_required_fields.json");
	}
	
	@Test
	@Order(12)
	public void variantBulkUploadDuplicateNotes() throws Exception {
		checkSuccessfulBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "DN_01_duplicate_notes.json");
		
		RestAssured.given().
			when().
			get(variantGetEndpoint + "VARIANTTEST:DN01").
			then().
			statusCode(200).
			body("entity.modEntityId", is("VARIANTTEST:DN01")).
			body("entity.relatedNotes", hasSize(1));
	}
	
}
