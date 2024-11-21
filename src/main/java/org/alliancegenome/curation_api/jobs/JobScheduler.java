package org.alliancegenome.curation_api.jobs;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.concurrent.Semaphore;

import org.alliancegenome.curation_api.dao.loads.BulkLoadDAO;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileDAO;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileExceptionDAO;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileHistoryDAO;
import org.alliancegenome.curation_api.dao.loads.BulkLoadGroupDAO;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.jobs.events.PendingBulkLoadJobEvent;
import org.alliancegenome.curation_api.jobs.events.PendingLoadJobEvent;
import org.alliancegenome.curation_api.jobs.events.StartedBulkLoadJobEvent;
import org.alliancegenome.curation_api.jobs.events.StartedLoadJobEvent;
import org.alliancegenome.curation_api.jobs.util.SlackNotifier;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadGroup;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkScheduledLoad;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;

@ApplicationScoped
public class JobScheduler {

	// @Inject
	// EventBus bus;

	@Inject Event<PendingBulkLoadJobEvent> pendingJobEvents;

	@Inject Event<StartedBulkLoadJobEvent> startedJobEvents;

	@Inject Event<StartedLoadJobEvent> startedFileJobEvents;

	
	@Inject BulkLoadFileDAO bulkLoadFileDAO;
	@Inject BulkLoadFileHistoryDAO bulkLoadFileHistoryDAO;
	@Inject BulkLoadGroupDAO groupDAO;
	@Inject BulkLoadDAO bulkLoadDAO;
	@Inject BulkLoadFileExceptionDAO bulkLoadFileExceptionDAO;
	@Inject SlackNotifier slackNotifier;

	@ConfigProperty(name = "bulk.data.loads.schedulingEnabled") Boolean loadSchedulingEnabled;

	@ConfigProperty(name = "reindex.schedulingEnabled", defaultValue = "false") Boolean reindexSchedulingEnabled;

	private ZonedDateTime lastCheck;
	private Semaphore sem = new Semaphore(1);

	@PostConstruct
	public void init() {
		// Set any running jobs to failed as the server has restarted
		// Log.info("Init: ");
		SearchResponse<BulkLoadGroup> groups = groupDAO.findAll();
		for (BulkLoadGroup g : groups.getResults()) {
			if (g.getLoads().size() > 0) {
				for (BulkLoad b : g.getLoads()) {
					boolean isFirst = true;
					for (BulkLoadFileHistory bfh : b.getHistory()) {
						BulkLoadFile bulkLoadFile = bfh.getBulkLoadFile();
						if (bfh.getBulkloadStatus() == null || bfh.getBulkloadStatus().isPending() || bfh.getBulkloadStatus().isRunning() || bfh.getBulkloadStatus().isStarted() || bulkLoadFile.getLocalFilePath() != null) {
							if (bulkLoadFile.getLocalFilePath() != null) {
								File file = new File(bulkLoadFile.getLocalFilePath());
								if (file.exists()) {
									file.delete();
								}
							}
							bulkLoadFile.setLocalFilePath(null);
							bfh.setErrorMessage("Failed due to server start up: Process never finished before the server restarted");
							bfh.setBulkloadStatus(JobStatus.FAILED);
							if (isFirst) {
								slackNotifier.slackalert(bfh); // Only notify on the first failed file not all the failed files under a load
							}
							bulkLoadFileDAO.merge(bulkLoadFile);
							bulkLoadFileHistoryDAO.merge(bfh);
						}
						isFirst = false;
					}
					if (b.getBulkloadStatus() == null) {
						b.setBulkloadStatus(JobStatus.STOPPED);
						bulkLoadDAO.merge(b);
					}
					if (b.getBulkloadStatus().isRunning()) {
						b.setErrorMessage("Failed due to server start up: Process never finished before the server restarted");
						b.setBulkloadStatus(JobStatus.FAILED);
						slackNotifier.slackalert(b);
						bulkLoadDAO.merge(b);
					}
				}
			}
		}
	}

	@Scheduled(every = "1s")
	public void scheduleCronGroupJobs() {
		if (loadSchedulingEnabled) {
			if (sem.tryAcquire()) {
				ZonedDateTime start = ZonedDateTime.now();
				// Log.info("scheduleGroupJobs: Scheduling Enabled: " + loadSchedulingEnabled);
				SearchResponse<BulkLoadGroup> groups = groupDAO.findAll();
				for (BulkLoadGroup g : groups.getResults()) {
					if (g.getLoads().size() > 0) {
						for (BulkLoad b : g.getLoads()) {
							if (b instanceof BulkScheduledLoad bsl) {
								if (bsl.getScheduleActive() != null && bsl.getScheduleActive() && bsl.getCronSchedule() != null && !bsl.getBulkloadStatus().isRunning()) {

									CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
									CronParser parser = new CronParser(cronDefinition);
									try {
										Cron unixCron = parser.parse(bsl.getCronSchedule());
										unixCron.validate();

										if (lastCheck != null) {
											ExecutionTime executionTime = ExecutionTime.forCron(unixCron);
											ZonedDateTime nextExecution = executionTime.nextExecution(lastCheck).get();

											if (lastCheck.isBefore(nextExecution) && start.isAfter(nextExecution)) {
												Log.info("Need to run Cron: " + bsl.getName());
												bsl.setSchedulingErrorMessage(null);
												bsl.setBulkloadStatus(JobStatus.SCHEDULED_PENDING);
												bulkLoadDAO.merge(bsl);
												pendingJobEvents.fireAsync(new PendingBulkLoadJobEvent(bsl.getId()));
											}
										}
									} catch (Exception e) {
										bsl.setSchedulingErrorMessage(e.getLocalizedMessage());
										bsl.setErrorMessage(e.getLocalizedMessage());
										bsl.setBulkloadStatus(JobStatus.FAILED);
										slackNotifier.slackalert(bsl);
										Log.error(e.getLocalizedMessage());
										bulkLoadDAO.merge(bsl);
									}
								}
							}
						}
					}
				}
				lastCheck = start;
				sem.release();
			} else {
				Log.debug("scheduleCronGroupJobs: unable to aquire lock");
			}
		}
	}

	public void pendingJobs(@ObservesAsync PendingBulkLoadJobEvent event) {
		BulkLoad load = bulkLoadDAO.find(event.getId());
		if (load != null) {
			if (load.getBulkloadStatus().isPending()) {
				load.setBulkloadStatus(load.getBulkloadStatus().getNextStatus());
				bulkLoadDAO.merge(load);
				Log.debug("Firing Start Job Event: " + load.getId());
				startedJobEvents.fireAsync(new StartedBulkLoadJobEvent(load.getId()));
			}
		}
	}

	public void pendingFileJobs(@ObservesAsync PendingLoadJobEvent event) {
		BulkLoadFileHistory fileLoadHistory = bulkLoadFileHistoryDAO.find(event.getId());
		if (fileLoadHistory != null) {
			if (fileLoadHistory.getBulkloadStatus().isPending()) {
				fileLoadHistory.setBulkloadStatus(fileLoadHistory.getBulkloadStatus().getNextStatus());
				bulkLoadFileHistoryDAO.merge(fileLoadHistory);
				Log.debug("Firing Start File History Job Event: " + fileLoadHistory.getId());
				startedFileJobEvents.fireAsync(new StartedLoadJobEvent(fileLoadHistory.getId()));
			}
		}
	}

	@Scheduled(cron = "0 0 0 ? * *")
	public void cleanUpFileExceptions() {
		bulkLoadFileExceptionDAO.cleanUpTwoWeekOldExceptions();
	}

	@Scheduled(cron = "0 0 0 ? * SUN")
	public void runMassIndexerEverything() {
		// Not sure what is going to happen when this time's out but should run anyway
		// Defaults taken from the API endpoint
		// DAO used doesn't matter they all have this method

		if (reindexSchedulingEnabled) {
			Log.info("Scheduled mass reindexing initiated.");
			bulkLoadDAO.reindexEverything(1000, 10000, 0, 4, 7200, 1);
		} else {
			Log.info("Scheduled mass reindexing not initiated (reindex scheduling not enabled).");
		}
	}

}
