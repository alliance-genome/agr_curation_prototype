package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.time.OffsetDateTime;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
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
@DisplayName("201 - Allele Gene Associations bulk upload")
@Order(201)
public class AlleleGeneAssociationBulkUploadITCase extends BaseITCase {
	
	private Allele allele;
	private Gene gene;
	private String alleleCurie = "ALLELETEST:Allele0001";
	private String relationName = "is_allele_of";
	private String geneCurie = "GENETEST:Gene0001";
	private String reference = "AGRKB:000000001";
	private String reference2 = "AGRKB:000000021";
	private String evidenceCodeCurie = "DATEST:Evidence0001";
	private String evidenceCodeCurie2 = "DATEST:Evidence0002";
	private String noteType = "comment";
	private String noteType2 = "remark";
	
	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 60000)
					.setParam("http.connection.timeout", 60000));
	}

	private final String alleleGeneAssociationBulkPostEndpoint = "/api/allelegeneassociation/bulk/WB/associationFile";
	private final String alleleGeneAssociationGetEndpoint = "/api/allelegeneassociation/findBy";
	private final String alleleGeneAssociationTestFilePath = "src/test/resources/bulk/AA01_allele_gene_association/";
	private final String alleleGetEndpoint = "/api/allele/";
	private final String geneGetEndpoint = "/api/gene/";

	private void loadRequiredEntities() throws Exception {
		Vocabulary noteTypeVocab = getVocabulary("note_type");
		createVocabularyTerm(noteTypeVocab, noteType2, false);
		addVocabularyTermToSet("allele_genomic_entity_association_note_type", noteType2, noteTypeVocab, false);
		allele = getAllele(alleleCurie);
		gene = getGene(geneCurie);
	}
	
	@Test
	@Order(1)
	public void alleleGeneAssociationBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();
		
		checkSuccessfulBulkLoad(alleleGeneAssociationBulkPostEndpoint, alleleGeneAssociationTestFilePath + "AF_01_all_fields.json");
	
		RestAssured.given().
			when().
			get(alleleGeneAssociationGetEndpoint + "?alleleId=" + allele.getId() + "&relationName=" + relationName + "&geneId=" + gene.getId()).
			then().
			statusCode(200).
			body("entity.relation.name", is(relationName)).
			body("entity.alleleGeneAssociationObject.primaryExternalId", is(geneCurie)).
			body("entity.alleleAssociationSubject.primaryExternalId", is(alleleCurie)).
			body("entity.evidence", hasSize(1)).
			body("entity.evidence[0].curie", is(reference)).
			body("entity.evidenceCode.curie", is(evidenceCodeCurie)).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.relatedNote.internal", is(false)).
			body("entity.relatedNote.obsolete", is(true)).
			body("entity.relatedNote.updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.relatedNote.createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.relatedNote.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.relatedNote.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.relatedNote.freeText", is("Test note")).
			body("entity.relatedNote.noteType.name", is(noteType)).
			body("entity.relatedNote.references[0].curie", is(reference));
		
		RestAssured.given().
			when().
			get(alleleGetEndpoint + alleleCurie).
			then().
			statusCode(200).
			body("entity.alleleGeneAssociations", hasSize(1)).
			body("entity.alleleGeneAssociations[0].relation.name", is(relationName)).
			body("entity.alleleGeneAssociations[0].alleleGeneAssociationObject.primaryExternalId", is(geneCurie)).
			body("entity.alleleGeneAssociations[0].alleleAssociationSubject", not(hasKey("alleleGeneAssociations")));
		
		RestAssured.given().
			when().
			get(geneGetEndpoint + geneCurie).
			then().
			statusCode(200).
			body("entity.alleleGeneAssociations", hasSize(1)).
			body("entity.alleleGeneAssociations[0].relation.name", is(relationName)).
			body("entity.alleleGeneAssociations[0].alleleGeneAssociationObject.primaryExternalId", is(geneCurie)).
			body("entity.alleleGeneAssociations[0].alleleGeneAssociationObject", not(hasKey("alleleGeneAssociations")));
	}
	
	@Test
	@Order(2)
	public void alleleGeneAssociationBulkUploadUpdateCheckFields() throws Exception {
		loadRequiredEntities();
		
		checkSuccessfulBulkLoad(alleleGeneAssociationBulkPostEndpoint, alleleGeneAssociationTestFilePath + "UD_01_update_all_except_default_fields.json");
	
		RestAssured.given().
			when().
			get(alleleGeneAssociationGetEndpoint + "?alleleId=" + allele.getId() + "&relationName=" + relationName + "&geneId=" + gene.getId()).
			then().
			statusCode(200).
			body("entity.relation.name", is(relationName)).
			body("entity.alleleGeneAssociationObject.primaryExternalId", is(geneCurie)).
			body("entity.evidence", hasSize(1)).
			body("entity.evidence[0].curie", is(reference2)).
			body("entity.evidenceCode.curie", is(evidenceCodeCurie2)).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.createdBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.updatedBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString())).
			body("entity.relatedNote.internal", is(true)).
			body("entity.relatedNote.obsolete", is(false)).
			body("entity.relatedNote.updatedBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.relatedNote.createdBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.relatedNote.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString())).
			body("entity.relatedNote.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
			body("entity.relatedNote.freeText", is("Test note 2")).
			body("entity.relatedNote.noteType.name", is(noteType2)).
			body("entity.relatedNote.references[0].curie", is(reference2));
		
		RestAssured.given().
			when().
			get(alleleGetEndpoint + alleleCurie).
			then().
			statusCode(200).
			body("entity.alleleGeneAssociations", hasSize(1));
		
		RestAssured.given().
			when().
			get(geneGetEndpoint + geneCurie).
			then().
			statusCode(200).
			body("entity.alleleGeneAssociations", hasSize(1));
	}
	
	@Test
	@Order(3)
	public void alleleGeneAssociationBulkUploadMissingRequiredFields() throws Exception {
		checkFailedBulkLoad(alleleGeneAssociationBulkPostEndpoint, alleleGeneAssociationTestFilePath + "MR_01_no_subject.json");
		checkFailedBulkLoad(alleleGeneAssociationBulkPostEndpoint, alleleGeneAssociationTestFilePath + "MR_02_no_relation.json");
		checkFailedBulkLoad(alleleGeneAssociationBulkPostEndpoint, alleleGeneAssociationTestFilePath + "MR_03_no_object.json");
		checkFailedBulkLoad(alleleGeneAssociationBulkPostEndpoint, alleleGeneAssociationTestFilePath + "MR_04_no_related_note_note_type.json");
		checkFailedBulkLoad(alleleGeneAssociationBulkPostEndpoint, alleleGeneAssociationTestFilePath + "MR_05_no_related_note_free_text.json");
	}
	
	@Test
	@Order(4)
	public void alleleGeneAssociationBulkUploadEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(alleleGeneAssociationBulkPostEndpoint, alleleGeneAssociationTestFilePath + "ER_01_empty_subject.json");
		checkFailedBulkLoad(alleleGeneAssociationBulkPostEndpoint, alleleGeneAssociationTestFilePath + "ER_02_empty_relation.json");
		checkFailedBulkLoad(alleleGeneAssociationBulkPostEndpoint, alleleGeneAssociationTestFilePath + "ER_03_empty_object.json");
		checkFailedBulkLoad(alleleGeneAssociationBulkPostEndpoint, alleleGeneAssociationTestFilePath + "ER_04_empty_related_note_note_type.json");
		checkFailedBulkLoad(alleleGeneAssociationBulkPostEndpoint, alleleGeneAssociationTestFilePath + "ER_05_empty_related_note_free_text.json");
	}
	
	@Test
	@Order(5)
	public void alleleGeneAssociationBulkUploadInvalidFields() throws Exception {
		checkFailedBulkLoad(alleleGeneAssociationBulkPostEndpoint, alleleGeneAssociationTestFilePath + "IV_01_invalid_subject.json");
		checkFailedBulkLoad(alleleGeneAssociationBulkPostEndpoint, alleleGeneAssociationTestFilePath + "IV_02_invalid_relation.json");
		checkFailedBulkLoad(alleleGeneAssociationBulkPostEndpoint, alleleGeneAssociationTestFilePath + "IV_03_invalid_object.json");
		checkFailedBulkLoad(alleleGeneAssociationBulkPostEndpoint, alleleGeneAssociationTestFilePath + "IV_04_invalid_date_created.json");
		checkFailedBulkLoad(alleleGeneAssociationBulkPostEndpoint, alleleGeneAssociationTestFilePath + "IV_05_invalid_date_updated.json");
		checkFailedBulkLoad(alleleGeneAssociationBulkPostEndpoint, alleleGeneAssociationTestFilePath + "IV_06_invalid_evidence_code.json");
		checkFailedBulkLoad(alleleGeneAssociationBulkPostEndpoint, alleleGeneAssociationTestFilePath + "IV_07_invalid_evidence.json");
		checkFailedBulkLoad(alleleGeneAssociationBulkPostEndpoint, alleleGeneAssociationTestFilePath + "IV_08_invalid_related_note_note_type.json");
	}
	
	@Test
	@Order(6)
	public void alleleGeneAssociationBulkUploadUpdateMissingNonRequiredFieldsLevel1() throws Exception {
		checkSuccessfulBulkLoad(alleleGeneAssociationBulkPostEndpoint, alleleGeneAssociationTestFilePath + "UD_01_update_all_except_default_fields.json");
		checkSuccessfulBulkLoad(alleleGeneAssociationBulkPostEndpoint, alleleGeneAssociationTestFilePath + "UM_01_update_no_non_required_fields_level_1.json");
		
		RestAssured.given().
			when().
			get(alleleGeneAssociationGetEndpoint + "?alleleId=" + allele.getId() + "&relationName=" + relationName + "&geneId=" + gene.getId()).
			then().
			statusCode(200).
			body("entity", not(hasKey("createdBy"))).
			body("entity", not(hasKey("updatedBy"))).
			body("entity", not(hasKey("evidence"))).
			body("entity", not(hasKey("evidenceCode"))).
			body("entity", not(hasKey("relatedNote")));
	}
	
	@Test
	@Order(7)
	public void alleleGeneAssociationBulkUploadUpdateMissingNonRequiredFieldsLevel2() throws Exception {
		checkSuccessfulBulkLoad(alleleGeneAssociationBulkPostEndpoint, alleleGeneAssociationTestFilePath + "UD_01_update_all_except_default_fields.json");
		checkSuccessfulBulkLoad(alleleGeneAssociationBulkPostEndpoint, alleleGeneAssociationTestFilePath + "UM_02_update_no_non_required_fields_level_2.json");
		
		RestAssured.given().
			when().
			get(alleleGeneAssociationGetEndpoint + "?alleleId=" + allele.getId() + "&relationName=" + relationName + "&geneId=" + gene.getId()).
			then().
			statusCode(200).
			body("entity.relatedNote", not(hasKey("createdBy"))).
			body("entity.relatedNote", not(hasKey("updatedBy"))).
			body("entity.relatedNote", not(hasKey("evidence")));
	}
	
	@Test
	@Order(8)
	public void alleleGeneAssociationBulkUploadUpdateEmptyNonRequiredFieldsLevel2() throws Exception {
		checkSuccessfulBulkLoad(alleleGeneAssociationBulkPostEndpoint, alleleGeneAssociationTestFilePath + "UD_01_update_all_except_default_fields.json");
		checkSuccessfulBulkLoad(alleleGeneAssociationBulkPostEndpoint, alleleGeneAssociationTestFilePath + "UE_01_update_empty_non_required_fields.json");
		
		RestAssured.given().
			when().
			get(alleleGeneAssociationGetEndpoint + "?alleleId=" + allele.getId() + "&relationName=" + relationName + "&geneId=" + gene.getId()).
			then().
			statusCode(200).
			body("entity", not(hasKey("createdBy"))).
			body("entity", not(hasKey("updatedBy"))).
			body("entity", not(hasKey("evidence"))).
			body("entity", not(hasKey("evidenceCode"))).
			body("entity.relatedNote", not(hasKey("createdBy"))).
			body("entity.relatedNote", not(hasKey("updatedBy"))).
			body("entity.relatedNote", not(hasKey("evidence")));
	}
	
}
