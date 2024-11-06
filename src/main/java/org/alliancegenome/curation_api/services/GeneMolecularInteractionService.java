package org.alliancegenome.curation_api.services;

import java.util.List;

import org.alliancegenome.curation_api.dao.GeneMolecularInteractionDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.GeneMolecularInteraction;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PsiMiTabDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.GeneMolecularInteractionFmsDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class GeneMolecularInteractionService extends BaseEntityCrudService<GeneMolecularInteraction, GeneMolecularInteractionDAO> implements BaseUpsertServiceInterface<GeneMolecularInteraction, PsiMiTabDTO> {

	@Inject GeneMolecularInteractionDAO geneMolecularInteractionDAO;
	@Inject GeneMolecularInteractionFmsDTOValidator geneMolInteractionValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneMolecularInteractionDAO);
	}

	public ObjectResponse<GeneMolecularInteraction> getByIdentifier(String identifier) {
		return new ObjectResponse<>(findByAlternativeFields(List.of("interactionId", "uniqueId"), identifier));
	}

	@Transactional
	public GeneMolecularInteraction upsert(PsiMiTabDTO dto, BackendBulkDataProvider backendBulkDataProvider) throws ValidationException {
		GeneMolecularInteraction interaction = geneMolInteractionValidator.validateGeneMolecularInteractionFmsDTO(dto);
		return geneMolecularInteractionDAO.persist(interaction);
	}

}
