package org.alliancegenome.curation_api.services.validation.dto.associations;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.EvidenceAssociation;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.ingest.dto.associations.EvidenceAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.InformationContentEntityService;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@RequestScoped
public class EvidenceAssociationDTOValidator extends BaseDTOValidator {

	@Inject
	InformationContentEntityService informationContentEntityService;

	public <E extends EvidenceAssociation, D extends EvidenceAssociationDTO> ObjectResponse<E> validateEvidenceAssociationDTO(E association, D dto) {
		ObjectResponse<E> assocResponse = validateAuditedObjectDTO(association, dto);
		association = assocResponse.getEntity();

		if (CollectionUtils.isNotEmpty(dto.getEvidenceCuries())) {
			List<InformationContentEntity> evidence = new ArrayList<>();
			for (String evidenceCurie : dto.getEvidenceCuries()) {
				InformationContentEntity evidenceEntity = informationContentEntityService.retrieveFromDbOrLiteratureService(evidenceCurie);
				if (evidenceEntity == null) {
					assocResponse.addErrorMessage("evidence_curies", ValidationConstants.INVALID_MESSAGE + " (" + evidenceCurie + ")");
					break;
				}
				evidence.add(evidenceEntity);
			}
			association.setEvidence(evidence);
		} else {
			association.setEvidence(null);
		}

		assocResponse.setEntity(association);

		return assocResponse;
	}
}
