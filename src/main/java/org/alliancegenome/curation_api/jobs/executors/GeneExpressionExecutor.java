package org.alliancegenome.curation_api.jobs.executors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;
import org.alliancegenome.curation_api.model.entities.GeneExpressionExperiment;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.GeneExpressionFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.GeneExpressionIngestFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.GeneExpressionAnnotationService;
import org.alliancegenome.curation_api.services.GeneExpressionExperimentService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

@ApplicationScoped
public class GeneExpressionExecutor extends LoadFileExecutor {
	@Inject GeneExpressionAnnotationService geneExpressionAnnotationService;
	@Inject GeneExpressionExperimentService geneExpressionExperimentService;
	static final String ANNOTATIONS = "Annotations";
	static final String EXPERIMENTS = "Experiments";


	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) {
		try {
			BulkFMSLoad fms = (BulkFMSLoad) bulkLoadFileHistory.getBulkLoad();
			BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(fms.getFmsDataSubType());

			GeneExpressionIngestFmsDTO geneExpressionIngestFmsDTO = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath())), GeneExpressionIngestFmsDTO.class);
			bulkLoadFileHistory.getBulkLoadFile().setRecordCount(geneExpressionIngestFmsDTO.getData().size());

			AGRCurationSchemaVersion version = GeneExpressionAnnotation.class.getAnnotation(AGRCurationSchemaVersion.class);
			bulkLoadFileHistory.getBulkLoadFile().setLinkMLSchemaVersion(version.max());
			if (geneExpressionIngestFmsDTO.getMetaData() != null && StringUtils.isNotBlank(geneExpressionIngestFmsDTO.getMetaData().getRelease())) {
				bulkLoadFileHistory.getBulkLoadFile().setAllianceMemberReleaseVersion(geneExpressionIngestFmsDTO.getMetaData().getRelease());
			}
			bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

			bulkLoadFileHistory.setCount(ANNOTATIONS, geneExpressionIngestFmsDTO.getData().size());
			updateHistory(bulkLoadFileHistory);

			List<Long> annotationIdsLoaded = new ArrayList<>();
			List<Long> annotationIdsBefore = geneExpressionAnnotationService.getAnnotationIdsByDataProvider(dataProvider);

			List<Long> experimentIdsLoaded = new ArrayList<>();
			List<Long> experimentIdsBefore = geneExpressionExperimentService.getExperimentIdsByDataProvider(dataProvider);

			boolean success = runLoad(geneExpressionAnnotationService, bulkLoadFileHistory, dataProvider, geneExpressionIngestFmsDTO.getData(), annotationIdsLoaded, ANNOTATIONS);

			if (success) {
				runCleanup(geneExpressionAnnotationService, bulkLoadFileHistory, dataProvider.name(), annotationIdsBefore, annotationIdsLoaded, ANNOTATIONS);
				loadExperiments(bulkLoadFileHistory, dataProvider, experimentIdsLoaded);
				runCleanup(geneExpressionExperimentService, bulkLoadFileHistory, dataProvider.name(), experimentIdsBefore, experimentIdsLoaded, EXPERIMENTS);
			}

			bulkLoadFileHistory.finishLoad();
			updateHistory(bulkLoadFileHistory);
			updateExceptions(bulkLoadFileHistory);

		} catch (Exception e) {
			failLoad(bulkLoadFileHistory, e);
			e.printStackTrace();
		}
	}

	public APIResponse runLoadAPI(GeneExpressionAnnotationService service, String dataProviderName, List<GeneExpressionFmsDTO> objectList) {
		List<Long> idsLoaded = new ArrayList<>();
		BulkLoadFileHistory history = new BulkLoadFileHistory(objectList.size());
		history = bulkLoadFileHistoryDAO.persist(history);
		BackendBulkDataProvider dataProvider = null;
		if (dataProviderName != null) {
			dataProvider = BackendBulkDataProvider.valueOf(dataProviderName);
		}
		boolean success = runLoad(service, history, dataProvider, objectList, idsLoaded, true, "Records");
		if (success) {
			loadExperiments(history, dataProvider, new ArrayList<>());
		}
		history.finishLoad();
		return new LoadHistoryResponce(history);
	}

	private void loadExperiments(BulkLoadFileHistory history, BackendBulkDataProvider dataProvider, List<Long> experimentIdsLoaded) {
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		Map<String, Set<String>> experiments = geneExpressionAnnotationService.getExperiments();
		ph.startProcess("Saving gene expression experiments: ", experiments.size());
		history.setCount(EXPERIMENTS, geneExpressionAnnotationService.getExperiments().size());
		for (String experimentId: experiments.keySet()) {
			try {
				GeneExpressionExperiment experiment = geneExpressionExperimentService.upsert(experimentId, experiments.get(experimentId), dataProvider);
				if (experiment != null) {
					experimentIdsLoaded.add(experiment.getId());
					history.incrementCompleted(EXPERIMENTS);
				}
			} catch (ObjectUpdateException e) {
				history.incrementFailed(EXPERIMENTS);
				addException(history, e.getData());
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed(EXPERIMENTS);
				addException(history, new ObjectUpdateException.ObjectUpdateExceptionData(experimentId, e.getMessage(), e.getStackTrace()));
			}
			ph.progressProcess();
		}
		updateHistory(history);
	}
}
