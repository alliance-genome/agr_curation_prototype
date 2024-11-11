package org.alliancegenome.curation_api.services.loads;

import org.alliancegenome.curation_api.dao.loads.BulkFMSLoadDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class BulkFMSLoadService extends BaseEntityCrudService<BulkFMSLoad, BulkFMSLoadDAO> {

	@Inject BulkFMSLoadDAO bulkFMSLoadDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(bulkFMSLoadDAO);
	}

}