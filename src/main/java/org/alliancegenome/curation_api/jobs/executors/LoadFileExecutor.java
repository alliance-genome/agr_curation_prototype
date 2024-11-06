package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.dao.loads.BulkLoadFileDAO;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileExceptionDAO;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileHistoryDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.exceptions.KnownIssueValidationException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.jobs.util.SlackNotifier;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileException;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.APIVersionInfoService;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.processing.LoadProcessDisplayService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;

public class LoadFileExecutor {

	@Inject protected ObjectMapper mapper;
	@Inject protected LoadProcessDisplayService loadProcessDisplayService;
	@Inject protected BulkLoadFileDAO bulkLoadFileDAO;
	@Inject protected BulkLoadFileHistoryDAO bulkLoadFileHistoryDAO;
	@Inject BulkLoadFileExceptionDAO bulkLoadFileExceptionDAO;
	@Inject APIVersionInfoService apiVersionInfoService;
	@Inject SlackNotifier slackNotifier;

	protected void updateHistory(BulkLoadFileHistory history) {
		bulkLoadFileHistoryDAO.merge(history);
	}

	protected void updateExceptions(BulkLoadFileHistory history) {
		//bulkLoadFileHistoryDAO.merge(history);
		for (BulkLoadFileException e : history.getExceptions()) {
			bulkLoadFileExceptionDAO.merge(e);
		}
	}

	protected void addException(BulkLoadFileHistory history, ObjectUpdateExceptionData objectUpdateExceptionData) {
		BulkLoadFileException exception = new BulkLoadFileException();
		exception.setException(objectUpdateExceptionData);
		exception.setBulkLoadFileHistory(history);
		//history.getExceptions().add(exception);
		bulkLoadFileExceptionDAO.persist(exception);
		//bulkLoadFileHistoryDAO.merge(history);
	}

	protected String getVersionNumber(String versionString) {
		if (StringUtils.isBlank(versionString)) {
			return null;
		}
		if (versionString.startsWith("v")) {
			return versionString.substring(1);
		}
		return versionString;
	}

	private List<Integer> getVersionParts(String version) {
		List<String> stringParts = new ArrayList<String>(Arrays.asList(version.split("\\.")));
		List<Integer> intParts = new ArrayList<Integer>();
		for (String part : stringParts) {
			try {
				Integer intPart = Integer.parseInt(part);
				intParts.add(intPart);
			} catch (NumberFormatException e) {
				return null;
			}
		}

		while (intParts.size() < 3) {
			intParts.add(0);
		}

		return intParts;
	}

	protected boolean checkSchemaVersion(BulkLoadFileHistory bulkLoadFileHistory, Class<?> dtoClass) {
		if (bulkLoadFileHistory.getBulkLoadFile().getLinkMLSchemaVersion() == null) {
			bulkLoadFileHistory.setErrorMessage("Missing Schema Version");
			bulkLoadFileHistory.setBulkloadStatus(JobStatus.FAILED);
			slackNotifier.slackalert(bulkLoadFileHistory);
			bulkLoadFileHistoryDAO.merge(bulkLoadFileHistory);
			return false;
		}
		if (!validSchemaVersion(bulkLoadFileHistory.getBulkLoadFile().getLinkMLSchemaVersion(), dtoClass)) {
			bulkLoadFileHistory.setErrorMessage("Invalid Schema Version: " + bulkLoadFileHistory.getBulkLoadFile().getLinkMLSchemaVersion());
			bulkLoadFileHistory.setBulkloadStatus(JobStatus.FAILED);
			slackNotifier.slackalert(bulkLoadFileHistory);
			bulkLoadFileHistoryDAO.merge(bulkLoadFileHistory);
			return false;
		}
		return true;
	}

	protected IngestDTO readIngestFile(BulkLoadFileHistory bulkLoadFileHistory, Class<?> dtoClass) {
		try {
			IngestDTO ingestDto = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath())), IngestDTO.class);
			bulkLoadFileHistory.getBulkLoadFile().setLinkMLSchemaVersion(getVersionNumber(ingestDto.getLinkMLVersion()));
			if (StringUtils.isNotBlank(ingestDto.getAllianceMemberReleaseVersion())) {
				bulkLoadFileHistory.getBulkLoadFile().setAllianceMemberReleaseVersion(ingestDto.getAllianceMemberReleaseVersion());
			}
			
			bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());
			
			if (!checkSchemaVersion(bulkLoadFileHistory, dtoClass)) {
				return null;
			}

			return ingestDto;
		} catch (Exception e) {
			failLoad(bulkLoadFileHistory, e);
			e.printStackTrace();
		}
		return null;
	}

	protected boolean validSchemaVersion(String submittedSchemaVersion, Class<?> dtoClass) {

		List<String> versionRange = apiVersionInfoService.getVersionRange(dtoClass.getAnnotation(AGRCurationSchemaVersion.class));
		List<Integer> minVersionParts = getVersionParts(versionRange.get(0));
		List<Integer> maxVersionParts = getVersionParts(versionRange.get(1));
		List<Integer> fileVersionParts = getVersionParts(submittedSchemaVersion);

		if (minVersionParts == null || maxVersionParts == null || fileVersionParts == null) {
			return false;
		}

		// check not lower than min version
		if (fileVersionParts.get(0) < minVersionParts.get(0)) {
			return false;
		}
		if (fileVersionParts.get(0).equals(minVersionParts.get(0))) {
			if (fileVersionParts.get(1) < minVersionParts.get(1)) {
				return false;
			}
			if (fileVersionParts.get(1).equals(minVersionParts.get(1))) {
				if (fileVersionParts.get(2) < minVersionParts.get(2)) {
					return false;
				}
			}
		}
		// check not higher than max version
		if (fileVersionParts.get(0) > maxVersionParts.get(0)) {
			return false;
		}
		if (fileVersionParts.get(0).equals(maxVersionParts.get(0))) {
			if (fileVersionParts.get(1) > maxVersionParts.get(1)) {
				return false;
			}
			if (fileVersionParts.get(1).equals(maxVersionParts.get(1))) {
				if (fileVersionParts.get(2) > maxVersionParts.get(2)) {
					return false;
				}
			}
		}

		return true;
	}

	public <E extends AuditedObject, T extends BaseDTO> APIResponse runLoadApi(BaseUpsertServiceInterface<E, T> service, String dataProviderName, List<T> objectList) {
		List<Long> idsLoaded = new ArrayList<>();
		BulkLoadFileHistory history = new BulkLoadFileHistory(objectList.size());
		history = bulkLoadFileHistoryDAO.persist(history);
		BackendBulkDataProvider dataProvider = null;
		if (dataProviderName != null) {
			dataProvider = BackendBulkDataProvider.valueOf(dataProviderName);
		}
		runLoad(service, history, dataProvider, objectList, idsLoaded, true);
		history.finishLoad();
		return new LoadHistoryResponce(history);
	}
	
	protected <E extends AuditedObject, T extends BaseDTO> boolean runLoad(BaseUpsertServiceInterface<E, T> service, BulkLoadFileHistory history, BackendBulkDataProvider dataProvider, List<T> objectList, List<Long> idsAdded) {
		return runLoad(service, history, dataProvider, objectList, idsAdded, true);
	}

	protected <E extends AuditedObject, T extends BaseDTO> boolean runLoad(BaseUpsertServiceInterface<E, T> service, BulkLoadFileHistory history, BackendBulkDataProvider dataProvider, List<T> objectList, List<Long> idsAdded, Boolean terminateFailing) {
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		if (CollectionUtils.isNotEmpty(objectList)) {
			String loadMessage = objectList.get(0).getClass().getSimpleName() + " update";
			if (dataProvider != null) {
				loadMessage = loadMessage + " for " + dataProvider.name();
			}
			ph.startProcess(loadMessage, objectList.size());
			
			updateHistory(history);
			for (T dtoObject : objectList) {
				try {
					E dbObject = service.upsert(dtoObject, dataProvider);
					history.incrementCompleted();
					if (idsAdded != null) {
						idsAdded.add(dbObject.getId());
					}
				} catch (ObjectUpdateException e) {
					// e.printStackTrace();
					history.incrementFailed();
					addException(history, e.getData());
				} catch (KnownIssueValidationException e) {
					Log.debug(e.getMessage());
					history.incrementSkipped();
				} catch (Exception e) {
					// e.printStackTrace();
					history.incrementFailed();
					addException(history, new ObjectUpdateExceptionData(dtoObject, e.getMessage(), e.getStackTrace()));
				}
				if (terminateFailing && history.getErrorRate() > 0.25) {
					Log.error("Failure Rate > 25% aborting load");
					updateHistory(history);
					updateExceptions(history);
					failLoadAboveErrorRateCutoff(history);
					return false;
				}
				ph.progressProcess();
			}
			updateHistory(history);
			updateExceptions(history);
			ph.finishProcess();
		}
		return true;
	}

	protected <S extends BaseEntityCrudService<?, ?>> void runCleanup(S service, BulkLoadFileHistory history, String dataProviderName, List<Long> annotationIdsBefore, List<Long> annotationIdsAfter, String loadTypeString) {
		runCleanup(service, history, dataProviderName, annotationIdsBefore, annotationIdsAfter, loadTypeString, true);
	}

	// The following methods are for bulk validation
	protected <S extends BaseEntityCrudService<?, ?>> void runCleanup(S service, BulkLoadFileHistory history, String dataProviderName, List<Long> annotationIdsBefore, List<Long> annotationIdsAfter, String loadTypeString, Boolean deprecate) {
		Log.debug("runLoad: After: " + dataProviderName + " " + annotationIdsAfter.size());

		List<Long> distinctAfter = annotationIdsAfter.stream().distinct().collect(Collectors.toList());
		Log.debug("runLoad: Distinct: " + dataProviderName + " " + distinctAfter.size());

		List<Long> idsToRemove = ListUtils.subtract(annotationIdsBefore, distinctAfter);
		Log.debug("runLoad: Remove: " + dataProviderName + " " + idsToRemove.size());

		String countType = loadTypeString + " Deleted";
		
		long existingDeletes = history.getCount(countType).getTotal() == null ? 0 : history.getCount(countType).getTotal();
		history.setCount(countType, idsToRemove.size() + existingDeletes);

		String loadDescription = dataProviderName + " " + loadTypeString + " bulk load (" + history.getBulkLoadFile().getMd5Sum() + ")";
		
		ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
		ph.startProcess("Deletion/deprecation of: " + dataProviderName + " " + loadTypeString, idsToRemove.size());
		//updateHistory(history);
		for (Long id : idsToRemove) {
			try {
				service.deprecateOrDelete(id, false, loadDescription, deprecate);
				history.incrementCompleted(countType);
			} catch (Exception e) {
				history.incrementFailed(countType);
				addException(history, new ObjectUpdateExceptionData("{ \"id\": " + id + "}", e.getMessage(), e.getStackTrace()));
			}
			if (history.getErrorRate(countType) > 0.25) {
				Log.error(countType + " failure rate > 25% aborting load");
				failLoadAboveErrorRateCutoff(history);
				break;
			}
			ph.progressProcess();
		}
		updateHistory(history);
		updateExceptions(history);
		ph.finishProcess();
	}

	protected void failLoad(BulkLoadFileHistory bulkLoadFileHistory, Exception e) {
		Set<String> errorMessages = new LinkedHashSet<String>();
		errorMessages.add(e.getMessage());
		errorMessages.add(e.getLocalizedMessage());
		Throwable cause = e.getCause();
		while (e.getCause() != null) {
			errorMessages.add(cause.getMessage());
			cause = cause.getCause();
		}
		bulkLoadFileHistory.setErrorMessage(String.join("|", errorMessages));
		bulkLoadFileHistory.setBulkloadStatus(JobStatus.FAILED);
		slackNotifier.slackalert(bulkLoadFileHistory);
		updateHistory(bulkLoadFileHistory);
	}
	
	protected void failLoadAboveErrorRateCutoff(BulkLoadFileHistory bulkLoadFileHistory) {
		bulkLoadFileHistory.setBulkloadStatus(JobStatus.FAILED);
		bulkLoadFileHistory.setErrorMessage("Failure rate exceeded cutoff");
		slackNotifier.slackalert(bulkLoadFileHistory);
		updateHistory(bulkLoadFileHistory);
	}
}
