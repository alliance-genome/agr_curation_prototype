package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ontology.SoTermService;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.SlotAnnotationDTOValidator;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleMutationTypeSlotAnnotationDTOValidator extends SlotAnnotationDTOValidator<AlleleMutationTypeSlotAnnotation, AlleleMutationTypeSlotAnnotationDTO> {

	@Inject SoTermService soTermService;

	public ObjectResponse<AlleleMutationTypeSlotAnnotation> validateAlleleMutationTypeSlotAnnotationDTO(AlleleMutationTypeSlotAnnotation annotation, AlleleMutationTypeSlotAnnotationDTO dto) {
		response = new ObjectResponse<AlleleMutationTypeSlotAnnotation>();
		
		if (annotation == null) {
			annotation = new AlleleMutationTypeSlotAnnotation();
		}

		annotation = validateSlotAnnotationDTO(annotation, dto);
		
		List<SOTerm> soTerms = validateRequiredOntologyTerms(soTermService, "mutation_type_curies", dto.getMutationTypeCuries());
		annotation.setMutationTypes(soTerms);

		response.setEntity(annotation);

		return response;
	}
}
