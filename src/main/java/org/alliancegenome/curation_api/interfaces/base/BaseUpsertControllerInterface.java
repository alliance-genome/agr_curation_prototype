package org.alliancegenome.curation_api.interfaces.base;

import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

public interface BaseUpsertControllerInterface<E extends AuditedObject, T extends BaseDTO> {

	@POST
	@Path("/upsert")
	@JsonView(View.FieldsOnly.class)
	E upsert(T dto) throws ValidationException;
}
