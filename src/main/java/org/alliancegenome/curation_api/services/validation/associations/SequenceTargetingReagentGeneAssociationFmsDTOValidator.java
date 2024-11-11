package org.alliancegenome.curation_api.services.validation.associations;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.SequenceTargetingReagentDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.sequenceTargetingReagentAssociations.SequenceTargetingReagentGeneAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.SequenceTargetingReagentFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.services.VocabularyTermService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class SequenceTargetingReagentGeneAssociationFmsDTOValidator {
	@Inject
	SequenceTargetingReagentDAO sqtrDAO;
	@Inject
	VocabularyTermService vocabularyTermService;
	@Inject
	GeneService geneService;

	public List<SequenceTargetingReagentGeneAssociation> validateSQTRGeneAssociationFmsDTO(SequenceTargetingReagentFmsDTO dto, BackendBulkDataProvider beDataProvider) throws ObjectValidationException {
		List<SequenceTargetingReagentGeneAssociation> strGeneAssociations = new ArrayList<>();
		ObjectResponse<SequenceTargetingReagent> sqtrResponse = new ObjectResponse<>();

		SequenceTargetingReagent sqtr;
		SearchResponse<SequenceTargetingReagent> sqtrSearchResponse = sqtrDAO.findByField("modEntityId",
				dto.getPrimaryId());

		if (sqtrSearchResponse == null || sqtrSearchResponse.getSingleResult() == null) {
			sqtrResponse.addErrorMessage("modEntityId",
					ValidationConstants.INVALID_MESSAGE + " (" + dto.getPrimaryId() + ")");
			sqtr = new SequenceTargetingReagent();
		} else {
			sqtr = sqtrSearchResponse.getSingleResult();
		}

		VocabularyTerm relation;
		SearchResponse<VocabularyTerm> relationSearchResponse = vocabularyTermService.findByField("name", "targets");
		if (relationSearchResponse == null || relationSearchResponse.getSingleResult() == null) {
			sqtrResponse.addErrorMessage("relation", ValidationConstants.INVALID_MESSAGE + " (" + "targets" + ")");
			relation = new VocabularyTerm();
		} else {
			relation = relationSearchResponse.getSingleResult();
		}

		if (dto.getTargetGeneIds() != null) {
			for (String geneId : dto.getTargetGeneIds()) {
				Gene gene = geneService.findByIdentifierString(geneId);

				if (gene == null) {
					sqtrResponse.addErrorMessage("targetGeneIds",
							ValidationConstants.INVALID_MESSAGE + " (" + geneId + ")");
				} else {
					SequenceTargetingReagentGeneAssociation strGeneAssociation = new SequenceTargetingReagentGeneAssociation();
					strGeneAssociation.setSequenceTargetingReagentAssociationSubject(sqtr);
					strGeneAssociation.setRelation(relation);
					strGeneAssociation.setSequenceTargetingReagentGeneAssociationObject(gene);

					strGeneAssociations.add(strGeneAssociation);
				}

			}
		}

		if (sqtrResponse.hasErrors()) {
			throw new ObjectValidationException(dto, sqtrResponse.errorMessagesString());
		}

		return strGeneAssociations;
	}
}
