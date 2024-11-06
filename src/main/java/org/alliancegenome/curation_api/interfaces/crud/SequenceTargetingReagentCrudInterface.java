package org.alliancegenome.curation_api.interfaces.crud;

import java.util.HashMap;

import org.alliancegenome.curation_api.interfaces.base.BaseSubmittedObjectCrudInterface;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.ingest.dto.fms.SequenceTargetingReagentIngestFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/sqtr")
@Tag(name = "CRUD - SQTR")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface SequenceTargetingReagentCrudInterface extends BaseSubmittedObjectCrudInterface<SequenceTargetingReagent> {

	@POST
	@Path("/bulk/{dataProvider}/sqtrfile")
	@JsonView(View.FieldsAndLists.class)
	APIResponse updateSequenceTargetingReagent(@PathParam("dataProvider") String dataProvider, SequenceTargetingReagentIngestFmsDTO sqtrData);

	@Override
	@POST
	@Path("/find")
	@Tag(name = "Relational Database Browsing Endpoints")
	@JsonView(View.SequenceTargetingReagentView.class)
	SearchResponse<SequenceTargetingReagent> find(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, @RequestBody HashMap<String, Object> params);

	@Override
	@POST
	@Path("/search")
	@Tag(name = "Elastic Search Browsing Endpoints")
	@JsonView({ View.SequenceTargetingReagentView.class })
	SearchResponse<SequenceTargetingReagent> search(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, @RequestBody HashMap<String, Object> params);

	@Override
	@GET
	@Path("/{identifierString}")
	@JsonView(View.SequenceTargetingReagentDetailView.class)
	ObjectResponse<SequenceTargetingReagent> getByIdentifier(@PathParam("identifierString") String identifierString);
}
