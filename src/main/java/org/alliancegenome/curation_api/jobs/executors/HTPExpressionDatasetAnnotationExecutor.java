package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.dao.ExternalDataBaseEntityDAO;
import org.alliancegenome.curation_api.dao.HTPExpressionDatasetAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetAnnotation;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.HTPExpressionDatasetAnnotationFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.HTPExpressionDatasetAnnotationIngestFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.ExternalDataBaseEntityService;
import org.alliancegenome.curation_api.services.HTPExpressionDatasetAnnotationService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.lang3.StringUtils;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class HTPExpressionDatasetAnnotationExecutor extends LoadFileExecutor {

	@Inject ExternalDataBaseEntityService externalDataBaseEntityService;
	@Inject ExternalDataBaseEntityDAO externalDataBaseEntityDAO;
	@Inject HTPExpressionDatasetAnnotationService htpExpressionDatasetAnnotationService;
	@Inject HTPExpressionDatasetAnnotationDAO htpExpressionDatasetAnnotationDAO;
	
	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) {
		try {
			BulkFMSLoad fms = (BulkFMSLoad) bulkLoadFileHistory.getBulkLoad();

			HTPExpressionDatasetAnnotationIngestFmsDTO htpExpressionDatasetData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath())), HTPExpressionDatasetAnnotationIngestFmsDTO.class);
			bulkLoadFileHistory.getBulkLoadFile().setRecordCount(htpExpressionDatasetData.getData().size());

			AGRCurationSchemaVersion version = HTPExpressionDatasetAnnotation.class.getAnnotation(AGRCurationSchemaVersion.class);
			bulkLoadFileHistory.getBulkLoadFile().setLinkMLSchemaVersion(version.max());
			if (htpExpressionDatasetData.getMetaData() != null && StringUtils.isNotBlank(htpExpressionDatasetData.getMetaData().getRelease())) {
				bulkLoadFileHistory.getBulkLoadFile().setAllianceMemberReleaseVersion(htpExpressionDatasetData.getMetaData().getRelease());
			}

			BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(fms.getFmsDataSubType());
			List<Long> htpAnnotationsIdsLoaded = new ArrayList<>();
			List<Long> previousIds = htpExpressionDatasetAnnotationService.getAnnotationIdsByDataProvider(dataProvider.name());
			
			bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

			bulkLoadFileHistory.setCount((long) htpExpressionDatasetData.getData().size());
			updateHistory(bulkLoadFileHistory);
			
			boolean success = runLoad(bulkLoadFileHistory, dataProvider, htpExpressionDatasetData.getData(), htpAnnotationsIdsLoaded);
			
			if (success) {
				runCleanup(htpExpressionDatasetAnnotationService, bulkLoadFileHistory, dataProvider.name(), previousIds, htpAnnotationsIdsLoaded, fms.getFmsDataType());
			}
			bulkLoadFileHistory.finishLoad();

			updateHistory(bulkLoadFileHistory);
			updateExceptions(bulkLoadFileHistory);

		} catch (Exception e) {
			failLoad(bulkLoadFileHistory, e);
			e.printStackTrace();
		}
	}

	private boolean runLoad(BulkLoadFileHistory history, BackendBulkDataProvider dataProvider, List<HTPExpressionDatasetAnnotationFmsDTO> htpDatasetAnnotations, List<Long> htpAnnotationsIdsLoaded) {
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess("HTP Expression Dataset Annotation DTO Update for " + dataProvider.name(), htpDatasetAnnotations.size() * 2);

		updateHistory(history);
		for (HTPExpressionDatasetAnnotationFmsDTO dto : htpDatasetAnnotations) {
			try {
				HTPExpressionDatasetAnnotation dbObject = htpExpressionDatasetAnnotationService.upsert(dto, dataProvider);
				history.incrementCompleted();
				if (dbObject != null) {
					htpAnnotationsIdsLoaded.add(dbObject.getId());
				}
			} catch (ObjectUpdateException e) {
				history.incrementFailed();
				addException(history, e.getData());
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed();
				addException(history, new ObjectUpdateExceptionData(dto, e.getMessage(), e.getStackTrace()));
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
		return true;
	}

	public APIResponse runLoadApi(String dataProviderName, List<HTPExpressionDatasetAnnotationFmsDTO> htpDataset) {
		List<Long> htpAnnotationsIdsLoaded = new ArrayList<>();

		BulkLoadFileHistory history = new BulkLoadFileHistory(htpDataset.size());
		BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(dataProviderName);
		history = bulkLoadFileHistoryDAO.persist(history);
		runLoad(history, dataProvider, htpDataset, htpAnnotationsIdsLoaded);
		history.finishLoad();

		return new LoadHistoryResponce(history);
	}
}