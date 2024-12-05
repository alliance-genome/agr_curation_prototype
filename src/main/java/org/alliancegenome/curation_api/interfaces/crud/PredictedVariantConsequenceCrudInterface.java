package org.alliancegenome.curation_api.interfaces.crud;

import java.util.List;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.PredictedVariantConsequence;
import org.alliancegenome.curation_api.model.ingest.dto.fms.VepTxtDTO;
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

@Path("predictedvariantconsequence")
@Tag(name = "CRUD - Predicted Variant Consequence")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface PredictedVariantConsequenceCrudInterface extends BaseIdCrudInterface<PredictedVariantConsequence> {

	@POST
	@Path("/bulk/{dataProvider}/transcriptConsequenceFile")
	@JsonView(View.FieldsAndLists.class)
	APIResponse updateTranscriptLevelConsequences(@PathParam("dataProvider") String dataProvider, List<VepTxtDTO> consequenceData);
	
	@POST
	@Path("/bulk/{dataProvider}/geneConsequenceFile")
	@JsonView(View.FieldsAndLists.class)
	APIResponse updateGeneLevelConsequences(@PathParam("dataProvider") String dataProvider, List<VepTxtDTO> consequenceData);

}
