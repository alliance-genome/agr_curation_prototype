package org.alliancegenome.curation_api.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Reference;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ReferenceDAO extends BaseSQLDAO<Reference> {

	protected ReferenceDAO() {
		super(Reference.class);
	}

	public void updateReferenceForeignKeys(String originalCurie, String newCurie) {
		updateReferenceForeignKey("diseaseannotation", "singlereference_curie", originalCurie, newCurie);
		updateReferenceForeignKey("conditionrelation", "singlereference_curie", originalCurie, newCurie);
		updateReferenceForeignKey("note_reference", "references_curie", originalCurie, newCurie);
		updateReferenceForeignKey("paperhandle", "reference_curie", originalCurie, newCurie);
		updateReferenceForeignKey("allele_reference", "references_curie", originalCurie, newCurie);
		deleteReferenceForeignKey("reference_crossreference", "reference_curie", originalCurie);
	}

	@Transactional
	protected void updateReferenceForeignKey(String table, String column, String originalCurie, String newCurie) {
		Query jpqlQuery = entityManager.createNativeQuery("UPDATE " + table + " SET " + column + " = '" + newCurie + "' WHERE " + column + " = '" + originalCurie + "'");
		jpqlQuery.executeUpdate();
	}

	@Transactional
	protected void deleteReferenceForeignKey(String table, String column, String originalCurie) {
		Query jpqlQuery = entityManager.createNativeQuery("DELETE FROM " + table + " WHERE " + column + " = '" + originalCurie + "'");
		jpqlQuery.executeUpdate();
	}

	public HashMap<String, Reference> getReferenceMap(boolean withCrossReferences) {
		HashMap<String, Reference> referenceIdMap = new HashMap<>();
		Query q = entityManager.createNativeQuery("""
					SELECT ref.id, cr.referencedcurie
					FROM Reference as ref, reference_crossreference as assoc, Crossreference as cr
					where assoc.reference_id = ref.id
					and assoc.crossreferences_id = cr.id
			""");
		List<Object[]> ids = q.getResultList();
		Set<Long> refIDs = ids.stream().map(object -> (Long) object[0]).collect(Collectors.toSet());
		Map<Long, List<Object[]>> idMap = ids.stream().collect(Collectors.groupingBy(o -> (Long) o[0]));
		List<Reference> refs = new ArrayList<>();
		Log.info("Caching: " + refIDs.size() + " references into memory");
		for (Long id: refIDs) {
			Reference reference = getShallowEntity(Reference.class, id);
			if (withCrossReferences) {
				reference.getCrossReferences().size();
			}
			refs.add(reference);
			referenceIdMap.put(String.valueOf(id), reference);
			if (idMap.get(id) != null) {
				idMap.get(id).forEach(objects -> {
					referenceIdMap.put((String) objects[1], reference);
				});
			}
		}
		Log.info("Caching references finished");
		return referenceIdMap;
	}

}
