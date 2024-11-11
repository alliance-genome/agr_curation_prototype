package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.FbcvTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.FbcvTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.FBCVTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.FbcvTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class FbcvTermCrudController extends BaseOntologyTermController<FbcvTermService, FBCVTerm, FbcvTermDAO> implements FbcvTermCrudInterface {

	@Inject
	FbcvTermService fbcvTermService;

	@Override
	@PostConstruct
	public void init() {
		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		config.setLoadOnlyIRIPrefix("FBcv");
		setService(fbcvTermService, FBCVTerm.class, config);
	}

}
