package org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations;

import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.Query;

@ApplicationScoped
public class ConstructComponentSlotAnnotationDAO extends BaseSQLDAO<ConstructComponentSlotAnnotation> {

	@Inject
	NoteDAO noteDAO;
	
	protected ConstructComponentSlotAnnotationDAO() {
		super(ConstructComponentSlotAnnotation.class);
	}

	public void deleteAttachedNote(Long id) {
		Query jpqlQuery = entityManager.createNativeQuery("DELETE FROM slotannotation_note WHERE relatednotes_id = '" + id + "'");
		jpqlQuery.executeUpdate();

		noteDAO.remove(id);
	}
}
