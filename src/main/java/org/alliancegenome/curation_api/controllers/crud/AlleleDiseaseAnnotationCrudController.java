package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.BaseAnnotationDTOCrudController;
import org.alliancegenome.curation_api.dao.AlleleDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.AlleleDiseaseAnnotationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.AlleleDiseaseAnnotationExecutor;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.AlleleDiseaseAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleDiseaseAnnotationCrudController extends BaseAnnotationDTOCrudController<AlleleDiseaseAnnotationService, AlleleDiseaseAnnotation, AlleleDiseaseAnnotationDTO, AlleleDiseaseAnnotationDAO>
	implements AlleleDiseaseAnnotationCrudInterface {

	@Inject
	AlleleDiseaseAnnotationService alleleDiseaseAnnotationService;
	@Inject
	AlleleDiseaseAnnotationExecutor alleleDiseaseAnnotationExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(alleleDiseaseAnnotationService);
	}

	public APIResponse updateAlleleDiseaseAnnotations(String dataProvider, List<AlleleDiseaseAnnotationDTO> annotations) {
		return alleleDiseaseAnnotationExecutor.runLoadApi(alleleDiseaseAnnotationService, dataProvider, annotations);
	}
	
	public ObjectResponse<AlleleDiseaseAnnotation> getByIdentifier(String identifierString) {
		return alleleDiseaseAnnotationService.getByIdentifier(identifierString);
	}

}
