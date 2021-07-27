package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Allele;

public class AlleleDAO extends BaseSQLDAO<Allele> {

	protected AlleleDAO() {
		super(Allele.class);
	}
}
