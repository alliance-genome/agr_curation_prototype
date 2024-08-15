package org.alliancegenome.curation_api.services;

import org.alliancegenome.curation_api.dao.HTPExpressionDatasetAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.HTPExpressionDatasetAnnotationFmsDTO;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.HTPExpressionDatasetAnnotationFmsDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class HTPExpressionDatasetAnnotationService extends BaseEntityCrudService<HTPExpressionDatasetAnnotation, HTPExpressionDatasetAnnotationDAO> implements BaseUpsertServiceInterface<HTPExpressionDatasetAnnotation, HTPExpressionDatasetAnnotationFmsDTO> {
	
	@Inject HTPExpressionDatasetAnnotationDAO htpExpressionDatasetAnnotationDAO;
	@Inject HTPExpressionDatasetAnnotationFmsDTOValidator htpExpressionDatasetAnnotationFmsDtoValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(htpExpressionDatasetAnnotationDAO);
	}

	public HTPExpressionDatasetAnnotation upsert(HTPExpressionDatasetAnnotationFmsDTO htpExpressionDatasetAnnotationData, BackendBulkDataProvider backendBulkDataProvider)  throws ObjectUpdateException {
		return htpExpressionDatasetAnnotationFmsDtoValidator.validateHTPExpressionDatasetAnnotationFmsDTO(htpExpressionDatasetAnnotationData);
	}
}
