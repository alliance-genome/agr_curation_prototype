package org.alliancegenome.curation_api.controllers.crud;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.GeneExpressionExperimentDAO;
import org.alliancegenome.curation_api.interfaces.crud.GeneExpressionExperimentCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.GeneExpressionExecutor;
import org.alliancegenome.curation_api.model.entities.GeneExpressionExperiment;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.GeneExpressionExperimentService;


@RequestScoped
public class GeneExpressionExperimentCrudController extends BaseEntityCrudController<GeneExpressionExperimentService, GeneExpressionExperiment, GeneExpressionExperimentDAO> implements GeneExpressionExperimentCrudInterface {

	@Inject
	GeneExpressionExperimentService geneExpressionExperimentService;
	@Inject
	GeneExpressionExecutor geneExpressionExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(geneExpressionExperimentService);
	}

	public ObjectResponse<GeneExpressionExperiment> getByIdentifier(String identifierString) {
		return geneExpressionExperimentService.getByIdentifier(identifierString);
	}

}

