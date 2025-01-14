package org.alliancegenome.curation_api.services.validation;

import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.ExperimentalConditionDAO;
import org.alliancegenome.curation_api.dao.ontology.AnatomicalTermDAO;
import org.alliancegenome.curation_api.dao.ontology.ChemicalTermDAO;
import org.alliancegenome.curation_api.dao.ontology.ExperimentalConditionOntologyTermDAO;
import org.alliancegenome.curation_api.dao.ontology.GoTermDAO;
import org.alliancegenome.curation_api.dao.ontology.ZecoTermDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.ontology.AnatomicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ChemicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ExperimentalConditionOntologyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ZECOTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.annotations.AnnotationUniqueIdHelper;
import org.alliancegenome.curation_api.services.helpers.annotations.ExperimentalConditionSummary;
import org.alliancegenome.curation_api.services.validation.base.AuditedObjectValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ExperimentalConditionValidator extends AuditedObjectValidator<ExperimentalCondition> {

	@Inject ExperimentalConditionDAO experimentalConditionDAO;
	@Inject GoTermDAO goTermDAO;
	@Inject ZecoTermDAO zecoTermDAO;
	@Inject AnatomicalTermDAO anatomicalTermDAO;
	@Inject ChemicalTermDAO chemicalTermDAO;
	@Inject ExperimentalConditionOntologyTermDAO ecOntologyTermDAO;

	private String errorMessage;

	public ExperimentalCondition validateExperimentalConditionUpdate(ExperimentalCondition uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update ExperimentalCondition: [" + uiEntity.getUniqueId() + "]";

		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No ExperimentalCondition ID provided");
			throw new ApiErrorException(response);
		}
		ExperimentalCondition dbEntity = experimentalConditionDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("Could not find ExperimentalCondition with ID: [" + id + "]");
			throw new ApiErrorException(response);
		}

		dbEntity = (ExperimentalCondition) validateAuditedObjectFields(uiEntity, dbEntity, false);

		return validateExperimentalCondition(uiEntity, dbEntity);
	}

	public ExperimentalCondition validateExperimentalConditionCreate(ExperimentalCondition uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not create ExperimentalCondition";

		ExperimentalCondition dbEntity = new ExperimentalCondition();

		dbEntity = (ExperimentalCondition) validateAuditedObjectFields(uiEntity, dbEntity, true);

		return validateExperimentalCondition(uiEntity, dbEntity);
	}

	public ExperimentalCondition validateExperimentalCondition(ExperimentalCondition uiEntity, ExperimentalCondition dbEntity) {
		ZECOTerm conditionClass = validateConditionClass(uiEntity, dbEntity);
		dbEntity.setConditionClass(conditionClass);

		ExperimentalConditionOntologyTerm ecOntologyTerm = validateEntity(ecOntologyTermDAO, "conditionId", uiEntity.getConditionId(), dbEntity.getConditionId());
		dbEntity.setConditionId(ecOntologyTerm);

		GOTerm conditionGeneOntology = validateEntity(goTermDAO, "conditionGeneOntology", uiEntity.getConditionGeneOntology(), dbEntity.getConditionGeneOntology());
		dbEntity.setConditionGeneOntology(conditionGeneOntology);

		AnatomicalTerm conditionAnatomy = validateEntity(anatomicalTermDAO, "conditionAnatomy", uiEntity.getConditionAnatomy(), dbEntity.getConditionAnatomy());
		dbEntity.setConditionAnatomy(conditionAnatomy);

		ChemicalTerm conditionChemical = validateEntity(chemicalTermDAO, "conditionChemical", uiEntity.getConditionChemical(), dbEntity.getConditionChemical());
		dbEntity.setConditionChemical(conditionChemical);

		NCBITaxonTerm conditionTaxon = validateTaxon(uiEntity.getConditionTaxon(), dbEntity.getConditionTaxon(), "conditionTaxon");
		dbEntity.setConditionTaxon(conditionTaxon);

		dbEntity.setConditionQuantity(handleStringField(uiEntity.getConditionQuantity()));
		dbEntity.setConditionFreeText(handleStringField(uiEntity.getConditionFreeText()));

		dbEntity.setConditionSummary(ExperimentalConditionSummary.getConditionSummary(dbEntity));

		String uniqueId = AnnotationUniqueIdHelper.getExperimentalConditionUniqueId(dbEntity);
		if (!uniqueId.equals(uiEntity.getUniqueId())) {
			SearchResponse<ExperimentalCondition> dbSearchResponse = experimentalConditionDAO.findByField("uniqueId", uniqueId);
			if (dbSearchResponse != null) {
				addMessageResponse("uniqueId", ValidationConstants.NON_UNIQUE_MESSAGE);
			} else {
				dbEntity.setUniqueId(uniqueId);
			}
		}

		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		return dbEntity;
	}

	private ZECOTerm validateConditionClass(ExperimentalCondition uiEntity, ExperimentalCondition dbEntity) {
		String field = "conditionClass";
		ZECOTerm conditionClass = validateRequiredEntity(zecoTermDAO, field, uiEntity.getConditionClass(), dbEntity.getConditionClass());
		
		if (conditionClass != null && (CollectionUtils.isEmpty(conditionClass.getSubsets()) || !conditionClass.getSubsets().contains(OntologyConstants.ZECO_AGR_SLIM_SUBSET))) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		} 
		
		return conditionClass;
	}
}
