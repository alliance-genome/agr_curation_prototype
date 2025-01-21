package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.PredictedVariantConsequenceDAO;
import org.alliancegenome.curation_api.interfaces.crud.PredictedVariantConsequenceCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.VepGeneExecutor;
import org.alliancegenome.curation_api.jobs.executors.VepTranscriptExecutor;
import org.alliancegenome.curation_api.model.entities.PredictedVariantConsequence;
import org.alliancegenome.curation_api.model.ingest.dto.fms.VepTxtDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.services.PredictedVariantConsequenceService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class PredictedVariantConsequenceCrudController extends BaseEntityCrudController<PredictedVariantConsequenceService, PredictedVariantConsequence, PredictedVariantConsequenceDAO>
	implements PredictedVariantConsequenceCrudInterface {

	@Inject PredictedVariantConsequenceService predictedVariantConsequenceService;
	@Inject VepTranscriptExecutor vepTranscriptExecutor;
	@Inject VepGeneExecutor vepGeneExecutor;
	
	@Override
	@PostConstruct
	protected void init() {
		setService(predictedVariantConsequenceService);
	}

	public APIResponse updateTranscriptLevelConsequences(String dataProvider, List<VepTxtDTO> consequenceData) {
		return vepTranscriptExecutor.runLoadApi(predictedVariantConsequenceService, dataProvider, consequenceData);
	}

	public APIResponse updateGeneLevelConsequences(String dataProvider, List<VepTxtDTO> consequenceData) {
		return vepGeneExecutor.runLoadApi(dataProvider, consequenceData);
	}
}
