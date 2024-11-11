package org.alliancegenome.curation_api.services;

import org.alliancegenome.curation_api.dao.BiologicalEntityDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.ingest.dto.BiologicalEntityDTO;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class BiologicalEntityService extends SubmittedObjectCrudService<BiologicalEntity, BiologicalEntityDTO, BiologicalEntityDAO> {

	@Inject BiologicalEntityDAO biologicalEntityDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(biologicalEntityDAO);
	}

	@Override
	public BiologicalEntity upsert(BiologicalEntityDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		return null;
	}

	@Override
	public BiologicalEntity deprecateOrDelete(Long id, Boolean throwApiError, String requestSource, Boolean deprecate) {
		return null;
	}

}
