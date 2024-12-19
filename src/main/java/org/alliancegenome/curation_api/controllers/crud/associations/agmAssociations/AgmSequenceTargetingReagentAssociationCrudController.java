package org.alliancegenome.curation_api.controllers.crud.associations.agmAssociations;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.associations.agmAssociations.AgmSequenceTargetingReagentAssociationDAO;
import org.alliancegenome.curation_api.interfaces.crud.associations.agmAssociations.AgmSequenceTargetingReagentAssociationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.associations.agmAssociations.AgmStrAssociationExecutor;
import org.alliancegenome.curation_api.model.entities.associations.agmAssociations.AgmSequenceTargetingReagentAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.agmAssociations.AgmSequenceTargetingReagentAssociationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.associations.agmAssociations.AgmStrAssociationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AgmSequenceTargetingReagentAssociationCrudController extends
	BaseEntityCrudController<AgmStrAssociationService, AgmSequenceTargetingReagentAssociation, AgmSequenceTargetingReagentAssociationDAO>
	implements AgmSequenceTargetingReagentAssociationCrudInterface {

	@Inject
	AgmStrAssociationService agmStrAssociationService;
	@Inject
	AgmStrAssociationExecutor agmStrAssociationExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(agmStrAssociationService);
	}

	public ObjectResponse<AgmSequenceTargetingReagentAssociation> getAssociation(Long alleleId, String relationName, Long geneId) {
		return agmStrAssociationService.getAssociation(alleleId, relationName, geneId);
	}

	@Override
	public APIResponse updateAgmStrAssociations(String dataProvider, List<AgmSequenceTargetingReagentAssociationDTO> associationData) {
		return agmStrAssociationExecutor.runLoadApi(agmStrAssociationService, dataProvider, associationData);

	}
}
