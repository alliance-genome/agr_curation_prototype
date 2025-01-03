package org.alliancegenome.curation_api.services.associations.agmAssociations;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.SequenceTargetingReagentDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.dao.associations.agmAssociations.AgmSequenceTargetingReagentAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.entities.associations.agmAssociations.AgmSequenceTargetingReagentAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.agmAssociations.AgmSequenceTargetingReagentAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.base.BaseAssociationDTOCrudService;
import org.alliancegenome.curation_api.services.validation.dto.associations.agmAssociations.AgmSequenceTargetingReagentAssociationDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AgmStrAssociationService extends BaseAssociationDTOCrudService<AgmSequenceTargetingReagentAssociation, AgmSequenceTargetingReagentAssociationDTO, AgmSequenceTargetingReagentAssociationDAO> implements BaseUpsertServiceInterface<AgmSequenceTargetingReagentAssociation, AgmSequenceTargetingReagentAssociationDTO> {

	@Inject AgmSequenceTargetingReagentAssociationDAO agmStrAssociationDAO;
	@Inject AgmSequenceTargetingReagentAssociationDTOValidator agmStrAssociationDtoValidator;
	@Inject AffectedGenomicModelDAO agmDAO;
	@Inject NoteDAO noteDAO;
	@Inject SequenceTargetingReagentDAO strDAO;
	@Inject PersonService personService;
	@Inject PersonDAO personDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(agmStrAssociationDAO);
	}

	@Override
	@Transactional
	public AgmSequenceTargetingReagentAssociation upsert(AgmSequenceTargetingReagentAssociationDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		AgmSequenceTargetingReagentAssociation association = agmStrAssociationDtoValidator.validateAgmSequenceTargetingReagentAssociationDTO(dto, dataProvider);
		if (association != null) {
			addAssociationToAgm(association);
			addAssociationToStr(association);
		}

		return association;
	}

	public List<Long> getAssociationsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.AGM_ASSOCIATION_SUBJECT_DATA_PROVIDER, dataProvider.sourceOrganization);
		List<Long> associationIds = agmStrAssociationDAO.findIdsByParams(params);
		associationIds.removeIf(Objects::isNull);

		return associationIds;
	}

	//todo: is this needed?
	@Override
	@Transactional
	public AgmSequenceTargetingReagentAssociation deprecateOrDelete(Long id, Boolean throwApiError, String loadDescription, Boolean deprecate) {
		AgmSequenceTargetingReagentAssociation association = agmStrAssociationDAO.find(id);

		if (association == null) {
			String errorMessage = "Could not find AgmSequenceTargetingReagentAssociation with id: " + id;
			if (throwApiError) {
				ObjectResponse<AgmSequenceTargetingReagentAssociation> response = new ObjectResponse<>();
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
				return agmStrAssociationDAO.persist(association);
			}
			return association;
		}

		return null;
	}

	public ObjectResponse<AgmSequenceTargetingReagentAssociation> getAssociation(Long agmId, String relationName, Long strId) {
		AgmSequenceTargetingReagentAssociation association = null;

		Map<String, Object> params = new HashMap<>();
		params.put("agmAssociationSubject.id", agmId);
		params.put("relation.name", relationName);
		params.put("agmSequenceTargetingReagentAssociationObject.id", strId);

		SearchResponse<AgmSequenceTargetingReagentAssociation> resp = agmStrAssociationDAO.findByParams(params);
		if (resp != null && resp.getSingleResult() != null) {
			association = resp.getSingleResult();
		}

		ObjectResponse<AgmSequenceTargetingReagentAssociation> response = new ObjectResponse<>();
		response.setEntity(association);

		return response;
	}

	private void addAssociationToAgm(AgmSequenceTargetingReagentAssociation association) {
		AffectedGenomicModel agm = association.getAgmAssociationSubject();
		List<AgmSequenceTargetingReagentAssociation> currentAssociations = agm.getAgmSequenceTargetingReagentAssociations();
		if (currentAssociations == null) {
			currentAssociations = new ArrayList<>();
			agm.setAgmSequenceTargetingReagentAssociations(currentAssociations);
		}

		List<Long> currentAssociationIds = new ArrayList<>();
		for (AgmSequenceTargetingReagentAssociation aga : currentAssociations) {
			currentAssociationIds.add(aga.getId());
		}

		if (!currentAssociationIds.contains(association.getId())) {
			currentAssociations.add(association);
		}
	}

	private void addAssociationToStr(AgmSequenceTargetingReagentAssociation association) {
		SequenceTargetingReagent str = association.getAgmSequenceTargetingReagentAssociationObject();
		List<AgmSequenceTargetingReagentAssociation> currentAssociations = str.getAgmSequenceTargetingReagentAssociations();
		if (currentAssociations == null) {
			currentAssociations = new ArrayList<>();
			str.setAgmSequenceTargetingReagentAssociations(currentAssociations);
		}

		List<Long> currentAssociationIds = new ArrayList<>();
		for (AgmSequenceTargetingReagentAssociation aga : currentAssociations) {
			currentAssociationIds.add(aga.getId());
		}

		if (!currentAssociationIds.contains(association.getId())) {
			currentAssociations.add(association);
		}
		
	}
}
