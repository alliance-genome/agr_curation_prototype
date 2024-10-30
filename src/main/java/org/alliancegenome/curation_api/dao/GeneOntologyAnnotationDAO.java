package org.alliancegenome.curation_api.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.GeneOntologyAnnotation;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.ingest.dto.GeneOntologyAnnotationDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class GeneOntologyAnnotationDAO extends BaseSQLDAO<GeneOntologyAnnotation> {

	protected GeneOntologyAnnotationDAO() {
		super(GeneOntologyAnnotation.class);
	}

	public GeneOntologyAnnotation persistGeneGoAssociation(GeneOntologyAnnotation gaf) {
		String sql = """
			insert into GeneOntologyAnnotation (id, singlegene_id,goterm_id)
			VALUES (nextval('gene_go_annotation_seq'), :geneID, :goID)
						""";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("goID", gaf.getGoTerm().getId());
		query.setParameter("geneID", gaf.getSingleGene().getId());
		query.executeUpdate();

		sql = "select currval('gene_go_annotation_seq')";
		Object object = entityManager.createNativeQuery(sql).getSingleResult();
		gaf.setId((Long) object);
		return gaf;
	}

	public Map<Long, GeneOntologyAnnotationDTO> getAllGafIdsPerProvider(Organization sourceOrganization) {
		Query query = entityManager.createNativeQuery("""
			select gga.*, be.modentityid, ot.curie
			from GeneOntologyAnnotation as gga , BiologicalEntity as be, ontologyterm as ot,
			species as spec
			where gga.singlegene_id = be.id
			and be.taxon_id = spec.taxon_id
			and spec.displayname = :speciesName
			and gga.goterm_id = ot.id
			""");
		query.setParameter("speciesName", sourceOrganization.getAbbreviation());
		List<Object[]> result = query.getResultList();
		Map<Long, GeneOntologyAnnotationDTO> map = new HashMap<>();
		result.forEach(object -> {
			GeneOntologyAnnotationDTO dto = new GeneOntologyAnnotationDTO();
			dto.setGeneIdentifier((String) object[3]);
			dto.setGoTermCurie((String) object[4]);
			map.put((Long) object[0], dto);
		});
		return map;
	}

	public void delete(Long id) {
		String sql = """
			delete from GeneOntologyAnnotation where id = :id
						""";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("id", id);
		query.executeUpdate();
	}
}
