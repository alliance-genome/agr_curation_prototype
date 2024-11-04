package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.PredictedVariantConsequenceDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.PredictedVariantConsequence;
import org.alliancegenome.curation_api.model.ingest.dto.fms.VepTxtDTO;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.VepGeneFmsDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.fms.VepTranscriptFmsDTOValidator;
import org.apache.commons.lang.StringUtils;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class PredictedVariantConsequenceService extends BaseEntityCrudService<PredictedVariantConsequence, PredictedVariantConsequenceDAO> implements BaseUpsertServiceInterface<PredictedVariantConsequence, VepTxtDTO> {

	@Inject PredictedVariantConsequenceDAO predictedVariantConsequenceDAO;
	@Inject VepTranscriptFmsDTOValidator vepTranscriptFmsDtoValidator;
	@Inject VepGeneFmsDTOValidator vepGeneFmsDtoValidator;
	@Inject PersonService personService;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(predictedVariantConsequenceDAO);
	}

	public List<Long> getIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put("variantTranscript." + EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
		if (StringUtils.equals(dataProvider.sourceOrganization, "RGD")) {
			params.put("variantTranscript." + EntityFieldConstants.TAXON, dataProvider.canonicalTaxonCurie);
		}
		List<Long> ids = predictedVariantConsequenceDAO.findIdsByParams(params);
		ids.removeIf(Objects::isNull);
		return ids;
	}

	public List<Long> getGeneLevelIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put("variantTranscript." + EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
		if (StringUtils.equals(dataProvider.sourceOrganization, "RGD")) {
			params.put("variantTranscript." + EntityFieldConstants.TAXON, dataProvider.canonicalTaxonCurie);
		}
		params.put("geneLevelConsequence", true);
		List<Long> ids = predictedVariantConsequenceDAO.findIdsByParams(params);
		ids.removeIf(Objects::isNull);
		return ids;
	}

	@Override
	@Transactional
	public PredictedVariantConsequence upsert(VepTxtDTO dto, BackendBulkDataProvider dataProvider)
			throws ValidationException {
		return vepTranscriptFmsDtoValidator.validateTranscriptLevelConsequence(dto, dataProvider);
	}

	@Transactional
	public Long updateGeneLevelConsequence(VepTxtDTO dto) throws ValidationException {
		return vepGeneFmsDtoValidator.validateGeneLevelConsequence(dto);
	}
	
	@Transactional
	public PredictedVariantConsequence resetGeneLevelConsequence(Long id, String requestSource) {
		PredictedVariantConsequence pvc = predictedVariantConsequenceDAO.find(id);
		
		if (pvc == null) {
			String errorMessage = "Could not find PredictedVariantConsequence with id: " + id;
			Log.error(errorMessage);
			return null;
		}

		if (pvc.getGeneLevelConsequence()) {
			pvc.setGeneLevelConsequence(true);
			if (authenticatedPerson.getUniqueId() != null) {
				requestSource = authenticatedPerson.getUniqueId();
			}
			Person updatedBy = personService.fetchByUniqueIdOrCreate(requestSource);
				pvc.setUpdatedBy(updatedBy);
				pvc.setDateUpdated(OffsetDateTime.now());
			return predictedVariantConsequenceDAO.persist(pvc);
		}
		
		return pvc;
	}
}
