package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.geneSlotAnnotations;

import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.SecondaryIdSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.SecondaryIdSlotAnnotationDTOValidator;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class GeneSecondaryIdSlotAnnotationDTOValidator extends SecondaryIdSlotAnnotationDTOValidator<GeneSecondaryIdSlotAnnotation, SecondaryIdSlotAnnotationDTO> {

	public ObjectResponse<GeneSecondaryIdSlotAnnotation> validateGeneSecondaryIdSlotAnnotationDTO(GeneSecondaryIdSlotAnnotation annotation, SecondaryIdSlotAnnotationDTO dto) {
		response = new ObjectResponse<GeneSecondaryIdSlotAnnotation>();
		
		if (annotation == null) {
			annotation = new GeneSecondaryIdSlotAnnotation();
		}

		annotation = (GeneSecondaryIdSlotAnnotation) validateSecondaryIdSlotAnnotationDTO(annotation, dto);
		
		response.setEntity(annotation);

		return response;
	}
}