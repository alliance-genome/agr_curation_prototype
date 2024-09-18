package org.alliancegenome.curation_api.services;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.alliancegenome.curation_api.dao.GenomicEntityDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.ingest.dto.GenomicEntityDTO;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;

import java.util.HashMap;
import java.util.Map;

@RequestScoped
public class GenomicEntityService extends SubmittedObjectCrudService<GenomicEntity, GenomicEntityDTO, GenomicEntityDAO> {

	@Inject
	GenomicEntityDAO genomicEntityDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(genomicEntityDAO);
	}

	@Override
	public GenomicEntity upsert(GenomicEntityDTO dto) throws ValidationException {
		return null;
	}

	@Override
	public GenomicEntity upsert(GenomicEntityDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		return null;
	}

	@Override
	public GenomicEntity deprecateOrDelete(Long id, Boolean throwApiError, String requestSource, Boolean deprecate) {
		return null;
	}

	public Map<String, Long> getGenomicEntityIdMap() {
		if (genomicEntityIdMap.size() > 0) {
			return genomicEntityIdMap;
		}
		genomicEntityIdMap = genomicEntityDAO.getGenomicEntityIdMap();
		return genomicEntityIdMap;
	}

	private Map<String, Long> genomicEntityIdMap = new HashMap<>();

	public Long getIdByModID(String modID) {
		return getGenomicEntityIdMap().get(modID);
	}

	public GenomicEntity getShallowEntity(Long id) {
		return genomicEntityDAO.getShallowEntity(GenomicEntity.class, id);
	}
}
