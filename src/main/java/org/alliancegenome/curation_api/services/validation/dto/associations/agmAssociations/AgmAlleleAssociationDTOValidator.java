package org.alliancegenome.curation_api.services.validation.dto.associations.agmAssociations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.associations.agmAssociations.AgmAlleleAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.agmAssociations.AgmAlleleAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.agmAssociations.AgmAlleleAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.AffectedGenomicModelService;
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AgmAlleleAssociationDTOValidator extends BaseDTOValidator {
	@Inject AgmAlleleAssociationDAO agmAlleleAssociationDAO;
	@Inject AffectedGenomicModelService agmService;
	@Inject AlleleService alleleService;
	@Inject VocabularyTermService vocabularyTermService;

	public AgmAlleleAssociation validateAgmAlleleAssociationDTO(AgmAlleleAssociationDTO dto, BackendBulkDataProvider beDataProvider) throws ValidationException {
		ObjectResponse<AgmAlleleAssociation> aaaResponse = new ObjectResponse<AgmAlleleAssociation>();

		List<String> zygosityCuries = Arrays.asList(
		"GENO:0000602",
			"GENO:0000603",
			"GENO:0000604",
			"GENO:0000605",
			"GENO:0000606",
			"GENO:0000135",
			"GENO:0000136",
			"GENO:0000137",
			"GENO:0000134"
		);

		List<Long> subjectIds = null;
		if (StringUtils.isBlank(dto.getAgmSubjectIdentifier())) {
			aaaResponse.addErrorMessage("agm_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			subjectIds = agmService.findIdsByIdentifierString(dto.getAgmSubjectIdentifier());
			if (subjectIds == null || subjectIds.size() != 1) {
				aaaResponse.addErrorMessage("agm_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAgmSubjectIdentifier() + ")");
			}
		}

		List<Long> objectIds = null;
		if (StringUtils.isBlank(dto.getAlleleIdentifier())) {
			aaaResponse.addErrorMessage("allele_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			objectIds = alleleService.findIdsByIdentifierString(dto.getAlleleIdentifier());
			if (objectIds == null || objectIds.size() != 1) {
				aaaResponse.addErrorMessage("allele_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAlleleIdentifier() + ")");
			}
		}

		AgmAlleleAssociation association = null;
		if (subjectIds != null && subjectIds.size() == 1 && objectIds != null && objectIds.size() == 1 && StringUtils.isNotBlank(dto.getRelationName())) {
			HashMap<String, Object> params = new HashMap<>();

			params.put("agmAssociationSubject.id", subjectIds.get(0));
			params.put("relation.name", dto.getRelationName());
			params.put("agmAlleleAssociationObject.id", objectIds.get(0));

			SearchResponse<AgmAlleleAssociation> searchResponse = agmAlleleAssociationDAO.findByParams(params);
			if (searchResponse != null && searchResponse.getResults().size() == 1) {
				association = searchResponse.getSingleResult();
			}
		}

		if (association == null) {
			association = new AgmAlleleAssociation();
		}

		VocabularyTerm relation = null;
		if (StringUtils.isNotEmpty(dto.getRelationName())) {
			relation = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.AGM_ALLELE_RELATION_VOCABULARY_TERM_SET, dto.getRelationName()).getEntity();
			if (relation == null) {
				aaaResponse.addErrorMessage("relation_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getRelationName() + ")");
			}
		} else {
			aaaResponse.addErrorMessage("relation_name", ValidationConstants.REQUIRED_MESSAGE);
		}
		association.setRelation(relation);

		if (association.getAgmAssociationSubject() == null && !StringUtils.isBlank(dto.getAgmSubjectIdentifier())) {

			AffectedGenomicModel subject = agmService.findByIdentifierString(dto.getAgmSubjectIdentifier());
			if (subject == null) {
				aaaResponse.addErrorMessage("agm_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAgmSubjectIdentifier() + ")");
			} else if (beDataProvider != null && !subject.getDataProvider().getAbbreviation().equals(beDataProvider.sourceOrganization)) {
				aaaResponse.addErrorMessage("agm_identifier", ValidationConstants.INVALID_MESSAGE + " for " + beDataProvider.name() + " load (" + dto.getAgmSubjectIdentifier() + ")");
			} else {
				association.setAgmAssociationSubject(subject);
			}
		}

		if (association.getAgmAlleleAssociationObject() == null && !StringUtils.isBlank(dto.getAlleleIdentifier())) {

			Allele object = alleleService.findByIdentifierString(dto.getAlleleIdentifier());
			if (object == null) {
				aaaResponse.addErrorMessage("allele_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAlleleIdentifier() + ")");
			} else if (beDataProvider != null && !object.getDataProvider().getAbbreviation().equals(beDataProvider.sourceOrganization)) {
				aaaResponse.addErrorMessage("allele_identifier", ValidationConstants.INVALID_MESSAGE + " for " + beDataProvider.name() + " load (" + dto.getAlleleIdentifier() + ")");
			} else {
				association.setAgmAlleleAssociationObject(object);
			}
		}

		if (StringUtils.isBlank(dto.getZygosityCurie())) {
			aaaResponse.addErrorMessage("zygosity", ValidationConstants.INVALID_MESSAGE + " (" + dto.getZygosityCurie() + ")");
		} else if (!zygosityCuries.contains(dto.getZygosityCurie())) {
			aaaResponse.addErrorMessage("zygosity", ValidationConstants.INVALID_MESSAGE + " (" + dto.getZygosityCurie() + ")");
		} else {
			association.setZygosity(dto.getZygosityCurie());
		}

		ObjectResponse<AgmAlleleAssociation> assocResponse = validateAuditedObjectDTO(association, dto);
		aaaResponse.addErrorMessages(assocResponse.getErrorMessages());
		association = (AgmAlleleAssociation) assocResponse.getEntity();

		if (aaaResponse.hasErrors()) {
			throw new ObjectValidationException(dto, aaaResponse.errorMessagesString());
		}
		association = agmAlleleAssociationDAO.persist(association);

		return association;
	}
}
