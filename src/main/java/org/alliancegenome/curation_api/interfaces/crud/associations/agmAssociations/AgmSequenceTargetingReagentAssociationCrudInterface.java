package org.alliancegenome.curation_api.interfaces.crud.associations.agmAssociations;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.associations.agmAssociations.AgmSequenceTargetingReagentAssociation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/allelegeneassociation")
@Tag(name = "CRUD - AGM Sequence Targeting Reagent Associations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AgmSequenceTargetingReagentAssociationCrudInterface extends BaseIdCrudInterface<AgmSequenceTargetingReagentAssociation> {

	@GET
	@Path("/findBy")
	@JsonView(View.FieldsAndLists.class)
	ObjectResponse<AgmSequenceTargetingReagentAssociation> getAssociation(@QueryParam("agmId") Long agmId, @QueryParam("relationName") String relationName, @QueryParam("strId") Long strId);
}
