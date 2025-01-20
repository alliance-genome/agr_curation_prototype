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
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleVariantAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.alleleAssociations.AlleleVariantAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.services.VariantService;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleVariantAssociationDTOValidator extends AlleleGenomicEntityAssociationDTOValidator<AlleleVariantAssociation, AlleleVariantAssociationDTO> {

	@Inject AlleleVariantAssociationDAO alleleVariantAssociationDAO;
	@Inject AlleleService alleleService;
	@Inject VariantService variantService;

	public AlleleVariantAssociation validateAlleleVariantAssociationDTO(AlleleVariantAssociationDTO dto, BackendBulkDataProvider beDataProvider) throws ValidationException {
		response = new ObjectResponse<AlleleVariantAssociation>();
		
		List<Long> subjectIds = null;
		if (StringUtils.isBlank(dto.getAlleleIdentifier())) {
			response.addErrorMessage("allele_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			subjectIds = alleleService.findIdsByIdentifierString(dto.getAlleleIdentifier());
			if (subjectIds == null || subjectIds.size() != 1) {
				response.addErrorMessage("allele_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAlleleIdentifier() + ")");
			}
		}

		List<Long> objectIds = null;
		if (StringUtils.isBlank(dto.getVariantIdentifier())) {
			response.addErrorMessage("variant_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			objectIds = variantService.findIdsByIdentifierString(dto.getVariantIdentifier());
			if (objectIds == null || objectIds.size() != 1) {
				response.addErrorMessage("variant_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getVariantIdentifier() + ")");
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

		association = validateAlleleGenomicEntityAssociationDTO(association, dto, VocabularyConstants.ALLELE_VARIANT_RELATION_VOCABULARY_TERM_SET);
		
		if (association.getAlleleAssociationSubject() == null && !StringUtils.isBlank(dto.getAlleleIdentifier())) {

			Allele subject = alleleService.findByIdentifierString(dto.getAlleleIdentifier());
			if (subject == null) {
				response.addErrorMessage("allele_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAlleleIdentifier() + ")");
			} else if (beDataProvider != null && !subject.getDataProvider().getAbbreviation().equals(beDataProvider.sourceOrganization)) {
				response.addErrorMessage("allele_identifier", ValidationConstants.INVALID_MESSAGE + " for " + beDataProvider.name() + " load (" + dto.getAlleleIdentifier() + ")");
			} else {
				association.setAlleleAssociationSubject(subject);
			}
		}

		if (association.getAlleleVariantAssociationObject() == null && !StringUtils.isBlank(dto.getVariantIdentifier())) {

			Variant object = variantService.findByIdentifierString(dto.getVariantIdentifier());
			if (object == null) {
				response.addErrorMessage("variant_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getVariantIdentifier() + ")");
			} else if (beDataProvider != null && !object.getDataProvider().getAbbreviation().equals(beDataProvider.sourceOrganization)) {
				response.addErrorMessage("variant_identifier", ValidationConstants.INVALID_MESSAGE + " for " + beDataProvider.name() + " load (" + dto.getVariantIdentifier() + ")");
			} else {
				association.setAlleleVariantAssociationObject(object);
			}
		}

		if (response.hasErrors()) {
			throw new ObjectValidationException(dto, response.errorMessagesString());
		}

		association = alleleVariantAssociationDAO.persist(association);
		return association;
	}
}
