package org.alliancegenome.curation_api.services.validation.dto.associations.agmAssociations;

import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.associations.agmAssociations.AgmSequenceTargetingReagentAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.agmAssociations.AgmSequenceTargetingReagentAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.agmAssociations.AgmSequenceTargetingReagentAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.AffectedGenomicModelService;
import org.alliancegenome.curation_api.services.SequenceTargetingReagentService;
import org.alliancegenome.curation_api.services.validation.dto.base.AuditedObjectDTOValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AgmSequenceTargetingReagentAssociationDTOValidator extends AuditedObjectDTOValidator<AgmSequenceTargetingReagentAssociation, AgmSequenceTargetingReagentAssociationDTO> {

	@Inject AgmSequenceTargetingReagentAssociationDAO agmStrAssociationDAO;
	@Inject AffectedGenomicModelService agmService;
	@Inject SequenceTargetingReagentService strService;

	public AgmSequenceTargetingReagentAssociation validateAgmSequenceTargetingReagentAssociationDTO(AgmSequenceTargetingReagentAssociationDTO dto, BackendBulkDataProvider beDataProvider) throws ValidationException {
		response = new ObjectResponse<AgmSequenceTargetingReagentAssociation>();
		
		List<Long> subjectIds = null;
		if (StringUtils.isBlank(dto.getAgmSubjectIdentifier())) {
			response.addErrorMessage("agm_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			subjectIds = agmService.findIdsByIdentifierString(dto.getAgmSubjectIdentifier());
			if (subjectIds == null || subjectIds.size() != 1) {
				response.addErrorMessage("agm_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAgmSubjectIdentifier() + ")");
			}
		}

		List<Long> objectIds = null;
		if (StringUtils.isBlank(dto.getSequenceTargetingReagentIdentifier())) {
			response.addErrorMessage("str_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			objectIds = strService.findIdsByIdentifierString(dto.getSequenceTargetingReagentIdentifier());
			if (objectIds == null || objectIds.size() != 1) {
				response.addErrorMessage("str_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getSequenceTargetingReagentIdentifier() + ")");
			}
		}

		AgmSequenceTargetingReagentAssociation association = null;
		if (subjectIds != null && subjectIds.size() == 1 && objectIds != null && objectIds.size() == 1 && StringUtils.isNotBlank(dto.getRelationName())) {
			HashMap<String, Object> params = new HashMap<>();

			params.put("agmAssociationSubject.id", subjectIds.get(0));
			params.put("relation.name", dto.getRelationName());
			params.put("agmSequenceTargetingReagentAssociationObject.id", objectIds.get(0));

			SearchResponse<AgmSequenceTargetingReagentAssociation> searchResponse = agmStrAssociationDAO.findByParams(params);
			if (searchResponse != null && searchResponse.getResults().size() == 1) {
				association = searchResponse.getSingleResult();
			}
		}

		if (association == null) {
			association = new AgmSequenceTargetingReagentAssociation();
		}

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation_name", dto.getRelationName(), VocabularyConstants.AGM_STR_RELATION_VOCABULARY_TERM_SET);
		association.setRelation(relation);

		if (association.getAgmAssociationSubject() == null && !StringUtils.isBlank(dto.getAgmSubjectIdentifier())) {
			AffectedGenomicModel subject = agmService.findByIdentifierString(dto.getAgmSubjectIdentifier());
			if (subject == null) {
				response.addErrorMessage("agm_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAgmSubjectIdentifier() + ")");
			} else if (beDataProvider != null && !subject.getDataProvider().getAbbreviation().equals(beDataProvider.sourceOrganization)) {
				response.addErrorMessage("agm_identifier", ValidationConstants.INVALID_MESSAGE + " for " + beDataProvider.name() + " load (" + dto.getAgmSubjectIdentifier() + ")");
			} else {
				association.setAgmAssociationSubject(subject);
			}
		}

		if (association.getAgmSequenceTargetingReagentAssociationObject() == null && !StringUtils.isBlank(dto.getSequenceTargetingReagentIdentifier())) {

			SequenceTargetingReagent object = strService.findByIdentifierString(dto.getSequenceTargetingReagentIdentifier());
			if (object == null) {
				response.addErrorMessage("str_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getSequenceTargetingReagentIdentifier() + ")");
			} else if (beDataProvider != null && !object.getDataProvider().getAbbreviation().equals(beDataProvider.sourceOrganization)) {
				response.addErrorMessage("str_identifier", ValidationConstants.INVALID_MESSAGE + " for " + beDataProvider.name() + " load (" + dto.getSequenceTargetingReagentIdentifier() + ")");
			} else {
				association.setAgmSequenceTargetingReagentAssociationObject(object);
			}
		}
		association = validateAuditedObjectDTO(association, dto);
	
		if (response.hasErrors()) {
			throw new ObjectValidationException(dto, response.errorMessagesString());
		}

		association = agmStrAssociationDAO.persist(association);
		return association;
	}
}
