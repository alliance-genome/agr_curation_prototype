package org.alliancegenome.curation_api.interfaces.fms;

import java.util.List;

import org.alliancegenome.curation_api.model.fms.DataFile;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/datafile")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "DataFile Endpoints")
public interface DataFileRESTInterface {

	@GET
	@Path("/{id}")
	DataFile get(@Parameter(in = ParameterIn.PATH, name = "id", description = "Long Id or md5Sum", required = true, schema = @Schema(type = SchemaType.STRING)) @PathParam("id") String id);

	@GET
	@Path("/all")
	List<DataFile> getDataFiles();

	@GET
	@Path("/by/{dataType}")
	List<DataFile> getDataTypeFiles(@PathParam("dataType") String dataType,
		@DefaultValue("false") @Parameter(in = ParameterIn.QUERY, name = "latest", description = "Latest File or All", required = false, schema = @Schema(type = SchemaType.BOOLEAN)) @QueryParam("latest") Boolean latest);

	@GET
	@Path("/by/release/{releaseVersion}")
	List<DataFile> getDataFilesByRelease(@PathParam("releaseVersion") String releaseVersion,
		@DefaultValue("false") @Parameter(in = ParameterIn.QUERY, name = "latest", description = "Latest File or All", required = false, schema = @Schema(type = SchemaType.BOOLEAN)) @QueryParam("latest") Boolean latest);

	@GET
	@Path("/by/{dataType}/{dataSubtype}")
	@Operation(summary = "Get list of DataFile's", description = "Get list of DataFile's")
	List<DataFile> getDataTypeSubTypeFiles(
		@Parameter(in = ParameterIn.PATH, name = "dataType", description = "Data Type Name", required = true, schema = @Schema(type = SchemaType.STRING)) @PathParam("dataType") String dataType,
		@Parameter(in = ParameterIn.PATH, name = "dataSubtype", description = "Data Sub Type Name", required = true, schema = @Schema(type = SchemaType.STRING)) @PathParam("dataSubtype") String dataSubType,
		@DefaultValue("false") @Parameter(in = ParameterIn.QUERY, name = "latest", description = "Latest File or All", required = false, schema = @Schema(type = SchemaType.BOOLEAN)) @QueryParam("latest") Boolean latest);

	@GET
	@Path("/by/{releaseVersion}/{dataType}/{dataSubtype}")
	@Operation(summary = "Get list of DataFile's", description = "Get list of DataFile's")
	List<DataFile> getReleaseDataTypeSubTypeFiles(
		@Parameter(in = ParameterIn.PATH, name = "releaseVersion", description = "Release Version Name", required = true, schema = @Schema(type = SchemaType.STRING)) @PathParam("releaseVersion") String releaseVersion,
		@Parameter(in = ParameterIn.PATH, name = "dataType", description = "Data Type Name", required = true, schema = @Schema(type = SchemaType.STRING)) @PathParam("dataType") String dataType,
		@Parameter(in = ParameterIn.PATH, name = "dataSubtype", description = "Data Sub Type Name", required = true, schema = @Schema(type = SchemaType.STRING)) @PathParam("dataSubtype") String dataSubType,
		@DefaultValue("false") @Parameter(in = ParameterIn.QUERY, name = "latest", description = "Latest File or All", required = false, schema = @Schema(type = SchemaType.BOOLEAN)) @QueryParam("latest") Boolean latest);
}
