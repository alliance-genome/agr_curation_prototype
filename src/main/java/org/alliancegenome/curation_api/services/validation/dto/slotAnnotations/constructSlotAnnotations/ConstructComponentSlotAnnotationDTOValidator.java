package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.constructSlotAnnotations;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.constructSlotAnnotations.ConstructComponentSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.notes.NoteIdentityHelper;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.services.validation.dto.NoteDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.SlotAnnotationDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ConstructComponentSlotAnnotationDTOValidator extends SlotAnnotationDTOValidator {

	@Inject NcbiTaxonTermService ncbiTaxonTermService;
	@Inject ConstructComponentSlotAnnotationDAO constructComponentDAO;
	@Inject NoteDTOValidator noteDtoValidator;
	@Inject NoteDAO noteDAO;
	@Inject VocabularyTermService vocabularyTermService;

	public ObjectResponse<ConstructComponentSlotAnnotation> validateConstructComponentSlotAnnotationDTO(ConstructComponentSlotAnnotation annotation, ConstructComponentSlotAnnotationDTO dto) {
		ObjectResponse<ConstructComponentSlotAnnotation> ccsaResponse = new ObjectResponse<ConstructComponentSlotAnnotation>();

		if (annotation == null) {
			annotation = new ConstructComponentSlotAnnotation();
		}

		ObjectResponse<ConstructComponentSlotAnnotation> saResponse = validateSlotAnnotationDTO(annotation, dto);
		annotation = saResponse.getEntity();
		ccsaResponse.addErrorMessages(saResponse.getErrorMessages());

		if (StringUtils.isAllBlank(dto.getComponentSymbol())) {
			ccsaResponse.addErrorMessage("component_symbol", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			annotation.setComponentSymbol(dto.getComponentSymbol());
		}

		if (StringUtils.isNotEmpty(dto.getRelationName())) {
			VocabularyTerm diseaseRelation = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.CONSTRUCT_GENOMIC_ENTITY_RELATION_VOCABULARY_TERM_SET, dto.getRelationName()).getEntity();
			if (diseaseRelation == null) {
				ccsaResponse.addErrorMessage("relation_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getRelationName() + ")");
			}
			annotation.setRelation(diseaseRelation);
		} else {
			ccsaResponse.addErrorMessage("relation_name", ValidationConstants.REQUIRED_MESSAGE);
		}

		if (StringUtils.isNotBlank(dto.getTaxonCurie())) {
			ObjectResponse<NCBITaxonTerm> taxonResponse = ncbiTaxonTermService.getByCurie(dto.getTaxonCurie());
			if (taxonResponse.getEntity() == null) {
				ccsaResponse.addErrorMessage("taxon_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getTaxonCurie() + ")");
			}
			annotation.setTaxon(taxonResponse.getEntity());
		} else {
			annotation.setTaxon(null);
		}

		if (StringUtils.isNotBlank(dto.getTaxonText())) {
			annotation.setTaxonText(dto.getTaxonText());
		} else {
			annotation.setTaxonText(null);
		}
		
		
		if (annotation.getRelatedNotes() != null) {
			annotation.getRelatedNotes().clear();
		}
		
		List<Note> validatedNotes = new ArrayList<Note>();
		List<String> noteIdentities = new ArrayList<String>();
		Boolean allNotesValid = true;
		if (CollectionUtils.isNotEmpty(dto.getNoteDtos())) {
			for (int ix = 0; ix < dto.getNoteDtos().size(); ix++) {
				ObjectResponse<Note> noteResponse = noteDtoValidator.validateNoteDTO(dto.getNoteDtos().get(ix), VocabularyConstants.CONSTRUCT_COMPONENT_NOTE_TYPES_VOCABULARY_TERM_SET);
				if (noteResponse.hasErrors()) {
					allNotesValid = false;
					ccsaResponse.addErrorMessages("relatedNotes", ix, noteResponse.getErrorMessages());
					break;
				}
				String noteIdentity = NoteIdentityHelper.noteDtoIdentity(dto.getNoteDtos().get(ix));
				if (!noteIdentities.contains(noteIdentity)) {
					noteIdentities.add(noteIdentity);
					validatedNotes.add(noteDAO.persist(noteResponse.getEntity()));
				}
			}
		}
		if (!allNotesValid) {
			ccsaResponse.convertMapToErrorMessages("relatedNotes");
		}
		if (CollectionUtils.isNotEmpty(validatedNotes) && allNotesValid) {
			if (annotation.getRelatedNotes() == null) {
				annotation.setRelatedNotes(new ArrayList<>());
			}
			annotation.getRelatedNotes().addAll(validatedNotes);
		}


		ccsaResponse.setEntity(annotation);

		return ccsaResponse;
	}
}
