package org.alliancegenome.curation_api.services.validation.dto.base;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.base.SubmittedObject;
import org.alliancegenome.curation_api.model.ingest.dto.base.SubmittedObjectDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.UniqueIdentifierHelper;
import org.apache.commons.lang3.tuple.ImmutablePair;

import jakarta.inject.Inject;

public class SubmittedObjectDTOValidator <E extends SubmittedObject, D extends SubmittedObjectDTO> extends AuditedObjectDTOValidator<E, D> {

	@Inject CrossReferenceDAO crossReferenceDAO;

	public E validateSubmittedObjectDTO(E entity, D dto) {

		entity = validateAuditedObjectDTO(entity, dto);
		
		UniqueIdentifierHelper.setSubmittedObjectIdentifiers(dto, entity, null);
		
		if (dto.getDataProviderDto() == null) {
			response.addErrorMessage("data_provider_dto", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<ImmutablePair<Organization, CrossReference>> dpResponse = validateDataProviderDTO(dto.getDataProviderDto(), entity.getDataProviderCrossReference());
			if (dpResponse.hasErrors()) {
				response.addErrorMessage("data_provider_dto", dpResponse.errorMessagesString());
			} else {
				entity.setDataProvider(dpResponse.getEntity().getLeft());
				if (dpResponse.getEntity().getRight() != null) {
					entity.setDataProviderCrossReference(crossReferenceDAO.persist(dpResponse.getEntity().getRight()));
				} else {
					entity.setDataProviderCrossReference(null);
				}
			}
		}

		return entity;
	}

}
