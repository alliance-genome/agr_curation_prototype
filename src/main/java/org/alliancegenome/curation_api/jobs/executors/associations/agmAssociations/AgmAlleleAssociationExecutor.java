package org.alliancegenome.curation_api.jobs.executors.associations.agmAssociations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.jobs.executors.LoadFileExecutor;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.model.ingest.dto.associations.agmAssociations.AgmAlleleAssociationDTO;
import org.alliancegenome.curation_api.services.associations.agmAssociations.AgmAlleleAssociationService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class AgmAlleleAssociationExecutor extends LoadFileExecutor {
	
	@Inject AgmAlleleAssociationService agmAlleleAssociationService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFileHistory.getBulkLoad();
		BackendBulkDataProvider dataProvider = manual.getDataProvider();
		log.info("Running with dataProvider: " + dataProvider.name());

		IngestDTO ingestDto = readIngestFile(bulkLoadFileHistory, AgmAlleleAssociationDTO.class);
		if (ingestDto == null) {
			return;
		}

		List<AgmAlleleAssociationDTO> associations = ingestDto.getAgmAlleleAssociationIngestSet();
		if (associations == null) {
			associations = new ArrayList<>();
		}

		List<Long> associationIdsLoaded = new ArrayList<>();
		List<Long> associationIdsBefore = new ArrayList<>();
		if (cleanUp) {
			associationIdsBefore.addAll(agmAlleleAssociationService.getAssociationsByDataProvider(dataProvider));
			associationIdsBefore.removeIf(Objects::isNull);
		}

		bulkLoadFileHistory.getBulkLoadFile().setRecordCount(associations.size() + bulkLoadFileHistory.getBulkLoadFile().getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

		bulkLoadFileHistory.setCount(associations.size());
		updateHistory(bulkLoadFileHistory);

		boolean success = runLoad(agmAlleleAssociationService, bulkLoadFileHistory, dataProvider, associations, associationIdsLoaded);
		if (success && cleanUp) {
			runCleanup(agmAlleleAssociationService, bulkLoadFileHistory, dataProvider.name(), associationIdsBefore, associationIdsLoaded, "agm allele association");
		}
		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);
	}
}
