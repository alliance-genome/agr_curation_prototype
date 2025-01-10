package org.alliancegenome.curation_api.services.validation;

import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.OrganizationDAO;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.Reagent;
import org.alliancegenome.curation_api.services.OrganizationService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.services.validation.base.SubmittedObjectValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.inject.Inject;

public class ReagentValidator extends SubmittedObjectValidator<Reagent> {

	@Inject NcbiTaxonTermService ncbiTaxonTermService;
	@Inject OrganizationService organizationService;
	@Inject OrganizationDAO organizationDAO;

	public Reagent validateCommonReagentFields(Reagent uiEntity, Reagent dbEntity) {

		Boolean newEntity = false;
		if (dbEntity.getId() == null) {
			newEntity = true;
		}
		dbEntity = (Reagent) validateAuditedObjectFields(uiEntity, dbEntity, newEntity);

		String primaryExternalId = StringUtils.isNotBlank(uiEntity.getPrimaryExternalId()) ? uiEntity.getPrimaryExternalId() : null;
		dbEntity.setPrimaryExternalId(primaryExternalId);

		String modInternalId = validateModInternalId(uiEntity);
		dbEntity.setModInternalId(modInternalId);

		List<String> secondaryIds = CollectionUtils.isNotEmpty(uiEntity.getSecondaryIdentifiers()) ? uiEntity.getSecondaryIdentifiers() : null;
		dbEntity.setSecondaryIdentifiers(secondaryIds);

		Organization dataProvider = validateDataProvider(uiEntity, dbEntity);
		dbEntity.setDataProvider(dataProvider);

		return dbEntity;
	}

	public Organization validateDataProvider(Reagent uiEntity, Reagent dbEntity) {
		String field = "dataProvider";

		if (uiEntity.getDataProvider() == null) {
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
}
