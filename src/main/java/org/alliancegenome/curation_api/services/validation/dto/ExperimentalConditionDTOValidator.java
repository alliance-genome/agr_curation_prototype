package org.alliancegenome.curation_api.services.validation.dto;

import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.ExperimentalConditionDAO;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.ontology.AnatomicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ChemicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ExperimentalConditionOntologyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ZECOTerm;
import org.alliancegenome.curation_api.model.ingest.dto.ExperimentalConditionDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.annotations.AnnotationUniqueIdHelper;
import org.alliancegenome.curation_api.services.helpers.annotations.ExperimentalConditionSummary;
import org.alliancegenome.curation_api.services.ontology.AnatomicalTermService;
import org.alliancegenome.curation_api.services.ontology.ChemicalTermService;
import org.alliancegenome.curation_api.services.ontology.ExperimentalConditionOntologyTermService;
import org.alliancegenome.curation_api.services.ontology.GoTermService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.services.ontology.ZecoTermService;
import org.alliancegenome.curation_api.services.validation.dto.base.AuditedObjectDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ExperimentalConditionDTOValidator extends AuditedObjectDTOValidator<ExperimentalCondition, ExperimentalConditionDTO> {

	@Inject ExperimentalConditionDAO experimentalConditionDAO;
	@Inject ZecoTermService zecoTermService;
	@Inject ChemicalTermService chemicalTermService;
	@Inject AnatomicalTermService anatomicalTermService;
	@Inject NcbiTaxonTermService ncbiTaxonTermService;
	@Inject GoTermService goTermService;
	@Inject ExperimentalConditionOntologyTermService experimentalConditionOntologyTermService;
	@Inject ExperimentalConditionSummary experimentalConditionSummary;

	public ObjectResponse<ExperimentalCondition> validateExperimentalConditionDTO(ExperimentalConditionDTO dto) {
		response = new ObjectResponse<ExperimentalCondition>();
		
		String uniqueId = AnnotationUniqueIdHelper.getExperimentalConditionUniqueId(dto);

		ExperimentalCondition experimentalCondition = findDatabaseObject(experimentalConditionDAO, "uniqueId", uniqueId);
		if (experimentalCondition == null) {
			experimentalCondition = new ExperimentalCondition();
			experimentalCondition.setUniqueId(uniqueId);
		}
		
		experimentalCondition = validateAuditedObjectDTO(experimentalCondition, dto);

		ChemicalTerm conditionChemical = validateOntologyTerm(chemicalTermService, "condition_checmical_curie", dto.getConditionChemicalCurie());
		experimentalCondition.setConditionChemical(conditionChemical);

		ExperimentalConditionOntologyTerm conditionId = validateOntologyTerm(experimentalConditionOntologyTermService, "condition_id_curie", dto.getConditionIdCurie());
		experimentalCondition.setConditionId(conditionId);

		ZECOTerm conditionClass = validateRequiredOntologyTerm(zecoTermService, "condition_class_curie", dto.getConditionClassCurie());
		if (conditionClass != null && (CollectionUtils.isEmpty(conditionClass.getSubsets()) || !conditionClass.getSubsets().contains(OntologyConstants.ZECO_AGR_SLIM_SUBSET))) {
			response.addErrorMessage("condition_class_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getConditionClassCurie() + ")");
		}
		experimentalCondition.setConditionClass(conditionClass);
		
		AnatomicalTerm conditionAnatomy = validateOntologyTerm(anatomicalTermService, "condition_anatomy_curie", dto.getConditionAnatomyCurie());
		experimentalCondition.setConditionAnatomy(conditionAnatomy);

		NCBITaxonTerm conditionTaxon = validateTaxon("condition_taxon_curie", dto.getConditionTaxonCurie());
		experimentalCondition.setConditionTaxon(conditionTaxon);

		GOTerm conditionGeneOntology = validateOntologyTerm(goTermService, "condition_gene_ontology_curie", dto.getConditionGeneOntologyCurie());
		experimentalCondition.setConditionGeneOntology(conditionGeneOntology);

		String conditionQuantity = null;
		if (StringUtils.isNotBlank(dto.getConditionQuantity())) {
			conditionQuantity = dto.getConditionQuantity();
		}
		experimentalCondition.setConditionQuantity(conditionQuantity);

		String conditionFreeText = handleStringField(dto.getConditionFreeText());
		experimentalCondition.setConditionFreeText(conditionFreeText);

		if (!response.hasErrors()) {
			String conditionSummary = experimentalConditionSummary.getConditionSummary(dto);
			experimentalCondition.setConditionSummary(conditionSummary);
		}

		response.setEntity(experimentalCondition);

		return response;
	}

}
