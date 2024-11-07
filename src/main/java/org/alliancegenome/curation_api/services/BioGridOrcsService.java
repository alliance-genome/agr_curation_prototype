package org.alliancegenome.curation_api.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.SequenceTargetingReagentDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.ingest.dto.fms.SequenceTargetingReagentFmsDTO;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.BioGridOrcsFmsDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.fms.SequenceTargetingReagentFmsDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class BioGridOrcsService {

	@Inject BioGridOrcsFmsDTOValidator bioGridOrcsFmsDTOValidator;
	@Inject CrossReferenceDAO crossReferenceDAO;

	// @Override
	// @PostConstruct
	// protected void init() {
	// 	setSQLDao(crossReferenceDAO);
	// }

	@Transactional
	public CrossReference insert(BioGridOrcsFmsDTOValidator dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		// CrossReference crossReference = bioGridOrcsFmsDTOValidator.validateSQTRFmsDTO(dto, dataProvider);
		return new CrossReference();
		// return crossReferenceDAO.persist(sqtr);
	}

	// public List<Long> getIdsByDataProvider(String dataProvider) {
	// 	Map<String, Object> params = new HashMap<>();
	// 	params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider);
	// 	List<Long> ids = sqtrDAO.findIdsByParams(params);
	// 	ids.removeIf(Objects::isNull);
	// 	return ids;
	// }
}
