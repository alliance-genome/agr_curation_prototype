package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.dao.GeneToGeneParalogyDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.GeneToGeneParalogy;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.ParalogyIngestFmsDTO;
import org.alliancegenome.curation_api.services.GeneToGeneParalogyService;
import org.apache.commons.lang3.StringUtils;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ParalogyExecutor extends LoadFileExecutor {

	@Inject GeneToGeneParalogyService geneToGeneParalogyService;
	@Inject GeneToGeneParalogyDAO geneToGeneParalogyDAO;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) {
		try {
			BulkFMSLoad fms = (BulkFMSLoad) bulkLoadFileHistory.getBulkLoad();

			ParalogyIngestFmsDTO paralogyData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath())), ParalogyIngestFmsDTO.class);
			bulkLoadFileHistory.getBulkLoadFile().setRecordCount(paralogyData.getData().size());

			AGRCurationSchemaVersion version = GeneToGeneParalogy.class.getAnnotation(AGRCurationSchemaVersion.class);
			bulkLoadFileHistory.getBulkLoadFile().setLinkMLSchemaVersion(version.max());
			if (paralogyData.getMetaData() != null && StringUtils.isNotBlank(paralogyData.getMetaData().getRelease())) {
				bulkLoadFileHistory.getBulkLoadFile().setAllianceMemberReleaseVersion(paralogyData.getMetaData().getRelease());
			}

			List<Long> paralogyIdsLoaded = new ArrayList<>();
			BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(fms.getFmsDataSubType());
			List<Long> paralogyPairsBefore = geneToGeneParalogyService.getAllParalogyPairIdsBySubjectGeneDataProvider(dataProvider);
			Log.debug("runLoad: Before: total " + paralogyPairsBefore.size());

			bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

			bulkLoadFileHistory.setCount(paralogyData.getData().size());
			updateHistory(bulkLoadFileHistory);

			boolean success = runLoad(geneToGeneParalogyService, bulkLoadFileHistory, dataProvider, paralogyData.getData(), paralogyIdsLoaded, false);

			if (success) {
				runCleanup(geneToGeneParalogyService, bulkLoadFileHistory, fms.getFmsDataSubType(), paralogyPairsBefore, paralogyIdsLoaded, fms.getFmsDataType(), false);
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
