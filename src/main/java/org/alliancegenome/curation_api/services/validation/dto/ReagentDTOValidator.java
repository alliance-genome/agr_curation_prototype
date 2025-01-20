package org.alliancegenome.curation_api.services.validation.dto;

import org.alliancegenome.curation_api.model.entities.Reagent;
import org.alliancegenome.curation_api.model.ingest.dto.ReagentDTO;
import org.alliancegenome.curation_api.services.validation.dto.base.SubmittedObjectDTOValidator;

public class ReagentDTOValidator <E extends Reagent, D extends ReagentDTO> extends SubmittedObjectDTOValidator<E, D> {

	public E validateReagentDTO(E reagent, D dto) {
		reagent = validateSubmittedObjectDTO(reagent, dto);
		
		reagent.setSecondaryIdentifiers(handleStringListField(dto.getSecondaryIdentifiers()));

		return reagent;
	}
}
