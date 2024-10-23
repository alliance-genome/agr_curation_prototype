package org.alliancegenome.curation_api.jobs.executors;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.Gaf;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkURLLoad;
import org.alliancegenome.curation_api.model.ingest.dto.GafDTO;
import org.alliancegenome.curation_api.services.GafService;
import org.alliancegenome.curation_api.services.OrganizationService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

@JBossLog
@ApplicationScoped
public class GafExecutor extends LoadFileExecutor {

	@Inject
	GafService service;
	@Inject
	OrganizationService organizationService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) throws IOException {

		String url = ((BulkURLLoad) bulkLoadFileHistory.getBulkLoad()).getBulkloadUrl();

		String[] tok = url.split("/");
		String orgAbbrev = tok[tok.length - 1].toUpperCase();
		String abbr = orgAbbrev.split("\\.")[0];
		Organization organization = organizationService.getByAbbr(abbr).getEntity();

		// curie, List<GO curie>
		Map<String, List<String>> uiMap = new HashMap<>();
		Set<String> orgIDs = new HashSet<>();
		GZIPInputStream stream = new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath()));
		try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
			Stream<String> lines = br.lines();

			// Process each line
			lines.filter(s -> !s.startsWith("!") && StringUtils.isNotEmpty(s)).forEach(s -> {
				String[] token = s.split("\t");
				String orgID = token[0];
				orgIDs.add(orgID);
				String modID = token[1];
				String goID = token[4];
				if (abbr.equals(orgID)) {
					List<String> goIDs = uiMap.computeIfAbsent(modID, list -> new ArrayList<>());
					goIDs.add(goID);
				}
			});

		} catch (IOException e) {
			e.printStackTrace();
		}

		String name = bulkLoadFileHistory.getBulkLoad().getName();

		Map<Long, GafDTO> gafMap = service.getGafMap(organization);
		List<Long> gafIdsBefore = new ArrayList<>(gafMap.keySet().stream().toList());
		gafIdsBefore.removeIf(Objects::isNull);

		List<Long> geneGoIdsLoaded = new ArrayList<>();
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		List<GafDTO> dtos = uiMap.entrySet()
			.stream()
			.map(entry -> entry.getValue().stream().map(goID -> {
				GafDTO dto = new GafDTO();
				dto.setGeneID(abbr + ":" + entry.getKey());
				dto.setGoID(goID);
				return dto;
			}).toList()).flatMap(Collection::stream).toList();

		ph.startProcess(name, dtos.size());
		dtos.forEach(modID -> {
			Long geneID = service.getGeneID(modID, abbr);
			if (geneID != null) {
				Gaf newGaf = service.insert(modID, abbr).getEntity();
				if (newGaf != null) {
					geneGoIdsLoaded.add(newGaf.getId());
					bulkLoadFileHistory.incrementCompleted();
				} else {
					bulkLoadFileHistory.incrementSkipped();
				}
			} else {
				addException(bulkLoadFileHistory, new ObjectUpdateException.ObjectUpdateExceptionData(modID, "Could not find gene " + modID.getGeneID(), null));
				bulkLoadFileHistory.incrementFailed();
			}
			ph.progressProcess();
		});
		bulkLoadFileHistory.setTotalCount(dtos.size());
		runCleanupSpecial(service, bulkLoadFileHistory, abbr, gafIdsBefore, geneGoIdsLoaded, "GAF Load");
		ph.finishProcess();
		updateHistory(bulkLoadFileHistory);

		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);
	}

	private void runCleanupSpecial(GafService service, BulkLoadFileHistory history, String abbr, List<Long> gafIdsBefore, List<Long> geneGoIdsLoaded, String gafLoad) {
		Log.debug("runLoad: After: " + abbr + " " + geneGoIdsLoaded.size());

		List<Long> distinctAfter = geneGoIdsLoaded.stream().distinct().collect(Collectors.toList());
		Log.debug("runLoad: Distinct: " + abbr + " " + distinctAfter.size());

		List<Long> idsToRemove = ListUtils.subtract(gafIdsBefore, distinctAfter);
		Log.debug("runLoad: Remove: " + abbr + " " + idsToRemove.size());

		String countType = gafLoad + " Deleted";

		long existingDeletes = history.getCount(countType).getTotal() == null ? 0 : history.getCount(countType).getTotal();
		history.setCount(countType, idsToRemove.size() + existingDeletes);

		String loadDescription = abbr + " " + gafLoad + " bulk load (" + history.getBulkLoadFile().getMd5Sum() + ")";

		ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
		ph.startProcess("Deletion/deprecation of: " + abbr + " " + gafLoad, idsToRemove.size());
		//updateHistory(history);
		for (Long id : idsToRemove) {
			try {
				service.deprecateOrDelete(id, false, loadDescription, false);
				history.incrementCompleted(countType);
			} catch (Exception e) {
				history.incrementFailed(countType);
				addException(history, new ObjectUpdateException.ObjectUpdateExceptionData("{ \"id\": " + id + "}", e.getMessage(), e.getStackTrace()));
			}
			if (history.getErrorRate(countType) > 0.25) {
				Log.error(countType + " failure rate > 25% aborting load");
				failLoadAboveErrorRateCutoff(history);
				break;
			}
			ph.progressProcess();
		}
		updateHistory(history);
		updateExceptions(history);
		ph.finishProcess();

	}
}
