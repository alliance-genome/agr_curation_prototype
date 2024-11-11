package org.alliancegenome.curation_api.services;

import java.util.List;

import org.alliancegenome.curation_api.dao.AGMPhenotypeAnnotationDAO;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.AGMPhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PhenotypeFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseAnnotationCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.AGMPhenotypeAnnotationFmsDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class AGMPhenotypeAnnotationService extends BaseAnnotationCrudService<AGMPhenotypeAnnotation, AGMPhenotypeAnnotationDAO> {

	@Inject AGMPhenotypeAnnotationDAO agmPhenotypeAnnotationDAO;
	@Inject ConditionRelationDAO conditionRelationDAO;
	@Inject PhenotypeAnnotationService phenotypeAnnotationService;
	@Inject AGMPhenotypeAnnotationFmsDTOValidator agmPhenotypeAnnotationFmsDtoValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(agmPhenotypeAnnotationDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<AGMPhenotypeAnnotation> update(AGMPhenotypeAnnotation uiEntity) {
		return null;
	}

	@Override
	@Transactional
	public ObjectResponse<AGMPhenotypeAnnotation> create(AGMPhenotypeAnnotation uiEntity) {
		return null;
	}

	@Transactional
	public AGMPhenotypeAnnotation upsertPrimaryAnnotation(AffectedGenomicModel subject, PhenotypeFmsDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		AGMPhenotypeAnnotation annotation = agmPhenotypeAnnotationFmsDtoValidator.validatePrimaryAnnotation(subject, dto, dataProvider);
		return agmPhenotypeAnnotationDAO.persist(annotation);
	}

	@Transactional
	public List<AGMPhenotypeAnnotation> addInferredOrAssertedEntities(AffectedGenomicModel primaryAnnotationSubject, PhenotypeFmsDTO secondaryAnnotationDto, BackendBulkDataProvider dataProvider) throws ValidationException {
		List<AGMPhenotypeAnnotation> annotations = agmPhenotypeAnnotationFmsDtoValidator.validateInferredOrAssertedEntities(primaryAnnotationSubject, secondaryAnnotationDto, dataProvider);
		for (AGMPhenotypeAnnotation annotation : annotations) {
			agmPhenotypeAnnotationDAO.persist(annotation);
		}
		return annotations;
	}

	@Override
	@Transactional
	public ObjectResponse<AGMPhenotypeAnnotation> deleteById(Long id) {
		deprecateOrDelete(id, true, "AGM phenotype annotation DELETE API call", false);
		ObjectResponse<AGMPhenotypeAnnotation> ret = new ObjectResponse<>();
		return ret;
	}

	public List<Long> getAnnotationIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		return phenotypeAnnotationService.getAnnotationIdsByDataProvider(agmPhenotypeAnnotationDAO, dataProvider);
	}
}
