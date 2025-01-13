package org.alliancegenome.curation_api.services.validation.dto.associations.alleleAssociations;

import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.associations.alleleAssociations.AlleleVariantAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleVariantAssociation;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.alleleAssociations.AlleleVariantAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.services.VariantService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleVariantAssociationDTOValidator extends AlleleGenomicEntityAssociationDTOValidator {

	@Inject AlleleVariantAssociationDAO alleleVariantAssociationDAO;
	@Inject AlleleService alleleService;
	@Inject VariantService variantService;
	@Inject VocabularyTermService vocabularyTermService;

	public AlleleVariantAssociation validateAlleleVariantAssociationDTO(AlleleVariantAssociationDTO dto, BackendBulkDataProvider beDataProvider) throws ValidationException {
		ObjectResponse<AlleleVariantAssociation> agaResponse = new ObjectResponse<AlleleVariantAssociation>();

		List<Long> subjectIds = null;
		if (StringUtils.isBlank(dto.getAlleleIdentifier())) {
			agaResponse.addErrorMessage("allele_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			subjectIds = alleleService.findIdsByIdentifierString(dto.getAlleleIdentifier());
			if (subjectIds == null || subjectIds.size() != 1) {
				agaResponse.addErrorMessage("allele_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAlleleIdentifier() + ")");
			}
		}

		List<Long> objectIds = null;
		if (StringUtils.isBlank(dto.getVariantIdentifier())) {
			agaResponse.addErrorMessage("variant_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			objectIds = variantService.findIdsByIdentifierString(dto.getVariantIdentifier());
			if (objectIds == null || objectIds.size() != 1) {
				agaResponse.addErrorMessage("variant_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getVariantIdentifier() + ")");
			}
		}

		AlleleVariantAssociation association = null;
		if (subjectIds != null && subjectIds.size() == 1 && objectIds != null || objectIds.size() == 1 && StringUtils.isNotBlank(dto.getRelationName())) {
			HashMap<String, Object> params = new HashMap<>();

			params.put("alleleAssociationSubject.id", subjectIds.get(0));
			params.put("relation.name", dto.getRelationName());
			params.put("alleleVariantAssociationObject.id", objectIds.get(0));

			SearchResponse<AlleleVariantAssociation> searchResponse = alleleVariantAssociationDAO.findByParams(params);
			if (searchResponse != null && searchResponse.getResults().size() == 1) {
				association = searchResponse.getSingleResult();
			}
		}

		if (association == null) {
			association = new AlleleVariantAssociation();
		}

		VocabularyTerm relation = null;
		if (StringUtils.isNotEmpty(dto.getRelationName())) {
			relation = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.ALLELE_VARIANT_RELATION_VOCABULARY_TERM_SET, dto.getRelationName()).getEntity();
			if (relation == null) {
				agaResponse.addErrorMessage("relation_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getRelationName() + ")");
			}
		} else {
			agaResponse.addErrorMessage("relation_name", ValidationConstants.REQUIRED_MESSAGE);
		}
		association.setRelation(relation);

		if (association.getAlleleAssociationSubject() == null && !StringUtils.isBlank(dto.getAlleleIdentifier())) {

			Allele subject = alleleService.findByIdentifierString(dto.getAlleleIdentifier());
			if (subject == null) {
				agaResponse.addErrorMessage("allele_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAlleleIdentifier() + ")");
			} else if (beDataProvider != null && !subject.getDataProvider().getAbbreviation().equals(beDataProvider.sourceOrganization)) {
				agaResponse.addErrorMessage("allele_identifier", ValidationConstants.INVALID_MESSAGE + " for " + beDataProvider.name() + " load (" + dto.getAlleleIdentifier() + ")");
			} else {
				association.setAlleleAssociationSubject(subject);
			}
		}

		if (association.getAlleleVariantAssociationObject() == null && !StringUtils.isBlank(dto.getVariantIdentifier())) {

			Variant object = variantService.findByIdentifierString(dto.getVariantIdentifier());
			if (object == null) {
				agaResponse.addErrorMessage("variant_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getVariantIdentifier() + ")");
			} else if (beDataProvider != null && !object.getDataProvider().getAbbreviation().equals(beDataProvider.sourceOrganization)) {
				agaResponse.addErrorMessage("variant_identifier", ValidationConstants.INVALID_MESSAGE + " for " + beDataProvider.name() + " load (" + dto.getVariantIdentifier() + ")");
			} else {
				association.setAlleleVariantAssociationObject(object);
			}
		}

		ObjectResponse<AlleleGenomicEntityAssociation> ageaResponse = validateAlleleGenomicEntityAssociationDTO(association, dto);
		agaResponse.addErrorMessages(ageaResponse.getErrorMessages());
		association = (AlleleVariantAssociation) ageaResponse.getEntity();

		if (agaResponse.hasErrors()) {
			throw new ObjectValidationException(dto, agaResponse.errorMessagesString());
		}

		association = alleleVariantAssociationDAO.persist(association);
		return association;
	}
}
