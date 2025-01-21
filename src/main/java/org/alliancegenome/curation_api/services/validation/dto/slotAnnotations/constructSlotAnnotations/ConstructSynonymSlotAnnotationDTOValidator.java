package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.constructSlotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.NameSlotAnnotationDTOValidator;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class ConstructSynonymSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator<ConstructSynonymSlotAnnotation, NameSlotAnnotationDTO> {

	public ObjectResponse<ConstructSynonymSlotAnnotation> validateConstructSynonymSlotAnnotationDTO(ConstructSynonymSlotAnnotation annotation, NameSlotAnnotationDTO dto) {
		response = new ObjectResponse<ConstructSynonymSlotAnnotation>();
		
		if (annotation == null) {
			annotation = new ConstructSynonymSlotAnnotation();
		}

		annotation = validateNameSlotAnnotationDTO(annotation, dto, VocabularyConstants.NAME_TYPE_VOCABULARY);
		
		response.setEntity(annotation);
		return response;
	}
}