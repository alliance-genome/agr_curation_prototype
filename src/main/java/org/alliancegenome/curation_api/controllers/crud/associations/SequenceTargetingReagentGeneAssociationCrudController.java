package org.alliancegenome.curation_api.controllers.crud.associations;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.associations.SequenceTargetingReagentGeneAssociationDAO;
import org.alliancegenome.curation_api.interfaces.crud.associations.SequenceTargetingReagentGeneAssociationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.SequenceTargetingReagentExecutor;
import org.alliancegenome.curation_api.model.entities.associations.sequenceTargetingReagentAssociations.SequenceTargetingReagentGeneAssociation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.associations.SequenceTargetingReagentGeneAssociationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class SequenceTargetingReagentGeneAssociationCrudController extends
	BaseEntityCrudController<SequenceTargetingReagentGeneAssociationService, SequenceTargetingReagentGeneAssociation, SequenceTargetingReagentGeneAssociationDAO> implements SequenceTargetingReagentGeneAssociationCrudInterface {

	@Inject SequenceTargetingReagentExecutor sqtrExecutor;
	@Inject SequenceTargetingReagentGeneAssociationService sequenceTargetingReagentGeneAssociationService;

	@Override
	@PostConstruct
	protected void init() {
		setService(sequenceTargetingReagentGeneAssociationService);
	}

	@Override
	public ObjectResponse<SequenceTargetingReagentGeneAssociation> getAssociation(Long sqtrId, String relationName, Long geneId) {
		return sequenceTargetingReagentGeneAssociationService.getAssociation(sqtrId, relationName, geneId);
	}
}
