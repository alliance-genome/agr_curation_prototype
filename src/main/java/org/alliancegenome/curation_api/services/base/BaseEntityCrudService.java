package org.alliancegenome.curation_api.services.base;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.auth.AuthenticatedUser;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.dao.base.BaseEntityDAO;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.PersonService;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

public abstract class BaseEntityCrudService<E extends AuditedObject, D extends BaseEntityDAO<E>> {

	protected BaseSQLDAO<E> dao;

	protected void setSQLDao(BaseSQLDAO<E> dao) {
		this.dao = dao;
	}

	@Inject
	@AuthenticatedUser protected Person authenticatedPerson;
	@Inject PersonService personService;
	@Inject PersonDAO personDAO;
	
	protected abstract void init();

	@Transactional
	public ObjectResponse<E> create(E entity) {
		// log.info("Authed Person: " + authenticatedPerson);
		E object = dao.persist(entity);
		ObjectResponse<E> ret = new ObjectResponse<E>(object);
		return ret;
	}

	@Transactional
	public ObjectListResponse<E> create(List<E> entities) {
		// log.info("Authed Person: " + authenticatedPerson);
		List<E> objects = dao.persist(entities);
		ObjectListResponse<E> ret = new ObjectListResponse<E>(objects);
		return ret;
	}

	public ObjectResponse<E> getById(Long id) {
		E object = dao.find(id);
		ObjectResponse<E> ret = new ObjectResponse<E>(object);
		return ret;
	}

	public ObjectResponse<E> getByCurie(String curie) {
		E object = findByCurie(curie);
		ObjectResponse<E> ret = new ObjectResponse<E>(object);
		return ret;
	}

	public ObjectResponse<E> getByIdentifier(String identifier) {
		E object = findByAlternativeFields(List.of("curie", "primaryExternalId", "modInternalId"), identifier);
		ObjectResponse<E> ret = new ObjectResponse<E>(object);
		return ret;
	}

	public E findByCurie(String curie) {
		if (curie != null) {
			SearchResponse<E> response = findByField("curie", curie);
			if (response == null || response.getSingleResult() == null) {
				Log.debug("Entity Not Found: " + curie);
				return null;
			}
			E entity = response.getSingleResult();
			Log.debug("Entity Found: " + entity);
			return entity;
		} else {
			Log.debug("Input Param is null: " + curie);
			return null;
		}
	}

	public E findByAlternativeFields(List<String> fields, String value) {
		if (value != null) {
			SearchResponse<E> response = dao.findByFields(fields, value);
			if (response == null) {
				Log.debug("Entity Not Found: " + value);
				return null;
			}
			E entity = response.getSingleResult();
			return entity;
		} else {
			Log.debug("Input Param is null: " + value);
			return null;
		}
	}

	public List<Long> findIdsByAlternativeFields(List<String> fields, String value) {
		if (value != null) {
			List<Long> response = dao.findIdsByFields(fields, value);
			if (response == null) {
				Log.debug("Entity Not Found: " + value);
				return null;
			}
			return response;
		} else {
			Log.debug("Input Param is null: " + value);
			return null;
		}
	}

	@Transactional
	public ObjectResponse<E> update(E entity) {
		// log.info("Authed Person: " + authenticatedPerson);
		E object = dao.merge(entity);
		ObjectResponse<E> ret = new ObjectResponse<E>(object);
		return ret;
	}

	@Transactional
	public ObjectResponse<E> deleteByCurie(String curie) {
		E object = findByCurie(curie);
		if (object != null) {
			dao.remove(object.getId());
		}
		ObjectResponse<E> ret = new ObjectResponse<>(object);
		return ret;
	}

	@Transactional
	public ObjectResponse<E> deleteById(Long id) {
		E object = dao.remove(id);
		ObjectResponse<E> ret = new ObjectResponse<>(object);
		return ret;
	}
	
	@Transactional
	public E deprecateOrDelete(Long id, Boolean throwApiError, String requestSource, Boolean deprecate) {
		E object = dao.find(id);
		
		if (object == null) {
			String errorMessage = "Could not find entity with id: " + id;
			if (throwApiError) {
				ObjectResponse<E> response = new ObjectResponse<>();
				response.addErrorMessage("id", errorMessage);
				throw new ApiErrorException(response);
			}
			Log.error(errorMessage);
			return null;
		}

		if (deprecate) {
			if (!object.getObsolete()) {
				object.setObsolete(true);
				if (authenticatedPerson.getUniqueId() != null) {
					requestSource = authenticatedPerson.getUniqueId();
				}
				Person updatedBy = personService.fetchByUniqueIdOrCreate(requestSource);
				object.setUpdatedBy(updatedBy);
				object.setDateUpdated(OffsetDateTime.now());
				return dao.persist(object);
			} else {
				return object;
			}
		} else {
			dao.remove(id);
		}

		return null;
	}

	public SearchResponse<E> findByField(String field, String value) {
		return dao.findByField(field, value);
	}

	public SearchResponse<E> findByParams(Pagination pagination, Map<String, Object> params) {
		return dao.findByParams(pagination, params);
	}

	public SearchResponse<E> searchByParams(Pagination pagination, Map<String, Object> params) {
		return dao.searchByParams(pagination, params);
	}

	public void reindex() {
		dao.reindex();
	}

	public void reindex(Integer batchSizeToLoadObjects, Integer idFetchSize, Integer limitIndexedObjectsTo, Integer threadsToLoadObjects, Integer transactionTimeout, Integer typesToIndexInParallel) {
		dao.reindex(batchSizeToLoadObjects, idFetchSize, limitIndexedObjectsTo, threadsToLoadObjects, transactionTimeout, typesToIndexInParallel);
	}

	public void reindexEverything(Integer batchSizeToLoadObjects, Integer idFetchSize, Integer limitIndexedObjectsTo, Integer threadsToLoadObjects, Integer transactionTimeout, Integer typesToIndexInParallel) {
		dao.reindexEverything(batchSizeToLoadObjects, idFetchSize, limitIndexedObjectsTo, threadsToLoadObjects, transactionTimeout, typesToIndexInParallel);
	}

}
