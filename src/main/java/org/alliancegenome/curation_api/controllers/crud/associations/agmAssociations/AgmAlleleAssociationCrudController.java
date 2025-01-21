package org.alliancegenome.curation_api.controllers.crud.associations.agmAssociations;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.interfaces.crud.associations.agmAssociations.AgmAlleleAssociationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.associations.agmAssociations.AgmAlleleAssociationExecutor;
import org.alliancegenome.curation_api.model.entities.associations.agmAssociations.AgmAlleleAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.agmAssociations.AgmAlleleAssociationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.associations.agmAssociations.AgmAlleleAssociationService;
import org.alliancegenome.curation_api.dao.associations.agmAssociations.AgmAlleleAssociationDAO;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AgmAlleleAssociationCrudController extends BaseEntityCrudController<AgmAlleleAssociationService, AgmAlleleAssociation, AgmAlleleAssociationDAO>
	implements AgmAlleleAssociationCrudInterface {

	@Inject
	AgmAlleleAssociationService agmAlleleAssociationService;
	@Inject
	AgmAlleleAssociationExecutor agmAlleleAssociationExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(agmAlleleAssociationService);
	}

	public ObjectResponse<AgmAlleleAssociation> getAssociation(Long agmId, String relationName, Long alleleId) {
		return agmAlleleAssociationService.getAssociation(agmId, relationName, alleleId);
	}

	@Override
	public APIResponse updateAgmAlleleAssociations(String dataProvider, List<AgmAlleleAssociationDTO> associationData) {
		return agmAlleleAssociationExecutor.runLoadApi(agmAlleleAssociationService, dataProvider, associationData);

	}
}
