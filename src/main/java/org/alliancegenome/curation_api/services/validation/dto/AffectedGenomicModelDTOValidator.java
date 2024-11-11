package org.alliancegenome.curation_api.services.validation.dto;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.AffectedGenomicModelDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AffectedGenomicModelDTOValidator extends BaseDTOValidator {

	@Inject AffectedGenomicModelDAO affectedGenomicModelDAO;
	@Inject VocabularyTermService vocabularyTermService;

	private ObjectResponse<AffectedGenomicModel> agmResponse = new ObjectResponse<AffectedGenomicModel>();

	public AffectedGenomicModel validateAffectedGenomicModelDTO(AffectedGenomicModelDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {

		AffectedGenomicModel agm = null;
		if (StringUtils.isNotBlank(dto.getModEntityId())) {
			SearchResponse<AffectedGenomicModel> response = affectedGenomicModelDAO.findByField("modEntityId", dto.getModEntityId());
			if (response != null && response.getSingleResult() != null) {
				agm = response.getSingleResult();
			}
		} else {
			agmResponse.addErrorMessage("modEntityId", ValidationConstants.REQUIRED_MESSAGE);
		}

		if (agm == null) {
			agm = new AffectedGenomicModel();
		}

		agm.setModEntityId(dto.getModEntityId());
		agm.setModInternalId(handleStringField(dto.getModInternalId()));
		agm.setName(handleStringField(dto.getName()));

		ObjectResponse<AffectedGenomicModel> geResponse = validateGenomicEntityDTO(agm, dto, dataProvider);
		agmResponse.addErrorMessages(geResponse.getErrorMessages());

		agm = geResponse.getEntity();

		VocabularyTerm subtype = null;
		if (StringUtils.isBlank(dto.getSubtypeName())) {
			agmResponse.addErrorMessage("subtype_name", ValidationConstants.REQUIRED_MESSAGE);

		} else {
			subtype = vocabularyTermService.getTermInVocabulary(VocabularyConstants.AGM_SUBTYPE_VOCABULARY, dto.getSubtypeName()).getEntity();
			if (subtype == null) {
				agmResponse.addErrorMessage("subtype_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getSubtypeName() + ")");
			}
		}
		agm.setSubtype(subtype);

		agmResponse.convertErrorMessagesToMap();

		if (agmResponse.hasErrors()) {
			throw new ObjectValidationException(dto, agmResponse.errorMessagesString());
		}

		return agm;
	}
}
