package org.alliancegenome.curation_api.jobs.processors;

import java.util.List;

import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.jobs.events.StartedBulkLoadJobEvent;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad;
import org.alliancegenome.curation_api.model.fms.DataFile;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class BulkLoadFMSProcessor extends BulkLoadProcessor {

	public void processBulkFMSLoad(@ObservesAsync StartedBulkLoadJobEvent load) {
		BulkLoad bulkLoad = bulkLoadDAO.find(load.getId());
		if (bulkLoad instanceof BulkFMSLoad bulkFMSLoad) {

			Log.debug("processBulkFMSLoad: " + load.getId());
			startLoad(bulkFMSLoad);

			if (bulkFMSLoad.getFmsDataType() != null && bulkFMSLoad.getFmsDataSubType() != null) {
				List<DataFile> files = fmsDataFileService.getDataFiles(bulkFMSLoad.getFmsDataType(), bulkFMSLoad.getFmsDataSubType());

				if (files.size() == 1) {
					DataFile df = files.get(0);
					String s3Url = df.getS3Url();
					String filePath = fileHelper.saveIncomingURLFile(s3Url);
					String localFilePath = fileHelper.compressInputFile(filePath);
					processFilePath(bulkFMSLoad, localFilePath);
					endLoad(bulkFMSLoad, null, JobStatus.FINISHED);
				} else {
					log.warn("Files: " + files);
					log.warn("Issue pulling files from the FMS: " + bulkFMSLoad.getFmsDataType() + " " + bulkFMSLoad.getFmsDataSubType());
					endLoad(bulkFMSLoad, "Issue pulling files from the FMS: " + bulkFMSLoad.getFmsDataType() + " " + bulkFMSLoad.getFmsDataSubType(), JobStatus.FAILED);
				}

			} else {
				log.error("Load: " + bulkFMSLoad.getName() + " failed: FMS Params are missing");
				endLoad(bulkFMSLoad, "Load: " + bulkFMSLoad.getName() + " failed: FMS Params are missing", JobStatus.FAILED);
			}
		}
	}

}
