package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HTPExpressionDatasetAnnotationDAO extends BaseSQLDAO<HTPExpressionDatasetAnnotation> {
	
	protected HTPExpressionDatasetAnnotationDAO() {
		super(HTPExpressionDatasetAnnotation.class);
	}
}
