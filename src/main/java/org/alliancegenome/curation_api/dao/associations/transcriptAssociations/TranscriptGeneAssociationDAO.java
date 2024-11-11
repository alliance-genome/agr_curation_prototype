package org.alliancegenome.curation_api.dao.associations.transcriptAssociations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.transcriptAssociations.TranscriptGeneAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TranscriptGeneAssociationDAO extends BaseSQLDAO<TranscriptGeneAssociation> {

	protected TranscriptGeneAssociationDAO() {
		super(TranscriptGeneAssociation.class);
	}

}
