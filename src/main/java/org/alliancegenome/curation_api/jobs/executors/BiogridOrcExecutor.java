package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.jobs.util.CsvSchemaBuilder;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.BiogridOrcFmsDTO;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BiogridOrcExecutor extends LoadFileExecutor {

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) {
		try (TarArchiveInputStream tarInputStream = new TarArchiveInputStream(
			new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath())))) {
			TarArchiveEntry tarEntry;

			Set<String> biogridIds = new HashSet<>();

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

				List<BiogridOrcFmsDTO> biogridData = it.readAll();
				runLoad(bulkLoadFileHistory, biogridData, biogridIds);

			}
		} catch (Exception e) {
			failLoad(bulkLoadFileHistory, e);
			e.printStackTrace();
		}
	}

	private boolean runLoad(BulkLoadFileHistory history, List<BiogridOrcFmsDTO> biogridList, Set<String> biogridIds) {
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		if (CollectionUtils.isNotEmpty(biogridList)) {
			String loadMessage = biogridList.get(0).getClass().getSimpleName() + " update";
			ph.startProcess(loadMessage, biogridList.size());

			updateHistory(history);
			for (BiogridOrcFmsDTO biogridOrcFmsDTO : biogridList) {
				try {
					if (biogridOrcFmsDTO.getIdentifierType().equals("ENTREZ_GENE")) {
						String identifier = "NCBI_Gene:" + biogridOrcFmsDTO.getIdentifierId();
						biogridIds.add(identifier);
						history.incrementCompleted();
					} else {
						history.incrementSkipped();

					}
				} catch (Exception e) {
					e.printStackTrace();
					history.incrementFailed();
					addException(history,
							new ObjectUpdateExceptionData(biogridOrcFmsDTO, e.getMessage(), e.getStackTrace()));
				}
				ph.progressProcess();
			}
			updateHistory(history);
			updateExceptions(history);
			ph.finishProcess();
		}

		return true;
	}
}