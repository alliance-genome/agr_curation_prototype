package org.alliancegenome.curation_api.interfaces.crud;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.ingest.dto.fms.BiogridOrcIngestFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.websocket.server.PathParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/cross-reference")
@Tag(name = "CRUD - Cross References")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface CrossReferenceCrudInterface extends BaseIdCrudInterface<CrossReference> {

	@POST
	@Path("/bulk/{dataProvider}/biogridfile")
	@JsonView(View.FieldsAndLists.class)
	APIResponse updateBiogridOrc(@PathParam("dataProvider") String dataProvider, BiogridOrcIngestFmsDTO biogridOrcData);
}