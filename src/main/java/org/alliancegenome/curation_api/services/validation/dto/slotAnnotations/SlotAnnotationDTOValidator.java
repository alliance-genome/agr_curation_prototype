package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.SlotAnnotationDTO;
import org.alliancegenome.curation_api.services.InformationContentEntityService;
import org.alliancegenome.curation_api.services.validation.dto.base.AuditedObjectDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.inject.Inject;

public class SlotAnnotationDTOValidator<E extends SlotAnnotation, D extends SlotAnnotationDTO> extends AuditedObjectDTOValidator<E, D> {

	@Inject InformationContentEntityService informationContentEntityService;

	public E validateSlotAnnotationDTO(E annotation, D dto) {
		annotation = validateAuditedObjectDTO(annotation, dto);
		
		List<InformationContentEntity> evidence = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dto.getEvidenceCuries())) {
			for (String evidenceCurie : dto.getEvidenceCuries()) {
				InformationContentEntity evidenceEntity = informationContentEntityService.retrieveFromDbOrLiteratureService(evidenceCurie);
				if (evidenceEntity == null) {
					response.addErrorMessage("evidence_curies", ValidationConstants.INVALID_MESSAGE + " (" + evidenceCurie + ")");
					break;
				}
				evidence.add(evidenceEntity);
			}
			annotation.setEvidence(evidence);
		} else {
			annotation.setEvidence(null);
		}

		return annotation;
	}
}
