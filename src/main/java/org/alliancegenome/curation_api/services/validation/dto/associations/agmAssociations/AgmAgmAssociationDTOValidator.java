package org.alliancegenome.curation_api.services.validation.dto.associations.agmAssociations;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.associations.agmAssociations.AgmAgmAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.agmAssociations.AgmAgmAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.agmAssociations.AgmAgmAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.AffectedGenomicModelService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;

@RequestScoped
public class AgmAgmAssociationDTOValidator extends BaseDTOValidator {

	@Inject
	AgmAgmAssociationDAO agmStrAssociationDAO;
	@Inject
	AffectedGenomicModelService agmService;
	@Inject
	VocabularyTermService vocabularyTermService;

	public AgmAgmAssociation validateAgmAgmAssociationDTO(AgmAgmAssociationDTO dto, BackendBulkDataProvider beDataProvider) throws ValidationException {
		ObjectResponse<AgmAgmAssociation> asaResponse = new ObjectResponse<>();

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
		if (StringUtils.isBlank(dto.getAgmObjectIdentifier())) {
			asaResponse.addErrorMessage("agm_object_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			objectIds = agmService.findIdsByIdentifierString(dto.getAgmObjectIdentifier());
			if (objectIds == null || objectIds.size() != 1) {
				asaResponse.addErrorMessage("agm_object_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAgmObjectIdentifier() + ")");
			}
		}

		AgmAgmAssociation association = null;
		if (subjectIds != null && subjectIds.size() == 1 && objectIds != null && objectIds.size() == 1 && StringUtils.isNotBlank(dto.getRelationName())) {
			HashMap<String, Object> params = new HashMap<>();

			params.put("agmAssociationSubject.id", subjectIds.get(0));
			params.put("relation.name", dto.getRelationName());
			params.put("agmAssociationObject.id", objectIds.get(0));

			SearchResponse<AgmAgmAssociation> searchResponse = agmStrAssociationDAO.findByParams(params);
			if (searchResponse != null && searchResponse.getResults().size() == 1) {
				association = searchResponse.getSingleResult();
			}
		}

		if (association == null) {
			association = new AgmAgmAssociation();
		}

		VocabularyTerm relation = null;
		if (StringUtils.isNotEmpty(dto.getRelationName())) {
			relation = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.AGM_AGM_RELATION_VOCABULARY_TERM_SET, dto.getRelationName()).getEntity();
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
			} else if (beDataProvider != null && !subject.getDataProvider().getAbbreviation().equals(beDataProvider.sourceOrganization)) {
				asaResponse.addErrorMessage("agm_identifier", ValidationConstants.INVALID_MESSAGE + " for " + beDataProvider.name() + " load (" + dto.getAgmSubjectIdentifier() + ")");
			} else {
				association.setAgmAssociationSubject(subject);
			}
		}

		if (association.getAgmAssociationObject() == null && !StringUtils.isBlank(dto.getAgmObjectIdentifier())) {

			AffectedGenomicModel object = agmService.findByIdentifierString(dto.getAgmObjectIdentifier());
			if (object == null) {
				asaResponse.addErrorMessage("agm_object_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAgmObjectIdentifier() + ")");
			} else if (beDataProvider != null && !object.getDataProvider().getAbbreviation().equals(beDataProvider.sourceOrganization)) {
				asaResponse.addErrorMessage("agm_object_identifier", ValidationConstants.INVALID_MESSAGE + " for " + beDataProvider.name() + " load (" + dto.getAgmObjectIdentifier() + ")");
			} else {
				association.setAgmAssociationObject(object);
			}
		}
		ObjectResponse<AgmAgmAssociation> assocResponse = validateAuditedObjectDTO(association, dto);
		asaResponse.addErrorMessages(assocResponse.getErrorMessages());
		association = assocResponse.getEntity();
		if (asaResponse.hasErrors()) {
			throw new ObjectValidationException(dto, asaResponse.errorMessagesString());
		}

		association = agmStrAssociationDAO.persist(association);
		return association;
	}
}
