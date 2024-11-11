package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.dao.orthology.GeneToGeneOrthologyGeneratedDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyGenerated;
import org.alliancegenome.curation_api.model.ingest.dto.fms.OrthologyIngestFmsDTO;
import org.alliancegenome.curation_api.services.orthology.GeneToGeneOrthologyGeneratedService;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class OrthologyExecutor extends LoadFileExecutor {

	@Inject GeneToGeneOrthologyGeneratedService generatedOrthologyService;
	@Inject GeneToGeneOrthologyGeneratedDAO generatedOrthologyDAO;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) {
		try {
			BulkFMSLoad fms = (BulkFMSLoad) bulkLoadFileHistory.getBulkLoad();

			OrthologyIngestFmsDTO orthologyData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath())), OrthologyIngestFmsDTO.class);
			bulkLoadFileHistory.getBulkLoadFile().setRecordCount(orthologyData.getData().size());

			AGRCurationSchemaVersion version = GeneToGeneOrthologyGenerated.class.getAnnotation(AGRCurationSchemaVersion.class);
			bulkLoadFileHistory.getBulkLoadFile().setLinkMLSchemaVersion(version.max());
			if (orthologyData.getMetaData() != null && StringUtils.isNotBlank(orthologyData.getMetaData().getRelease())) {
				bulkLoadFileHistory.getBulkLoadFile().setAllianceMemberReleaseVersion(orthologyData.getMetaData().getRelease());
			}

			List<Long> orthoPairIdsLoaded = new ArrayList<>();
			BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(fms.getFmsDataSubType());
			List<Long> orthoPairIdsBefore = generatedOrthologyService.getAllOrthologyPairIdsBySubjectGeneDataProvider(dataProvider);
			log.debug("runLoad: Before: total " + orthoPairIdsBefore.size());

			bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

			bulkLoadFileHistory.setCount(orthologyData.getData().size());
			updateHistory(bulkLoadFileHistory);

			boolean success = runLoad(generatedOrthologyService, bulkLoadFileHistory, dataProvider, orthologyData.getData(), orthoPairIdsLoaded);
			if (success) {
				runCleanup(generatedOrthologyService, bulkLoadFileHistory, fms.getFmsDataSubType(), orthoPairIdsBefore, orthoPairIdsLoaded, fms.getFmsDataType(), false);
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
