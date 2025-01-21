package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.constructSlotAnnotations;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.constructSlotAnnotations.ConstructComponentSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.SlotAnnotationDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ConstructComponentSlotAnnotationDTOValidator extends SlotAnnotationDTOValidator<ConstructComponentSlotAnnotation, ConstructComponentSlotAnnotationDTO> {

	@Inject ConstructComponentSlotAnnotationDAO constructComponentDAO;
	
	public ObjectResponse<ConstructComponentSlotAnnotation> validateConstructComponentSlotAnnotationDTO(ConstructComponentSlotAnnotation annotation, ConstructComponentSlotAnnotationDTO dto) {
		response = new ObjectResponse<ConstructComponentSlotAnnotation>();
		
		if (annotation == null) {
			annotation = new ConstructComponentSlotAnnotation();
		}

		annotation = validateSlotAnnotationDTO(annotation, dto);
		
		if (StringUtils.isAllBlank(dto.getComponentSymbol())) {
			response.addErrorMessage("component_symbol", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			annotation.setComponentSymbol(dto.getComponentSymbol());
		}

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation_name", dto.getRelationName(), VocabularyConstants.CONSTRUCT_GENOMIC_ENTITY_RELATION_VOCABULARY_TERM_SET);
		annotation.setRelation(relation);

		NCBITaxonTerm taxon = validateTaxon("taxon_curie", dto.getTaxonCurie());
		annotation.setTaxon(taxon);

		annotation.setTaxonText(handleStringField(dto.getTaxonText()));
		
		if (annotation.getRelatedNotes() != null) {
			annotation.getRelatedNotes().clear();
		}

		List<Note> validatedNotes = validateNotes(dto.getNoteDtos(), VocabularyConstants.CONSTRUCT_COMPONENT_NOTE_TYPES_VOCABULARY_TERM_SET);
		if (CollectionUtils.isNotEmpty(validatedNotes)) {
			if (annotation.getRelatedNotes() == null) {
				annotation.setRelatedNotes(new ArrayList<>());
			}
			annotation.getRelatedNotes().addAll(validatedNotes);
		}

		response.setEntity(annotation);

		return response;
	}
}
