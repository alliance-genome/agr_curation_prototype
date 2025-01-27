package org.alliancegenome.curation_api.controllers.crud;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.MoleculeDAO;
import org.alliancegenome.curation_api.interfaces.crud.MoleculeCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.MoleculeExecutor;
import org.alliancegenome.curation_api.model.entities.Molecule;
import org.alliancegenome.curation_api.model.ingest.dto.fms.MoleculeIngestFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.services.MoleculeService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class MoleculeCrudController extends BaseEntityCrudController<MoleculeService, Molecule, MoleculeDAO> implements MoleculeCrudInterface {

	@Inject
	MoleculeService moleculeService;

	@Inject
	MoleculeExecutor moleculeExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(moleculeService);
	}

	public APIResponse updateMolecules(MoleculeIngestFmsDTO moleculeData) {
		return moleculeExecutor.runLoadApi(moleculeService, null, moleculeData.getData());
	}

}
