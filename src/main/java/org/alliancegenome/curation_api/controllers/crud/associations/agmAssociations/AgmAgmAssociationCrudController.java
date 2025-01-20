package org.alliancegenome.curation_api.controllers.crud.associations.agmAssociations;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.associations.agmAssociations.AgmAgmAssociationDAO;
import org.alliancegenome.curation_api.interfaces.crud.associations.agmAssociations.AgmAgmAssociationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.associations.agmAssociations.AgmAgmAssociationExecutor;
import org.alliancegenome.curation_api.model.entities.associations.agmAssociations.AgmAgmAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.agmAssociations.AgmAgmAssociationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.associations.agmAssociations.AgmAgmAssociationService;

import java.util.List;

@RequestScoped
public class AgmAgmAssociationCrudController extends
	BaseEntityCrudController<AgmAgmAssociationService, AgmAgmAssociation, AgmAgmAssociationDAO>
	implements AgmAgmAssociationCrudInterface {

	@Inject
	AgmAgmAssociationService agmAgmAssociationService;
	@Inject
	AgmAgmAssociationExecutor agmAgmAssociationExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(agmAgmAssociationService);
	}

	public ObjectResponse<AgmAgmAssociation> getAssociation(Long alleleId, String relationName, Long geneId) {
		return agmAgmAssociationService.getAssociation(alleleId, relationName, geneId);
	}

	@Override
	public APIResponse updateAgmAgmAssociations(String dataProvider, List<AgmAgmAssociationDTO> associationData) {
		return agmAgmAssociationExecutor.runLoadApi(agmAgmAssociationService, dataProvider, associationData);

	}
}
