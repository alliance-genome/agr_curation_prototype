package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Transcript;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TranscriptDAO extends BaseSQLDAO<Transcript> {

	protected TranscriptDAO() {
		super(Transcript.class);
	}

}
