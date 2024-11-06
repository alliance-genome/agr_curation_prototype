package org.alliancegenome.curation_api.interfaces.crud;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

public interface BaseUpsertServiceInterface<E extends AuditedObject, T extends BaseDTO> {

	default E upsert(T dto) throws ValidationException {
		return upsert(dto, null);
	}
	
	E upsert(T dto, BackendBulkDataProvider dataProvider) throws ValidationException;
	
}
