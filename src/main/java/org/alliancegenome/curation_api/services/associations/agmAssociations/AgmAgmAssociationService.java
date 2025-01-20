package org.alliancegenome.curation_api.services.associations.agmAssociations;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.jbosslog.JBossLog;
import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.dao.SequenceTargetingReagentDAO;
import org.alliancegenome.curation_api.dao.associations.agmAssociations.AgmAgmAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.associations.agmAssociations.AgmAgmAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.agmAssociations.AgmAgmAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.base.BaseAssociationDTOCrudService;
import org.alliancegenome.curation_api.services.validation.dto.associations.agmAssociations.AgmAgmAssociationDTOValidator;

import java.time.OffsetDateTime;
import java.util.*;

@JBossLog
@RequestScoped
public class AgmAgmAssociationService extends BaseAssociationDTOCrudService<AgmAgmAssociation, AgmAgmAssociationDTO, AgmAgmAssociationDAO> implements BaseUpsertServiceInterface<AgmAgmAssociation, AgmAgmAssociationDTO> {

	@Inject
	AgmAgmAssociationDAO agmAgmAssociationDAO;
	@Inject
	AgmAgmAssociationDTOValidator agmAgmAssociationDtoValidator;
	@Inject
	AffectedGenomicModelDAO agmDAO;
	@Inject
	NoteDAO noteDAO;
	@Inject
	SequenceTargetingReagentDAO strDAO;
	@Inject
	PersonService personService;
	@Inject
	PersonDAO personDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(agmAgmAssociationDAO);
	}

	@Override
	@Transactional
	public AgmAgmAssociation upsert(AgmAgmAssociationDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		AgmAgmAssociation association = agmAgmAssociationDtoValidator.validateAgmAgmAssociationDTO(dto, dataProvider);
		if (association != null) {
			addAssociationToAgm(association);
			addAssociationToStr(association);
		}

		return association;
	}

	public List<Long> getAssociationsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.AGM_ASSOCIATION_SUBJECT_DATA_PROVIDER, dataProvider.sourceOrganization);
		List<Long> associationIds = agmAgmAssociationDAO.findIdsByParams(params);
		associationIds.removeIf(Objects::isNull);

		return associationIds;
	}

	//todo: is this needed?
	@Override
	@Transactional
	public AgmAgmAssociation deprecateOrDelete(Long id, Boolean throwApiError, String loadDescription, Boolean deprecate) {
		AgmAgmAssociation association = agmAgmAssociationDAO.find(id);

		if (association == null) {
			String errorMessage = "Could not find AgmAgmAssociation with id: " + id;
			if (throwApiError) {
				ObjectResponse<AgmAgmAssociation> response = new ObjectResponse<>();
				response.addErrorMessage("id", errorMessage);
				throw new ApiErrorException(response);
			}
			log.error(errorMessage);
			return null;
		}
		if (deprecate) {
			if (!association.getObsolete()) {
				association.setObsolete(true);
				if (authenticatedPerson.getId() != null) {
					association.setUpdatedBy(personDAO.find(authenticatedPerson.getId()));
				} else {
					association.setUpdatedBy(personService.fetchByUniqueIdOrCreate(loadDescription));
				}
				association.setDateUpdated(OffsetDateTime.now());
				return agmAgmAssociationDAO.persist(association);
			}
			return association;
		}

		return null;
	}

	public ObjectResponse<AgmAgmAssociation> getAssociation(Long agmId, String relationName, Long strId) {
		AgmAgmAssociation association = null;

		Map<String, Object> params = new HashMap<>();
		params.put("agmAssociationSubject.id", agmId);
		params.put("relation.name", relationName);
		params.put("agmAssociationObject.id", strId);

		SearchResponse<AgmAgmAssociation> resp = agmAgmAssociationDAO.findByParams(params);
		if (resp != null && resp.getSingleResult() != null) {
			association = resp.getSingleResult();
		}

		ObjectResponse<AgmAgmAssociation> response = new ObjectResponse<>();
		response.setEntity(association);

		return response;
	}

	private void addAssociationToAgm(AgmAgmAssociation association) {
		AffectedGenomicModel agm = association.getAgmAssociationSubject();
		List<AgmAgmAssociation> currentAssociations = agm.getAgmAgmAssociations();
		if (currentAssociations == null) {
			currentAssociations = new ArrayList<>();
			agm.setAgmAgmAssociations(currentAssociations);
		}

		List<Long> currentAssociationIds = new ArrayList<>();
		for (AgmAgmAssociation aga : currentAssociations) {
			currentAssociationIds.add(aga.getId());
		}

		if (!currentAssociationIds.contains(association.getId())) {
			currentAssociations.add(association);
		}
	}

	private void addAssociationToStr(AgmAgmAssociation association) {
		AffectedGenomicModel str = association.getAgmAssociationObject();
		List<AgmAgmAssociation> currentAssociations = str.getAgmAgmAssociations();
		if (currentAssociations == null) {
			currentAssociations = new ArrayList<>();
			str.setAgmAgmAssociations(currentAssociations);
		}

		List<Long> currentAssociationIds = new ArrayList<>();
		for (AgmAgmAssociation aga : currentAssociations) {
			currentAssociationIds.add(aga.getId());
		}

		if (!currentAssociationIds.contains(association.getId())) {
			currentAssociations.add(association);
		}

	}
}
