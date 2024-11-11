package org.alliancegenome.curation_api.services;

import java.util.List;

import org.alliancegenome.curation_api.dao.AllelePhenotypeAnnotationDAO;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.AllelePhenotypeAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PhenotypeFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseAnnotationCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.AllelePhenotypeAnnotationFmsDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class AllelePhenotypeAnnotationService extends BaseAnnotationCrudService<AllelePhenotypeAnnotation, AllelePhenotypeAnnotationDAO> {

	@Inject AllelePhenotypeAnnotationDAO allelePhenotypeAnnotationDAO;
	@Inject ConditionRelationDAO conditionRelationDAO;
	@Inject PhenotypeAnnotationService phenotypeAnnotationService;
	@Inject AllelePhenotypeAnnotationFmsDTOValidator allelePhenotypeAnnotationFmsDtoValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(allelePhenotypeAnnotationDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<AllelePhenotypeAnnotation> update(AllelePhenotypeAnnotation uiEntity) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	@Transactional
	public ObjectResponse<AllelePhenotypeAnnotation> create(AllelePhenotypeAnnotation uiEntity) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Transactional
	public AllelePhenotypeAnnotation upsertPrimaryAnnotation(Allele subject, PhenotypeFmsDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		AllelePhenotypeAnnotation annotation = allelePhenotypeAnnotationFmsDtoValidator.validatePrimaryAnnotation(subject, dto, dataProvider);
		return allelePhenotypeAnnotationDAO.persist(annotation);
	}

	@Transactional
	public List<AllelePhenotypeAnnotation> addInferredOrAssertedEntities(Allele primaryAnnotationSubject, PhenotypeFmsDTO secondaryAnnotationDto, BackendBulkDataProvider dataProvider) throws ValidationException {
		List<AllelePhenotypeAnnotation> annotations = allelePhenotypeAnnotationFmsDtoValidator.validateInferredOrAssertedEntities(primaryAnnotationSubject, secondaryAnnotationDto, dataProvider);
		for (AllelePhenotypeAnnotation annotation : annotations) {
			allelePhenotypeAnnotationDAO.persist(annotation);
		}
		return annotations;
	}

	@Override
	@Transactional
	public ObjectResponse<AllelePhenotypeAnnotation> deleteById(Long id) {
		deprecateOrDelete(id, true, "Allele phenotype annotation DELETE API call", false);
		ObjectResponse<AllelePhenotypeAnnotation> ret = new ObjectResponse<>();
		return ret;
	}

	public List<Long> getAnnotationIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		return phenotypeAnnotationService.getAnnotationIdsByDataProvider(allelePhenotypeAnnotationDAO, dataProvider);
	}
}
