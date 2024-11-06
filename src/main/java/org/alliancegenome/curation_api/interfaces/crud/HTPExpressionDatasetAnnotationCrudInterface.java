package org.alliancegenome.curation_api.interfaces.crud;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.HTPExpressionDatasetAnnotationIngestFmsDTO;
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

@Path("/htpexpressiondatasetannotation")
@Tag(name = "CRUD - HTP Expression Dataset Annotation")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface HTPExpressionDatasetAnnotationCrudInterface extends BaseIdCrudInterface<HTPExpressionDatasetAnnotation> {
	
	@POST
	@Path("/bulk/{dataProvider}/htpexpressiondatasetannotationfile")
	@JsonView(View.FieldsAndLists.class)
	APIResponse updateHTPExpressionDatasetAnnotation(@PathParam("dataProvider") String dataProvider, HTPExpressionDatasetAnnotationIngestFmsDTO htpDatasetData);
}