package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XBSTerm;

@ApplicationScoped
public class XbsTermDAO extends BaseSQLDAO<XBSTerm> {

	protected XbsTermDAO() {
		super(XBSTerm.class);
	}

}
