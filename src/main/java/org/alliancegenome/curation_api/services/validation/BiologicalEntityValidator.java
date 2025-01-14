package org.alliancegenome.curation_api.services.validation;

import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.services.validation.base.SubmittedObjectValidator;

public class BiologicalEntityValidator<E extends BiologicalEntity> extends SubmittedObjectValidator<E> {

	public E validateBiologicalEntityFields(E uiEntity, E dbEntity) {

		dbEntity = validateSubmittedObjectFields(uiEntity, dbEntity);

		NCBITaxonTerm taxon = validateRequiredTaxon(uiEntity.getTaxon(), dbEntity.getTaxon());
		dbEntity.setTaxon(taxon);

		return dbEntity;
	}
}
