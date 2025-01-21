package org.alliancegenome.curation_api.interfaces.crud;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.alliancegenome.curation_api.interfaces.base.crud.BaseCreateControllerInterface;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/gaf")
@Tag(name = "CRUD - GAF")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneOntologyAnnotationCrudInterface extends BaseCreateControllerInterface<CrossReference> {

}
