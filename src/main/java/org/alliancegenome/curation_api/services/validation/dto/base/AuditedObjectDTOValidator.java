package org.alliancegenome.curation_api.services.validation.dto.base;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.ingest.dto.NoteDTO;
import org.alliancegenome.curation_api.model.ingest.dto.base.AuditedObjectDTO;
import org.alliancegenome.curation_api.services.PersonService;
import org.apache.commons.lang3.StringUtils;

import jakarta.inject.Inject;

public class AuditedObjectDTOValidator<E extends AuditedObject, D extends AuditedObjectDTO> extends BaseDTOValidator<E> {

	@Inject PersonService personService;

	public E validateAuditedObjectDTO(E entity, D dto) {

		Person createdBy = null;
		if (StringUtils.isNotBlank(dto.getCreatedByCurie())) {
			createdBy = personService.fetchByUniqueIdOrCreate(dto.getCreatedByCurie());
		}
		entity.setCreatedBy(createdBy);

		Person updatedBy = null;
		if (StringUtils.isNotBlank(dto.getUpdatedByCurie())) {
			updatedBy = personService.fetchByUniqueIdOrCreate(dto.getUpdatedByCurie());
		}
		entity.setUpdatedBy(updatedBy);

		Boolean internal = dto instanceof NoteDTO;
		if (dto.getInternal() != null) {
			internal = dto.getInternal();
		}
		entity.setInternal(internal);

		Boolean obsolete = false;
		if (dto.getObsolete() != null) {
			obsolete = dto.getObsolete();
		}
		entity.setObsolete(obsolete);

		OffsetDateTime dateUpdated = null;
		if (StringUtils.isNotBlank(dto.getDateUpdated())) {
			try {
				dateUpdated = OffsetDateTime.parse(dto.getDateUpdated());
			} catch (DateTimeParseException e) {
				response.addErrorMessage("date_updated", ValidationConstants.INVALID_MESSAGE + " (" + dto.getDateUpdated() + ")");
			}
		}
		entity.setDateUpdated(dateUpdated);

		OffsetDateTime creationDate = null;
		if (StringUtils.isNotBlank(dto.getDateCreated())) {
			try {
				creationDate = OffsetDateTime.parse(dto.getDateCreated());
			} catch (DateTimeParseException e) {
				response.addErrorMessage("date_created", ValidationConstants.INVALID_MESSAGE + " (" + dto.getDateCreated() + ")");
			}
		}
		entity.setDateCreated(creationDate);

		return entity;
	}

}
