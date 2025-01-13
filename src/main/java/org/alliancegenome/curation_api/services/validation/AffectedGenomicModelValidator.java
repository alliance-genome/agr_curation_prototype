package org.alliancegenome.curation_api.services.validation;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.collections4.CollectionUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AffectedGenomicModelValidator extends GenomicEntityValidator<AffectedGenomicModel> {

	@Inject
	AffectedGenomicModelDAO affectedGenomicModelDAO;
	
	private String errorMessage;

	public AffectedGenomicModel validateAffectedGenomicModelUpdate(AffectedGenomicModel uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update AGM: [" + uiEntity.getIdentifier() + "]";

		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No AGM ID provided");
			throw new ApiErrorException(response);
		}

		AffectedGenomicModel dbEntity = affectedGenomicModelDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("id", ValidationConstants.INVALID_MESSAGE);
			throw new ApiErrorException(response);
		}

		dbEntity = (AffectedGenomicModel) validateAuditedObjectFields(uiEntity, dbEntity, false);

		return validateAffectedGenomicModel(uiEntity, dbEntity);
	}

	public AffectedGenomicModel validateAffectedGenomicModelCreate(AffectedGenomicModel uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not create AGM";

		AffectedGenomicModel dbEntity = new AffectedGenomicModel();

		dbEntity = (AffectedGenomicModel) validateAuditedObjectFields(uiEntity, dbEntity, true);

		return validateAffectedGenomicModel(uiEntity, dbEntity);
	}

	private AffectedGenomicModel validateAffectedGenomicModel(AffectedGenomicModel uiEntity, AffectedGenomicModel dbEntity) {

		dbEntity = validateGenomicEntityFields(uiEntity, dbEntity);

		String name = handleStringField(uiEntity.getName());
		dbEntity.setName(name);

		VocabularyTerm subtype = validateRequiredTermInVocabulary("subtype", VocabularyConstants.AGM_SUBTYPE_VOCABULARY, dbEntity.getSubtype(), uiEntity.getSubtype());
		dbEntity.setSubtype(subtype);

		if (CollectionUtils.isNotEmpty(uiEntity.getSynonyms())) {
			dbEntity.setSynonyms(uiEntity.getSynonyms());
		} else {
			dbEntity.setSynonyms(null);
		}

		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		return dbEntity;
	}

}
