package org.alliancegenome.curation_api.services;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.ConstructDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.ingest.dto.ConstructDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.associations.constructAssociations.ConstructGenomicEntityAssociationService;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.validation.ConstructValidator;
import org.alliancegenome.curation_api.services.validation.dto.ConstructDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequestScoped
public class ConstructService extends SubmittedObjectCrudService<Construct, ConstructDTO, ConstructDAO> {

	@Inject
	ConstructDAO constructDAO;
	@Inject
	ConstructValidator constructValidator;
	@Inject
	ConstructDTOValidator constructDtoValidator;
	@Inject
	ConstructService constructService;
	@Inject
	PersonService personService;
	@Inject
	ConstructComponentSlotAnnotationDAO constructComponentDAO;
	@Inject
	ConstructGenomicEntityAssociationService constructGenomicEntityAssociationService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(constructDAO);
	}

	@Override
	public ObjectResponse<Construct> getByIdentifier(String identifier) {
		Construct construct = findByIdentifierString(identifier);
		if (construct == null) {
			SearchResponse<Construct> response = findByField("uniqueId", identifier);
			if (response != null) {
				construct = response.getSingleResult();
			}
		}
		return new ObjectResponse<>(construct);
	}

	@Override
	@Transactional
	public ObjectResponse<Construct> update(Construct uiEntity) {
		Construct dbEntity = constructValidator.validateConstructUpdate(uiEntity);
		return new ObjectResponse<>(constructDAO.persist(dbEntity));
	}

	@Override
	@Transactional
	public ObjectResponse<Construct> create(Construct uiEntity) {
		Construct dbEntity = constructValidator.validateConstructCreate(uiEntity);
		return new ObjectResponse<>(constructDAO.persist(dbEntity));
	}

	@Transactional
	public Construct upsert(ConstructDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		Construct construct = constructDtoValidator.validateConstructDTO(dto, dataProvider);

		return constructDAO.persist(construct);
	}

	@Override
	@Transactional
	public ObjectResponse<Construct> deleteById(Long id) {
		deprecateOrDelete(id, true, "Construct DELETE API call", false);
		ObjectResponse<Construct> ret = new ObjectResponse<>();
		return ret;
	}

	@Override
	@Transactional
	public Construct deprecateOrDelete(Long id, Boolean throwApiError, String requestSource, Boolean forceDeprecate) {
		Construct construct = constructDAO.find(id);

		if (construct != null) {
			if (forceDeprecate || CollectionUtils.isNotEmpty(construct.getConstructGenomicEntityAssociations())) {
				if (!construct.getObsolete()) {
					construct.setUpdatedBy(personService.fetchByUniqueIdOrCreate(requestSource));
					construct.setDateUpdated(OffsetDateTime.now());
					construct.setObsolete(true);
					return constructDAO.persist(construct);
				} else {
					return construct;
				}
			} else {
				constructDAO.remove(id);
			}
		} else {
			String errorMessage = "Could not find Construct with id: " + id;
			if (throwApiError) {
				ObjectResponse<AffectedGenomicModel> response = new ObjectResponse<>();
				response.addErrorMessage("id", errorMessage);
				throw new ApiErrorException(response);
			}
			Log.error(errorMessage);
		}
		return null;
	}

	public List<Long> getConstructIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
		List<Long> constructIds = constructDAO.findIdsByParams(params);
		constructIds.removeIf(Objects::isNull);

		return constructIds;
	}

	public Map<String, Long> getConstructIdMap() {
		if (constructIdMap.size() > 0) {
			return constructIdMap;
		}
		constructIdMap = constructDAO.getConstructIdMap();
		return constructIdMap;
	}

	private Map<String, Long> constructIdMap = new HashMap<>();

	public Long getIdByModID(String modID) {
		return getConstructIdMap().get(modID);
	}

	public Construct getShallowEntity(Long id) {
		return constructDAO.getShallowEntity(Construct.class, id);
	}
}
