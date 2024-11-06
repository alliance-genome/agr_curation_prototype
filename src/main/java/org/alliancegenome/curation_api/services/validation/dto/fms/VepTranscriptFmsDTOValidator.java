package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.PredictedVariantConsequenceDAO;
import org.alliancegenome.curation_api.dao.TranscriptDAO;
import org.alliancegenome.curation_api.dao.associations.variantAssociations.CuratedVariantGenomicLocationAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.KnownIssueValidationException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.PredictedVariantConsequence;
import org.alliancegenome.curation_api.model.entities.Transcript;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.variantAssociations.CuratedVariantGenomicLocationAssociation;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.ingest.dto.fms.VepTxtDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.associations.variantAssociations.CuratedVariantGenomicLocationAssociationService;
import org.alliancegenome.curation_api.services.ontology.SoTermService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class VepTranscriptFmsDTOValidator {

	@Inject PredictedVariantConsequenceDAO predictedVariantConsequenceDAO;
	@Inject CuratedVariantGenomicLocationAssociationDAO cvglaDAO;
	@Inject CuratedVariantGenomicLocationAssociationService cvglaService;
	@Inject TranscriptDAO transcriptDAO;
	@Inject VocabularyTermService vocabularyTermService;
	@Inject SoTermService soTermService;
	
	private static final Pattern PATHOGENICITY_PREDICTION_RESULT = Pattern.compile("^([\\w]+)\\(([\\d\\.]+)\\)$");
	private static final Pattern POSITION_STRING = Pattern.compile("^[\\d\\?\\-]+$");
	
	public PredictedVariantConsequence validateTranscriptLevelConsequence(VepTxtDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		ObjectResponse<PredictedVariantConsequence> response = new ObjectResponse<>();
		PredictedVariantConsequence predictedVariantConsequence = new PredictedVariantConsequence();

		CuratedVariantGenomicLocationAssociation variantLocation = null;
		if (StringUtils.isBlank(dto.getUploadedVariation())) {
			response.addErrorMessage("uploadedVariant", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			SearchResponse<CuratedVariantGenomicLocationAssociation> cvglaResponse = cvglaService.findByField("hgvs", dto.getUploadedVariation());
			if (cvglaResponse != null && cvglaResponse.getSingleResult() != null) {
				variantLocation = cvglaResponse.getSingleResult();
			} else {
				response.addErrorMessage("uploadedVariant", ValidationConstants.INVALID_MESSAGE + " (" + dto.getUploadedVariation() + ")");
			}
		}
		
		Transcript transcript = null;
		if (StringUtils.isBlank(dto.getFeature())) {
			if (dto.getConsequence().contains("intergenic_variant")) {
				throw new KnownIssueValidationException("Intergenic variant consequences not currently supported");
			}
			response.addErrorMessage("feature", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			HashMap<String, Object> params = new HashMap<>();
			params.put("transcriptId", dto.getFeature());
			params.put("obsolete", false);
			
			SearchResponse<Transcript> searchResponse = transcriptDAO.findByParams(params);
			if (searchResponse == null || searchResponse.getSingleResult() == null) {
				response.addErrorMessage("feature", ValidationConstants.INVALID_MESSAGE + " (" + dto.getFeature() + ")");
			} else if (searchResponse.getReturnedRecords() > 1) {
				response.addErrorMessage("feature", ValidationConstants.AMBIGUOUS_MESSAGE + " (" + dto.getFeature() + ")");
			} else {
				transcript = searchResponse.getSingleResult();
			}
		}
		
		Boolean isUpdate = false;
		if (variantLocation != null && CollectionUtils.isNotEmpty(variantLocation.getPredictedVariantConsequences()) && transcript != null) {
			for (PredictedVariantConsequence existingPvc : variantLocation.getPredictedVariantConsequences()) {
				if (transcript.getId() == existingPvc.getVariantTranscript().getId()) {
					predictedVariantConsequence = existingPvc;
					isUpdate = true;
					break;
				}
			}
		}
		
		predictedVariantConsequence.setVariantGenomicLocation(variantLocation);
		predictedVariantConsequence.setVariantTranscript(transcript);

		Map<String, String> attributes = getExtraAttributes(dto);
		
		VocabularyTerm vepImpact = null;
		if (!attributes.containsKey("IMPACT")) {
			response.addErrorMessage("extra - IMPACT", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			vepImpact = vocabularyTermService.getTermInVocabulary(VocabularyConstants.VEP_IMPACT_VOCABULARY, attributes.get("IMPACT")).getEntity();
			if (vepImpact == null) {
				response.addErrorMessage("extra - IMPACT", ValidationConstants.INVALID_MESSAGE + " (" + attributes.get("IMPACT") + ")");
			}
		}
		predictedVariantConsequence.setVepImpact(vepImpact);
		
		List<SOTerm> vepConsequences = null;
		if (StringUtils.isBlank(dto.getConsequence())) {
			response.addErrorMessage("consequence", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			vepConsequences = new ArrayList<>();
			for (String consequence : dto.getConsequence().split(",")) {
				SearchResponse<SOTerm> soTermResponse = soTermService.findByField("name", consequence);
				SOTerm vepConsequence = null;
				if (soTermResponse != null && soTermResponse.getSingleResult() != null
						&& vocabularyTermService.getTermInVocabulary(VocabularyConstants.VEP_CONSEQUENCE_VOCABULARY, consequence).getEntity() != null) {
					vepConsequence = soTermResponse.getSingleResult();
				}
				if (vepConsequence == null) {
					response.addErrorMessage("consequence", ValidationConstants.INVALID_MESSAGE + " (" + consequence + ")");
					break;
				} else {
					vepConsequences.add(vepConsequence);
				}
			}
		}
		predictedVariantConsequence.setVepConsequences(vepConsequences);
		
		String hgvsCodingNomenclature = null;
		if (attributes.containsKey("HGVSc")) {
			hgvsCodingNomenclature = attributes.get("HGVSc");
		}
		predictedVariantConsequence.setHgvsCodingNomenclature(hgvsCodingNomenclature);
		
		String hgvsProteinNomenclature = null;
		if (attributes.containsKey("HGVSp")) {
			hgvsProteinNomenclature = attributes.get("HGVSp");
		}
		predictedVariantConsequence.setHgvsProteinNomenclature(hgvsProteinNomenclature);
			
		String referenceCodon = null;
		String variantCodon = null;
		if (StringUtils.isNotBlank(dto.getCodons())) {
			String[] refVarCodons = dto.getCodons().split("/");
			if (refVarCodons.length == 1 && dto.getConsequence().contains("synonymous_variant")) {
				referenceCodon = dto.getCodons();
				variantCodon = dto.getCodons();
			} else if (refVarCodons.length == 2) {
				referenceCodon = refVarCodons[0];
				variantCodon = refVarCodons[1];
			} else {
				response.addErrorMessage("codons", ValidationConstants.INVALID_MESSAGE + " (" + dto.getCodons() + ")");
			}
		}
		predictedVariantConsequence.setCodonReference(referenceCodon);
		predictedVariantConsequence.setCodonVariant(variantCodon);
		
		String referenceAminoAcids = null;
		String variantAminoAcids = null;
		if (StringUtils.isNotBlank(dto.getAminoAcids())) {
			String[] refVarAminoAcids = dto.getAminoAcids().split("/");
			if (refVarAminoAcids.length == 1 && (Objects.equals(dto.getAminoAcids(), "X") || dto.getConsequence().contains("synonymous_variant"))) {
				referenceAminoAcids = refVarAminoAcids[0];
				variantAminoAcids = refVarAminoAcids[0];
			} else if (refVarAminoAcids.length == 2) {
				referenceAminoAcids = refVarAminoAcids[0];
				variantAminoAcids = refVarAminoAcids[1];
			} else {
				response.addErrorMessage("aminoAcids", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAminoAcids() + ")");
			}
		}
		predictedVariantConsequence.setAminoAcidReference(referenceAminoAcids);
		predictedVariantConsequence.setAminoAcidVariant(variantAminoAcids);
		
		VocabularyTerm polyphenPrediction = null;
		Float polyphenScore = null;
		if (attributes.containsKey("PolyPhen")) {
			Pair<VocabularyTerm, Float> polyphenResult = parsePathogenicityPredictionScore(attributes.get("PolyPhen"), VocabularyConstants.POLYPHEN_PREDICTION_VOCABULARY);
			if (polyphenResult == null) {
				response.addErrorMessage("extra - PolyPhen", ValidationConstants.INVALID_MESSAGE + " (" + attributes.get("PolyPhen") + ")");
			
			} else {
				polyphenPrediction = polyphenResult.getLeft();
				polyphenScore = polyphenResult.getRight();
			}
		}
		predictedVariantConsequence.setPolyphenPrediction(polyphenPrediction);
		predictedVariantConsequence.setPolyphenScore(polyphenScore);
		
		VocabularyTerm siftPrediction = null;
		Float siftScore = null;
		if (attributes.containsKey("SIFT")) {
			Pair<VocabularyTerm, Float> siftResult = parsePathogenicityPredictionScore(attributes.get("SIFT"), VocabularyConstants.SIFT_PREDICTION_VOCABULARY);
			if (siftResult == null) {
				response.addErrorMessage("extra - SIFT", ValidationConstants.INVALID_MESSAGE + " (" + attributes.get("SIFT") + ")");
			
			} else {
				siftPrediction = siftResult.getLeft();
				siftScore = siftResult.getRight();
			}
		}
		predictedVariantConsequence.setSiftPrediction(siftPrediction);
		predictedVariantConsequence.setSiftScore(siftScore);
		
		Integer cdnaStart = null;
		Integer cdnaEnd = null;
		if (StringUtils.isNotBlank(dto.getCdnaPosition())) {
			Pair<Integer, Integer> cdnaStartEnd = parseStartEnd(dto.getCdnaPosition());
			if (cdnaStartEnd == null) {
				response.addErrorMessage("cdnaPosition", ValidationConstants.INVALID_MESSAGE + " (" + dto.getCdnaPosition() + ")");
			} else {
				cdnaStart = cdnaStartEnd.getLeft();
				cdnaEnd = cdnaStartEnd.getRight();
			}
		}
		predictedVariantConsequence.setCalculatedCdnaStart(cdnaStart);
		predictedVariantConsequence.setCalculatedCdnaEnd(cdnaEnd);
		
		Integer cdsStart = null;
		Integer cdsEnd = null;
		if (StringUtils.isNotBlank(dto.getCdsPosition())) {
			Pair<Integer, Integer> cdsStartEnd = parseStartEnd(dto.getCdsPosition());
			if (cdsStartEnd == null) {
				response.addErrorMessage("cdsPosition", ValidationConstants.INVALID_MESSAGE + " (" + dto.getCdsPosition() + ")");
			} else {
				cdsStart = cdsStartEnd.getLeft();
				cdsEnd = cdsStartEnd.getRight();
			}
		}
		predictedVariantConsequence.setCalculatedCdsStart(cdsStart);
		predictedVariantConsequence.setCalculatedCdsEnd(cdsEnd);
		
		Integer proteinStart = null;
		Integer proteinEnd = null;
		if (StringUtils.isNotBlank(dto.getProteinPosition())) {
			Pair<Integer, Integer> proteinStartEnd = parseStartEnd(dto.getProteinPosition());
			if (proteinStartEnd == null) {
				response.addErrorMessage("proteinPosition", ValidationConstants.INVALID_MESSAGE + " (" + dto.getProteinPosition() + ")");
			} else {
				proteinStart = proteinStartEnd.getLeft();
				proteinEnd = proteinStartEnd.getRight();
			}
		}
		predictedVariantConsequence.setCalculatedProteinStart(proteinStart);
		predictedVariantConsequence.setCalculatedProteinEnd(proteinEnd);
		
		if (response.hasErrors()) {
			throw new ObjectValidationException(dto, response.errorMessagesString());
		}
		
		predictedVariantConsequence = predictedVariantConsequenceDAO.persist(predictedVariantConsequence);
			
		if (!isUpdate) {
			if (variantLocation.getPredictedVariantConsequences() == null) {
				variantLocation.setPredictedVariantConsequences(new ArrayList<>());
			}
			variantLocation.getPredictedVariantConsequences().add(predictedVariantConsequence);
		}
		cvglaDAO.persist(variantLocation);
		
		return predictedVariantConsequence;
	}
	
	private Map<String, String> getExtraAttributes(VepTxtDTO dto) {
		Map<String, String> attributes = new HashMap<String, String>();
		if (CollectionUtils.isNotEmpty(dto.getExtra())) {
			for (String keyValue : dto.getExtra()) {
				String[] parts = keyValue.split("=");
				if (parts.length == 2) {
					attributes.put(parts[0], parts[1]);
				}
			}
		}
		
		return attributes;
	}
	
	private Pair<VocabularyTerm, Float> parsePathogenicityPredictionScore(String result, String vocabularyName) {
		
		if (StringUtils.isBlank(result)) {
			return null;
		}
		
		Matcher matcher = PATHOGENICITY_PREDICTION_RESULT.matcher(result);
		if (!matcher.find()) {
			return null;
		}
		
		VocabularyTerm consequence = vocabularyTermService.getTermInVocabulary(vocabularyName, matcher.group(1)).getEntity();
		if (consequence == null) {
			return null;
		}
		
		ImmutablePair<VocabularyTerm, Float> parsedResult = new ImmutablePair<>(consequence, Float.parseFloat(matcher.group(2)));
		
		return parsedResult;
	}
	
	private Pair<Integer, Integer> parseStartEnd(String position) {
		Matcher matcher = POSITION_STRING.matcher(position);
		if (!matcher.find()) {
			return null;
		}
		
		Integer start = null;
		Integer end = null;
		String[] positions = position.split("-");
		
		if (positions.length > 2) {
			return null;
		}
		
		if (positions.length == 1) {
			start = Integer.parseInt(position);
			end = start;
		} else {
			if (!Objects.equals("?", positions[0])) {
				start = Integer.parseInt(positions[0]);
			}
			if (!Objects.equals("?", positions[1])) {
				end = Integer.parseInt(positions[1]);
			}
		}
		
		ImmutablePair<Integer, Integer> startEnd = new ImmutablePair<>(start, end);
		
		return startEnd;
	}
}
