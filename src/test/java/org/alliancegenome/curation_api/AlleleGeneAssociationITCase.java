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
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
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
@DisplayName("401 - AlleleGeneAssociationITCase")
@Order(401)
public class AlleleGeneAssociationITCase extends BaseITCase {

	private Allele allele;
	private Allele allele2;
	private Allele obsoleteAllele;
	private Gene gene;
	private Gene gene2;
	private Gene obsoleteGene;
	private VocabularyTerm relation;
	private VocabularyTerm relation2;
	private VocabularyTerm obsoleteRelation;
	private ECOTerm ecoTerm;
	private ECOTerm ecoTerm2;
	private ECOTerm obsoleteEcoTerm;
	private Reference reference;
	private Reference reference2;
	private Reference obsoleteReference;
	private VocabularyTerm noteType;
	private VocabularyTerm noteType2;
	private VocabularyTerm obsoleteNoteType;
	private Note note;
	private Note note2;
	
	private final String alleleGeneAssociationGetEndpoint = "/api/allelegeneassociation/findBy";
	private final String alleleGetEndpoint = "/api/allele/";
	private final String geneGetEndpoint = "/api/gene/";
	
	private void loadRequiredEntities() {
		Vocabulary nameTypeVocabulary = getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY);
		VocabularyTerm symbolNameType = getVocabularyTerm(nameTypeVocabulary, "nomenclature_symbol");
		allele = createAllele("AGA:Allele0001", "NCBITaxon:6239", symbolNameType, false);
		allele2 = createAllele("AGA:Allele0002", "NCBITaxon:6239", symbolNameType, false);
		obsoleteAllele = createAllele("AGA:Allele0003", "NCBITaxon:6239", symbolNameType, true);
		gene = createGene("AGA:Gene0001", "NCBITaxon:6239", symbolNameType, false);
		gene2 = createGene("AGA:Gene0002", "NCBITaxon:6239", symbolNameType, false);
		obsoleteGene = createGene("AGA:Gene0003", "NCBITaxon:6239", symbolNameType, true);
		Vocabulary relationVocabulary = getVocabulary(VocabularyConstants.ALLELE_RELATION_VOCABULARY);
		relation = getVocabularyTerm(relationVocabulary, "is_allele_of");
		relation2 = getVocabularyTerm(relationVocabulary, "mutation_excludes");
		obsoleteRelation = addVocabularyTermToSet(VocabularyConstants.ALLELE_GENE_RELATION_VOCABULARY_TERM_SET, "is_obsolete", relationVocabulary, true);
		ecoTerm = createEcoTerm("ECO:AGA0001", "AGA_ECO_test", false, true);
		ecoTerm2 = createEcoTerm("ECO:AGA0002", "AGA_ECO_test2", false, true);
		obsoleteEcoTerm = createEcoTerm("ECO:AGA0003", "AGA_ECO_test3", true, true);
		reference = createReference("AGRKB:AGA0001", false);
		reference2 = createReference("AGRKB:AGA0002", false);
		obsoleteReference = createReference("AGRKB:AGA0003", true);
		Vocabulary noteTypeVocabulary = getVocabulary(VocabularyConstants.NOTE_TYPE_VOCABULARY);
		noteType = getVocabularyTerm(noteTypeVocabulary, "comment");
		noteType2 = getVocabularyTerm(noteTypeVocabulary, "remark");
		obsoleteNoteType = addVocabularyTermToSet(VocabularyConstants.ALLELE_GENOMIC_ENTITY_ASSOCIATION_NOTE_TYPES_VOCABULARY_TERM_SET, "obsolete_note_type", noteTypeVocabulary, true);
		note = createNote(noteType, "AGA Test Note", false, reference);
		note2 = createNote(noteType2, "AGA Test Note 2", false, reference2);
	}
	
	@Test
	@Order(1)
	public void createValidAlleleGeneAssociation() {
		loadRequiredEntities();
		
		AlleleGeneAssociation association = new AlleleGeneAssociation();
		association.setAlleleAssociationSubject(allele);
		association.setAlleleGeneAssociationObject(gene);
		association.setRelation(relation);
		association.setEvidenceCode(ecoTerm);
		association.setInternal(true);
		association.setObsolete(true);
		association.setRelatedNote(note);
		association.setEvidence(List.of(reference));
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			post("/api/allelegeneassociation").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get(alleleGeneAssociationGetEndpoint + "?alleleId=" + allele.getId() + "&relationName=" + relation.getName() + "&geneId=" + gene.getId()).
			then().
			statusCode(200).
			body("entity.relation.name", is(relation.getName())).
			body("entity.alleleGeneAssociationObject.modEntityId", is(gene.getModEntityId())).
			body("entity.alleleAssociationSubject.modEntityId", is(allele.getModEntityId())).
			body("entity.evidence", hasSize(1)).
			body("entity.evidence[0].curie", is(reference.getCurie())).
			body("entity.evidenceCode.curie", is(ecoTerm.getCurie())).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.relatedNote.internal", is(false)).
			body("entity.relatedNote.obsolete", is(false)).
			body("entity.relatedNote.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.relatedNote.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.relatedNote.freeText", is(note.getFreeText())).
			body("entity.relatedNote.noteType.name", is(noteType.getName())).
			body("entity.relatedNote.references[0].curie", is(reference.getCurie()));

		RestAssured.given().
			when().
			get(alleleGetEndpoint + allele.getModEntityId()).
			then().
			statusCode(200).
			body("entity.alleleGeneAssociations", hasSize(1)).
			body("entity.alleleGeneAssociations[0].relation.name", is(relation.getName())).
			body("entity.alleleGeneAssociations[0].alleleGeneAssociationObject.modEntityId", is(gene.getModEntityId())).
			body("entity.alleleGeneAssociations[0].alleleAssociationSubject", not(hasKey("alleleGeneAssociations")));
		
		RestAssured.given().
			when().
			get(geneGetEndpoint + gene.getModEntityId()).
			then().
			statusCode(200).
			body("entity.alleleGeneAssociations", hasSize(1)).
			body("entity.alleleGeneAssociations[0].relation.name", is(relation.getName())).
			body("entity.alleleGeneAssociations[0].alleleGeneAssociationObject.modEntityId", is(gene.getModEntityId())).
			body("entity.alleleGeneAssociations[0].alleleGeneAssociationObject", not(hasKey("alleleGeneAssociations")));
	}
	
	@Test
	@Order(2)
	public void editAlleleGeneAssociation() {
		AlleleGeneAssociation association = getAlleleGeneAssociation(allele.getId(), relation.getName(), gene.getId());
		association.setAlleleAssociationSubject(allele2);
		association.setRelation(relation2);
		association.setAlleleGeneAssociationObject(gene2);
		association.setEvidenceCode(ecoTerm2);
		association.setInternal(false);
		association.setObsolete(false);
		association.setEvidence(List.of(reference2));
		association.setRelatedNote(note2);
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			put("/api/allelegeneassociation").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get(alleleGeneAssociationGetEndpoint + "?alleleId=" + allele2.getId() + "&relationName=" + relation2.getName() + "&geneId=" + gene2.getId()).
			then().
			statusCode(200).
			body("entity.relation.name", is(relation2.getName())).
			body("entity.alleleGeneAssociationObject.modEntityId", is(gene2.getModEntityId())).
			body("entity.alleleAssociationSubject.modEntityId", is(allele2.getModEntityId())).
			body("entity.evidence", hasSize(1)).
			body("entity.evidence[0].curie", is(reference2.getCurie())).
			body("entity.evidenceCode.curie", is(ecoTerm2.getCurie())).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.relatedNote.internal", is(false)).
			body("entity.relatedNote.obsolete", is(false)).
			body("entity.relatedNote.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.relatedNote.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.relatedNote.freeText", is(note2.getFreeText())).
			body("entity.relatedNote.noteType.name", is(noteType2.getName())).
			body("entity.relatedNote.references[0].curie", is(reference2.getCurie()));
	}
	
	@Test
	@Order(3)
	public void createAlleleGeneAssociationWithMissingRequiredFieldsLevel1() {
		AlleleGeneAssociation association = new AlleleGeneAssociation();
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			post("/api/allelegeneassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(3))).
			body("errorMessages.alleleAssociationSubject", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.relation", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.alleleGeneAssociationObject", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(4)
	public void createAlleleGeneAssociationWithMissingRequiredFieldsLevel2() {
		AlleleGeneAssociation association = new AlleleGeneAssociation();
		association.setAlleleAssociationSubject(allele2);
		association.setRelation(relation);
		association.setAlleleGeneAssociationObject(gene);
		
		Note emptyNote = new Note();
		association.setRelatedNote(emptyNote);
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			post("/api/allelegeneassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.relatedNote", is(String.join(" | ", List.of(
					"freeText - " + ValidationConstants.REQUIRED_MESSAGE,
					"noteType - " + ValidationConstants.REQUIRED_MESSAGE))));
	}
	
	@Test
	@Order(5)
	public void createAlleleGeneAssociationWithEmptyRequiredFieldsLevel2() {
		AlleleGeneAssociation association = new AlleleGeneAssociation();
		association.setAlleleAssociationSubject(allele2);
		association.setRelation(relation);
		association.setAlleleGeneAssociationObject(gene);
		
		Note invalidNote = new Note();
		invalidNote.setNoteType(noteType);
		association.setRelatedNote(invalidNote);
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			post("/api/allelegeneassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.relatedNote", is("freeText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(6)
	public void editAlleleGeneAssociationWithMissingRequiredFieldsLevel1() {
		AlleleGeneAssociation association = getAlleleGeneAssociation(allele2.getId(), relation2.getName(), gene2.getId());
		association.setAlleleAssociationSubject(null);
		association.setRelation(null);
		association.setAlleleGeneAssociationObject(null);
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			put("/api/allelegeneassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(3))).
			body("errorMessages.alleleAssociationSubject", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.relation", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.alleleGeneAssociationObject", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(7)
	public void editAlleleGeneAssociationWithMissingRequiredFieldsLevel2() {
		AlleleGeneAssociation association = getAlleleGeneAssociation(allele2.getId(), relation2.getName(), gene2.getId());
	
		Note invalidNote = association.getRelatedNote();
		invalidNote.setFreeText(null);
		invalidNote.setNoteType(null);
		association.setRelatedNote(invalidNote);
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			put("/api/allelegeneassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.relatedNote", is(String.join(" | ", List.of(
					"freeText - " + ValidationConstants.REQUIRED_MESSAGE,
					"noteType - " + ValidationConstants.REQUIRED_MESSAGE))));
	}
	
	@Test
	@Order(8)
	public void editAlleleGeneAssociationWithEmptyRequiredFieldsLevel2() {
		AlleleGeneAssociation association = getAlleleGeneAssociation(allele2.getId(), relation2.getName(), gene2.getId());
		
		Note invalidNote = association.getRelatedNote();
		invalidNote.setFreeText("");
		association.setRelatedNote(invalidNote);
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			put("/api/allelegeneassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.relatedNote", is("freeText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(9)
	public void createAlleleGeneAssociationWithInvalidFields() {
		Allele nonPersistedAllele = new Allele();
		nonPersistedAllele.setModEntityId("NP:Allele01");
		Gene nonPersistedGene = new Gene();
		nonPersistedGene.setModEntityId("NP:Gene01");
		Reference nonPersistedReference = new Reference();
		nonPersistedReference.setCurie("AGRKB:Invalid");
		ECOTerm nonPersistedEcoTerm = new ECOTerm();
		nonPersistedEcoTerm.setCurie("ECO:Invalid");
		
		Note invalidNote = new Note();
		invalidNote.setNoteType(relation);
		invalidNote.setReferences(List.of(nonPersistedReference));
		invalidNote.setFreeText("Invalid");
		
		AlleleGeneAssociation association = new AlleleGeneAssociation();
		association.setAlleleAssociationSubject(nonPersistedAllele);
		association.setRelation(noteType);
		association.setAlleleGeneAssociationObject(nonPersistedGene);
		association.setEvidence(List.of(nonPersistedReference));
		association.setRelatedNote(invalidNote);
		association.setEvidenceCode(nonPersistedEcoTerm);
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			post("/api/allelegeneassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.alleleAssociationSubject", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.relation", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.alleleGeneAssociationObject", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.evidence", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.evidenceCode", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.relatedNote", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.INVALID_MESSAGE,
					"references - " + ValidationConstants.INVALID_MESSAGE))));
	}
	
	@Test
	@Order(9)
	public void editAlleleGeneAssociationWithInvalidFields() {
		Allele nonPersistedAllele = new Allele();
		nonPersistedAllele.setModEntityId("NP:Allele01");
		Gene nonPersistedGene = new Gene();
		nonPersistedGene.setModEntityId("NP:Gene01");
		Reference nonPersistedReference = new Reference();
		nonPersistedReference.setCurie("AGRKB:Invalid");
		ECOTerm nonPersistedEcoTerm = new ECOTerm();
		nonPersistedEcoTerm.setCurie("ECO:Invalid");
		
		Note invalidNote = new Note();
		invalidNote.setNoteType(relation);
		invalidNote.setReferences(List.of(nonPersistedReference));
		invalidNote.setFreeText("Invalid");
		
		AlleleGeneAssociation association = getAlleleGeneAssociation(allele2.getId(), relation2.getName(), gene2.getId());
		association.setAlleleAssociationSubject(nonPersistedAllele);
		association.setRelation(noteType);
		association.setAlleleGeneAssociationObject(nonPersistedGene);
		association.setEvidence(List.of(nonPersistedReference));
		association.setRelatedNote(invalidNote);
		association.setEvidenceCode(nonPersistedEcoTerm);
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			put("/api/allelegeneassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.alleleAssociationSubject", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.relation", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.alleleGeneAssociationObject", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.evidence", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.evidenceCode", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.relatedNote", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.INVALID_MESSAGE,
					"references - " + ValidationConstants.INVALID_MESSAGE))));
	}
	
	@Test
	@Order(10)
	public void createAlleleGeneAssociationWithObsoleteFields() {
		AlleleGeneAssociation association = new AlleleGeneAssociation();
		
		association.setAlleleAssociationSubject(obsoleteAllele);
		association.setRelation(obsoleteRelation);
		association.setAlleleGeneAssociationObject(obsoleteGene);
		association.setEvidenceCode(obsoleteEcoTerm);
		association.setEvidence(List.of(obsoleteReference));
		
		Note note = new Note();
		note.setFreeText("Obsolete note");
		note.setNoteType(obsoleteNoteType);
		note.setReferences(List.of(obsoleteReference));
		
		association.setRelatedNote(note);
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			post("/api/allelegeneassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.alleleAssociationSubject", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.relation", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.alleleGeneAssociationObject", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.evidence", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.evidenceCode", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.relatedNote", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"references - " + ValidationConstants.OBSOLETE_MESSAGE))));
	}
	
	@Test
	@Order(11)
	public void editAlleleGeneAssociationWithObsoleteFields() {
		AlleleGeneAssociation association = getAlleleGeneAssociation(allele2.getId(), relation2.getName(), gene2.getId());
		
		association.setAlleleAssociationSubject(obsoleteAllele);
		association.setRelation(obsoleteRelation);
		association.setAlleleGeneAssociationObject(obsoleteGene);
		association.setEvidenceCode(obsoleteEcoTerm);
		association.setEvidence(List.of(obsoleteReference));
		
		Note note = association.getRelatedNote();
		note.setNoteType(obsoleteNoteType);
		note.setReferences(List.of(obsoleteReference));
		
		association.setRelatedNote(note);
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			put("/api/allelegeneassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.alleleAssociationSubject", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.relation", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.alleleGeneAssociationObject", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.evidence", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.evidenceCode", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.relatedNote", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"references - " + ValidationConstants.OBSOLETE_MESSAGE))));
	}
	
	@Test
	@Order(12)
	public void editAlleleGeneAssociationWithNullNonRequiredFieldsLevel2() {
		AlleleGeneAssociation association = getAlleleGeneAssociation(allele2.getId(), relation2.getName(), gene2.getId());
		
		Note editedNote = association.getRelatedNote();
		editedNote.setReferences(null);
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			put("/api/allelegeneassociation").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get(alleleGeneAssociationGetEndpoint + "?alleleId=" + allele2.getId() + "&relationName=" + relation2.getName() + "&geneId=" + gene2.getId()).
			then().
			statusCode(200).
			body("entity", hasKey("relatedNote")).
			body("entity.relatedNote", not(hasKey("references")));
	}
	
	@Test
	@Order(13)
	public void editAlleleGeneAssociationWithNullNonRequiredFieldsLevel1() {
		AlleleGeneAssociation association = getAlleleGeneAssociation(allele2.getId(), relation2.getName(), gene2.getId());
		
		association.setEvidence(null);
		association.setEvidenceCode(null);
		association.setRelatedNote(null);
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			put("/api/allelegeneassociation").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get(alleleGeneAssociationGetEndpoint + "?alleleId=" + allele2.getId() + "&relationName=" + relation2.getName() + "&geneId=" + gene2.getId()).
			then().
			statusCode(200).
			body("entity", not(hasKey("evidence"))).
			body("entity", not(hasKey("evidenceCode"))).
			body("entity", not(hasKey("relatedNote")));
	}
}
