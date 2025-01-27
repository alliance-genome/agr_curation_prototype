package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.dao.ExternalDataBaseEntityDAO;
import org.alliancegenome.curation_api.dao.HTPExpressionDatasetSampleAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetSampleAnnotation;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.HTPExpressionDatasetSampleAnnotationIngestFmsDTO;
import org.alliancegenome.curation_api.services.ExternalDataBaseEntityService;
import org.alliancegenome.curation_api.services.HTPExpressionDatasetSampleAnnotationService;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class HTPExpressionDatasetSampleAnnotationExecutor extends LoadFileExecutor {

	@Inject ExternalDataBaseEntityService externalDataBaseEntityService;
	@Inject ExternalDataBaseEntityDAO externalDataBaseEntityDAO;
	@Inject HTPExpressionDatasetSampleAnnotationService htpExpressionDatasetSampleAnnotationService;
	@Inject HTPExpressionDatasetSampleAnnotationDAO htpExpressionDatasetSampleAnnotationDAO;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) {
		try {
			BulkFMSLoad fms = (BulkFMSLoad) bulkLoadFileHistory.getBulkLoad();

			HTPExpressionDatasetSampleAnnotationIngestFmsDTO htpExpressionDatasetSampleData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath())), HTPExpressionDatasetSampleAnnotationIngestFmsDTO.class);
			bulkLoadFileHistory.getBulkLoadFile().setRecordCount(htpExpressionDatasetSampleData.getData().size());

			AGRCurationSchemaVersion version = HTPExpressionDatasetSampleAnnotation.class.getAnnotation(AGRCurationSchemaVersion.class);
			bulkLoadFileHistory.getBulkLoadFile().setLinkMLSchemaVersion(version.max());
			if (htpExpressionDatasetSampleData.getMetaData() != null && StringUtils.isNotBlank(htpExpressionDatasetSampleData.getMetaData().getRelease())) {
			bulkLoadFileHistory.getBulkLoadFile().setAllianceMemberReleaseVersion(htpExpressionDatasetSampleData.getMetaData().getRelease());
			}

			BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(fms.getFmsDataSubType());
			List<Long> htpAnnotationsIdsLoaded = new ArrayList<>();
			List<Long> previousIds = htpExpressionDatasetSampleAnnotationService.getAnnotationIdsByDataProvider(dataProvider.name());
			
			bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

			bulkLoadFileHistory.setCount((long) htpExpressionDatasetSampleData.getData().size());
			updateHistory(bulkLoadFileHistory);
			
			boolean success = runLoad(htpExpressionDatasetSampleAnnotationService, bulkLoadFileHistory, dataProvider, htpExpressionDatasetSampleData.getData(), htpAnnotationsIdsLoaded);
			
			if (success) {
				runCleanup(htpExpressionDatasetSampleAnnotationService, bulkLoadFileHistory, dataProvider.name(), previousIds, htpAnnotationsIdsLoaded, fms.getFmsDataType());
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
