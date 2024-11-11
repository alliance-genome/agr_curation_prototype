package org.alliancegenome.curation_api.jobs.executors;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class GeneExecutor extends LoadFileExecutor {

	@Inject GeneDAO geneDAO;

	@Inject GeneService geneService;

	@Inject NcbiTaxonTermService ncbiTaxonTermService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFileHistory.getBulkLoad();
		BackendBulkDataProvider dataProvider = manual.getDataProvider();
		log.info("Running with dataProvider : " + dataProvider.name());

		IngestDTO ingestDto = readIngestFile(bulkLoadFileHistory, GeneDTO.class);
		if (ingestDto == null) {
			return;
		}

		List<GeneDTO> genes = ingestDto.getGeneIngestSet();
		if (genes == null) {
			genes = new ArrayList<>();
		}

		List<Long> geneIdsLoaded = new ArrayList<>();
		List<Long> geneIdsBefore = new ArrayList<>();
		if (cleanUp) {
			geneIdsBefore.addAll(geneService.getIdsByDataProvider(dataProvider));
			log.debug("runLoad: Before: total " + geneIdsBefore.size());
		}

		bulkLoadFileHistory.getBulkLoadFile().setRecordCount(genes.size() + bulkLoadFileHistory.getBulkLoadFile().getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

		bulkLoadFileHistory.setCount(genes.size());
		updateHistory(bulkLoadFileHistory);
		
		boolean success = runLoad(geneService, bulkLoadFileHistory, dataProvider, genes, geneIdsLoaded);
		if (success && cleanUp) {
			runCleanup(geneService, bulkLoadFileHistory, dataProvider.name(), geneIdsBefore, geneIdsLoaded, "gene");
		}
		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);

	}

}
