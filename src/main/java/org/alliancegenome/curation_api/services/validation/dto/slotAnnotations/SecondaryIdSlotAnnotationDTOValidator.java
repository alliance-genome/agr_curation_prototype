package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.SecondaryIdSlotAnnotationDTO;
import org.apache.commons.lang3.StringUtils;

public class SecondaryIdSlotAnnotationDTOValidator<E extends SecondaryIdSlotAnnotation, D extends SecondaryIdSlotAnnotationDTO> extends SlotAnnotationDTOValidator <E, D>{

	public SecondaryIdSlotAnnotation validateSecondaryIdSlotAnnotationDTO(E annotation, D dto) {
		annotation = validateSlotAnnotationDTO(annotation, dto);

		if (StringUtils.isBlank(dto.getSecondaryId())) {
			response.addErrorMessage("secondary_id", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			annotation.setSecondaryId(dto.getSecondaryId());
		}

		return annotation;
	}
}
