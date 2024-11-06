package org.alliancegenome.curation_api.services;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.GeneExpressionAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.GeneExpressionFmsDTO;
import org.alliancegenome.curation_api.services.base.BaseAnnotationCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.GeneExpressionAnnotationFmsDTOValidator;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestScoped
public class GeneExpressionAnnotationService extends BaseAnnotationCrudService<GeneExpressionAnnotation, GeneExpressionAnnotationDAO> implements BaseUpsertServiceInterface<GeneExpressionAnnotation, GeneExpressionFmsDTO> {

	@Inject GeneExpressionAnnotationDAO geneExpressionAnnotationDAO;
	@Inject GeneExpressionAnnotationFmsDTOValidator geneExpressionAnnotationFmsDTOValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneExpressionAnnotationDAO);
	}

	public List<Long> getAnnotationIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
		if (StringUtils.equals(dataProvider.sourceOrganization, "RGD") || StringUtils.equals(dataProvider.sourceOrganization, "XB")) {
			params.put(EntityFieldConstants.EA_SUBJECT_TAXON, dataProvider.canonicalTaxonCurie);
		}
		List<Long> annotationIds = geneExpressionAnnotationDAO.findIdsByParams(params);
		return annotationIds;
	}

	@Transactional
	@Override
	public GeneExpressionAnnotation upsert(GeneExpressionFmsDTO geneExpressionFmsDTO, BackendBulkDataProvider dataProvider) throws ValidationException {
		GeneExpressionAnnotation geneExpressionAnnotation = geneExpressionAnnotationFmsDTOValidator.validateAnnotation(geneExpressionFmsDTO, dataProvider);
		return geneExpressionAnnotationDAO.persist(geneExpressionAnnotation);
	}
}
