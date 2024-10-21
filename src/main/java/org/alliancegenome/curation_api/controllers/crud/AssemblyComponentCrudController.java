package org.alliancegenome.curation_api.controllers.crud;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.AssemblyComponentDAO;
import org.alliancegenome.curation_api.interfaces.crud.AssemblyComponentCrudInterface;
import org.alliancegenome.curation_api.model.entities.AssemblyComponent;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.AssemblyComponentService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AssemblyComponentCrudController extends BaseEntityCrudController<AssemblyComponentService, AssemblyComponent, AssemblyComponentDAO> implements AssemblyComponentCrudInterface {

	@Inject AssemblyComponentService assemblyComponentService;

	@Override
	@PostConstruct
	protected void init() {
		setService(assemblyComponentService);
	}

	@Override
	public ObjectResponse<AssemblyComponent> getByIdentifier(String identifierString) {
		return assemblyComponentService.getByIdentifier(identifierString);
	}

	@Override
	public ObjectResponse<AssemblyComponent> deleteByIdentifier(String identifierString) {
		return assemblyComponentService.deleteByIdentifier(identifierString);
	}

}
