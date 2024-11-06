package org.alliancegenome.curation_api.services.loads;

import org.alliancegenome.curation_api.dao.loads.BulkLoadFileDAO;
import org.alliancegenome.curation_api.jobs.events.PendingLoadJobEvent;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

@RequestScoped
public class BulkLoadFileService extends BaseEntityCrudService<BulkLoadFile, BulkLoadFileDAO> {

	@Inject BulkLoadFileDAO bulkLoadFileDAO;

	@Inject Event<PendingLoadJobEvent> pendingFileJobEvents;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(bulkLoadFileDAO);
	}

}