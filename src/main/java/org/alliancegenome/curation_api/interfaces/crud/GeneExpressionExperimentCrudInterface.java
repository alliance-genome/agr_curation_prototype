package org.alliancegenome.curation_api.interfaces.crud;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.GeneExpressionExperiment;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;


@Path("/gene-expression-experiment")
@Tag(name = "CRUD - Gene Expression Experiments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneExpressionExperimentCrudInterface extends BaseIdCrudInterface<GeneExpressionExperiment> {
	@GET
	@Path("/findBy/{identifier}")
	@JsonView(View.FieldsAndLists.class)
	ObjectResponse<GeneExpressionExperiment> getByIdentifier(@PathParam("identifier") String identifier);
}
