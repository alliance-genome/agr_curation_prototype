package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.FBCVTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FbcvTermDAO extends BaseSQLDAO<FBCVTerm> {

	protected FbcvTermDAO() {
		super(FBCVTerm.class);
	}

}
