package org.alliancegenome.curation_api.interfaces.crud.ontology;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.base.BaseCurieCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.CHEBITerm;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/chebiterm")
@Tag(name = "CRUD - Ontology - ChEBI")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface CHEBITermCrudInterface extends BaseCurieCrudInterface<CHEBITerm> {

}
