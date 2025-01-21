package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import java.util.Objects;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.NameSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.apache.commons.lang3.StringUtils;

import jakarta.inject.Inject;

public class NameSlotAnnotationDTOValidator<E extends NameSlotAnnotation, D extends NameSlotAnnotationDTO> extends SlotAnnotationDTOValidator<E, D> {

	@Inject VocabularyTermService vocabularyTermService;

	public E validateNameSlotAnnotationDTO(E annotation, D dto, String nameTypeVocabularyOrSet) {
		annotation = validateSlotAnnotationDTO(annotation, dto);

		if (StringUtils.isBlank(dto.getDisplayText())) {
			response.addErrorMessage("display_text", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			annotation.setDisplayText(dto.getDisplayText());
		}

		if (StringUtils.isBlank(dto.getFormatText())) {
			response.addErrorMessage("format_text", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			annotation.setFormatText(dto.getFormatText());
		}

		VocabularyTerm nameType = null;
		if (Objects.equals(VocabularyConstants.NAME_TYPE_VOCABULARY, nameTypeVocabularyOrSet)) {
			nameType = validateRequiredTermInVocabulary("name_type_name", dto.getNameTypeName(), VocabularyConstants.NAME_TYPE_VOCABULARY);
		} else {
			nameType = validateRequiredTermInVocabularyTermSet("name_type_name", dto.getNameTypeName(), nameTypeVocabularyOrSet);
		}
		annotation.setNameType(nameType);

		annotation.setSynonymUrl(handleStringField(dto.getSynonymUrl()));

		VocabularyTerm synonymScope = validateTermInVocabulary("synonym_scope_name", dto.getSynonymScopeName(), VocabularyConstants.SYNONYM_SCOPE_VOCABULARY);
		annotation.setSynonymScope(synonymScope);
		
		return annotation;
	}
}
