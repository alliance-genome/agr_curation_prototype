package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.AnnotationDAO;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.dao.DataProviderDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.ingest.dto.AnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.ConditionRelationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.helpers.notes.NoteIdentityHelper;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AnnotationDTOValidator extends BaseDTOValidator {

	@Inject ReferenceService referenceService;
	@Inject NoteDAO noteDAO;
	@Inject AnnotationDAO annotationDAO;
	@Inject ConditionRelationDAO conditionRelationDAO;
	@Inject ConditionRelationDTOValidator conditionRelationDtoValidator;
	@Inject NoteDTOValidator noteDtoValidator;
	@Inject DataProviderDTOValidator dataProviderDtoValidator;
	@Inject DataProviderDAO dataProviderDAO;

	public <E extends Annotation, D extends AnnotationDTO> ObjectResponse<E> validateAnnotationDTO(E annotation, D dto, String noteTypeSet) {
		ObjectResponse<E> annotResponse = validateAuditedObjectDTO(annotation, dto);
		annotation = annotResponse.getEntity();

		annotation.setModEntityId(handleStringField(dto.getModEntityId()));
		annotation.setModInternalId(handleStringField(dto.getModInternalId()));

		if (dto.getDataProviderDto() == null) {
			annotResponse.addErrorMessage("data_provider_dto", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<DataProvider> dpResponse = dataProviderDtoValidator.validateDataProviderDTO(dto.getDataProviderDto(), annotation.getDataProvider());
			if (dpResponse.hasErrors()) {
				annotResponse.addErrorMessage("data_provider_dto", dpResponse.errorMessagesString());
			} else {
				annotation.setDataProvider(dataProviderDAO.persist(dpResponse.getEntity()));
			}
		}

		if (annotation.getRelatedNotes() != null) {
			annotation.getRelatedNotes().clear();
		}

		List<Note> validatedNotes = new ArrayList<Note>();
		List<String> noteIdentities = new ArrayList<String>();
		Boolean allNotesValid = true;
		if (CollectionUtils.isNotEmpty(dto.getNoteDtos())) {
			for (int ix = 0; ix < dto.getNoteDtos().size(); ix++) {
				ObjectResponse<Note> noteResponse = noteDtoValidator.validateNoteDTO(dto.getNoteDtos().get(ix), noteTypeSet);
				if (noteResponse.hasErrors()) {
					allNotesValid = false;
					annotResponse.addErrorMessages("relatedNotes", ix, noteResponse.getErrorMessages());
				} else {
					if (CollectionUtils.isNotEmpty(dto.getNoteDtos().get(ix).getEvidenceCuries())) {
						for (String noteRef : dto.getNoteDtos().get(ix).getEvidenceCuries()) {
							if (!noteRef.equals(dto.getReferenceCurie())) {
								Map<String, String> noteRefErrorMessages = new HashMap<>();
								noteRefErrorMessages.put("evidence_curies", ValidationConstants.INVALID_MESSAGE + " (" + noteRef + ")");
								annotResponse.addErrorMessages("relatedNotes", ix, noteRefErrorMessages);
								allNotesValid = false;
							}
						}
					}
					String noteIdentity = NoteIdentityHelper.noteDtoIdentity(dto.getNoteDtos().get(ix));
					if (!noteIdentities.contains(noteIdentity)) {
						noteIdentities.add(noteIdentity);
						validatedNotes.add(noteDAO.persist(noteResponse.getEntity()));
					}
				}
			}
		}
		if (!allNotesValid) {
			annotResponse.convertMapToErrorMessages("relatedNotes");
		}
		if (CollectionUtils.isNotEmpty(validatedNotes) && allNotesValid) {
			if (annotation.getRelatedNotes() == null) {
				annotation.setRelatedNotes(new ArrayList<>());
			}
			annotation.getRelatedNotes().addAll(validatedNotes);
		}

		if (CollectionUtils.isNotEmpty(dto.getConditionRelationDtos())) {
			List<ConditionRelation> relations = new ArrayList<>();
			for (ConditionRelationDTO conditionRelationDTO : dto.getConditionRelationDtos()) {
				if (StringUtils.isNotBlank(conditionRelationDTO.getHandle())) {
					if (!StringUtils.equals(conditionRelationDTO.getReferenceCurie(), dto.getReferenceCurie())) {
						annotResponse.addErrorMessage("condition_relation_dtos - reference_curie", ValidationConstants.INVALID_MESSAGE + " (" + conditionRelationDTO.getReferenceCurie() + ")");
					}
				}
				ObjectResponse<ConditionRelation> crResponse = conditionRelationDtoValidator.validateConditionRelationDTO(conditionRelationDTO);
				if (crResponse.hasErrors()) {
					annotResponse.addErrorMessage("condition_relation_dtos", crResponse.errorMessagesString());
				} else {
					relations.add(conditionRelationDAO.persist(crResponse.getEntity()));
				}
			}
			annotation.setConditionRelations(relations);
		} else {
			annotation.setConditionRelations(null);
		}

		annotResponse.setEntity(annotation);

		return annotResponse;
	}

	public <E extends Annotation, D extends AnnotationDTO> ObjectResponse<E> validateReference(E annotation, D dto) {
		ObjectResponse<E> aResponse = new ObjectResponse<E>();

		Reference reference = null;
		if (StringUtils.isBlank(dto.getReferenceCurie())) {
			aResponse.addErrorMessage("reference_curie", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			reference = referenceService.retrieveFromDbOrLiteratureService(dto.getReferenceCurie());
			if (reference == null) {
				aResponse.addErrorMessage("reference_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getReferenceCurie() + ")");
			}
		}
		annotation.setSingleReference(reference);

		aResponse.setEntity(annotation);
		return aResponse;
	}


}
