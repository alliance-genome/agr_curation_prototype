package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.dao.PredictedVariantConsequenceDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.jobs.util.CsvSchemaBuilder;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.VepTxtDTO;
import org.alliancegenome.curation_api.services.PredictedVariantConsequenceService;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class VepTranscriptExecutor extends LoadFileExecutor {

	@Inject PredictedVariantConsequenceDAO predictedVariantConsequenceDAO;
	@Inject PredictedVariantConsequenceService predictedVariantConsequenceService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) {
		try {

			CsvSchema vepTxtSchema = CsvSchemaBuilder.vepTxtSchema();
			CsvMapper csvMapper = new CsvMapper();
			MappingIterator<VepTxtDTO> it = csvMapper.enable(CsvParser.Feature.INSERT_NULLS_FOR_MISSING_COLUMNS).readerFor(VepTxtDTO.class).with(vepTxtSchema).readValues(new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath())));
			List<VepTxtDTO> vepData = it.readAll();
			

			BulkFMSLoad fmsLoad = (BulkFMSLoad) bulkLoadFileHistory.getBulkLoad();
			BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(fmsLoad.getFmsDataSubType());

			List<Long> consequenceIdsLoaded = new ArrayList<>();
			List<Long> consequenceIdsBefore = predictedVariantConsequenceService.getIdsByDataProvider(dataProvider);
			
			bulkLoadFileHistory.setCount(vepData.size());
			updateHistory(bulkLoadFileHistory);
			
			boolean success = runLoad(predictedVariantConsequenceService, bulkLoadFileHistory, dataProvider, vepData, consequenceIdsLoaded);
			if (success) {
				runCleanup(predictedVariantConsequenceService, bulkLoadFileHistory, dataProvider.name(), consequenceIdsBefore, consequenceIdsLoaded, "predicted variant consequences");
			}
			bulkLoadFileHistory.finishLoad();
			updateHistory(bulkLoadFileHistory);
			updateExceptions(bulkLoadFileHistory);

		} catch (Exception e) {
			failLoad(bulkLoadFileHistory, e);
			e.printStackTrace();
		}
	}

}
