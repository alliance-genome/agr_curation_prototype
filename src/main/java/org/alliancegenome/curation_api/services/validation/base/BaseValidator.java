package org.alliancegenome.curation_api.services.validation.base;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.auth.AuthenticatedUser;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.OrganizationDAO;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.CrossReferenceService;
import org.alliancegenome.curation_api.services.OrganizationService;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.validation.CrossReferenceValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.inject.Inject;

public class BaseValidator<E extends Object> {

	@Inject
	@AuthenticatedUser protected Person authenticatedPerson;

	@Inject CrossReferenceDAO crossReferenceDAO;
	@Inject CrossReferenceService crossReferenceService;
	@Inject CrossReferenceValidator crossReferenceValidator;
	@Inject OrganizationDAO organizationDAO;
	@Inject OrganizationService organizationService;
	@Inject PersonService personService;
	@Inject VocabularyTermService vocabularyTermService;
	
	public ObjectResponse<E> response;

	public String handleStringField(String string) {
		if (!StringUtils.isBlank(string)) {
			return string;
		}
		return null;
	}

	public List<E> handleListField(List<E> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			return list;
		}
		return null;
	}

	public void addMessageResponse(String message) {
		response.setErrorMessage(message);
	}

	public void addMessageResponse(String fieldName, String message) {
		response.addErrorMessage(fieldName, message);
	}

	public void convertMapToErrorMessages(String fieldName) {
		response.convertMapToErrorMessages(fieldName);
	}

	public Organization validateDataProvider(Organization uiDataProvider, Organization dbDataProvider, boolean newEntity) {
		String field = "dataProvider";

		if (uiDataProvider == null) {
			if (newEntity) {
				return organizationDAO.getOrCreateOrganization("Alliance");
			} else {
				addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
				return null;
			}
		}
		
		Organization dataProvider = null;
		if (uiDataProvider.getId() != null) {
			dataProvider = organizationService.getById(uiDataProvider.getId()).getEntity();
		} else if (StringUtils.isNotBlank(uiDataProvider.getAbbreviation())) {
			dataProvider = organizationService.getByAbbr(uiDataProvider.getAbbreviation()).getEntity();
		}
		
		if (dataProvider == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (dataProvider.getObsolete() && (dbDataProvider == null || !dataProvider.getId().equals(dbDataProvider.getId()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return dataProvider;
	}
	
	protected CrossReference validateDataProviderCrossReference(CrossReference uiXref, CrossReference dbXref) {
		return validateDataProviderCrossReference(uiXref, dbXref, false);
	}

	protected CrossReference validateDataProviderCrossReference(CrossReference uiXref, CrossReference dbXref, boolean isSecondaryProvider) {
		String fieldName = isSecondaryProvider ? "secondaryDataProviderCrossReference" : "dataProviderCrossReference";
		
		CrossReference xref = null;
		String dbXrefUniqueId = null;
		String uiXrefUniqueId = null;
		if (dbXref != null) {
			dbXrefUniqueId = crossReferenceService.getCrossReferenceUniqueId(dbXref);
		}
		
		if (ObjectUtils.isNotEmpty(uiXref)) {
			ObjectResponse<CrossReference> xrefResponse = crossReferenceValidator.validateCrossReference(uiXref, false);
			if (xrefResponse.hasErrors()) {
				addMessageResponse(fieldName, xrefResponse.errorMessagesString());
			} else {
				uiXrefUniqueId = crossReferenceService.getCrossReferenceUniqueId(xrefResponse.getEntity());
				if (dbXrefUniqueId == null || !dbXrefUniqueId.equals(uiXrefUniqueId)) {
					xref = crossReferenceDAO.persist(xrefResponse.getEntity());
				} else if (dbXrefUniqueId != null && dbXrefUniqueId.equals(uiXrefUniqueId)) {
					xref = crossReferenceService.updateCrossReference(dbXref, uiXref);
				}
			}
		}
		
		return xref;
	}
	
	protected VocabularyTerm validateTermInVocabulary(String field, String vocabularyName, VocabularyTerm dbTerm, VocabularyTerm uiTerm) {
		return validateVocabularyTerm(field, vocabularyName, dbTerm, uiTerm, false, false);
	}
	
	protected VocabularyTerm validateRequiredTermInVocabulary(String field, String vocabularyName, VocabularyTerm dbTerm, VocabularyTerm uiTerm) {
		return validateVocabularyTerm(field, vocabularyName, dbTerm, uiTerm, true, false);
	}
	
	protected VocabularyTerm validateTermInVocabularyTermSet(String field, String vocabularyName, VocabularyTerm dbTerm, VocabularyTerm uiTerm) {
		return validateVocabularyTerm(field, vocabularyName, dbTerm, uiTerm, false, true);
	}
	
	protected VocabularyTerm validateRequiredTermInVocabularyTermSet(String field, String vocabularyName, VocabularyTerm dbTerm, VocabularyTerm uiTerm) {
		return validateVocabularyTerm(field, vocabularyName, dbTerm, uiTerm, true, true);
	}
	
	protected VocabularyTerm validateVocabularyTerm(String field, String vocabularyOrSetName, VocabularyTerm dbTerm, VocabularyTerm uiTerm, boolean isRequired, boolean isTermSet) {
		if (uiTerm == null) {
			if (isRequired) {
				addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			}
			return null;
		}

		VocabularyTerm term = null;
		if (isTermSet) {
			term = vocabularyTermService.getTermInVocabularyTermSet(vocabularyOrSetName, uiTerm.getName()).getEntity();
		} else {
			term = vocabularyTermService.getTermInVocabulary(vocabularyOrSetName, uiTerm.getName()).getEntity();
		}
		
		if (term == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (term.getObsolete() && (dbTerm == null || !term.getName().equals(dbTerm.getName()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return term;
	}
	
	protected List<VocabularyTerm> validateTermsInVocabulary(String field, String vocabularyName, List<VocabularyTerm> dbTerms, List<VocabularyTerm> uiTerms) {
		return validateVocabularyTerms(field, vocabularyName, dbTerms, uiTerms, false, false);
	}
	
	protected List<VocabularyTerm> validateRequiredTermInVocabulary(String field, String vocabularyName, List<VocabularyTerm> dbTerms, List<VocabularyTerm> uiTerms) {
		return validateVocabularyTerms(field, vocabularyName, dbTerms, uiTerms, true, false);
	}
	
	protected List<VocabularyTerm> validateTermInVocabularyTermSet(String field, String vocabularyName, List<VocabularyTerm> dbTerms, List<VocabularyTerm> uiTerms) {
		return validateVocabularyTerms(field, vocabularyName, dbTerms, uiTerms, false, true);
	}
	
	protected List<VocabularyTerm> validateRequiredTermInVocabularyTermSet(String field, String vocabularyName, List<VocabularyTerm> dbTerms, List<VocabularyTerm> uiTerms) {
		return validateVocabularyTerms(field, vocabularyName, dbTerms, uiTerms, true, true);
	}
	
	protected List<VocabularyTerm> validateVocabularyTerms(String field, String vocabularyOrSetName, List<VocabularyTerm> dbTerms, List<VocabularyTerm> uiTerms, boolean isRequired, boolean isTermSet) {
		if (CollectionUtils.isEmpty(uiTerms)) {
			if (isRequired) {
				addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			}
			return null;
		}
		
		List<VocabularyTerm> validTerms = new ArrayList<>();
		List<Long> previousIds = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(dbTerms)) {
			previousIds = dbTerms.stream().map(VocabularyTerm::getId).collect(Collectors.toList());
		}
		for (VocabularyTerm uiTerm : uiTerms) {
			VocabularyTerm term = null;
			if (isTermSet) {
				term = vocabularyTermService.getTermInVocabularyTermSet(vocabularyOrSetName, uiTerm.getName()).getEntity();
			} else {
				term = vocabularyTermService.getTermInVocabulary(vocabularyOrSetName, uiTerm.getName()).getEntity();
			}
			
			if (term == null) {
				addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			}
			
			if (term.getObsolete() && (CollectionUtils.isEmpty(dbTerms)) || !previousIds.contains(term.getId())) {
				addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}
			
			validTerms.add(term);
		}
		
		return validTerms;
	}

}
