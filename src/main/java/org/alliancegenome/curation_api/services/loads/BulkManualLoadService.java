package org.alliancegenome.curation_api.services.loads;

import org.alliancegenome.curation_api.dao.loads.BulkManualLoadDAO;
import org.alliancegenome.curation_api.jobs.events.PendingBulkLoadJobEvent;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

@RequestScoped
public class BulkManualLoadService extends BaseEntityCrudService<BulkManualLoad, BulkManualLoadDAO> {

	@Inject BulkManualLoadDAO bulkManualLoadDAO;

	@Inject Event<PendingBulkLoadJobEvent> pendingJobEvents;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(bulkManualLoadDAO);
	}

}