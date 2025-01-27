package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.VocabularyDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermSetDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.VocabularyTermSet;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.validation.base.AuditedObjectValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class VocabularyTermSetValidator extends AuditedObjectValidator<VocabularyTermSet> {

	@Inject VocabularyTermSetDAO vocabularyTermSetDAO;
	@Inject VocabularyDAO vocabularyDAO;

	private String errorMessage;

	public VocabularyTermSet validateVocabularyTermSetUpdate(VocabularyTermSet uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update VocabularyTermSet: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No VocabularyTermSet ID provided");
			throw new ApiErrorException(response);
		}
		VocabularyTermSet dbEntity = vocabularyTermSetDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("Could not find Vocabulary with ID: [" + id + "]");
			throw new ApiErrorException(response);
		}

		dbEntity = (VocabularyTermSet) validateAuditedObjectFields(uiEntity, dbEntity, false);

		return validateVocabularyTermSet(uiEntity, dbEntity);
	}

	public VocabularyTermSet validateVocabularyTermSetCreate(VocabularyTermSet uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not create VocabularyTermSet: [" + uiEntity.getName() + "]";

		VocabularyTermSet dbEntity = new VocabularyTermSet();

		dbEntity = (VocabularyTermSet) validateAuditedObjectFields(uiEntity, dbEntity, true);

		return validateVocabularyTermSet(uiEntity, dbEntity);
	}

	private VocabularyTermSet validateVocabularyTermSet(VocabularyTermSet uiEntity, VocabularyTermSet dbEntity) {

		String name = validateName(uiEntity);
		dbEntity.setName(name);

		String label = validateVocabularyLabel(uiEntity, dbEntity);
		dbEntity.setVocabularyLabel(label);

		Vocabulary vocabularyTermSetVocabulary = validateVocabularyTermSetVocabulary(uiEntity, dbEntity);
		dbEntity.setVocabularyTermSetVocabulary(vocabularyTermSetVocabulary);

		dbEntity.setVocabularyTermSetDescription(handleStringField(uiEntity.getVocabularyTermSetDescription()));

		List<VocabularyTerm> memberTerms = validateMemberTerms(uiEntity, dbEntity);
		dbEntity.setMemberTerms(memberTerms);

		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		return dbEntity;
	}

	private String validateName(VocabularyTermSet uiEntity) {
		String field = "name";
		if (StringUtils.isBlank(uiEntity.getName())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		if (!isUniqueValue(uiEntity.getName(), field, uiEntity.getId())) {
			addMessageResponse(field, ValidationConstants.NON_UNIQUE_MESSAGE);
			return null;
		}

		return uiEntity.getName();
	}

	public String validateVocabularyLabel(VocabularyTermSet uiEntity, VocabularyTermSet dbEntity) {
		String field = "vocabularyLabel";
		if (StringUtils.isBlank(uiEntity.getVocabularyLabel())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		if (StringUtils.isNotBlank(dbEntity.getVocabularyLabel()) && !StringUtils.equals(uiEntity.getVocabularyLabel(), dbEntity.getVocabularyLabel())) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		if (!isUniqueValue(uiEntity.getVocabularyLabel(), field, uiEntity.getId())) {
			addMessageResponse(field, ValidationConstants.NON_UNIQUE_MESSAGE);
			return null;
		}

		return uiEntity.getVocabularyLabel();
	}

	private Vocabulary validateVocabularyTermSetVocabulary(VocabularyTermSet uiEntity, VocabularyTermSet dbEntity) {
		String field = "vocabularyTermSetVocabulary";
		if (uiEntity.getVocabularyTermSetVocabulary() == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		SearchResponse<Vocabulary> vocabularyResponse = vocabularyDAO.findByField("name", uiEntity.getVocabularyTermSetVocabulary().getName());
		if (vocabularyResponse == null || vocabularyResponse.getSingleResult() == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		Vocabulary vocabulary = vocabularyResponse.getSingleResult();
		if (vocabulary.getObsolete() && (dbEntity.getVocabularyTermSetVocabulary() == null || !vocabulary.getName().equals(dbEntity.getVocabularyTermSetVocabulary().getName()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return vocabulary;
	}

	private List<VocabularyTerm> validateMemberTerms(VocabularyTermSet uiEntity, VocabularyTermSet dbEntity) {
		String field = "memberTerms";

		if (dbEntity.getVocabularyTermSetVocabulary() == null) {
			return null;
		}

		if (CollectionUtils.isEmpty(uiEntity.getMemberTerms())) {
			return null;
		}

		List<Long> previousIds = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(dbEntity.getMemberTerms())) {
			dbEntity.getMemberTerms().stream().map(VocabularyTerm::getId).collect(Collectors.toList());
		}

		for (VocabularyTerm memberTerm : uiEntity.getMemberTerms()) {
			if (!memberTerm.getVocabulary().getId().equals(dbEntity.getVocabularyTermSetVocabulary().getId())) {
				addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			}

			if (memberTerm.getObsolete() && (CollectionUtils.isEmpty(dbEntity.getMemberTerms()) || !previousIds.contains(memberTerm.getId()))) {
				addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}
		}

		return uiEntity.getMemberTerms();
	}

	private Boolean isUniqueValue(String uiEntityValue, String field, Long uiEntityId) {
		SearchResponse<VocabularyTermSet> response = vocabularyTermSetDAO.findByField(field, uiEntityValue);
		if (response == null || response.getSingleResult() == null) {
			return true;
		}
		if (uiEntityId == null) {
			return false;
		}
		if (uiEntityId.equals(response.getSingleResult().getId())) {
			return true;
		}
		return false;
	}
}
