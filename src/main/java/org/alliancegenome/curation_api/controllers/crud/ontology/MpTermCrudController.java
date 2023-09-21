package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.MpTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.MpTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.MPTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.MpTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class MpTermCrudController extends BaseOntologyTermController<MpTermService, MPTerm, MpTermDAO> implements MpTermCrudInterface {

	@Inject
	MpTermService mpTermService;

	@Override
	@PostConstruct
	public void init() {
		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		config.setLoadOnlyIRIPrefix("MP");
		setService(mpTermService, MPTerm.class, config);
	}

}
