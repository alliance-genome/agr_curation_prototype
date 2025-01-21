package org.alliancegenome.curation_api.services.validation.dto.base;

import java.util.Objects;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.ingest.dto.BiologicalEntityDTO;

public class BiologicalEntityDTOValidator<E extends BiologicalEntity, D extends BiologicalEntityDTO> extends SubmittedObjectDTOValidator<E, D> {

	public E validateBiologicalEntityDTO(E entity, D dto, BackendBulkDataProvider beDataProvider) {

		entity = validateSubmittedObjectDTO(entity, dto);
		
		NCBITaxonTerm taxon = validateRequiredTaxon("taxon_curie", dto.getTaxonCurie());
		if (beDataProvider != null && (beDataProvider.name().equals("RGD") || beDataProvider.name().equals("HUMAN")) && !Objects.equals(taxon.getCurie(), beDataProvider.canonicalTaxonCurie)) {
			response.addErrorMessage("taxon_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getTaxonCurie() + ") for " + beDataProvider.name() + " load");
		}
		entity.setTaxon(taxon);

		return entity;
	}

}
