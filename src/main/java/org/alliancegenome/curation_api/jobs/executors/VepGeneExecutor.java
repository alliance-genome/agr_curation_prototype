package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.dao.PredictedVariantConsequenceDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.KnownIssueValidationException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.jobs.util.CsvSchemaBuilder;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.VepTxtDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.PredictedVariantConsequenceService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class VepGeneExecutor extends LoadFileExecutor {

	@Inject PredictedVariantConsequenceDAO predictedVariantConsequenceDAO;
	@Inject PredictedVariantConsequenceService predictedVariantConsequenceService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) {
		try {

			CsvSchema vepTxtSchema = CsvSchemaBuilder.vepTxtSchema();
			CsvMapper csvMapper = new CsvMapper();
			MappingIterator<VepTxtDTO> it = csvMapper.enable(CsvParser.Feature.INSERT_NULLS_FOR_MISSING_COLUMNS).readerFor(VepTxtDTO.class).with(vepTxtSchema).readValues(new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath())));
			List<VepTxtDTO> vepData = it.readAll();
			

			BulkFMSLoad fmsLoad = (BulkFMSLoad) bulkLoadFileHistory.getBulkLoad();
			BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(fmsLoad.getFmsDataSubType());

			List<Long> consequenceIdsLoaded = new ArrayList<>();
			List<Long> consequenceIdsBefore = predictedVariantConsequenceService.getGeneLevelIdsByDataProvider(dataProvider);
			
			bulkLoadFileHistory.setCount(vepData.size());
			updateHistory(bulkLoadFileHistory);
			
			boolean success = runLoad(bulkLoadFileHistory, dataProvider, vepData, consequenceIdsLoaded);
			if (success) {
				runCleanup(predictedVariantConsequenceService, bulkLoadFileHistory, dataProvider.name(), consequenceIdsBefore, consequenceIdsLoaded, "gene-level predicted variant consequences");
			}
			bulkLoadFileHistory.finishLoad();
			updateHistory(bulkLoadFileHistory);
			updateExceptions(bulkLoadFileHistory);

		} catch (Exception e) {
			failLoad(bulkLoadFileHistory, e);
			e.printStackTrace();
		}
	}
	
	protected boolean runLoad(BulkLoadFileHistory history, BackendBulkDataProvider dataProvider, List<VepTxtDTO> objectList, List<Long> idsUpdated) {
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		if (CollectionUtils.isNotEmpty(objectList)) {
			String loadMessage = objectList.get(0).getClass().getSimpleName() + " update";
			if (dataProvider != null) {
				loadMessage = loadMessage + " for " + dataProvider.name();
			}
			ph.startProcess(loadMessage, objectList.size());
			
			updateHistory(history);
			for (VepTxtDTO dtoObject : objectList) {
				try {
					Long idUpdated = predictedVariantConsequenceService.updateGeneLevelConsequence(dtoObject);
					history.incrementCompleted();
					if (idsUpdated != null) {
						idsUpdated.add(idUpdated);
					}
				} catch (ObjectUpdateException e) {
					history.incrementFailed();
					addException(history, e.getData());
				} catch (KnownIssueValidationException e) {
					Log.debug(e.getMessage());
					history.incrementSkipped();
				} catch (Exception e) {
					e.printStackTrace();
					history.incrementFailed();
					addException(history, new ObjectUpdateExceptionData(dtoObject, e.getMessage(), e.getStackTrace()));
				}
				if (history.getErrorRate() > 0.25) {
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
	
	protected void runCleanup(BulkLoadFileHistory history, String dataProviderName, List<Long> annotationIdsBefore, List<Long> annotationIdsAfter, String loadTypeString, Boolean deprecate) {
		Log.debug("runLoad: After: " + dataProviderName + " " + annotationIdsAfter.size());

		List<Long> distinctAfter = annotationIdsAfter.stream().distinct().collect(Collectors.toList());
		Log.debug("runLoad: Distinct: " + dataProviderName + " " + distinctAfter.size());

		List<Long> idsToReset = ListUtils.subtract(annotationIdsBefore, distinctAfter);
		Log.debug("runLoad: Reset: " + dataProviderName + " " + idsToReset.size());

		String countType = loadTypeString + " reset";
		
		long existingResets = history.getCount(countType).getTotal() == null ? 0 : history.getCount(countType).getTotal();
		history.setCount(countType, idsToReset.size() + existingResets);

		String loadDescription = dataProviderName + " " + loadTypeString + " bulk load (" + history.getBulkLoadFile().getMd5Sum() + ")";
		
		ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
		ph.startProcess("Deletion/deprecation of: " + dataProviderName + " " + loadTypeString, idsToReset.size());
		
		for (Long id : idsToReset) {
			try {
				predictedVariantConsequenceService.resetGeneLevelConsequence(id, loadDescription);
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

	public APIResponse runLoadApi(String dataProviderName, List<VepTxtDTO> consequenceData) {
		List<Long> idsLoaded = new ArrayList<>();
		BulkLoadFileHistory history = new BulkLoadFileHistory(consequenceData.size());
		history = bulkLoadFileHistoryDAO.persist(history);
		BackendBulkDataProvider dataProvider = null;
		if (dataProviderName != null) {
			dataProvider = BackendBulkDataProvider.valueOf(dataProviderName);
		}
		runLoad(history, dataProvider, consequenceData, idsLoaded);
		history.finishLoad();
		return new LoadHistoryResponce(history);
	}

}
