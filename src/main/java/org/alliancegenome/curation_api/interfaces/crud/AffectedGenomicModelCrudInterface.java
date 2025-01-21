package org.alliancegenome.curation_api.interfaces.crud;

import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseSubmittedObjectCrudInterface;
import org.alliancegenome.curation_api.interfaces.base.BaseUpsertControllerInterface;
import org.alliancegenome.curation_api.interfaces.base.crud.BaseReadIdentifierControllerInterface;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.ingest.dto.AffectedGenomicModelDTO;
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
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/agm")
@Tag(name = "CRUD - Affected Genomic Models")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AffectedGenomicModelCrudInterface extends BaseSubmittedObjectCrudInterface<AffectedGenomicModel>,
	BaseReadIdentifierControllerInterface<AffectedGenomicModel>,
	BaseUpsertControllerInterface<AffectedGenomicModel, AffectedGenomicModelDTO> {

	@POST
	@Path("/bulk/{dataProvider}/agms")
	@JsonView(View.FieldsAndLists.class)
	APIResponse updateAGMs(@PathParam("dataProvider") String dataProvider, List<AffectedGenomicModelDTO> agmData);

	@Override
	@GET
	@Path("/{identifierString}")
	@JsonView(View.AffectedGenomicModelDetailView.class)
	ObjectResponse<AffectedGenomicModel> getByIdentifier(@PathParam("identifierString") String identifierString);

	@Override
	@PUT
	@Path("/")
	@JsonView(View.AffectedGenomicModelView.class)
	ObjectResponse<AffectedGenomicModel> update(AffectedGenomicModel entity);
	
	@Override
	@POST
	@Path("/")
	@JsonView(View.AffectedGenomicModelView.class)
	ObjectResponse<AffectedGenomicModel> create(AffectedGenomicModel entity);

	@Override
	@POST
	@Path("/find")
	@Tag(name = "Relational Database Browsing Endpoints")
	@JsonView(View.AffectedGenomicModelView.class)
	SearchResponse<AffectedGenomicModel> find(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, @RequestBody HashMap<String, Object> params);

	@Override
	@POST
	@Path("/search")
	@Tag(name = "Elastic Search Browsing Endpoints")
	@JsonView({ View.AffectedGenomicModelView.class })
	SearchResponse<AffectedGenomicModel> search(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, @RequestBody HashMap<String, Object> params);

}
