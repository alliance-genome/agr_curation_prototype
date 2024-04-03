package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.dao.GeneGeneticInteractionDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.jobs.util.CsvSchemaBuilder;
import org.alliancegenome.curation_api.model.entities.GeneGeneticInteraction;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PsiMiTabDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.GeneInteractionService;
import org.alliancegenome.curation_api.services.GeneGeneticInteractionService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GeneGeneticInteractionExecutor extends LoadFileExecutor {

	@Inject
	GeneGeneticInteractionDAO geneGeneticInteractionDAO;
	@Inject
	GeneGeneticInteractionService geneGeneticInteractionService;
	@Inject
	GeneInteractionService geneInteractionService;
	
	public void runLoad(BulkLoadFile bulkLoadFile) {
		try {
			
			CsvSchema psiMiTabSchema = CsvSchemaBuilder.psiMiTabSchema();
			CsvMapper csvMapper = new CsvMapper();
			MappingIterator<PsiMiTabDTO> it = csvMapper.enable(CsvParser.Feature.INSERT_NULLS_FOR_MISSING_COLUMNS)
					.readerFor(PsiMiTabDTO.class).with(psiMiTabSchema)
					.readValues(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())));
			List<PsiMiTabDTO> interactionData = it.readAll();
			
			BulkLoadFileHistory history = new BulkLoadFileHistory(interactionData.size());
			
			List<Long> interactionIdsLoaded = new ArrayList<>();
			List<Long> interactionIdsBefore = geneGeneticInteractionDAO.findAllIds().getResults();
			
			runLoad(history, interactionData, interactionIdsLoaded);
			
			runCleanup(geneInteractionService, history, interactionIdsBefore, interactionIdsLoaded, bulkLoadFile.getMd5Sum());

			history.finishLoad();
			
			trackHistory(history, bulkLoadFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Gets called from the API directly
	public APIResponse runLoad(List<PsiMiTabDTO> interactions) {
		List<Long> interactionIdsLoaded = new ArrayList<>();
		
		BulkLoadFileHistory history = new BulkLoadFileHistory(interactions.size());
		runLoad(history, interactions, interactionIdsLoaded);
		history.finishLoad();
		
		return new LoadHistoryResponce(history);
	}

	
	private void runLoad(BulkLoadFileHistory history, List<PsiMiTabDTO> interactions, List<Long> idsAdded) {
		ProcessDisplayHelper ph = new ProcessDisplayHelper(2000);
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess("Gene Genetic Interaction DTO Update", interactions.size());
		for (PsiMiTabDTO dto : interactions) {
			try {
				GeneGeneticInteraction interaction = geneGeneticInteractionService.upsert(dto);
				if (interaction != null) {
					history.incrementCompleted();
					if (idsAdded != null)
						idsAdded.add(interaction.getId());
				} 
			} catch (ObjectUpdateException e) {
				history.incrementFailed();
				addException(history, e.getData());
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed();
				addException(history, new ObjectUpdateExceptionData(dto, e.getMessage(), e.getStackTrace()));
			}
	
			ph.progressProcess();
		}
		ph.finishProcess();

	}

}