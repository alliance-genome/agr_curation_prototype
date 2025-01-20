package org.alliancegenome.curation_api.services.validation.dto;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.ingest.dto.CrossReferenceDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ResourceDescriptorPageService;
import org.alliancegenome.curation_api.services.ResourceDescriptorService;
import org.alliancegenome.curation_api.services.validation.dto.base.AuditedObjectDTOValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class CrossReferenceDTOValidator extends AuditedObjectDTOValidator<CrossReference, CrossReferenceDTO> {

	@Inject ResourceDescriptorService resourceDescriptorService;
	@Inject ResourceDescriptorPageService resourceDescriptorPageService;

	public ObjectResponse<CrossReference> validateCrossReferenceDTO(CrossReferenceDTO dto, CrossReference xref) {
		response = new ObjectResponse<CrossReference>();
		
		if (xref == null) {
			xref = new CrossReference();
		}

		xref = validateAuditedObjectDTO(xref, dto);

		if (StringUtils.isBlank(dto.getPrefix())) {
			response.addErrorMessage("prefix", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<ResourceDescriptor> rdResponse = resourceDescriptorService.getByPrefixOrSynonym(dto.getPrefix());
			if (rdResponse == null || rdResponse.getEntity() == null) {
				response.addErrorMessage("prefix", ValidationConstants.INVALID_MESSAGE + " (" + dto.getPrefix() + ")");
			}
		}

		if (StringUtils.isBlank(dto.getReferencedCurie())) {
			response.addErrorMessage("reference_curie", ValidationConstants.REQUIRED_MESSAGE);
		}
		xref.setReferencedCurie(dto.getReferencedCurie());

		if (StringUtils.isBlank(dto.getDisplayName())) {
			response.addErrorMessage("display_name", ValidationConstants.REQUIRED_MESSAGE);
		}
		xref.setDisplayName(dto.getDisplayName());

		if (StringUtils.isBlank(dto.getPageArea())) {
			response.addErrorMessage("page_area", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ResourceDescriptorPage page = resourceDescriptorPageService.getPageForResourceDescriptor(dto.getPrefix(), dto.getPageArea());
			if (page == null) {
				response.addErrorMessage("page_area", ValidationConstants.INVALID_MESSAGE + " (" + dto.getPageArea() + ")");
			}
			xref.setResourceDescriptorPage(page);
		}

		response.setEntity(xref);

		return response;
	}

}
