package org.alliancegenome.curation_api.interfaces.crud;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.alliancegenome.curation_api.interfaces.base.crud.BaseCreateControllerInterface;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.ingest.dto.CrossReferenceDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/expression-atlas")
@Tag(name = "CRUD - Expression Atlas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ExpressionAtlasCrudInterface extends BaseCreateControllerInterface<CrossReference> {

	@POST
	@Path("/bulk/{dataProvider}_{assemblyName}/transcripts")
	@JsonView(View.FieldsAndLists.class)
	APIResponse updateExpressionAtlas(@PathParam("dataProvider") String dataProvider, @PathParam("assemblyName") String assemblyName, List<CrossReferenceDTO> crossRefData);

}
