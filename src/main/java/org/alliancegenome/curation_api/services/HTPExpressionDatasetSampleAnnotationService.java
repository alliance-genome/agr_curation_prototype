package org.alliancegenome.curation_api.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.HTPExpressionDatasetSampleAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetSampleAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.HTPExpressionDatasetSampleAnnotationFmsDTO;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.HTPExpressionDatasetSampleAnnotationFmsDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

public class HTPExpressionDatasetSampleAnnotationService extends BaseEntityCrudService<HTPExpressionDatasetSampleAnnotation, HTPExpressionDatasetSampleAnnotationDAO> implements BaseUpsertServiceInterface<HTPExpressionDatasetSampleAnnotation, HTPExpressionDatasetSampleAnnotationFmsDTO> {
    @Inject HTPExpressionDatasetSampleAnnotationDAO htpExpressionDatasetSampleAnnotationDAO;
	@Inject HTPExpressionDatasetSampleAnnotationFmsDTOValidator htpExpressionDatasetSampleAnnotationFmsDtoValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(htpExpressionDatasetSampleAnnotationDAO);
	}

	public HTPExpressionDatasetSampleAnnotation upsert(HTPExpressionDatasetSampleAnnotationFmsDTO htpExpressionDatasetSampleAnnotationData, BackendBulkDataProvider backendBulkDataProvider) throws ValidationException {
		return htpExpressionDatasetSampleAnnotationFmsDtoValidator.validateHTPExpressionDatasetSampleAnnotationFmsDTO(htpExpressionDatasetSampleAnnotationData, backendBulkDataProvider);
	}

	public List<Long> getAnnotationIdsByDataProvider(String dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider);
		List<Long> ids = htpExpressionDatasetSampleAnnotationDAO.findIdsByParams(params);
		ids.removeIf(Objects::isNull);
		return ids;
	}
}
