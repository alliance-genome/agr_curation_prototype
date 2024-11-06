package org.alliancegenome.curation_api.controllers.crud;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.GenomeAssemblyDAO;
import org.alliancegenome.curation_api.interfaces.crud.GenomeAssemblyCrudInterface;
import org.alliancegenome.curation_api.model.entities.GenomeAssembly;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.GenomeAssemblyService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GenomeAssemblyCrudController extends BaseEntityCrudController<GenomeAssemblyService, GenomeAssembly, GenomeAssemblyDAO> implements GenomeAssemblyCrudInterface {

	@Inject GenomeAssemblyService genomeAssemblyService;

	@Override
	@PostConstruct
	protected void init() {
		setService(genomeAssemblyService);
	}

	@Override
	public ObjectResponse<GenomeAssembly> getByIdentifier(String identifierString) {
		return genomeAssemblyService.getByIdentifier(identifierString);
	}

	@Override
	public ObjectResponse<GenomeAssembly> deleteByIdentifier(String identifierString) {
		return genomeAssemblyService.deleteByIdentifier(identifierString);
	}

}
