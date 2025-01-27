package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.OntologyTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.OntologyTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.services.ontology.OntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class OntologyTermCrudController extends BaseOntologyTermController<OntologyTermService, OntologyTerm, OntologyTermDAO> implements OntologyTermCrudInterface {

	@Inject
	OntologyTermService ontologyTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(ontologyTermService, OntologyTerm.class);
	}

}
