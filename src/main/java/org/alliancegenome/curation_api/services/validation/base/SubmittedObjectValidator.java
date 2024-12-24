package org.alliancegenome.curation_api.services.validation.base;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.base.SubmittedObject;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.DataProviderService;
import org.alliancegenome.curation_api.services.validation.DataProviderValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.inject.Inject;

public class SubmittedObjectValidator<E extends SubmittedObject> extends AuditedObjectValidator<SubmittedObject> {

	@Inject DataProviderService dataProviderService;
	@Inject DataProviderValidator dataProviderValidator;

	public E validateSubmittedObjectFields(E uiEntity, E dbEntity) {
		String curie = handleStringField(uiEntity.getCurie());
		dbEntity.setCurie(curie);

		String primaryExternalId = handleStringField(uiEntity.getPrimaryExternalId());
		dbEntity.setPrimaryExternalId(primaryExternalId);

		String modInternalId = validateModInternalId(uiEntity);
		dbEntity.setModInternalId(modInternalId);

		DataProvider dataProvider = validateDataProvider(uiEntity, dbEntity);
		dbEntity.setDataProvider(dataProvider);

		return dbEntity;
	}

	public String validateCurie(E uiEntity) {
		String curie = handleStringField(uiEntity.getCurie());
		if (curie != null && !curie.startsWith("AGRKB:")) {
			addMessageResponse("curie", ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		return curie;
	}

	public String validateModInternalId(E uiEntity) {
		String modInternalId = uiEntity.getModInternalId();
		if (StringUtils.isBlank(modInternalId)) {
			if (StringUtils.isBlank(uiEntity.getPrimaryExternalId())) {
				addMessageResponse("modInternalId", ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + "primaryExternalId");
			}
			return null;
		}
		return modInternalId;
	}

	public DataProvider validateDataProvider(E uiEntity, E dbEntity) {
		String field = "dataProvider";
		if (uiEntity.getDataProvider() == null) {
			if (dbEntity.getDataProvider() == null) {
				uiEntity.setDataProvider(dataProviderService.createAffiliatedModDataProvider());
			}
			if (uiEntity.getDataProvider() == null) {
				addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
				return null;
			}
		}

		DataProvider uiDataProvider = uiEntity.getDataProvider();
		DataProvider dbDataProvider = dbEntity.getDataProvider();

		ObjectResponse<DataProvider> dpResponse = dataProviderValidator.validateDataProvider(uiDataProvider, dbDataProvider, false);
		if (dpResponse.hasErrors()) {
			addMessageResponse(field, dpResponse.errorMessagesString());
			return null;
		}

		DataProvider validatedDataProvider = dpResponse.getEntity();
		if (validatedDataProvider.getObsolete() && (dbDataProvider == null || !validatedDataProvider.getId().equals(dbDataProvider.getId()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return validatedDataProvider;
	}
}
