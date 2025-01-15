package org.alliancegenome.curation_api.interfaces.crud;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.alliancegenome.curation_api.interfaces.base.crud.BaseCreateControllerInterface;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.ingest.dto.fms.BiogridOrcIngestFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;


@Path("/biogrid-orc")
@Tag(name = "CRUD - Biogrid Orc")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BiogridOrcCrudInterface extends BaseCreateControllerInterface<CrossReference> {


	@POST
	@Path("/bulk/{dataProvider}/biogridfile")
	@JsonView(View.FieldsAndLists.class)
	APIResponse updateBiogridOrc(@PathParam("dataProvider") String dataProvider, BiogridOrcIngestFmsDTO biogridOrcData);

}
