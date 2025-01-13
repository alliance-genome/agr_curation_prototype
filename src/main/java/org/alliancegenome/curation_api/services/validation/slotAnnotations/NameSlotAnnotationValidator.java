package org.alliancegenome.curation_api.services.validation.slotAnnotations;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.NameSlotAnnotation;
import org.apache.commons.lang3.StringUtils;

public class NameSlotAnnotationValidator<E extends NameSlotAnnotation> extends SlotAnnotationValidator<E> {

	public E validateNameSlotAnnotationFields(E uiEntity, E dbEntity, Boolean newEntity) {

		dbEntity = validateSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		if (StringUtils.isBlank(uiEntity.getDisplayText())) {
			addMessageResponse("displayText", ValidationConstants.REQUIRED_MESSAGE);
			dbEntity.setDisplayText(null);
		} else {
			dbEntity.setDisplayText(uiEntity.getDisplayText());
		}

		if (StringUtils.isBlank(uiEntity.getFormatText())) {
			addMessageResponse("formatText", ValidationConstants.REQUIRED_MESSAGE);
			dbEntity.setFormatText(null);
		} else {
			dbEntity.setFormatText(uiEntity.getFormatText());
		}

		if (StringUtils.isBlank(uiEntity.getSynonymUrl())) {
			dbEntity.setSynonymUrl(null);
		} else {
			dbEntity.setSynonymUrl(uiEntity.getSynonymUrl());
		}

		VocabularyTerm synonymScope = validateTermInVocabulary("synonymScope", VocabularyConstants.SYNONYM_SCOPE_VOCABULARY, dbEntity.getSynonymScope(), uiEntity.getSynonymScope());
		dbEntity.setSynonymScope(synonymScope);

		return dbEntity;
	}

}
