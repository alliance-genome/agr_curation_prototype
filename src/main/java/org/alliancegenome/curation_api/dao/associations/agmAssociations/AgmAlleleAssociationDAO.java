package org.alliancegenome.curation_api.dao.associations.agmAssociations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.agmAssociations.AgmAlleleAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AgmAlleleAssociationDAO extends BaseSQLDAO<AgmAlleleAssociation> {
	
	protected AgmAlleleAssociationDAO() {
		super(AgmAlleleAssociation.class);
	}
	
}
