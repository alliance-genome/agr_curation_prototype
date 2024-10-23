package org.alliancegenome.curation_api.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Gaf;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.ingest.dto.GafDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class GafDAO extends BaseSQLDAO<Gaf> {

	protected GafDAO() {
		super(Gaf.class);
	}

	public Gaf persistGeneGoAssociation(Gaf gaf) {
		String sql = """
			insert into gene_go_annotation (id, gene_id,goterm_id)
			VALUES (nextval('gene_go_annotation_seq'), :geneID, :goID)
						""";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("goID", gaf.getGoTerm().getId());
		query.setParameter("geneID", gaf.getGene().getId());
		query.executeUpdate();

		sql = "select currval('gene_go_annotation_seq')";
		Object object = entityManager.createNativeQuery(sql).getSingleResult();
		gaf.setId((Long) object);
		return gaf;
	}

	public Map<Long, GafDTO> getAllGafIdsPerProvider(Organization sourceOrganization) {
		Query query = entityManager.createNativeQuery("""
			select gga.*, be.modentityid, ot.curie
			from gene_go_annotation as gga , BiologicalEntity as be, ontologyterm as ot,
			species as spec
			where gga.gene_id = be.id
			and be.taxon_id = spec.taxon_id
			and spec.displayname = :speciesName
			and gga.goterm_id = ot.id
			""");
		query.setParameter("speciesName", sourceOrganization.getAbbreviation());
		List<Object[]> result = query.getResultList();
		Map<Long, GafDTO> map = new HashMap<>();
		result.forEach(object -> {
			GafDTO dto = new GafDTO();
			dto.setGeneID((String) object[3]);
			dto.setGoID((String) object[4]);
			map.put((Long) object[0], dto);
		});
		return map;
	}

	public void delete(Long id) {
		String sql = """
			delete from gene_go_annotation where id = :id
						""";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("id", id);
		query.executeUpdate();
	}
}
