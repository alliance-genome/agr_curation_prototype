package org.alliancegenome.curation_api.jobs.executors.associations.agmAssociations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.jobs.executors.LoadFileExecutor;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.model.ingest.dto.associations.agmAssociations.AgmSequenceTargetingReagentAssociationDTO;
import org.alliancegenome.curation_api.services.associations.agmAssociations.AgmStrAssociationService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class AgmStrAssociationExecutor extends LoadFileExecutor {

	@Inject AgmStrAssociationService agmStrAssociationService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFileHistory.getBulkLoad();
		BackendBulkDataProvider dataProvider = manual.getDataProvider();
		log.info("Running with dataProvider: " + dataProvider.name());

		IngestDTO ingestDto = readIngestFile(bulkLoadFileHistory, AgmSequenceTargetingReagentAssociationDTO.class);
		if (ingestDto == null) {
			return;
		}

		List<AgmSequenceTargetingReagentAssociationDTO> associations = ingestDto.getAgmStrAssociationIngestSet();
		if (associations == null) {
			associations = new ArrayList<>();
		}

		List<Long> associationIdsLoaded = new ArrayList<>();
		List<Long> associationIdsBefore = new ArrayList<>();
		if (cleanUp) {
			associationIdsBefore.addAll(agmStrAssociationService.getAssociationsByDataProvider(dataProvider));
			associationIdsBefore.removeIf(Objects::isNull);
		}

		bulkLoadFileHistory.getBulkLoadFile().setRecordCount(associations.size() + bulkLoadFileHistory.getBulkLoadFile().getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

		bulkLoadFileHistory.setCount(associations.size());
		updateHistory(bulkLoadFileHistory);
		
		boolean success = runLoad(agmStrAssociationService, bulkLoadFileHistory, dataProvider, associations, associationIdsLoaded);
		if (success && cleanUp) {
			runCleanup(agmStrAssociationService, bulkLoadFileHistory, dataProvider.name(), associationIdsBefore, associationIdsLoaded, "agm str association");
		}
		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);
	}

}
