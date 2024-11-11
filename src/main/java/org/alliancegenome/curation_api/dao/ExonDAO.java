package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Exon;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExonDAO extends BaseSQLDAO<Exon> {

	protected ExonDAO() {
		super(Exon.class);
	}

}
