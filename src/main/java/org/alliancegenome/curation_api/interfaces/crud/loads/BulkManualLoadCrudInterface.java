package org.alliancegenome.curation_api.interfaces.crud.loads;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.interfaces.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/bulkmanualload")
@Tag(name = "Bulk Load - CRUD")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BulkManualLoadCrudInterface extends BaseIdCrudInterface<BulkManualLoad> {
    
    @GET
    @Path("/restart/{id}")
    @JsonView(View.FieldsOnly.class)
    public ObjectResponse<BulkManualLoad> restartLoad(@PathParam("id") Long id);
    
}
