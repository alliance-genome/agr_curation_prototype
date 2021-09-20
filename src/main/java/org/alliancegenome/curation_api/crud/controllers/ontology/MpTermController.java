package org.alliancegenome.curation_api.crud.controllers.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.MpTermDAO;
import org.alliancegenome.curation_api.interfaces.rest.ontology.MpTermRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.MPTerm;
import org.alliancegenome.curation_api.services.ontology.MpTermService;

@RequestScoped
public class MpTermController extends BaseOntologyTermController<MpTermService, MPTerm, MpTermDAO> implements MpTermRESTInterface {

    @Inject MpTermService mpTermService;

    @Override
    @PostConstruct
    protected void init() {
        setService(mpTermService);
    }

}
