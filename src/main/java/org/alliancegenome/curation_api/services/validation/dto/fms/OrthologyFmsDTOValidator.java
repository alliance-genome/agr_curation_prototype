package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.orthology.GeneToGeneOrthologyGeneratedDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.KnownIssueValidationException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyGenerated;
import org.alliancegenome.curation_api.model.ingest.dto.fms.OrthologyFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class OrthologyFmsDTOValidator {

	@Inject GeneToGeneOrthologyGeneratedDAO generatedOrthologyDAO;
	@Inject GeneService geneService;
	@Inject NcbiTaxonTermService ncbiTaxonTermService;
	@Inject VocabularyTermService vocabularyTermService;

	@Transactional
	public GeneToGeneOrthologyGenerated validateOrthologyFmsDTO(OrthologyFmsDTO dto) throws ValidationException {

		ObjectResponse<GeneToGeneOrthologyGenerated> orthologyResponse = new ObjectResponse<GeneToGeneOrthologyGenerated>();

		String subjectGeneIdentifier = null;
		String objectGeneIdentifier = null;

		GeneToGeneOrthologyGenerated orthoPair = null;

		if (StringUtils.isBlank(dto.getGene1())) {
			orthologyResponse.addErrorMessage("gene1", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			subjectGeneIdentifier = convertToModCurie(dto.getGene1(), dto.getGene1Species());
		}
		if (StringUtils.isBlank(dto.getGene2())) {
			orthologyResponse.addErrorMessage("gene2", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			objectGeneIdentifier = convertToModCurie(dto.getGene2(), dto.getGene2Species());
		}

		Gene subjectGene = null;
		if (StringUtils.isNotBlank(dto.getGene1())) {
			subjectGene = geneService.findByIdentifierString(subjectGeneIdentifier);
			if (subjectGene == null) {
				//orthologyResponse.addErrorMessage("gene1", ValidationConstants.INVALID_MESSAGE + " (" + subjectGeneIdentifier + ")");
				throw new KnownIssueValidationException(ValidationConstants.UNRECOGNIZED_MESSAGE + " (" + subjectGeneIdentifier + ")");
			} else {
				if (dto.getGene1Species() == null) {
					orthologyResponse.addErrorMessage("gene1Species", ValidationConstants.REQUIRED_MESSAGE);
				} else {
					ObjectResponse<NCBITaxonTerm> taxonResponse = ncbiTaxonTermService.getByCurie("NCBITaxon:" + dto.getGene1Species());
					NCBITaxonTerm subjectTaxon = taxonResponse.getEntity();
					if (subjectTaxon == null) {
						orthologyResponse.addErrorMessage("gene1Species", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGene1Species() + ")");
					} else if (!sameGenus(subjectTaxon, subjectGene.getTaxon())) {
						orthologyResponse.addErrorMessage("gene1Species", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGene1Species() + ") for gene " + subjectGene.getCurie());
					}
				}
			}
		}

		Gene objectGene = null;
		if (StringUtils.isNotBlank(dto.getGene2())) {
			objectGene = geneService.findByIdentifierString(objectGeneIdentifier);
			if (objectGene == null) {
				//orthologyResponse.addErrorMessage("gene2", ValidationConstants.INVALID_MESSAGE + " (" + objectGeneIdentifier + ")");
				throw new KnownIssueValidationException(ValidationConstants.UNRECOGNIZED_MESSAGE + " (" + objectGeneIdentifier + ")");
			} else {
				if (dto.getGene2Species() == null) {
					orthologyResponse.addErrorMessage("gene2Species", ValidationConstants.REQUIRED_MESSAGE);
				} else {
					ObjectResponse<NCBITaxonTerm> taxonResponse = ncbiTaxonTermService.getByCurie("NCBITaxon:" + dto.getGene2Species());
					NCBITaxonTerm objectTaxon = taxonResponse.getEntity();
					if (objectTaxon == null) {
						orthologyResponse.addErrorMessage("gene2Species", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGene2Species() + ")");
					} else if (!sameGenus(objectTaxon, objectGene.getTaxon())) {
						orthologyResponse.addErrorMessage("gene2Species", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGene2Species() + ") for gene " + objectGene.getCurie());
					}
				}
			}
		}

		if (subjectGene != null && objectGene != null) {
			Map<String, Object> params = new HashMap<>();
			params.put("subjectGene.id", subjectGene.getId());
			params.put("objectGene.id", objectGene.getId());
			SearchResponse<GeneToGeneOrthologyGenerated> searchResponse = generatedOrthologyDAO.findByParams(params);
			if (searchResponse != null && searchResponse.getSingleResult() != null) {
				orthoPair = searchResponse.getSingleResult();
			}
		}

		if (orthoPair == null) {
			orthoPair = new GeneToGeneOrthologyGenerated();
		}

		orthoPair.setSubjectGene(subjectGene);
		orthoPair.setObjectGene(objectGene);

		VocabularyTerm isBestScore = null;
		if (StringUtils.isBlank(dto.getIsBestScore())) {
			orthologyResponse.addErrorMessage("isBestScore", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			isBestScore = vocabularyTermService.getTermInVocabulary(VocabularyConstants.ORTHOLOGY_BEST_SCORE_VOCABULARY, dto.getIsBestScore()).getEntity();
			if (isBestScore == null) {
				orthologyResponse.addErrorMessage("isBestScore", ValidationConstants.INVALID_MESSAGE + " (" + dto.getIsBestScore() + ")");
			}
		}
		orthoPair.setIsBestScore(isBestScore);

		VocabularyTerm isBestScoreReverse = null;
		if (StringUtils.isBlank(dto.getIsBestRevScore())) {
			orthologyResponse.addErrorMessage("isBestRevScore", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			isBestScoreReverse = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.ORTHOLOGY_BEST_REVERSE_SCORE_VOCABULARY_TERM_SET, dto.getIsBestRevScore()).getEntity();
			if (isBestScoreReverse == null) {
				orthologyResponse.addErrorMessage("isBestRevScore", ValidationConstants.INVALID_MESSAGE + " (" + dto.getIsBestRevScore() + ")");
			}
		}
		orthoPair.setIsBestScoreReverse(isBestScoreReverse);

		List<VocabularyTerm> predictionMethodsMatched = null;
		if (CollectionUtils.isNotEmpty(dto.getPredictionMethodsMatched())) {
			predictionMethodsMatched = new ArrayList<>();
			for (String methodName : dto.getPredictionMethodsMatched()) {
				VocabularyTerm method = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.ORTHOLOGY_PREDICTION_METHOD_VOCABULARY_TERM_SET, methodName).getEntity();
				if (method == null) {
					orthologyResponse.addErrorMessage("predictionMethodsMatched", ValidationConstants.INVALID_MESSAGE + " (" + methodName + ")");
				} else {
					predictionMethodsMatched.add(method);
				}
			}
		}
		orthoPair.setPredictionMethodsMatched(predictionMethodsMatched);

		List<VocabularyTerm> predictionMethodsNotMatched = null;
		if (CollectionUtils.isNotEmpty(dto.getPredictionMethodsNotMatched())) {
			predictionMethodsNotMatched = new ArrayList<>();
			for (String methodName : dto.getPredictionMethodsNotMatched()) {
				VocabularyTerm method = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.ORTHOLOGY_PREDICTION_METHOD_VOCABULARY_TERM_SET, methodName).getEntity();
				if (method == null) {
					orthologyResponse.addErrorMessage("predictionMethodsNotMatched", ValidationConstants.INVALID_MESSAGE + " (" + methodName + ")");
				} else {
					predictionMethodsNotMatched.add(method);
				}
			}
		}
		orthoPair.setPredictionMethodsNotMatched(predictionMethodsNotMatched);

		List<VocabularyTerm> predictionMethodsNotCalled = null;
		if (CollectionUtils.isNotEmpty(dto.getPredictionMethodsNotCalled())) {
			predictionMethodsNotCalled = new ArrayList<>();
			for (String methodName : dto.getPredictionMethodsNotCalled()) {
				VocabularyTerm method = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.ORTHOLOGY_PREDICTION_METHOD_VOCABULARY_TERM_SET, methodName).getEntity();
				if (method == null) {
					orthologyResponse.addErrorMessage("predictionMethodsNotCalled", ValidationConstants.INVALID_MESSAGE + " (" + methodName + ")");
				} else {
					predictionMethodsNotCalled.add(method);
				}
			}
		}
		orthoPair.setPredictionMethodsNotCalled(predictionMethodsNotCalled);

		VocabularyTerm confidence = null;
		if (StringUtils.isBlank(dto.getConfidence())) {
			orthologyResponse.addErrorMessage("confidence", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			confidence = vocabularyTermService.getTermInVocabulary(VocabularyConstants.HOMOLOGY_CONFIDENCE_VOCABULARY, dto.getConfidence()).getEntity();
			if (confidence == null) {
				orthologyResponse.addErrorMessage("confidence", ValidationConstants.INVALID_MESSAGE + " (" + dto.getConfidence() + ")");
			}
		}
		orthoPair.setConfidence(confidence);

		orthoPair.setModerateFilter(dto.getModerateFilter());

		orthoPair.setStrictFilter(dto.getStrictFilter());

		orthoPair.setObsolete(false);
		orthoPair.setInternal(false);

		if (orthologyResponse.hasErrors()) {
			throw new ObjectValidationException(dto, orthologyResponse.errorMessagesString());
		}

		return generatedOrthologyDAO.persist(orthoPair);

	}

	private boolean sameGenus(NCBITaxonTerm taxon, NCBITaxonTerm geneTaxon) {
		if (StringUtils.equals(taxon.getCurie(), "NCBITaxon:8355") || StringUtils.equals(taxon.getCurie(), "NCBITaxon:8364")) {
			// Must be same species for Xenopus as cleanup uses taxon curie
			if (StringUtils.equals(taxon.getCurie(), geneTaxon.getCurie())) {
				return true;
			}
			return false;
		}
		String genus = taxon.getName().substring(0, taxon.getName().indexOf(" "));
		String geneGenus = geneTaxon.getName().substring(0, geneTaxon.getName().indexOf(" "));
		if (StringUtils.equals(genus, geneGenus)) {
			return true;
		}
		return false;
	}

	private String convertToModCurie(String curie, Integer taxonId) {
		curie = curie.replaceFirst("^DRSC:", "");
		if (curie.indexOf(":") == -1) {
			String prefix = BackendBulkDataProvider.getCuriePrefixFromTaxonId(taxonId);
			if (prefix != null) {
				curie = prefix + curie;
			}
		}

		return curie;
	}

}
