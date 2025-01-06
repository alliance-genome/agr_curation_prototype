package org.alliancegenome.curation_api.controllers.crud;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.interfaces.crud.BiogridOrcCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.BiogridOrcExecutor;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.ingest.dto.fms.BiogridOrcIngestFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.services.CrossReferenceService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class BiogridOrcCrudController extends BaseEntityCrudController<CrossReferenceService, CrossReference, CrossReferenceDAO> implements BiogridOrcCrudInterface {

	@Inject
	CrossReferenceService crossReferenceService;

	@Inject
	BiogridOrcExecutor biogridOrcExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(crossReferenceService);
	}

	@Override
	public APIResponse updateBiogridOrc(String dataProvider, BiogridOrcIngestFmsDTO biogridOrcData) {
		return biogridOrcExecutor.runLoadApi(dataProvider, biogridOrcData.getData());
	}
}
