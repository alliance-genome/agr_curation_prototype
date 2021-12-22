package org.alliancegenome.curation_api.controllers.bulk.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseOntologyTermBulkController;
import org.alliancegenome.curation_api.dao.ontology.MpTermDAO;
import org.alliancegenome.curation_api.interfaces.bulk.ontology.MpTermBulkInterface;
import org.alliancegenome.curation_api.model.entities.ontology.MPTerm;
import org.alliancegenome.curation_api.services.ontology.MpTermService;

@RequestScoped
public class MpTermBulkController extends BaseOntologyTermBulkController<MpTermService, MPTerm, MpTermDAO> implements MpTermBulkInterface {

    @Inject MpTermService mpTermService;

    @Override
    @PostConstruct
    public void init() {
        setService(mpTermService, MPTerm.class);
    }

}
