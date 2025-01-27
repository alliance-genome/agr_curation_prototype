package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.GoTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.GoTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.GoTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GoTermCrudController extends BaseOntologyTermController<GoTermService, GOTerm, GoTermDAO> implements GoTermCrudInterface {

	@Inject
	GoTermService goTermService;

	@Override
	@PostConstruct
	public void init() {
		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		config.getAltNameSpaces().add("biological_process");
		config.getAltNameSpaces().add("molecular_function");
		config.getAltNameSpaces().add("cellular_component");
		setService(goTermService, GOTerm.class, config);
	}

}
