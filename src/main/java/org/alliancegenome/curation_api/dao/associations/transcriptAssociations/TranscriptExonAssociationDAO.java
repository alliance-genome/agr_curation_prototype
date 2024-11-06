package org.alliancegenome.curation_api.dao.associations.transcriptAssociations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.transcriptAssociations.TranscriptExonAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TranscriptExonAssociationDAO extends BaseSQLDAO<TranscriptExonAssociation> {

	protected TranscriptExonAssociationDAO() {
		super(TranscriptExonAssociation.class);
	}

}
