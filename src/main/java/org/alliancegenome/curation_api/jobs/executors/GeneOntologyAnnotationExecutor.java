package org.alliancegenome.curation_api.jobs.executors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.GeneOntologyAnnotation;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkURLLoad;
import org.alliancegenome.curation_api.model.ingest.dto.GeneOntologyAnnotationDTO;
import org.alliancegenome.curation_api.services.GeneOntologyAnnotationService;
import org.alliancegenome.curation_api.services.OrganizationService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

@JBossLog
@ApplicationScoped
public class GeneOntologyAnnotationExecutor extends LoadFileExecutor {

	@Inject
	GeneOntologyAnnotationService service;
	@Inject
	OrganizationService organizationService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) throws IOException {

		String url = ((BulkURLLoad) bulkLoadFileHistory.getBulkLoad()).getBulkloadUrl();

		String[] tok = url.split("/");
		String orgAbbrev = tok[tok.length - 1].toUpperCase();
		String abbrName = orgAbbrev.split("\\.")[0];
		String abbr;
		if (abbrName.contains("HUMAN")) {
			abbr = "HUMAN";
		} else if (abbrName.equalsIgnoreCase("xenbase")) {
			abbr = "XB";
		} else {
			abbr = abbrName;
		}
		Organization organization = organizationService.getByAbbr(abbr).getEntity();

		// curie, List<GO curie>
		Map<String, List<String>> uiMap = new HashMap<>();
		GZIPInputStream stream = new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath()));
		try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
			Stream<String> lines = br.lines();

			// Process each line
			lines.filter(s -> !s.startsWith("!") && StringUtils.isNotEmpty(s)).forEach(s -> {
				String[] token = s.split("\t");
				String orgID = token[0];
				String modID = token[1];
				String goID = token[4];
				if (abbr.equalsIgnoreCase(orgID) || orgID.equalsIgnoreCase("Xenbase") || abbr.equals("HUMAN") && orgID.equals("RGD")) {
					List<String> goIDs = uiMap.computeIfAbsent(modID, list -> new ArrayList<>());
					goIDs.add(goID);
				}
			});

		} catch (IOException e) {
			e.printStackTrace();
		}

		String name = bulkLoadFileHistory.getBulkLoad().getName();

		Map<Long, GeneOntologyAnnotationDTO> gafMap = service.getGafMap(organization);
		List<Long> gafIdsBefore = new ArrayList<>(gafMap.keySet().stream().toList());
		gafIdsBefore.removeIf(Objects::isNull);

		List<Long> geneGoIdsLoaded = new ArrayList<>();
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		List<GeneOntologyAnnotationDTO> dtos = uiMap.entrySet()
			.stream()
			.map(entry -> entry.getValue().stream().map(goID -> {
				GeneOntologyAnnotationDTO dto = new GeneOntologyAnnotationDTO();
				String prefix = abbr;
				if (abbr.equalsIgnoreCase("XB")) {
					prefix = "Xenbase";
				}
				if (abbr.equalsIgnoreCase("HUMAN")) {
					prefix = null;
				}
				String geneIdentifier = prefix != null ? prefix + ":" + entry.getKey() : entry.getKey();
				dto.setGeneIdentifier(geneIdentifier);
				dto.setGoTermCurie(goID);
				return dto;
			}).toList()).flatMap(Collection::stream).toList();

		ph.startProcess(name, dtos.size());
		for (GeneOntologyAnnotationDTO modID : dtos) {
			Long geneID = service.getGeneID(modID, abbr);
			if (geneID != null) {
				GeneOntologyAnnotation newGaf = service.insert(modID, abbr).getEntity();
				if (newGaf != null) {
					geneGoIdsLoaded.add(newGaf.getId());
					bulkLoadFileHistory.incrementCompleted();
				} else {
					bulkLoadFileHistory.incrementSkipped();
				}
			} else {
				addException(bulkLoadFileHistory, new ObjectUpdateException.ObjectUpdateExceptionData(modID, "Could not find gene " + modID.getGeneIdentifier(), null));
				bulkLoadFileHistory.incrementFailed();
			}
			ph.progressProcess();
		}
		bulkLoadFileHistory.setTotalCount(dtos.size());
		runCleanup(service, bulkLoadFileHistory, abbr, gafIdsBefore, geneGoIdsLoaded, "GAF Load");
		ph.finishProcess();
		updateHistory(bulkLoadFileHistory);

		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);
	}

}
