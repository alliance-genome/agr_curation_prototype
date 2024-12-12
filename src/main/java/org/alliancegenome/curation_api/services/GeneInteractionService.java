package org.alliancegenome.curation_api.services;

import java.util.List;

import org.alliancegenome.curation_api.dao.GeneInteractionDAO;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.model.entities.GeneInteraction;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneInteractionService extends BaseEntityCrudService<GeneInteraction, GeneInteractionDAO> {

	@Inject GeneInteractionDAO geneInteractionDAO;
	@Inject PersonService personService;
	@Inject PersonDAO personDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneInteractionDAO);
	}

	public ObjectResponse<GeneInteraction> getByIdentifer(String identifier) {
		GeneInteraction interaction = findByAlternativeFields(List.of("interactionId", "uniqueId"), identifier);
		return new ObjectResponse<GeneInteraction>(interaction);
	}
}
