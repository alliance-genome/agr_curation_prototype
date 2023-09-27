package org.alliancegenome.curation_api.services.validation.dto.associations;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.model.entities.EvidenceAssociation;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.ingest.dto.associations.EvidenceAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.apache.commons.collections.CollectionUtils;

@RequestScoped
public class EvidenceAssociationDTOValidator extends BaseDTOValidator {

	@Inject
	NoteDAO noteDAO;
	

	public <E extends EvidenceAssociation, D extends EvidenceAssociationDTO> ObjectResponse<E> validateEvidenceAssociationDTO(E association, D dto) {
		ObjectResponse<E> assocResponse = validateAuditedObjectDTO(association, dto);
		association = assocResponse.getEntity();

		if (CollectionUtils.isNotEmpty(dto.getEvidenceCuries())) {
			List<InformationContentEntity> evidence = new ArrayList<>();
			// TODO: validate InformationContentEntity stuff here
		} else {
			association.setEvidence(null);
		}

		assocResponse.setEntity(association);
		
		return assocResponse;
	}
}