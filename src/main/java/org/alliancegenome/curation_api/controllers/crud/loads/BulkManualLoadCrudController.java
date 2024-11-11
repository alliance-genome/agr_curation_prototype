package org.alliancegenome.curation_api.controllers.crud.loads;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.loads.BulkManualLoadDAO;
import org.alliancegenome.curation_api.interfaces.crud.bulkloads.BulkManualLoadCrudInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.services.loads.BulkManualLoadService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class BulkManualLoadCrudController extends BaseEntityCrudController<BulkManualLoadService, BulkManualLoad, BulkManualLoadDAO> implements BulkManualLoadCrudInterface {

	@Inject
	BulkManualLoadService bulkManualLoadService;

	@Override
	@PostConstruct
	protected void init() {
		setService(bulkManualLoadService);
	}

}
