package org.alliancegenome.curation_api.controllers.crud.geneAssociations;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.associations.geneAssociations.GeneGenomicLocationAssociationDAO;
import org.alliancegenome.curation_api.interfaces.crud.geneAssociations.GeneGenomicLocationAssociationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.gff.Gff3GeneExecutor;
import org.alliancegenome.curation_api.model.entities.associations.geneAssociations.GeneGenomicLocationAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.associations.geneAssociations.GeneGenomicLocationAssociationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneGenomicLocationAssociationCrudController extends BaseEntityCrudController<GeneGenomicLocationAssociationService, GeneGenomicLocationAssociation, GeneGenomicLocationAssociationDAO> implements GeneGenomicLocationAssociationCrudInterface {

	@Inject GeneGenomicLocationAssociationService geneGenomicLocationService;

	@Inject Gff3GeneExecutor gff3GeneExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(geneGenomicLocationService);
	}

	public APIResponse updateGeneLocations(String dataProvider, String assembly, List<Gff3DTO> gffData) {
		LoadHistoryResponce resp = (LoadHistoryResponce) gff3GeneExecutor.runLoadApi(dataProvider, assembly, gffData);
		return new LoadHistoryResponce(resp.getHistory());
	}
}
