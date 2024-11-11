package org.alliancegenome.curation_api.dao.associations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.sequenceTargetingReagentAssociations.SequenceTargetingReagentGeneAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SequenceTargetingReagentGeneAssociationDAO extends BaseSQLDAO<SequenceTargetingReagentGeneAssociation> {

	protected SequenceTargetingReagentGeneAssociationDAO() {
		super(SequenceTargetingReagentGeneAssociation.class);
	}

}
