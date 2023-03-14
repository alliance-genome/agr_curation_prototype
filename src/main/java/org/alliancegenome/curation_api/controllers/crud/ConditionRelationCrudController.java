package org.alliancegenome.curation_api.controllers.crud;

import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.interfaces.crud.ConditionRelationCrudInterface;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.ConditionRelationService;


@RequestScoped
public class ConditionRelationCrudController extends BaseEntityCrudController<ConditionRelationService, ConditionRelation, ConditionRelationDAO> implements ConditionRelationCrudInterface {

	@Inject
	ConditionRelationService conditionRelationService;

	@Override
	@PostConstruct
	protected void init() {
		setService(conditionRelationService);
	}

	public ObjectResponse<ConditionRelation> validate(ConditionRelation entity) {
		return conditionRelationService.validate(entity);
	}

	@Override
	public SearchResponse<ConditionRelation> findExperiments(HashMap<String, Object> params) {
		return conditionRelationService.getConditionRelationSearchResponse(params);
	}

}
