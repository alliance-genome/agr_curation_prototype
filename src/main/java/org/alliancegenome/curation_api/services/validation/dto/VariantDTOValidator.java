package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.VariantDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.ingest.dto.VariantDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.notes.NoteIdentityHelper;
import org.alliancegenome.curation_api.services.ontology.SoTermService;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class VariantDTOValidator extends BaseDTOValidator {

	@Inject VariantDAO variantDAO;
	@Inject NoteDAO noteDAO;
	@Inject NoteDTOValidator noteDtoValidator;
	@Inject VocabularyTermService vocabularyTermService;
	@Inject SoTermService soTermService;

	private ObjectResponse<Variant> variantResponse;

	@Transactional
	public Variant validateVariantDTO(VariantDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {

		variantResponse = new ObjectResponse<Variant>();

		Variant variant = null;
		if (StringUtils.isNotBlank(dto.getModEntityId())) {
			SearchResponse<Variant> response = variantDAO.findByField("modEntityId", dto.getModEntityId());
			if (response != null && response.getSingleResult() != null) {
				variant = response.getSingleResult();
			}
		}
		if (variant == null) {
			if (StringUtils.isBlank(dto.getModInternalId())) {
				if (StringUtils.isBlank(dto.getModEntityId())) {
					variantResponse.addErrorMessage("modInternalId", ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + "modEntityId");
				}
			} else {
				SearchResponse<Variant> response = variantDAO.findByField("modInternalId", dto.getModInternalId());
				if (response != null && response.getSingleResult() != null) {
					variant = response.getSingleResult();
				}
			}
		}

		if (variant == null) {
			variant = new Variant();
		}

		variant.setModEntityId(dto.getModEntityId());
		variant.setModInternalId(dto.getModInternalId());

		ObjectResponse<Variant> geResponse = validateGenomicEntityDTO(variant, dto, dataProvider);
		variantResponse.addErrorMessages(geResponse.getErrorMessages());

		variant = geResponse.getEntity();

		SOTerm variantType = null;
		if (StringUtils.isBlank(dto.getVariantTypeCurie())) {
			variantResponse.addErrorMessage("variant_type_curie", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			variantType = soTermService.findByCurieOrSecondaryId(dto.getVariantTypeCurie());
			if (variantType == null) {
				variantResponse.addErrorMessage("variant_type_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getVariantTypeCurie() + ")");
			}
		}
		variant.setVariantType(variantType);

		VocabularyTerm variantStatus = null;
		if (StringUtils.isNotBlank(dto.getVariantStatusName())) {
			variantStatus = vocabularyTermService.getTermInVocabulary(VocabularyConstants.VARIANT_STATUS_VOCABULARY, dto.getVariantStatusName()).getEntity();
			if (variantStatus == null) {
				variantResponse.addErrorMessage("variant_status_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getVariantStatusName() + ")");
			}
		}
		variant.setVariantStatus(variantStatus);

		SOTerm sourceGeneralConsequence = null;
		if (!StringUtils.isBlank(dto.getSourceGeneralConsequenceCurie())) {
			sourceGeneralConsequence = soTermService.findByCurieOrSecondaryId(dto.getSourceGeneralConsequenceCurie());
			if (sourceGeneralConsequence == null) {
				variantResponse.addErrorMessage("source_general_consequence_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getSourceGeneralConsequenceCurie() + ")");
			}
		}
		variant.setSourceGeneralConsequence(sourceGeneralConsequence);

		List<Note> relatedNotes = validateRelatedNotes(variant, dto);
		if (relatedNotes != null) {
			if (variant.getRelatedNotes() == null) {
				variant.setRelatedNotes(new ArrayList<>());
			}
			variant.getRelatedNotes().addAll(relatedNotes);
		}

		variantResponse.convertErrorMessagesToMap();

		if (variantResponse.hasErrors()) {
			throw new ObjectValidationException(dto, variantResponse.errorMessagesString());
		}

		variant = variantDAO.persist(variant);

		return variant;
	}

	private List<Note> validateRelatedNotes(Variant variant, VariantDTO dto) {
		String field = "relatedNotes";

		if (variant.getRelatedNotes() != null) {
			variant.getRelatedNotes().clear();
		}

		List<Note> validatedNotes = new ArrayList<Note>();
		List<String> noteIdentities = new ArrayList<String>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(dto.getNoteDtos())) {
			for (int ix = 0; ix < dto.getNoteDtos().size(); ix++) {
				ObjectResponse<Note> noteResponse = noteDtoValidator.validateNoteDTO(dto.getNoteDtos().get(ix), VocabularyConstants.VARIANT_NOTE_TYPES_VOCABULARY_TERM_SET);
				if (noteResponse.hasErrors()) {
					allValid = false;
					variantResponse.addErrorMessages(field, ix, noteResponse.getErrorMessages());
				} else {
					String noteIdentity = NoteIdentityHelper.noteDtoIdentity(dto.getNoteDtos().get(ix));
					if (!noteIdentities.contains(noteIdentity)) {
						noteIdentities.add(noteIdentity);
						validatedNotes.add(noteResponse.getEntity());
					}
				}
			}
		}

		if (!allValid) {
			variantResponse.convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedNotes)) {
			return null;
		}

		return validatedNotes;
	}

}
