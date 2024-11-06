package org.alliancegenome.curation_api.services.validation;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.DataProviderDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.CrossReferenceService;
import org.alliancegenome.curation_api.services.OrganizationService;
import org.alliancegenome.curation_api.services.validation.base.AuditedObjectValidator;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class DataProviderValidator extends AuditedObjectValidator<DataProvider> {

	@Inject DataProviderDAO dataProviderDAO;
	@Inject OrganizationService organizationService;
	@Inject CrossReferenceValidator crossReferenceValidator;
	@Inject CrossReferenceService crossReferenceService;
	@Inject CrossReferenceDAO crossReferenceDAO;

	public ObjectResponse<DataProvider> validateDataProvider(DataProvider uiEntity, DataProvider dbEntity, Boolean throwError) {
		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update DataProvider: [" + uiEntity.getId() + "]";

		if (dbEntity == null) {
			dbEntity = new DataProvider();
		}

		Boolean newEntity = true;
		if (uiEntity.getId() != null) {
			dbEntity = dataProviderDAO.find(uiEntity.getId());
			newEntity = false;
		} else {
			dbEntity = new DataProvider();
		}

		dbEntity = validateAuditedObjectFields(uiEntity, dbEntity, newEntity);

		Organization sourceOrganization = validateSourceOrganization(uiEntity, dbEntity);
		dbEntity.setSourceOrganization(sourceOrganization);

		String dbXrefUniqueId = null;
		String uiXrefUniqueId = null;
		Long dbXrefId = null;
		if (dbEntity.getCrossReference() != null) {
			dbXrefUniqueId = crossReferenceService.getCrossReferenceUniqueId(dbEntity.getCrossReference());
			dbXrefId = dbEntity.getCrossReference().getId();
		}

		if (uiEntity.getCrossReference() != null) {
			ObjectResponse<CrossReference> xrefResponse = crossReferenceValidator.validateCrossReference(uiEntity.getCrossReference(), false);
			if (xrefResponse.hasErrors()) {
				addMessageResponse("crossReference", xrefResponse.errorMessagesString());
			} else {
				uiXrefUniqueId = crossReferenceService.getCrossReferenceUniqueId(xrefResponse.getEntity());
				if (dbXrefUniqueId == null || !dbXrefUniqueId.equals(uiXrefUniqueId)) {
					dbEntity.setCrossReference(crossReferenceDAO.persist(xrefResponse.getEntity()));
				} else if (dbXrefUniqueId != null && dbXrefUniqueId.equals(uiXrefUniqueId)) {
					dbEntity.setCrossReference(crossReferenceService.updateCrossReference(dbEntity.getCrossReference(), uiEntity.getCrossReference()));
				}
			}
		} else {
			dbEntity.setCrossReference(null);
		}

		if (response.hasErrors()) {
			if (throwError) {
				response.setErrorMessage(errorTitle);
				throw new ApiErrorException(response);
			}
			return response;
		} else {
			if (dbXrefId != null && (uiXrefUniqueId == null || !dbXrefUniqueId.equals(uiXrefUniqueId))) {
				crossReferenceDAO.remove(dbXrefId);
			}

			if (dbEntity.getId() == null) {
				dbEntity = dataProviderDAO.persist(dbEntity);
			}
		}

		response.setEntity(dbEntity);

		return response;
	}

	private Organization validateSourceOrganization(DataProvider uiEntity, DataProvider dbEntity) {
		String field = "sourceOrganization";

		if (uiEntity.getSourceOrganization() == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		Organization sourceOrganization = organizationService.getById(uiEntity.getSourceOrganization().getId()).getEntity();
		if (sourceOrganization == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (sourceOrganization.getObsolete() && (dbEntity.getSourceOrganization() == null || !sourceOrganization.getId().equals(dbEntity.getSourceOrganization().getId()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return sourceOrganization;
	}
}