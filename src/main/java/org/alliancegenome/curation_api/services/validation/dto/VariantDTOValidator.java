package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.VariantDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.ingest.dto.VariantDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ontology.SoTermService;
import org.alliancegenome.curation_api.services.validation.dto.base.GenomicEntityDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class VariantDTOValidator extends GenomicEntityDTOValidator<Variant, VariantDTO> {

	@Inject VariantDAO variantDAO;
	@Inject SoTermService soTermService;

	@Transactional
	public Variant validateVariantDTO(VariantDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		response = new ObjectResponse<Variant>();
		
		Variant variant = null;
		if (StringUtils.isNotBlank(dto.getPrimaryExternalId())) {
			variant = findDatabaseObject(variantDAO, "primaryExternalId", dto.getPrimaryExternalId());
		}
		if (variant == null) {
			if (StringUtils.isBlank(dto.getModInternalId())) {
				if (StringUtils.isBlank(dto.getPrimaryExternalId())) {
					response.addErrorMessage("mod_internal_id", ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + "primary_external_id");
				}
			} else {
				variant = findDatabaseObject(variantDAO, "modInternalId", dto.getModInternalId());
			}
		}
		if (variant == null) {
			variant = new Variant();
		}

		variant = validateGenomicEntityDTO(variant, dto, dataProvider);
		
		SOTerm variantType = validateRequiredOntologyTerm(soTermService, "variant_type_curie", dto.getVariantTypeCurie());
		variant.setVariantType(variantType);

		VocabularyTerm variantStatus = validateTermInVocabulary("variant_status_name", dto.getVariantStatusName(), VocabularyConstants.VARIANT_STATUS_VOCABULARY);
		variant.setVariantStatus(variantStatus);

		SOTerm sourceGeneralConsequence = validateOntologyTerm(soTermService, "source_general_consequence_curie", dto.getSourceGeneralConsequenceCurie());
		variant.setSourceGeneralConsequence(sourceGeneralConsequence);

		if (variant.getRelatedNotes() != null) {
			variant.getRelatedNotes().clear();
		}

		List<Note> validatedNotes = validateNotes(dto.getNoteDtos(), VocabularyConstants.VARIANT_NOTE_TYPES_VOCABULARY_TERM_SET);
		if (CollectionUtils.isNotEmpty(validatedNotes)) {
			if (variant.getRelatedNotes() == null) {
				variant.setRelatedNotes(new ArrayList<>());
			}
			variant.getRelatedNotes().addAll(validatedNotes);
		}
		
		response.convertErrorMessagesToMap();

		if (response.hasErrors()) {
			throw new ObjectValidationException(dto, response.errorMessagesString());
		}

		return variantDAO.persist(variant);
	}

}
