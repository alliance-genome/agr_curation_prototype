package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseOntologyTermService;
import org.alliancegenome.curation_api.dao.ontology.XaoDsTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XAODsTerm;

@RequestScoped
public class XaoDsTermService extends BaseOntologyTermService<XAODsTerm, XaoDsTermDAO> {

    @Inject XaoDsTermDAO xaoDsTermDAO;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(xaoDsTermDAO);
    }

}
