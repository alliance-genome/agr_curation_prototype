package org.alliancegenome.curation_api.controllers.crud;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.HTPExpressionDatasetAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.HTPExpressionDatasetAnnotationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.HTPExpressionDatasetAnnotationExecutor;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.HTPExpressionDatasetAnnotationIngestFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.services.HTPExpressionDatasetAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class HTPExpressionDatasetAnnotationCrudController extends BaseEntityCrudController<HTPExpressionDatasetAnnotationService, HTPExpressionDatasetAnnotation, HTPExpressionDatasetAnnotationDAO> implements HTPExpressionDatasetAnnotationCrudInterface {

	@Inject
	HTPExpressionDatasetAnnotationService htpExpressionDatasetAnnotationService;
	@Inject
	HTPExpressionDatasetAnnotationExecutor htpExpressionDatasetAnnotationExecutor;

	@Override
	@PostConstruct
	public void init() {
		setService(htpExpressionDatasetAnnotationService);
	}

	@Override
	public APIResponse updateHTPExpressionDatasetAnnotation(String dataProvider, HTPExpressionDatasetAnnotationIngestFmsDTO htpDatasetData) {
		return htpExpressionDatasetAnnotationExecutor.runLoadApi(dataProvider, htpDatasetData.getData());
	}
	
}