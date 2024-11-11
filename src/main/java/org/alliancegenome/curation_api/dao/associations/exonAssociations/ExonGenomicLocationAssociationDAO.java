package org.alliancegenome.curation_api.dao.associations.exonAssociations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.exonAssociations.ExonGenomicLocationAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExonGenomicLocationAssociationDAO extends BaseSQLDAO<ExonGenomicLocationAssociation> {

	protected ExonGenomicLocationAssociationDAO() {
		super(ExonGenomicLocationAssociation.class);
	}

}
