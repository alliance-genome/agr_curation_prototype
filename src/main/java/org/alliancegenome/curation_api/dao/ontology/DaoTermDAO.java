package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.DAOTerm;

@ApplicationScoped
public class DaoTermDAO extends BaseSQLDAO<DAOTerm> {

    protected DaoTermDAO() {
        super(DAOTerm.class);
    }

}
