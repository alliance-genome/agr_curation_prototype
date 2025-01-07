package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.KnownIssueValidationException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.jobs.util.CsvSchemaBuilder;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.BiogridOrcFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BiogridOrcExecutor extends LoadFileExecutor {

	@Inject GeneService geneService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) {
		try (TarArchiveInputStream tarInputStream = new TarArchiveInputStream(
				new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath())))) {
			TarArchiveEntry tarEntry;

			List<BiogridOrcFmsDTO> biogridData = new ArrayList<>();
			String name = bulkLoadFileHistory.getBulkLoad().getName();
			String dataProviderName = name.substring(0, name.indexOf(" "));
			BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(dataProviderName);

			while ((tarEntry = tarInputStream.getNextEntry()) != null) {

				CsvMapper csvMapper = new CsvMapper();
				CsvSchema biogridOrcFmsSchema = CsvSchemaBuilder.biogridOrcFmsSchema();
				String regexPattern = "BIOGRID-ORCS-SCREEN_(\\d+)-1.1.16.screen.tab.txt";
				Pattern pattern = Pattern.compile(regexPattern);

				Matcher matcher = pattern.matcher(tarEntry.getName());

				if (tarEntry.isDirectory() || !matcher.matches()) {
					continue;
				}

				MappingIterator<BiogridOrcFmsDTO> it = csvMapper
						.enable(CsvParser.Feature.INSERT_NULLS_FOR_MISSING_COLUMNS)
						.readerFor(BiogridOrcFmsDTO.class)
						.with(biogridOrcFmsSchema)
						.readValues(tarInputStream.readAllBytes());

				biogridData.addAll(it.readAll());

			}
			runLoad(bulkLoadFileHistory, biogridData, dataProvider);
			
			bulkLoadFileHistory.finishLoad();
			updateHistory(bulkLoadFileHistory);
			updateExceptions(bulkLoadFileHistory);

		} catch (Exception e) {
			failLoad(bulkLoadFileHistory, e);
			e.printStackTrace();
		}
	}

	private void runLoad(BulkLoadFileHistory history, List<BiogridOrcFmsDTO> biogridList, BackendBulkDataProvider dataProvider) {
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		if (CollectionUtils.isNotEmpty(biogridList)) {
			Set<String> entrezIds = populateEntrezIdsFromFiles(biogridList);
			
			String loadMessage = "BioGRID-ORCS cross-reference update";
			if (dataProvider != null) {
				loadMessage = loadMessage + " for " + dataProvider.name();
			}
			ph.startProcess(loadMessage, entrezIds.size());
			
			history.setCount(entrezIds.size());
			updateHistory(history);
			
			for (String entrezId : entrezIds) {
				try {
					geneService.addBiogridXref(entrezId, dataProvider);
					history.incrementCompleted();
				} catch (ObjectUpdateException e) {
					history.incrementFailed();
					addException(history, e.getData());
				} catch (KnownIssueValidationException e) {
					Log.debug(e.getMessage());
					history.incrementSkipped();
				} catch (Exception e) {
					e.printStackTrace();
					history.incrementFailed();
					addException(history, new ObjectUpdateExceptionData(entrezId, e.getMessage(), e.getStackTrace()));
				}
				if (history.getErrorRate() > 0.25) {
					Log.error("Failure Rate > 25% aborting load");
					updateHistory(history);
					updateExceptions(history);
					failLoadAboveErrorRateCutoff(history);
					return;
				}
				ph.progressProcess();
				if (Thread.currentThread().isInterrupted()) {
					history.setErrorMessage("Thread isInterrupted");
					throw new RuntimeException("Thread isInterrupted");
				}
			}
			
			updateHistory(history);
			updateExceptions(history);
			ph.finishProcess();
			
		}
	}

	public APIResponse runLoadApi(String dataProviderName, List<BiogridOrcFmsDTO> biogridDTOs) {
		BulkLoadFileHistory history = new BulkLoadFileHistory(biogridDTOs.size());
		history = bulkLoadFileHistoryDAO.persist(history);
		BackendBulkDataProvider dataProvider = null;
		if (dataProviderName != null) {
			dataProvider = BackendBulkDataProvider.valueOf(dataProviderName);
		}
		runLoad(history, biogridDTOs, dataProvider);
		history.finishLoad();

		return new LoadHistoryResponce(history);
	}

	private Set<String> populateEntrezIdsFromFiles(List<BiogridOrcFmsDTO> biogridList) {
		Set<String> biogridIds = new HashSet<>();

		for (BiogridOrcFmsDTO biogridOrcFmsDTO : biogridList) {
			if (biogridOrcFmsDTO.getIdentifierType().equals("ENTREZ_GENE")) {
				String identifier = biogridOrcFmsDTO.getIdentifierId();
				biogridIds.add(identifier);
			}
		}

		return biogridIds;

	}
}