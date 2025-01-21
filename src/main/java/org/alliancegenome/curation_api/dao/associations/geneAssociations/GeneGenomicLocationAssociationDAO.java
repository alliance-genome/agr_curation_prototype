package org.alliancegenome.curation_api.dao.associations.geneAssociations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.geneAssociations.GeneGenomicLocationAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GeneGenomicLocationAssociationDAO extends BaseSQLDAO<GeneGenomicLocationAssociation> {

	protected GeneGenomicLocationAssociationDAO() {
		super(GeneGenomicLocationAssociation.class);
	}

}
