package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.CrossReferenceService;
import org.apache.commons.collections.CollectionUtils;

import jakarta.inject.Inject;

public class GenomicEntityValidator<E extends GenomicEntity> extends BiologicalEntityValidator<E> {

	@Inject CrossReferenceValidator crossReferenceValidator;
	@Inject CrossReferenceService crossReferenceService;

	public E validateGenomicEntityFields(E uiEntity, E dbEntity) {

		dbEntity = validateBiologicalEntityFields(uiEntity, dbEntity);

		List<CrossReference> xrefs = validateCrossReferences(uiEntity, dbEntity);
		if (dbEntity.getCrossReferences() != null) {
			dbEntity.getCrossReferences().clear();
		}
		if (xrefs != null) {
			if (dbEntity.getCrossReferences() == null) {
				dbEntity.setCrossReferences(new ArrayList<>());
			}
			dbEntity.getCrossReferences().addAll(xrefs);
		}

		return dbEntity;
	}

	public List<CrossReference> validateCrossReferences(E uiEntity, E dbEntity) {
		String field = "crossReferences";

		List<CrossReference> validatedXrefs = new ArrayList<CrossReference>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getCrossReferences())) {
			for (int ix = 0; ix < uiEntity.getCrossReferences().size(); ix++) {
				CrossReference xref = uiEntity.getCrossReferences().get(ix);
				ObjectResponse<CrossReference> xrefResponse = crossReferenceValidator.validateCrossReference(xref, false);
				if (xrefResponse.hasErrors()) {
					allValid = false;
					response.addErrorMessages(field, ix, xrefResponse.getErrorMessages());
				} else {
					validatedXrefs.add(xrefResponse.getEntity());
				}
			}
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedXrefs)) {
			return null;
		}

		return validatedXrefs;
	}

}
