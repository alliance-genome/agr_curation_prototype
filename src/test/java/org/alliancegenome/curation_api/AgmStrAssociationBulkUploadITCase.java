package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.time.OffsetDateTime;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
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
@DisplayName("1001 - AffectedGenomicModel SequenceTargetingReagent Associations bulk upload")
@Order(1001)
public class AgmStrAssociationBulkUploadITCase extends BaseITCase {
	
	private AffectedGenomicModel agm;
	private SequenceTargetingReagent str;
	private String agmCurie = "AMGTEST:AffectedGenomicModel0001";
	private String relationName = "contains";
	private String strCurie = "ZFIN:ZDB-TALEN-180503-1";
	private String vocabTerm = "contains";
	
	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 60000)
					.setParam("http.connection.timeout", 60000));
	}

	private final String agmSequenceTargetingReagentAssociationBulkPostEndpoint = "/api/agmstrassociation/bulk/WB/associationFile";
	private final String agmSequenceTargetingReagentAssociationGetEndpoint = "/api/agmstrassociation/findBy";
	private final String agmSequenceTargetingReagentAssociationTestFilePath = "src/test/resources/bulk/AGMA01_agm_str_association";
	private final String agmGetEndpoint = "/api/agm/";
	private final String strGetEndpoint = "/api/str/";

	private void loadRequiredEntities() throws Exception {
		Vocabulary agmRelationVocab = createVocabulary("agm_relation", false);
		createVocabularyTerm(agmRelationVocab, vocabTerm, false);
		addVocabularyTermToSet("agm_str_relation", vocabTerm, agmRelationVocab, false);
		agm = getAffectedGenomicModel(agmCurie);
		str = getSequenceTargetingReagent(strCurie);
	}
	
	@Test
	@Order(1)
	public void agmSequenceTargetingReagentAssociationBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();
		
		checkSuccessfulBulkLoad(agmSequenceTargetingReagentAssociationBulkPostEndpoint, agmSequenceTargetingReagentAssociationTestFilePath + "AF_01_all_fields.json");
	
		RestAssured.given().
			when().
			get(agmSequenceTargetingReagentAssociationGetEndpoint + "?agmId=" + agm.getId() + "&relationName=" + relationName + "&strId=" + str.getId()).
			then().
			statusCode(200).
			body("entity.relation.name", is(relationName)).
			body("entity.agmSequenceTargetingReagentAssociationObject.modEntityId", is(strCurie)).
			body("entity.agmSubjectIdentifier.modEntityId", is(agmCurie)).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.createdBy.uniqueId", is("AMGTEST:Person0001")).
			body("entity.updatedBy.uniqueId", is("AMGTEST:Person0002")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12+00:00").toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12+00:00").toString()));
		
		RestAssured.given().
			when().
			get(agmGetEndpoint + agmCurie).
			then().
			statusCode(200).
			body("entity.agmSequenceTargetingReagentAssociations", hasSize(1)).
			body("entity.agmSequenceTargetingReagentAssociations[0].relation.name", is(relationName)).
			body("entity.agmSequenceTargetingReagentAssociations[0].agmSequenceTargetingReagentAssociationObject.modEntityId", is(strCurie)).
			body("entity.agmSequenceTargetingReagentAssociations[0].agmAssociationSubject", not(hasKey("agmSequenceTargetingReagentAssociations")));
		
		RestAssured.given().
			when().
			get(strGetEndpoint + strCurie).
			then().
			statusCode(200).
			body("entity.agmSequenceTargetingReagentAssociations", hasSize(1)).
			body("entity.agmSequenceTargetingReagentAssociations[0].relation.name", is(relationName)).
			body("entity.agmSequenceTargetingReagentAssociations[0].agmSequenceTargetingReagentAssociationObject.modEntityId", is(strCurie)).
			body("entity.agmSequenceTargetingReagentAssociations[0].agmSequenceTargetingReagentAssociationObject", not(hasKey("agmSequenceTargetingReagentAssociations")));
	}

	@Test
	@Order(2)
	public void agmStrAssociationBulkUploadUpdateCheckFields() throws Exception {
		loadRequiredEntities();

		checkSuccessfulBulkLoad(agmSequenceTargetingReagentAssociationBulkPostEndpoint, agmSequenceTargetingReagentAssociationTestFilePath + "UD_01_update_all_except_default_fields.json");

		RestAssured.given().
				when().
				get(agmSequenceTargetingReagentAssociationGetEndpoint + "?agmId=" + agm.getId() + "&relationName=" + relationName + "&strId=" + str.getId()).
				then().
				statusCode(200).
				body("entity.relation.name", is(relationName)).
				body("entity.agmSequenceTargetingReagentAssociationObject.modEntityId", is(strCurie)).
				body("entity.agmSubjectIdentifier.modEntityId", is(agmCurie)).
				body("entity.internal", is(false)).
				body("entity.obsolete", is(false)).
				body("entity.createdBy.uniqueId", is("AMGTEST:Person0002")).
				body("entity.updatedBy.uniqueId", is("AMGTEST:Person0001")).
				body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
				body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString()));


		RestAssured.given().
				when().
				get(agmGetEndpoint + agmCurie).
				then().
				statusCode(200).
				body("entity.agmSequenceTargetingReagentAssociations", hasSize(1));

		RestAssured.given().
				when().
				get(strGetEndpoint + strCurie).
				then().
				statusCode(200).
				body("entity.agmSequenceTargetingReagentAssociations", hasSize(1));
	}

	@Test
	@Order(3)
	public void agmStrAssociationBulkUploadMissingRequiredFields() throws Exception {
		checkFailedBulkLoad(agmSequenceTargetingReagentAssociationBulkPostEndpoint, agmSequenceTargetingReagentAssociationTestFilePath + "MR_01_no_subject.json");
		checkFailedBulkLoad(agmSequenceTargetingReagentAssociationBulkPostEndpoint, agmSequenceTargetingReagentAssociationTestFilePath + "MR_02_no_relation.json");
		checkFailedBulkLoad(agmSequenceTargetingReagentAssociationBulkPostEndpoint, agmSequenceTargetingReagentAssociationTestFilePath + "MR_03_no_object.json");
	}

	@Test
	@Order(4)
	public void agmStrAssociationBulkUploadEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(agmSequenceTargetingReagentAssociationBulkPostEndpoint, agmSequenceTargetingReagentAssociationTestFilePath + "ER_01_empty_subject.json");
		checkFailedBulkLoad(agmSequenceTargetingReagentAssociationBulkPostEndpoint, agmSequenceTargetingReagentAssociationTestFilePath + "ER_02_empty_relation.json");
		checkFailedBulkLoad(agmSequenceTargetingReagentAssociationBulkPostEndpoint, agmSequenceTargetingReagentAssociationTestFilePath + "ER_03_empty_object.json");
	}

	@Test
	@Order(5)
	public void agmStrAssociationBulkUploadInvalidFields() throws Exception {
		checkFailedBulkLoad(agmSequenceTargetingReagentAssociationBulkPostEndpoint, agmSequenceTargetingReagentAssociationTestFilePath + "IV_01_invalid_subject.json");
		checkFailedBulkLoad(agmSequenceTargetingReagentAssociationBulkPostEndpoint, agmSequenceTargetingReagentAssociationTestFilePath + "IV_02_invalid_relation.json");
		checkFailedBulkLoad(agmSequenceTargetingReagentAssociationBulkPostEndpoint, agmSequenceTargetingReagentAssociationTestFilePath + "IV_03_invalid_object.json");
		checkFailedBulkLoad(agmSequenceTargetingReagentAssociationBulkPostEndpoint, agmSequenceTargetingReagentAssociationTestFilePath + "IV_04_invalid_date_created.json");
		checkFailedBulkLoad(agmSequenceTargetingReagentAssociationBulkPostEndpoint, agmSequenceTargetingReagentAssociationTestFilePath + "IV_05_invalid_date_updated.json");
	}
}
