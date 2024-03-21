package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Molecule;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PhenotypeFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PhenotypeIngestFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.PhenotypeAnnotationService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PhenotypeAnnotationExecutor extends LoadFileExecutor {

	@Inject
	PhenotypeAnnotationService phenotypeAnnotationService;

	public void runLoad(BulkLoadFile bulkLoadFile) {
		try {
			
			BulkFMSLoad fmsLoad = (BulkFMSLoad) bulkLoadFile.getBulkLoad();
			BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(fmsLoad.getFmsDataSubType());
			
			PhenotypeIngestFmsDTO phenotypeData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), PhenotypeIngestFmsDTO.class);
			bulkLoadFile.setRecordCount(phenotypeData.getData().size());
			if (bulkLoadFile.getLinkMLSchemaVersion() == null) {
				AGRCurationSchemaVersion version = Molecule.class.getAnnotation(AGRCurationSchemaVersion.class);
				bulkLoadFile.setLinkMLSchemaVersion(version.max());
			}
			if (phenotypeData.getMetaData() != null && StringUtils.isNotBlank(phenotypeData.getMetaData().getRelease()))
				bulkLoadFile.setAllianceMemberReleaseVersion(phenotypeData.getMetaData().getRelease());
			bulkLoadFileDAO.merge(bulkLoadFile);

			BulkLoadFileHistory history = new BulkLoadFileHistory(phenotypeData.getData().size());
			
			List<Long> annotationIdsLoaded = new ArrayList<>();
			List<Long> annotationIdsBefore = phenotypeAnnotationService.getAnnotationIdsByDataProvider(dataProvider);
			
			runLoad(history, phenotypeData.getData(), annotationIdsLoaded, dataProvider);
			
			runCleanup(phenotypeAnnotationService, history, dataProvider.name(), annotationIdsBefore, annotationIdsLoaded, "phenotype annotation", bulkLoadFile.getMd5Sum());

			history.finishLoad();
			
			trackHistory(history, bulkLoadFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Gets called from the API directly
	public APIResponse runLoad(String dataProviderName, List<PhenotypeFmsDTO> annotations) {
		List<Long> annotationIdsLoaded = new ArrayList<>();
		
		BulkLoadFileHistory history = new BulkLoadFileHistory(annotations.size());
		BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(dataProviderName);
		runLoad(history, annotations, annotationIdsLoaded, dataProvider);
		history.finishLoad();
		
		return new LoadHistoryResponce(history);
	}

	
	private void runLoad(BulkLoadFileHistory history, List<PhenotypeFmsDTO> annotations, List<Long> idsAdded, BackendBulkDataProvider dataProvider) {
		ProcessDisplayHelper ph = new ProcessDisplayHelper(2000);
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess("Phenotype annotation DTO Update for " + dataProvider.name(), annotations.size());

		loadPrimaryAnnotations(history, annotations, idsAdded, dataProvider, ph);
		loadSecondaryAnnotations(history, annotations, idsAdded, dataProvider, ph);
		
		ph.finishProcess();

	}
	
	private void loadSecondaryAnnotations(BulkLoadFileHistory history, List<PhenotypeFmsDTO> annotations, List<Long> idsAdded, BackendBulkDataProvider dataProvider, ProcessDisplayHelper ph) {
		for (PhenotypeFmsDTO dto : annotations) {
			if (CollectionUtils.isEmpty(dto.getPrimaryGeneticEntityIds()))
				continue;

			try {
				phenotypeAnnotationService.addInferredOrAssertedEntities(dto, idsAdded, dataProvider);
				history.incrementCompleted();
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
		
	}


	private void loadPrimaryAnnotations(BulkLoadFileHistory history, List<PhenotypeFmsDTO> annotations, List<Long> idsAdded, BackendBulkDataProvider dataProvider, ProcessDisplayHelper ph) {
		for (PhenotypeFmsDTO dto : annotations) {
			if (CollectionUtils.isNotEmpty(dto.getPrimaryGeneticEntityIds()))
				continue;

			try {
				Long primaryAnnotationId = phenotypeAnnotationService.upsertPrimaryAnnotation(dto, dataProvider);
				if (primaryAnnotationId != null) {
					history.incrementCompleted();
					if (idsAdded != null)
						idsAdded.add(primaryAnnotationId);
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
	}

}