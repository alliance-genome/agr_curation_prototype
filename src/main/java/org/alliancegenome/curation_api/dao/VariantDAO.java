package org.alliancegenome.curation_api.dao;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.Query;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Variant;

@ApplicationScoped
public class VariantDAO extends BaseSQLDAO<Variant> {
	
	@Inject
	NoteDAO noteDAO;

	protected VariantDAO() {
		super(Variant.class);
	}

	public List<String> findAllCuriesByDataProvider(String dataProvider) {
		Query jpqlQuery = entityManager.createQuery("SELECT variant.curie FROM Variant variant WHERE variant.dataProvider.sourceOrganization.abbreviation = :dataProvider");
		jpqlQuery.setParameter("dataProvider", dataProvider);
		return (List<String>) jpqlQuery.getResultList();
	}

	public void deleteAttachedNote(Long id) {
		Query jpqlQuery = entityManager.createNativeQuery("DELETE FROM variant_note WHERE relatednotes_id = '" + id + "'");
		jpqlQuery.executeUpdate();

		noteDAO.remove(id);
	}

}
