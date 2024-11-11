package org.alliancegenome.curation_api.controllers.crud.loads;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.loads.BulkURLLoadDAO;
import org.alliancegenome.curation_api.interfaces.crud.bulkloads.BulkURLLoadCrudInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkURLLoad;
import org.alliancegenome.curation_api.services.loads.BulkURLLoadService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class BulkURLLoadCrudController extends BaseEntityCrudController<BulkURLLoadService, BulkURLLoad, BulkURLLoadDAO> implements BulkURLLoadCrudInterface {

	@Inject
	BulkURLLoadService bulkURLLoadService;

	@Override
	@PostConstruct
	protected void init() {
		setService(bulkURLLoadService);
	}

}
