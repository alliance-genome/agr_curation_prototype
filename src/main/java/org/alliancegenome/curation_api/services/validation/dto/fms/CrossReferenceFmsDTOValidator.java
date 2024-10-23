package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.ingest.dto.fms.CrossReferenceFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ResourceDescriptorPageService;
import org.alliancegenome.curation_api.services.ResourceDescriptorService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class CrossReferenceFmsDTOValidator {

	@Inject ResourceDescriptorService resourceDescriptorService;
	@Inject ResourceDescriptorPageService resourceDescriptorPageService;

	public ObjectResponse<List<CrossReference>> validateCrossReferenceFmsDTO(CrossReferenceFmsDTO dto) {
		
		ObjectResponse<List<CrossReference>> crResponse = new ObjectResponse<>();
		List<CrossReference> xrefList = new ArrayList<>();
		List<String> pageNames = List.of("default");
		
		if (CollectionUtils.isNotEmpty(dto.getPages())) {
			pageNames = dto.getPages();
		}
		
		for (String pageName : pageNames) {		
			CrossReference xref = new CrossReference();
		
			if (StringUtils.isBlank(dto.getId())) {
				crResponse.addErrorMessage("id", ValidationConstants.REQUIRED_MESSAGE);
			} else {
				String[] idParts = dto.getId().split(":");
				ResourceDescriptor resourceDescriptor = null;
				if (idParts.length == 2) {
					ObjectResponse<ResourceDescriptor> rdResponse = resourceDescriptorService.getByPrefixOrSynonym(idParts[0]);
					if (rdResponse != null) {
						resourceDescriptor = rdResponse.getEntity();
					}
				}
				if (resourceDescriptor == null) {
					crResponse.addErrorMessage("id", ValidationConstants.INVALID_MESSAGE + " (" + dto.getId() + ")");
				} else {
					xref.setReferencedCurie(dto.getId());
					xref.setDisplayName(dto.getId());
					ResourceDescriptorPage page = resourceDescriptorPageService.getPageForResourceDescriptor(idParts[0], pageName);
					if (page == null) {
						crResponse.addErrorMessage("pages", ValidationConstants.INVALID_MESSAGE + " (" + page + ")");
					}
					xref.setResourceDescriptorPage(page);
					xrefList.add(xref);
				}
			}		
		}

		crResponse.setEntity(xrefList);

		return crResponse;
	}

}
