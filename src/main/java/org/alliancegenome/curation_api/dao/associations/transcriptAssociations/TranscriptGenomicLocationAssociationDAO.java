package org.alliancegenome.curation_api.dao.associations.transcriptAssociations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.transcriptAssociations.TranscriptGenomicLocationAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TranscriptGenomicLocationAssociationDAO extends BaseSQLDAO<TranscriptGenomicLocationAssociation> {

	protected TranscriptGenomicLocationAssociationDAO() {
		super(TranscriptGenomicLocationAssociation.class);
	}

}
