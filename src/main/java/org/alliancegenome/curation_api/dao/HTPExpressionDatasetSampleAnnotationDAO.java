package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetSampleAnnotation;

public class HTPExpressionDatasetSampleAnnotationDAO extends BaseSQLDAO<HTPExpressionDatasetSampleAnnotation> {
    	
    protected HTPExpressionDatasetSampleAnnotationDAO() {
		super(HTPExpressionDatasetSampleAnnotation.class);
	}
}
