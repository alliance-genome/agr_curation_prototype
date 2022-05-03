package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XBEDTerm;

@ApplicationScoped
public class XbedTermDAO extends BaseSQLDAO<XBEDTerm> {

    protected XbedTermDAO() {
        super(XBEDTerm.class);
    }

}
