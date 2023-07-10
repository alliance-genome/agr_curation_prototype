package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.ModTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.ModTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.MODTerm;
import org.alliancegenome.curation_api.services.ontology.ModTermService;

@RequestScoped
public class ModTermCrudController extends BaseOntologyTermController<ModTermService, MODTerm, ModTermDAO> implements ModTermCrudInterface {

	@Inject
	ModTermService modTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(modTermService, MODTerm.class);
	}

}