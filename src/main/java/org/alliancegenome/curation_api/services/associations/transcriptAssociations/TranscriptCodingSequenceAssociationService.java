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
import org.alliancegenome.curation_api.dao.associations.transcriptAssociations.TranscriptCodingSequenceAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.CodingSequence;
import org.alliancegenome.curation_api.model.entities.Transcript;
import org.alliancegenome.curation_api.model.entities.associations.transcriptAssociations.TranscriptCodingSequenceAssociation;
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
public class TranscriptCodingSequenceAssociationService extends BaseEntityCrudService<TranscriptCodingSequenceAssociation, TranscriptCodingSequenceAssociationDAO> {

	@Inject TranscriptCodingSequenceAssociationDAO transcriptCodingSequenceAssociationDAO;
	@Inject PersonDAO personDAO;
	@Inject PersonService personService;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(transcriptCodingSequenceAssociationDAO);
	}


	public List<Long> getIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.TRANSCRIPT_ASSOCIATION_SUBJECT_DATA_PROVIDER, dataProvider.sourceOrganization);
		if (StringUtils.equals(dataProvider.sourceOrganization, "RGD")) {
			params.put(EntityFieldConstants.TRANSCRIPT_ASSOCIATION_SUBJECT_TAXON, dataProvider.canonicalTaxonCurie);
		}
		List<Long> associationIds = transcriptCodingSequenceAssociationDAO.findIdsByParams(params);
		associationIds.removeIf(Objects::isNull);

		return associationIds;
	}

	@Override
	@Transactional
	public TranscriptCodingSequenceAssociation deprecateOrDelete(Long id, Boolean throwApiError, String loadDescription, Boolean deprecate) {
		TranscriptCodingSequenceAssociation association = transcriptCodingSequenceAssociationDAO.find(id);

		if (association == null) {
			String errorMessage = "Could not find TranscriptCodingSequenceAssociation with id: " + id;
			if (throwApiError) {
				ObjectResponse<TranscriptCodingSequenceAssociation> response = new ObjectResponse<>();
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
				return transcriptCodingSequenceAssociationDAO.persist(association);
			}
			return association;
		}
		
		transcriptCodingSequenceAssociationDAO.remove(association.getId());
		
		return null;
	}

	public ObjectResponse<TranscriptCodingSequenceAssociation> getLocationAssociation(Long transcriptId, Long assemblyComponentId) {
		TranscriptCodingSequenceAssociation association = null;

		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.TRANSCRIPT_ASSOCIATION_SUBJECT + ".id", transcriptId);
		params.put("transcriptCodingSequenceAssociationObject.id", assemblyComponentId);

		SearchResponse<TranscriptCodingSequenceAssociation> resp = transcriptCodingSequenceAssociationDAO.findByParams(params);
		if (resp != null && resp.getSingleResult() != null) {
			association = resp.getSingleResult();
		}

		ObjectResponse<TranscriptCodingSequenceAssociation> response = new ObjectResponse<>();
		response.setEntity(association);

		return response;
	}
	
	public void addAssociationToSubjectAndObject(TranscriptCodingSequenceAssociation association) {
		Transcript transcript = association.getTranscriptAssociationSubject();
		
		List<TranscriptCodingSequenceAssociation> currentSubjectAssociations = transcript.getTranscriptCodingSequenceAssociations();
		if (currentSubjectAssociations == null) {
			currentSubjectAssociations = new ArrayList<>();
		}
		
		List<Long> currentSubjectAssociationIds = currentSubjectAssociations.stream()
				.map(TranscriptCodingSequenceAssociation::getId).collect(Collectors.toList());
		
		if (!currentSubjectAssociationIds.contains(association.getId())) {
			currentSubjectAssociations.add(association);
		}
		
		CodingSequence codingSequence = association.getTranscriptCodingSequenceAssociationObject();
		
		List<TranscriptCodingSequenceAssociation> currentObjectAssociations = codingSequence.getTranscriptCodingSequenceAssociations();
		if (currentObjectAssociations == null) {
			currentObjectAssociations = new ArrayList<>();
		}
		
		List<Long> currentObjectAssociationIds = currentObjectAssociations.stream()
				.map(TranscriptCodingSequenceAssociation::getId).collect(Collectors.toList());
		
		if (!currentObjectAssociationIds.contains(association.getId())) {
			currentObjectAssociations.add(association);
		}
	}
}
