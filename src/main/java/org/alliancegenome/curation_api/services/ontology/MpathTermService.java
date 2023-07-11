package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.MpathTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MPATHTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

@RequestScoped
public class MpathTermService extends BaseOntologyTermService<MPATHTerm, MpathTermDAO> {

	@Inject
	MpathTermDAO mpathTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(mpathTermDAO);
	}

}
