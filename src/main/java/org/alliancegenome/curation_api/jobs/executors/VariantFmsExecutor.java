package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.entities.associations.variantAssociations.CuratedVariantGenomicLocationAssociation;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.VariantFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.VariantIngestFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.VariantService;
import org.alliancegenome.curation_api.services.associations.alleleAssociations.AlleleVariantAssociationService;
import org.alliancegenome.curation_api.services.associations.variantAssociations.CuratedVariantGenomicLocationAssociationService;
import org.alliancegenome.curation_api.services.validation.dto.fms.VariantFmsDTOValidator;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class VariantFmsExecutor extends LoadFileExecutor {

	@Inject VariantService variantService;
	@Inject CuratedVariantGenomicLocationAssociationService curatedVariantGenomicLocationAssociationService;
	@Inject AlleleVariantAssociationService alleleVariantAssociationService;
	@Inject VariantFmsDTOValidator variantFmsDtoValidator;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) {
		try {
			BulkFMSLoad fms = (BulkFMSLoad) bulkLoadFileHistory.getBulkLoad();
		
			VariantIngestFmsDTO variantData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath())), VariantIngestFmsDTO.class);
			
			AGRCurationSchemaVersion version = CuratedVariantGenomicLocationAssociation.class.getAnnotation(AGRCurationSchemaVersion.class);
			bulkLoadFileHistory.getBulkLoadFile().setLinkMLSchemaVersion(version.max());
			if (variantData.getMetaData() != null && StringUtils.isNotBlank(variantData.getMetaData().getRelease())) {
				bulkLoadFileHistory.getBulkLoadFile().setAllianceMemberReleaseVersion(variantData.getMetaData().getRelease());
			}
		
			BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(fms.getFmsDataSubType());
		
			List<Long> entityIdsAdded = new ArrayList<>();
			List<Long> locationIdsAdded = new ArrayList<>();
			List<Long> associationIdsAdded = new ArrayList<>();
			
			bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());
		
			bulkLoadFileHistory.setCount(variantData.getData().size());
			updateHistory(bulkLoadFileHistory);
			
			boolean success = runLoad(bulkLoadFileHistory, variantData.getData(), entityIdsAdded, locationIdsAdded, associationIdsAdded, dataProvider);
			if (success) {
				runCleanup(variantService, bulkLoadFileHistory, dataProvider.name(), variantService.getIdsByDataProvider(dataProvider.name()), entityIdsAdded, "variant");
				runCleanup(curatedVariantGenomicLocationAssociationService, bulkLoadFileHistory, dataProvider.name(), curatedVariantGenomicLocationAssociationService.getIdsByDataProvider(dataProvider), locationIdsAdded, "curated variant genomic location association");
				runCleanup(alleleVariantAssociationService, bulkLoadFileHistory, dataProvider.name(), alleleVariantAssociationService.getAssociationsByDataProvider(dataProvider), associationIdsAdded, "allele variant association");
			}
			bulkLoadFileHistory.finishLoad();
			updateHistory(bulkLoadFileHistory);
			updateExceptions(bulkLoadFileHistory);
		} catch (Exception e) {
			failLoad(bulkLoadFileHistory, e);
			e.printStackTrace();
		}
	}

	private boolean runLoad(
			BulkLoadFileHistory history,
			List<VariantFmsDTO> data,
			List<Long> entityIdsAdded,
			List<Long> locationIdsAdded,
			List<Long> associationIdsAdded,
			BackendBulkDataProvider dataProvider) {

		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess("Variant update for " + dataProvider.name(), data.size());
		
		history.setCount("Entities", data.size());
		history.setCount("Locations", data.size());
		history.setCount("Associations", data.size());
		updateHistory(history);
		
		String countType = null;
		for (VariantFmsDTO dto : data) {
			countType = "Entities";
			Variant variant = null;
			try {
				variant = variantFmsDtoValidator.validateVariant(dto, entityIdsAdded, dataProvider);
				history.incrementCompleted(countType);
			} catch (ObjectUpdateException e) {
				history.incrementFailed(countType);
				addException(history, e.getData());
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed(countType);
				addException(history, new ObjectUpdateExceptionData(dto, e.getMessage(), e.getStackTrace()));
			}
			countType = "Locations";
			try {
				variantFmsDtoValidator.validateCuratedVariantGenomicLocationAssociation(dto, locationIdsAdded, variant);
				history.incrementCompleted(countType);
			} catch (ObjectUpdateException e) {
				history.incrementFailed(countType);
				addException(history, e.getData());
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed(countType);
				addException(history, new ObjectUpdateExceptionData(dto, e.getMessage(), e.getStackTrace()));
			}
			countType = "Associations";
			try {
				variantFmsDtoValidator.validateAlleleVariantAssociation(dto, associationIdsAdded, variant);
				history.incrementCompleted(countType);
			} catch (ObjectUpdateException e) {
				history.incrementFailed(countType);
				addException(history, e.getData());
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed(countType);
				addException(history, new ObjectUpdateExceptionData(dto, e.getMessage(), e.getStackTrace()));
			}
		}
		
		updateHistory(history);
		ph.finishProcess();
		
		return true;
	}

	public APIResponse runLoadApi(String dataProviderName, List<VariantFmsDTO> gffData) {
		List<Long> idsAdded = new ArrayList<>();
		BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(dataProviderName);
		BulkLoadFileHistory history = new BulkLoadFileHistory();
		history = bulkLoadFileHistoryDAO.persist(history);
		runLoad(history, gffData, idsAdded, idsAdded, idsAdded, dataProvider);
		history.finishLoad();
		
		return new LoadHistoryResponce(history);
	}
}
