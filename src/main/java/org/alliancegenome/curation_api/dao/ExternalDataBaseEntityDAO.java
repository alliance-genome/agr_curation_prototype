package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ExternalDataBaseEntity;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExternalDataBaseEntityDAO extends BaseSQLDAO<ExternalDataBaseEntity> {
	
	protected ExternalDataBaseEntityDAO() {
		super(ExternalDataBaseEntity.class);
	}
}
