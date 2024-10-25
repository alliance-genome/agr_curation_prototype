package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.SubmittedObjectCrudController;
import org.alliancegenome.curation_api.dao.VariantDAO;
import org.alliancegenome.curation_api.interfaces.crud.VariantCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.VariantExecutor;
import org.alliancegenome.curation_api.jobs.executors.VariantFmsExecutor;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.ingest.dto.VariantDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.VariantFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.services.VariantService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class VariantCrudController extends SubmittedObjectCrudController<VariantService, Variant, VariantDTO, VariantDAO> implements VariantCrudInterface {

	@Inject
	VariantService variantService;

	@Inject
	VariantExecutor variantExecutor;
	
	@Inject
	VariantFmsExecutor variantFmsExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(variantService);
	}

	@Override
	public APIResponse updateVariants(String dataProvider, List<VariantDTO> variantData) {
		return variantExecutor.runLoadApi(variantService, dataProvider, variantData);
	}

	@Override
	public APIResponse updateFmsVariants(String dataProvider, List<VariantFmsDTO> variantData) {
		return variantFmsExecutor.runLoadApi(dataProvider, variantData);
	}

}
