package org.alliancegenome.curation_api.interfaces.crud;


import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.interfaces.base.*;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDTO;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/allele")
@Tag(name = "CRUD - Alleles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AlleleCrudInterface extends BaseCurieCrudInterface<Allele>, BaseDTOCrudControllerInterface<Allele, AlleleDTO> {

	@POST
	@Path("/bulk/alleles")
	@JsonView(View.FieldsAndLists.class)
	public APIResponse updateAlleles(List<AlleleDTO> alleleData);

	@Override
	@GET
	@JsonView(View.FieldsAndLists.class)
	@Path("/{curie}")
	public ObjectResponse<Allele> get(@PathParam("curie") String curie);

	@Override
	@GET
	@Path("/reindex")
	@Tag(name = "Reindex Endpoints")
	public void reindex(
		@DefaultValue("50") @QueryParam("batchSizeToLoadObjects") Integer batchSizeToLoadObjects,
		@DefaultValue("0") @QueryParam("idFetchSize") Integer idFetchSize,
		@DefaultValue("0") @QueryParam("limitIndexedObjectsTo") Integer limitIndexedObjectsTo,
		@DefaultValue("1") @QueryParam("threadsToLoadObjects") Integer threadsToLoadObjects,
		@DefaultValue("7200") @QueryParam("transactionTimeout") Integer transactionTimeout,
		@DefaultValue("1") @QueryParam("typesToIndexInParallel") Integer typesToIndexInParallel
	);

	
	@Override
	@PUT
	@Path("/")
	@JsonView(View.FieldsAndLists.class)
	public ObjectResponse<Allele> update(Allele entity);
	
	@Override
	@POST
	@Path("/")
	@JsonView(View.FieldsAndLists.class)
	public ObjectResponse<Allele> create(Allele entity);
}
