package org.alliancegenome.curation_api.services.base;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Annotation;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections.CollectionUtils;

public abstract class BaseAnnotationCrudService<E extends Annotation, D extends BaseSQLDAO<E>> extends BaseEntityCrudService<E, D> {

	@Override
	protected abstract void init();

	protected List<Long> getAllReferencedConditionRelationIds(D dao) {
		ProcessDisplayHelper pdh = new ProcessDisplayHelper();

		List<Long> daIds = dao.findAllIds().getResults();
		pdh.startProcess("Checking DAs for referenced Conditions ", daIds.size());

		List<Long> conditionRelationIds = new ArrayList<>();
		daIds.forEach(daId -> {
			E annotation = dao.find(daId);
			if (CollectionUtils.isNotEmpty(annotation.getConditionRelations())) {
				List<Long> crIds = annotation.getConditionRelations().stream().map(ConditionRelation::getId).collect(Collectors.toList());
				conditionRelationIds.addAll(crIds);
			}
			pdh.progressProcess();
		});
		pdh.finishProcess();

		return conditionRelationIds;
	}
	
	public ObjectResponse<E> getByIdentifier(String identifier) {
		E object = findByAlternativeFields(List.of("curie", "primaryExternalId", "modInternalId", "uniqueId"), identifier);
		ObjectResponse<E> ret = new ObjectResponse<E>(object);
		return ret;
	}
}
