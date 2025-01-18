package org.alliancegenome.curation_api.interfaces.crud.associations.agmAssociations;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.associations.agmAssociations.AgmAgmAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.agmAssociations.AgmAgmAssociationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/agmagmassociation")
@Tag(name = "CRUD - AGM AGM Associations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AgmAgmAssociationCrudInterface extends BaseIdCrudInterface<AgmAgmAssociation> {

	@GET
	@Path("/findBy")
	@JsonView(View.FieldsAndLists.class)
	ObjectResponse<AgmAgmAssociation> getAssociation(@QueryParam("agmSubjectId") Long agmId, @QueryParam("relationName") String relationName, @QueryParam("agmObjectId") Long strId);

	@POST
	@Path("/bulk/{dataProvider}/associationFile")
	@JsonView(View.FieldsAndLists.class)
	APIResponse updateAgmAgmAssociations(@PathParam("dataProvider") String dataProvider, List<AgmAgmAssociationDTO> associationData);
}
