package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.dao.ontology.FbcvTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.FBCVTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class FbcvTermService extends BaseOntologyTermService<FBCVTerm, FbcvTermDAO> {

	@Inject
	FbcvTermDAO fbcvTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(fbcvTermDAO);
	}

}
