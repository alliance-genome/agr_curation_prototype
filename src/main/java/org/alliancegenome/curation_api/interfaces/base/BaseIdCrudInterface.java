package org.alliancegenome.curation_api.interfaces.base;

import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.view.View;
import org.apache.commons.lang3.ObjectUtils.Null;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BaseIdCrudInterface<E extends AuditedObject> {

	@POST
	@Path("/")
	@JsonView(View.FieldsOnly.class)
	@RequestBody( 
		description = "Post Request",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = String.class)
		)
	)
	@APIResponses(
		@APIResponse(
			description = "Response Entity",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = Null.class)
			)
		)
	)
	public ObjectResponse<E> create(E entity);

	@POST
	@Path("/multiple")
	@JsonView(View.FieldsOnly.class)
	@RequestBody( 
		description = "Post Request",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = String.class)
		)
	)
	@APIResponses(
		@APIResponse(
			description = "Response Entity",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = Null.class)
			)
		)
	)
	public ObjectListResponse<E> create(List<E> entities);

	@GET
	@Path("/{id}")
	@JsonView(View.FieldsOnly.class)
	@APIResponses(
		@APIResponse(
			description = "Get Entity by Id",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = Null.class)
			)
		)
	)
	public ObjectResponse<E> get(@PathParam("id") Long id);

	@PUT
	@Path("/")
	@JsonView(View.FieldsOnly.class)
	@RequestBody( 
		description = "Put Request",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = String.class)
		)
	)
	@APIResponses(
		@APIResponse(
			description = "Response Entity",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = Null.class)
			)
		)
	)
	public ObjectResponse<E> update(E entity);

	@DELETE
	@Path("/{id}")
	@JsonView(View.FieldsOnly.class)
	@APIResponses(
		@APIResponse(
			description = "Delete Entity by Id",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = Null.class)
			)
		)
	)
	public ObjectResponse<E> delete(@PathParam("id") Long id);

	@POST
	@Path("/find")
	@Tag(name = "Relational Database Browsing Endpoints")
	@JsonView(View.FieldsAndLists.class)
	@RequestBody( 
		description = "Post Request",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = String.class)
		)
	)
	@APIResponses(
		@APIResponse(
			description = "Response Entity",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = Null.class)
			)
		)
	)
	public SearchResponse<E> find(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, @RequestBody HashMap<String, Object> params);

	@POST
	@Path("/findForPublic")
	@Tag(name = "Public Web API Database Searching Endpoints")
	@JsonView(View.ForPublic.class)
	@RequestBody( 
		description = "Post Request",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = String.class)
		)
	)
	@APIResponses(
		@APIResponse(
			description = "Response Entity",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = Null.class)
			)
		)
	)
	public SearchResponse<E> findForPublic(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, @RequestBody HashMap<String, Object> params);
	
	@POST
	@Path("/search")
	@Tag(name = "Elastic Search Browsing Endpoints")
	@JsonView({ View.FieldsAndLists.class })
	@RequestBody( 
		description = "Post Request",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = String.class)
		)
	)
	@APIResponses(
		@APIResponse(
			description = "Response Entity",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = Null.class)
			)
		)
	)
	public SearchResponse<E> search(@DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("limit") Integer limit, @RequestBody HashMap<String, Object> params);

	@GET
	@Path("/reindex")
	@Tag(name = "Reindex Endpoints")
	public void reindex(
		@DefaultValue("1000") @QueryParam("batchSizeToLoadObjects") Integer batchSizeToLoadObjects,
		@DefaultValue("0") @QueryParam("idFetchSize") Integer idFetchSize,
		@DefaultValue("0") @QueryParam("limitIndexedObjectsTo") Integer limitIndexedObjectsTo,
		@DefaultValue("4") @QueryParam("threadsToLoadObjects") Integer threadsToLoadObjects,
		@DefaultValue("14400") @QueryParam("transactionTimeout") Integer transactionTimeout,
		@DefaultValue("1") @QueryParam("typesToIndexInParallel") Integer typesToIndexInParallel);

}
