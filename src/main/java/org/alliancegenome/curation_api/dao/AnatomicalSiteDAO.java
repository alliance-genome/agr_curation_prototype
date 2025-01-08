package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.AnatomicalSite;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AnatomicalSiteDAO extends BaseSQLDAO<AnatomicalSite> {
	protected AnatomicalSiteDAO() {
		super(AnatomicalSite.class);
	}
}
