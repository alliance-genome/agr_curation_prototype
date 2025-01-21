package org.alliancegenome.curation_api.services.validation.dto;

import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.NoteDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.base.AuditedObjectDTOValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class NoteDTOValidator extends AuditedObjectDTOValidator<Note, NoteDTO> {

	public ObjectResponse<Note> validateNoteDTO(NoteDTO dto, String noteTypeVocabularyTermSet) {
		response = new ObjectResponse<Note>();
		
		Note note = new Note();
		note = validateAuditedObjectDTO(note, dto);

		if (StringUtils.isBlank(dto.getFreeText())) {
			response.addErrorMessage("freeText", ValidationConstants.REQUIRED_MESSAGE);
		}
		note.setFreeText(dto.getFreeText());

		VocabularyTerm noteType = validateRequiredTermInVocabularyTermSet("note_type_name", dto.getNoteTypeName(), noteTypeVocabularyTermSet);
		note.setNoteType(noteType);

		List<Reference> references = validateReferences("evidence_curies", dto.getEvidenceCuries());
		note.setReferences(references);
		
		response.setEntity(note);

		return response;
	}

}
