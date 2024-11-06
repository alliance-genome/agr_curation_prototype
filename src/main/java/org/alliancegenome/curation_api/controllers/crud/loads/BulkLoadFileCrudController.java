package org.alliancegenome.curation_api.controllers.crud.loads;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileDAO;
import org.alliancegenome.curation_api.interfaces.crud.bulkloads.BulkLoadFileCrudInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.services.loads.BulkLoadFileService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class BulkLoadFileCrudController extends BaseEntityCrudController<BulkLoadFileService, BulkLoadFile, BulkLoadFileDAO> implements BulkLoadFileCrudInterface {

	@Inject
	BulkLoadFileService bulkLoadFileService;

	@Override
	@PostConstruct
	protected void init() {
		setService(bulkLoadFileService);
	}

}
