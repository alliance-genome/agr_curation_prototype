package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.dao.ontology.WbbtTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.WBBTTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class WbbtTermService extends BaseOntologyTermService<WBBTTerm, WbbtTermDAO> {

	@Inject
	WbbtTermDAO wbbtTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(wbbtTermDAO);
	}

}
