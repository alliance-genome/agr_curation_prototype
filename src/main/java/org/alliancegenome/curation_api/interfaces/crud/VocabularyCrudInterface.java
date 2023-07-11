package org.alliancegenome.curation_api.interfaces.crud;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/vocabulary")
@Tag(name = "CRUD - Vocabulary")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface VocabularyCrudInterface extends BaseIdCrudInterface<Vocabulary> {

	@GET
	@Path("/{id}")
	@JsonView(View.VocabularyView.class)
	ObjectResponse<Vocabulary> get(@PathParam("id") Long id);

	@GET
	@Path("/{id}/terms")
	@JsonView(View.VocabularyTermView.class)
	ObjectListResponse<VocabularyTerm> getTerms(@PathParam("id") Long id);

	@GET
	@Path("/findBy/{name}")
	@JsonView(View.FieldsAndLists.class)
	public ObjectResponse<Vocabulary> findByName(@PathParam("name") String name);

}
