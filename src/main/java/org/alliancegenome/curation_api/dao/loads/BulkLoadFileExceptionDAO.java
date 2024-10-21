package org.alliancegenome.curation_api.dao.loads;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileException;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class BulkLoadFileExceptionDAO extends BaseSQLDAO<BulkLoadFileException> {
	protected BulkLoadFileExceptionDAO() {
		super(BulkLoadFileException.class);
	}

	@Transactional
	public void cleanUpTwoWeekOldExceptions() {
		Log.info("Deleting Old Bulk Exceptions: \"DELETE FROM BulkLoadFileException WHERE dbdatecreated < NOW() - INTERVAL '14 days'\"");
		Query jpqlQuery = entityManager.createNativeQuery("DELETE FROM BulkLoadFileException WHERE dbdatecreated < NOW() - INTERVAL '14 days'");
		jpqlQuery.executeUpdate();
	}
}