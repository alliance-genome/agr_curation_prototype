package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
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
@DisplayName("604 - Gene Interaction bulk upload - FMS")
@Order(604)
public class GeneInteractionBulkUploadFmsITCase extends BaseITCase {

	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 100000)
					.setParam("http.connection.timeout", 100000));
	}

	private final String geneMolecularInteractionBulkPostEndpoint = "/api/gene-molecular-interaction/bulk/interactionFile";
	private final String geneGeneticInteractionBulkPostEndpoint = "/api/gene-genetic-interaction/bulk/interactionFile";
	private final String geneInteractionTestFilePath = "src/test/resources/bulk/fms/04_gene_interaction/";
	private final String geneMolecularInteractionGetEndpoint = "/api/gene-molecular-interaction/findBy/";
	private final String geneGeneticInteractionGetEndpoint = "/api/gene-genetic-interaction/findBy/";
	private final String geneMolecularInteractionId = "WB:WBInteraction0001";
	private final String geneGeneticInteractionId = "WB:WBInteraction0002";
	private final String geneInteractionXrefLookupId = "WB:WBInteraction0003";
	private final String geneInteractionXrefLookupId2 = "WB:WBInteraction0004";
	private final String gene1 = "WB:GITestGene0001";
	private final String gene2 = "WB:GITestGene0002";
	private final String gene3 = "WB:GITestGene0003";
	private final String gene3xref = "NCBI_Gene:EZL1";
	private final String gene4 = "WB:GITestGene0004";
	private final String gene4xref = "NCBI_Gene:EZL2";
	private final String reference = "AGRKB:000000002";
	private final String reference2 = "AGRKB:000000021";
	private final String miTerm1 = "MI:Test0001";
	private final String miTerm2 = "MI:Test0002";
	private final String miTerm3 = "MI:Test0003";
	private final String miTerm4 = "MI:Test0004";
	private final String miTerm5 = "MI:Test0005";
	private final String miTerm6 = "MI:Test0006";
	private final String miTerm7 = "MI:Test0007";
	private final String defaultAggregationDbTerm = "MI:0670";
	private final String allele1 = "WB:WBVar11111111";
	private final String allele2 = "WB:WBVar22222222";
	private final String wbPhenotypeTerm = "WBPhenotype:0001";
	
	private void loadRequiredEntities() throws Exception {
		Vocabulary nameTypeVocabulary = getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY);
		VocabularyTerm symbolTerm = getVocabularyTerm(nameTypeVocabulary, "nomenclature_symbol");
		DataProvider dataProvider = createDataProvider("WB", false);
		DataProvider dataProvider2 = createDataProvider("RGD", false);
		createGenes(List.of(gene1, gene2), "NCBITaxon:6239", symbolTerm, false, dataProvider);
		createGeneWithXref(gene3, "NCBITaxon:6239", symbolTerm, false, dataProvider, gene3xref);
		createGeneWithXref(gene4, "NCBITaxon:9606", symbolTerm, false, dataProvider2, gene4xref);
		createAllele(allele1, "GGITestVar1", "NCBITaxon:6239", symbolTerm, false, dataProvider);
		createAllele(allele2, "GGITestVar2", "NCBITaxon:6239", symbolTerm, false, dataProvider);
		createMiTerm(miTerm1, "Test MITerm 1");
		createMiTerm(miTerm2, "Test MITerm 2");
		createMiTerm(miTerm3, "Test MITerm 3");
		createMiTerm(miTerm4, "Test MITerm 4");
		createMiTerm(miTerm5, "Test MITerm 5");
		createMiTerm(miTerm6, "Test MITerm 6");
		createMiTerm(miTerm7, "Test MITerm 7");
		createMiTerm(defaultAggregationDbTerm, "IMEX");
		createWbPhenotypeTerm(wbPhenotypeTerm, "Test WBPhenotype term");
		ResourceDescriptor rd = createResourceDescriptor("WB");
		createResourceDescriptorPage("gene/interactions", "http://test.org", rd);
	}
	
	@Test
	@Order(1)
	public void geneMolecularInteractionBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();
		
		checkSuccessfulBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "AF_01_all_fields_gene_molecular_interaction.json");
		
		RestAssured.given().
			when().
			get(geneMolecularInteractionGetEndpoint + geneMolecularInteractionId).
			then().
			statusCode(200).
			body("entity.interactionId", is(geneMolecularInteractionId)).
			body("entity.uniqueId", is("WB:WBInteraction0001|WB:GITestGene0001|physically_interacts_with|WB:GITestGene0002|AGRKB:000000002|MI:Test0002|MI:Test0003|MI:Test0004|MI:Test0005|MI:Test0006|MI:Test0007")).
			body("entity.geneAssociationSubject.modEntityId", is(gene1)).
			body("entity.geneGeneAssociationObject.modEntityId", is(gene2)).
			body("entity.relation.name", is(VocabularyConstants.GENE_MOLECULAR_INTERACTION_RELATION_TERM)).
			body("entity.dateCreated", is("2024-01-17T00:00:00Z")).
			body("entity.dateUpdated", is("2024-01-18T00:00:00Z")).
			body("entity.evidence[0].curie", is(reference)).
			body("entity.interactionSource.curie", is(miTerm1)).
			body("entity.interactionType.curie", is(miTerm2)).
			body("entity.interactorARole.curie", is(miTerm3)).
			body("entity.interactorBRole.curie", is(miTerm4)).
			body("entity.interactorAType.curie", is(miTerm5)).
			body("entity.interactorBType.curie", is(miTerm6)).
			body("entity.aggregationDatabase.curie", is(defaultAggregationDbTerm)).
			body("entity.detectionMethod.curie", is(miTerm7)).
			body("entity.crossReferences[0].referencedCurie", is(geneMolecularInteractionId));
	}
	
	@Test
	@Order(2)
	public void geneGeneticInteractionBulkUploadCheckFields() throws Exception {
		
		checkSuccessfulBulkLoad(geneGeneticInteractionBulkPostEndpoint, geneInteractionTestFilePath + "AF_02_all_fields_gene_genetic_interaction.json");
		
		RestAssured.given().
			when().
			get(geneGeneticInteractionGetEndpoint + geneGeneticInteractionId).
			then().
			statusCode(200).
			body("entity.interactionId", is(geneGeneticInteractionId)).
			body("entity.uniqueId", is("WB:WBInteraction0002|WB:GITestGene0001|genetically_interacts_with|WB:GITestGene0002|AGRKB:000000002|MI:Test0002|MI:Test0003|MI:Test0004|MI:Test0005|MI:Test0006|MI:Test0001|WB:WBVar11111111|WB:WBVar22222222|Test WBPhenotype term")).
			body("entity.geneAssociationSubject.modEntityId", is(gene1)).
			body("entity.geneGeneAssociationObject.modEntityId", is(gene2)).
			body("entity.relation.name", is(VocabularyConstants.GENE_GENETIC_INTERACTION_RELATION_TERM)).
			body("entity.dateCreated", is("2024-01-17T00:00:00Z")).
			body("entity.dateUpdated", is("2024-01-18T00:00:00Z")).
			body("entity.evidence[0].curie", is(reference)).
			body("entity.interactionSource.curie", is(miTerm1)).
			body("entity.interactionType.curie", is(miTerm2)).
			body("entity.interactorARole.curie", is(miTerm3)).
			body("entity.interactorBRole.curie", is(miTerm4)).
			body("entity.interactorAType.curie", is(miTerm5)).
			body("entity.interactorBType.curie", is(miTerm6)).
			body("entity.interactorAGeneticPerturbation.modEntityId", is(allele1)).
			body("entity.interactorBGeneticPerturbation.modEntityId", is(allele2)).
			body("entity.phenotypesOrTraits[0]", is("Test WBPhenotype term")).
			body("entity.crossReferences[0].referencedCurie", is(geneGeneticInteractionId));
	}
	
	@Test
	@Order(3)
	public void geneMolecularInteractionBulkUploadUpdateCheckFields() throws Exception {
		
		checkSuccessfulBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "UD_01_update_all_fields_gene_molecular_interaction.json");
		
		RestAssured.given().
			when().
			get(geneMolecularInteractionGetEndpoint + geneMolecularInteractionId).
			then().
			statusCode(200).
			body("entity.interactionId", is(geneMolecularInteractionId)).
			body("entity.uniqueId", is("WB:WBInteraction0001|WB:GITestGene0002|physically_interacts_with|WB:GITestGene0001|AGRKB:000000021|MI:Test0003|MI:Test0004|MI:Test0005|MI:Test0006|MI:Test0007|MI:Test0001")).
			body("entity.geneAssociationSubject.modEntityId", is(gene2)).
			body("entity.geneGeneAssociationObject.modEntityId", is(gene1)).
			body("entity.relation.name", is(VocabularyConstants.GENE_MOLECULAR_INTERACTION_RELATION_TERM)).
			body("entity.dateCreated", is("2024-01-18T00:00:00Z")).
			body("entity.dateUpdated", is("2024-01-19T00:00:00Z")).
			body("entity.evidence[0].curie", is(reference2)).
			body("entity.interactionSource.curie", is(miTerm2)).
			body("entity.interactionType.curie", is(miTerm3)).
			body("entity.interactorARole.curie", is(miTerm4)).
			body("entity.interactorBRole.curie", is(miTerm5)).
			body("entity.interactorAType.curie", is(miTerm6)).
			body("entity.interactorBType.curie", is(miTerm7)).
			body("entity.aggregationDatabase.curie", is(defaultAggregationDbTerm)).
			body("entity.detectionMethod.curie", is(miTerm1)).
			body("entity.crossReferences[0].referencedCurie", is(geneMolecularInteractionId));
	}
	
	@Test
	@Order(4)
	public void geneGeneticInteractionBulkUploadUpdateCheckFields() throws Exception {
		
		checkSuccessfulBulkLoad(geneGeneticInteractionBulkPostEndpoint, geneInteractionTestFilePath + "UD_02_update_all_fields_gene_genetic_interaction.json");
		
		RestAssured.given().
			when().
			get(geneGeneticInteractionGetEndpoint + geneGeneticInteractionId).
			then().
			statusCode(200).
			body("entity.interactionId", is(geneGeneticInteractionId)).
			body("entity.uniqueId", is("WB:WBInteraction0002|WB:GITestGene0002|genetically_interacts_with|WB:GITestGene0001|AGRKB:000000021|MI:Test0003|MI:Test0004|MI:Test0005|MI:Test0006|MI:Test0007|MI:Test0002|WB:WBVar22222222|WB:WBVar11111111|vegetative growth, complete rescue")).
			body("entity.geneAssociationSubject.modEntityId", is(gene2)).
			body("entity.geneGeneAssociationObject.modEntityId", is(gene1)).
			body("entity.relation.name", is(VocabularyConstants.GENE_GENETIC_INTERACTION_RELATION_TERM)).
			body("entity.dateCreated", is("2024-01-18T00:00:00Z")).
			body("entity.dateUpdated", is("2024-01-19T00:00:00Z")).
			body("entity.evidence[0].curie", is(reference2)).
			body("entity.interactionSource.curie", is(miTerm2)).
			body("entity.interactionType.curie", is(miTerm3)).
			body("entity.interactorARole.curie", is(miTerm4)).
			body("entity.interactorBRole.curie", is(miTerm5)).
			body("entity.interactorAType.curie", is(miTerm6)).
			body("entity.interactorBType.curie", is(miTerm7)).
			body("entity.interactorAGeneticPerturbation.modEntityId", is(allele2)).
			body("entity.interactorBGeneticPerturbation.modEntityId", is(allele1)).
			body("entity.phenotypesOrTraits[0]", is("vegetative growth, complete rescue")).
			body("entity.crossReferences[0].referencedCurie", is(geneGeneticInteractionId));
	}
	
	@Test
	@Order(5)
	public void geneInteractionBulkUploadMissingRequiredFields() throws Exception {
		checkFailedBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "MR_01_no_interactor_a_identifier.json");
		checkFailedBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "MR_02_no_interactor_b_identifier.json");
		checkFailedBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "MR_03_no_interaction_type.json");
		checkFailedBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "MR_04_no_source_database_ids.json");
		checkFailedBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "MR_05_no_interactor_a_type.json");
		checkFailedBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "MR_06_no_interactor_b_type.json");
		checkFailedBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "MR_07_no_interactor_a_taxon.json");
		checkFailedBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "MR_08_no_interactor_b_taxon.json");
	}
	
	@Test
	@Order(6)
	public void geneInteractionBulkUploadEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "ER_01_empty_interactor_a_identifier.json");
		checkFailedBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "ER_02_empty_interactor_b_identifier.json");
		checkFailedBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "ER_03_empty_interaction_type.json");
		checkFailedBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "ER_04_empty_source_database_ids.json");
		checkFailedBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "ER_05_empty_interactor_a_type.json");
		checkFailedBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "ER_06_empty_interactor_b_type.json");
		checkFailedBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "ER_07_empty_interactor_a_taxon.json");
		checkFailedBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "ER_08_empty_interactor_b_taxon.json");
	}
	
	@Test
	@Order(7)
	public void geneMolecularInteractionBulkUploadUpdateMissingNonRequiredFields() throws Exception {
		
		checkSuccessfulBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "AF_01_all_fields_gene_molecular_interaction.json");
		checkSuccessfulBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "UM_01_update_no_non_required_fields_gene_molecular_interaction.json");
		
		RestAssured.given().
			when().
			get(geneMolecularInteractionGetEndpoint + geneMolecularInteractionId).
			then().
			statusCode(200).
			body("entity.interactionId", is(geneMolecularInteractionId)).
			body("entity", not(hasKey("dateCreated"))).
			body("entity", not(hasKey("dateUpdated"))).
			body("entity", not(hasKey("evidence"))).
			body("entity", not(hasKey("interactorARole"))).
			body("entity", not(hasKey("interactorBRole"))).
			body("entity", not(hasKey("detectionMethod")));
	}
	
	@Test
	@Order(8)
	public void geneMolecularInteractionBulkUploadUpdateEmptyNonRequiredFields() throws Exception {
		
		checkSuccessfulBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "AF_01_all_fields_gene_molecular_interaction.json");
		checkSuccessfulBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "UE_01_update_empty_non_required_fields_gene_molecular_interaction.json");
		
		RestAssured.given().
			when().
			get(geneMolecularInteractionGetEndpoint + geneMolecularInteractionId).
			then().
			statusCode(200).
			body("entity.interactionId", is(geneMolecularInteractionId)).
			body("entity", not(hasKey("dateCreated"))).
			body("entity", not(hasKey("dateUpdated"))).
			body("entity", not(hasKey("evidence"))).
			body("entity", not(hasKey("interactorARole"))).
			body("entity", not(hasKey("interactorBRole"))).
			body("entity", not(hasKey("detectionMethod")));
	}
	
	@Test
	@Order(9)
	public void geneInteractionBulkUploadInvalidFields() throws Exception {
		checkFailedBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "IV_01_invalid_interactor_a_identifier.json");
		checkFailedBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "IV_02_invalid_interactor_b_identifier.json");
		checkFailedBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "IV_03_invalid_publication_ids.json");
		checkFailedBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "IV_04_invalid_source_database_ids.json");
		checkFailedBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "IV_05_invalid_interaction_types.json");
		checkFailedBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "IV_06_invalid_experimental_role_a.json");
		checkFailedBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "IV_07_invalid_experimental_role_b.json");
		checkFailedBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "IV_08_invalid_interactor_a_type.json");
		checkFailedBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "IV_09_invalid_interactor_b_type.json");
		checkFailedBulkLoad(geneGeneticInteractionBulkPostEndpoint, geneInteractionTestFilePath + "IV_10_invalid_interactor_a_annotation_string.json");
		checkFailedBulkLoad(geneGeneticInteractionBulkPostEndpoint, geneInteractionTestFilePath + "IV_11_invalid_interactor_b_annotation_string.json");
		checkFailedBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "IV_12_invalid_interactor_a_taxon.json");
		checkFailedBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "IV_13_invalid_interactor_b_taxon.json");
	}
	
	@Test
	@Order(10)
	public void geneInteractionBulkUploadXrefLookup() throws Exception {
		checkSuccessfulBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "XR_01_cross_reference_lookup.json");
		
		RestAssured.given().
			when().
			get(geneMolecularInteractionGetEndpoint + geneInteractionXrefLookupId).
			then().
			statusCode(200).
			body("entity.geneAssociationSubject.modEntityId", is(gene3));
			
	}
	
	@Test
	@Order(11)
	public void geneInteractionHumanGeneBulkUploadXrefLookup() throws Exception {
		checkSuccessfulBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "XR_02_RGD_human_gene_cross_reference_lookup.json");
		
		RestAssured.given().
			when().
			get(geneMolecularInteractionGetEndpoint + geneInteractionXrefLookupId2).
			then().
			statusCode(200).
			body("entity.geneAssociationSubject.modEntityId", is(gene4));
			
	}
	
	@Test
	@Order(12)
	public void geneInteractionHandleDuplicateReferences() throws Exception {
		checkSuccessfulBulkLoad(geneMolecularInteractionBulkPostEndpoint, geneInteractionTestFilePath + "DR_01_duplicate_references.json");
		
		RestAssured.given().
			when().
			get(geneMolecularInteractionGetEndpoint + geneMolecularInteractionId).
			then().
			statusCode(200).
			body("entity.evidence", hasSize(1));
	}
}
