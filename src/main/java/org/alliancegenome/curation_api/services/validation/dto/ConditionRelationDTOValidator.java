package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.dao.ExperimentalConditionDAO;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.ConditionRelationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.ExperimentalConditionDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.annotations.AnnotationUniqueIdHelper;
import org.alliancegenome.curation_api.services.validation.dto.base.AuditedObjectDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ConditionRelationDTOValidator extends AuditedObjectDTOValidator<ConditionRelation, ConditionRelationDTO> {

	@Inject ConditionRelationDAO conditionRelationDAO;
	@Inject VocabularyTermService vocabularyTermService;
	@Inject ExperimentalConditionDTOValidator experimentalConditionDtoValidator;
	@Inject ExperimentalConditionDAO experimentalConditionDAO;
	@Inject ReferenceDAO referenceDAO;
	@Inject ReferenceService referenceService;

	public ObjectResponse<ConditionRelation> validateConditionRelationDTO(ConditionRelationDTO dto) {
		response = new ObjectResponse<ConditionRelation>();
		
		ConditionRelation relation = new ConditionRelation();

		Reference reference = validateReference(dto.getReferenceCurie());
		String refCurie = reference == null ? null : reference.getCurie();

		String uniqueId = AnnotationUniqueIdHelper.getConditionRelationUniqueId(dto, refCurie);
		SearchResponse<ConditionRelation> searchResponseRel = conditionRelationDAO.findByField("uniqueId", uniqueId);

		if (searchResponseRel == null || searchResponseRel.getSingleResult() == null) {
			relation = new ConditionRelation();
			relation.setUniqueId(uniqueId);
		} else {
			relation = searchResponseRel.getSingleResult();
		}
		relation.setSingleReference(reference);

		relation = validateAuditedObjectDTO(relation, dto);
		
		VocabularyTerm relationType = validateRequiredTermInVocabulary("condition_relation_type_name", dto.getConditionRelationTypeName(), VocabularyConstants.CONDITION_RELATION_TYPE_VOCABULARY);
		relation.setConditionRelationType(relationType);

		List<ExperimentalCondition> conditions = new ArrayList<>();
		if (CollectionUtils.isEmpty(dto.getConditionDtos())) {
			response.addErrorMessage("condition_dtos", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			for (ExperimentalConditionDTO conditionDTO : dto.getConditionDtos()) {
				ObjectResponse<ExperimentalCondition> ecResponse = experimentalConditionDtoValidator.validateExperimentalConditionDTO(conditionDTO);
				if (ecResponse.hasErrors()) {
					response.addErrorMessage("condition_dtos", ecResponse.errorMessagesString());
				} else {
					conditions.add(experimentalConditionDAO.persist(ecResponse.getEntity()));
				}
			}
		}
		relation.setConditions(conditions);

		if (StringUtils.isNotBlank(dto.getHandle())) {
			relation.setHandle(dto.getHandle());
			if (StringUtils.isBlank(dto.getReferenceCurie())) {
				response.addErrorMessage("handle", ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "reference_curie");
			}
		} else {
			if (relation.getHandle() != null) {
				response.addErrorMessage("handle", ValidationConstants.REQUIRED_MESSAGE);
			}
			relation.setHandle(null);
		}

		response.setEntity(relation);

		return response;
	}
}
