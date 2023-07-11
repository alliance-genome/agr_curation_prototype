package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.ZfaTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ZFATerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

@RequestScoped
public class ZfaTermService extends BaseOntologyTermService<ZFATerm, ZfaTermDAO> {

	@Inject
	ZfaTermDAO zfaTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(zfaTermDAO);
	}

}
