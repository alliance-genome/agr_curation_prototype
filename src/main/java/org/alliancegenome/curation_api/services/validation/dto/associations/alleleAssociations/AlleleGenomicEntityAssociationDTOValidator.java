package org.alliancegenome.curation_api.services.validation.dto.associations.alleleAssociations;

import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.model.ingest.dto.associations.alleleAssociations.AlleleGenomicEntityAssociationDTO;
import org.alliancegenome.curation_api.services.ontology.EcoTermService;
import org.alliancegenome.curation_api.services.validation.dto.associations.EvidenceAssociationDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.inject.Inject;

public class AlleleGenomicEntityAssociationDTOValidator<E extends AlleleGenomicEntityAssociation, D extends AlleleGenomicEntityAssociationDTO> extends EvidenceAssociationDTOValidator<E, D> {

	@Inject EcoTermService ecoTermService;

	public E validateAlleleGenomicEntityAssociationDTO(E association, D dto, String relationVocabularyTermSet) {
		
		association = validateEvidenceAssociationDTO(association, dto);

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation_name", dto.getRelationName(), relationVocabularyTermSet);
		association.setRelation(relation);

		ECOTerm ecoTerm = validateOntologyTerm(ecoTermService, "evidence_code_curie", dto.getEvidenceCodeCurie());
		if (ecoTerm != null && (CollectionUtils.isEmpty(ecoTerm.getSubsets()) || !ecoTerm.getSubsets().contains(OntologyConstants.AGR_ECO_TERM_SUBSET))) {
			response.addErrorMessage("evidence_code_curie", ValidationConstants.UNSUPPORTED_MESSAGE + " (" + dto.getEvidenceCodeCurie() + ")");
		}
		association.setEvidenceCode(ecoTerm);
		
		Note relatedNote = validateNote(dto.getNoteDto(), VocabularyConstants.ALLELE_GENOMIC_ENTITY_ASSOCIATION_NOTE_TYPES_VOCABULARY_TERM_SET);
		association.setRelatedNote(relatedNote);

		return association;
	}
}
