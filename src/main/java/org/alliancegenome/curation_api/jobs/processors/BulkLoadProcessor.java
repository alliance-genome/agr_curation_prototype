package org.alliancegenome.curation_api.jobs.processors;

import java.io.File;
import java.time.LocalDateTime;

import org.alliancegenome.curation_api.dao.loads.BulkFMSLoadDAO;
import org.alliancegenome.curation_api.dao.loads.BulkLoadDAO;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileDAO;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileHistoryDAO;
import org.alliancegenome.curation_api.dao.loads.BulkManualLoadDAO;
import org.alliancegenome.curation_api.dao.loads.BulkURLLoadDAO;
import org.alliancegenome.curation_api.enums.BulkLoadCleanUp;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.jobs.events.PendingLoadJobEvent;
import org.alliancegenome.curation_api.jobs.executors.BulkLoadJobExecutor;
import org.alliancegenome.curation_api.jobs.util.SlackNotifier;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.fms.DataFileService;
import org.alliancegenome.curation_api.util.FileTransferHelper;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.logging.Log;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

public class BulkLoadProcessor {

	@ConfigProperty(name = "bulk.data.loads.s3Bucket") String s3Bucket;
	@ConfigProperty(name = "bulk.data.loads.s3PathPrefix") String s3PathPrefix;
	@ConfigProperty(name = "bulk.data.loads.s3AccessKey") String s3AccessKey;
	@ConfigProperty(name = "bulk.data.loads.s3SecretKey") String s3SecretKey;

	@Inject DataFileService fmsDataFileService;

	@Inject BulkLoadDAO bulkLoadDAO;
	@Inject BulkLoadFileDAO bulkLoadFileDAO;
	@Inject BulkLoadFileHistoryDAO bulkLoadFileHistoryDAO;
	
	
	@Inject BulkManualLoadDAO bulkManualLoadDAO;
	@Inject BulkFMSLoadDAO bulkFMSLoadDAO;
	@Inject BulkURLLoadDAO bulkURLLoadDAO;

	@Inject BulkLoadJobExecutor bulkLoadJobExecutor;

	@Inject SlackNotifier slackNotifier;

	@Inject Event<PendingLoadJobEvent> pendingFileJobEvents;

	protected FileTransferHelper fileHelper = new FileTransferHelper();

	public void syncWithS3(BulkLoadFileHistory bulkLoadFileHistory) {
		BulkLoad bulkLoad = bulkLoadFileHistory.getBulkLoad();
		BulkLoadFile bulkLoadFile = bulkLoadFileHistory.getBulkLoadFile();
		Log.info("Starting Syncing with S3: Local File: " + bulkLoadFile.getLocalFilePath() + " S3 File: " + bulkLoadFile.getS3Path());

		if ((bulkLoadFile.getS3Path() != null || bulkLoadFile.generateS3MD5Path(bulkLoad) != null) && bulkLoadFile.getLocalFilePath() == null) {
			File outfile = fileHelper.downloadFileFromS3(s3AccessKey, s3SecretKey, s3Bucket, bulkLoadFile.getS3Path());
			if (outfile != null) {
				// log.info(outfile + " is of size: " + outfile.length());
				bulkLoadFile.setFileSize(outfile.length());
				bulkLoadFile.setLocalFilePath(outfile.getAbsolutePath());
				bulkLoadFileDAO.merge(bulkLoadFile);
			} else {
				// log.error("Failed to download file from S3 Path: " + s3PathPrefix + "/" +
				// bulkLoadFile.generateS3MD5Path());
				bulkLoadFileHistory.setErrorMessage("Failed to download file from S3 Path: " + s3PathPrefix + "/" + bulkLoadFile.generateS3MD5Path(bulkLoad));
				bulkLoadFileHistory.setBulkloadStatus(JobStatus.FAILED);
				slackNotifier.slackalert(bulkLoadFileHistory);
				bulkLoadFileHistoryDAO.merge(bulkLoadFileHistory);
			}
			// log.info("Saving File: " + bulkLoadFile);
			
		} else if (bulkLoadFile.getS3Path() == null && bulkLoadFile.getLocalFilePath() != null) {
			if (s3AccessKey != null && s3AccessKey.length() > 0) {
				String s3Path = fileHelper.uploadFileToS3(s3AccessKey, s3SecretKey, s3Bucket, s3PathPrefix, bulkLoadFile.generateS3MD5Path(bulkLoad), new File(bulkLoadFile.getLocalFilePath()));
				bulkLoadFile.setS3Path(s3Path);
			}
			bulkLoadFileDAO.merge(bulkLoadFile);
		} else if (bulkLoadFile.getS3Path() == null && bulkLoadFile.getLocalFilePath() == null) {
			bulkLoadFileHistory.setErrorMessage("Failed to download or upload file with S3 Path: " + s3PathPrefix + "/" + bulkLoadFile.generateS3MD5Path(bulkLoad) + " Local and remote file missing");
			bulkLoadFileHistory.setBulkloadStatus(JobStatus.FAILED);
			slackNotifier.slackalert(bulkLoadFileHistory);
			bulkLoadFileHistoryDAO.merge(bulkLoadFileHistory);
		} else {
			Log.info("No S3 syncing required");
		}
		Log.info("Syncing with S3 Finished");
	}

	protected void processFilePath(BulkLoad bulkLoad, String localFilePath) {
		processFilePath(bulkLoad, localFilePath, false);
	}

	protected void processFilePath(BulkLoad bulkLoad, String localFilePath, Boolean cleanUp) {
		String md5Sum = fileHelper.getMD5SumOfGzipFile(localFilePath);
		Log.info("processFilePath: MD5 Sum: " + md5Sum);

		File inputFile = new File(localFilePath);

		BulkLoad load = bulkLoadDAO.find(bulkLoad.getId());

		SearchResponse<BulkLoadFile> bulkLoadFiles = bulkLoadFileDAO.findByField("md5Sum", md5Sum);
		BulkLoadFile bulkLoadFile;
		
		BulkLoadFileHistory history = new BulkLoadFileHistory();

		if (bulkLoadFiles == null || bulkLoadFiles.getResults().size() == 0) {
			Log.info("Bulk File does not exist creating it");
			bulkLoadFile = new BulkLoadFile();
			bulkLoadFile.setMd5Sum(md5Sum);
			bulkLoadFile.setFileSize(inputFile.length());
			
			if (load.getBulkloadStatus() == JobStatus.FORCED_RUNNING) {
				history.setBulkloadStatus(JobStatus.FORCED_PENDING);
			}
			if (load.getBulkloadStatus() == JobStatus.SCHEDULED_RUNNING) {
				history.setBulkloadStatus(JobStatus.SCHEDULED_PENDING);
			}
			if (load.getBulkloadStatus() == JobStatus.MANUAL_RUNNING) {
				history.setBulkloadStatus(JobStatus.MANUAL_PENDING);
			}

			Log.info(load.getBulkloadStatus());

			bulkLoadFile.setLocalFilePath(localFilePath);
			if (cleanUp) {
				bulkLoadFile.setBulkloadCleanUp(BulkLoadCleanUp.YES);
			}
			bulkLoadFileDAO.persist(bulkLoadFile);
		} else if (load.getBulkloadStatus().isForced()) {
			bulkLoadFile = bulkLoadFiles.getResults().get(0);
			if (history.getBulkloadStatus().isNotRunning()) {
				bulkLoadFile.setLocalFilePath(localFilePath);
				history.setErrorMessage(null);
				history.setBulkloadStatus(JobStatus.FORCED_PENDING);
			} else {
				Log.warn("Bulk File is already running: " + bulkLoadFile.getMd5Sum());
				Log.info("Cleaning up downloaded file: " + localFilePath);
				new File(localFilePath).delete();
			}
		} else {
			Log.info("Bulk File already exists not creating it");
			bulkLoadFile = bulkLoadFiles.getResults().get(0);
			Log.info("Cleaning up downloaded file: " + localFilePath);
			new File(localFilePath).delete();
			bulkLoadFile.setLocalFilePath(null);
		}
		
		history.setBulkLoad(bulkLoad);
		history.setBulkLoadFile(bulkLoadFile);
		bulkLoadFileHistoryDAO.persist(history);

		if (cleanUp) {
			bulkLoadFile.setBulkloadCleanUp(BulkLoadCleanUp.YES);
		}
		bulkLoadFileDAO.merge(bulkLoadFile);
		bulkLoadDAO.merge(load);
		Log.info("Firing Pending Bulk File History Event: " + history.getId());
		pendingFileJobEvents.fireAsync(new PendingLoadJobEvent(history.getId()));
	}

	protected void startLoad(BulkLoad load) {
		Log.info("Load: " + load.getName() + " is starting");

		BulkLoad bulkLoad = bulkLoadDAO.find(load.getId());
		if (!bulkLoad.getBulkloadStatus().isStarted()) {
			Log.warn("startLoad: Job is not started returning: " + bulkLoad.getBulkloadStatus());
			return;
		}
		bulkLoad.setBulkloadStatus(bulkLoad.getBulkloadStatus().getNextStatus());
		bulkLoadDAO.merge(bulkLoad);
		Log.info("Load: " + bulkLoad.getName() + " is running");
	}

	protected void endLoad(BulkLoad load, String message, JobStatus status) {
		BulkLoad bulkLoad = bulkLoadDAO.find(load.getId());
		bulkLoad.setErrorMessage(message);
		bulkLoad.setBulkloadStatus(status);
		if (status != JobStatus.FINISHED) {
			slackNotifier.slackalert(bulkLoad);
		}
		bulkLoadDAO.merge(bulkLoad);
		Log.info("Load: " + bulkLoad.getName() + " is finished");
	}

	protected void startLoad(BulkLoadFileHistory bulkLoadFileHistory) {
		bulkLoadFileHistory.setBulkloadStatus(bulkLoadFileHistory.getBulkloadStatus().getNextStatus());
		bulkLoadFileHistory.setLoadStarted(LocalDateTime.now());
		bulkLoadFileHistory.setErrorMessage(null);
		bulkLoadFileHistory.setLoadFinished(null);
		bulkLoadFileHistoryDAO.merge(bulkLoadFileHistory);
		bulkLoadFileHistory.getBulkLoad().setBulkloadStatus(bulkLoadFileHistory.getBulkloadStatus());
		bulkLoadDAO.merge(bulkLoadFileHistory.getBulkLoad());
		Log.info("Load File: " + bulkLoadFileHistory.getBulkLoadFile().getMd5Sum() + " is running with file: " + bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath());
	}

	protected void endLoad(BulkLoadFileHistory bulkLoadFileHistory, String message, JobStatus status) {
		if (bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath() != null) {
			Log.info("Removing old input file: " + bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath());
			new File(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath()).delete();
			bulkLoadFileHistory.getBulkLoadFile().setLocalFilePath(null);
			bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());
		}
		bulkLoadFileHistory.setErrorMessage(message);
		bulkLoadFileHistory.setBulkloadStatus(status);
		bulkLoadFileHistory.setLoadFinished(LocalDateTime.now());
		if (status != JobStatus.FINISHED) {
			slackNotifier.slackalert(bulkLoadFileHistory);
		}
		bulkLoadFileHistoryDAO.merge(bulkLoadFileHistory);
		bulkLoadFileHistory.getBulkLoad().setBulkloadStatus(status);
		bulkLoadDAO.merge(bulkLoadFileHistory.getBulkLoad());
		Log.info("Load File: " + bulkLoadFileHistory.getBulkLoadFile().getMd5Sum() + " is finished. Message: " + message + " Status: " + status);
	}

}
