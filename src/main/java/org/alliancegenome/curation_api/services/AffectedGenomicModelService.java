package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.ingest.dto.AffectedGenomicModelDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.associations.constructAssociations.ConstructGenomicEntityAssociationService;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.validation.AffectedGenomicModelValidator;
import org.alliancegenome.curation_api.services.validation.dto.AffectedGenomicModelDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class AffectedGenomicModelService extends SubmittedObjectCrudService<AffectedGenomicModel, AffectedGenomicModelDTO, AffectedGenomicModelDAO> {

	@Inject AffectedGenomicModelDAO agmDAO;
	@Inject AlleleDAO alleleDAO;
	@Inject AffectedGenomicModelValidator agmValidator;
	@Inject AffectedGenomicModelDTOValidator agmDtoValidator;
	@Inject DiseaseAnnotationService diseaseAnnotationService;
	@Inject PhenotypeAnnotationService phenotypeAnnotationService;
	@Inject PersonService personService;
	@Inject ConstructGenomicEntityAssociationService constructGenomicEntityAssociationService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(agmDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<AffectedGenomicModel> update(AffectedGenomicModel uiEntity) {
		AffectedGenomicModel dbEntity = agmDAO.persist(agmValidator.validateAffectedGenomicModelUpdate(uiEntity));
		return new ObjectResponse<AffectedGenomicModel>(dbEntity);
	}

	@Override
	@Transactional
	public ObjectResponse<AffectedGenomicModel> create(AffectedGenomicModel uiEntity) {
		AffectedGenomicModel dbEntity = agmDAO.persist(agmValidator.validateAffectedGenomicModelCreate(uiEntity));
		return new ObjectResponse<AffectedGenomicModel>(dbEntity);
	}

	@Transactional
	public AffectedGenomicModel upsert(AffectedGenomicModelDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		AffectedGenomicModel agm = agmDtoValidator.validateAffectedGenomicModelDTO(dto, dataProvider);

		if (agm == null) {
			return null;
		}

		return agmDAO.persist(agm);
	}

	@Override
	@Transactional
	public ObjectResponse<AffectedGenomicModel> deleteById(Long id) {
		deprecateOrDelete(id, true, "AGM DELETE API call", false);
		ObjectResponse<AffectedGenomicModel> ret = new ObjectResponse<>();
		return ret;
	}

	@Override
	@Transactional
	public AffectedGenomicModel deprecateOrDelete(Long id, Boolean throwApiError, String requestSource, Boolean forceDeprecate) {
		AffectedGenomicModel agm = agmDAO.find(id);
		if (agm != null) {
			if (forceDeprecate || agmDAO.hasReferencingDiseaseAnnotations(id)
					|| agmDAO.hasReferencingPhenotypeAnnotations(id)
					|| CollectionUtils.isNotEmpty(agm.getConstructGenomicEntityAssociations())) {
				if (!agm.getObsolete()) {
					agm.setUpdatedBy(personService.fetchByUniqueIdOrCreate(requestSource));
					agm.setDateUpdated(OffsetDateTime.now());
					agm.setObsolete(true);
					return agmDAO.persist(agm);
				} else {
					return agm;
				}
			} else {
				agmDAO.remove(id);
			}
		} else {
			String errorMessage = "Could not find AGM with id: " + id;
			if (throwApiError) {
				ObjectResponse<AffectedGenomicModel> response = new ObjectResponse<>();
				response.addErrorMessage("id", errorMessage);
				throw new ApiErrorException(response);
			}
			Log.error(errorMessage);
		}
		return null;
	}

	public List<Long> getIdsByDataProvider(String dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider);
		List<Long> ids = agmDAO.findIdsByParams(params);
		ids.removeIf(Objects::isNull);
		return ids;
	}

}
