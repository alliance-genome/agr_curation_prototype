package org.alliancegenome.curation_api.interfaces.crud;

import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseSubmittedObjectCrudInterface;
import org.alliancegenome.curation_api.interfaces.base.BaseUpsertControllerInterface;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.ingest.dto.VariantDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.VariantFmsDTO;
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

@Path("/variant")
@Tag(name = "CRUD - Variants")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface VariantCrudInterface extends BaseSubmittedObjectCrudInterface<Variant>, BaseUpsertControllerInterface<Variant, VariantDTO> {

	@POST
	@Path("/bulk/{dataProvider}/variants")
	@JsonView(View.FieldsAndLists.class)
	APIResponse updateVariants(@PathParam("dataProvider") String dataProvider, List<VariantDTO> alleleData);

	@POST
	@Path("/bulk/{dataProvider}/fmsvariants")
	@JsonView(View.FieldsAndLists.class)
	APIResponse updateFmsVariants(@PathParam("dataProvider") String dataProvider, List<VariantFmsDTO> alleleData);

	@Override
	@GET
	@JsonView(View.VariantDetailView.class)
	@Path("/{identifierString}")
	ObjectResponse<Variant> getByIdentifier(@PathParam("identifierString") String identifierString);

	@Override
	@PUT
	@Path("/")
	@JsonView(View.VariantView.class)
	ObjectResponse<Variant> update(Variant entity);

	@Override
	@POST
	@Path("/")
	@JsonView(View.VariantView.class)
	ObjectResponse<Variant> create(Variant entity);

	@Override
	@POST
	@Path("/find")
	@Tag(name = "Relational Database Browsing Endpoints")
	@JsonView(View.VariantView.class)
	SearchResponse<Variant> find(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, @RequestBody HashMap<String, Object> params);

	@Override
	@POST
	@Path("/search")
	@Tag(name = "Elastic Search Browsing Endpoints")
	@JsonView({ View.VariantView.class })
	SearchResponse<Variant> search(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, @RequestBody HashMap<String, Object> params);

}
