package org.alliancegenome.curation_api.services.associations.transcriptAssociations;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.dao.associations.transcriptAssociations.TranscriptGenomicLocationAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Transcript;
import org.alliancegenome.curation_api.model.entities.associations.transcriptAssociations.TranscriptGenomicLocationAssociation;
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
public class TranscriptGenomicLocationAssociationService extends BaseEntityCrudService<TranscriptGenomicLocationAssociation, TranscriptGenomicLocationAssociationDAO> {

	@Inject TranscriptGenomicLocationAssociationDAO transcriptGenomicLocationAssociationDAO;
	@Inject PersonDAO personDAO;
	@Inject PersonService personService;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(transcriptGenomicLocationAssociationDAO);
	}


	public List<Long> getIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.TRANSCRIPT_ASSOCIATION_SUBJECT_DATA_PROVIDER, dataProvider.sourceOrganization);
		if (StringUtils.equals(dataProvider.sourceOrganization, "RGD")) {
			params.put(EntityFieldConstants.TRANSCRIPT_ASSOCIATION_SUBJECT_TAXON, dataProvider.canonicalTaxonCurie);
		}
		List<Long> associationIds = transcriptGenomicLocationAssociationDAO.findIdsByParams(params);
		associationIds.removeIf(Objects::isNull);

		return associationIds;
	}

	@Override
	@Transactional
	public TranscriptGenomicLocationAssociation deprecateOrDelete(Long id, Boolean throwApiError, String loadDescription, Boolean deprecate) {
		TranscriptGenomicLocationAssociation association = transcriptGenomicLocationAssociationDAO.find(id);

		if (association == null) {
			String errorMessage = "Could not find TranscriptGenomicLocationAssociation with id: " + id;
			if (throwApiError) {
				ObjectResponse<TranscriptGenomicLocationAssociation> response = new ObjectResponse<>();
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
				return transcriptGenomicLocationAssociationDAO.persist(association);
			}
			return association;
		}
		
		transcriptGenomicLocationAssociationDAO.remove(association.getId());
		
		return null;
	}

	public ObjectResponse<TranscriptGenomicLocationAssociation> getLocationAssociation(Long transcriptId, Long assemblyComponentId) {
		TranscriptGenomicLocationAssociation association = null;

		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.TRANSCRIPT_ASSOCIATION_SUBJECT + ".id", transcriptId);
		params.put("transcriptGenomicLocationAssociationObject.id", assemblyComponentId);

		SearchResponse<TranscriptGenomicLocationAssociation> resp = transcriptGenomicLocationAssociationDAO.findByParams(params);
		if (resp != null && resp.getSingleResult() != null) {
			association = resp.getSingleResult();
		}

		ObjectResponse<TranscriptGenomicLocationAssociation> response = new ObjectResponse<>();
		response.setEntity(association);

		return response;
	}
	
	public void addAssociationToSubject(TranscriptGenomicLocationAssociation association) {
		Transcript transcript = association.getTranscriptAssociationSubject();
		
		List<TranscriptGenomicLocationAssociation> currentSubjectAssociations = transcript.getTranscriptGenomicLocationAssociations();
		if (currentSubjectAssociations == null) {
			currentSubjectAssociations = new ArrayList<>();
		}
		
		List<Long> currentSubjectAssociationIds = currentSubjectAssociations.stream()
				.map(TranscriptGenomicLocationAssociation::getId).collect(Collectors.toList());
		
		if (!currentSubjectAssociationIds.contains(association.getId())) {
			currentSubjectAssociations.add(association);
		}
	}
}
