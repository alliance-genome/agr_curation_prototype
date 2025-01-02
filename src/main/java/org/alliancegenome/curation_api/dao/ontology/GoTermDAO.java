package org.alliancegenome.curation_api.dao.ontology;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class GoTermDAO extends BaseSQLDAO<GOTerm> {

	protected GoTermDAO() {
		super(GOTerm.class);
	}

	public Map<String, Long> getAllGOIds() {
		String sql = """
						select id, curie
						from ontologyterm
						where ontologytermtype = :type
			""";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("type", "GOTerm");
		List<Object[]> objects = query.getResultList();
		Map<String, Long> ensemblGeneMap = new HashMap<>();
		objects.forEach(object -> {
			ensemblGeneMap.put((String) object[1], (Long) object[0]);
		});
		return ensemblGeneMap;
	}
}
