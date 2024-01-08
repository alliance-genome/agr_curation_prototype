package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.DpoTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.DpoTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.DPOTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.DpoTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class DpoTermCrudController extends BaseOntologyTermController<DpoTermService, DPOTerm, DpoTermDAO> implements DpoTermCrudInterface {

	@Inject
	DpoTermService dpoTermService;

	@Override
	@PostConstruct
	public void init() {
		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		config.getAltNameSpaces().add("phenotypic_class");
		setService(dpoTermService, DPOTerm.class, config);
	}

}
