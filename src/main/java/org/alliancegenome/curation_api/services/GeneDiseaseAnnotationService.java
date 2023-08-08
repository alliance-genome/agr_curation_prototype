package org.alliancegenome.curation_api.services;

import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.GeneDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseDTOCrudService;
import org.alliancegenome.curation_api.services.validation.GeneDiseaseAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.dto.GeneDiseaseAnnotationDTOValidator;
import org.apache.commons.lang.StringUtils;

@RequestScoped
public class GeneDiseaseAnnotationService extends BaseDTOCrudService<GeneDiseaseAnnotation, GeneDiseaseAnnotationDTO, GeneDiseaseAnnotationDAO> {

	@Inject
	GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
	@Inject
	GeneDiseaseAnnotationValidator geneDiseaseValidator;
	@Inject
	GeneDiseaseAnnotationDTOValidator geneDiseaseAnnotationDtoValidator;
	@Inject
	DiseaseAnnotationService diseaseAnnotationService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneDiseaseAnnotationDAO);
	}

	@Override
	public ObjectResponse<GeneDiseaseAnnotation> get(String identifier) {
		SearchResponse<GeneDiseaseAnnotation> ret = findByField("modEntityId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<GeneDiseaseAnnotation>(ret.getResults().get(0));
		
		ret = findByField("modInternalId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<GeneDiseaseAnnotation>(ret.getResults().get(0));
		
		ret = findByField("uniqueId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<GeneDiseaseAnnotation>(ret.getResults().get(0));
				
		return new ObjectResponse<GeneDiseaseAnnotation>();
	}

	@Override
	@Transactional
	public ObjectResponse<GeneDiseaseAnnotation> update(GeneDiseaseAnnotation uiEntity) {
		GeneDiseaseAnnotation dbEntity = geneDiseaseValidator.validateAnnotationUpdate(uiEntity);
		return new ObjectResponse<>(geneDiseaseAnnotationDAO.persist(dbEntity));
	}

	@Override
	@Transactional
	public ObjectResponse<GeneDiseaseAnnotation> create(GeneDiseaseAnnotation uiEntity) {
		GeneDiseaseAnnotation dbEntity = geneDiseaseValidator.validateAnnotationCreate(uiEntity);
		return new ObjectResponse<>(geneDiseaseAnnotationDAO.persist(dbEntity));
	}

	@Transactional
	public GeneDiseaseAnnotation upsert(GeneDiseaseAnnotationDTO dto) throws ObjectUpdateException {
		GeneDiseaseAnnotation annotation = geneDiseaseAnnotationDtoValidator.validateGeneDiseaseAnnotationDTO(dto);

		return geneDiseaseAnnotationDAO.persist(annotation);
	}

	@Override
	@Transactional
	public ObjectResponse<GeneDiseaseAnnotation> delete(Long id) {
		diseaseAnnotationService.deprecateOrDeleteAnnotationAndNotes(id, true, "Gene disease annotation DELETE API call", false);
		ObjectResponse<GeneDiseaseAnnotation> ret = new ObjectResponse<>();
		return ret;
	}

	@Override
	public void removeOrDeprecateNonUpdated(String curie, String dataProviderName, String md5sum) { }

	public List<Long> getAnnotationIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		List<Long> annotationIds;

		String sourceOrg = dataProvider.sourceOrganization;

		if( StringUtils.equals(sourceOrg, "RGD") ){
			annotationIds = geneDiseaseAnnotationDAO.findAllAnnotationIdsByDataProvider(dataProvider.sourceOrganization, dataProvider.canonicalTaxonCurie);
		} else {
			annotationIds = geneDiseaseAnnotationDAO.findAllAnnotationIdsByDataProvider(dataProvider.sourceOrganization);
		}

		annotationIds.removeIf(Objects::isNull);

		return annotationIds;
	}
}
