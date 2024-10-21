package org.alliancegenome.curation_api.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class CrossReferenceDAO extends BaseSQLDAO<CrossReference> {

	protected CrossReferenceDAO() {
		super(CrossReference.class);
	}

	public Map<String, Long> getGenesWithCrossRefs(ResourceDescriptorPage page) {
		String sql = """
			select gc.genomicentity_id, cr.referencedcurie from genomicentity_crossreference as gc, crossreference as cr
			where gc.crossreferences_id = cr.id AND cr.resourcedescriptorpage_id = :pageID
			""";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("pageID", page.getId());
		List<Object[]> objects = query.getResultList();
		Map<String, Long> ensemblGeneMap = new HashMap<>();
		objects.forEach(object -> {
			ensemblGeneMap.put((String) object[1], (Long) object[0]);
		});
		return ensemblGeneMap;
	}

	public Integer persistAccessionGeneAssociated(Long crossReferenceID, Long geneID) {
		String sql = """
			insert into genomicentity_crossreference (crossreferences_id,genomicentity_id)
			VALUES (:crossRef, :geneID)
						""";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("crossRef", crossReferenceID);
		query.setParameter("geneID", geneID);
		int update = query.executeUpdate();
		return update;
	}
}
