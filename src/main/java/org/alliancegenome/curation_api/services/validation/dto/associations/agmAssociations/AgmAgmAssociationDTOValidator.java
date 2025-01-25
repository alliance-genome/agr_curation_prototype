package org.alliancegenome.curation_api.services.validation.dto.associations.agmAssociations;

import java.util.HashMap;
import java.util.List;

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
import org.alliancegenome.curation_api.services.validation.dto.base.AuditedObjectDTOValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AgmAgmAssociationDTOValidator extends AuditedObjectDTOValidator<AgmAgmAssociation, AgmAgmAssociationDTO> {

	@Inject AgmAgmAssociationDAO agmAgmAssociationDAO;
	@Inject AffectedGenomicModelService agmService;

	public AgmAgmAssociation validateAgmAgmAssociationDTO(AgmAgmAssociationDTO dto, BackendBulkDataProvider beDataProvider) throws ValidationException {
		response = new ObjectResponse<AgmAgmAssociation>();

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
		if (StringUtils.isBlank(dto.getAgmObjectIdentifier())) {
			response.addErrorMessage("agm_object_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			objectIds = agmService.findIdsByIdentifierString(dto.getAgmObjectIdentifier());
			if (objectIds == null || objectIds.size() != 1) {
				response.addErrorMessage("agm_object_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAgmObjectIdentifier() + ")");
			}
		}

		AgmAgmAssociation association = null;
		if (subjectIds != null && subjectIds.size() == 1 && objectIds != null && objectIds.size() == 1 && StringUtils.isNotBlank(dto.getRelationName())) {
			HashMap<String, Object> params = new HashMap<>();

			params.put("agmAssociationSubject.id", subjectIds.get(0));
			params.put("relation.name", dto.getRelationName());
			params.put("agmAgmAssociationObject.id", objectIds.get(0));

			SearchResponse<AgmAgmAssociation> searchResponse = agmAgmAssociationDAO.findByParams(params);
			if (searchResponse != null && searchResponse.getResults().size() == 1) {
				association = searchResponse.getSingleResult();
			}
		}

		if (association == null) {
			association = new AgmAgmAssociation();
		}

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation_name", dto.getRelationName(), VocabularyConstants.AGM_AGM_RELATION_VOCABULARY_TERM_SET);
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

		if (association.getAgmAgmAssociationObject() == null && !StringUtils.isBlank(dto.getAgmObjectIdentifier())) {

			AffectedGenomicModel object = agmService.findByIdentifierString(dto.getAgmObjectIdentifier());
			if (object == null) {
				response.addErrorMessage("agm_object_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAgmObjectIdentifier() + ")");
			} else if (beDataProvider != null && !object.getDataProvider().getAbbreviation().equals(beDataProvider.sourceOrganization)) {
				response.addErrorMessage("agm_object_identifier", ValidationConstants.INVALID_MESSAGE + " for " + beDataProvider.name() + " load (" + dto.getAgmObjectIdentifier() + ")");
			} else {
				association.setAgmAgmAssociationObject(object);
			}
		}
		association = validateAuditedObjectDTO(association, dto);
		
		if (response.hasErrors()) {
			throw new ObjectValidationException(dto, response.errorMessagesString());
		}

		association = agmAgmAssociationDAO.persist(association);
		return association;
	}
}
