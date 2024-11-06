package org.alliancegenome.curation_api.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.HTPExpressionDatasetAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
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

	public HTPExpressionDatasetAnnotation upsert(HTPExpressionDatasetAnnotationFmsDTO htpExpressionDatasetAnnotationData, BackendBulkDataProvider backendBulkDataProvider) throws ValidationException {
		return htpExpressionDatasetAnnotationFmsDtoValidator.validateHTPExpressionDatasetAnnotationFmsDTO(htpExpressionDatasetAnnotationData, backendBulkDataProvider);
	}

	public List<Long> getAnnotationIdsByDataProvider(String dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider);
		List<Long> ids = htpExpressionDatasetAnnotationDAO.findIdsByParams(params);
		ids.removeIf(Objects::isNull);
		return ids;
	}

}