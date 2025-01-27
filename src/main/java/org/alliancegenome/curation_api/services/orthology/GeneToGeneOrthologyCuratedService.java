package org.alliancegenome.curation_api.services.orthology;

import org.alliancegenome.curation_api.dao.orthology.GeneToGeneOrthologyCuratedDAO;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyCurated;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneToGeneOrthologyCuratedService extends BaseEntityCrudService<GeneToGeneOrthologyCurated, GeneToGeneOrthologyCuratedDAO> {

	@Inject GeneToGeneOrthologyCuratedDAO geneToGeneOrthologyCuratedDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneToGeneOrthologyCuratedDAO);
	}

}
