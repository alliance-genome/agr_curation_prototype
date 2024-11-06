package org.alliancegenome.curation_api.services.validation.dto.fms;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.PredictedVariantConsequenceDAO;
import org.alliancegenome.curation_api.exceptions.KnownIssueValidationException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.PredictedVariantConsequence;
import org.alliancegenome.curation_api.model.entities.Transcript;
import org.alliancegenome.curation_api.model.entities.associations.variantAssociations.CuratedVariantGenomicLocationAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.VepTxtDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.TranscriptService;
import org.alliancegenome.curation_api.services.associations.variantAssociations.CuratedVariantGenomicLocationAssociationService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class VepGeneFmsDTOValidator {

	@Inject PredictedVariantConsequenceDAO predictedVariantConsequenceDAO;
	@Inject CuratedVariantGenomicLocationAssociationService cvglaService;
	@Inject TranscriptService transcriptService;
	
	public Long validateGeneLevelConsequence(VepTxtDTO dto) throws ValidationException {
		ObjectResponse<PredictedVariantConsequence> response = new ObjectResponse<>();
		PredictedVariantConsequence predictedVariantConsequence = null;

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
			SearchResponse<Transcript> searchResponse = transcriptService.findByField("transcriptId", dto.getFeature());
			if (searchResponse == null || searchResponse.getSingleResult() == null) {
				response.addErrorMessage("feature", ValidationConstants.INVALID_MESSAGE + " (" + dto.getFeature() + ")");
			} else if (searchResponse.getReturnedRecords() > 1) {
				response.addErrorMessage("feature", ValidationConstants.AMBIGUOUS_MESSAGE + " (" + dto.getFeature() + ")");
			} else {
				transcript = searchResponse.getSingleResult();
			}
		}
		
		if (variantLocation != null && CollectionUtils.isNotEmpty(variantLocation.getPredictedVariantConsequences()) && transcript != null) {
			for (PredictedVariantConsequence existingPvc : variantLocation.getPredictedVariantConsequences()) {
				if (transcript.getId() == existingPvc.getVariantTranscript().getId()) {
					predictedVariantConsequence = existingPvc;
					break;
				}
			}
		}

		if (predictedVariantConsequence == null) {
			response.addErrorMessage("uploadedVariant / feature", ValidationConstants.INVALID_MESSAGE + " ("
					+ dto.getUploadedVariation() + " / " + dto.getFeature() + ")");
		} else {
			predictedVariantConsequence.setGeneLevelConsequence(true);
		}
		
		if (response.hasErrors()) {
			throw new ObjectValidationException(dto, response.errorMessagesString());
		}
		
		predictedVariantConsequence = predictedVariantConsequenceDAO.persist(predictedVariantConsequence);
			
		return predictedVariantConsequence.getId();
	}
}
