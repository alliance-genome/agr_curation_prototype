package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.ResourceDescriptorPageDAO;
import org.alliancegenome.curation_api.jobs.util.CsvSchemaBuilder;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.BiogridOrcFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.CrossReferenceService;
import org.alliancegenome.curation_api.services.DataProviderService;
import org.alliancegenome.curation_api.services.OrganizationService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

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

	@Inject
	OrganizationService organizationService;

	@Inject
	DataProviderService dataProviderService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) {
		try (TarArchiveInputStream tarInputStream = new TarArchiveInputStream(
				new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath())))) {
			TarArchiveEntry tarEntry;

			List<BiogridOrcFmsDTO> biogridData = new ArrayList<>();
			String name = bulkLoadFileHistory.getBulkLoad().getName();
			String dataProviderName = name.substring(0, name.indexOf(" "));

			Organization organization = organizationService.getByAbbr(dataProviderName).getEntity();

			HashMap<String, Object> rdpParams = new HashMap<>();
			rdpParams.put("name", "biogrid/orcs");
			ResourceDescriptorPage resourceDescriptorPage = resourceDescriptorPageDAO.findByParams(rdpParams).getSingleResult();

			List<Long> dataProviderIdsBefore = new ArrayList<>(
					dataProviderService.getDataProviderMap(organization, resourceDescriptorPage)
							.values()
							.stream()
							.map(DataProvider::getId)
							.toList());

			dataProviderIdsBefore.removeIf(Objects::isNull);

			List<Long> dataProviderIdsLoaded = new ArrayList<>();

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
			runLoad(bulkLoadFileHistory, biogridData, resourceDescriptorPage, organization, dataProviderService,
					dataProviderIdsLoaded);

			runCleanup(dataProviderService, bulkLoadFileHistory, dataProviderName, dataProviderIdsBefore,
					dataProviderIdsLoaded, "Biogrid Orc Load Type");
		} catch (Exception e) {
			failLoad(bulkLoadFileHistory, e);
			e.printStackTrace();
		}
	}

	private boolean runLoad(BulkLoadFileHistory history, List<BiogridOrcFmsDTO> biogridList,
			ResourceDescriptorPage resourceDescriptorPage, Organization organization,
			DataProviderService dataProviderService, List<Long> dataProviderIdsLoaded) {
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		if (CollectionUtils.isNotEmpty(biogridList)) {
			try {
				String loadMessage = "BioGrid update";
				Set<String> referencedCuries = populateEntrezIdsFromFiles(biogridList, history);
				ph.startProcess(loadMessage, referencedCuries.size());
				updateHistory(history);

				Map<String, Long> genomicEntityCrossRefMap = crossReferenceService
						.getGenomicEntityCrossRefMap(referencedCuries);

				for (String referencedCurie : referencedCuries) {

					CrossReference newCrossRef = new CrossReference();
					newCrossRef.setReferencedCurie(referencedCurie);
					newCrossRef.setDisplayName("BioGRID CRISPR Screen Cell Line Phenotypes");
					newCrossRef.setResourceDescriptorPage(resourceDescriptorPage);

					DataProvider provider = new DataProvider();
					provider.setSourceOrganization(organization);
					provider.setCrossReference(newCrossRef);

					DataProvider entity = dataProviderService
							.insertBioGridOrcDataProvider(provider, genomicEntityCrossRefMap.get(referencedCurie))
							.getEntity();

					if (entity != null) {
						dataProviderIdsLoaded.add(entity.getId());
						history.incrementCompleted();
					} else {
						history.incrementSkipped();
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed();
				ph.progressProcess();
			}
			updateHistory(history);
			updateExceptions(history);
			ph.finishProcess();
		}

		return true;
	}

	public APIResponse runLoadApi(String dataProviderName, List<BiogridOrcFmsDTO> biogridDTOs) {
		List<Long> dataProviderIdsLoaded = new ArrayList<>();
		Organization organization = organizationService.getByAbbr(dataProviderName).getEntity();

		HashMap<String, Object> rdpParams = new HashMap<>();
		rdpParams.put("name", "biogrid/orcs");
		ResourceDescriptorPage resourceDescriptorPage = resourceDescriptorPageDAO.findByParams(rdpParams).getSingleResult();

		BulkLoadFileHistory history = new BulkLoadFileHistory(biogridDTOs.size());
		history = bulkLoadFileHistoryDAO.persist(history);
		runLoad(history, biogridDTOs, resourceDescriptorPage, organization, dataProviderService, dataProviderIdsLoaded);
		history.finishLoad();

		return new LoadHistoryResponce(history);
	}

	private Set<String> populateEntrezIdsFromFiles(List<BiogridOrcFmsDTO> biogridList, BulkLoadFileHistory history) {
		Set<String> biogridIds = new HashSet<>();

		for (BiogridOrcFmsDTO biogridOrcFmsDTO : biogridList) {
			try {
				if (!biogridOrcFmsDTO.getIdentifierType().equals("ENTREZ_GENE")) {
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