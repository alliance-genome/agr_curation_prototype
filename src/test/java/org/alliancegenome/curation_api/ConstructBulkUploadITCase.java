package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.time.OffsetDateTime;
import java.util.List;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
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
@DisplayName("105 - Construct bulk upload")
@Order(105)
public class ConstructBulkUploadITCase extends BaseITCase {
	
	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 60000)
					.setParam("http.connection.timeout", 60000));
	}
	
	private String reference = "AGRKB:000000001";
	private String referenceXref = "PMID:25920550";
	private String reference2 = "AGRKB:000000021";
	private String referenceXref2 = "PMID:25920551";
	private String dataProvider = "WB";
	private String dataProvider2 = "RGD";
	
	private VocabularyTerm noteType;
	private VocabularyTerm noteType2;
	private VocabularyTerm relation;
	private VocabularyTerm relation2;
	
	private final String constructBulkPostEndpoint = "/api/construct/bulk/WB/constructs";
	private final String constructBulkPostEndpointRGD = "/api/construct/bulk/RGD/constructs";
	private final String constructGetEndpoint = "/api/construct/";
	private final String constructTestFilePath = "src/test/resources/bulk/05_construct/";
	
	private void loadRequiredEntities() throws Exception {
		Vocabulary noteTypeVocabulary = getVocabulary(VocabularyConstants.NOTE_TYPE_VOCABULARY);
		noteType = addVocabularyTermToSet(VocabularyConstants.CONSTRUCT_COMPONENT_NOTE_TYPES_VOCABULARY_TERM_SET, "test_construct_component_note", noteTypeVocabulary, false);
		noteType2 = addVocabularyTermToSet(VocabularyConstants.CONSTRUCT_COMPONENT_NOTE_TYPES_VOCABULARY_TERM_SET, "test_construct_component_summary", noteTypeVocabulary, false);
		Vocabulary relationVocabulary = createVocabulary(VocabularyConstants.CONSTRUCT_RELATION_VOCABULARY, false);
		relation = createVocabularyTerm(relationVocabulary, "is_regulated_by", false);
		relation2 = createVocabularyTerm(relationVocabulary, "targets", false);
		createVocabularyTermSet(VocabularyConstants.CONSTRUCT_GENOMIC_ENTITY_RELATION_VOCABULARY_TERM_SET, relationVocabulary, List.of(relation, relation2));
	}

	@Test
	@Order(1)
	public void constructBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();
		
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "AF_01_all_fields.json");
		
		RestAssured.given().
			when().
			get(constructGetEndpoint + "WB:Construct0001").
			then().
			statusCode(200).
			body("entity.primaryExternalId", is("WB:Construct0001")).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.createdBy.uniqueId", is("CONSTRUCTTEST:Person0001")).
			body("entity.updatedBy.uniqueId", is("CONSTRUCTTEST:Person0002")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.references[0].curie", is(reference)).
			body("entity.references[0].crossReferences[0].referencedCurie", is(referenceXref)).
			body("entity.constructSymbol.displayText", is("Ta1")).
			body("entity.constructSymbol.formatText", is("Ta<sup>1</sup>")).
			body("entity.constructSymbol.synonymScope.name", is("exact")).
			body("entity.constructSymbol.synonymUrl", is("https://alliancegenome.org/test")).
			body("entity.constructSymbol.nameType.name", is("nomenclature_symbol")).
			body("entity.constructSymbol.evidence[0].curie", is(reference)).
			body("entity.constructSymbol.internal", is(true)).
			body("entity.constructSymbol.obsolete", is(true)).
			body("entity.constructSymbol.createdBy.uniqueId", is("CONSTRUCTTEST:Person0001")).
			body("entity.constructSymbol.updatedBy.uniqueId", is("CONSTRUCTTEST:Person0002")).
			body("entity.constructSymbol.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.constructSymbol.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.constructFullName.displayText", is("Test construct 1")).
			body("entity.constructFullName.formatText", is("Test construct<sup>1</sup>")).
			body("entity.constructFullName.synonymScope.name", is("exact")).
			body("entity.constructFullName.synonymUrl", is("https://alliancegenome.org/test")).
			body("entity.constructFullName.nameType.name", is("full_name")).
			body("entity.constructFullName.evidence[0].curie", is(reference)).
			body("entity.constructFullName.internal", is(true)).
			body("entity.constructFullName.obsolete", is(true)).
			body("entity.constructFullName.createdBy.uniqueId", is("CONSTRUCTTEST:Person0001")).
			body("entity.constructFullName.updatedBy.uniqueId", is("CONSTRUCTTEST:Person0002")).
			body("entity.constructFullName.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.constructFullName.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.constructSynonyms", hasSize(1)).
			body("entity.constructSynonyms[0].displayText", is("Test construct synonym 1")).
			body("entity.constructSynonyms[0].formatText", is("Test construct synonym <sup>1</sup>")).
			body("entity.constructSynonyms[0].synonymScope.name", is("exact")).
			body("entity.constructSynonyms[0].synonymUrl", is("https://alliancegenome.org/test")).
			body("entity.constructSynonyms[0].nameType.name", is("unspecified")).
			body("entity.constructSynonyms[0].evidence[0].curie", is(reference)).
			body("entity.constructSynonyms[0].internal", is(true)).
			body("entity.constructSynonyms[0].obsolete", is(true)).
			body("entity.constructSynonyms[0].createdBy.uniqueId", is("CONSTRUCTTEST:Person0001")).
			body("entity.constructSynonyms[0].updatedBy.uniqueId", is("CONSTRUCTTEST:Person0002")).
			body("entity.constructSynonyms[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.constructSynonyms[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.constructComponents", hasSize(1)).
			body("entity.constructComponents[0].componentSymbol", is("cmp1")).
			body("entity.constructComponents[0].relation.name", is(relation.getName())).
			body("entity.constructComponents[0].taxon.curie", is("NCBITaxon:9606")).
			body("entity.constructComponents[0].taxonText", is("Homo sapiens")).
			body("entity.constructComponents[0].evidence[0].curie", is(reference)).
			body("entity.constructComponents[0].evidence[0].crossReferences[0].referencedCurie", is(referenceXref)).
			body("entity.constructComponents[0].internal", is(true)).
			body("entity.constructComponents[0].obsolete", is(true)).
			body("entity.constructComponents[0].createdBy.uniqueId", is("CONSTRUCTTEST:Person0001")).
			body("entity.constructComponents[0].updatedBy.uniqueId", is("CONSTRUCTTEST:Person0002")).
			body("entity.constructComponents[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.constructComponents[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.constructComponents[0].relatedNotes[0].noteType.name", is(noteType.getName())).
			body("entity.constructComponents[0].relatedNotes[0].freeText", is("Test construct component note")).
			body("entity.constructComponents[0].relatedNotes[0].internal", is(true)).
			body("entity.constructComponents[0].relatedNotes[0].obsolete", is(true)).
			body("entity.constructComponents[0].relatedNotes[0].createdBy.uniqueId", is("CONSTRUCTTEST:Person0001")).
			body("entity.constructComponents[0].relatedNotes[0].updatedBy.uniqueId", is("CONSTRUCTTEST:Person0002")).
			body("entity.constructComponents[0].relatedNotes[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.constructComponents[0].relatedNotes[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.dataProvider.sourceOrganization.abbreviation", is(dataProvider)).
			body("entity.dataProvider.crossReference.referencedCurie", is("TEST:0001")).
			body("entity.dataProvider.crossReference.displayName", is("TEST:0001")).
			body("entity.dataProvider.crossReference.resourceDescriptorPage.name", is("homepage"));
	}
	
	@Test
	@Order(2)
	public void constructBulkUploadUpdateFields() throws Exception {
		checkSuccessfulBulkLoad(constructBulkPostEndpointRGD, constructTestFilePath + "UD_01_update_all_except_default_fields.json");
		
		RestAssured.given().
			when().
			get(constructGetEndpoint + "WB:Construct0001").
			then().
			statusCode(200).
			body("entity.primaryExternalId", is("WB:Construct0001")).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.createdBy.uniqueId", is("CONSTRUCTTEST:Person0002")).
			body("entity.updatedBy.uniqueId", is("CONSTRUCTTEST:Person0001")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString())).
			body("entity.references", hasSize(1)).
			body("entity.references[0].curie", is(reference2)).
			body("entity.references[0].crossReferences[0].referencedCurie", is(referenceXref2)).
			body("entity.constructSymbol.displayText", is("Ta1a")).
			body("entity.constructSymbol.formatText", is("Ta<sup>1a</sup>")).
			body("entity.constructSymbol.synonymScope.name", is("broad")).
			body("entity.constructSymbol.synonymUrl", is("https://alliancegenome.org/test2")).
			body("entity.constructSymbol.nameType.name", is("systematic_name")).
			body("entity.constructSymbol.evidence[0].curie", is(reference2)).
			body("entity.constructSymbol.internal", is(false)).
			body("entity.constructSymbol.obsolete", is(false)).
			body("entity.constructSymbol.createdBy.uniqueId", is("CONSTRUCTTEST:Person0002")).
			body("entity.constructSymbol.updatedBy.uniqueId", is("CONSTRUCTTEST:Person0001")).
			body("entity.constructSymbol.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
			body("entity.constructSymbol.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString())).
			body("entity.constructFullName.displayText", is("Test construct 1a")).
			body("entity.constructFullName.formatText", is("Test construct<sup>1a</sup>")).
			body("entity.constructFullName.synonymScope.name", is("broad")).
			body("entity.constructFullName.synonymUrl", is("https://alliancegenome.org/test2")).
			body("entity.constructFullName.nameType.name", is("full_name")).
			body("entity.constructFullName.evidence[0].curie", is(reference2)).
			body("entity.constructFullName.internal", is(false)).
			body("entity.constructFullName.obsolete", is(false)).
			body("entity.constructFullName.createdBy.uniqueId", is("CONSTRUCTTEST:Person0002")).
			body("entity.constructFullName.updatedBy.uniqueId", is("CONSTRUCTTEST:Person0001")).
			body("entity.constructFullName.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
			body("entity.constructFullName.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString())).
			body("entity.constructSynonyms", hasSize(1)).
			body("entity.constructSynonyms[0].displayText", is("Test construct synonym 1a")).
			body("entity.constructSynonyms[0].formatText", is("Test construct synonym <sup>1a</sup>")).
			body("entity.constructSynonyms[0].synonymScope.name", is("broad")).
			body("entity.constructSynonyms[0].synonymUrl", is("https://alliancegenome.org/test2")).
			body("entity.constructSynonyms[0].nameType.name", is("nomenclature_symbol")).
			body("entity.constructSynonyms[0].evidence[0].curie", is(reference2)).
			body("entity.constructSynonyms[0].internal", is(false)).
			body("entity.constructSynonyms[0].obsolete", is(false)).
			body("entity.constructSynonyms[0].createdBy.uniqueId", is("CONSTRUCTTEST:Person0002")).
			body("entity.constructSynonyms[0].updatedBy.uniqueId", is("CONSTRUCTTEST:Person0001")).
			body("entity.constructSynonyms[0].dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
			body("entity.constructSynonyms[0].dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString())).
			body("entity.constructComponents", hasSize(1)).
			body("entity.constructComponents[0].componentSymbol", is("cmp2")).
			body("entity.constructComponents[0].relation.name", is(relation2.getName())).
			body("entity.constructComponents[0].taxon.curie", is("NCBITaxon:6239")).
			body("entity.constructComponents[0].taxonText", is("Caenorhabditis elegans")).
			body("entity.constructComponents[0].evidence[0].curie", is(reference2)).
			body("entity.constructComponents[0].evidence[0].crossReferences[0].referencedCurie", is(referenceXref2)).
			body("entity.constructComponents[0].internal", is(false)).
			body("entity.constructComponents[0].obsolete", is(false)).
			body("entity.constructComponents[0].createdBy.uniqueId", is("CONSTRUCTTEST:Person0002")).
			body("entity.constructComponents[0].updatedBy.uniqueId", is("CONSTRUCTTEST:Person0001")).
			body("entity.constructComponents[0].dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
			body("entity.constructComponents[0].dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString())).
			body("entity.constructComponents[0].relatedNotes", hasSize(1)).
			body("entity.constructComponents[0].relatedNotes[0].noteType.name", is(noteType2.getName())).
			body("entity.constructComponents[0].relatedNotes[0].freeText", is("Test construct component note 2")).
			body("entity.constructComponents[0].relatedNotes[0].internal", is(true)).
			body("entity.constructComponents[0].relatedNotes[0].obsolete", is(false)).
			body("entity.constructComponents[0].relatedNotes[0].createdBy.uniqueId", is("CONSTRUCTTEST:Person0002")).
			body("entity.constructComponents[0].relatedNotes[0].updatedBy.uniqueId", is("CONSTRUCTTEST:Person0001")).
			body("entity.constructComponents[0].relatedNotes[0].dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
			body("entity.constructComponents[0].relatedNotes[0].dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString())).
			body("entity.dataProvider.sourceOrganization.abbreviation", is(dataProvider2)).
			body("entity.dataProvider.crossReference.referencedCurie", is("TEST2:0001")).
			body("entity.dataProvider.crossReference.displayName", is("TEST2:0001")).
			body("entity.dataProvider.crossReference.resourceDescriptorPage.name", is("homepage2"));
	}
	
	@Test
	@Order(3)
	public void constructBulkUploadMissingRequiredFields() throws Exception {
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MR_01_no_construct_component_component_symbol.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MR_02_no_construct_component_note_note_type.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MR_03_no_construct_component_note_free_text.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MR_04_no_data_provider_source_organization_abbreviation.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MR_05_no_data_provider_cross_reference_referenced_curie.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MR_06_no_data_provider_cross_reference_display_name.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MR_07_no_data_provider_cross_reference_prefix.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MR_08_no_data_provider_cross_reference_page_area.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MR_09_no_construct_symbol_display_text.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MR_10_no_construct_full_name_display_text.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MR_11_no_construct_synonym_display_text.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MR_12_no_construct_symbol_format_text.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MR_13_no_construct_full_name_format_text.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MR_14_no_construct_synonym_format_text.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MR_15_no_construct_symbol_name_type.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MR_16_no_construct_full_name_name_type.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MR_17_no_construct_synonym_name_type.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MR_18_no_construct_component_relation.json");
	}
	
	
	@Test
	@Order(4)
	public void constructBulkUploadEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "ER_01_empty_construct_component_component_symbol.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "ER_02_empty_construct_component_note_note_type.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "ER_03_empty_construct_component_note_free_text.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "ER_04_empty_data_provider_source_organization_abbreviation.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "ER_05_empty_data_provider_cross_reference_referenced_curie.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "ER_06_empty_data_provider_cross_reference_display_name.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "ER_07_empty_data_provider_cross_reference_prefix.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "ER_08_empty_data_provider_cross_reference_page_area.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "ER_09_empty_construct_symbol_display_text.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "ER_10_empty_construct_full_name_display_text.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "ER_11_empty_construct_synonym_display_text.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "ER_12_empty_construct_symbol_format_text.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "ER_13_empty_construct_full_name_format_text.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "ER_14_empty_construct_synonym_format_text.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "ER_15_empty_construct_symbol_name_type.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "ER_16_empty_construct_full_name_name_type.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "ER_17_empty_construct_synonym_name_type.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "ER_18_empty_construct_component_relation.json");
	}
	
	@Test
	@Order(5)
	public void constructBulkUploadInvalidFields() throws Exception {
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_01_invalid_date_created.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_02_invalid_date_updated.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_03_invalid_reference.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_04_invalid_construct_component_evidence.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_05_invalid_construct_component_taxon.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_06_invalid_construct_component_note_note_type.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_07_invalid_data_provider_source_organization_abbreviation.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_08_invalid_data_provider_cross_reference_prefix.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_09_invalid_data_provider_cross_reference_page_area.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_10_invalid_construct_symbol_name_type.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_11_invalid_construct_full_name_name_type.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_12_invalid_construct_synonym_name_type.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_13_invalid_construct_symbol_synonym_scope.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_14_invalid_construct_full_name_synonym_scope.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_15_invalid_construct_synonym_synonym_scope.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_16_invalid_construct_symbol_evidence.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_17_invalid_construct_full_name_evidence.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_18_invalid_construct_synonym_evidence.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_19_invalid_construct_component_relation.json");
	}
	
	@Test
	@Order(6)
	public void constructBulkUploadUpdateMissingNonRequiredFieldsLevel1() throws Exception {
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "AF_01_all_fields.json");
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "UM_01_update_no_non_required_fields_level_1.json");
		
		RestAssured.given().
			when().
			get(constructGetEndpoint + "WB:Construct0001").
			then().
			statusCode(200).
			body("entity", not(hasKey("createdBy"))).
			body("entity", not(hasKey("updatedBy"))).
			body("entity", not(hasKey("dateCreated"))).
			body("entity", not(hasKey("dateUpdated"))).
			body("entity", not(hasKey("constructFullName"))).
			body("entity", not(hasKey("constructSynonyms"))).
			body("entity", not(hasKey("constructComponents"))).
			body("entity", not(hasKey("secondaryIdentifiers")));
	}

	@Test
	@Order(7)
	public void constructBulkUploadUpdateMissingNonRequiredFieldsLevel2() throws Exception {
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "AF_01_all_fields.json");
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "UM_02_update_no_non_required_fields_level_2.json");
		
		RestAssured.given().
			when().
			get(constructGetEndpoint + "WB:Construct0001").
			then().
			statusCode(200).
			body("entity", hasKey("primaryExternalId")).
			body("entity.constructSymbol", not(hasKey("synonymScope"))).
			body("entity.constructSymbol", not(hasKey("synonymUrl"))).
			body("entity.constructSymbol", not(hasKey("evidence"))).
			body("entity.constructSymbol", not(hasKey("createdBy"))).
			body("entity.constructSymbol", not(hasKey("updatedBy"))).
			body("entity.constructSymbol", not(hasKey("dateCreated"))).
			body("entity.constructSymbol", not(hasKey("dateUpdated"))).
			body("entity.constructFullName", not(hasKey("synonymScope"))).
			body("entity.constructFullName", not(hasKey("synonymUrl"))).
			body("entity.constructFullName", not(hasKey("evidence"))).
			body("entity.constructFullName", not(hasKey("createdBy"))).
			body("entity.constructFullName", not(hasKey("updatedBy"))).
			body("entity.constructFullName", not(hasKey("dateCreated"))).
			body("entity.constructFullName", not(hasKey("dateUpdated"))).
			body("entity.constructSynonyms[0]", not(hasKey("synonymScope"))).
			body("entity.constructSynonyms[0]", not(hasKey("synonymUrl"))).
			body("entity.constructSynonyms[0]", not(hasKey("evidence"))).
			body("entity.constructSynonyms[0]", not(hasKey("createdBy"))).
			body("entity.constructSynonyms[0]", not(hasKey("updatedBy"))).
			body("entity.constructSynonyms[0]", not(hasKey("dateCreated"))).
			body("entity.constructSynonyms[0]", not(hasKey("dateUpdated"))).
			body("entity.constructComponents[0]", not(hasKey("taxon"))).
			body("entity.constructComponents[0]", not(hasKey("taxon_text"))).
			body("entity.constructComponents[0]", not(hasKey("evidence"))).
			body("entity.constructComponents[0]", not(hasKey("createdBy"))).
			body("entity.constructComponents[0]", not(hasKey("updatedBy"))).
			body("entity.constructComponents[0]", not(hasKey("dateCreated"))).
			body("entity.constructComponents[0]", not(hasKey("dateUpdated"))).
			body("entity.constructComponents[0]", not(hasKey("relatedNotes"))).
			body("entity.dataProvider", not(hasKey("crossReferences")));
	}
	
	@Test
	@Order(8)
	public void constructBulkUploadUpdateMissingNonRequiredFieldsLevel3() throws Exception {
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "AF_01_all_fields.json");
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "UM_03_update_no_non_required_fields_level_3.json");
		
		RestAssured.given().
			when().
			get(constructGetEndpoint + "WB:Construct0001").
			then().
			statusCode(200).
			body("entity", hasKey("primaryExternalId")).
			body("entity.constructComponents[0].relatedNotes[0]", not(hasKey("evidence"))).
			body("entity.constructComponents[0].relatedNotes[0]", not(hasKey("createdBy"))).
			body("entity.constructComponents[0].relatedNotes[0]", not(hasKey("updatedBy"))).
			body("entity.constructComponents[0].relatedNotes[0]", not(hasKey("dateCreated"))).
			body("entity.constructComponents[0].relatedNotes[0]", not(hasKey("dateUpdated")));
	}

	@Test
	@Order(9)
	public void constructBulkUploadUpdateEmptyNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "AF_01_all_fields.json");
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "UE_01_update_empty_non_required_fields.json");
		
		RestAssured.given().
			when().
			get(constructGetEndpoint + "WB:Construct0001").
			then().
			statusCode(200).
			body("entity", hasKey("primaryExternalId")).
			body("entity", not(hasKey("createdBy"))).
			body("entity", not(hasKey("updatedBy"))).
			body("entity", not(hasKey("dateCreated"))).
			body("entity", not(hasKey("dateUpdated"))).
			body("entity", not(hasKey("taxon"))).
			body("entity", not(hasKey("references"))).
			body("entity.constructSymbol", not(hasKey("synonymScope"))).
			body("entity.constructSymbol", not(hasKey("synonymUrl"))).
			body("entity.constructSymbol", not(hasKey("evidence"))).
			body("entity.constructSymbol", not(hasKey("createdBy"))).
			body("entity.constructSymbol", not(hasKey("updatedBy"))).
			body("entity.constructSymbol", not(hasKey("dateCreated"))).
			body("entity.constructSymbol", not(hasKey("dateUpdated"))).
			body("entity.constructFullName", not(hasKey("synonymScope"))).
			body("entity.constructFullName", not(hasKey("synonymUrl"))).
			body("entity.constructFullName", not(hasKey("evidence"))).
			body("entity.constructFullName", not(hasKey("createdBy"))).
			body("entity.constructFullName", not(hasKey("updatedBy"))).
			body("entity.constructFullName", not(hasKey("dateCreated"))).
			body("entity.constructFullName", not(hasKey("dateUpdated"))).
			body("entity.constructSynonyms[0]", not(hasKey("synonymScope"))).
			body("entity.constructSynonyms[0]", not(hasKey("synonymUrl"))).
			body("entity.constructSynonyms[0]", not(hasKey("evidence"))).
			body("entity.constructSynonyms[0]", not(hasKey("createdBy"))).
			body("entity.constructSynonyms[0]", not(hasKey("updatedBy"))).
			body("entity.constructSynonyms[0]", not(hasKey("dateCreated"))).
			body("entity.constructSynonyms[0]", not(hasKey("dateUpdated"))).body("entity.constructComponents[0]", not(hasKey("taxon"))).
			body("entity.constructComponents[0]", not(hasKey("taxon_text"))).
			body("entity.constructComponents[0]", not(hasKey("evidence"))).
			body("entity.constructComponents[0]", not(hasKey("createdBy"))).
			body("entity.constructComponents[0]", not(hasKey("updatedBy"))).
			body("entity.constructComponents[0]", not(hasKey("dateCreated"))).
			body("entity.constructComponents[0]", not(hasKey("dateUpdated"))).
			body("entity.constructComponents[0].relatedNotes[0]", not(hasKey("evidence"))).
			body("entity.constructComponents[0].relatedNotes[0]", not(hasKey("createdBy"))).
			body("entity.constructComponents[0].relatedNotes[0]", not(hasKey("updatedBy"))).
			body("entity.constructComponents[0].relatedNotes[0]", not(hasKey("dateCreated"))).
			body("entity.constructComponents[0].relatedNotes[0]", not(hasKey("dateUpdated")));
	}
	
	@Test
	@Order(10)
	public void constructBulkUploadMissingNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MN_01_no_non_required_fields_level_1.json");
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MN_02_no_non_required_fields_level_2.json");
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MN_03_no_non_required_fields_level_3.json");
	}

	@Test
	@Order(11)
	public void constructBulkUploadEmptyNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "EN_01_empty_non_required_fields.json");
	}
	
	@Test
	@Order(12)
	public void constructBulkUploadDuplicateNotes() throws Exception {
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "DN_01_duplicate_notes.json");
		
		RestAssured.given().
			when().
			get(constructGetEndpoint + "WB:DN01").
			then().
			statusCode(200).
			body("entity.primaryExternalId", is("WB:DN01")).
			body("entity.constructComponents", hasSize(1)).
			body("entity.constructComponents[0].relatedNotes", hasSize(1));
	}
	
	@Test
	@Order(13)
	public void reloadInitialConstruct() throws Exception {
		// Required for later association tests - original gets overwritten
		// by missing fields tests due to uniqueId matching
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "AF_01_all_fields.json");
		
		RestAssured.given().
			when().
			get(constructGetEndpoint + "WB:Construct0001").
			then().
			statusCode(200).
			body("entity.primaryExternalId", is("WB:Construct0001"));
	}
		
}
