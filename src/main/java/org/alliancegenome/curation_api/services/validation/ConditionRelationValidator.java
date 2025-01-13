package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.dao.ExperimentalConditionDAO;
import org.alliancegenome.curation_api.dao.LiteratureReferenceDAO;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.helpers.annotations.AnnotationUniqueIdHelper;
import org.alliancegenome.curation_api.services.validation.base.AuditedObjectValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ConditionRelationValidator extends AuditedObjectValidator<ConditionRelation> {

	@Inject ConditionRelationDAO conditionRelationDAO;
	@Inject LiteratureReferenceDAO literatureReferenceDAO;
	@Inject ReferenceDAO referenceDAO;
	@Inject ExperimentalConditionDAO experimentalConditionDAO;
	@Inject ReferenceService referenceService;
	@Inject ReferenceValidator referenceValidator;

	private String errorMessage;

	public ObjectResponse<ConditionRelation> validateConditionRelation(ConditionRelation uiEntity) {
		ConditionRelation conditionRelation;
		if (uiEntity.getId() == null) {
			conditionRelation = validateConditionRelationCreate(uiEntity, false, false);
		} else {
			conditionRelation = validateConditionRelationUpdate(uiEntity, false, false);
		}
		response.setEntity(conditionRelation);
		return response;
	}

	public ConditionRelation validateConditionRelationUpdate(ConditionRelation uiEntity, Boolean throwError, Boolean checkUniqueness) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update ConditionRelation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No ConditionRelation ID provided");
			throw new ApiErrorException(response);
		}
		ConditionRelation dbEntity = conditionRelationDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("Could not find ConditionRelation with ID: [" + id + "]");
			throw new ApiErrorException(response);
		}

		dbEntity = validateAuditedObjectFields(uiEntity, dbEntity, false);

		return validateConditionRelation(uiEntity, dbEntity, throwError, checkUniqueness);
	}

	public ConditionRelation validateConditionRelationCreate(ConditionRelation uiEntity, Boolean throwError, Boolean checkUniqueness) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not create ConditionRelation";

		ConditionRelation dbEntity = new ConditionRelation();

		dbEntity = validateAuditedObjectFields(uiEntity, dbEntity, true);

		return validateConditionRelation(uiEntity, dbEntity, throwError, checkUniqueness);
	}

	public ConditionRelation validateConditionRelation(ConditionRelation uiEntity, ConditionRelation dbEntity, Boolean throwError, Boolean checkUniqueness) {
		String handle = validateHandle(uiEntity, dbEntity);
		dbEntity.setHandle(handle);

		VocabularyTerm conditionRelationType = validateRequiredTermInVocabulary("conditionRelationType", VocabularyConstants.CONDITION_RELATION_TYPE_VOCABULARY, dbEntity.getConditionRelationType(), uiEntity.getConditionRelationType());
		dbEntity.setConditionRelationType(conditionRelationType);

		List<ExperimentalCondition> conditions = validateConditions(uiEntity, dbEntity);
		dbEntity.setConditions(conditions);

		Reference singleReference = validateSingleReference(uiEntity, dbEntity);
		dbEntity.setSingleReference(singleReference);

		String uniqueId = AnnotationUniqueIdHelper.getConditionRelationUniqueId(dbEntity);
		if (checkUniqueness && !uniqueId.equals(dbEntity.getUniqueId())) {
			SearchResponse<ConditionRelation> crSearchResponse = conditionRelationDAO.findByField("uniqueId", uniqueId);
			if (crSearchResponse != null) {
				addMessageResponse("uniqueId", ValidationConstants.NON_UNIQUE_MESSAGE);
			} else {
				dbEntity.setUniqueId(uniqueId);
			}
		} else {
			dbEntity.setUniqueId(uniqueId);
		}

		if (response.hasErrors()) {
			if (throwError) {
				response.setErrorMessage(errorMessage);
				throw new ApiErrorException(response);
			} else {
				return null;
			}
		}

		return dbEntity;
	}

	private String validateHandle(ConditionRelation uiEntity, ConditionRelation dbEntity) {
		// You cannot move from a condition-relation with handle to one without.
		if (!StringUtils.isBlank(dbEntity.getHandle()) && StringUtils.isBlank(uiEntity.getHandle())) {
			addMessageResponse("handle", ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		if (StringUtils.isBlank(uiEntity.getHandle())) {
			return null;
		}

		if (uiEntity.getSingleReference() == null || StringUtils.isBlank(uiEntity.getSingleReference().getCurie())) {
			addMessageResponse("handle", ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "singleReference");
			return null;
		}

		// if handle / pub combination has changed check that the new key is not already
		// taken in the database
		if (!StringUtils.isBlank(dbEntity.getHandle()) && !getUniqueKey(uiEntity).equals(getUniqueKey(dbEntity))) {
			HashMap<String, Object> params = new HashMap<>();
			params.put("handle", uiEntity.getHandle());
			params.put("singleReference.curie", uiEntity.getSingleReference().getCurie());

			SearchResponse<ConditionRelation> response = conditionRelationDAO.findByParams(params);
			if (response.getResults().size() > 0) {
				addMessageResponse("handle", "Handle / Pub combination already exists");
				return null;
			}
		}

		return uiEntity.getHandle();
	}

	private String getUniqueKey(ConditionRelation relation) {
		return relation.getHandle() + relation.getSingleReference().getCurie();
	}

	private Reference validateSingleReference(ConditionRelation uiEntity, ConditionRelation dbEntity) {
		String field = "singleReference";
		if (uiEntity.getSingleReference() == null) {
			return null;
		}

		ObjectResponse<Reference> singleRefResponse = referenceValidator.validateReference(uiEntity.getSingleReference());
		if (singleRefResponse.getEntity() == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (singleRefResponse.getEntity().getObsolete() && (dbEntity.getSingleReference() == null || !singleRefResponse.getEntity().getCurie().equals(dbEntity.getSingleReference().getCurie()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return singleRefResponse.getEntity();
	}

	public List<ExperimentalCondition> validateConditions(ConditionRelation uiEntity, ConditionRelation dbEntity) {
		String field = "conditions";
		if (CollectionUtils.isEmpty(uiEntity.getConditions())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		List<ExperimentalCondition> conditions = new ArrayList<>();
		for (ExperimentalCondition condition : uiEntity.getConditions()) {
			SearchResponse<ExperimentalCondition> conditionResponse = experimentalConditionDAO.findByField("uniqueId", condition.getUniqueId());
			if (conditionResponse == null || conditionResponse.getSingleResult() == null) {
				addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			}
			if (condition.getId() == null) {
				condition = conditionResponse.getSingleResult();
			}
			conditions.add(condition);
		}

		return conditions;
	}
}
