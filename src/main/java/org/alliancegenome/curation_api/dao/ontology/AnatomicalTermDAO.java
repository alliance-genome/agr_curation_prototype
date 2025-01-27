package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.AnatomicalTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AnatomicalTermDAO extends BaseSQLDAO<AnatomicalTerm> {

	protected AnatomicalTermDAO() {
		super(AnatomicalTerm.class);
	}

}