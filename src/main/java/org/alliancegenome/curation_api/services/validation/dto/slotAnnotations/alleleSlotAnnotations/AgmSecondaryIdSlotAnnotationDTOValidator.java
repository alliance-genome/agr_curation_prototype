package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations;

import jakarta.enterprise.context.RequestScoped;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.agmSlotAnnotations.AgmSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.SecondaryIdSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.SecondaryIdSlotAnnotationDTOValidator;

@RequestScoped
public class AgmSecondaryIdSlotAnnotationDTOValidator extends SecondaryIdSlotAnnotationDTOValidator {

	public ObjectResponse<AgmSecondaryIdSlotAnnotation> validateAgmSecondaryIdSlotAnnotationDTO(AgmSecondaryIdSlotAnnotation annotation, SecondaryIdSlotAnnotationDTO dto) {
		ObjectResponse<AgmSecondaryIdSlotAnnotation> asidResponse = new ObjectResponse<>();

		if (annotation == null) {
			annotation = new AgmSecondaryIdSlotAnnotation();
		}

		ObjectResponse<AgmSecondaryIdSlotAnnotation> saResponse = validateSecondaryIdSlotAnnotationDTO(annotation, dto);
		annotation = saResponse.getEntity();
		asidResponse.addErrorMessages(saResponse.getErrorMessages());

		asidResponse.setEntity(annotation);

		return asidResponse;
	}
}