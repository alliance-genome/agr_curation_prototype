package org.alliancegenome.curation_api.services.associations.agmAssociations;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.associations.agmAssociations.AgmAlleleAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.associations.agmAssociations.AgmAlleleAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.agmAssociations.AgmAlleleAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.base.BaseAssociationDTOCrudService;
import org.alliancegenome.curation_api.services.validation.dto.associations.agmAssociations.AgmAlleleAssociationDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AgmAlleleAssociationService  extends BaseAssociationDTOCrudService<AgmAlleleAssociation, AgmAlleleAssociationDTO, AgmAlleleAssociationDAO> implements BaseUpsertServiceInterface<AgmAlleleAssociation, AgmAlleleAssociationDTO> {
	
	@Inject AgmAlleleAssociationDAO agmAlleleAssociationDAO;
	@Inject AgmAlleleAssociationDTOValidator agmAlleleAssociationDtoValidator;
	@Inject AffectedGenomicModelDAO agmDAO;
	@Inject NoteDAO noteDAO;
	@Inject AlleleDAO strDAO;
	@Inject PersonService personService;
	@Inject PersonDAO personDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(agmAlleleAssociationDAO);
	}

	@Override
	@Transactional
	public AgmAlleleAssociation upsert(AgmAlleleAssociationDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		AgmAlleleAssociation association = agmAlleleAssociationDtoValidator.validateAgmAlleleAssociationDTO(dto, dataProvider);
		if (association != null) {
			addAssociationToAgm(association);
		}

		return association;
	}

	public List<Long> getAssociationsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.AGM_ASSOCIATION_SUBJECT_DATA_PROVIDER, dataProvider.sourceOrganization);
		List<Long> associationIds = agmAlleleAssociationDAO.findIdsByParams(params);
		associationIds.removeIf(Objects::isNull);

		return associationIds;
	}

	//todo: is this needed?
	@Override
	@Transactional
	public AgmAlleleAssociation deprecateOrDelete(Long id, Boolean throwApiError, String loadDescription, Boolean deprecate) {
		AgmAlleleAssociation association = agmAlleleAssociationDAO.find(id);

		if (association == null) {
			String errorMessage = "Could not find AgmAlleleAssociation with id: " + id;
			if (throwApiError) {
				ObjectResponse<AgmAlleleAssociation> response = new ObjectResponse<>();
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
				return agmAlleleAssociationDAO.persist(association);
			}
			return association;
		}

		return null;
	}

	public ObjectResponse<AgmAlleleAssociation> getAssociation(Long agmId, String relationName, Long alleleId) {
		AgmAlleleAssociation association = null;

		Map<String, Object> params = new HashMap<>();
		params.put("agmAssociationSubject.id", agmId);
		params.put("relation.name", relationName);
		params.put("agmAlleleAssociationObject.id", alleleId);

		SearchResponse<AgmAlleleAssociation> resp = agmAlleleAssociationDAO.findByParams(params);
		if (resp != null && resp.getSingleResult() != null) {
			association = resp.getSingleResult();
		}

		ObjectResponse<AgmAlleleAssociation> response = new ObjectResponse<>();
		response.setEntity(association);

		return response;
	}

	private void addAssociationToAgm(AgmAlleleAssociation association) {
		AffectedGenomicModel agm = association.getAgmAssociationSubject();
		List<AgmAlleleAssociation> currentAssociations = agm.getComponents();
		if (currentAssociations == null) {
			currentAssociations = new ArrayList<>();
			agm.setComponents(currentAssociations);
		}

		List<Long> currentAssociationIds = new ArrayList<>();
		for (AgmAlleleAssociation aga : currentAssociations) {
			currentAssociationIds.add(aga.getId());
		}

		if (!currentAssociationIds.contains(association.getId())) {
			currentAssociations.add(association);
		}
	}

}
