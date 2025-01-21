package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.PredictedVariantConsequence;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PredictedVariantConsequenceDAO extends BaseSQLDAO<PredictedVariantConsequence> {

	protected PredictedVariantConsequenceDAO() {
		super(PredictedVariantConsequence.class);
	}

}
