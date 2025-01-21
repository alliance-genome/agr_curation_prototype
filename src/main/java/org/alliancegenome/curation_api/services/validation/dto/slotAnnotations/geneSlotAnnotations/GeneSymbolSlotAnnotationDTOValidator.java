package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.geneSlotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.NameSlotAnnotationDTOValidator;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class GeneSymbolSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator<GeneSymbolSlotAnnotation, NameSlotAnnotationDTO> {

	public ObjectResponse<GeneSymbolSlotAnnotation> validateGeneSymbolSlotAnnotationDTO(GeneSymbolSlotAnnotation annotation, NameSlotAnnotationDTO dto) {
		response = new ObjectResponse<GeneSymbolSlotAnnotation>();
		
		if (annotation == null) {
			annotation = new GeneSymbolSlotAnnotation();
		}

		annotation = validateNameSlotAnnotationDTO(annotation, dto, VocabularyConstants.SYMBOL_NAME_TYPE_TERM_SET);
		
		response.setEntity(annotation);
		return response;
	}
}