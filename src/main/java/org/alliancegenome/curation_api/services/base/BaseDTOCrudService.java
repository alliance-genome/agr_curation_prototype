package org.alliancegenome.curation_api.services.base;

import org.alliancegenome.curation_api.dao.base.BaseEntityDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

public abstract class BaseDTOCrudService<E extends AuditedObject, T extends BaseDTO, D extends BaseEntityDAO<E>> extends BaseEntityCrudService<E, D> {

	protected abstract void init();

	public abstract E upsert(T dto, BackendBulkDataProvider dataProvider) throws ValidationException;

}
