package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.SecondaryIdSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.SecondaryIdSlotAnnotationDTOValidator;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class AlleleSecondaryIdSlotAnnotationDTOValidator extends SecondaryIdSlotAnnotationDTOValidator {

	public ObjectResponse<AlleleSecondaryIdSlotAnnotation> validateAlleleSecondaryIdSlotAnnotationDTO(AlleleSecondaryIdSlotAnnotation annotation, SecondaryIdSlotAnnotationDTO dto) {
		ObjectResponse<AlleleSecondaryIdSlotAnnotation> asidResponse = new ObjectResponse<>();

		if (annotation == null) {
			annotation = new AlleleSecondaryIdSlotAnnotation();
		}

		ObjectResponse<AlleleSecondaryIdSlotAnnotation> saResponse = validateSecondaryIdSlotAnnotationDTO(annotation, dto);
		annotation = saResponse.getEntity();
		asidResponse.addErrorMessages(saResponse.getErrorMessages());

		asidResponse.setEntity(annotation);

		return asidResponse;
	}
}