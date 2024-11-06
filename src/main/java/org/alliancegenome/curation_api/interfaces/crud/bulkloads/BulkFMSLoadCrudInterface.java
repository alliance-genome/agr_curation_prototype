package org.alliancegenome.curation_api.interfaces.crud.bulkloads;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/bulkfmsload")
@Tag(name = "Bulk Load - CRUD")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BulkFMSLoadCrudInterface extends BaseIdCrudInterface<BulkFMSLoad> {
	
}
