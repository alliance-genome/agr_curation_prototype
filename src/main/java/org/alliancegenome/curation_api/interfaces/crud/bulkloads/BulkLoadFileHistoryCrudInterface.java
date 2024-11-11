package org.alliancegenome.curation_api.interfaces.crud.bulkloads;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/bulkloadfilehistory")
@Tag(name = "Bulk Load - CRUD")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BulkLoadFileHistoryCrudInterface extends BaseIdCrudInterface<BulkLoadFileHistory> {
	@GET
	@Path("/{id}/download")
	@JsonView(View.BulkLoadFileHistoryView.class)
	@Produces(MediaType.APPLICATION_JSON)
	Response download(@PathParam("id") Long id);

	@GET
	@Path("/restartload/{id}")
	@JsonView(View.FieldsOnly.class)
	ObjectResponse<BulkLoad> restartBulkLoad(@PathParam("id") Long id);
	
	@GET
	@Path("/restartloadhistory/{id}")
	@JsonView(View.FieldsOnly.class)
	ObjectResponse<BulkLoadFile> restartBulkLoadHistory(@PathParam("id") Long id);
}
