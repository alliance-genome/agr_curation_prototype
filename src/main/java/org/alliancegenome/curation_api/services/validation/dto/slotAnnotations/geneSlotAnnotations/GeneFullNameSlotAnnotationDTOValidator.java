package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.geneSlotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.NameSlotAnnotationDTOValidator;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class GeneFullNameSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator<GeneFullNameSlotAnnotation, NameSlotAnnotationDTO> {

	public ObjectResponse<GeneFullNameSlotAnnotation> validateGeneFullNameSlotAnnotationDTO(GeneFullNameSlotAnnotation annotation, NameSlotAnnotationDTO dto) {
		response = new ObjectResponse<GeneFullNameSlotAnnotation>();
		
		if (annotation == null) {
			annotation = new GeneFullNameSlotAnnotation();
		}

		annotation = validateNameSlotAnnotationDTO(annotation, dto, VocabularyConstants.FULL_NAME_TYPE_TERM_SET);
		
		response.setEntity(annotation);
		return response;
	}
}