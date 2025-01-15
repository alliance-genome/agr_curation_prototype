package org.alliancegenome.curation_api.services.validation;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.Reagent;
import org.alliancegenome.curation_api.services.validation.base.SubmittedObjectValidator;
import org.apache.commons.collections.CollectionUtils;

public class ReagentValidator extends SubmittedObjectValidator<Reagent> {

	public Reagent validateCommonReagentFields(Reagent uiEntity, Reagent dbEntity) {

		dbEntity = (Reagent) validateSubmittedObjectFields(uiEntity, dbEntity);

		List<String> secondaryIds = CollectionUtils.isNotEmpty(uiEntity.getSecondaryIdentifiers()) ? uiEntity.getSecondaryIdentifiers() : null;
		dbEntity.setSecondaryIdentifiers(secondaryIds);
		
		return dbEntity;
	}
}
