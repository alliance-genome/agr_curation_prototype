package org.alliancegenome.curation_api.services.validation.base;

import java.time.OffsetDateTime;
import java.util.List;

import org.alliancegenome.curation_api.auth.AuthenticatedUser;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.OrganizationDAO;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.CrossReferenceService;
import org.alliancegenome.curation_api.services.OrganizationService;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.validation.CrossReferenceValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.inject.Inject;

public class AuditedObjectValidator<E extends AuditedObject> {

	@Inject
	@AuthenticatedUser protected Person authenticatedPerson;

	@Inject PersonService personService;
	@Inject OrganizationDAO organizationDAO;
	@Inject OrganizationService organizationService;
	@Inject CrossReferenceDAO crossReferenceDAO;
	@Inject CrossReferenceService crossReferenceService;
	@Inject CrossReferenceValidator crossReferenceValidator;

	public ObjectResponse<E> response;

	public E validateAuditedObjectFields(E uiEntity, E dbEntity, Boolean newEntity) {
		Boolean defaultInternal = false;
		if (uiEntity instanceof Note) {
			defaultInternal = true;
		}

		Boolean internal = uiEntity.getInternal() == null ? defaultInternal : uiEntity.getInternal();
		dbEntity.setInternal(internal);

		Boolean obsolete = uiEntity.getObsolete() == null ? defaultInternal : uiEntity.getObsolete();
		dbEntity.setObsolete(obsolete);

		if (newEntity && uiEntity.getDateCreated() == null) {
			dbEntity.setDateCreated(OffsetDateTime.now());
		} else {
			dbEntity.setDateCreated(uiEntity.getDateCreated());
		}

		if (uiEntity.getCreatedBy() != null) {
			Person createdBy = personService.fetchByUniqueIdOrCreate(uiEntity.getCreatedBy().getUniqueId());
			createdBy.getEmails().size();
			createdBy.getOldEmails().size();
			dbEntity.setCreatedBy(createdBy);
		} else if (newEntity) {
			Person createdBy = personService.findPersonByOktaEmail(authenticatedPerson.getOktaEmail());
			createdBy.getEmails().size();
			createdBy.getOldEmails().size();
			dbEntity.setCreatedBy(createdBy);
		}

		Person updatedBy = personService.findPersonByOktaEmail(authenticatedPerson.getOktaEmail());
		dbEntity.setUpdatedBy(updatedBy);

		dbEntity.setDateUpdated(OffsetDateTime.now());

		return dbEntity;
	}

	public String handleStringField(String string) {
		if (!StringUtils.isBlank(string)) {
			return string;
		}
		return null;
	}

	public List<Object> handleListField(List<Object> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			return list;
		}
		return null;
	}

	public void addMessageResponse(String message) {
		response.setErrorMessage(message);
	}

	public void addMessageResponse(String fieldName, String message) {
		response.addErrorMessage(fieldName, message);
	}

	public void convertMapToErrorMessages(String fieldName) {
		response.convertMapToErrorMessages(fieldName);
	}

	public Organization validateDataProvider(Organization uiDataProvider, Organization dbDataProvider, boolean newEntity) {
		String field = "dataProvider";

		if (uiDataProvider == null) {
			if (newEntity) {
				return organizationDAO.getOrCreateOrganization("Alliance");
			} else {
				addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
				return null;
			}
		}
		
		Organization dataProvider = null;
		if (uiDataProvider.getId() != null) {
			dataProvider = organizationService.getById(uiDataProvider.getId()).getEntity();
		} else if (StringUtils.isNotBlank(uiDataProvider.getAbbreviation())) {
			dataProvider = organizationService.getByAbbr(uiDataProvider.getAbbreviation()).getEntity();
		}
		
		if (dataProvider == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (dataProvider.getObsolete() && (dbDataProvider == null || !dataProvider.getId().equals(dbDataProvider.getId()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return dataProvider;
	}
	
	protected CrossReference validateDataProviderCrossReference(CrossReference uiXref, CrossReference dbXref) {
		return validateDataProviderCrossReference(uiXref, dbXref, false);
	}

	protected CrossReference validateDataProviderCrossReference(CrossReference uiXref, CrossReference dbXref, boolean isSecondaryProvider) {
		String fieldName = isSecondaryProvider ? "secondaryDataProviderCrossReference" : "dataProviderCrossReference";
		
		CrossReference xref = null;
		String dbXrefUniqueId = null;
		String uiXrefUniqueId = null;
		if (dbXref != null) {
			dbXrefUniqueId = crossReferenceService.getCrossReferenceUniqueId(dbXref);
		}
		
		if (ObjectUtils.isNotEmpty(uiXref)) {
			ObjectResponse<CrossReference> xrefResponse = crossReferenceValidator.validateCrossReference(uiXref, false);
			if (xrefResponse.hasErrors()) {
				addMessageResponse(fieldName, xrefResponse.errorMessagesString());
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
