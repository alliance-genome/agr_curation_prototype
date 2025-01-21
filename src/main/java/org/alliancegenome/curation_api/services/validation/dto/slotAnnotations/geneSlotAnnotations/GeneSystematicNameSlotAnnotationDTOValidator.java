package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.geneSlotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.NameSlotAnnotationDTOValidator;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class GeneSystematicNameSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator<GeneSystematicNameSlotAnnotation, NameSlotAnnotationDTO> {

	public ObjectResponse<GeneSystematicNameSlotAnnotation> validateGeneSystematicNameSlotAnnotationDTO(GeneSystematicNameSlotAnnotation annotation, NameSlotAnnotationDTO dto) {
		response = new ObjectResponse<GeneSystematicNameSlotAnnotation>();
		
		if (annotation == null) {
			annotation = new GeneSystematicNameSlotAnnotation();
		}

		annotation = validateNameSlotAnnotationDTO(annotation, dto, VocabularyConstants.SYSTEMATIC_NAME_TYPE_TERM_SET);
		
		response.setEntity(annotation);
		return response;
	}
}