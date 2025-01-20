package org.alliancegenome.curation_api.services.validation.dto.associations;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.EvidenceAssociation;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.ingest.dto.associations.EvidenceAssociationDTO;
import org.alliancegenome.curation_api.services.InformationContentEntityService;
import org.alliancegenome.curation_api.services.validation.dto.base.AuditedObjectDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.inject.Inject;

public class EvidenceAssociationDTOValidator <E extends EvidenceAssociation, D extends EvidenceAssociationDTO> extends AuditedObjectDTOValidator<E, D> {

	@Inject
	InformationContentEntityService informationContentEntityService;

	public E validateEvidenceAssociationDTO(E association, D dto) {
		association = validateAuditedObjectDTO(association, dto);
		
		if (CollectionUtils.isNotEmpty(dto.getEvidenceCuries())) {
			List<InformationContentEntity> evidence = new ArrayList<>();
			for (String evidenceCurie : dto.getEvidenceCuries()) {
				InformationContentEntity evidenceEntity = informationContentEntityService.retrieveFromDbOrLiteratureService(evidenceCurie);
				if (evidenceEntity == null) {
					response.addErrorMessage("evidence_curies", ValidationConstants.INVALID_MESSAGE + " (" + evidenceCurie + ")");
					break;
				}
				evidence.add(evidenceEntity);
			}
			association.setEvidence(evidence);
		} else {
			association.setEvidence(null);
		}

		return association;
	}
}
