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
import org.alliancegenome.curation_api.dao.associations.transcriptAssociations.TranscriptGeneAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Transcript;
import org.alliancegenome.curation_api.model.entities.associations.transcriptAssociations.TranscriptGeneAssociation;
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
public class TranscriptGeneAssociationService extends BaseEntityCrudService<TranscriptGeneAssociation, TranscriptGeneAssociationDAO> {

	@Inject TranscriptGeneAssociationDAO transcriptGeneAssociationDAO;
	@Inject PersonDAO personDAO;
	@Inject PersonService personService;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(transcriptGeneAssociationDAO);
	}


	public List<Long> getIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.TRANSCRIPT_ASSOCIATION_SUBJECT_DATA_PROVIDER, dataProvider.sourceOrganization);
		if (StringUtils.equals(dataProvider.sourceOrganization, "RGD")) {
			params.put(EntityFieldConstants.TRANSCRIPT_ASSOCIATION_SUBJECT_TAXON, dataProvider.canonicalTaxonCurie);
		}
		List<Long> associationIds = transcriptGeneAssociationDAO.findIdsByParams(params);
		associationIds.removeIf(Objects::isNull);

		return associationIds;
	}

	@Override
	@Transactional
	public TranscriptGeneAssociation deprecateOrDelete(Long id, Boolean throwApiError, String loadDescription, Boolean deprecate) {
		TranscriptGeneAssociation association = transcriptGeneAssociationDAO.find(id);

		if (association == null) {
			String errorMessage = "Could not find TranscriptGeneAssociation with id: " + id;
			if (throwApiError) {
				ObjectResponse<TranscriptGeneAssociation> response = new ObjectResponse<>();
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
				return transcriptGeneAssociationDAO.persist(association);
			}
			return association;
		}
		
		transcriptGeneAssociationDAO.remove(association.getId());
		
		return null;
	}

	public ObjectResponse<TranscriptGeneAssociation> getLocationAssociation(Long transcriptId, Long assemblyComponentId) {
		TranscriptGeneAssociation association = null;

		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.TRANSCRIPT_ASSOCIATION_SUBJECT + ".id", transcriptId);
		params.put("transcriptGeneAssociationObject.id", assemblyComponentId);

		SearchResponse<TranscriptGeneAssociation> resp = transcriptGeneAssociationDAO.findByParams(params);
		if (resp != null && resp.getSingleResult() != null) {
			association = resp.getSingleResult();
		}

		ObjectResponse<TranscriptGeneAssociation> response = new ObjectResponse<>();
		response.setEntity(association);

		return response;
	}
	
	public void addAssociationToSubjectAndObject(TranscriptGeneAssociation association) {
		Transcript transcript = association.getTranscriptAssociationSubject();
		
		List<TranscriptGeneAssociation> currentSubjectAssociations = transcript.getTranscriptGeneAssociations();
		if (currentSubjectAssociations == null) {
			currentSubjectAssociations = new ArrayList<>();
		}
		
		List<Long> currentSubjectAssociationIds = currentSubjectAssociations.stream()
				.map(TranscriptGeneAssociation::getId).collect(Collectors.toList());
		
		if (!currentSubjectAssociationIds.contains(association.getId())) {
			currentSubjectAssociations.add(association);
		}
		
		Gene gene = association.getTranscriptGeneAssociationObject();
		
		List<TranscriptGeneAssociation> currentObjectAssociations = gene.getTranscriptGeneAssociations();
		if (currentObjectAssociations == null) {
			currentObjectAssociations = new ArrayList<>();
		}
		
		List<Long> currentObjectAssociationIds = currentObjectAssociations.stream()
				.map(TranscriptGeneAssociation::getId).collect(Collectors.toList());
		
		if (!currentObjectAssociationIds.contains(association.getId())) {
			currentObjectAssociations.add(association);
		}
	}
}
