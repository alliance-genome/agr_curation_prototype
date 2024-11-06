package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.constructAssociations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
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
@DisplayName("402 - ConstructGenomicEntityAssociationITCase")
@Order(402)
public class ConstructGenomicEntityAssociationITCase extends BaseITCase {

	private Construct construct;
	private Construct construct2;
	private Construct obsoleteConstruct;
	private Gene gene;
	private Gene gene2;
	private Gene obsoleteGene;
	private VocabularyTerm relation;
	private VocabularyTerm relation2;
	private VocabularyTerm obsoleteRelation;
	private Reference reference;
	private Reference reference2;
	private Reference obsoleteReference;
	private VocabularyTerm noteType;
	private VocabularyTerm noteType2;
	private VocabularyTerm obsoleteNoteType;
	private Note note;
	private Note note2;
	
	private final String constructGenomicEntityAssociationGetEndpoint = "/api/constructgenomicentityassociation/findBy";
	private final String constructGetEndpoint = "/api/construct/";
	private final String geneGetEndpoint = "/api/gene/";
	
	private void loadRequiredEntities() {
		Vocabulary nameTypeVocabulary = getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY);
		VocabularyTerm symbolNameType = getVocabularyTerm(nameTypeVocabulary, "nomenclature_symbol");
		construct = createConstruct("CGEA:Construct0001", false, symbolNameType);
		construct2 = createConstruct("CGEA:Construct0002", false, symbolNameType);
		obsoleteConstruct = createConstruct("CGEA:Construct0003", true, symbolNameType);
		gene = createGene("CGEA:Gene0001", "NCBITaxon:6239", symbolNameType, false);
		gene2 = createGene("CGEA:Gene0002", "NCBITaxon:6239", symbolNameType, false);
		obsoleteGene = createGene("CGEA:Gene0003", "NCBITaxon:6239", symbolNameType, true);
		Vocabulary relationVocabulary = getVocabulary(VocabularyConstants.CONSTRUCT_RELATION_VOCABULARY);
		relation = getVocabularyTerm(relationVocabulary, "is_regulated_by");
		relation2 = getVocabularyTerm(relationVocabulary, "targets");
		obsoleteRelation = getVocabularyTerm(relationVocabulary, "obsolete_relation");
		reference = createReference("AGRKB:CGEA0001", false);
		reference2 = createReference("AGRKB:CGEA0002", false);
		obsoleteReference = createReference("AGRKB:CGEA0003", true);
		Vocabulary noteTypeVocabulary = getVocabulary(VocabularyConstants.NOTE_TYPE_VOCABULARY);
		noteType = getVocabularyTerm(noteTypeVocabulary, "test_construct_component_note");
		noteType2 = getVocabularyTerm(noteTypeVocabulary, "test_construct_component_summary");
		obsoleteNoteType = getVocabularyTerm(noteTypeVocabulary, "obsolete_type");
		note = createNote(noteType, "CGEA Test Note", false, reference);
		note2 = createNote(noteType2, "CGEA Test Note 2", false, reference2);
	}
	
	@Test
	@Order(1)
	public void createValidConstructGenomicEntityAssociation() {
		loadRequiredEntities();
		
		ConstructGenomicEntityAssociation association = new ConstructGenomicEntityAssociation();
		association.setConstructAssociationSubject(construct);
		association.setConstructGenomicEntityAssociationObject(gene);
		association.setRelation(relation);
		association.setInternal(true);
		association.setObsolete(true);
		association.setRelatedNotes(List.of(note));
		association.setEvidence(List.of(reference));
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			post("/api/constructgenomicentityassociation").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get(constructGenomicEntityAssociationGetEndpoint + "?constructId=" + construct.getId() + "&relationName=" + relation.getName() + "&genomicEntityId=" + gene.getId()).
			then().
			statusCode(200).
			body("entity.relation.name", is(relation.getName())).
			body("entity.constructGenomicEntityAssociationObject.modEntityId", is(gene.getModEntityId())).
			body("entity.constructAssociationSubject.modEntityId", is(construct.getModEntityId())).
			body("entity.evidence", hasSize(1)).
			body("entity.evidence[0].curie", is(reference.getCurie())).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.relatedNotes[0].internal", is(false)).
			body("entity.relatedNotes[0].obsolete", is(false)).
			body("entity.relatedNotes[0].updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.relatedNotes[0].createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.relatedNotes[0].freeText", is(note.getFreeText())).
			body("entity.relatedNotes[0].noteType.name", is(noteType.getName())).
			body("entity.relatedNotes[0].references[0].curie", is(reference.getCurie()));

		RestAssured.given().
			when().
			get(constructGetEndpoint + construct.getModEntityId()).
			then().
			statusCode(200).
			body("entity.constructGenomicEntityAssociations", hasSize(1)).
			body("entity.constructGenomicEntityAssociations[0].relation.name", is(relation.getName())).
			body("entity.constructGenomicEntityAssociations[0].constructGenomicEntityAssociationObject.modEntityId", is(gene.getModEntityId())).
			body("entity.constructGenomicEntityAssociations[0].constructAssociationSubject", not(hasKey("constructGeneAssociations")));
		
		RestAssured.given().
			when().
			get(geneGetEndpoint + gene.getModEntityId()).
			then().
			statusCode(200).
			body("entity.constructGenomicEntityAssociations", hasSize(1)).
			body("entity.constructGenomicEntityAssociations[0].relation.name", is(relation.getName())).
			body("entity.constructGenomicEntityAssociations[0].constructGenomicEntityAssociationObject.modEntityId", is(gene.getModEntityId())).
			body("entity.constructGenomicEntityAssociations[0].constructAssociationSubject", not(hasKey("constructGeneAssociations")));
	}
	
	@Test
	@Order(2)
	public void editConstructGenomicEntityAssociation() {
		ConstructGenomicEntityAssociation association = getConstructGenomicEntityAssociation(construct.getId(), relation.getName(), gene.getId());
		association.setConstructAssociationSubject(construct2);
		association.setRelation(relation2);
		association.setConstructGenomicEntityAssociationObject(gene2);
		association.setInternal(false);
		association.setObsolete(false);
		association.setEvidence(List.of(reference2));
		association.setRelatedNotes(List.of(note2));
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			put("/api/constructgenomicentityassociation").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get(constructGenomicEntityAssociationGetEndpoint + "?constructId=" + construct2.getId() + "&relationName=" + relation2.getName() + "&genomicEntityId=" + gene2.getId()).
			then().
			statusCode(200).
			body("entity.relation.name", is(relation2.getName())).
			body("entity.constructGenomicEntityAssociationObject.curie", is(gene2.getCurie())).
			body("entity.constructAssociationSubject.modEntityId", is(construct2.getModEntityId())).
			body("entity.evidence", hasSize(1)).
			body("entity.evidence[0].curie", is(reference2.getCurie())).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.relatedNotes[0].internal", is(false)).
			body("entity.relatedNotes[0].obsolete", is(false)).
			body("entity.relatedNotes[0].updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.relatedNotes[0].createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.relatedNotes[0].freeText", is(note2.getFreeText())).
			body("entity.relatedNotes[0].noteType.name", is(noteType2.getName())).
			body("entity.relatedNotes[0].references[0].curie", is(reference2.getCurie()));
	}
	
	@Test
	@Order(3)
	public void createConstructGenomicEntityAssociationWithMissingRequiredFieldsLevel1() {
		ConstructGenomicEntityAssociation association = new ConstructGenomicEntityAssociation();
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			post("/api/constructgenomicentityassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(3))).
			body("errorMessages.constructAssociationSubject", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.relation", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.constructGenomicEntityAssociationObject", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(4)
	public void createConstructGenomicEntityAssociationWithMissingRequiredFieldsLevel2() {
		ConstructGenomicEntityAssociation association = new ConstructGenomicEntityAssociation();
		association.setConstructAssociationSubject(construct2);
		association.setRelation(relation);
		association.setConstructGenomicEntityAssociationObject(gene);
		
		Note emptyNote = new Note();
		association.setRelatedNotes(List.of(emptyNote));
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			post("/api/constructgenomicentityassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"freeText - " + ValidationConstants.REQUIRED_MESSAGE,
					"noteType - " + ValidationConstants.REQUIRED_MESSAGE))));
	}
	
	@Test
	@Order(5)
	public void createConstructGenomicEntityAssociationWithEmptyRequiredFieldsLevel2() {
		ConstructGenomicEntityAssociation association = new ConstructGenomicEntityAssociation();
		association.setConstructAssociationSubject(construct2);
		association.setRelation(relation);
		association.setConstructGenomicEntityAssociationObject(gene);
		
		Note invalidNote = new Note();
		invalidNote.setNoteType(noteType);
		association.setRelatedNotes(List.of(invalidNote));
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			post("/api/constructgenomicentityassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.relatedNotes", is("freeText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(6)
	public void editConstructGenomicEntityAssociationWithMissingRequiredFieldsLevel1() {
		ConstructGenomicEntityAssociation association = getConstructGenomicEntityAssociation(construct2.getId(), relation2.getName(), gene2.getId());
		association.setConstructAssociationSubject(null);
		association.setRelation(null);
		association.setConstructGenomicEntityAssociationObject(null);
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			put("/api/constructgenomicentityassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(3))).
			body("errorMessages.constructAssociationSubject", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.relation", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.constructGenomicEntityAssociationObject", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(7)
	public void editConstructGenomicEntityAssociationWithMissingRequiredFieldsLevel2() {
		ConstructGenomicEntityAssociation association = getConstructGenomicEntityAssociation(construct2.getId(), relation2.getName(), gene2.getId());
	
		Note invalidNote = association.getRelatedNotes().get(0);
		invalidNote.setFreeText(null);
		invalidNote.setNoteType(null);
		association.setRelatedNotes(List.of(invalidNote));
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			put("/api/constructgenomicentityassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"freeText - " + ValidationConstants.REQUIRED_MESSAGE,
					"noteType - " + ValidationConstants.REQUIRED_MESSAGE))));
	}
	
	@Test
	@Order(8)
	public void editConstructGenomicEntityAssociationWithEmptyRequiredFieldsLevel2() {
		ConstructGenomicEntityAssociation association = getConstructGenomicEntityAssociation(construct2.getId(), relation2.getName(), gene2.getId());
		
		Note invalidNote = association.getRelatedNotes().get(0);
		invalidNote.setFreeText("");
		association.setRelatedNotes(List.of(invalidNote));
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			put("/api/constructgenomicentityassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.relatedNotes", is("freeText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(9)
	public void createConstructGenomicEntityAssociationWithInvalidFields() {
		Construct nonPersistedConstruct = new Construct();
		nonPersistedConstruct.setCurie("NP:Construct01");
		Gene nonPersistedGene = new Gene();
		nonPersistedGene.setCurie("NP:Gene01");
		Reference nonPersistedReference = new Reference();
		nonPersistedReference.setCurie("AGRKB:Invalid");
		ECOTerm nonPersistedEcoTerm = new ECOTerm();
		nonPersistedEcoTerm.setCurie("ECO:Invalid");
		
		Note invalidNote = new Note();
		invalidNote.setNoteType(relation);
		invalidNote.setReferences(List.of(nonPersistedReference));
		invalidNote.setFreeText("Invalid");
		
		ConstructGenomicEntityAssociation association = new ConstructGenomicEntityAssociation();
		association.setConstructAssociationSubject(nonPersistedConstruct);
		association.setRelation(noteType);
		association.setConstructGenomicEntityAssociationObject(nonPersistedGene);
		association.setEvidence(List.of(nonPersistedReference));
		association.setRelatedNotes(List.of(invalidNote));
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			post("/api/constructgenomicentityassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(5))).
			body("errorMessages.constructAssociationSubject", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.relation", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.constructGenomicEntityAssociationObject", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.evidence", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.INVALID_MESSAGE,
					"references - " + ValidationConstants.INVALID_MESSAGE))));
	}
	
	@Test
	@Order(9)
	public void editConstructGenomicEntityAssociationWithInvalidFields() {
		Construct nonPersistedConstruct = new Construct();
		nonPersistedConstruct.setCurie("NP:Construct01");
		Gene nonPersistedGene = new Gene();
		nonPersistedGene.setCurie("NP:Gene01");
		Reference nonPersistedReference = new Reference();
		nonPersistedReference.setCurie("AGRKB:Invalid");
		ECOTerm nonPersistedEcoTerm = new ECOTerm();
		nonPersistedEcoTerm.setCurie("ECO:Invalid");
		
		Note invalidNote = new Note();
		invalidNote.setNoteType(relation);
		invalidNote.setReferences(List.of(nonPersistedReference));
		invalidNote.setFreeText("Invalid");
		
		ConstructGenomicEntityAssociation association = getConstructGenomicEntityAssociation(construct2.getId(), relation2.getName(), gene2.getId());
		association.setConstructAssociationSubject(nonPersistedConstruct);
		association.setRelation(noteType);
		association.setConstructGenomicEntityAssociationObject(nonPersistedGene);
		association.setEvidence(List.of(nonPersistedReference));
		association.setRelatedNotes(List.of(invalidNote));
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			put("/api/constructgenomicentityassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(5))).
			body("errorMessages.constructAssociationSubject", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.relation", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.constructGenomicEntityAssociationObject", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.evidence", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.INVALID_MESSAGE,
					"references - " + ValidationConstants.INVALID_MESSAGE))));
	}
	
	@Test
	@Order(10)
	public void createConstructGenomicEntityAssociationWithObsoleteFields() {
		ConstructGenomicEntityAssociation association = new ConstructGenomicEntityAssociation();
		
		association.setConstructAssociationSubject(obsoleteConstruct);
		association.setRelation(obsoleteRelation);
		association.setConstructGenomicEntityAssociationObject(obsoleteGene);
		association.setEvidence(List.of(obsoleteReference));
		
		Note note = new Note();
		note.setFreeText("Obsolete note");
		note.setNoteType(obsoleteNoteType);
		note.setReferences(List.of(obsoleteReference));
		
		association.setRelatedNotes(List.of(note));
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			post("/api/constructgenomicentityassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(5))).
			body("errorMessages.constructAssociationSubject", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.relation", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.constructGenomicEntityAssociationObject", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.evidence", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"references - " + ValidationConstants.OBSOLETE_MESSAGE))));
	}
	
	@Test
	@Order(11)
	public void editConstructGenomicEntityAssociationWithObsoleteFields() {
		ConstructGenomicEntityAssociation association = getConstructGenomicEntityAssociation(construct2.getId(), relation2.getName(), gene2.getId());
		
		association.setConstructAssociationSubject(obsoleteConstruct);
		association.setRelation(obsoleteRelation);
		association.setConstructGenomicEntityAssociationObject(obsoleteGene);
		association.setEvidence(List.of(obsoleteReference));
		
		Note note = association.getRelatedNotes().get(0);
		note.setNoteType(obsoleteNoteType);
		note.setReferences(List.of(obsoleteReference));
		
		association.setRelatedNotes(List.of(note));
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			put("/api/constructgenomicentityassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(5))).
			body("errorMessages.constructAssociationSubject", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.relation", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.constructGenomicEntityAssociationObject", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.evidence", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"references - " + ValidationConstants.OBSOLETE_MESSAGE))));
	}
	
	@Test
	@Order(12)
	public void editConstructGenomicEntityAssociationWithNullNonRequiredFieldsLevel2() {
		ConstructGenomicEntityAssociation association = getConstructGenomicEntityAssociation(construct2.getId(), relation2.getName(), gene2.getId());
		
		Note editedNote = association.getRelatedNotes().get(0);
		editedNote.setReferences(null);
		association.setRelatedNotes(List.of(editedNote));
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			put("/api/constructgenomicentityassociation").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get(constructGenomicEntityAssociationGetEndpoint + "?constructId=" + construct2.getId() + "&relationName=" + relation2.getName() + "&genomicEntityId=" + gene2.getId()).
			then().
			statusCode(200).
			body("entity", hasKey("relatedNotes")).
			body("entity.relatedNotes[0]", not(hasKey("references")));
	}
	
	@Test
	@Order(13)
	public void editConstructGenomicEntityAssociationWithNullNonRequiredFieldsLevel1() {
		ConstructGenomicEntityAssociation association = getConstructGenomicEntityAssociation(construct2.getId(), relation2.getName(), gene2.getId());
		
		association.setEvidence(null);
		association.setRelatedNotes(null);
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			put("/api/constructgenomicentityassociation").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get(constructGenomicEntityAssociationGetEndpoint + "?constructId=" + construct2.getId() + "&relationName=" + relation2.getName() + "&genomicEntityId=" + gene2.getId()).
			then().
			statusCode(200).
			body("entity", not(hasKey("evidence"))).
			body("entity", not(hasKey("relatedNotes")));
	}
}
