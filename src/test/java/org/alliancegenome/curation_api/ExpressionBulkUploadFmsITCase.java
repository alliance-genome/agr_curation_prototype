package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.VocabularyTermSet;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
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
@DisplayName("606 - Expression bulk upload - FMS")
@Order(606)
public class ExpressionBulkUploadFmsITCase extends BaseITCase {

	private final String expressionBulkPostEndpoint = "/api/gene-expression-annotation/bulk/ZFIN/annotationFile";
	private final String expressionTestFilePath = "src/test/resources/bulk/fms/07_expression/";
	private final String expressionFindEndpoint = "/api/gene-expression-annotation/find?limit=100&page=0";
	private final String experimentFindEndpoint = "/api/gene-expression-experiment/find?limit=100&page=0";
	private final String taxon = "NCBITaxon:7955";
	private final String gene = "GEXPTEST:GENE001";
	private final String mmoTerm = "GEXPTEST:assay001";
	private final String referenceId = "ZFIN:PUB001";
	private final String agrReferenceId = "AGRKB:101000000668377";
	private final String publicationId = "PMID:009";
	private final String agrPublicationId = "AGRKB:101000000668376";
	private final String pipe = "|";
	private final String experimentUniqueIdExpected = gene + pipe + agrPublicationId + pipe + mmoTerm;
	private final String stageTermId = "ZFS:001";
	private final String stageUberonTermId = "UBERON:001";
	private final String anatomicalStructureTermId = "ANAT:001";
	private final String anatomicalSubstructureTermId = "ANAT:002";
	private final String cellularComponentTermId = "GOTEST:0012";
	private final String cellularComponentRibbonTermId = "GOSLIMTEST:0012";
	private final String anatomicalStructureQualifierTermId = "UBERON:002";
	private final String anatomicalSubstructureQualifierTermId = "UBERON:003";
	private final String cellularComponentQualifierTermId = "FBCV:001";
	private final String anatomicalStructureUberonTermId1 = "UBERON:004";
	private final String anatomicalStructureUberonTermId2 = "UBERON:005";
	private final String anatomicalSubstructureUberonTermId1 = "UBERON:006";
	private final String anatomicalSubstructureUberonTermId2 = "UBERON:007";
	private final String annotationUniqueIdExpected = String.join(pipe, mmoTerm, gene, agrPublicationId, stageTermId,
		"stage1", "trunk", anatomicalStructureTermId, cellularComponentTermId);

	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
			.httpClient(HttpClientConfig.httpClientConfig()
				.setParam("http.socket.timeout", 100000)
				.setParam("http.connection.timeout", 100000));
	}

	@Test
	@Order(1)
	public void expressionBulkUploadAllFields() throws Exception {
		loadRequiredEntities();
		
		HashMap<String, HashMap<String, Integer>> params = new HashMap<>();
		params.put("Annotations", createCountParams(1, 0, 1, 0));
		params.put("Experiments", createCountParams(1, 0, 1, 0));

		checkBulkLoadRecordCounts(expressionBulkPostEndpoint, expressionTestFilePath + "AF_01_all_fields.json", params);

		RestAssured.given().when()
			.header("Content-Type", "application/json")
			.body("{}")
			.post(experimentFindEndpoint)
			.then()
			.statusCode(200)
			.body("totalResults", is(1))
			.body("results", hasSize(1))
			.body("results[0].dataProvider.abbreviation", is("ZFIN"))
			.body("results[0].uniqueId", is(experimentUniqueIdExpected))
			.body("results[0].expressionAnnotations.size()", is(1))
			.body("results[0].entityAssayed.primaryExternalId", is(gene))
			.body("results[0].singleReference.crossReferences[0].referencedCurie", is(publicationId))
			.body("results[0].expressionAssayUsed.curie", is(mmoTerm))
			.body("results[0].obsolete", is(false))
			.body("results[0].internal", is(false))
			.body("results[0].expressionAnnotations[0].uniqueId", is(annotationUniqueIdExpected));

		RestAssured.given().when()
			.header("Content-Type", "application/json")
			.body("{}")
			.post(expressionFindEndpoint)
			.then()
			.statusCode(200)
			.body("totalResults", is(1))
			.body("results", hasSize(1))
			.body("results[0].dateCreated", is("2024-01-17T15:31:34Z"))
			.body("results[0].dataProvider.abbreviation", is("ZFIN"))
			.body("results[0].expressionAnnotationSubject.primaryExternalId", is(gene))
			.body("results[0].expressionAssayUsed.curie", is(mmoTerm))
			.body("results[0].whereExpressedStatement", is("trunk"))
			.body("results[0].whenExpressedStageName", is("stage1"))
			.body("results[0].singleReference.crossReferences[0].referencedCurie", is(publicationId))
			.body("results[0].relation.name", is(VocabularyConstants.GENE_EXPRESSION_RELATION_TERM))
			.body("results[0].expressionPattern.whenExpressed.developmentalStageStart.curie", is(stageTermId))
			.body("results[0].expressionPattern.whenExpressed.stageUberonSlimTerms[0].name", is(stageUberonTermId))
			.body("results[0].expressionPattern.whereExpressed.anatomicalStructure.curie", is(anatomicalStructureTermId))
			.body("results[0].expressionPattern.whereExpressed.anatomicalSubstructure.curie", is(anatomicalSubstructureTermId))
			.body("results[0].expressionPattern.whereExpressed.cellularComponentTerm.curie", is(cellularComponentTermId))
			.body("results[0].expressionPattern.whereExpressed.cellularComponentRibbonTerm.curie", is(cellularComponentRibbonTermId))
			.body("results[0].expressionPattern.whereExpressed.cellularComponentOther", is(false))
			.body("results[0].expressionPattern.whereExpressed.anatomicalStructureQualifiers[0].curie", is(anatomicalStructureQualifierTermId))
			.body("results[0].expressionPattern.whereExpressed.anatomicalSubstructureQualifiers[0].curie", is(anatomicalSubstructureQualifierTermId))
			.body("results[0].expressionPattern.whereExpressed.cellularComponentQualifiers[0].curie", is(cellularComponentQualifierTermId))
			.body("results[0].expressionPattern.whereExpressed.anatomicalStructureUberonTerms[0].curie", is(anatomicalStructureUberonTermId1))
			.body("results[0].expressionPattern.whereExpressed.anatomicalStructureUberonTerms[1].curie", is(anatomicalStructureUberonTermId2))
			.body("results[0].expressionPattern.whereExpressed.anatomicalSubstructureUberonTerms[0].curie", is(anatomicalSubstructureUberonTermId1))
			.body("results[0].expressionPattern.whereExpressed.anatomicalSubstructureUberonTerms[1].curie", is(anatomicalSubstructureUberonTermId2));
	}

	@Test
	@Order(2)
	public void expressionBulkUploadMissingRequiredFields() throws Exception {
		HashMap<String, HashMap<String, Integer>> params = new HashMap<>();
		params.put("Annotations", createCountParams(1, 1, 0, 0));

		checkBulkLoadRecordCounts(expressionBulkPostEndpoint, expressionTestFilePath + "MR_01_no_geneId.json", params);
		checkBulkLoadRecordCounts(expressionBulkPostEndpoint, expressionTestFilePath + "MR_02_no_dateAssigned.json", params);
		checkBulkLoadRecordCounts(expressionBulkPostEndpoint, expressionTestFilePath + "MR_03_no_evidence.json", params);
		checkBulkLoadRecordCounts(expressionBulkPostEndpoint, expressionTestFilePath + "MR_04_no_assay.json", params);
		checkBulkLoadRecordCounts(expressionBulkPostEndpoint, expressionTestFilePath + "MR_05_no_whenExpressed.json", params);
		checkBulkLoadRecordCounts(expressionBulkPostEndpoint, expressionTestFilePath + "MR_06_no_whereExpressed.json", params);
		checkBulkLoadRecordCounts(expressionBulkPostEndpoint, expressionTestFilePath + "MR_07_nowhenExpressedStageName.json", params);
		checkBulkLoadRecordCounts(expressionBulkPostEndpoint, expressionTestFilePath + "MR_08_nowhereExpressedStatement.json", params);
		checkBulkLoadRecordCounts(expressionBulkPostEndpoint, expressionTestFilePath + "MR_09_norCellComponentNORanatStructure.json", params);
	}

	@Test
	@Order(3)
	public void expressionBulkUploadEmptyRequiredFields() throws Exception {
		HashMap<String, HashMap<String, Integer>> params = new HashMap<>();
		params.put("Annotations", createCountParams(1, 1, 0, 0));

		checkBulkLoadRecordCounts(expressionBulkPostEndpoint, expressionTestFilePath + "ER_01_empty_geneId.json", params);
		checkBulkLoadRecordCounts(expressionBulkPostEndpoint, expressionTestFilePath + "ER_02_empty_dateAssigned.json", params);
		checkBulkLoadRecordCounts(expressionBulkPostEndpoint, expressionTestFilePath + "ER_03_empty_assay.json", params);
		checkBulkLoadRecordCounts(expressionBulkPostEndpoint, expressionTestFilePath + "ER_04_empty_whenExpressedStageName.json", params);
		checkBulkLoadRecordCounts(expressionBulkPostEndpoint, expressionTestFilePath + "ER_05_empty_whereExpressedStatement.json", params);
	}

	@Test
	@Order(4)
	public void expressionBulkUploadInvalidFields() throws Exception {
		HashMap<String, HashMap<String, Integer>> params = new HashMap<>();
		params.put("Annotations", createCountParams(1, 1, 0, 0));

		checkBulkLoadRecordCounts(expressionBulkPostEndpoint, expressionTestFilePath + "IV_01_invalid_geneId.json", params);
		checkBulkLoadRecordCounts(expressionBulkPostEndpoint, expressionTestFilePath + "IV_02_invalid_dateAssigned.json", params);
		checkBulkLoadRecordCounts(expressionBulkPostEndpoint, expressionTestFilePath + "IV_03_invalid_assay.json", params);
		checkBulkLoadRecordCounts(expressionBulkPostEndpoint, expressionTestFilePath + "IV_04_invalid_publicationId.json", params);
		checkBulkLoadRecordCounts(expressionBulkPostEndpoint, expressionTestFilePath + "IV_05_invalid_stageterm.json", params);
		checkBulkLoadRecordCounts(expressionBulkPostEndpoint, expressionTestFilePath + "IV_06_invalid_anatomical_structure.json", params);
		checkBulkLoadRecordCounts(expressionBulkPostEndpoint, expressionTestFilePath + "IV_07_invalid_anatomical_substructure.json", params);
		checkBulkLoadRecordCounts(expressionBulkPostEndpoint, expressionTestFilePath + "IV_08_invalid_cellularcomponent.json", params);
		checkBulkLoadRecordCounts(expressionBulkPostEndpoint, expressionTestFilePath + "IV_09_invalid_anatomicalstructurequalifier.json", params);
		checkBulkLoadRecordCounts(expressionBulkPostEndpoint, expressionTestFilePath + "IV_10_invalid_anatomicalsubstructurequalifier.json", params);
		checkBulkLoadRecordCounts(expressionBulkPostEndpoint, expressionTestFilePath + "IV_11_invalid_cellularcomponentqualifier.json", params);
		checkBulkLoadRecordCounts(expressionBulkPostEndpoint, expressionTestFilePath + "IV_12_invalid_anatomicalstructureuberonslimterms.json", params);
		checkBulkLoadRecordCounts(expressionBulkPostEndpoint, expressionTestFilePath + "IV_13_invalid_anatomicalsubstructureuberonslimterms.json", params);
	}

	private void loadRequiredEntities() throws Exception {
		Organization dataProvider = getOrganization("ZFIN");
		Vocabulary vocabulary1 = getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY);
		VocabularyTerm symbolTerm = getVocabularyTerm(vocabulary1, "nomenclature_symbol");
		createGene(gene, taxon, symbolTerm, false, dataProvider);
		createMmoTerm(mmoTerm, "assay001");
		ResourceDescriptor rd1 = createResourceDescriptor("ZFIN");
		createResourceDescriptorPage("homepage", "https://zfin.org/", rd1);
		createResourceDescriptorPage("reference", "https://zfin.org/[%s]", rd1);
		createReference(agrPublicationId, publicationId);
		createReference(agrReferenceId, referenceId);
		Vocabulary vocabulary2 = createVocabulary(VocabularyConstants.GENE_EXPRESSION_VOCABULARY, false);
		createVocabularyTerm(vocabulary2, VocabularyConstants.GENE_EXPRESSION_RELATION_TERM, false);
		Vocabulary stageUberonTermVocabulary = getVocabulary(VocabularyConstants.STAGE_UBERON_SLIM_TERMS);
		VocabularyTermSet anatatomicalStructureQualifierTermset = getVocabularyTermSet(VocabularyConstants.ANATOMICAL_STRUCTURE_QUALIFIER);
		VocabularyTermSet anatatomicalSubstructureQualifierTermset = getVocabularyTermSet(VocabularyConstants.ANATOMICAL_SUBSTRUCTURE_QUALIFIER);
		VocabularyTermSet cellularComponentQualifierTermset = getVocabularyTermSet(VocabularyConstants.CELLULAR_COMPONENT_QUALIFIER);
		createStageTerm(stageTermId, "StageTermTest");
		createVocabularyTerm(stageUberonTermVocabulary, stageUberonTermId, false);
		createAnatomicalTerm(anatomicalStructureTermId, "AnatomicalStructureTermTest");
		createAnatomicalTerm(anatomicalSubstructureTermId, "AnatomicalSubStructureTermTest");
		createOntologyTerm(anatomicalStructureQualifierTermId, "anatomicalSubstructureQualifierTermId", false);
		createOntologyTerm(anatomicalSubstructureQualifierTermId, "anatomicalSubstructureQualifierTermId", false);
		createOntologyTerm(cellularComponentQualifierTermId, "anatomicalSubstructureQualifierTermId", false);
		createVocabularyTerm(anatatomicalStructureQualifierTermset, anatomicalStructureQualifierTermId, false);
		createVocabularyTerm(anatatomicalSubstructureQualifierTermset, anatomicalSubstructureQualifierTermId, false);
		createVocabularyTerm(cellularComponentQualifierTermset, cellularComponentQualifierTermId, false);
		List<String> subsets = new ArrayList<String>();
		subsets.add("goslim_agr");
		GOTerm isaAncestor = createGoTerm(cellularComponentRibbonTermId, "CellularComponentRibbonTermTest", false, subsets);
		createGoTerm(cellularComponentTermId, "CellularComponentTermTest", false, isaAncestor);
		createUberonTerm(anatomicalStructureUberonTermId1, "UberonTermTest1");
		createUberonTerm(anatomicalStructureUberonTermId2, "UberonTermTest2");
		createUberonTerm(anatomicalSubstructureUberonTermId1, "UberonTermTest3");
		createUberonTerm(anatomicalSubstructureUberonTermId2, "UberonTermTest4");
	}
}
