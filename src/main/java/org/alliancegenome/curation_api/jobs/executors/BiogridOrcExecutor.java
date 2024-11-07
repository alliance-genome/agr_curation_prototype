package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.ResourceDescriptorPageDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.jobs.util.CsvSchemaBuilder;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.BiogridOrcFmsDTO;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.CrossReferenceService;
import org.alliancegenome.curation_api.services.DataProviderService;
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
import jakarta.transaction.Transactional;

@ApplicationScoped
public class BiogridOrcExecutor extends LoadFileExecutor {

	@Inject
	ResourceDescriptorPageDAO resourceDescriptorPageDAO;

	@Inject
	GeneDAO geneDAO;

	@Inject
	CrossReferenceDAO crossRefDAO;

	@Inject
	CrossReferenceService crossReferenceService;

	//Todo: remove this and add to the service method once it's created
	@Transactional
	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) {
		try (TarArchiveInputStream tarInputStream = new TarArchiveInputStream(
				new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath())))) {
			TarArchiveEntry tarEntry;

			Set<String> biogridIds = new HashSet<>();

			HashMap<String, Object> rdpParams = new HashMap<>();
			rdpParams.put("name", "biogrid/orcs");
			ResourceDescriptorPage resourceDescriptorPage = resourceDescriptorPageDAO.findByParams(rdpParams)
					.getSingleResult();

			int index = 0;

			while ((tarEntry = tarInputStream.getNextEntry()) != null) {
				if (tarEntry.getName().equals("BIOGRID-ORCS-SCREEN_1558-1.1.16.screen.tab.txt")) {
					Log.debug("----------Starting file: -----------------------");
					Log.debug(tarEntry.getName());
				}
				Log.debug("----------------on loop number:----------");
				Log.debug(index);
				index++;

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
				runLoad(bulkLoadFileHistory, biogridData, resourceDescriptorPage);

			}
		} catch (Exception e) {
			failLoad(bulkLoadFileHistory, e);
			e.printStackTrace();
		}
	}

	private boolean runLoad(BulkLoadFileHistory history, List<BiogridOrcFmsDTO> biogridList, ResourceDescriptorPage resourceDescriptorPage) {
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		if (CollectionUtils.isNotEmpty(biogridList)) {
			try {
				String loadMessage = "BioGrid update";
				Set<String> referencedCuries = populateEntrezIdsFromFiles(biogridList, history);
				ph.startProcess(loadMessage, referencedCuries.size());
				updateHistory(history);

				Map<String, Long> genomicEntityCrossRefMap = crossReferenceService.getGenomicEntityCrossRefMap(referencedCuries);

				for (String referencedCurie : genomicEntityCrossRefMap.keySet()) {

					HashMap<String, Object> crossRefParams = new HashMap<>();
					crossRefParams.put("referencedCurie", referencedCurie);
					crossRefParams.put("displayName", referencedCurie);
					crossRefParams.put("resourceDescriptorPage.id", resourceDescriptorPage.getId());

					SearchResponse<CrossReference> crossRefDupSearch =
					crossRefDAO.findByParams(crossRefParams);

					if(!crossRefDupSearch.getResults().isEmpty()) continue;

					CrossReference newCrossRef = new CrossReference();
					newCrossRef.setReferencedCurie(referencedCurie);
					newCrossRef.setDisplayName("BioGRID CRISPR Screen Cell Line Phenotypes");
					newCrossRef.setResourceDescriptorPage(resourceDescriptorPage);

					crossRefDAO.persist(newCrossRef);

					crossRefDAO.persistAccessionGeneAssociated(newCrossRef.getId(), genomicEntityCrossRefMap.get(referencedCurie));


					history.incrementCompleted();

				}
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed();
				addException(history, new ObjectUpdateExceptionData(biogridOrcFmsDTO, e.getMessage(), e.getStackTrace()));
				ph.progressProcess();
			}
			history.incrementCompleted();
			updateHistory(history);
			updateExceptions(history);
			ph.finishProcess();
		}

		return true;
	}

	private Set<String> populateEntrezIdsFromFiles(List<BiogridOrcFmsDTO> biogridList, BulkLoadFileHistory history) {
		Set<String> biogridIds = new HashSet<>();

		for (BiogridOrcFmsDTO biogridOrcFmsDTO : biogridList) {
			try {
				if (!biogridOrcFmsDTO.getIdentifierType().equals("ENTREZ_GENE")){
					history.incrementSkipped();
					continue;
				}

				String identifier = "NCBI_Gene:" + biogridOrcFmsDTO.getIdentifierId();
				biogridIds.add(identifier);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return biogridIds;

	}
}