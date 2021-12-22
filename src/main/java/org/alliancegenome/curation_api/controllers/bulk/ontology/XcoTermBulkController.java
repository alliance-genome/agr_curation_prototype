package org.alliancegenome.curation_api.controllers.bulk.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseOntologyTermBulkController;
import org.alliancegenome.curation_api.dao.ontology.XcoTermDAO;
import org.alliancegenome.curation_api.interfaces.bulk.ontology.XcoTermBulkInterface;
import org.alliancegenome.curation_api.model.entities.ontology.XcoTerm;
import org.alliancegenome.curation_api.services.ontology.XcoTermService;

@RequestScoped
public class XcoTermBulkController extends BaseOntologyTermBulkController<XcoTermService, XcoTerm, XcoTermDAO> implements XcoTermBulkInterface {

    @Inject XcoTermService xcoTermService;

    @Override
    @PostConstruct
    public void init() {
        setService(xcoTermService, XcoTerm.class);
    }

}
