package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.CodingSequence;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CodingSequenceDAO extends BaseSQLDAO<CodingSequence> {

	protected CodingSequenceDAO() {
		super(CodingSequence.class);
	}

}
