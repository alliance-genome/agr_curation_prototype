package org.alliancegenome.curation_api.jobs.executors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;
import org.alliancegenome.curation_api.model.entities.GeneExpressionExperiment;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.GeneExpressionIngestFmsDTO;
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
@JBossLog
public class GeneExpressionExecutor extends LoadFileExecutor {
	@Inject GeneExpressionAnnotationService geneExpressionAnnotationService;
	@Inject GeneExpressionExperimentService geneExpressionExperimentService;

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

			bulkLoadFileHistory.setCount(geneExpressionIngestFmsDTO.getData().size());
			updateHistory(bulkLoadFileHistory);

			List<Long> annotationIdsLoaded = new ArrayList<>();
			List<Long> annotationIdsBefore = geneExpressionAnnotationService.getAnnotationIdsByDataProvider(dataProvider);

			boolean success = runLoad(geneExpressionAnnotationService, bulkLoadFileHistory, dataProvider, geneExpressionIngestFmsDTO.getData(), annotationIdsLoaded);

			if (success) {
				runCleanup(geneExpressionAnnotationService, bulkLoadFileHistory, dataProvider.name(), annotationIdsBefore, annotationIdsLoaded, "gene expression annotation");
				loadExperiments(bulkLoadFileHistory);
			}

			bulkLoadFileHistory.finishLoad();
			updateHistory(bulkLoadFileHistory);
			updateExceptions(bulkLoadFileHistory);

		} catch (Exception e) {
			failLoad(bulkLoadFileHistory, e);
			e.printStackTrace();
		}
	}

	private void loadExperiments(BulkLoadFileHistory history) {
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		Map<String, Set<String>> experiments = geneExpressionAnnotationService.getExperiments();
		ph.startProcess("Saving experiments: ", experiments.size());
		for (String experimentId: experiments.keySet()) {
			try {
				GeneExpressionExperiment experiment = geneExpressionExperimentService.upsert(experimentId, experiments.get(experimentId));
				if (experiment != null) {
					history.incrementCompleted();
				}
			} catch (ObjectUpdateException e) {
				history.incrementFailed();
				addException(history, e.getData());
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed();
				addException(history, new ObjectUpdateException.ObjectUpdateExceptionData(experimentId, e.getMessage(), e.getStackTrace()));
			}
			ph.progressProcess();
		}
		updateHistory(history);
	}
}
