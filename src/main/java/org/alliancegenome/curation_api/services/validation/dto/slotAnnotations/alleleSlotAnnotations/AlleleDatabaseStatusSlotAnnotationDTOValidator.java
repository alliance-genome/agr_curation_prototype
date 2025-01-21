package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleDatabaseStatusSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.alleleSlotAnnotations.AlleleDatabaseStatusSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.SlotAnnotationDTOValidator;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class AlleleDatabaseStatusSlotAnnotationDTOValidator extends SlotAnnotationDTOValidator<AlleleDatabaseStatusSlotAnnotation, AlleleDatabaseStatusSlotAnnotationDTO> {

	public ObjectResponse<AlleleDatabaseStatusSlotAnnotation> validateAlleleDatabaseStatusSlotAnnotationDTO(AlleleDatabaseStatusSlotAnnotation annotation, AlleleDatabaseStatusSlotAnnotationDTO dto) {
		response = new ObjectResponse<AlleleDatabaseStatusSlotAnnotation>();
		
		if (annotation == null) {
			annotation = new AlleleDatabaseStatusSlotAnnotation();
		}

		annotation = validateSlotAnnotationDTO(annotation, dto);
		
		VocabularyTerm databaseStatus = validateRequiredTermInVocabulary("database_status_name", dto.getDatabaseStatusName(), VocabularyConstants.ALLELE_DATABASE_STATUS_VOCABULARY);
		annotation.setDatabaseStatus(databaseStatus);

		response.setEntity(annotation);
		return response;
	}
}
