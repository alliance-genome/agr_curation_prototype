package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;

@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("609 - Sequence Targeting Reagent Gene Associations bulk upload")
@Order(609)
public class SequenceTargetingReagentGeneAssociationBulkUploadFmsITCase extends BaseITCase {
	
	private Gene gene;
	private SequenceTargetingReagent sqtr;
	private VocabularyTerm relation;
	private String relationName = "targets";
	private String geneCurie = "GENETEST:Gene0001";
	private String sqtrModEntityId = "ZFIN:ZDB-TALEN-180503-1";
	
	private final String sqtrGeneAssociationGetEndpoint = "/api/sqtrgeneassociation/findBy";
	private final String sqtrGeneAssociationTestFilePath = "src/test/resources/bulk/fms/SA01_sequencetargetingreagent_gene_association/";
	private final String geneGetEndpoint = "/api/gene/";
	private final String sqtrGetEndpoint = "/api/sqtr/";

	private final String sqtrBulkPostEndpoint = "/api/sqtr/bulk/ZFIN/sqtrfile";

	private void loadRequiredEntities() throws Exception {
		Vocabulary noteTypeVocab = getVocabulary("construct_relation");
		relation = getVocabularyTerm(noteTypeVocab, relationName);
		gene = getGene(geneCurie);
		sqtr = getSequenceTargetingReagent(sqtrModEntityId);
	}

	@Test
	@Order(1)
	public void sqtrGeneAssociationBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();
		
		checkSuccessfulBulkLoad(sqtrBulkPostEndpoint, sqtrGeneAssociationTestFilePath + "AF_01_all_fields.json", 2);
	
		RestAssured.given().
			when().
			get(sqtrGeneAssociationGetEndpoint + "?sqtrId=" + sqtr.getId() + "&relationName=" + relationName + "&geneId=" + gene.getId()).
			then().
			statusCode(200).
			body("entity.relation.name", is(relationName)).
			body("entity.sequenceTargetingReagentGeneAssociationObject.modEntityId", is(geneCurie)).
			body("entity.sequenceTargetingReagentAssociationSubject.modEntityId", is(sqtrModEntityId));
		
		RestAssured.given().
			when().
			get(sqtrGetEndpoint + sqtrModEntityId).
			then().
			statusCode(200).
			body("entity.sequenceTargetingReagentGeneAssociations", hasSize(1)).
			body("entity.sequenceTargetingReagentGeneAssociations[0].relation.name", is(relationName)).
			body("entity.sequenceTargetingReagentGeneAssociations[0].sequenceTargetingReagentGeneAssociationObject.modEntityId", is(geneCurie)).
			body("entity.sequenceTargetingReagentGeneAssociations[0].sequenceTargetingReagentAssociationSubject", not(hasKey("sequenceTargetingReagentGeneAssociations")));
		
		RestAssured.given().
			when().
			get(geneGetEndpoint + geneCurie).
			then().
			statusCode(200).
			body("entity.sequenceTargetingReagentGeneAssociations", hasSize(1)).
			body("entity.sequenceTargetingReagentGeneAssociations[0].relation.name", is(relationName)).
			body("entity.sequenceTargetingReagentGeneAssociations[0].sequenceTargetingReagentGeneAssociationObject.modEntityId", is(geneCurie)).
			body("entity.sequenceTargetingReagentGeneAssociations[0].sequenceTargetingReagentGeneAssociationObject", not(hasKey("sequenceTargetingReagentGeneAssociations")));
	}

	@Test
	@Order(2)
	public void sqtrGeneAssociationBulkUploadInvalidGenes() throws Exception {
		checkBulkLoadRecordCounts(sqtrBulkPostEndpoint, sqtrGeneAssociationTestFilePath + "IV_01_invalid_gene_ids.json", "Records", 2, 1, 1, 0);
	}
	
	@Test
	@Order(3)
	public void sqtrGeneAssociationBulkUploadMissingGenes() throws Exception {
		checkBulkLoadRecordCounts(sqtrBulkPostEndpoint, sqtrGeneAssociationTestFilePath + "UE_01_update_empty_gene_ids.json", "Records", 2, 0, 2, 0);
	}
	

}
