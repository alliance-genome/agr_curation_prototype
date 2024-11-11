package org.alliancegenome.curation_api.services;

import java.util.List;

import org.alliancegenome.curation_api.dao.AlleleDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseAnnotationDTOCrudService;
import org.alliancegenome.curation_api.services.validation.AlleleDiseaseAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.dto.AlleleDiseaseAnnotationDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class AlleleDiseaseAnnotationService extends BaseAnnotationDTOCrudService<AlleleDiseaseAnnotation, AlleleDiseaseAnnotationDTO, AlleleDiseaseAnnotationDAO> {

	@Inject AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
	@Inject AlleleDiseaseAnnotationValidator alleleDiseaseValidator;
	@Inject AlleleDiseaseAnnotationDTOValidator alleleDiseaseAnnotationDtoValidator;
	@Inject DiseaseAnnotationService diseaseAnnotationService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(alleleDiseaseAnnotationDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<AlleleDiseaseAnnotation> update(AlleleDiseaseAnnotation uiEntity) {
		AlleleDiseaseAnnotation dbEntity = alleleDiseaseValidator.validateAnnotationUpdate(uiEntity);
		return new ObjectResponse<>(alleleDiseaseAnnotationDAO.persist(dbEntity));
	}

	@Override
	@Transactional
	public ObjectResponse<AlleleDiseaseAnnotation> create(AlleleDiseaseAnnotation uiEntity) {
		AlleleDiseaseAnnotation dbEntity = alleleDiseaseValidator.validateAnnotationCreate(uiEntity);
		return new ObjectResponse<>(alleleDiseaseAnnotationDAO.persist(dbEntity));
	}

	@Transactional
	public AlleleDiseaseAnnotation upsert(AlleleDiseaseAnnotationDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		AlleleDiseaseAnnotation annotation = alleleDiseaseAnnotationDtoValidator.validateAlleleDiseaseAnnotationDTO(dto, dataProvider);

		return alleleDiseaseAnnotationDAO.persist(annotation);
	}

	@Override
	@Transactional
	public ObjectResponse<AlleleDiseaseAnnotation> deleteById(Long id) {
		deprecateOrDelete(id, true, "Allele disease annotation DELETE API call", false);
		ObjectResponse<AlleleDiseaseAnnotation> ret = new ObjectResponse<>();
		return ret;
	}

	public List<Long> getAnnotationIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		return diseaseAnnotationService.getAnnotationIdsByDataProvider(alleleDiseaseAnnotationDAO, dataProvider);
	}
}
