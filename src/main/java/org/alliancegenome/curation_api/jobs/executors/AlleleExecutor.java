package org.alliancegenome.curation_api.jobs.executors;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.services.AlleleService;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AlleleExecutor extends LoadFileExecutor {

	@Inject AlleleDAO alleleDAO;
	@Inject AlleleService alleleService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFileHistory.getBulkLoad();
		Log.info("Running with: " + manual.getDataProvider().name());

		IngestDTO ingestDto = readIngestFile(bulkLoadFileHistory, AlleleDTO.class);
		if (ingestDto == null) {
			return;
		}

		List<AlleleDTO> alleles = ingestDto.getAlleleIngestSet();
		if (alleles == null) {
			alleles = new ArrayList<>();
		}

		BackendBulkDataProvider dataProvider = manual.getDataProvider();

		List<Long> alleleIdsLoaded = new ArrayList<>();
		List<Long> alleleIdsBefore = new ArrayList<>();
		if (cleanUp) {
			alleleIdsBefore.addAll(alleleService.getIdsByDataProvider(dataProvider.name()));
			Log.debug("runLoad: Before: total " + alleleIdsBefore.size());
		}

		bulkLoadFileHistory.getBulkLoadFile().setRecordCount(alleles.size() + bulkLoadFileHistory.getBulkLoadFile().getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

		bulkLoadFileHistory.setCount(alleles.size());
		updateHistory(bulkLoadFileHistory);
		
		boolean success = runLoad(alleleService, bulkLoadFileHistory, dataProvider, alleles, alleleIdsLoaded);
		if (success && cleanUp) {
			runCleanup(alleleService, bulkLoadFileHistory, dataProvider.name(), alleleIdsBefore, alleleIdsLoaded, "allele");
		}
		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);
	}

}
