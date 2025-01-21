package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.alleleSlotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.SlotAnnotationDTOValidator;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class AlleleGermlineTransmissionStatusSlotAnnotationDTOValidator extends SlotAnnotationDTOValidator<AlleleGermlineTransmissionStatusSlotAnnotation, AlleleGermlineTransmissionStatusSlotAnnotationDTO> {

	public ObjectResponse<AlleleGermlineTransmissionStatusSlotAnnotation> validateAlleleGermlineTransmissionStatusSlotAnnotationDTO(AlleleGermlineTransmissionStatusSlotAnnotation annotation, AlleleGermlineTransmissionStatusSlotAnnotationDTO dto) {
		response = new ObjectResponse<AlleleGermlineTransmissionStatusSlotAnnotation>();
		
		if (annotation == null) {
			annotation = new AlleleGermlineTransmissionStatusSlotAnnotation();
		}

		annotation = validateSlotAnnotationDTO(annotation, dto);
		
		VocabularyTerm gts = validateRequiredTermInVocabulary("germline_transmission_status_name", dto.getGermlineTransmissionStatusName(), VocabularyConstants.GERMLINE_TRANSMISSION_STATUS_VOCABULARY);
		annotation.setGermlineTransmissionStatus(gts);
	
		response.setEntity(annotation);

		return response;
	}
}
