package org.alliancegenome.curation_api.services.loads;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.dao.loads.BulkLoadGroupDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadGroup;

@RequestScoped
public class BulkLoadGroupService extends BaseService<BulkLoadGroup, BulkLoadGroupDAO> {
    
    @Inject
    BulkLoadGroupDAO bulkLoadGroupDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(bulkLoadGroupDAO);
    }
}