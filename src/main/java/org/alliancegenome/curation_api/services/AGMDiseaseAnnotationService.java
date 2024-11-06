package org.alliancegenome.curation_api.services;

import java.util.List;

import org.alliancegenome.curation_api.dao.AGMDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AGMDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseAnnotationDTOCrudService;
import org.alliancegenome.curation_api.services.validation.AGMDiseaseAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.dto.AGMDiseaseAnnotationDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class AGMDiseaseAnnotationService extends BaseAnnotationDTOCrudService<AGMDiseaseAnnotation, AGMDiseaseAnnotationDTO, AGMDiseaseAnnotationDAO> {

	@Inject AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
	@Inject AGMDiseaseAnnotationValidator agmDiseaseValidator;
	@Inject ConditionRelationDAO conditionRelationDAO;
	@Inject DiseaseAnnotationService diseaseAnnotationService;
	@Inject AGMDiseaseAnnotationDTOValidator agmDiseaseAnnotationDtoValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(agmDiseaseAnnotationDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<AGMDiseaseAnnotation> update(AGMDiseaseAnnotation uiEntity) {
		AGMDiseaseAnnotation dbEntity = agmDiseaseValidator.validateAnnotationUpdate(uiEntity);
		return new ObjectResponse<>(agmDiseaseAnnotationDAO.persist(dbEntity));
	}

	@Override
	@Transactional
	public ObjectResponse<AGMDiseaseAnnotation> create(AGMDiseaseAnnotation uiEntity) {
		AGMDiseaseAnnotation dbEntity = agmDiseaseValidator.validateAnnotationCreate(uiEntity);
		return new ObjectResponse<>(agmDiseaseAnnotationDAO.persist(dbEntity));
	}

	@Transactional
	public AGMDiseaseAnnotation upsert(AGMDiseaseAnnotationDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		AGMDiseaseAnnotation annotation = agmDiseaseAnnotationDtoValidator.validateAGMDiseaseAnnotationDTO(dto, dataProvider);

		return agmDiseaseAnnotationDAO.persist(annotation);
	}

	@Override
	@Transactional
	public ObjectResponse<AGMDiseaseAnnotation> deleteById(Long id) {
		deprecateOrDelete(id, true, "AGM disease annotation DELETE API call", false);
		ObjectResponse<AGMDiseaseAnnotation> ret = new ObjectResponse<>();
		return ret;
	}

	public List<Long> getAnnotationIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		return diseaseAnnotationService.getAnnotationIdsByDataProvider(agmDiseaseAnnotationDAO, dataProvider);
	}
}
