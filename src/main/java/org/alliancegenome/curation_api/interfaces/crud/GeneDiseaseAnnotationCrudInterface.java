package org.alliancegenome.curation_api.interfaces.crud;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.interfaces.base.BaseDTOCrudControllerInterface;
import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/gene-disease-annotation")
@Tag(name = "CRUD - Gene Disease Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneDiseaseAnnotationCrudInterface extends BaseIdCrudInterface<GeneDiseaseAnnotation>, BaseDTOCrudControllerInterface<GeneDiseaseAnnotation, GeneDiseaseAnnotationDTO> {

	@GET
	@Path("/findBy/{uniqueId}")
	@JsonView(View.FieldsAndLists.class)
	public ObjectResponse<GeneDiseaseAnnotation> get(@PathParam("uniqueId") String uniqueId);

	@PUT
	@Path("/")
	@JsonView(View.DiseaseAnnotation.class)
	public ObjectResponse<GeneDiseaseAnnotation> update(GeneDiseaseAnnotation entity);

	@POST
	@Path("/")
	@JsonView(View.DiseaseAnnotation.class)
	public ObjectResponse<GeneDiseaseAnnotation> create(GeneDiseaseAnnotation entity);

	@POST
	@Path("/bulk/{dataType}/annotationFile")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateGeneDiseaseAnnotations(@PathParam("dataType") String dataType, List<GeneDiseaseAnnotationDTO> annotationData);

}
