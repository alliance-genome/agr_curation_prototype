package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetAnnotation;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.HTPExpressionDatasetAnnotationIngestFmsDTO;
import org.apache.commons.lang3.StringUtils;

public class HTPExpressionDatasetSampleAnnotationExecutor extends LoadFileExecutor{
    public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) {
        try {
			BulkFMSLoad fms = (BulkFMSLoad) bulkLoadFileHistory.getBulkLoad();

            

			//HTPExpressionDatasetAnnotationIngestFmsDTO htpExpressionDatasetData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath())), HTPExpressionDatasetAnnotationIngestFmsDTO.class);
			//bulkLoadFileHistory.getBulkLoadFile().setRecordCount(htpExpressionDatasetData.getData().size());

			//AGRCurationSchemaVersion version = HTPExpressionDatasetAnnotation.class.getAnnotation(AGRCurationSchemaVersion.class);
			//bulkLoadFileHistory.getBulkLoadFile().setLinkMLSchemaVersion(version.max());
			// if (htpExpressionDatasetData.getMetaData() != null && StringUtils.isNotBlank(htpExpressionDatasetData.getMetaData().getRelease())) {
			// 	bulkLoadFileHistory.getBulkLoadFile().setAllianceMemberReleaseVersion(htpExpressionDatasetData.getMetaData().getRelease());
			// }

			// BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(fms.getFmsDataSubType());
			// List<Long> htpAnnotationsIdsLoaded = new ArrayList<>();
			// List<Long> previousIds = htpExpressionDatasetAnnotationService.getAnnotationIdsByDataProvider(dataProvider.name());
			
			// bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

			// bulkLoadFileHistory.setCount((long) htpExpressionDatasetData.getData().size());
			// updateHistory(bulkLoadFileHistory);
			
			// boolean success = runLoad(bulkLoadFileHistory, dataProvider, htpExpressionDatasetData.getData(), htpAnnotationsIdsLoaded);
			
			// if (success) {
			// 	runCleanup(htpExpressionDatasetAnnotationService, bulkLoadFileHistory, dataProvider.name(), previousIds, htpAnnotationsIdsLoaded, fms.getFmsDataType());
			// }
			// bulkLoadFileHistory.finishLoad();

			// updateHistory(bulkLoadFileHistory);
			// updateExceptions(bulkLoadFileHistory);

		} catch (Exception e) {
			failLoad(bulkLoadFileHistory, e);
			e.printStackTrace();
		}
    }
}
