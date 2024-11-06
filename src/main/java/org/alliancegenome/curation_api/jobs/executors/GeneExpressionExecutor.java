package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.GeneExpressionIngestFmsDTO;
import org.alliancegenome.curation_api.services.GeneExpressionAnnotationService;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GeneExpressionExecutor extends LoadFileExecutor {
	@Inject
	GeneExpressionAnnotationService geneExpressionAnnotationService;

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
			}

			bulkLoadFileHistory.finishLoad();
			updateHistory(bulkLoadFileHistory);
			updateExceptions(bulkLoadFileHistory);
		} catch (Exception e) {
			failLoad(bulkLoadFileHistory, e);
			e.printStackTrace();
		}
	}
}
