package org.alliancegenome.curation_api.jobs.processors;

import java.io.File;
import java.time.OffsetDateTime;

import org.alliancegenome.curation_api.dao.loads.BulkFMSLoadDAO;
import org.alliancegenome.curation_api.dao.loads.BulkLoadDAO;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileDAO;
import org.alliancegenome.curation_api.dao.loads.BulkManualLoadDAO;
import org.alliancegenome.curation_api.dao.loads.BulkURLLoadDAO;
import org.alliancegenome.curation_api.enums.BulkLoadCleanUp;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.jobs.events.PendingBulkLoadFileJobEvent;
import org.alliancegenome.curation_api.jobs.executors.BulkLoadJobExecutor;
import org.alliancegenome.curation_api.jobs.util.SlackNotifier;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
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
	@Inject BulkManualLoadDAO bulkManualLoadDAO;
	@Inject BulkLoadFileDAO bulkLoadFileDAO;
	@Inject BulkFMSLoadDAO bulkFMSLoadDAO;
	@Inject BulkURLLoadDAO bulkURLLoadDAO;

	@Inject BulkLoadJobExecutor bulkLoadJobExecutor;

	@Inject SlackNotifier slackNotifier;

	@Inject Event<PendingBulkLoadFileJobEvent> pendingFileJobEvents;

	protected FileTransferHelper fileHelper = new FileTransferHelper();

//	private String processFMS(String dataType, String dataSubType) {
//		List<DataFile> files = fmsDataFileService.getDataFiles(dataType, dataSubType);
//
//		if (files.size() == 1) {
//			DataFile df = files.get(0);
//			return df.getS3Url();
//		} else {
//			Log.warn("Files: " + files);
//			Log.warn("Issue pulling files from the FMS: " + dataType + " " + dataSubType);
//		}
//		return null;
//	}

	public void syncWithS3(BulkLoadFile bulkLoadFile) {
		Log.info("Syncing with S3");
		Log.info("Local: " + bulkLoadFile.getLocalFilePath());
		Log.info("S3: " + bulkLoadFile.getS3Path());

		if ((bulkLoadFile.getS3Path() != null || bulkLoadFile.generateS3MD5Path() != null) && bulkLoadFile.getLocalFilePath() == null) {
			File outfile = fileHelper.downloadFileFromS3(s3AccessKey, s3SecretKey, s3Bucket, bulkLoadFile.getS3Path());
			if (outfile != null) {
				// log.info(outfile + " is of size: " + outfile.length());
				bulkLoadFile.setFileSize(outfile.length());
				bulkLoadFile.setLocalFilePath(outfile.getAbsolutePath());
			} else {
				// log.error("Failed to download file from S3 Path: " + s3PathPrefix + "/" +
				// bulkLoadFile.generateS3MD5Path());
				bulkLoadFile.setErrorMessage("Failed to download file from S3 Path: " + s3PathPrefix + "/" + bulkLoadFile.generateS3MD5Path());
				bulkLoadFile.setBulkloadStatus(JobStatus.FAILED);
				slackNotifier.slackalert(bulkLoadFile);
			}
			// log.info("Saving File: " + bulkLoadFile);
			bulkLoadFileDAO.merge(bulkLoadFile);
		} else if (bulkLoadFile.getS3Path() == null && bulkLoadFile.getLocalFilePath() != null) {
			if (s3AccessKey != null && s3AccessKey.length() > 0) {
				String s3Path = fileHelper.uploadFileToS3(s3AccessKey, s3SecretKey, s3Bucket, s3PathPrefix, bulkLoadFile.generateS3MD5Path(), new File(bulkLoadFile.getLocalFilePath()));
				bulkLoadFile.setS3Path(s3Path);
			}
			bulkLoadFileDAO.merge(bulkLoadFile);
		} else if (bulkLoadFile.getS3Path() == null && bulkLoadFile.getLocalFilePath() == null) {
			bulkLoadFile.setErrorMessage("Failed to download or upload file with S3 Path: " + s3PathPrefix + "/" + bulkLoadFile.generateS3MD5Path() + " Local and remote file missing");
			bulkLoadFile.setBulkloadStatus(JobStatus.FAILED);
			slackNotifier.slackalert(bulkLoadFile);
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

		if (bulkLoadFiles == null || bulkLoadFiles.getResults().size() == 0) {
			Log.info("Bulk File does not exist creating it");
			bulkLoadFile = new BulkLoadFile();
			bulkLoadFile.setBulkLoad(load);
			bulkLoadFile.setMd5Sum(md5Sum);
			bulkLoadFile.setFileSize(inputFile.length());
			if (load.getBulkloadStatus() == JobStatus.FORCED_RUNNING) {
				bulkLoadFile.setBulkloadStatus(JobStatus.FORCED_PENDING);
			}
			if (load.getBulkloadStatus() == JobStatus.SCHEDULED_RUNNING) {
				bulkLoadFile.setBulkloadStatus(JobStatus.SCHEDULED_PENDING);
			}
			if (load.getBulkloadStatus() == JobStatus.MANUAL_RUNNING) {
				bulkLoadFile.setBulkloadStatus(JobStatus.MANUAL_PENDING);
			}

			Log.info(load.getBulkloadStatus());

			bulkLoadFile.setLocalFilePath(localFilePath);
			if (cleanUp) {
				bulkLoadFile.setBulkloadCleanUp(BulkLoadCleanUp.YES);
			}
			bulkLoadFileDAO.persist(bulkLoadFile);
		} else if (load.getBulkloadStatus().isForced()) {
			bulkLoadFile = bulkLoadFiles.getResults().get(0);
			if (bulkLoadFile.getBulkloadStatus().isNotRunning()) {
				bulkLoadFile.setLocalFilePath(localFilePath);
				bulkLoadFile.setBulkloadStatus(JobStatus.FORCED_PENDING);
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

		if (!load.getLoadFiles().contains(bulkLoadFile)) {
			load.getLoadFiles().add(bulkLoadFile);
		}
		if (cleanUp) {
			bulkLoadFile.setBulkloadCleanUp(BulkLoadCleanUp.YES);
		}
		bulkLoadFileDAO.merge(bulkLoadFile);
		bulkLoadDAO.merge(load);
		Log.info("Firing Pending Bulk File Event: " + bulkLoadFile.getId());
		pendingFileJobEvents.fire(new PendingBulkLoadFileJobEvent(bulkLoadFile.getId()));
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

	protected void startLoadFile(BulkLoadFile bulkLoadFile) {
		bulkLoadFile.setBulkloadStatus(bulkLoadFile.getBulkloadStatus().getNextStatus());
		bulkLoadFileDAO.merge(bulkLoadFile);
		Log.info("Load File: " + bulkLoadFile.getMd5Sum() + " is running with file: " + bulkLoadFile.getLocalFilePath());
	}

	protected void endLoadFile(BulkLoadFile bulkLoadFile, String message, JobStatus status) {
		if (bulkLoadFile.getLocalFilePath() != null) {
			Log.info("Removing old input file: " + bulkLoadFile.getLocalFilePath());
			new File(bulkLoadFile.getLocalFilePath()).delete();
			bulkLoadFile.setLocalFilePath(null);
		}
		bulkLoadFile.setErrorMessage(message);
		bulkLoadFile.setBulkloadStatus(status);
		bulkLoadFile.setDateLastLoaded(OffsetDateTime.now());
		if (status != JobStatus.FINISHED) {
			slackNotifier.slackalert(bulkLoadFile);
		}
		bulkLoadFileDAO.merge(bulkLoadFile);
		Log.info("Load File: " + bulkLoadFile.getMd5Sum() + " is finished. Message: " + message + " Status: " + status);
	}

}
