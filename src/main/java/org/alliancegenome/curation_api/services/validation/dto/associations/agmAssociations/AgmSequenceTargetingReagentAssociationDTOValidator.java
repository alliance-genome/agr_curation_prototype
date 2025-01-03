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
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AgmSequenceTargetingReagentAssociationDTOValidator extends BaseDTOValidator {

	@Inject AgmSequenceTargetingReagentAssociationDAO agmStrAssociationDAO;
	@Inject AffectedGenomicModelService agmService;
	@Inject SequenceTargetingReagentService strService;
	@Inject VocabularyTermService vocabularyTermService;

	public AgmSequenceTargetingReagentAssociation validateAgmSequenceTargetingReagentAssociationDTO(AgmSequenceTargetingReagentAssociationDTO dto, BackendBulkDataProvider beDataProvider) throws ValidationException {
		ObjectResponse<AgmSequenceTargetingReagentAssociation> asaResponse = new ObjectResponse<AgmSequenceTargetingReagentAssociation>();

		List<Long> subjectIds = null;
		if (StringUtils.isBlank(dto.getAgmSubjectIdentifier())) {
			asaResponse.addErrorMessage("agm_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			subjectIds = agmService.findIdsByIdentifierString(dto.getAgmSubjectIdentifier());
			if (subjectIds == null || subjectIds.size() != 1) {
				asaResponse.addErrorMessage("agm_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAgmSubjectIdentifier() + ")");
			}
		}

		List<Long> objectIds = null;
		if (StringUtils.isBlank(dto.getSequenceTargetingReagentIdentifier())) {
			asaResponse.addErrorMessage("str_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			objectIds = strService.findIdsByIdentifierString(dto.getSequenceTargetingReagentIdentifier());
			if (objectIds == null || objectIds.size() != 1) {
				asaResponse.addErrorMessage("str_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getSequenceTargetingReagentIdentifier() + ")");
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

		VocabularyTerm relation = null;
		if (StringUtils.isNotEmpty(dto.getRelationName())) {
			relation = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.AGM_STR_RELATION_VOCABULARY_TERM_SET, dto.getRelationName()).getEntity();
			if (relation == null) {
				asaResponse.addErrorMessage("relation_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getRelationName() + ")");
			}
		} else {
			asaResponse.addErrorMessage("relation_name", ValidationConstants.REQUIRED_MESSAGE);
		}
		association.setRelation(relation);

		if (association.getAgmAssociationSubject() == null && !StringUtils.isBlank(dto.getAgmSubjectIdentifier())) {

			AffectedGenomicModel subject = agmService.findByIdentifierString(dto.getAgmSubjectIdentifier());
			if (subject == null) {
				asaResponse.addErrorMessage("agm_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAgmSubjectIdentifier() + ")");
			} else if (beDataProvider != null && !subject.getDataProvider().getSourceOrganization().getAbbreviation().equals(beDataProvider.sourceOrganization)) {
				asaResponse.addErrorMessage("agm_identifier", ValidationConstants.INVALID_MESSAGE + " for " + beDataProvider.name() + " load (" + dto.getAgmSubjectIdentifier() + ")");
			} else {
				association.setAgmAssociationSubject(subject);
			}
		}

		if (association.getAgmSequenceTargetingReagentAssociationObject() == null && !StringUtils.isBlank(dto.getSequenceTargetingReagentIdentifier())) {

			SequenceTargetingReagent object = strService.findByIdentifierString(dto.getSequenceTargetingReagentIdentifier());
			if (object == null) {
				asaResponse.addErrorMessage("str_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getSequenceTargetingReagentIdentifier() + ")");
			} else if (beDataProvider != null && !object.getDataProvider().getSourceOrganization().getAbbreviation().equals(beDataProvider.sourceOrganization)) {
				asaResponse.addErrorMessage("str_identifier", ValidationConstants.INVALID_MESSAGE + " for " + beDataProvider.name() + " load (" + dto.getSequenceTargetingReagentIdentifier() + ")");
			} else {
				association.setAgmSequenceTargetingReagentAssociationObject(object);
			}
		}
		ObjectResponse<AgmSequenceTargetingReagentAssociation> assocResponse = validateAuditedObjectDTO(association, dto);
		asaResponse.addErrorMessages(assocResponse.getErrorMessages());
		association = (AgmSequenceTargetingReagentAssociation) assocResponse.getEntity();
		if (asaResponse.hasErrors()) {
			throw new ObjectValidationException(dto, asaResponse.errorMessagesString());
		}

		association = agmStrAssociationDAO.persist(association);
		return association;
	}
}
