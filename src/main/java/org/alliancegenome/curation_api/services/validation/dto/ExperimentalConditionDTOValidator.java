package org.alliancegenome.curation_api.services.validation.dto;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.ExperimentalConditionDAO;
import org.alliancegenome.curation_api.dao.ontology.AnatomicalTermDAO;
import org.alliancegenome.curation_api.dao.ontology.ChemicalTermDAO;
import org.alliancegenome.curation_api.dao.ontology.ExperimentalConditionOntologyTermDAO;
import org.alliancegenome.curation_api.dao.ontology.GoTermDAO;
import org.alliancegenome.curation_api.dao.ontology.NcbiTaxonTermDAO;
import org.alliancegenome.curation_api.dao.ontology.ZecoTermDAO;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.ontology.AnatomicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ChemicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ExperimentalConditionOntologyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ZECOTerm;
import org.alliancegenome.curation_api.model.ingest.dto.ExperimentalConditionDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurie;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.ExperimentalConditionSummary;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class ExperimentalConditionDTOValidator extends BaseDTOValidator {
	
	@Inject ExperimentalConditionDAO experimentalConditionDAO;
	@Inject ZecoTermDAO zecoTermDAO;
	@Inject ChemicalTermDAO chemicalTermDAO;
	@Inject AnatomicalTermDAO anatomicalTermDAO;
	@Inject NcbiTaxonTermDAO ncbiTaxonTermDAO;
	@Inject GoTermDAO goTermDAO;
	@Inject ExperimentalConditionOntologyTermDAO experimentalConditionOntologyTermDAO;
	@Inject ExperimentalConditionSummary experimentalConditionSummary;
	@Inject PersonService personService;
	
	public ObjectResponse<ExperimentalCondition> validateExperimentalConditionDTO(ExperimentalConditionDTO dto) {
		ObjectResponse<ExperimentalCondition> ecResponse = new ObjectResponse<ExperimentalCondition>();
		
		String uniqueId = DiseaseAnnotationCurie.getExperimentalConditionCurie(dto);

		ExperimentalCondition experimentalCondition;
		SearchResponse<ExperimentalCondition> searchResponse = experimentalConditionDAO.findByField("uniqueId", uniqueId);
		if (searchResponse == null || searchResponse.getSingleResult() == null) {
			experimentalCondition = new ExperimentalCondition();
			experimentalCondition.setUniqueId(uniqueId);
		} else {
			experimentalCondition = searchResponse.getSingleResult();
		}
		
		ObjectResponse<ExperimentalCondition> aoResponse = validateAuditedObjectDTO(experimentalCondition, dto);
		experimentalCondition = aoResponse.getEntity();
		ecResponse.addErrorMessages(aoResponse.getErrorMessages());
		
		ChemicalTerm conditionChemical = null;
		if (StringUtils.isNotBlank(dto.getConditionChemicalCurie())) {
			conditionChemical = chemicalTermDAO.find(dto.getConditionChemicalCurie());
			if (conditionChemical == null)
				ecResponse.addErrorMessage("condition_chemical_curie", ValidationConstants.INVALID_MESSAGE);
		}
		experimentalCondition.setConditionChemical(conditionChemical);
		
		ExperimentalConditionOntologyTerm conditionId = null;
		if (StringUtils.isNotBlank(dto.getConditionIdCurie())) {
			conditionId = experimentalConditionOntologyTermDAO.find(dto.getConditionIdCurie());
			if (conditionId == null)
				ecResponse.addErrorMessage("condition_id_curie", ValidationConstants.INVALID_MESSAGE);
		}
		experimentalCondition.setConditionId(conditionId);

		if (StringUtils.isNotBlank(dto.getConditionClassCurie())) {
			ZECOTerm term = zecoTermDAO.find(dto.getConditionClassCurie());
			if (term == null || term.getSubsets().isEmpty() || !term.getSubsets().contains(OntologyConstants.ZECO_AGR_SLIM_SUBSET))
				ecResponse.addErrorMessage("condition_class_curie", ValidationConstants.INVALID_MESSAGE);
			experimentalCondition.setConditionClass(term);
		}
		else {
			ecResponse.addErrorMessage("condition_class_curie", ValidationConstants.REQUIRED_MESSAGE);
		}
		
		AnatomicalTerm conditionAnatomy = null;
		if (StringUtils.isNotBlank(dto.getConditionAnatomyCurie())) {
			conditionAnatomy = anatomicalTermDAO.find(dto.getConditionAnatomyCurie());
			if (conditionAnatomy == null)
				ecResponse.addErrorMessage("condition_anatomy_curie", ValidationConstants.REQUIRED_MESSAGE);
		}
		experimentalCondition.setConditionAnatomy(conditionAnatomy);
		
		NCBITaxonTerm conditionTaxon = null;
		if (StringUtils.isNotBlank(dto.getConditionTaxonCurie())) {
			conditionTaxon = ncbiTaxonTermDAO.find(dto.getConditionTaxonCurie());
			if (conditionTaxon == null) {
				conditionTaxon = ncbiTaxonTermDAO.downloadAndSave(dto.getConditionTaxonCurie());
			}
			if (conditionTaxon == null)
				ecResponse.addErrorMessage("condition_taxon_curie", ValidationConstants.INVALID_MESSAGE);
		}
		experimentalCondition.setConditionTaxon(conditionTaxon);

		GOTerm conditionGeneOntology = null;
		if (StringUtils.isNotBlank(dto.getConditionGeneOntologyCurie())) {
			conditionGeneOntology = goTermDAO.find(dto.getConditionGeneOntologyCurie());
			if (conditionGeneOntology == null)
				ecResponse.addErrorMessage("condition_gene_ontology_curie", ValidationConstants.INVALID_MESSAGE);
		}
		experimentalCondition.setConditionGeneOntology(conditionGeneOntology);
		
		String conditionQuantity = null;
		if (StringUtils.isNotBlank(dto.getConditionQuantity()))
			conditionQuantity = dto.getConditionQuantity();
		experimentalCondition.setConditionQuantity(conditionQuantity);
		
		String conditionFreeText = null;
		if (StringUtils.isNotBlank(dto.getConditionFreeText()))
			conditionFreeText = dto.getConditionFreeText();
		experimentalCondition.setConditionFreeText(conditionFreeText);
				
		if (!ecResponse.hasErrors()) {
			String conditionSummary = experimentalConditionSummary.getConditionSummary(dto);
			experimentalCondition.setConditionSummary(conditionSummary);
		}
		
		ecResponse.setEntity(experimentalCondition);
		
		return ecResponse;
	}

}
