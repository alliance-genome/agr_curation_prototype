package org.alliancegenome.curation_api.services;

import org.alliancegenome.curation_api.dao.ExternalDataBaseEntityDAO;
import org.alliancegenome.curation_api.model.entities.ExternalDataBaseEntity;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ExternalDataBaseEntityService extends BaseEntityCrudService<ExternalDataBaseEntity, ExternalDataBaseEntityDAO> {
	
	@Inject ExternalDataBaseEntityDAO externalDataBaseEntityDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(externalDataBaseEntityDAO);
	}
}