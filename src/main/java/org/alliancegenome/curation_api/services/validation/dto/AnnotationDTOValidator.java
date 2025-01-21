package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.model.entities.Annotation;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.ingest.dto.AnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.ConditionRelationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.base.AuditedObjectDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import jakarta.inject.Inject;

public class AnnotationDTOValidator<E extends Annotation, D extends AnnotationDTO> extends AuditedObjectDTOValidator<E, D> {

	@Inject ConditionRelationDAO conditionRelationDAO;
	@Inject ConditionRelationDTOValidator conditionRelationDtoValidator;
	@Inject CrossReferenceDAO crossReferenceDAO;

	public E validateAnnotationDTO(E annotation, D dto, String noteTypeSet) {
		annotation = validateAuditedObjectDTO(annotation, dto);

		if (dto.getDataProviderDto() == null) {
			response.addErrorMessage("data_provider_dto", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<ImmutablePair<Organization, CrossReference>> dpResponse = validateDataProviderDTO(dto.getDataProviderDto(), annotation.getDataProviderCrossReference());
			if (dpResponse.hasErrors()) {
				response.addErrorMessage("data_provider_dto", dpResponse.errorMessagesString());
			} else {
				annotation.setDataProvider(dpResponse.getEntity().getLeft());
				if (dpResponse.getEntity().getRight() != null) {
					annotation.setDataProviderCrossReference(crossReferenceDAO.persist(dpResponse.getEntity().getRight()));
				} else {
					annotation.setDataProviderCrossReference(null);
				}
			}
		}

		if (annotation.getRelatedNotes() != null) {
			annotation.getRelatedNotes().clear();
		}

		List<Note> validatedNotes = validateNotes(dto.getNoteDtos(), noteTypeSet, dto.getReferenceCurie());
		if (CollectionUtils.isNotEmpty(validatedNotes)) {
			if (annotation.getRelatedNotes() == null) {
				annotation.setRelatedNotes(new ArrayList<>());
			}
			annotation.getRelatedNotes().addAll(validatedNotes);
		}

		if (CollectionUtils.isNotEmpty(dto.getConditionRelationDtos())) {
			List<ConditionRelation> relations = new ArrayList<>();
			for (ConditionRelationDTO conditionRelationDTO : dto.getConditionRelationDtos()) {
				if (StringUtils.isNotBlank(conditionRelationDTO.getHandle())) {
					if (!StringUtils.equals(conditionRelationDTO.getReferenceCurie(), dto.getReferenceCurie())) {
						response.addErrorMessage("condition_relation_dtos - reference_curie", ValidationConstants.INVALID_MESSAGE + " (" + conditionRelationDTO.getReferenceCurie() + ")");
					}
				}
				ObjectResponse<ConditionRelation> crResponse = conditionRelationDtoValidator.validateConditionRelationDTO(conditionRelationDTO);
				if (crResponse.hasErrors()) {
					response.addErrorMessage("condition_relation_dtos", crResponse.errorMessagesString());
				} else {
					relations.add(conditionRelationDAO.persist(crResponse.getEntity()));
				}
			}
			annotation.setConditionRelations(relations);
		} else {
			annotation.setConditionRelations(null);
		}

		return annotation;
	}


}
