package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetSampleAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HTPExpressionDatasetSampleAnnotationDAO extends BaseSQLDAO<HTPExpressionDatasetSampleAnnotation> {
    	
    protected HTPExpressionDatasetSampleAnnotationDAO() {
		super(HTPExpressionDatasetSampleAnnotation.class);
	}
}
