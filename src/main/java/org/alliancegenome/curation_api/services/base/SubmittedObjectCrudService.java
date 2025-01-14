package org.alliancegenome.curation_api.services.base;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.dao.base.BaseEntityDAO;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.base.SubmittedObject;
import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.transaction.Transactional;

public abstract class SubmittedObjectCrudService<E extends SubmittedObject, T extends BaseDTO, D extends BaseEntityDAO<E>> extends BaseEntityCrudService<E, D> implements BaseUpsertServiceInterface<E, T> {

	public ObjectResponse<E> getByIdentifier(String identifier) {
		E object = findByIdentifierString(identifier);
		ObjectResponse<E> ret = new ObjectResponse<>(object);
		return ret;
	}
	
	public List<Long> getIdsByIdentifier(String identifier) {
		return findIdsByIdentifierString(identifier);
	}

	@Transactional
	public ObjectResponse<E> deleteByIdentifier(String identifierString) {
		E object = findByIdentifierString(identifierString);
		if (object != null) {
			dao.remove(object.getId());
		}
		ObjectResponse<E> ret = new ObjectResponse<>(object);
		return ret;
	}

	public E findByIdentifierString(String id) {
		if (id != null && id.startsWith("AGRKB:")) {
			return findByCurie(id);
		}

		return findByAlternativeFields(List.of("primaryExternalId", "modInternalId"), id);
	}
	
	public List<Long> findIdsByIdentifierString(String id) {
		if (id != null && id.startsWith("AGRKB:")) {
			E object = findByCurie(id);
			ArrayList<Long> ids = new ArrayList<>();
			ids.add(object.getId());
			return ids;
		}

		return findIdsByAlternativeFields(List.of("primaryExternalId", "modInternalId"), id);
	}
	
	
}

