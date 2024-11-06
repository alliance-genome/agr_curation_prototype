package org.alliancegenome.curation_api.jobs.processors;

import org.alliancegenome.curation_api.dao.loads.BulkLoadDAO;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.jobs.events.StartedBulkLoadJobEvent;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkURLLoad;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;

@ApplicationScoped
public class BulkLoadURLProcessor extends BulkLoadProcessor {

	@Inject BulkLoadDAO bulkLoadDAO;

	public void processBulkURLLoad(@ObservesAsync StartedBulkLoadJobEvent load) {

		BulkLoad bulkLoad = bulkLoadDAO.find(load.getId());
		if (bulkLoad instanceof BulkURLLoad bulkURLLoad) {

			Log.info("processBulkURLLoad: " + load.getId());
			startLoad(bulkURLLoad);

			if (bulkURLLoad.getBulkloadUrl() != null && bulkURLLoad.getBulkloadUrl().length() > 0) {
				String filePath = fileHelper.saveIncomingURLFile(bulkURLLoad.getBulkloadUrl());
				String localFilePath = fileHelper.compressInputFile(filePath);

				if (filePath == null) {
					Log.info("Load: " + bulkURLLoad.getName() + " failed");
					endLoad(bulkURLLoad, "Load: " + bulkURLLoad.getName() + " failed: to download URL: " + bulkURLLoad.getBulkloadUrl(), JobStatus.FAILED);
				} else if (localFilePath == null) {
					Log.info("Load: " + bulkURLLoad.getName() + " failed");
					endLoad(bulkURLLoad, "Load: " + bulkURLLoad.getName() + " failed: to save local file: " + filePath, JobStatus.FAILED);
				} else {
					processFilePath(bulkURLLoad, localFilePath);
					endLoad(bulkURLLoad, null, JobStatus.FINISHED);
				}
			} else {
				Log.info("Load: " + bulkURLLoad.getName() + " failed: URL is missing");
				endLoad(bulkURLLoad, "Load: " + bulkURLLoad.getName() + " failed: URL is missing", JobStatus.FAILED);
			}
		}
	}
}
