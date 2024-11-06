package org.alliancegenome.curation_api.jobs.executors;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.alliancegenome.curation_api.dao.AGMDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.AGMDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.services.AGMDiseaseAnnotationService;
import org.alliancegenome.curation_api.services.DiseaseAnnotationService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class AgmDiseaseAnnotationExecutor extends LoadFileExecutor {

	@Inject AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
	@Inject DiseaseAnnotationService diseaseAnnotationService;
	@Inject AGMDiseaseAnnotationService agmDiseaseAnnotationService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFileHistory.getBulkLoad();
		BackendBulkDataProvider dataProvider = manual.getDataProvider();
		log.info("Running with dataProvider: " + dataProvider.name());

		IngestDTO ingestDto = readIngestFile(bulkLoadFileHistory, AGMDiseaseAnnotationDTO.class);
		if (ingestDto == null) {
			return;
		}

		List<AGMDiseaseAnnotationDTO> annotations = ingestDto.getDiseaseAgmIngestSet();
		if (annotations == null) {
			annotations = new ArrayList<>();
		}

		List<Long> annotationIdsLoaded = new ArrayList<>();
		List<Long> annotationIdsBefore = new ArrayList<>();
		if (cleanUp) {
			annotationIdsBefore.addAll(agmDiseaseAnnotationService.getAnnotationIdsByDataProvider(dataProvider));
			annotationIdsBefore.removeIf(Objects::isNull);
		}

		bulkLoadFileHistory.getBulkLoadFile().setRecordCount(annotations.size() + bulkLoadFileHistory.getBulkLoadFile().getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

		bulkLoadFileHistory.setCount(annotations.size());
		updateHistory(bulkLoadFileHistory);
		
		boolean success = runLoad(agmDiseaseAnnotationService, bulkLoadFileHistory, dataProvider, annotations, annotationIdsLoaded);
		if (success && cleanUp) {
			runCleanup(diseaseAnnotationService, bulkLoadFileHistory, dataProvider.name(), annotationIdsBefore, annotationIdsLoaded, "AGM disease annotation");
		}
		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);
	}

}
