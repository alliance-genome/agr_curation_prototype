package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PublicationRefFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.VariantNoteFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class VariantNoteFmsDTOValidator {

	@Inject ReferenceDAO referenceDAO;
	@Inject ReferenceService referenceService;
	@Inject PersonService personService;
	@Inject VocabularyTermService vocabularyTermService;

	public ObjectResponse<Note> validateVariantNoteFmsDTO(VariantNoteFmsDTO dto) {
		Note note = new Note();
		ObjectResponse<Note> noteResponse = new ObjectResponse<>();

		if (StringUtils.isBlank(dto.getNote())) {
			noteResponse.addErrorMessage("note", ValidationConstants.REQUIRED_MESSAGE);
		}
		note.setFreeText(dto.getNote());

		VocabularyTerm noteType = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.VARIANT_NOTE_TYPES_VOCABULARY_TERM_SET, "comment").getEntity();
		if (noteType == null) {
			noteResponse.addErrorMessage("note_type", ValidationConstants.INVALID_MESSAGE + " (comment)");
		}
		note.setNoteType(noteType);

		if (CollectionUtils.isNotEmpty(dto.getReferences())) {
			List<Reference> noteReferences = new ArrayList<>();
			for (PublicationRefFmsDTO publicationDto : dto.getReferences()) {
				if (StringUtils.isBlank(publicationDto.getPublicationId())) {
					noteResponse.addErrorMessage("references - publicationId", ValidationConstants.REQUIRED_MESSAGE);
				} else {
					Reference reference = null;
					reference = referenceService.retrieveFromDbOrLiteratureService(publicationDto.getPublicationId());
					if (reference == null) {
						noteResponse.addErrorMessage("evidence_curies", ValidationConstants.INVALID_MESSAGE + " (" + publicationDto.getPublicationId() + ")");
						break;
					}
					noteReferences.add(reference);
				}
			}
			note.setReferences(noteReferences);
		} else {
			note.setReferences(null);
		}

		note.setInternal(false);

		noteResponse.setEntity(note);

		return noteResponse;
	}

}
