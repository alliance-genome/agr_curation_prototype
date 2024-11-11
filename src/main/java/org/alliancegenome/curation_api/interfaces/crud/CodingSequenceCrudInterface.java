package org.alliancegenome.curation_api.interfaces.crud;

import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseSubmittedObjectCrudInterface;
import org.alliancegenome.curation_api.model.entities.CodingSequence;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/cds")
@Tag(name = "CRUD - Coding Sequence")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface CodingSequenceCrudInterface extends BaseSubmittedObjectCrudInterface<CodingSequence> {

	@POST
	@Path("/bulk/{dataProvider}_{assemblyName}/codingSequences")
	@JsonView(View.FieldsAndLists.class)
	APIResponse updateCodingSequences(@PathParam("dataProvider") String dataProvider, @PathParam("assemblyName") String assemblyName, List<Gff3DTO> gff3Data);
	
}
