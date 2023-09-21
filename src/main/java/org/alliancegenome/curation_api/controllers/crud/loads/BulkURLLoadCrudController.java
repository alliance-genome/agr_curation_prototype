package org.alliancegenome.curation_api.controllers.crud.loads;

import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.loads.BulkURLLoadDAO;
import org.alliancegenome.curation_api.interfaces.crud.bulkloads.BulkURLLoadCrudInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkURLLoad;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.loads.BulkURLLoadService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class BulkURLLoadCrudController extends BaseEntityCrudController<BulkURLLoadService, BulkURLLoad, BulkURLLoadDAO> implements BulkURLLoadCrudInterface {

	@Inject
	BulkURLLoadService bulkURLLoadService;

	@Override
	@PostConstruct
	protected void init() {
		setService(bulkURLLoadService);
	}

	@Override
	public ObjectResponse<BulkURLLoad> restartLoad(Long id) {
		return bulkURLLoadService.restartLoad(id);
	}
}
