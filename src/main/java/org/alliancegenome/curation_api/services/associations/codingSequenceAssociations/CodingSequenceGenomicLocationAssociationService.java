package org.alliancegenome.curation_api.services.associations.codingSequenceAssociations;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.dao.associations.codingSequenceAssociations.CodingSequenceGenomicLocationAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.CodingSequence;
import org.alliancegenome.curation_api.model.entities.associations.codingSequenceAssociations.CodingSequenceGenomicLocationAssociation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.apache.commons.lang.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class CodingSequenceGenomicLocationAssociationService extends BaseEntityCrudService<CodingSequenceGenomicLocationAssociation, CodingSequenceGenomicLocationAssociationDAO> {

	@Inject CodingSequenceGenomicLocationAssociationDAO codingSequenceGenomicLocationAssociationDAO;
	@Inject PersonDAO personDAO;
	@Inject PersonService personService;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(codingSequenceGenomicLocationAssociationDAO);
	}


	public List<Long> getIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.CODING_SEQUENCE_ASSOCIATION_SUBJECT_DATA_PROVIDER, dataProvider.sourceOrganization);
		if (StringUtils.equals(dataProvider.sourceOrganization, "RGD")) {
			params.put(EntityFieldConstants.CODING_SEQUENCE_ASSOCIATION_SUBJECT_TAXON, dataProvider.canonicalTaxonCurie);
		}
		List<Long> associationIds = codingSequenceGenomicLocationAssociationDAO.findIdsByParams(params);
		associationIds.removeIf(Objects::isNull);

		return associationIds;
	}

	@Override
	@Transactional
	public CodingSequenceGenomicLocationAssociation deprecateOrDelete(Long id, Boolean throwApiError, String loadDescription, Boolean deprecate) {
		CodingSequenceGenomicLocationAssociation association = codingSequenceGenomicLocationAssociationDAO.find(id);

		if (association == null) {
			String errorMessage = "Could not find CodingSequenceGenomicLocationAssociation with id: " + id;
			if (throwApiError) {
				ObjectResponse<CodingSequenceGenomicLocationAssociation> response = new ObjectResponse<>();
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
				return codingSequenceGenomicLocationAssociationDAO.persist(association);
			}
			return association;
		}
		
		codingSequenceGenomicLocationAssociationDAO.remove(association.getId());
		
		return null;
	}

	public ObjectResponse<CodingSequenceGenomicLocationAssociation> getLocationAssociation(Long codingSequenceId, Long assemblyComponentId) {
		CodingSequenceGenomicLocationAssociation association = null;

		Map<String, Object> params = new HashMap<>();
		params.put("codingSequenceAssociationSubject.id", codingSequenceId);
		params.put("codingSequenceGenomicLocationAssociationObject.id", assemblyComponentId);

		SearchResponse<CodingSequenceGenomicLocationAssociation> resp = codingSequenceGenomicLocationAssociationDAO.findByParams(params);
		if (resp != null && resp.getSingleResult() != null) {
			association = resp.getSingleResult();
		}

		ObjectResponse<CodingSequenceGenomicLocationAssociation> response = new ObjectResponse<>();
		response.setEntity(association);

		return response;
	}
	
	public void addAssociationToSubject(CodingSequenceGenomicLocationAssociation association) {
		CodingSequence cds = association.getCodingSequenceAssociationSubject();
		
		List<CodingSequenceGenomicLocationAssociation> currentSubjectAssociations = cds.getCodingSequenceGenomicLocationAssociations();
		if (currentSubjectAssociations == null) {
			currentSubjectAssociations = new ArrayList<>();
		}
		
		List<Long> currentSubjectAssociationIds = currentSubjectAssociations.stream().map(CodingSequenceGenomicLocationAssociation::getId).collect(Collectors.toList());
		
		if (!currentSubjectAssociationIds.contains(association.getId())) {
			currentSubjectAssociations.add(association);
		}
	}
}
