package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.alliancegenome.curation_api.response.ObjectResponse;
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
@DisplayName("308 - NoteITCase")
@Order(308)
public class NoteITCase extends BaseITCase {
	
	private Vocabulary testVocabulary;
	private VocabularyTerm testVocabularyTerm;
	private VocabularyTerm testVocabularyTerm2;
	private VocabularyTerm testObsoleteVocabularyTerm;
	private List<Reference> testReferences = new ArrayList<Reference>();
	private List<Reference> testReferences2 = new ArrayList<Reference>();
	private List<Reference> testObsoleteReferences = new ArrayList<Reference>();
	private Long testNoteId;
	
	private void createRequiredObjects() {
		testVocabulary = getVocabulary(VocabularyConstants.NOTE_TYPE_VOCABULARY);
		testVocabularyTerm = createVocabularyTerm(testVocabulary, "Note test vocabulary term", false);
		testVocabularyTerm2 = createVocabularyTerm(testVocabulary, "Note test vocabulary term 2", false);
		testObsoleteVocabularyTerm = createVocabularyTerm(testVocabulary, "Obsolete Note test vocabularyTerm", true);
		Reference testReference = createReference("AGRKB:000000007", false);
		testReferences.add(testReference);
		Reference testReference2 = createReference("AGRKB:000000008", false);
		testReferences2.add(testReference2);
		Reference testObsoleteReference = createReference("AGRKB:000000009", true);
		testObsoleteReferences.add(testObsoleteReference);
	}

	@Test
	@Order(1)
	public void createNote() {
		createRequiredObjects();
		
		Note note = new Note();
		note.setNoteType(testVocabularyTerm);
		note.setInternal(true);
		note.setObsolete(true);
		note.setReferences(testReferences);
		note.setFreeText("Test text");
		
		ObjectResponse<Note> response = RestAssured.given().
				contentType("application/json").
				body(note).
				when().
				post("/api/note").
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefNote());
		
		testNoteId = response.getEntity().getId();
		
		RestAssured.given().
				when().
				get("/api/note/" + testNoteId).
				then().
				statusCode(200).
				body("entity.obsolete", is(true)).
				body("entity.internal", is(true)).
				body("entity.noteType.name", is("Note test vocabulary term")).
				body("entity.references[0].curie", is(testReferences.get(0).getCurie())).
				body("entity.freeText", is("Test text")).
				body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
				body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org"));
	}

	@Test
	@Order(2)
	public void editVocabularyTerm() {
		
		Note editedNote = getNote(testNoteId);
		
		editedNote.setNoteType(testVocabularyTerm2);
		editedNote.setObsolete(false);
		editedNote.setInternal(false);
		editedNote.setReferences(testReferences2);
		editedNote.setFreeText("Edited test text");
		
		RestAssured.given().
				contentType("application/json").
				body(editedNote).
				when().
				put("/api/note").
				then().
				statusCode(200);
		
		RestAssured.given().
				when().
				get("/api/note/" + testNoteId).
				then().
				statusCode(200).
				body("entity.obsolete", is(false)).
				body("entity.internal", is(false)).
				body("entity.noteType.name", is("Note test vocabulary term 2")).
				body("entity.references[0].curie", is(testReferences2.get(0).getCurie())).
				body("entity.freeText", is("Edited test text")).
				body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
				body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org"));
	}

	@Test
	@Order(3)
	public void editWithMissingNoteType() {
		
		Note editedNote = getNote(testNoteId);
		
		editedNote.setNoteType(null);
		editedNote.setObsolete(false);
		editedNote.setInternal(false);
		editedNote.setReferences(testReferences2);
		editedNote.setFreeText("Edited test text");
		
		RestAssured.given().
				contentType("application/json").
				body(editedNote).
				when().
				put("/api/note").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.noteType", is(ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(4)
	public void editWithInvalidNoteType() {
		
		Note editedNote = getNote(testNoteId);
		
		VocabularyTerm nonPersistedVocabularyTerm = new VocabularyTerm();
		nonPersistedVocabularyTerm.setName("Non-persisted vocabulary term");
		nonPersistedVocabularyTerm.setVocabulary(testVocabulary);
		
		editedNote.setNoteType(nonPersistedVocabularyTerm);
		editedNote.setObsolete(false);
		editedNote.setInternal(false);
		editedNote.setReferences(testReferences2);
		editedNote.setFreeText("Edited test text");
		
		RestAssured.given().
				contentType("application/json").
				body(editedNote).
				when().
				put("/api/note").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.noteType", is(ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(5)
	public void editWithObsoleteNoteType() {
		
		Note editedNote = getNote(testNoteId);
		
		editedNote.setNoteType(testObsoleteVocabularyTerm);
		editedNote.setObsolete(false);
		editedNote.setInternal(false);
		editedNote.setReferences(testReferences2);
		editedNote.setFreeText("Edited test text");
		
		RestAssured.given().
				contentType("application/json").
				body(editedNote).
				when().
				put("/api/note").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.noteType", is(ValidationConstants.OBSOLETE_MESSAGE));
	}

	@Test
	@Order(6)
	public void editWithMissingFreeText() {
		
		Note editedNote = getNote(testNoteId);
		
		editedNote.setNoteType(testVocabularyTerm2);
		editedNote.setObsolete(false);
		editedNote.setInternal(false);
		editedNote.setReferences(testReferences2);
		editedNote.setFreeText(null);
		
		RestAssured.given().
				contentType("application/json").
				body(editedNote).
				when().
				put("/api/note").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.freeText", is(ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(7)
	public void editWithEmptyFreeText() {
		
		Note editedNote = getNote(testNoteId);
		
		editedNote.setNoteType(testVocabularyTerm2);
		editedNote.setObsolete(false);
		editedNote.setInternal(false);
		editedNote.setReferences(testReferences2);
		editedNote.setFreeText("");
		
		RestAssured.given().
				contentType("application/json").
				body(editedNote).
				when().
				put("/api/note").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.freeText", is(ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(8)
	public void editWithInvalidReference() {
		
		Note editedNote = getNote(testNoteId);
		
		List<Reference> nonPersistedReferences = new ArrayList<Reference>();
		Reference nonPersistedReference = new Reference();
		nonPersistedReference.setCurie("Invalid");
		nonPersistedReferences.add(nonPersistedReference);
		
		editedNote.setNoteType(testVocabularyTerm2);
		editedNote.setObsolete(false);
		editedNote.setInternal(false);
		editedNote.setReferences(nonPersistedReferences);
		editedNote.setFreeText("Edited test text");
		
		RestAssured.given().
				contentType("application/json").
				body(editedNote).
				when().
				put("/api/note").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.references", is(ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(9)
	public void editWithObsoleteReference() {
		
		Note editedNote = getNote(testNoteId);
		
		editedNote.setNoteType(testVocabularyTerm2);
		editedNote.setObsolete(false);
		editedNote.setInternal(false);
		editedNote.setReferences(testObsoleteReferences);
		editedNote.setFreeText("Edited test text");
		
		RestAssured.given().
				contentType("application/json").
				body(editedNote).
				when().
				put("/api/note").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.references", is(ValidationConstants.OBSOLETE_MESSAGE));
		}

	@Test
	@Order(10)
	public void createWithMissingNoteType() {
		
		Note newNote = new Note();
		
		newNote.setNoteType(null);
		newNote.setObsolete(false);
		newNote.setInternal(false);
		newNote.setReferences(testReferences2);
		newNote.setFreeText("New test text");
		
		RestAssured.given().
				contentType("application/json").
				body(newNote).
				when().
				post("/api/note").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.noteType", is(ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(11)
	public void createWithInvalidNoteType() {
		
		Note newNote = new Note();
		
		VocabularyTerm nonPersistedVocabularyTerm = new VocabularyTerm();
		nonPersistedVocabularyTerm.setName("Non-persisted vocabulary term");
		nonPersistedVocabularyTerm.setVocabulary(testVocabulary);
		
		newNote.setNoteType(nonPersistedVocabularyTerm);
		newNote.setObsolete(false);
		newNote.setInternal(false);
		newNote.setReferences(testReferences2);
		newNote.setFreeText("New test text");
		
		RestAssured.given().
				contentType("application/json").
				body(newNote).
				when().
				post("/api/note").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.noteType", is(ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(12)
	public void createWithObsoleteNoteType() {
		
		Note newNote = new Note();
		
		newNote.setNoteType(testObsoleteVocabularyTerm);
		newNote.setObsolete(false);
		newNote.setInternal(false);
		newNote.setReferences(testReferences2);
		newNote.setFreeText("New test text");
		
		RestAssured.given().
				contentType("application/json").
				body(newNote).
				when().
				post("/api/note").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.noteType", is(ValidationConstants.OBSOLETE_MESSAGE));
	}

	@Test
	@Order(13)
	public void createWithMissingFreeText() {
		
		Note newNote = new Note();
		
		newNote.setNoteType(testVocabularyTerm2);
		newNote.setObsolete(false);
		newNote.setInternal(false);
		newNote.setReferences(testReferences2);
		newNote.setFreeText(null);
		
		RestAssured.given().
				contentType("application/json").
				body(newNote).
				when().
				post("/api/note").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.freeText", is(ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(14)
	public void createWithEmptyFreeText() {
		
		Note newNote = new Note();
		
		newNote.setNoteType(testVocabularyTerm2);
		newNote.setObsolete(false);
		newNote.setInternal(false);
		newNote.setReferences(testReferences2);
		newNote.setFreeText("");
		
		RestAssured.given().
				contentType("application/json").
				body(newNote).
				when().
				post("/api/note").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.freeText", is(ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(15)
	public void createWithInvalidReference() {
		
		Note newNote = new Note();
		
		List<Reference> nonPersistedReferences = new ArrayList<Reference>();
		Reference nonPersistedReference = new Reference();
		nonPersistedReference.setCurie("Invalid");
		nonPersistedReferences.add(nonPersistedReference);
		
		newNote.setNoteType(testVocabularyTerm2);
		newNote.setObsolete(false);
		newNote.setInternal(false);
		newNote.setReferences(nonPersistedReferences);
		newNote.setFreeText("New test text");
		
		RestAssured.given().
				contentType("application/json").
				body(newNote).
				when().
				post("/api/note").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.references", is(ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(16)
	public void createWithObsoleteReference() {
		
		Note newNote = new Note();
		
		newNote.setNoteType(testVocabularyTerm2);
		newNote.setObsolete(false);
		newNote.setInternal(false);
		newNote.setReferences(testObsoleteReferences);
		newNote.setFreeText("New test text");
		
		RestAssured.given().
				contentType("application/json").
				body(newNote).
				when().
				post("/api/note").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.references", is(ValidationConstants.OBSOLETE_MESSAGE));
	}

}
