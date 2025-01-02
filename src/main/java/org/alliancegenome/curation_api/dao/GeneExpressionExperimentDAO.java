package org.alliancegenome.curation_api.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.GeneExpressionExperiment;

@ApplicationScoped
public class GeneExpressionExperimentDAO extends BaseSQLDAO<GeneExpressionExperiment> {

	protected GeneExpressionExperimentDAO() {
		super(GeneExpressionExperiment.class);
	}
}
