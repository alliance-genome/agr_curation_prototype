package org.alliancegenome.curation_api.services.validation;

import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.ExperimentalConditionDAO;
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
import org.alliancegenome.curation_api.services.ontology.AnatomicalTermService;
import org.alliancegenome.curation_api.services.ontology.ChemicalTermService;
import org.alliancegenome.curation_api.services.ontology.ExperimentalConditionOntologyTermService;
import org.alliancegenome.curation_api.services.ontology.GoTermService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.services.ontology.ZecoTermService;
import org.alliancegenome.curation_api.services.validation.base.AuditedObjectValidator;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ExperimentalConditionValidator extends AuditedObjectValidator<ExperimentalCondition> {

	@Inject ExperimentalConditionDAO experimentalConditionDAO;
	@Inject GoTermService goTermService;
	@Inject ZecoTermService zecoTermService;
	@Inject AnatomicalTermService anatomicalTermService;
	@Inject ChemicalTermService chemicalTermService;
	@Inject ExperimentalConditionOntologyTermService ecOntologyTermService;
	@Inject NcbiTaxonTermService ncbiTaxonTermService;

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

		ExperimentalConditionOntologyTerm ecOntologyTerm = validateConditionId(uiEntity, dbEntity);
		dbEntity.setConditionId(ecOntologyTerm);

		GOTerm conditionGeneOntology = validateConditionGeneOntology(uiEntity, dbEntity);
		dbEntity.setConditionGeneOntology(conditionGeneOntology);

		AnatomicalTerm conditionAnatomy = validateConditionAnatomy(uiEntity, dbEntity);
		dbEntity.setConditionAnatomy(conditionAnatomy);

		ChemicalTerm conditionChemical = validateConditionChemical(uiEntity, dbEntity);
		dbEntity.setConditionChemical(conditionChemical);

		NCBITaxonTerm conditionTaxon = validateConditionTaxon(uiEntity, dbEntity);
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

	public ZECOTerm validateConditionClass(ExperimentalCondition uiEntity, ExperimentalCondition dbEntity) {
		String field = "conditionClass";
		if (ObjectUtils.isEmpty(uiEntity.getConditionClass())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		ZECOTerm zecoTerm = null;
		if (StringUtils.isNotBlank(uiEntity.getConditionClass().getCurie())) {
			zecoTerm = zecoTermService.findByCurie(uiEntity.getConditionClass().getCurie());
			if (zecoTerm == null || zecoTerm.getSubsets().isEmpty() || !zecoTerm.getSubsets().contains(OntologyConstants.ZECO_AGR_SLIM_SUBSET)) {
				addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			} else if (zecoTerm.getObsolete() && (dbEntity.getConditionClass() == null || !zecoTerm.getId().equals(dbEntity.getConditionClass().getId()))) {
				addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}
		}
		return zecoTerm;
	}

	public ExperimentalConditionOntologyTerm validateConditionId(ExperimentalCondition uiEntity, ExperimentalCondition dbEntity) {
		String field = "conditionId";
		if (ObjectUtils.isEmpty(uiEntity.getConditionId())) {
			return null;
		}
		ExperimentalConditionOntologyTerm ecOntologyTerm = null;
		if (StringUtils.isNotBlank(uiEntity.getConditionId().getCurie())) {
			ecOntologyTerm = ecOntologyTermService.findByCurie(uiEntity.getConditionId().getCurie());
			if (ecOntologyTerm == null) {
				addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			} else if (ecOntologyTerm.getObsolete() && (dbEntity.getConditionId() == null || !ecOntologyTerm.getId().equals(dbEntity.getConditionId().getId()))) {
				addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}
		}
		return ecOntologyTerm;
	}

	public GOTerm validateConditionGeneOntology(ExperimentalCondition uiEntity, ExperimentalCondition dbEntity) {
		String field = "conditionGeneOntology";
		if (ObjectUtils.isEmpty(uiEntity.getConditionGeneOntology())) {
			return null;
		}
		GOTerm goTerm = null;
		if (StringUtils.isNotBlank(uiEntity.getConditionGeneOntology().getCurie())) {
			goTerm = goTermService.findByCurie(uiEntity.getConditionGeneOntology().getCurie());
			if (goTerm == null) {
				addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			} else if (goTerm.getObsolete() && (dbEntity.getConditionGeneOntology() == null || !goTerm.getId().equals(dbEntity.getConditionGeneOntology().getId()))) {
				addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}
		}
		return goTerm;
	}

	public AnatomicalTerm validateConditionAnatomy(ExperimentalCondition uiEntity, ExperimentalCondition dbEntity) {
		String field = "conditionAnatomy";
		if (ObjectUtils.isEmpty(uiEntity.getConditionAnatomy())) {
			return null;
		}
		AnatomicalTerm anatomicalTerm = null;
		if (StringUtils.isNotBlank(uiEntity.getConditionAnatomy().getCurie())) {
			anatomicalTerm = anatomicalTermService.findByCurie(uiEntity.getConditionAnatomy().getCurie());
			if (anatomicalTerm == null) {
				addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			} else if (anatomicalTerm.getObsolete() && (dbEntity.getConditionAnatomy() == null || !anatomicalTerm.getId().equals(dbEntity.getConditionAnatomy().getId()))) {
				addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}
		}
		return anatomicalTerm;
	}

	public ChemicalTerm validateConditionChemical(ExperimentalCondition uiEntity, ExperimentalCondition dbEntity) {
		String field = "conditionChemical";
		if (ObjectUtils.isEmpty(uiEntity.getConditionChemical())) {
			return null;
		}
		ChemicalTerm chemicalTerm = null;
		if (StringUtils.isNotBlank(uiEntity.getConditionChemical().getCurie())) {
			chemicalTerm = chemicalTermService.findByCurie(uiEntity.getConditionChemical().getCurie());
			if (chemicalTerm == null) {
				addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			} else if (chemicalTerm.getObsolete() && (dbEntity.getConditionChemical() == null || !chemicalTerm.getId().equals(dbEntity.getConditionChemical().getId()))) {
				addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}
		}
		return chemicalTerm;
	}

	public NCBITaxonTerm validateConditionTaxon(ExperimentalCondition uiEntity, ExperimentalCondition dbEntity) {
		String field = "conditionTaxon";
		if (ObjectUtils.isEmpty(uiEntity.getConditionTaxon())) {
			return null;
		}
		NCBITaxonTerm taxonTerm = null;
		if (StringUtils.isNotBlank(uiEntity.getConditionTaxon().getCurie())) {
			taxonTerm = ncbiTaxonTermService.findByCurie(uiEntity.getConditionTaxon().getCurie());
			if (taxonTerm == null) {
				taxonTerm = ncbiTaxonTermService.getTaxonFromDB(uiEntity.getConditionTaxon().getCurie());
			}
			if (taxonTerm == null) {
				addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			} else if (taxonTerm.getObsolete() && (dbEntity.getConditionTaxon() == null || !taxonTerm.getId().equals(dbEntity.getConditionTaxon().getId()))) {
				addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}
		}
		return taxonTerm;
	}
}
