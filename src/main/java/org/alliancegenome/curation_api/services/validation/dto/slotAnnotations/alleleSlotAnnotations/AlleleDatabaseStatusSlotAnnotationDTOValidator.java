package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleDatabaseStatusSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.alleleSlotAnnotations.AlleleDatabaseStatusSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.SlotAnnotationDTOValidator;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class AlleleDatabaseStatusSlotAnnotationDTOValidator extends SlotAnnotationDTOValidator {

	@Inject
	VocabularyTermDAO vocabularyTermDAO;

	public ObjectResponse<AlleleDatabaseStatusSlotAnnotation> validateAlleleDatabaseStatusSlotAnnotationDTO(AlleleDatabaseStatusSlotAnnotation annotation, AlleleDatabaseStatusSlotAnnotationDTO dto) {
		ObjectResponse<AlleleDatabaseStatusSlotAnnotation> adsResponse = new ObjectResponse<AlleleDatabaseStatusSlotAnnotation>();

		if (annotation == null)
			annotation = new AlleleDatabaseStatusSlotAnnotation();

		VocabularyTerm databaseStatus = null;
		if (StringUtils.isBlank(dto.getDatabaseStatusName())) {
			adsResponse.addErrorMessage("database_status_name", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			databaseStatus = vocabularyTermDAO.getTermInVocabulary(VocabularyConstants.ALLELE_DATABASE_STATUS_VOCABULARY, dto.getDatabaseStatusName());
			if (databaseStatus == null)
				adsResponse.addErrorMessage("database_status_name", ValidationConstants.INVALID_MESSAGE);
		}
		annotation.setDatabaseStatus(databaseStatus);
	
		ObjectResponse<AlleleDatabaseStatusSlotAnnotation> saResponse = validateSlotAnnotationDTO(annotation, dto);
		annotation = saResponse.getEntity();
		adsResponse.addErrorMessages(saResponse.getErrorMessages());

		adsResponse.setEntity(annotation);

		return adsResponse;
	}
}