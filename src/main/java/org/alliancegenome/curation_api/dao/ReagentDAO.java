package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Reagent;

@ApplicationScoped
public class ReagentDAO extends BaseSQLDAO<Reagent> {

	protected ReagentDAO() {
		super(Reagent.class);
	}

}
