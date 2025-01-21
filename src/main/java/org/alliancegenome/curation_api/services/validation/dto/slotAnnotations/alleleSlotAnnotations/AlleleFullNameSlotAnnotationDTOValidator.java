package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.NameSlotAnnotationDTOValidator;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class AlleleFullNameSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator<AlleleFullNameSlotAnnotation, NameSlotAnnotationDTO> {

	public ObjectResponse<AlleleFullNameSlotAnnotation> validateAlleleFullNameSlotAnnotationDTO(AlleleFullNameSlotAnnotation annotation, NameSlotAnnotationDTO dto) {
		response = new ObjectResponse<AlleleFullNameSlotAnnotation>();
		
		if (annotation == null) {
			annotation = new AlleleFullNameSlotAnnotation();
		}

		annotation = validateNameSlotAnnotationDTO(annotation, dto, VocabularyConstants.FULL_NAME_TYPE_TERM_SET);
		
		response.setEntity(annotation);
		return response;
	}
}