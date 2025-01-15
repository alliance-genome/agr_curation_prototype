package org.alliancegenome.curation_api.services.validation;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.base.AuditedObjectValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class PersonValidator extends AuditedObjectValidator<Person> {

	@Inject PersonDAO personDAO;

	public Person validatePerson(Person uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not update Person: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No Person ID provided");
			throw new ApiErrorException(response);
		}
		Person dbEntity = personDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("Could not find Person with ID: [" + id + "]");
			throw new ApiErrorException(response);
		}

		dbEntity = (Person) validateAuditedObjectFields(uiEntity, dbEntity, false);

		String uniqueId = validateUniqueId(uiEntity);
		dbEntity.setUniqueId(uniqueId);

		dbEntity.setFirstName(handleStringField(uiEntity.getFirstName()));
		dbEntity.setMiddleName(handleStringField(uiEntity.getMiddleName()));
		dbEntity.setLastName(handleStringField(uiEntity.getLastName()));
		dbEntity.setOrcid(handleStringField(uiEntity.getOrcid()));
		dbEntity.setPrimaryExternalId(handleStringField(uiEntity.getPrimaryExternalId()));

		if (CollectionUtils.isNotEmpty(uiEntity.getEmails())) {
			dbEntity.setEmails(uiEntity.getEmails());
		} else {
			dbEntity.setEmails(null);
		}

		if (CollectionUtils.isNotEmpty(uiEntity.getOldEmails())) {
			dbEntity.setOldEmails(uiEntity.getOldEmails());
		} else {
			dbEntity.setOldEmails(null);
		}

		if (response.hasErrors()) {
			response.setErrorMessage(errorTitle);
			throw new ApiErrorException(response);
		}

		return dbEntity;
	}

	public String validateUniqueId(Person uiEntity) {
		String field = "uniqueId";
		if (StringUtils.isBlank(uiEntity.getUniqueId())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		return uiEntity.getUniqueId();
	}

}
