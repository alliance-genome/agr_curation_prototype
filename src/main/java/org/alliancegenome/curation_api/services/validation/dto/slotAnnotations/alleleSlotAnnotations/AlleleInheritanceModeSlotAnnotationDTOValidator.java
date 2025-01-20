package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.PhenotypeTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ontology.PhenotypeTermService;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.SlotAnnotationDTOValidator;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleInheritanceModeSlotAnnotationDTOValidator extends SlotAnnotationDTOValidator<AlleleInheritanceModeSlotAnnotation, AlleleInheritanceModeSlotAnnotationDTO> {

	@Inject PhenotypeTermService phenotypeTermService;

	public ObjectResponse<AlleleInheritanceModeSlotAnnotation> validateAlleleInheritanceModeSlotAnnotationDTO(AlleleInheritanceModeSlotAnnotation annotation, AlleleInheritanceModeSlotAnnotationDTO dto) {
		response = new ObjectResponse<AlleleInheritanceModeSlotAnnotation>();
		
		if (annotation == null) {
			annotation = new AlleleInheritanceModeSlotAnnotation();
		}

		annotation = validateSlotAnnotationDTO(annotation, dto);
		
		VocabularyTerm inheritanceMode = validateRequiredTermInVocabulary("inheritance_mode_name", dto.getInheritanceModeName(), VocabularyConstants.ALLELE_INHERITANCE_MODE_VOCABULARY);
		annotation.setInheritanceMode(inheritanceMode);

		PhenotypeTerm phenotypeTerm = validateOntologyTerm(phenotypeTermService, "phenotype_term_curie", dto.getPhenotypeTermCurie());
		annotation.setPhenotypeTerm(phenotypeTerm);

		annotation.setPhenotypeStatement(handleStringField(dto.getPhenotypeStatement()));

		response.setEntity(annotation);

		return response;
	}
}
