package org.alliancegenome.curation_api.services.validation.base;

import java.time.OffsetDateTime;

import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;

public class AuditedObjectValidator<E extends AuditedObject> extends BaseValidator<E> {

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

}
