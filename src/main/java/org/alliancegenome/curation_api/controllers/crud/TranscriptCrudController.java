package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.TranscriptDAO;
import org.alliancegenome.curation_api.interfaces.crud.TranscriptCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.gff.Gff3TranscriptExecutor;
import org.alliancegenome.curation_api.model.entities.Transcript;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.TranscriptService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class TranscriptCrudController extends BaseEntityCrudController<TranscriptService, Transcript, TranscriptDAO> implements TranscriptCrudInterface {

	@Inject TranscriptService transcriptService;

	@Inject Gff3TranscriptExecutor gff3TranscriptExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(transcriptService);
	}

	@Override
	public APIResponse updateTranscripts(String dataProvider, String assembly, List<Gff3DTO> gffData) {
		LoadHistoryResponce resp = (LoadHistoryResponce) gff3TranscriptExecutor.runLoadApi(dataProvider, assembly, gffData);
		return new LoadHistoryResponce(resp.getHistory());
	}

	@Override
	public ObjectResponse<Transcript> getByIdentifier(String identifierString) {
		return transcriptService.getByIdentifier(identifierString);
	}

	@Override
	public ObjectResponse<Transcript> deleteByIdentifier(String identifierString) {
		return transcriptService.deleteByIdentifier(identifierString);
	}

}
