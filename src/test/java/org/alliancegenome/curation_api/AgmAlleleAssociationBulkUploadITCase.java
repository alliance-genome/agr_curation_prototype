package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.time.OffsetDateTime;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Organization;
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
@DisplayName("1002 - AffectedGenomicModel Allele Associations bulk upload")
@Order(1002)
public class AgmAlleleAssociationBulkUploadITCase extends BaseITCase {
	
	private AffectedGenomicModel agm;
	private Allele allele;
	private String agmCurie = "AMGTEST:AffectedGenomicModel0001";
	private String relationName = "contains";
	private String alleleCurie = "AAA:Allele0001";
	private String zygosityCurie = "GENO:0000602";
	
	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 60000)
					.setParam("http.connection.timeout", 60000));
	}

	private final String agmAlleleAssociationBulkPostEndpoint = "/api/agmalleleassociation/bulk/ZFIN/associationFile";
	private final String agmAlleleAssociationGetEndpoint = "/api/agmalleleassociation/findBy";
	private final String agmAlleleAssociationTestFilePath = "src/test/resources/bulk/AGMA02_agm_allele_association/";
	private final String agmGetEndpoint = "/api/agm/";

	private void loadRequiredEntities() throws Exception {
		Vocabulary nameTypeVocabulary = getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY);
		VocabularyTerm symbolNameType = getVocabularyTerm(nameTypeVocabulary, "nomenclature_symbol");
		Organization dataProvider = getOrganization("ZFIN");
		allele = createAllele(alleleCurie, alleleCurie, "NCBITaxon:7955", symbolNameType, false, dataProvider);
		agm = getAffectedGenomicModel(agmCurie);
	}

	@Test
	@Order(1)
	public void agmAlleleAssociationBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();

		checkSuccessfulBulkLoad(agmAlleleAssociationBulkPostEndpoint, agmAlleleAssociationTestFilePath + "AF_01_all_fields.json");
	
		RestAssured.given().
			when().
			get(agmAlleleAssociationGetEndpoint + "?agmId=" + agm.getId() + "&relationName=" + relationName + "&alleleId=" + allele.getId()).
			then().
			statusCode(200).
			body("entity.relation.name", is(relationName)).
			body("entity.agmAlleleAssociationObject.primaryExternalId", is(alleleCurie)).
			body("entity.agmAssociationSubject.primaryExternalId", is(agmCurie)).
			body("entity.zygosity.name", is(zygosityCurie)).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.createdBy.uniqueId", is("AGMTEST:Person0001")).
			body("entity.updatedBy.uniqueId", is("AGMTEST:Person0002")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12+00:00").toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12+00:00").toString()));
		
		RestAssured.given().
			when().
			get(agmGetEndpoint + agmCurie).
			then().
			statusCode(200).
			body("entity.components", hasSize(1)).
			body("entity.components[0].relation.name", is(relationName)).
			body("entity.components[0].agmAlleleAssociationObject.primaryExternalId", is(alleleCurie)).
			body("entity.components[0].agmAssociationSubject", not(hasKey("agmAlleles"))).
			body("entity.components[0].zygosity.name", is(zygosityCurie));

	}

	@Test
	@Order(2)
	public void agmAlleleAssociationBulkUploadUpdateCheckFields() throws Exception {

		checkSuccessfulBulkLoad(agmAlleleAssociationBulkPostEndpoint, agmAlleleAssociationTestFilePath + "UD_01_update_all_except_default_fields.json");

		RestAssured.given().
				when().
				get(agmAlleleAssociationGetEndpoint + "?agmId=" + agm.getId() + "&relationName=" + relationName + "&alleleId=" + allele.getId()).
				then().
				statusCode(200).
				body("entity.relation.name", is(relationName)).
				body("entity.agmAlleleAssociationObject.primaryExternalId", is(alleleCurie)).
				body("entity.agmAssociationSubject.primaryExternalId", is(agmCurie)).
				body("entity.zygosity.name", is(zygosityCurie)).
				body("entity.internal", is(false)).
				body("entity.obsolete", is(false)).
				body("entity.createdBy.uniqueId", is("AGMTEST:Person0001")).
				body("entity.updatedBy.uniqueId", is("AGMTEST:Person0002")).
				body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12+00:00").toString())).
				body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12+00:00").toString()));

		RestAssured.given().
				when().
				get(agmGetEndpoint + agmCurie).
				then().
				statusCode(200).
				body("entity.components", hasSize(1));

	}

	@Test
	@Order(3)
	public void agmAlleleAssociationBulkUploadMissingRequiredFields() throws Exception {
		checkFailedBulkLoad(agmAlleleAssociationBulkPostEndpoint, agmAlleleAssociationTestFilePath + "MR_01_no_subject.json");
		checkFailedBulkLoad(agmAlleleAssociationBulkPostEndpoint, agmAlleleAssociationTestFilePath + "MR_02_no_relation.json");
		checkFailedBulkLoad(agmAlleleAssociationBulkPostEndpoint, agmAlleleAssociationTestFilePath + "MR_03_no_object.json");
	}

	@Test
	@Order(4)
	public void agmAlleleAssociationBulkUploadEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(agmAlleleAssociationBulkPostEndpoint, agmAlleleAssociationTestFilePath + "ER_01_empty_subject.json");
		checkFailedBulkLoad(agmAlleleAssociationBulkPostEndpoint, agmAlleleAssociationTestFilePath + "ER_02_empty_relation.json");
		checkFailedBulkLoad(agmAlleleAssociationBulkPostEndpoint, agmAlleleAssociationTestFilePath + "ER_03_empty_object.json");
	}

	@Test
	@Order(5)
	public void agmAlleleAssociationBulkUploadInvalidFields() throws Exception {
		checkFailedBulkLoad(agmAlleleAssociationBulkPostEndpoint, agmAlleleAssociationTestFilePath + "IV_01_invalid_subject.json");
		checkFailedBulkLoad(agmAlleleAssociationBulkPostEndpoint, agmAlleleAssociationTestFilePath + "IV_02_invalid_relation.json");
		checkFailedBulkLoad(agmAlleleAssociationBulkPostEndpoint, agmAlleleAssociationTestFilePath + "IV_03_invalid_object.json");
		checkFailedBulkLoad(agmAlleleAssociationBulkPostEndpoint, agmAlleleAssociationTestFilePath + "IV_04_invalid_date_created.json");
		checkFailedBulkLoad(agmAlleleAssociationBulkPostEndpoint, agmAlleleAssociationTestFilePath + "IV_05_invalid_date_updated.json");
		checkFailedBulkLoad(agmAlleleAssociationBulkPostEndpoint, agmAlleleAssociationTestFilePath + "IV_06_invalid_zygosity.json");
	}
}
