package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.ExonDAO;
import org.alliancegenome.curation_api.interfaces.crud.ExonCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.gff.Gff3ExonExecutor;
import org.alliancegenome.curation_api.model.entities.Exon;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ExonService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ExonCrudController extends BaseEntityCrudController<ExonService, Exon, ExonDAO> implements ExonCrudInterface {

	@Inject ExonService exonService;
	@Inject Gff3ExonExecutor gff3ExonExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(exonService);
	}

	@Override
	public APIResponse updateExons(String dataProvider, String assembly, List<Gff3DTO> gffData) {
		LoadHistoryResponce resp = (LoadHistoryResponce) gff3ExonExecutor.runLoadApi(dataProvider, assembly, gffData);
		return new LoadHistoryResponce(resp.getHistory());
	}

	@Override
	public ObjectResponse<Exon> getByIdentifier(String identifierString) {
		return exonService.getByIdentifier(identifierString);
	}

	@Override
	public ObjectResponse<Exon> deleteByIdentifier(String identifierString) {
		return exonService.deleteByIdentifier(identifierString);
	}

}
