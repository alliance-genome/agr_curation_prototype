package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.MpTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MPTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

@ApplicationScoped
public class MpTermService extends BaseOntologyTermService<MPTerm, MpTermDAO> {

	@Inject MpTermDAO mpTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(mpTermDAO);
	}

}
