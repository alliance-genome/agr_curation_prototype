package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.CodingSequenceDAO;
import org.alliancegenome.curation_api.interfaces.crud.CodingSequenceCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.gff.Gff3CDSExecutor;
import org.alliancegenome.curation_api.model.entities.CodingSequence;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.CodingSequenceService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class CodingSequenceCrudController extends BaseEntityCrudController<CodingSequenceService, CodingSequence, CodingSequenceDAO> implements CodingSequenceCrudInterface {

	@Inject CodingSequenceService codingSequenceService;
	@Inject Gff3CDSExecutor gff3CDSExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(codingSequenceService);
	}

	@Override
	public APIResponse updateCodingSequences(String dataProvider, String assembly, List<Gff3DTO> gffData) {
		LoadHistoryResponce resp = (LoadHistoryResponce) gff3CDSExecutor.runLoadApi(dataProvider, assembly, gffData);
		return new LoadHistoryResponce(resp.getHistory());
	}

	@Override
	public ObjectResponse<CodingSequence> getByIdentifier(String identifierString) {
		return codingSequenceService.getByIdentifier(identifierString);
	}

	@Override
	public ObjectResponse<CodingSequence> deleteByIdentifier(String identifierString) {
		return codingSequenceService.deleteByIdentifier(identifierString);
	}

}
