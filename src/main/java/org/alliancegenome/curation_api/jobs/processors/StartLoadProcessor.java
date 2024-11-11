package org.alliancegenome.curation_api.jobs.processors;

import org.alliancegenome.curation_api.dao.loads.BulkLoadFileHistoryDAO;
import org.alliancegenome.curation_api.enums.BulkLoadCleanUp;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.jobs.events.StartedLoadJobEvent;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;

@ApplicationScoped
public class StartLoadProcessor extends BulkLoadProcessor {

	@Inject BulkLoadFileHistoryDAO bulkLoadFileHistoryDAO;

	public void bulkLoadFile(@ObservesAsync StartedLoadJobEvent event) { // An @Observes method should not be in a super class as then it gets run for every child class
		BulkLoadFileHistory bulkLoadFileHistory = bulkLoadFileHistoryDAO.find(event.getId());

		if (!bulkLoadFileHistory.getBulkloadStatus().isStarted()) {
			Log.warn("bulkLoadFileHistory: Job is not started returning: " + bulkLoadFileHistory.getBulkloadStatus());
			// endLoad(bulkLoadFile, "Finished ended due to status: " +
			// bulkLoadFile.getBulkloadStatus(), bulkLoadFile.getBulkloadStatus());
			return;
		} else {
			startLoad(bulkLoadFileHistory);
		}

		try {
			syncWithS3(bulkLoadFileHistory);
			bulkLoadJobExecutor.process(bulkLoadFileHistory, bulkLoadFileHistory.getBulkLoadFile().getBulkloadCleanUp() == BulkLoadCleanUp.YES);
			JobStatus status = bulkLoadFileHistory.getBulkloadStatus().equals(JobStatus.FAILED) ? JobStatus.FAILED : JobStatus.FINISHED;
			endLoad(bulkLoadFileHistory, bulkLoadFileHistory.getErrorMessage(), status);

		} catch (Exception e) {
			endLoad(bulkLoadFileHistory, "Failed loading: " + bulkLoadFileHistory.getBulkLoad().getName() + " please check the logs for more info. " + bulkLoadFileHistory.getErrorMessage(), JobStatus.FAILED);
			Log.error("Load File: " + bulkLoadFileHistory.getBulkLoad().getName() + " is failed");
			e.printStackTrace();
		}

	}
}
