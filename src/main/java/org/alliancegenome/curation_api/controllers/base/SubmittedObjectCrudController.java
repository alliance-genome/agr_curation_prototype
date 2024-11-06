package org.alliancegenome.curation_api.controllers.base;

import org.alliancegenome.curation_api.dao.base.BaseEntityDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.base.BaseSubmittedObjectCrudInterface;
import org.alliancegenome.curation_api.interfaces.base.BaseUpsertControllerInterface;
import org.alliancegenome.curation_api.model.entities.base.SubmittedObject;
import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;

public abstract class SubmittedObjectCrudController<S extends SubmittedObjectCrudService<E, T, D>, E extends SubmittedObject, T extends BaseDTO, D extends BaseEntityDAO<E>> extends BaseEntityCrudController<S, E, D> implements BaseSubmittedObjectCrudInterface<E>, BaseUpsertControllerInterface<E, T> {

	protected SubmittedObjectCrudService<E, T, D> service;

	@Override
	protected void setService(S service) {
		super.setService(service);
		this.service = service;
	}

	@Override
	public E upsert(T dto) throws ValidationException {
		return service.upsert(dto);
	}

	public E upsert(T dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		return service.upsert(dto, dataProvider);
	}

	@Override
	public ObjectResponse<E> getByIdentifier(String identifierString) {
		return service.getByIdentifier(identifierString);
	}

	@Override
	public ObjectResponse<E> deleteByIdentifier(String identifierString) {
		return service.deleteByIdentifier(identifierString);
	}

}
