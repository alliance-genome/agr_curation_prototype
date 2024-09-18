package org.alliancegenome.curation_api.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.Query;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class GenomicEntityDAO extends BaseSQLDAO<GenomicEntity> {

	protected GenomicEntityDAO() {
		super(GenomicEntity.class);
	}

	public Map<String, Long> getGenomicEntityIdMap() {
		Map<String, Long> genomicEntityIdMap = new HashMap<>();
		Query q = entityManager.createNativeQuery("SELECT a.id, a.modEntityId, a.modInternalId FROM BiologicalEntity as a where exists (select * from genomicentity as g where g.id = a.id)");
		List<Object[]> ids = q.getResultList();
		ids.forEach(record -> {
			if (record[1] != null) {
				genomicEntityIdMap.put((String) record[1], (long) record[0]);
			}
			if (record[2] != null) {
				genomicEntityIdMap.put((String) record[2], (long) record[0]);
			}
		});
		return genomicEntityIdMap;
	}

}
