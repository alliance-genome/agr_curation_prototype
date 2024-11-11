package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.time.OffsetDateTime;

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
@DisplayName("102 - Allele bulk upload")
@Order(102)
public class AlleleBulkUploadITCase extends BaseITCase {
	
	private String requiredReference = "AGRKB:000000001";
	private String requiredReference2 = "AGRKB:000000021";
	private String requiredSoTerm = "SO:00001";
	private String requiredSoTerm2 = "SO:00002";
	private String requiredMpTerm = "MP:00001";
	private String requiredMpTerm2 = "MP:00002";
	private String requiredDataProvider = "WB";
	private String requiredDataProviderRGD = "RGD";
	
	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 60000)
					.setParam("http.connection.timeout", 60000));
	}

	private final String alleleBulkPostEndpoint = "/api/allele/bulk/WB/alleles";
	private final String alleleBulkPostEndpointRGD = "/api/allele/bulk/RGD/alleles";
	private final String alleleBulkPostEndpointHUMAN = "/api/allele/bulk/HUMAN/alleles";
	private final String alleleGetEndpoint = "/api/allele/";
	private final String alleleTestFilePath = "src/test/resources/bulk/02_allele/";

	private void loadRequiredEntities() throws Exception {
		createSoTerm(requiredSoTerm, "Test SOTerm", false);
		createSoTerm(requiredSoTerm2, "Test SOTerm2", false);
		createMpTerm(requiredMpTerm, "Test MPTerm");
		createMpTerm(requiredMpTerm2, "Test MPTerm2");
	}
	
	@Test
	@Order(1)
	public void alleleBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();
		
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "AF_01_all_fields.json");
	
		RestAssured.given().
			when().
			get(alleleGetEndpoint + "ALLELETEST:Allele0001").
			then().
			statusCode(200).
			body("entity.modEntityId", is("ALLELETEST:Allele0001")).
			body("entity.taxon.curie", is("NCBITaxon:6239")).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.inCollection.name", is("Million_mutations_project")).
			body("entity.isExtinct", is(false)).
			body("entity.references", hasSize(1)).
			body("entity.references[0].curie", is(requiredReference)).
			body("entity.relatedNotes", hasSize(1)).
			body("entity.relatedNotes[0].internal", is(false)).
			body("entity.relatedNotes[0].obsolete", is(true)).
			body("entity.relatedNotes[0].updatedBy.uniqueId", is("DATEST:Person0002")).
			body("entity.relatedNotes[0].createdBy.uniqueId", is("DATEST:Person0001")).
			body("entity.relatedNotes[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.relatedNotes[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.relatedNotes[0].freeText", is("Test note")).
			body("entity.relatedNotes[0].noteType.name", is("comment")).
			body("entity.relatedNotes[0].references[0].curie", is(requiredReference)).
			body("entity.alleleSymbol.displayText", is("Ta1")).
			body("entity.alleleSymbol.formatText", is("Ta<sup>1</sup>")).
			body("entity.alleleSymbol.synonymScope.name", is("exact")).
			body("entity.alleleSymbol.synonymUrl", is("https://alliancegenome.org/test")).
			body("entity.alleleSymbol.nameType.name", is("nomenclature_symbol")).
			body("entity.alleleSymbol.evidence[0].curie", is(requiredReference)).
			body("entity.alleleSymbol.internal", is(true)).
			body("entity.alleleSymbol.obsolete", is(true)).
			body("entity.alleleSymbol.createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleSymbol.updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleSymbol.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.alleleSymbol.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.alleleFullName.displayText", is("Test allele 1")).
			body("entity.alleleFullName.formatText", is("Test allele<sup>1</sup>")).
			body("entity.alleleFullName.synonymScope.name", is("exact")).
			body("entity.alleleFullName.synonymUrl", is("https://alliancegenome.org/test")).
			body("entity.alleleFullName.nameType.name", is("full_name")).
			body("entity.alleleFullName.evidence[0].curie", is(requiredReference)).
			body("entity.alleleFullName.internal", is(true)).
			body("entity.alleleFullName.obsolete", is(true)).
			body("entity.alleleFullName.createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleFullName.updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleFullName.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.alleleFullName.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.alleleSynonyms", hasSize(1)).
			body("entity.alleleSynonyms[0].displayText", is("Test allele synonym 1")).
			body("entity.alleleSynonyms[0].formatText", is("Test allele synonym <sup>1</sup>")).
			body("entity.alleleSynonyms[0].synonymScope.name", is("exact")).
			body("entity.alleleSynonyms[0].synonymUrl", is("https://alliancegenome.org/test")).
			body("entity.alleleSynonyms[0].nameType.name", is("unspecified")).
			body("entity.alleleSynonyms[0].evidence[0].curie", is(requiredReference)).
			body("entity.alleleSynonyms[0].internal", is(true)).
			body("entity.alleleSynonyms[0].obsolete", is(true)).
			body("entity.alleleSynonyms[0].createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleSynonyms[0].updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleSynonyms[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.alleleSynonyms[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.alleleMutationTypes", hasSize(1)).
			body("entity.alleleMutationTypes[0].evidence[0].curie", is(requiredReference)).
			body("entity.alleleMutationTypes[0].mutationTypes[0].curie", is(requiredSoTerm)).
			body("entity.alleleMutationTypes[0].internal", is(true)).
			body("entity.alleleMutationTypes[0].obsolete", is(true)).
			body("entity.alleleMutationTypes[0].createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleMutationTypes[0].updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleMutationTypes[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.alleleMutationTypes[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.alleleInheritanceModes", hasSize(1)).
			body("entity.alleleInheritanceModes[0].evidence[0].curie", is(requiredReference)).
			body("entity.alleleInheritanceModes[0].inheritanceMode.name", is("dominant")).
			body("entity.alleleInheritanceModes[0].phenotypeTerm.curie", is(requiredMpTerm)).
			body("entity.alleleInheritanceModes[0].phenotypeStatement", is("Phenotype statement")).
			body("entity.alleleInheritanceModes[0].internal", is(true)).
			body("entity.alleleInheritanceModes[0].obsolete", is(true)).
			body("entity.alleleInheritanceModes[0].createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleInheritanceModes[0].updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleInheritanceModes[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.alleleInheritanceModes[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.alleleSecondaryIds", hasSize(1)).
			body("entity.alleleSecondaryIds[0].evidence[0].curie", is(requiredReference)).
			body("entity.alleleSecondaryIds[0].secondaryId", is("TEST:Secondary")).
			body("entity.alleleSecondaryIds[0].internal", is(true)).
			body("entity.alleleSecondaryIds[0].obsolete", is(true)).
			body("entity.alleleSecondaryIds[0].createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleSecondaryIds[0].updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleSecondaryIds[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.alleleSecondaryIds[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.alleleFunctionalImpacts", hasSize(1)).
			body("entity.alleleFunctionalImpacts[0].evidence[0].curie", is(requiredReference)).
			body("entity.alleleFunctionalImpacts[0].functionalImpacts[0].name", is("cold_sensitive_hypermorphic")).
			body("entity.alleleFunctionalImpacts[0].phenotypeTerm.curie", is(requiredMpTerm)).
			body("entity.alleleFunctionalImpacts[0].phenotypeStatement", is("Phenotype statement")).
			body("entity.alleleFunctionalImpacts[0].internal", is(true)).
			body("entity.alleleFunctionalImpacts[0].obsolete", is(true)).
			body("entity.alleleFunctionalImpacts[0].createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleFunctionalImpacts[0].updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleFunctionalImpacts[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.alleleFunctionalImpacts[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.alleleGermlineTransmissionStatus.germlineTransmissionStatus.name", is("cell_line")).
			body("entity.alleleGermlineTransmissionStatus.internal", is(true)).
			body("entity.alleleGermlineTransmissionStatus.obsolete", is(true)).
			body("entity.alleleGermlineTransmissionStatus.createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleGermlineTransmissionStatus.updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleGermlineTransmissionStatus.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.alleleGermlineTransmissionStatus.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.alleleDatabaseStatus.databaseStatus.name", is("reserved")).
			body("entity.alleleDatabaseStatus.internal", is(true)).
			body("entity.alleleDatabaseStatus.obsolete", is(true)).
			body("entity.alleleDatabaseStatus.createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleDatabaseStatus.updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleDatabaseStatus.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.alleleDatabaseStatus.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.alleleNomenclatureEvents[0].internal", is(true)).
			body("entity.alleleNomenclatureEvents[0].nomenclatureEvent.name", is("data_merged")).
			body("entity.alleleNomenclatureEvents[0].obsolete", is(true)).
			body("entity.alleleNomenclatureEvents[0].createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleNomenclatureEvents[0].updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleNomenclatureEvents[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.alleleNomenclatureEvents[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.dataProvider.sourceOrganization.abbreviation", is(requiredDataProvider)).
			body("entity.dataProvider.crossReference.referencedCurie", is("TEST:0001")).
			body("entity.dataProvider.crossReference.displayName", is("TEST:0001")).
			body("entity.dataProvider.crossReference.resourceDescriptorPage.name", is("homepage"));
	}
	
	@Test
	@Order(2)
	public void alleleBulkUploadUpdateCheckFields() throws Exception {
		checkSuccessfulBulkLoad(alleleBulkPostEndpointRGD, alleleTestFilePath + "UD_01_update_all_except_default_fields.json");
	
		RestAssured.given().
			when().
			get(alleleGetEndpoint + "ALLELETEST:Allele0001").
			then().
			statusCode(200).
			body("entity.modEntityId", is("ALLELETEST:Allele0001")).
			body("entity.taxon.curie", is("NCBITaxon:10116")).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.createdBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.updatedBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString())).
			body("entity.inCollection.name", is("WGS_Yanai")).
			body("entity.isExtinct", is(true)).
			body("entity.references", hasSize(1)).
			body("entity.references[0].curie", is(requiredReference2)).
			body("entity.relatedNotes", hasSize(1)).
			body("entity.relatedNotes[0].internal", is(true)).
			body("entity.relatedNotes[0].obsolete", is(false)).
			body("entity.relatedNotes[0].updatedBy.uniqueId", is("DATEST:Person0001")).
			body("entity.relatedNotes[0].createdBy.uniqueId", is("DATEST:Person0002")).
			body("entity.relatedNotes[0].dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString())).
			body("entity.relatedNotes[0].dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
			body("entity.relatedNotes[0].freeText", is("Test note")).
			body("entity.relatedNotes[0].noteType.name", is("indel_verification")).
			body("entity.relatedNotes[0].references[0].curie", is(requiredReference2)).
			body("entity.alleleSymbol.displayText", is("Ta1a")).
			body("entity.alleleSymbol.formatText", is("Ta<sup>1a</sup>")).
			body("entity.alleleSymbol.synonymScope.name", is("broad")).
			body("entity.alleleSymbol.synonymUrl", is("https://alliancegenome.org/test2")).
			body("entity.alleleSymbol.nameType.name", is("systematic_name")).
			body("entity.alleleSymbol.evidence[0].curie", is(requiredReference2)).
			body("entity.alleleSymbol.internal", is(false)).
			body("entity.alleleSymbol.obsolete", is(false)).
			body("entity.alleleSymbol.createdBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleSymbol.updatedBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleSymbol.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
			body("entity.alleleSymbol.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString())).
			body("entity.alleleFullName.displayText", is("Test allele 1a")).
			body("entity.alleleFullName.formatText", is("Test allele<sup>1a</sup>")).
			body("entity.alleleFullName.synonymScope.name", is("broad")).
			body("entity.alleleFullName.synonymUrl", is("https://alliancegenome.org/test2")).
			body("entity.alleleFullName.nameType.name", is("full_name")).
			body("entity.alleleFullName.evidence[0].curie", is(requiredReference2)).
			body("entity.alleleFullName.internal", is(false)).
			body("entity.alleleFullName.obsolete", is(false)).
			body("entity.alleleFullName.createdBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleFullName.updatedBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleFullName.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
			body("entity.alleleFullName.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString())).
			body("entity.alleleSynonyms", hasSize(1)).
			body("entity.alleleSynonyms[0].displayText", is("Test allele synonym 1a")).
			body("entity.alleleSynonyms[0].formatText", is("Test allele synonym <sup>1a</sup>")).
			body("entity.alleleSynonyms[0].synonymScope.name", is("broad")).
			body("entity.alleleSynonyms[0].synonymUrl", is("https://alliancegenome.org/test2")).
			body("entity.alleleSynonyms[0].nameType.name", is("nomenclature_symbol")).
			body("entity.alleleSynonyms[0].evidence[0].curie", is(requiredReference2)).
			body("entity.alleleSynonyms[0].internal", is(false)).
			body("entity.alleleSynonyms[0].obsolete", is(false)).
			body("entity.alleleSynonyms[0].createdBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleSynonyms[0].updatedBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleSynonyms[0].dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
			body("entity.alleleSynonyms[0].dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString())).
			body("entity.alleleMutationTypes", hasSize(1)).
			body("entity.alleleMutationTypes[0].evidence[0].curie", is(requiredReference2)).
			body("entity.alleleMutationTypes[0].mutationTypes[0].curie", is(requiredSoTerm2)).
			body("entity.alleleMutationTypes[0].internal", is(false)).
			body("entity.alleleMutationTypes[0].obsolete", is(false)).
			body("entity.alleleMutationTypes[0].createdBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleMutationTypes[0].updatedBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleMutationTypes[0].dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
			body("entity.alleleMutationTypes[0].dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString())).
			body("entity.alleleInheritanceModes", hasSize(1)).
			body("entity.alleleInheritanceModes[0].evidence[0].curie", is(requiredReference2)).
			body("entity.alleleInheritanceModes[0].inheritanceMode.name", is("recessive")).
			body("entity.alleleInheritanceModes[0].phenotypeTerm.curie", is(requiredMpTerm2)).
			body("entity.alleleInheritanceModes[0].phenotypeStatement", is("Phenotype statement 2")).
			body("entity.alleleInheritanceModes[0].internal", is(false)).
			body("entity.alleleInheritanceModes[0].obsolete", is(false)).
			body("entity.alleleInheritanceModes[0].createdBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleInheritanceModes[0].updatedBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleInheritanceModes[0].dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
			body("entity.alleleInheritanceModes[0].dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString())).
			body("entity.alleleSecondaryIds", hasSize(1)).
			body("entity.alleleSecondaryIds[0].evidence[0].curie", is(requiredReference2)).
			body("entity.alleleSecondaryIds[0].secondaryId", is("TEST:Secondary2")).
			body("entity.alleleSecondaryIds[0].internal", is(false)).
			body("entity.alleleSecondaryIds[0].obsolete", is(false)).
			body("entity.alleleSecondaryIds[0].createdBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleSecondaryIds[0].updatedBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleSecondaryIds[0].dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
			body("entity.alleleSecondaryIds[0].dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString())).
			body("entity.alleleFunctionalImpacts", hasSize(1)).
			body("entity.alleleFunctionalImpacts[0].evidence[0].curie", is(requiredReference2)).
			body("entity.alleleFunctionalImpacts[0].functionalImpacts[0].name", is("cold_sensitive_neomorphic")).
			body("entity.alleleFunctionalImpacts[0].phenotypeTerm.curie", is(requiredMpTerm2)).
			body("entity.alleleFunctionalImpacts[0].phenotypeStatement", is("Phenotype statement 2")).
			body("entity.alleleFunctionalImpacts[0].internal", is(false)).
			body("entity.alleleFunctionalImpacts[0].obsolete", is(false)).
			body("entity.alleleFunctionalImpacts[0].createdBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleFunctionalImpacts[0].updatedBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleFunctionalImpacts[0].dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
			body("entity.alleleFunctionalImpacts[0].dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString())).
			body("entity.alleleGermlineTransmissionStatus.germlineTransmissionStatus.name", is("germline")).
			body("entity.alleleGermlineTransmissionStatus.internal", is(false)).
			body("entity.alleleGermlineTransmissionStatus.obsolete", is(false)).
			body("entity.alleleGermlineTransmissionStatus.createdBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleGermlineTransmissionStatus.updatedBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleGermlineTransmissionStatus.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
			body("entity.alleleGermlineTransmissionStatus.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString())).
			body("entity.alleleDatabaseStatus.databaseStatus.name", is("approved")).
			body("entity.alleleDatabaseStatus.evidence[0].curie", is(requiredReference2)).
			body("entity.alleleDatabaseStatus.internal", is(false)).
			body("entity.alleleDatabaseStatus.obsolete", is(false)).
			body("entity.alleleDatabaseStatus.createdBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleDatabaseStatus.updatedBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleDatabaseStatus.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
			body("entity.alleleDatabaseStatus.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString())).
			body("entity.alleleNomenclatureEvents[0].nomenclatureEvent.name", is("symbol_updated")).
			body("entity.alleleNomenclatureEvents[0].evidence[0].curie", is(requiredReference2)).
			body("entity.alleleNomenclatureEvents[0].internal", is(false)).
			body("entity.alleleNomenclatureEvents[0].obsolete", is(false)).
			body("entity.alleleNomenclatureEvents[0].createdBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleNomenclatureEvents[0].updatedBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleNomenclatureEvents[0].dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
			body("entity.alleleNomenclatureEvents[0].dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString())).
			body("entity.dataProvider.sourceOrganization.abbreviation", is(requiredDataProviderRGD)).
			body("entity.dataProvider.crossReference.referencedCurie", is("TEST2:0001")).
			body("entity.dataProvider.crossReference.displayName", is("TEST2:0001")).
			body("entity.dataProvider.crossReference.resourceDescriptorPage.name", is("homepage2"));
	}
	
	@Test
	@Order(3)
	public void alleleBulkUploadMissingRequiredFields() throws Exception {
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_01_no_mod_ids.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_02_no_taxon.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_03_no_allele_mutation_type_mutation_types.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_04_no_allele_symbol.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_05_no_allele_symbol_display_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_06_no_allele_full_name_display_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_07_no_allele_synonym_display_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_08_no_allele_symbol_format_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_09_no_allele_full_name_format_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_10_no_allele_synonym_format_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_11_no_allele_symbol_name_type.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_12_no_allele_full_name_name_type.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_13_no_allele_synonym_name_type.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_14_no_allele_secondary_id_secondary_id.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_15_no_allele_inheritance_mode_inheritance_mode.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_16_no_data_provider.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_17_no_data_provider_source_organization_abbreviation.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_18_no_data_provider_cross_reference_referenced_curie.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_19_no_data_provider_cross_reference_display_name.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_20_no_data_provider_cross_reference_prefix.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_21_no_data_provider_cross_reference_page_area.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_22_no_allele_functional_impacts_functional_impacts.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_23_no_allele_germline_transmission_status_germline_transmission_status.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_24_no_related_notes_note_type_name.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_25_no_related_notes_free_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_26_no_allele_database_status_database_status.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_27_no_allele_nomenclature_events_nomenclature_event.json");
	}
	
	@Test
	@Order(4)
	public void alleleBulkUploadEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_01_empty_mod_ids.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_02_empty_taxon.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_03_empty_allele_mutation_type_mutation_types.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_04_empty_allele_symbol_display_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_05_empty_allele_full_name_display_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_06_empty_allele_synonym_display_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_07_empty_allele_symbol_format_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_08_empty_allele_full_name_format_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_09_empty_allele_synonym_format_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_10_empty_allele_symbol_name_type.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_11_empty_allele_full_name_name_type.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_12_empty_allele_synonym_name_type.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_13_empty_allele_secondary_id_secondary_id.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_14_empty_allele_inheritance_mode_inheritance_mode.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_15_empty_data_provider_source_organization_abbreviation.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_16_empty_data_provider_cross_reference_referenced_curie.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_17_empty_data_provider_cross_reference_display_name.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_18_empty_data_provider_cross_reference_prefix.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_19_empty_data_provider_cross_reference_page_area.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_20_empty_allele_functional_impacts_functional_impacts.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_21_empty_allele_germline_transmission_status_germline_transmission_status.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_22_empty_related_notes_note_type_name.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_23_empty_related_notes_free_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_24_empty_allele_database_status_database_status.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_25_empty_allele_nomenclature_events_nomenclature_event.json");
	}
	
	@Test
	@Order(5)
	public void alleleBulkUploadInvalidFields() throws Exception {
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_01_invalid_date_created.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_02_invalid_date_updated.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_03_invalid_taxon.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_04_invalid_in_collection.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_05_invalid_reference.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_06_invalid_allele_mutation_type_mutation_type.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_07_invalid_allele_mutation_type_evidence.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_08_invalid_allele_symbol_name_type.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_09_invalid_allele_full_name_name_type.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_10_invalid_allele_synonym_name_type.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_11_invalid_allele_symbol_synonym_scope.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_12_invalid_allele_full_name_synonym_scope.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_13_invalid_allele_synonym_synonym_scope.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_14_invalid_allele_symbol_evidence.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_15_invalid_allele_full_name_evidence.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_16_invalid_allele_synonym_evidence.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_17_invalid_allele_secondary_id_evidence.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_18_invalid_allele_inheritance_mode_inheritance_mode.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_19_invalid_allele_inheritance_mode_phenotype_term.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_20_invalid_allele_inheritance_mode_evidence.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_21_invalid_data_provider_source_organization_abbreviation.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_22_invalid_data_provider_cross_reference_prefix.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_23_invalid_data_provider_cross_reference_page_area.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_24_invalid_allele_functional_impacts_functional_impacts.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_25_invalid_allele_functional_impacts_phenotype_term.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_26_invalid_allele_functional_impacts_evidence.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_27_invalid_allele_germline_transmission_status_germline_transmission_status.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_28_invalid_allele_germline_transmission_status_evidence.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_29_invalid_related_notes_note_type_name.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_30_invalid_related_notes_evidence.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_31_invalid_allele_database_status_database_status.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_32_invalid_allele_database_status_evidence.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_33_invalid_allele_nomenclature_events_nomenclature_event.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_34_invalid_allele_nomenclature_events_evidence.json");
	}
	
	@Test
	@Order(6)
	public void alleleBulkUploadUpdateMissingNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "AF_01_all_fields.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "UM_01_update_no_non_required_fields_level_1.json");
		
		RestAssured.given().
			when().
			get(alleleGetEndpoint + "ALLELETEST:Allele0001").
			then().
			statusCode(200).
			body("entity.modEntityId", is("ALLELETEST:Allele0001")).
			body("entity", not(hasKey("createdBy"))).
			body("entity", not(hasKey("updatedBy"))).
			body("entity", not(hasKey("dateCreated"))).
			body("entity", not(hasKey("dateUpdated"))).
			body("entity", not(hasKey("inCollection"))).
			body("entity", not(hasKey("references"))).
			body("entity", not(hasKey("relatedNotes"))).
			body("entity", not(hasKey("alleleMutationTypes"))).
			body("entity", not(hasKey("alleleFullName"))).
			body("entity", not(hasKey("alleleSynonyms"))).
			body("entity", not(hasKey("alleleSecondaryIds"))).
			body("entity", not(hasKey("alleleInheritanceModes"))).
			body("entity", not(hasKey("alleleFunctionalImpacts"))).
			body("entity", not(hasKey("alleleGermlineTransmissionStatus"))).
			body("entity", not(hasKey("alleleDatabaseStatus"))).
			body("entity", not(hasKey("alleleNomenclatureEvents")));
	}

	@Test
	@Order(7)
	public void alleleBulkUploadUpdateMissingNonRequiredFieldsLevel2() throws Exception {
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "AF_01_all_fields.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "UM_02_update_no_non_required_fields_level_2.json");
		
		RestAssured.given().
			when().
			get(alleleGetEndpoint + "ALLELETEST:Allele0001").then().
			statusCode(200).
			body("entity.modEntityId", is("ALLELETEST:Allele0001")).
			body("entity.relatedNotes[0]", not(hasKey("evidence"))).
			body("entity.relatedNotes[0]", not(hasKey("createdBy"))).
			body("entity.relatedNotes[0]", not(hasKey("updatedBy"))).
			body("entity.relatedNotes[0]", not(hasKey("dateCreated"))).
			body("entity.relatedNotes[0]", not(hasKey("dateUpdated"))).
			body("entity.alleleMutationTypes[0]", not(hasKey("evidence"))).
			body("entity.alleleMutationTypes[0]", not(hasKey("createdBy"))).
			body("entity.alleleMutationTypes[0]", not(hasKey("updatedBy"))).
			body("entity.alleleMutationTypes[0]", not(hasKey("dateCreated"))).
			body("entity.alleleMutationTypes[0]", not(hasKey("dateUpdated"))).
			body("entity.alleleSymbol", not(hasKey("synonymScope"))).
			body("entity.alleleSymbol", not(hasKey("synonymUrl"))).
			body("entity.alleleSymbol", not(hasKey("evidence"))).
			body("entity.alleleSymbol", not(hasKey("createdBy"))).
			body("entity.alleleSymbol", not(hasKey("updatedBy"))).
			body("entity.alleleSymbol", not(hasKey("dateCreated"))).
			body("entity.alleleSymbol", not(hasKey("dateUpdated"))).
			body("entity.alleleFullName", not(hasKey("synonymScope"))).
			body("entity.alleleFullName", not(hasKey("synonymUrl"))).
			body("entity.alleleFullName", not(hasKey("evidence"))).
			body("entity.alleleFullName", not(hasKey("createdBy"))).
			body("entity.alleleFullName", not(hasKey("updatedBy"))).
			body("entity.alleleFullName", not(hasKey("dateCreated"))).
			body("entity.alleleFullName", not(hasKey("dateUpdated"))).
			body("entity.alleleSynonyms[0]", not(hasKey("synonymScope"))).
			body("entity.alleleSynonyms[0]", not(hasKey("synonymUrl"))).
			body("entity.alleleSynonyms[0]", not(hasKey("evidence"))).
			body("entity.alleleSynonyms[0]", not(hasKey("createdBy"))).
			body("entity.alleleSynonyms[0]", not(hasKey("updatedBy"))).
			body("entity.alleleSynonyms[0]", not(hasKey("dateCreated"))).
			body("entity.alleleSynonyms[0]", not(hasKey("dateUpdated"))).
			body("entity.alleleSecondaryIds[0]", not(hasKey("evidence"))).
			body("entity.alleleSecondaryIds[0]", not(hasKey("createdBy"))).
			body("entity.alleleSecondaryIds[0]", not(hasKey("updatedBy"))).
			body("entity.alleleSecondaryIds[0]", not(hasKey("dateCreated"))).
			body("entity.alleleSecondaryIds[0]", not(hasKey("dateUpdated"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("evidence"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("phenotypeTerm"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("phenotypeStatement"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("createdBy"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("updatedBy"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("dateCreated"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("dateUpdated"))).
			body("entity.alleleFunctionalImpacts[0]", not(hasKey("evidence"))).
			body("entity.alleleFunctionalImpacts[0]", not(hasKey("phenotypeTerm"))).
			body("entity.alleleFunctionalImpacts[0]", not(hasKey("phenotypeStatement"))).
			body("entity.alleleFunctionalImpacts[0]", not(hasKey("createdBy"))).
			body("entity.alleleFunctionalImpacts[0]", not(hasKey("updatedBy"))).
			body("entity.alleleFunctionalImpacts[0]", not(hasKey("dateCreated"))).
			body("entity.alleleFunctionalImpacts[0]", not(hasKey("dateUpdated"))).
			body("entity.alleleGermlineTransmissionStatus", not(hasKey("evidence"))).
			body("entity.alleleGermlineTransmissionStatus", not(hasKey("createdBy"))).
			body("entity.alleleGermlineTransmissionStatus", not(hasKey("updatedBy"))).
			body("entity.alleleGermlineTransmissionStatus", not(hasKey("dateCreated"))).
			body("entity.alleleGermlineTransmissionStatus", not(hasKey("dateUpdated"))).
			body("entity.alleleDatabaseStatus", not(hasKey("evidence"))).
			body("entity.alleleDatabaseStatus", not(hasKey("createdBy"))).
			body("entity.alleleDatabaseStatus", not(hasKey("updatedBy"))).
			body("entity.alleleDatabaseStatus", not(hasKey("dateCreated"))).
			body("entity.alleleDatabaseStatus", not(hasKey("dateUpdated"))).
			body("entity.alleleNomenclatureEvents[0]", not(hasKey("evidence"))).
			body("entity.alleleNomenclatureEvents[0]", not(hasKey("createdBy"))).
			body("entity.alleleNomenclatureEvents[0]", not(hasKey("updatedBy"))).
			body("entity.alleleNomenclatureEvents[0]", not(hasKey("dateCreated"))).
			body("entity.alleleNomenclatureEvents[0]", not(hasKey("dateUpdated")));
	}
	
	@Test
	@Order(8)
	public void alleleBulkUploadUpdateEmptyNonRequiredFieldsLevel() throws Exception {
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "AF_01_all_fields.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "UE_01_update_empty_non_required_fields.json");
		
		RestAssured.given().
		when().
		get(alleleGetEndpoint + "ALLELETEST:Allele0001").then().
		statusCode(200).
			body("entity.modEntityId", is("ALLELETEST:Allele0001")).
			body("entity", not(hasKey("createdBy"))).
			body("entity", not(hasKey("updatedBy"))).
			body("entity", not(hasKey("dateCreated"))).
			body("entity", not(hasKey("dateUpdated"))).
			body("entity", not(hasKey("inheritanceMode"))).
			body("entity", not(hasKey("inCollection"))).
			body("entity", not(hasKey("references"))).
			body("entity.relatedNotes[0]", not(hasKey("evidence"))).
			body("entity.relatedNotes[0]", not(hasKey("createdBy"))).
			body("entity.relatedNotes[0]", not(hasKey("updatedBy"))).
			body("entity.relatedNotes[0]", not(hasKey("dateCreated"))).
			body("entity.relatedNotes[0]", not(hasKey("dateUpdated"))).
			body("entity.alleleMutationTypes[0]", not(hasKey("evidence"))).
			body("entity.alleleMutationTypes[0]", not(hasKey("createdBy"))).
			body("entity.alleleMutationTypes[0]", not(hasKey("updatedBy"))).
			body("entity.alleleMutationTypes[0]", not(hasKey("dateCreated"))).
			body("entity.alleleMutationTypes[0]", not(hasKey("dateUpdated"))).
			body("entity.alleleSymbol", not(hasKey("synonymScope"))).
			body("entity.alleleSymbol", not(hasKey("synonymUrl"))).
			body("entity.alleleSymbol", not(hasKey("evidence"))).
			body("entity.alleleSymbol", not(hasKey("createdBy"))).
			body("entity.alleleSymbol", not(hasKey("updatedBy"))).
			body("entity.alleleSymbol", not(hasKey("dateCreated"))).
			body("entity.alleleSymbol", not(hasKey("dateUpdated"))).
			body("entity.alleleFullName", not(hasKey("synonymScope"))).
			body("entity.alleleFullName", not(hasKey("synonymUrl"))).
			body("entity.alleleFullName", not(hasKey("evidence"))).
			body("entity.alleleFullName", not(hasKey("createdBy"))).
			body("entity.alleleFullName", not(hasKey("updatedBy"))).
			body("entity.alleleFullName", not(hasKey("dateCreated"))).
			body("entity.alleleFullName", not(hasKey("dateUpdated"))).
			body("entity.alleleSynonyms[0]", not(hasKey("synonymScope"))).
			body("entity.alleleSynonyms[0]", not(hasKey("synonymUrl"))).
			body("entity.alleleSynonyms[0]", not(hasKey("evidence"))).
			body("entity.alleleSynonyms[0]", not(hasKey("createdBy"))).
			body("entity.alleleSynonyms[0]", not(hasKey("updatedBy"))).
			body("entity.alleleSynonyms[0]", not(hasKey("dateCreated"))).
			body("entity.alleleSynonyms[0]", not(hasKey("dateUpdated"))).
			body("entity.alleleSecondaryIds[0]", not(hasKey("evidence"))).
			body("entity.alleleSecondaryIds[0]", not(hasKey("createdBy"))).
			body("entity.alleleSecondaryIds[0]", not(hasKey("updatedBy"))).
			body("entity.alleleSecondaryIds[0]", not(hasKey("dateCreated"))).
			body("entity.alleleSecondaryIds[0]", not(hasKey("dateUpdated"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("evidence"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("phenotypeTerm"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("phenotypeStatement"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("createdBy"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("updatedBy"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("dateCreated"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("dateUpdated"))).
			body("entity.alleleFunctionalImpacts[0]", not(hasKey("evidence"))).
			body("entity.alleleFunctionalImpacts[0]", not(hasKey("phenotypeTerm"))).
			body("entity.alleleFunctionalImpacts[0]", not(hasKey("phenotypeStatement"))).
			body("entity.alleleFunctionalImpacts[0]", not(hasKey("createdBy"))).
			body("entity.alleleFunctionalImpacts[0]", not(hasKey("updatedBy"))).
			body("entity.alleleFunctionalImpacts[0]", not(hasKey("dateCreated"))).
			body("entity.alleleFunctionalImpacts[0]", not(hasKey("dateUpdated"))).
			body("entity.alleleGermlineTransmissionStatus", not(hasKey("evidence"))).
			body("entity.alleleGermlineTransmissionStatus", not(hasKey("createdBy"))).
			body("entity.alleleGermlineTransmissionStatus", not(hasKey("updatedBy"))).
			body("entity.alleleGermlineTransmissionStatus", not(hasKey("dateCreated"))).
			body("entity.alleleGermlineTransmissionStatus", not(hasKey("dateUpdated"))).
			body("entity.alleleDatabaseStatus", not(hasKey("evidence"))).
			body("entity.alleleDatabaseStatus", not(hasKey("createdBy"))).
			body("entity.alleleDatabaseStatus", not(hasKey("updatedBy"))).
			body("entity.alleleDatabaseStatus", not(hasKey("dateCreated"))).
			body("entity.alleleDatabaseStatus", not(hasKey("dateUpdated"))).
			body("entity.alleleNomenclatureEvents[0]", not(hasKey("evidence"))).
			body("entity.alleleNomenclatureEvents[0]", not(hasKey("createdBy"))).
			body("entity.alleleNomenclatureEvents[0]", not(hasKey("updatedBy"))).
			body("entity.alleleNomenclatureEvents[0]", not(hasKey("dateCreated"))).
			body("entity.alleleNomenclatureEvents[0]", not(hasKey("dateUpdated")));
	}
	
	@Test
	@Order(9)
	public void alleleBulkUploadMissingNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MN_01_no_non_required_fields_level_1.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MN_02_no_non_required_fields_level_2.json");
	}

	@Test
	@Order(10)
	public void alleleBulkUploadEmptyNonRequiredFieldsLevel() throws Exception {
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "EN_01_empty_non_required_fields.json");
	}
	
	@Test
	@Order(11)
	public void alleleBulkUploadSecondaryIds() throws Exception {
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "SI_01_secondary_ids.json");
	}
	
	@Test
	@Order(12)
	public void alleleBulkUploadDuplicateNotes() throws Exception {
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "DN_01_duplicate_notes.json");
		
		RestAssured.given().
			when().
			get(alleleGetEndpoint + "ALLELETEST:DN01").
			then().
			statusCode(200).
			body("entity.modEntityId", is("ALLELETEST:DN01")).
			body("entity.relatedNotes", hasSize(1));
	}
	
	@Test
	@Order(13)
	public void geneBulkUploadDataProviderChecks() throws Exception {
		checkFailedBulkLoad(alleleBulkPostEndpointRGD, alleleTestFilePath + "AF_01_all_fields.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpointHUMAN, alleleTestFilePath + "VT_01_valid_taxon_for_HUMAN.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpointRGD, alleleTestFilePath + "VT_02_valid_taxon_for_RGD.json");
		checkFailedBulkLoad(alleleBulkPostEndpointRGD, alleleTestFilePath + "VT_01_valid_taxon_for_HUMAN.json");
		checkFailedBulkLoad(alleleBulkPostEndpointHUMAN, alleleTestFilePath + "VT_02_valid_taxon_for_RGD.json");
	}
}
