package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XAODsTerm;

@ApplicationScoped
public class XaoDsTermDAO extends BaseSQLDAO<XAODsTerm> {

    protected XaoDsTermDAO() {
        super(XAODsTerm.class);
    }

}
