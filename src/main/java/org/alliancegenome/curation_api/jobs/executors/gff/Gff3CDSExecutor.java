package org.alliancegenome.curation_api.jobs.executors.gff;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.jobs.util.CsvSchemaBuilder;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.CodingSequenceService;
import org.alliancegenome.curation_api.services.Gff3Service;
import org.alliancegenome.curation_api.services.associations.codingSequenceAssociations.CodingSequenceGenomicLocationAssociationService;
import org.alliancegenome.curation_api.services.associations.transcriptAssociations.TranscriptCodingSequenceAssociationService;
import org.alliancegenome.curation_api.services.helpers.gff3.Gff3AttributesHelper;
import org.alliancegenome.curation_api.services.validation.dto.Gff3DtoValidator;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.lang3.tuple.ImmutablePair;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Gff3CDSExecutor extends Gff3Executor {

	@Inject Gff3Service gff3Service;
	@Inject CodingSequenceService cdsService;
	@Inject Gff3DtoValidator gff3DtoValidator;
	@Inject CodingSequenceGenomicLocationAssociationService cdsLocationService;
	@Inject TranscriptCodingSequenceAssociationService transcriptCdsService;
	
	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) {
		try {

			CsvSchema gff3Schema = CsvSchemaBuilder.gff3Schema();
			CsvMapper csvMapper = new CsvMapper();
			MappingIterator<Gff3DTO> it = csvMapper.enable(CsvParser.Feature.INSERT_NULLS_FOR_MISSING_COLUMNS).readerFor(Gff3DTO.class).with(gff3Schema).readValues(new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath())));
			List<Gff3DTO> gffData = it.readAll();
			List<String> gffHeaderData = new ArrayList<>();
			for (Gff3DTO gffLine : gffData) {
				if (gffLine.getSeqId().startsWith("#")) {
					gffHeaderData.add(gffLine.getSeqId());
				} else {
					break;
				}
			}
			gffData.subList(0, gffHeaderData.size()).clear();
			
			BulkFMSLoad fmsLoad = (BulkFMSLoad) bulkLoadFileHistory.getBulkLoad();
			BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(fmsLoad.getFmsDataSubType());

			List<ImmutablePair<Gff3DTO, Map<String, String>>> preProcessedCDSGffData = Gff3AttributesHelper.getCDSGffData(gffData, dataProvider);
			
			gffData.clear();

			List<Long> entityIdsAdded = new ArrayList<>();
			List<Long> locationIdsAdded = new ArrayList<>();
			List<Long> associationIdsAdded = new ArrayList<>();
			
			String assemblyId = loadGenomeAssemblyFromGFF(gffHeaderData);
			
			if (assemblyId == null) {
				addException(bulkLoadFileHistory, new ObjectUpdateExceptionData(null, "GFF Header does not contain assembly", null));
			}
			
			boolean success = runLoad(bulkLoadFileHistory, gffHeaderData, preProcessedCDSGffData, entityIdsAdded, locationIdsAdded, associationIdsAdded, dataProvider, assemblyId);

			if (success) {
				runCleanup(cdsService, bulkLoadFileHistory, dataProvider.name(), cdsService.getIdsByDataProvider(dataProvider), entityIdsAdded, "GFF coding sequence");
				runCleanup(cdsLocationService, bulkLoadFileHistory, dataProvider.name(), cdsLocationService.getIdsByDataProvider(dataProvider), locationIdsAdded, "GFF coding sequence genomic location association");
				runCleanup(transcriptCdsService, bulkLoadFileHistory, dataProvider.name(), transcriptCdsService.getIdsByDataProvider(dataProvider), associationIdsAdded, "GFF transcript coding sequence association");
			}
			bulkLoadFileHistory.finishLoad();
			updateHistory(bulkLoadFileHistory);
			updateExceptions(bulkLoadFileHistory);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean runLoad(
		BulkLoadFileHistory history,
		List<String> gffHeaderData,
		List<ImmutablePair<Gff3DTO, Map<String, String>>> gffData,
		List<Long> entityIdsAdded,
		List<Long> locationIdsAdded,
		List<Long> associationIdsAdded,
		BackendBulkDataProvider dataProvider, String assemblyId) {

		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess("GFF CDS update for " + dataProvider.name(), gffData.size());
		
		history.setCount("Entities", gffData.size());
		history.setCount("Locations", gffData.size());
		history.setCount("Associations", gffData.size());
		updateHistory(history);
		
		String countType = null;
		for (ImmutablePair<Gff3DTO, Map<String, String>> gff3EntryPair : gffData) {
			
			countType = "Entities";
			try {
				gff3DtoValidator.validateCdsEntry(gff3EntryPair.getKey(), gff3EntryPair.getValue(), entityIdsAdded, dataProvider);
				history.incrementCompleted(countType);
			} catch (ObjectUpdateException e) {
				history.incrementFailed(countType);
				addException(history, e.getData());
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed(countType);
				addException(history, new ObjectUpdateExceptionData(gff3EntryPair.getKey(), e.getMessage(), e.getStackTrace()));
			}
			if (assemblyId != null) {
				countType = "Locations";
				try {
					gff3Service.loadCDSLocationAssociations(gff3EntryPair, locationIdsAdded, dataProvider, assemblyId);
					history.incrementCompleted(countType);
				} catch (ObjectUpdateException e) {
					history.incrementFailed(countType);
					addException(history, e.getData());
				} catch (Exception e) {
					e.printStackTrace();
					history.incrementFailed(countType);
					addException(history, new ObjectUpdateExceptionData(gff3EntryPair.getKey(), e.getMessage(), e.getStackTrace()));
				}
			}
			countType = "Associations";
			try {
				gff3Service.loadCDSParentChildAssociations(gff3EntryPair, associationIdsAdded, dataProvider);
				history.incrementCompleted(countType);
			} catch (ObjectUpdateException e) {
				history.incrementFailed(countType);
				addException(history, e.getData());
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed(countType);
				addException(history, new ObjectUpdateExceptionData(gff3EntryPair.getKey(), e.getMessage(), e.getStackTrace()));
			}
			ph.progressProcess();
			if (Thread.currentThread().isInterrupted()) {
				history.setErrorMessage("Thread isInterrupted");
				throw new RuntimeException("Thread isInterrupted");
			}
		}
		updateHistory(history);
		ph.finishProcess();
		return true;
	}

	public APIResponse runLoadApi(String dataProviderName, String assemblyName, List<Gff3DTO> gffData) {
		List<Long> idsAdded = new ArrayList<>();
		BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(dataProviderName);
		List<ImmutablePair<Gff3DTO, Map<String, String>>> preProcessedCDSGffData = Gff3AttributesHelper.getCDSGffData(gffData, dataProvider);
		BulkLoadFileHistory history = new BulkLoadFileHistory();
		history = bulkLoadFileHistoryDAO.persist(history);
		runLoad(history, null, preProcessedCDSGffData, idsAdded, idsAdded, idsAdded, dataProvider, assemblyName);
		history.finishLoad();
		
		return new LoadHistoryResponce(history);
	}

}
