package org.alliancegenome.curation_api.controllers.base;

import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.dao.base.BaseEntityDAO;
import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

public abstract class BaseEntityCrudController<S extends BaseEntityCrudService<E, D>, E extends AuditedObject, D extends BaseEntityDAO<E>> implements BaseIdCrudInterface<E> {

	protected BaseEntityCrudService<E, D> service;

	protected void setService(S service) {
		this.service = service;
	}

	protected abstract void init();

	@Override
	public ObjectResponse<E> create(E entity) {
		return service.create(entity);
	}

	@Override
	public ObjectListResponse<E> create(List<E> entities) {
		return service.create(entities);
	}

	@Override
	public ObjectResponse<E> getById(Long id) {
		return service.getById(id);
	}

	public ObjectResponse<E> getByCurie(String curie) {
		return service.getByCurie(curie);
	}

	@Override
	public ObjectResponse<E> update(E entity) {
		return service.update(entity);
	}

	public ObjectResponse<E> deleteByCurie(String curie) {
		return service.deleteByCurie(curie);
	}

	@Override
	public ObjectResponse<E> deleteById(Long id) {
		return service.deleteById(id);
	}

	public SearchResponse<E> findByField(String field, String value) {
		return service.findByField(field, value);
	}

	@Override
	public SearchResponse<E> find(Integer page, Integer limit, HashMap<String, Object> params) {
		if (params == null) {
			params = new HashMap<>();
		}
		Pagination pagination = new Pagination(page, limit);
		return service.findByParams(pagination, params);
	}

	@Override
	public SearchResponse<E> findForPublic(Integer page, Integer limit, HashMap<String, Object> params) {
		return find(page, limit, params);
	}

	@Override
	public SearchResponse<E> search(Integer page, Integer limit, HashMap<String, Object> params) {
		if (params == null) {
			params = new HashMap<>();
		}
		Pagination pagination = new Pagination(page, limit);
		return service.searchByParams(pagination, params);
	}

	@Override
	public void reindex(Integer batchSizeToLoadObjects, Integer idFetchSize, Integer limitIndexedObjectsTo, Integer threadsToLoadObjects, Integer transactionTimeout, Integer typesToIndexInParallel) {
		service.reindex(batchSizeToLoadObjects, idFetchSize, limitIndexedObjectsTo, threadsToLoadObjects, transactionTimeout, typesToIndexInParallel);
	}

	public void reindexEverything(Integer batchSizeToLoadObjects, Integer idFetchSize, Integer limitIndexedObjectsTo, Integer threadsToLoadObjects, Integer transactionTimeout, Integer typesToIndexInParallel) {
		service.reindexEverything(batchSizeToLoadObjects, idFetchSize, limitIndexedObjectsTo, threadsToLoadObjects, transactionTimeout, typesToIndexInParallel);
	}

}
