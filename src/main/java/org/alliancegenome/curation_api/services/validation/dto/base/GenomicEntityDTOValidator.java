package org.alliancegenome.curation_api.services.validation.dto.base;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.ingest.dto.CrossReferenceDTO;
import org.alliancegenome.curation_api.model.ingest.dto.GenomicEntityDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.CrossReferenceService;
import org.alliancegenome.curation_api.services.validation.dto.CrossReferenceDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.inject.Inject;

public class GenomicEntityDTOValidator <E extends GenomicEntity, D extends GenomicEntityDTO> extends BiologicalEntityDTOValidator<E, D> {

	@Inject CrossReferenceDTOValidator crossReferenceDtoValidator;
	@Inject CrossReferenceService crossReferenceService;

	public E validateGenomicEntityDTO(E entity, D dto, BackendBulkDataProvider dataProvider) {

		entity = validateBiologicalEntityDTO(entity, dto, dataProvider);
		
		List<CrossReference> validatedXrefs = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dto.getCrossReferenceDtos())) {
			for (CrossReferenceDTO xrefDto : dto.getCrossReferenceDtos()) {
				ObjectResponse<CrossReference> xrefResponse = crossReferenceDtoValidator.validateCrossReferenceDTO(xrefDto, null);
				if (xrefResponse.hasErrors()) {
					response.addErrorMessage("cross_reference_dtos", xrefResponse.errorMessagesString());
					break;
				} else {
					validatedXrefs.add(xrefResponse.getEntity());
				}
			}
		}

		List<CrossReference> xrefs = crossReferenceService.getUpdatedXrefList(validatedXrefs, entity.getCrossReferences());

		if (entity.getCrossReferences() != null) {
			entity.getCrossReferences().clear();
		}
		if (xrefs != null) {
			if (entity.getCrossReferences() == null) {
				entity.setCrossReferences(new ArrayList<>());
			}
			entity.getCrossReferences().addAll(xrefs);
		}
		
		return entity;
	}

}
