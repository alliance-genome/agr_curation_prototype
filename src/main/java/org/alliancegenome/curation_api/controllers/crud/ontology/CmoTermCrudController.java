package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.CmoTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.CmoTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.CMOTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.CmoTermService;

@RequestScoped
public class CmoTermCrudController extends BaseOntologyTermController<CmoTermService, CMOTerm, CmoTermDAO> implements CmoTermCrudInterface {

	@Inject
	CmoTermService cmoTermService;

	@Override
	@PostConstruct
	public void init() {
		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		config.setLoadOnlyIRIPrefix("CMO");
		setService(cmoTermService, CMOTerm.class, config);
	}

}