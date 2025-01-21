package org.alliancegenome.curation_api.services.validation.dto;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.SynonymDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.agmSlotAnnotations.AgmSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AffectedGenomicModelDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.SecondaryIdSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.slotAnnotations.SlotAnnotationIdentityHelper;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations.AgmSecondaryIdSlotAnnotationDTOValidator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestScoped
public class AffectedGenomicModelDTOValidator extends BaseDTOValidator {

	@Inject
	AffectedGenomicModelDAO affectedGenomicModelDAO;
	@Inject
	VocabularyTermService vocabularyTermService;
	@Inject
	SynonymDAO synonymDAO;
	@Inject
	SlotAnnotationIdentityHelper identityHelper;
	@Inject
	AgmSecondaryIdSlotAnnotationDTOValidator agmSecondaryIdDtoValidator;

	private final ObjectResponse<AffectedGenomicModel> agmResponse = new ObjectResponse<>();

	public AffectedGenomicModel validateAffectedGenomicModelDTO(AffectedGenomicModelDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {

		AffectedGenomicModel agm = null;
		if (StringUtils.isNotBlank(dto.getPrimaryExternalId())) {
			SearchResponse<AffectedGenomicModel> response = affectedGenomicModelDAO.findByField("primaryExternalId", dto.getPrimaryExternalId());
			if (response != null && response.getSingleResult() != null) {
				agm = response.getSingleResult();
			}
		} else {
			agmResponse.addErrorMessage("primaryExternalId", ValidationConstants.REQUIRED_MESSAGE);
		}

		if (agm == null) {
			agm = new AffectedGenomicModel();
		}

		agm.setPrimaryExternalId(dto.getPrimaryExternalId());
		agm.setModInternalId(handleStringField(dto.getModInternalId()));
		agm.setName(handleStringField(dto.getName()));
		if (CollectionUtils.isNotEmpty(dto.getSynonyms())) {
			List<String> existingSynonyms = agm.getSynonyms();
			if (CollectionUtils.isEmpty(existingSynonyms)) {
				existingSynonyms = new ArrayList<>();
			}
			// remove synonyms that are no longer in the submitted file
			List<String> toBeRemoved = existingSynonyms.stream()
				.filter(synonym -> !dto.getSynonyms().contains(synonym))
				.toList();
			existingSynonyms.removeIf(toBeRemoved::contains);
			// add missing synonyms
			final List<String> synonymStrings = existingSynonyms;
			List<String> toBeAdded = dto.getSynonyms().stream()
				.filter(synonym -> !synonymStrings.contains(synonym))
				.toList();
			existingSynonyms.addAll(toBeAdded);
			agm.setSynonyms(existingSynonyms);
		} else {
			agm.setSynonyms(null);
		}

		List<AgmSecondaryIdSlotAnnotation> secondaryIds = validateAgmSecondaryIds(agm, dto);
		if (agm.getAgmSecondaryIds() != null) {
			agm.getAgmSecondaryIds().clear();
		}
		if (secondaryIds != null) {
			if (agm.getAgmSecondaryIds() == null) {
				agm.setAgmSecondaryIds(new ArrayList<>());
			}
			agm.getAgmSecondaryIds().addAll(secondaryIds);
		}


		ObjectResponse<AffectedGenomicModel> geResponse = validateGenomicEntityDTO(agm, dto, dataProvider);
		agmResponse.addErrorMessages(geResponse.getErrorMessages());

		VocabularyTerm subtype = null;
		if (StringUtils.isBlank(dto.getSubtypeName())) {
			agmResponse.addErrorMessage("subtype_name", ValidationConstants.REQUIRED_MESSAGE);

		} else {
			subtype = vocabularyTermService.getTermInVocabulary(VocabularyConstants.AGM_SUBTYPE_VOCABULARY, dto.getSubtypeName()).getEntity();
			if (subtype == null) {
				agmResponse.addErrorMessage("subtype_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getSubtypeName() + ")");
			}
		}
		agm.setSubtype(subtype);

		agmResponse.convertErrorMessagesToMap();

		if (agmResponse.hasErrors()) {
			throw new ObjectValidationException(dto, agmResponse.errorMessagesString());
		}

		return agm;
	}

	private List<AgmSecondaryIdSlotAnnotation> validateAgmSecondaryIds(AffectedGenomicModel model, AffectedGenomicModelDTO dto) {
		String field = "agm_secondary_id_dtos";

		Map<String, AgmSecondaryIdSlotAnnotation> existingSecondaryIds = new HashMap<>();
		if (CollectionUtils.isNotEmpty(model.getAgmSecondaryIds())) {
			for (AgmSecondaryIdSlotAnnotation existingSecondaryId : model.getAgmSecondaryIds()) {
				existingSecondaryIds.put(SlotAnnotationIdentityHelper.secondaryIdIdentity(existingSecondaryId), existingSecondaryId);
			}
		}

		List<AgmSecondaryIdSlotAnnotation> validatedSecondaryIds = new ArrayList<>();
		boolean allValid = true;
		if (CollectionUtils.isNotEmpty(dto.getAgmSecondaryIdDtos())) {
			for (int ix = 0; ix < dto.getAgmSecondaryIdDtos().size(); ix++) {
				SecondaryIdSlotAnnotationDTO sidDto = dto.getAgmSecondaryIdDtos().get(ix);
				AgmSecondaryIdSlotAnnotation sid = existingSecondaryIds.remove(identityHelper.secondaryIdDtoIdentity(sidDto));
				ObjectResponse<AgmSecondaryIdSlotAnnotation> sidResponse = agmSecondaryIdDtoValidator.validateAgmSecondaryIdSlotAnnotationDTO(sid, sidDto);
				if (sidResponse.hasErrors()) {
					allValid = false;
					agmResponse.addErrorMessages(field, ix, sidResponse.getErrorMessages());
				} else {
					sid = sidResponse.getEntity();
					sid.setSingleAgm(model);
					validatedSecondaryIds.add(sid);
				}
			}
		}

		if (!allValid) {
			agmResponse.convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedSecondaryIds)) {
			return null;
		}

		return validatedSecondaryIds;
	}

}
