package org.alliancegenome.curation_api.services.validation.dto.fms;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.SequenceTargetingReagentDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.ingest.dto.fms.SequenceTargetingReagentFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.DataProviderService;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.services.SequenceTargetingReagentService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class SequenceTargetingReagentFmsDTOValidator {
	@Inject DataProviderService dataProviderService;

	@Inject GeneService geneService;

	@Inject SequenceTargetingReagentDAO sqtrDAO;

	@Inject NcbiTaxonTermService ncbiTaxonTermService;

	@Inject SequenceTargetingReagentService sqtrService;

	@Inject VocabularyTermService vocabularyTermService;


	public SequenceTargetingReagent validateSQTRFmsDTO(SequenceTargetingReagentFmsDTO dto, BackendBulkDataProvider beDataProvider) throws ValidationException {
		ObjectResponse<SequenceTargetingReagent> sqtrResponse = new ObjectResponse<>();
		
		SequenceTargetingReagent sqtr;
		
		if (StringUtils.isBlank(dto.getPrimaryId())) {
			sqtrResponse.addErrorMessage("primaryId", ValidationConstants.REQUIRED_MESSAGE);
			sqtr = new SequenceTargetingReagent();
		} else {
			SearchResponse<SequenceTargetingReagent> searchResponse = sqtrDAO.findByField("modEntityId", dto.getPrimaryId());
			if (searchResponse == null || searchResponse.getSingleResult() == null) {
				sqtr = new SequenceTargetingReagent();
				sqtr.setModEntityId(dto.getPrimaryId());
			} else {
				sqtr = searchResponse.getSingleResult();
			}
		}


		if (StringUtils.isBlank(dto.getName())) {
			sqtrResponse.addErrorMessage("name", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			sqtr.setName(dto.getName());
		}

		if (StringUtils.isBlank(dto.getTaxonId())) {
			sqtrResponse.addErrorMessage("taxonId", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<NCBITaxonTerm> taxonResponse = ncbiTaxonTermService.getByCurie(dto.getTaxonId());
			if (taxonResponse.getEntity() == null) {
				sqtrResponse.addErrorMessage("taxonId", ValidationConstants.INVALID_MESSAGE + " (" + dto.getTaxonId() + ")");
			}
			if (beDataProvider != null && (beDataProvider.name().equals("RGD") || beDataProvider.name().equals("HUMAN")) && !taxonResponse.getEntity().getCurie().equals(beDataProvider.canonicalTaxonCurie)) {
				sqtrResponse.addErrorMessage("taxonId", ValidationConstants.INVALID_MESSAGE + " (" + dto.getTaxonId() + ") for " + beDataProvider.name() + " load");
			}
			sqtr.setTaxon(taxonResponse.getEntity());
		}
		
		if (CollectionUtils.isNotEmpty(dto.getSynonyms())) {
			sqtr.setSynonyms(dto.getSynonyms());
		} else {
			sqtr.setSynonyms(null);
		}

		if (CollectionUtils.isNotEmpty(dto.getSecondaryIds())) {
			sqtr.setSecondaryIdentifiers(dto.getSecondaryIds());
		} else {
			sqtr.setSecondaryIdentifiers(null);
		}
		
		if (beDataProvider != null) {
			sqtr.setDataProvider(dataProviderService.getDefaultDataProvider(beDataProvider.sourceOrganization));
		}
		
		if (sqtrResponse.hasErrors()) {
			throw new ObjectValidationException(dto, sqtrResponse.errorMessagesString());
		}

		return sqtr;
	}

}
