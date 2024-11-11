package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.AGMPhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.AllelePhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.alliancegenome.curation_api.response.SearchResponse;
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
@DisplayName("603 - Phenotype Annotation bulk upload - FMS")
@Order(603)
public class PhenotypeAnnotationBulkUploadFmsITCase extends BaseITCase {

	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
						.setParam("http.socket.timeout", 100000)
						.setParam("http.connection.timeout", 100000));
	}

	private final String phenotypeAnnotationBulkPostEndpoint = "/api/phenotype-annotation/bulk/WB/annotationFile";
	private final String phenotypeAnnotationTestFilePath = "src/test/resources/bulk/fms/03_phenotype_annotation/";
	private final String phenotypeAnnotationGetEndpoint = "/api/phenotype-annotation/";
	private final String agmPhenotypeAnnotationFindEndpoint = "/api/agm-phenotype-annotation/find?limit=100&page=0";
	private final String genePhenotypeAnnotationFindEndpoint = "/api/gene-phenotype-annotation/find?limit=100&page=0";
	private final String allelePhenotypeAnnotationFindEndpoint = "/api/allele-phenotype-annotation/find?limit=100&page=0";
	private Long agmPaId;
	private Long allelePaId;
	private final String agm = "PATEST:AGM0001";
	private final String agm2 = "PATEST:AGM0002";
	private final String allele = "PATEST:Allele0001";
	private final String allele2 = "PATEST:Allele0002";
	private final String gene = "PATEST:Gene0001";
	private final String reference = "AGRKB:000000002";
	private final String conditionRelationType = "exacerbated_by";
	private final String zecoTerm = "PATEST:ExpCondTerm0001";
	private final String nonSlimZecoTerm = "PATEST:NSExpCondTerm0001";
	private final String goTerm = "PATEST:GOTerm0001";
	private final String anatomyTerm = "PATEST:AnatomyTerm0001";
	private final String chemicalTerm = "PATEST:ChemicalTerm0001";
	private final String expCondTerm = "PATEST:ExpCondTerm0002";
	private final String mpTerm = "PATEST:MPTerm001";
	private final String phenotypeStatement = "test phenotype statement";

	private void loadRequiredEntities() throws Exception {
		createGoTerm(goTerm, "Test GOTerm", false);
		createExperimentalConditionOntologyTerm(expCondTerm, "Test ExperimentalConditionOntologyTerm");
		createZecoTerm(zecoTerm, "Test ExperimentalConditionOntologyTerm", false, OntologyConstants.ZECO_AGR_SLIM_SUBSET);
		createZecoTerm(nonSlimZecoTerm, "Test ExperimentalConditionOntologyTerm", false);
		createChemicalTerm(chemicalTerm, "Test ChemicalTerm");
		createAnatomicalTerm(anatomyTerm, "Test AnatomicalTerm");
		Vocabulary nameTypeVocabulary = getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY);
		VocabularyTerm symbolTerm = getVocabularyTerm(nameTypeVocabulary, "nomenclature_symbol");
		DataProvider dataProvider = createDataProvider("WB", false);
		createGene(gene, "NCBITaxon:6239", symbolTerm, false, dataProvider);
		createAllele(allele, "TestAllele", "NCBITaxon:6239", symbolTerm, false, dataProvider);
		createAllele(allele2, "TestAllele2", "NCBITaxon:6239", symbolTerm, false, dataProvider);
		createAffectedGenomicModel(agm, "Test AGM", "NCBITaxon:6239", "strain", false, dataProvider);
		createAffectedGenomicModel(agm2, "Test AGM2", "NCBITaxon:6239", "strain", false, dataProvider);
		createMpTerm(mpTerm, "Test PhenotypeTerm");
		ResourceDescriptor rd = createResourceDescriptor("PMID");
		createResourceDescriptorPage("default", "https://www.ncbi.nlm.nih.gov/pubmed/[%s]", rd);
	}

	@Test
	@Order(1)
	public void agmPhenotypeAnnotationBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();

		checkSuccessfulBulkLoad(phenotypeAnnotationBulkPostEndpoint,
				phenotypeAnnotationTestFilePath + "AF_01_all_fields_primary_agm_annotation.json");

		SearchResponse<AGMPhenotypeAnnotation> response = RestAssured.given().when()
				.header("Content-Type", "application/json").body("{}").post(agmPhenotypeAnnotationFindEndpoint).then()
				.statusCode(200).body("totalResults", is(1)).body("results", hasSize(1))
				.body("results[0].phenotypeAnnotationSubject.modEntityId", is(agm))
				.body("results[0].phenotypeAnnotationObject", is(phenotypeStatement))
				.body("results[0].relation.name", is("has_phenotype"))
				.body("results[0].dateCreated", is("2024-01-17T15:26:56Z"))
				.body("results[0].conditionRelations", hasSize(1))
				.body("results[0].conditionRelations[0].internal", is(false))
				.body("results[0].conditionRelations[0].obsolete", is(false))
				.body("results[0].conditionRelations[0].conditionRelationType.name", is(conditionRelationType))
				.body("results[0].conditionRelations[0].conditions", hasSize(1))
				.body("results[0].conditionRelations[0].conditions[0].internal", is(false))
				.body("results[0].conditionRelations[0].conditions[0].obsolete", is(false))
				.body("results[0].conditionRelations[0].conditions[0].conditionClass.curie", is(zecoTerm))
				.body("results[0].conditionRelations[0].conditions[0].conditionId.curie", is(expCondTerm))
				.body("results[0].conditionRelations[0].conditions[0].conditionQuantity", is("Some amount"))
				.body("results[0].conditionRelations[0].conditions[0].conditionAnatomy.curie", is(anatomyTerm))
				.body("results[0].conditionRelations[0].conditions[0].conditionGeneOntology.curie", is(goTerm))
				.body("results[0].conditionRelations[0].conditions[0].conditionTaxon.curie", is("NCBITaxon:6239"))
				.body("results[0].conditionRelations[0].conditions[0].conditionChemical.curie", is(chemicalTerm))
				.body("results[0].conditionRelations[0].conditions[0].conditionSummary", is("condition summary test"))
				.body("results[0].singleReference.curie", is(reference))
				.body("results[0].crossReference.displayName", is("PMID:25920554"))
				.body("results[0].phenotypeTerms", hasSize(1))
				.body("results[0].phenotypeTerms[0].curie", is(mpTerm)).extract().body()
				.as(getSearchResponseTypeRefAGMPhenotypeAnnotation());

		agmPaId = response.getResults().get(0).getId();
	}

	@Test
	@Order(2)
	public void allelePhenotypeAnnotationBulkUploadCheckFields() throws Exception {

		checkSuccessfulBulkLoad(phenotypeAnnotationBulkPostEndpoint,
				phenotypeAnnotationTestFilePath + "AF_02_all_fields_primary_allele_annotation.json");

		SearchResponse<AllelePhenotypeAnnotation> response = RestAssured.given().when()
				.header("Content-Type", "application/json").body("{}").post(allelePhenotypeAnnotationFindEndpoint)
				.then().statusCode(200).body("totalResults", is(1)).body("results", hasSize(1))
				.body("results[0].phenotypeAnnotationSubject.modEntityId", is(allele))
				.body("results[0].phenotypeAnnotationObject", is(phenotypeStatement))
				.body("results[0].relation.name", is("has_phenotype"))
				.body("results[0].dateCreated", is("2024-01-17T15:26:56Z"))
				.body("results[0].conditionRelations", hasSize(1))
				.body("results[0].conditionRelations[0].internal", is(false))
				.body("results[0].conditionRelations[0].obsolete", is(false))
				.body("results[0].conditionRelations[0].conditionRelationType.name", is(conditionRelationType))
				.body("results[0].conditionRelations[0].conditions", hasSize(1))
				.body("results[0].conditionRelations[0].conditions[0].internal", is(false))
				.body("results[0].conditionRelations[0].conditions[0].obsolete", is(false))
				.body("results[0].conditionRelations[0].conditions[0].conditionClass.curie", is(zecoTerm))
				.body("results[0].conditionRelations[0].conditions[0].conditionId.curie", is(expCondTerm))
				.body("results[0].conditionRelations[0].conditions[0].conditionQuantity", is("Some amount"))
				.body("results[0].conditionRelations[0].conditions[0].conditionAnatomy.curie", is(anatomyTerm))
				.body("results[0].conditionRelations[0].conditions[0].conditionGeneOntology.curie", is(goTerm))
				.body("results[0].conditionRelations[0].conditions[0].conditionTaxon.curie", is("NCBITaxon:6239"))
				.body("results[0].conditionRelations[0].conditions[0].conditionChemical.curie", is(chemicalTerm))
				.body("results[0].conditionRelations[0].conditions[0].conditionSummary", is("condition summary test"))
				.body("results[0].singleReference.curie", is(reference))
				.body("results[0].crossReference.displayName", is("PMID:25920554"))
				.body("results[0].phenotypeTerms", hasSize(1))
				.body("results[0].phenotypeTerms[0].curie", is(mpTerm)).extract().body()
				.as(getSearchResponseTypeRefAllelePhenotypeAnnotation());

		allelePaId = response.getResults().get(0).getId();
	}

	@Test
	@Order(3)
	public void genePhenotypeAnnotationBulkUploadCheckFields() throws Exception {
		checkSuccessfulBulkLoad(phenotypeAnnotationBulkPostEndpoint,
				phenotypeAnnotationTestFilePath + "AF_03_all_fields_primary_gene_annotation.json");

		RestAssured.given().when().header("Content-Type", "application/json").body("{}")
				.post(genePhenotypeAnnotationFindEndpoint).then().statusCode(200).body("totalResults", is(1))
				.body("results", hasSize(1)).body("results[0].phenotypeAnnotationSubject.modEntityId", is(gene))
				.body("results[0].phenotypeAnnotationObject", is(phenotypeStatement))
				.body("results[0].relation.name", is("has_phenotype"))
				.body("results[0].dateCreated", is("2024-01-17T15:26:56Z"))
				.body("results[0].conditionRelations", hasSize(1))
				.body("results[0].conditionRelations[0].internal", is(false))
				.body("results[0].conditionRelations[0].obsolete", is(false))
				.body("results[0].conditionRelations[0].conditionRelationType.name", is(conditionRelationType))
				.body("results[0].conditionRelations[0].conditions", hasSize(1))
				.body("results[0].conditionRelations[0].conditions[0].internal", is(false))
				.body("results[0].conditionRelations[0].conditions[0].obsolete", is(false))
				.body("results[0].conditionRelations[0].conditions[0].conditionClass.curie", is(zecoTerm))
				.body("results[0].conditionRelations[0].conditions[0].conditionId.curie", is(expCondTerm))
				.body("results[0].conditionRelations[0].conditions[0].conditionQuantity", is("Some amount"))
				.body("results[0].conditionRelations[0].conditions[0].conditionAnatomy.curie", is(anatomyTerm))
				.body("results[0].conditionRelations[0].conditions[0].conditionGeneOntology.curie", is(goTerm))
				.body("results[0].conditionRelations[0].conditions[0].conditionTaxon.curie", is("NCBITaxon:6239"))
				.body("results[0].conditionRelations[0].conditions[0].conditionChemical.curie", is(chemicalTerm))
				.body("results[0].conditionRelations[0].conditions[0].conditionSummary", is("condition summary test"))
				.body("results[0].singleReference.curie", is(reference))
				.body("results[0].crossReference.displayName", is("PMID:25920554"))
				.body("results[0].phenotypeTerms", hasSize(1))
				.body("results[0].phenotypeTerms[0].curie", is(mpTerm));
	}

	@Test
	@Order(4)
	public void agmPhenotypeAnnotationAddSecondaryAnnotationFields() throws Exception {
		// Tests that secondary annotations are added to primary annotations as
		// asserted/inferred entities
		// as dictated by rules in BackendBulkDataProvider
		checkSuccessfulBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath
				+ "AS_01_add_secondary_allele_annotation_to_primary_agm_annotation.json", 3);

		RestAssured.given().when().get(phenotypeAnnotationGetEndpoint + agmPaId).then().statusCode(200)
				.body("entity.phenotypeAnnotationSubject.modEntityId", is(agm))
				.body("entity.inferredGene.modEntityId", is(gene))
				.body("entity.assertedAllele.modEntityId", is(allele));
	}

	@Test
	@Order(5)
	public void allelePhenotypeAnnotationAddSecondaryAnnotationFields() throws Exception {
		checkSuccessfulBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath
				+ "AS_02_add_secondary_gene_annotation_to_primary_allele_annotation.json", 2);

		RestAssured.given().when().get(phenotypeAnnotationGetEndpoint + allelePaId).then().statusCode(200)
				.body("entity.phenotypeAnnotationSubject.modEntityId", is(allele))
				.body("entity.inferredGene.modEntityId", is(gene));
	}

	@Test
	@Order(6)
	public void agmPhenotypeAnnotationClearSecondaryAnnotationFields() throws Exception {
		// Tests that secondary annotation fields are removed upon reloading of primary
		// annotation
		checkSuccessfulBulkLoad(phenotypeAnnotationBulkPostEndpoint,
				phenotypeAnnotationTestFilePath + "AF_01_all_fields_primary_agm_annotation.json");

		RestAssured.given().when().get(phenotypeAnnotationGetEndpoint + agmPaId).then().statusCode(200)
				.body("entity.phenotypeAnnotationSubject.modEntityId", is(agm))
				.body("entity", not(hasKey("inferredGene")));
	}

	@Test
	@Order(7)
	public void allelePhenotypeAnnotationClearSecondaryAnnotationFields() throws Exception {
		checkSuccessfulBulkLoad(phenotypeAnnotationBulkPostEndpoint,
				phenotypeAnnotationTestFilePath + "AF_02_all_fields_primary_allele_annotation.json");

		RestAssured.given().when().get(phenotypeAnnotationGetEndpoint + allelePaId).then().statusCode(200)
				.body("entity.phenotypeAnnotationSubject.modEntityId", is(allele))
				.body("entity", not(hasKey("inferredGene")));
	}

	@Test
	@Order(8)
	public void agmPhenotypeAnnotationInferredPrimaryAnnotation() throws Exception {
		// Tests that an AGM primary annotation is inferred correctly if secondary
		// annotation is loaded without primary annotation in database
		checkSuccessfulBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath
				+ "IF_01_infer_primary_agm_annotation_from_secondary_allele_annotation.json");

		RestAssured.given().when().header("Content-Type", "application/json")
				.body("{\"phenotypeAnnotationSubject.modEntityId\" : \"" + agm2 + "\"}")
				.post(agmPhenotypeAnnotationFindEndpoint).then().statusCode(200).body("totalResults", is(1))
				.body("results", hasSize(1)).body("results[0].phenotypeAnnotationSubject.modEntityId", is(agm2))
				.body("results[0].phenotypeAnnotationObject", is(phenotypeStatement))
				.body("results[0].relation.name", is("has_phenotype"))
				.body("results[0].dateCreated", is("2024-01-17T15:26:56Z"))
				.body("results[0].conditionRelations", hasSize(1))
				.body("results[0].conditionRelations[0].internal", is(false))
				.body("results[0].conditionRelations[0].obsolete", is(false))
				.body("results[0].conditionRelations[0].conditionRelationType.name", is(conditionRelationType))
				.body("results[0].conditionRelations[0].conditions", hasSize(1))
				.body("results[0].conditionRelations[0].conditions[0].internal", is(false))
				.body("results[0].conditionRelations[0].conditions[0].obsolete", is(false))
				.body("results[0].conditionRelations[0].conditions[0].conditionClass.curie", is(zecoTerm))
				.body("results[0].conditionRelations[0].conditions[0].conditionId.curie", is(expCondTerm))
				.body("results[0].conditionRelations[0].conditions[0].conditionQuantity", is("Some amount"))
				.body("results[0].conditionRelations[0].conditions[0].conditionAnatomy.curie", is(anatomyTerm))
				.body("results[0].conditionRelations[0].conditions[0].conditionGeneOntology.curie", is(goTerm))
				.body("results[0].conditionRelations[0].conditions[0].conditionTaxon.curie", is("NCBITaxon:6239"))
				.body("results[0].conditionRelations[0].conditions[0].conditionChemical.curie", is(chemicalTerm))
				.body("results[0].conditionRelations[0].conditions[0].conditionSummary", is("condition summary test"))
				.body("results[0].inferredGene.modEntityId", is(gene));
	}

	@Test
	@Order(9)
	public void allelePhenotypeAnnotationInferredPrimaryAnnotation() throws Exception {
		checkSuccessfulBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath
				+ "IF_02_infer_primary_allele_annotation_from_secondary_gene_annotation.json");

		RestAssured.given().when().header("Content-Type", "application/json")
				.body("{\"phenotypeAnnotationSubject.modEntityId\" : \"" + allele2 + "\"}")
				.post(allelePhenotypeAnnotationFindEndpoint).then().statusCode(200).body("totalResults", is(1))
				.body("results", hasSize(1)).body("results[0].phenotypeAnnotationSubject.modEntityId", is(allele2))
				.body("results[0].phenotypeAnnotationObject", is(phenotypeStatement))
				.body("results[0].relation.name", is("has_phenotype"))
				.body("results[0].dateCreated", is("2024-01-17T15:26:56Z"))
				.body("results[0].conditionRelations", hasSize(1))
				.body("results[0].conditionRelations[0].internal", is(false))
				.body("results[0].conditionRelations[0].obsolete", is(false))
				.body("results[0].conditionRelations[0].conditionRelationType.name", is(conditionRelationType))
				.body("results[0].conditionRelations[0].conditions", hasSize(1))
				.body("results[0].conditionRelations[0].conditions[0].internal", is(false))
				.body("results[0].conditionRelations[0].conditions[0].obsolete", is(false))
				.body("results[0].conditionRelations[0].conditions[0].conditionClass.curie", is(zecoTerm))
				.body("results[0].conditionRelations[0].conditions[0].conditionId.curie", is(expCondTerm))
				.body("results[0].conditionRelations[0].conditions[0].conditionQuantity", is("Some amount"))
				.body("results[0].conditionRelations[0].conditions[0].conditionAnatomy.curie", is(anatomyTerm))
				.body("results[0].conditionRelations[0].conditions[0].conditionGeneOntology.curie", is(goTerm))
				.body("results[0].conditionRelations[0].conditions[0].conditionTaxon.curie", is("NCBITaxon:6239"))
				.body("results[0].conditionRelations[0].conditions[0].conditionChemical.curie", is(chemicalTerm))
				.body("results[0].conditionRelations[0].conditions[0].conditionSummary", is("condition summary test"))
				.body("results[0].inferredGene.modEntityId", is(gene));
	}

	@Test
	@Order(10)
	public void phenotypeAnnotationBulkUploadMissingRequiredFields() throws Exception {
		checkFailedBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath + "MR_01_no_object_id.json");
		checkFailedBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath + "MR_02_no_date_assigned.json");
		checkFailedBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath + "MR_03_no_phenotype_statement.json");
		checkFailedBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath + "MR_04_no_evidence.json");
		checkFailedBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath + "MR_05_no_condition_relation_type.json");
		checkFailedBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath + "MR_06_no_conditions.json");
		checkFailedBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath + "MR_07_no_condition_statement.json");
		checkFailedBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath + "MR_08_no_condition_class_id.json");
	}

	@Test
	@Order(11)
	public void phenotypeAnnotationBulkUploadEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath + "ER_01_empty_object_id.json");
		checkFailedBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath + "ER_02_empty_date_assigned.json");
		checkFailedBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath + "ER_03_empty_phenotype_statement.json");
		checkFailedBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath + "ER_04_empty_evidence.json");
		checkFailedBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath + "ER_05_empty_condition_relation_type.json");
		checkFailedBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath + "ER_06_empty_conditions.json");
		checkFailedBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath + "ER_07_empty_condition_statement.json");
		checkFailedBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath + "ER_08_empty_condition_class_id.json");
	}

	@Test
	@Order(12)
	public void phenotypeAnnotationBulkUploadInvalidFields() throws Exception {
		checkFailedBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath + "IV_01_invalid_object_id.json");
		checkFailedBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath + "IV_02_invalid_primary_genetic_id.json");
		// TODO: Re-enable test if we stop ignoring invalid terms for this field
		//checkFailedBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath + "IV_03_invalid_phenotype_term_identifier.json");
		checkFailedBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath + "IV_04_invalid_evidence.json");
		checkFailedBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath + "IV_05_invalid_date_assigned.json");
		checkFailedBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath + "IV_06_invalid_condition_relation_type.json");
		checkFailedBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath + "IV_07_invalid_condition_class_id.json");
		checkFailedBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath + "IV_08_invalid_condition_id.json");
		checkFailedBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath + "IV_09_invalid_anatomical_ontology_id.json");
		checkFailedBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath + "IV_10_invalid_gene_ontology_id.json");
		checkFailedBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath + "IV_11_invalid_ncbi_taxon_id.json");
		checkFailedBulkLoad(phenotypeAnnotationBulkPostEndpoint, phenotypeAnnotationTestFilePath + "IV_12_invalid_chemical_ontology_id.json");

	}

	@Test
	@Order(13)
	public void diseaseAnnotationBulkUploadSecondaryIds() throws Exception {
		checkSuccessfulBulkLoad(phenotypeAnnotationBulkPostEndpoint,
				phenotypeAnnotationTestFilePath + "SI_01_secondary_ids.json");
	}

}
