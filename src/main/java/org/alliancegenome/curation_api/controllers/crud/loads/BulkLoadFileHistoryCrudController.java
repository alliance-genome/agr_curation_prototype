package org.alliancegenome.curation_api.controllers.crud.loads;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileHistoryDAO;
import org.alliancegenome.curation_api.interfaces.crud.bulkloads.BulkLoadFileHistoryCrudInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.loads.BulkLoadFileHistoryService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@RequestScoped
public class BulkLoadFileHistoryCrudController extends BaseEntityCrudController<BulkLoadFileHistoryService, BulkLoadFileHistory, BulkLoadFileHistoryDAO> implements BulkLoadFileHistoryCrudInterface {

	@Inject
	BulkLoadFileHistoryService bulkLoadFileHistoryService;

	@Override
	@PostConstruct
	protected void init() {
		setService(bulkLoadFileHistoryService);
	}

	@Override
	public Response download(Long id) {
		return bulkLoadFileHistoryService.download(id);
	}
	
	@Override
	public ObjectResponse<BulkLoad> restartBulkLoad(Long id) {
		return bulkLoadFileHistoryService.restartBulkLoad(id);
	}
	
	@Override
	public ObjectResponse<BulkLoadFile> restartBulkLoadHistory(Long id) {
		return bulkLoadFileHistoryService.restartBulkLoadHistory(id);
	}
}
