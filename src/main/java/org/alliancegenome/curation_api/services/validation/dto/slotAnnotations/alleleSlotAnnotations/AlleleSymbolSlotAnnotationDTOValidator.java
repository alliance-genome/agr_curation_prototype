package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.NameSlotAnnotationDTOValidator;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class AlleleSymbolSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator<AlleleSymbolSlotAnnotation, NameSlotAnnotationDTO> {

	public ObjectResponse<AlleleSymbolSlotAnnotation> validateAlleleSymbolSlotAnnotationDTO(AlleleSymbolSlotAnnotation annotation, NameSlotAnnotationDTO dto) {
		response = new ObjectResponse<AlleleSymbolSlotAnnotation>();
		
		if (annotation == null) {
			annotation = new AlleleSymbolSlotAnnotation();
		}

		annotation = validateNameSlotAnnotationDTO(annotation, dto, VocabularyConstants.SYMBOL_NAME_TYPE_TERM_SET);
		
		response.setEntity(annotation);
		return response;
	}
}