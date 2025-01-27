package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.dao.ontology.ModTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MODTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ModTermService extends BaseOntologyTermService<MODTerm, ModTermDAO> {

	@Inject
	ModTermDAO modTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(modTermDAO);
	}

}
