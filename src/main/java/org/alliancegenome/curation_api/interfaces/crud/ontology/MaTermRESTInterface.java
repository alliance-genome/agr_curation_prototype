package org.alliancegenome.curation_api.interfaces.crud.ontology;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.BaseCrudRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.*;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/materm")
@Tag(name = "Ontology - MA")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface MaTermRESTInterface extends BaseCrudRESTInterface<MATerm> {

}