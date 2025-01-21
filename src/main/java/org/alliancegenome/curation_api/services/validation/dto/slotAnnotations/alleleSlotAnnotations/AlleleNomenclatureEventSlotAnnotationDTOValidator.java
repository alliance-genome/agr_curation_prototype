package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleNomenclatureEventSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.alleleSlotAnnotations.AlleleNomenclatureEventSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.SlotAnnotationDTOValidator;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class AlleleNomenclatureEventSlotAnnotationDTOValidator extends SlotAnnotationDTOValidator<AlleleNomenclatureEventSlotAnnotation, AlleleNomenclatureEventSlotAnnotationDTO> {

	public ObjectResponse<AlleleNomenclatureEventSlotAnnotation> validateAlleleNomenclatureEventSlotAnnotationDTO(AlleleNomenclatureEventSlotAnnotation annotation, AlleleNomenclatureEventSlotAnnotationDTO dto) {
		response = new ObjectResponse<AlleleNomenclatureEventSlotAnnotation>();
		
		if (annotation == null) {
			annotation = new AlleleNomenclatureEventSlotAnnotation();
		}

		annotation = validateSlotAnnotationDTO(annotation, dto);
		
		VocabularyTerm nomenclatureEvent = validateRequiredTermInVocabulary("nomenclature_event_name", dto.getNomenclatureEventName(), VocabularyConstants.ALLELE_NOMENCLATURE_EVENT_VOCABULARY);
		annotation.setNomenclatureEvent(nomenclatureEvent);

		response.setEntity(annotation);

		return response;
	}
}
