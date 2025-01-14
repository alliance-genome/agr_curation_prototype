package org.alliancegenome.curation_api.services.validation.associations.alleleAssociations;

import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.ontology.EcoTermDAO;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.services.validation.associations.EvidenceAssociationValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.inject.Inject;

public class AlleleGenomicEntityAssociationValidator<E extends AlleleGenomicEntityAssociation> extends EvidenceAssociationValidator<E> {

	@Inject NoteDAO noteDAO;
	@Inject EcoTermDAO ecoTermDAO;
	@Inject AlleleDAO alleleDAO;

	public ECOTerm validateEvidenceCode(E uiEntity, E dbEntity) {
		String field = "evidenceCode";

		ECOTerm evidenceCode = validateEntity(ecoTermDAO, field, uiEntity.getEvidenceCode(), dbEntity.getEvidenceCode());
		if (evidenceCode != null && (CollectionUtils.isEmpty(evidenceCode.getSubsets()) || !evidenceCode.getSubsets().contains(OntologyConstants.AGR_ECO_TERM_SUBSET))) {
			addMessageResponse(field, ValidationConstants.UNSUPPORTED_MESSAGE);
			return null;
		}

		return evidenceCode;
	}

	public E validateAlleleGenomicEntityAssociationFields(E uiEntity, E dbEntity) {

		dbEntity = validateEvidenceAssociationFields(uiEntity, dbEntity);

		ECOTerm evidenceCode = validateEvidenceCode(uiEntity, dbEntity);
		dbEntity.setEvidenceCode(evidenceCode);

		Note relatedNote = validateRelatedNote(uiEntity.getRelatedNote(), VocabularyConstants.ALLELE_GENOMIC_ENTITY_ASSOCIATION_NOTE_TYPES_VOCABULARY_TERM_SET);
		dbEntity.setRelatedNote(relatedNote);

		return dbEntity;
	}
}
