package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.PhenotypeAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.PhenotypeAnnotationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.PhenotypeAnnotationExecutor;
import org.alliancegenome.curation_api.model.entities.PhenotypeAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PhenotypeFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.PhenotypeAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class PhenotypeAnnotationCrudController extends BaseEntityCrudController<PhenotypeAnnotationService, PhenotypeAnnotation, PhenotypeAnnotationDAO> implements PhenotypeAnnotationCrudInterface {

	@Inject
	PhenotypeAnnotationService phenotypeAnnotationService;
	@Inject
	PhenotypeAnnotationExecutor phenotypeAnnotationExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(phenotypeAnnotationService);
	}
	
	@Override
	public ObjectResponse<PhenotypeAnnotation> getByIdentifier(String identifierString) {
		return phenotypeAnnotationService.getByIdentifier(identifierString);
	}
	
	@Override
	public APIResponse updatePhenotypeAnnotations(String dataProvider, List<PhenotypeFmsDTO> annotations) {
		return phenotypeAnnotationExecutor.runLoadApi(dataProvider, annotations);
	}
}
