package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.constructSlotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.NameSlotAnnotationDTOValidator;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class ConstructSymbolSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator<ConstructSymbolSlotAnnotation, NameSlotAnnotationDTO> {

	public ObjectResponse<ConstructSymbolSlotAnnotation> validateConstructSymbolSlotAnnotationDTO(ConstructSymbolSlotAnnotation annotation, NameSlotAnnotationDTO dto) {
		response = new ObjectResponse<ConstructSymbolSlotAnnotation>();
		
		if (annotation == null) {
			annotation = new ConstructSymbolSlotAnnotation();
		}

		annotation = validateNameSlotAnnotationDTO(annotation, dto, VocabularyConstants.SYMBOL_NAME_TYPE_TERM_SET);
		
		response.setEntity(annotation);
		return response;
	}
}