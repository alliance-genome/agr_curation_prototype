package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations;

import java.util.List;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.PhenotypeTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFunctionalImpactSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.alleleSlotAnnotations.AlleleFunctionalImpactSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ontology.PhenotypeTermService;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.SlotAnnotationDTOValidator;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleFunctionalImpactSlotAnnotationDTOValidator extends SlotAnnotationDTOValidator<AlleleFunctionalImpactSlotAnnotation, AlleleFunctionalImpactSlotAnnotationDTO> {

	@Inject PhenotypeTermService phenotypeTermService;

	public ObjectResponse<AlleleFunctionalImpactSlotAnnotation> validateAlleleFunctionalImpactSlotAnnotationDTO(AlleleFunctionalImpactSlotAnnotation annotation, AlleleFunctionalImpactSlotAnnotationDTO dto) {
		response = new ObjectResponse<AlleleFunctionalImpactSlotAnnotation>();
		
		if (annotation == null) {
			annotation = new AlleleFunctionalImpactSlotAnnotation();
		}

		annotation = validateSlotAnnotationDTO(annotation, dto);
		
		List<VocabularyTerm> functionalImpacts = validateRequiredTermsInVocabulary("functional_impact_names", dto.getFunctionalImpactNames(), VocabularyConstants.ALLELE_FUNCTIONAL_IMPACT_VOCABULARY);
		annotation.setFunctionalImpacts(functionalImpacts);

		PhenotypeTerm phenotypeTerm = validateOntologyTerm(phenotypeTermService, "phenotype_term_curie", dto.getPhenotypeTermCurie());
		annotation.setPhenotypeTerm(phenotypeTerm);

		annotation.setPhenotypeStatement(handleStringField(dto.getPhenotypeStatement()));

		response.setEntity(annotation);

		return response;
	}
}
