package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.geneSlotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.NameSlotAnnotationDTOValidator;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class GeneSynonymSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator<GeneSynonymSlotAnnotation, NameSlotAnnotationDTO> {

	public ObjectResponse<GeneSynonymSlotAnnotation> validateGeneSynonymSlotAnnotationDTO(GeneSynonymSlotAnnotation annotation, NameSlotAnnotationDTO dto) {
		response = new ObjectResponse<GeneSynonymSlotAnnotation>();
		
		if (annotation == null) {
			annotation = new GeneSynonymSlotAnnotation();
		}

		annotation = validateNameSlotAnnotationDTO(annotation, dto, VocabularyConstants.NAME_TYPE_VOCABULARY);
		
		response.setEntity(annotation);
		return response;
	}
}