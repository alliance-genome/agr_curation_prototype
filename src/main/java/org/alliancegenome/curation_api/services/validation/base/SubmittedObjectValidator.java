package org.alliancegenome.curation_api.services.validation.base;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.OrganizationDAO;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.base.SubmittedObject;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.CrossReferenceService;
import org.alliancegenome.curation_api.services.OrganizationService;
import org.alliancegenome.curation_api.services.validation.CrossReferenceValidator;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.inject.Inject;

public class SubmittedObjectValidator<E extends SubmittedObject> extends AuditedObjectValidator<SubmittedObject> {

	@Inject CrossReferenceService crossReferenceService;
	@Inject CrossReferenceValidator crossReferenceValidator;
	@Inject CrossReferenceDAO crossReferenceDAO;
	@Inject OrganizationService organizationService;
	@Inject OrganizationDAO organizationDAO;

	public E validateSubmittedObjectFields(E uiEntity, E dbEntity) {
		String curie = handleStringField(uiEntity.getCurie());
		dbEntity.setCurie(curie);

		String primaryExternalId = handleStringField(uiEntity.getPrimaryExternalId());
		dbEntity.setPrimaryExternalId(primaryExternalId);

		String modInternalId = validateModInternalId(uiEntity);
		dbEntity.setModInternalId(modInternalId);

		Organization dataProvider = validateDataProvider(uiEntity, dbEntity);
		dbEntity.setDataProvider(dataProvider);
		
		CrossReference dataProviderXref = validateDataProviderCrossReference(uiEntity.getDataProviderCrossReference(), dbEntity.getDataProviderCrossReference());
		dbEntity.setDataProviderCrossReference(dataProviderXref);
		
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

	public Organization validateDataProvider(E uiEntity, E dbEntity) {
		String field = "dataProvider";

		if (ObjectUtils.isEmpty(uiEntity.getDataProvider())) {
			if (dbEntity.getId() == null) {
				return organizationDAO.getOrCreateOrganization("Alliance");
			} else {
				addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
				return null;
			}
		}
		
		Organization dataProvider = null;
		if (uiEntity.getDataProvider().getId() != null) {
			dataProvider = organizationService.getById(uiEntity.getDataProvider().getId()).getEntity();
		} else if (StringUtils.isNotBlank(uiEntity.getDataProvider().getAbbreviation())) {
			dataProvider = organizationService.getByAbbr(uiEntity.getDataProvider().getAbbreviation()).getEntity();
		}
		
		if (dataProvider == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (dataProvider.getObsolete() && (dbEntity.getDataProvider() == null || !dataProvider.getId().equals(dbEntity.getDataProvider().getId()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return dataProvider;
	}

	protected CrossReference validateDataProviderCrossReference(CrossReference uiXref, CrossReference dbXref) {
		CrossReference xref = null;
		String dbXrefUniqueId = null;
		String uiXrefUniqueId = null;
		if (dbXref != null) {
			dbXrefUniqueId = crossReferenceService.getCrossReferenceUniqueId(dbXref);
		}
		
		if (ObjectUtils.isNotEmpty(uiXref)) {
			ObjectResponse<CrossReference> xrefResponse = crossReferenceValidator.validateCrossReference(uiXref, false);
			if (xrefResponse.hasErrors()) {
				addMessageResponse("crossReference", xrefResponse.errorMessagesString());
			} else {
				uiXrefUniqueId = crossReferenceService.getCrossReferenceUniqueId(xrefResponse.getEntity());
				if (dbXrefUniqueId == null || !dbXrefUniqueId.equals(uiXrefUniqueId)) {
					xref = crossReferenceDAO.persist(xrefResponse.getEntity());
				} else if (dbXrefUniqueId != null && dbXrefUniqueId.equals(uiXrefUniqueId)) {
					xref = crossReferenceService.updateCrossReference(dbXref, uiXref);
				}
			}
		}
		
		return xref;
	}
}
