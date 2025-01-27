package org.alliancegenome.curation_api.services.validation.dto.associations.alleleAssociations;

import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.associations.alleleAssociations.AlleleGeneAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.alleleAssociations.AlleleGeneAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.services.GeneService;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleGeneAssociationDTOValidator extends AlleleGenomicEntityAssociationDTOValidator<AlleleGeneAssociation, AlleleGeneAssociationDTO> {

	@Inject AlleleGeneAssociationDAO alleleGeneAssociationDAO;
	@Inject AlleleService alleleService;
	@Inject GeneService geneService;
	public AlleleGeneAssociation validateAlleleGeneAssociationDTO(AlleleGeneAssociationDTO dto, BackendBulkDataProvider beDataProvider) throws ValidationException {
		response = new ObjectResponse<AlleleGeneAssociation>();
		
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
		if (StringUtils.isBlank(dto.getGeneIdentifier())) {
			response.addErrorMessage("gene_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			objectIds = geneService.findIdsByIdentifierString(dto.getGeneIdentifier());
			if (objectIds == null || objectIds.size() != 1) {
				response.addErrorMessage("gene_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGeneIdentifier() + ")");
			}
		}

		AlleleGeneAssociation association = null;
		if (subjectIds != null && subjectIds.size() == 1 && objectIds != null && objectIds.size() == 1 && StringUtils.isNotBlank(dto.getRelationName())) {
			HashMap<String, Object> params = new HashMap<>();

			params.put("alleleAssociationSubject.id", subjectIds.get(0));
			params.put("relation.name", dto.getRelationName());
			params.put("alleleGeneAssociationObject.id", objectIds.get(0));

			SearchResponse<AlleleGeneAssociation> searchResponse = alleleGeneAssociationDAO.findByParams(params);
			if (searchResponse != null && searchResponse.getResults().size() == 1) {
				association = searchResponse.getSingleResult();
			}
		}

		if (association == null) {
			association = new AlleleGeneAssociation();
		}

		association = validateAlleleGenomicEntityAssociationDTO(association, dto, VocabularyConstants.ALLELE_GENE_RELATION_VOCABULARY_TERM_SET);
		
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

		if (association.getAlleleGeneAssociationObject() == null && !StringUtils.isBlank(dto.getGeneIdentifier())) {

			Gene object = geneService.findByIdentifierString(dto.getGeneIdentifier());
			if (object == null) {
				response.addErrorMessage("gene_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGeneIdentifier() + ")");
			} else if (beDataProvider != null && !object.getDataProvider().getAbbreviation().equals(beDataProvider.sourceOrganization)) {
				response.addErrorMessage("gene_identifier", ValidationConstants.INVALID_MESSAGE + " for " + beDataProvider.name() + " load (" + dto.getGeneIdentifier() + ")");
			} else {
				association.setAlleleGeneAssociationObject(object);
			}
		}

		if (response.hasErrors()) {
			throw new ObjectValidationException(dto, response.errorMessagesString());
		}

		association = alleleGeneAssociationDAO.persist(association);
		return association;
	}
}
