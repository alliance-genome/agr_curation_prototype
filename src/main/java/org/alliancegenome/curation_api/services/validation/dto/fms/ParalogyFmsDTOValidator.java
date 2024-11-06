package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.GeneToGeneParalogyDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneToGeneParalogy;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.ingest.dto.fms.ParalogyFmsDTO;
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
public class ParalogyFmsDTOValidator {

	@Inject GeneToGeneParalogyDAO genetoGeneParalogyDAO;
	@Inject GeneService geneService;
	@Inject NcbiTaxonTermService ncbiTaxonTermService;
	@Inject VocabularyTermService vocabularyTermService;

	@Transactional
	public GeneToGeneParalogy validateParalogyFmsDTO(ParalogyFmsDTO dto) throws ValidationException {

		ObjectResponse<GeneToGeneParalogy> paralogyResponse = new ObjectResponse<GeneToGeneParalogy>();

		String subjectGeneIdentifier = null;
		String objectGeneIdentifier = null;

		GeneToGeneParalogy paralogyData = null;

		NCBITaxonTerm speciesTaxon = null;

		if (StringUtils.isBlank(dto.getGene1())) {
			paralogyResponse.addErrorMessage("gene1", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			subjectGeneIdentifier = convertToModCurie(dto.getGene1(), dto.getSpecies());
		}
		if (StringUtils.isBlank(dto.getGene2())) {
			paralogyResponse.addErrorMessage("gene2", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			objectGeneIdentifier = convertToModCurie(dto.getGene2(), dto.getSpecies());
		}

		if (dto.getSpecies() == null) {
			paralogyResponse.addErrorMessage("Species", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<NCBITaxonTerm> taxonResponse = ncbiTaxonTermService.getByCurie("NCBITaxon:" + dto.getSpecies());
			speciesTaxon = taxonResponse.getEntity();
			if (speciesTaxon == null) {
				paralogyResponse.addErrorMessage("Species", ValidationConstants.INVALID_MESSAGE + " (" + dto.getSpecies() + ")");
			}
		}

		Gene subjectGene = null;
		if (StringUtils.isNotBlank(dto.getGene1())) {
			subjectGene = geneService.findByIdentifierString(subjectGeneIdentifier);
			if (subjectGene == null) {
				paralogyResponse.addErrorMessage("gene1", ValidationConstants.INVALID_MESSAGE + " (" + subjectGeneIdentifier + ")");
			} else {
				if (!sameGenus(speciesTaxon, subjectGene.getTaxon())) {
					paralogyResponse.addErrorMessage("Species", ValidationConstants.INVALID_MESSAGE + " (" + dto.getSpecies() + ") for gene " + subjectGene.getCurie());
				}
			}
		}

		Gene objectGene = null;
		if (StringUtils.isNotBlank(dto.getGene2())) {
			objectGene = geneService.findByIdentifierString(objectGeneIdentifier);
			if (objectGene == null) {
				paralogyResponse.addErrorMessage("gene2", ValidationConstants.INVALID_MESSAGE + " (" + objectGeneIdentifier + ")");
			} else {
				if (!sameGenus(speciesTaxon, objectGene.getTaxon())) {
					paralogyResponse.addErrorMessage("Species", ValidationConstants.INVALID_MESSAGE + " (" + dto.getSpecies() + ") for gene " + objectGene.getCurie());
				}
			}
		}

		if (subjectGene != null && objectGene != null) {
			Map<String, Object> params = new HashMap<>();
			params.put("subjectGene.id", subjectGene.getId());
			params.put("objectGene.id", objectGene.getId());
			SearchResponse<GeneToGeneParalogy> searchResponse = genetoGeneParalogyDAO.findByParams(params);
			if (searchResponse != null && searchResponse.getSingleResult() != null) {
				paralogyData = searchResponse.getSingleResult();
			}
		}

		if (paralogyData == null) {
			paralogyData = new GeneToGeneParalogy();
		}

		paralogyData.setSubjectGene(subjectGene);
		paralogyData.setObjectGene(objectGene);

		List<VocabularyTerm> predictionMethodsMatched = null;
		if (CollectionUtils.isNotEmpty(dto.getPredictionMethodsMatched())) {
			predictionMethodsMatched = new ArrayList<>();
			for (String methodName : dto.getPredictionMethodsMatched()) {
				VocabularyTerm method = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.PARALOGY_PREDICTION_METHOD_VOCABULARY_TERM_SET, methodName).getEntity();
				if (method == null) {
					paralogyResponse.addErrorMessage("predictionMethodsMatched", ValidationConstants.INVALID_MESSAGE + " (" + methodName + ")");
				} else {
					predictionMethodsMatched.add(method);
				}
			}
		}
		paralogyData.setPredictionMethodsMatched(predictionMethodsMatched);

		List<VocabularyTerm> predictionMethodsNotMatched = null;
		if (CollectionUtils.isNotEmpty(dto.getPredictionMethodsNotMatched())) {
			predictionMethodsNotMatched = new ArrayList<>();
			for (String methodName : dto.getPredictionMethodsNotMatched()) {
				VocabularyTerm method = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.PARALOGY_PREDICTION_METHOD_VOCABULARY_TERM_SET, methodName).getEntity();
				if (method == null) {
					paralogyResponse.addErrorMessage("predictionMethodsNotMatched", ValidationConstants.INVALID_MESSAGE + " (" + methodName + ")");
				} else {
					predictionMethodsNotMatched.add(method);
				}
			}
		}
		paralogyData.setPredictionMethodsNotMatched(predictionMethodsNotMatched);

		List<VocabularyTerm> predictionMethodsNotCalled = null;
		if (CollectionUtils.isNotEmpty(dto.getPredictionMethodsNotCalled())) {
			predictionMethodsNotCalled = new ArrayList<>();
			for (String methodName : dto.getPredictionMethodsNotCalled()) {
				VocabularyTerm method = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.PARALOGY_PREDICTION_METHOD_VOCABULARY_TERM_SET, methodName).getEntity();
				if (method == null) {
					paralogyResponse.addErrorMessage("predictionMethodsNotCalled", ValidationConstants.INVALID_MESSAGE + " (" + methodName + ")");
				} else {
					predictionMethodsNotCalled.add(method);
				}
			}
		}
		paralogyData.setPredictionMethodsNotCalled(predictionMethodsNotCalled);

		VocabularyTerm confidence = null;
		if (StringUtils.isBlank(dto.getConfidence())) {
			paralogyResponse.addErrorMessage("confidence", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			confidence = vocabularyTermService.getTermInVocabulary(VocabularyConstants.HOMOLOGY_CONFIDENCE_VOCABULARY, dto.getConfidence()).getEntity();
			if (confidence == null) {
				paralogyResponse.addErrorMessage("confidence", ValidationConstants.INVALID_MESSAGE + " (" + dto.getConfidence() + ")");
			}
		}
		paralogyData.setConfidence(confidence);

		if (dto.getSimilarity() != null) {
			paralogyData.setSimilarity(dto.getSimilarity());
		}
		if (dto.getIdentity() != null) {
			paralogyData.setIdentity(dto.getIdentity());
		}
		if (dto.getLength() != null) {
			paralogyData.setLength(dto.getLength());
		}
		if (dto.getRank() == null) {
			paralogyResponse.addErrorMessage("Rank", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			paralogyData.setRank(dto.getRank());
		}

		paralogyData.setObsolete(false);
		paralogyData.setInternal(false);

		if (paralogyResponse.hasErrors()) {
			throw new ObjectValidationException(dto, paralogyResponse.errorMessagesString());
		}

		return genetoGeneParalogyDAO.persist(paralogyData);
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

}
