package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.ResourceDescriptorDAO;
import org.alliancegenome.curation_api.dao.ResourceDescriptorPageDAO;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.ingest.dto.ResourceDescriptorDTO;
import org.alliancegenome.curation_api.model.ingest.dto.ResourceDescriptorPageDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ResourceDescriptorDTOValidator extends BaseDTOValidator {

	@Inject ResourceDescriptorDAO resourceDescriptorDAO;
	@Inject ResourceDescriptorPageDAO resourceDescriptorPageDAO;
	@Inject ResourceDescriptorPageDTOValidator resourceDescriptorPageDtoValidator;

	public ResourceDescriptor validateResourceDescriptorDTO(ResourceDescriptorDTO dto) throws ValidationException {
		ObjectResponse<ResourceDescriptor> rdResponse = new ObjectResponse<ResourceDescriptor>();

		ResourceDescriptor rd = null;
		if (StringUtils.isBlank(dto.getDbPrefix())) {
			rdResponse.addErrorMessage("db_prefix", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			SearchResponse<ResourceDescriptor> rdSearchResponse = resourceDescriptorDAO.findByField("prefix", dto.getDbPrefix());
			if (rdSearchResponse == null || rdSearchResponse.getSingleResult() == null) {
				rd = new ResourceDescriptor();
				rd.setPrefix(dto.getDbPrefix());
			} else {
				rd = rdSearchResponse.getSingleResult();
			}
		}

		if (StringUtils.isBlank(dto.getName())) {
			rdResponse.addErrorMessage("name", ValidationConstants.REQUIRED_MESSAGE);
		}
		rd.setName(dto.getName());

		if (CollectionUtils.isNotEmpty(dto.getAliases())) {
			rd.setSynonyms(dto.getAliases());
		} else {
			rd.setSynonyms(null);
		}

		String idPattern = null;
		if (StringUtils.isNotBlank(dto.getGidPattern())) {
			idPattern = StringEscapeUtils.escapeJava(dto.getGidPattern());
		}
		rd.setIdPattern(idPattern);

		String idExample = null;
		if (StringUtils.isNotBlank(dto.getExampleGid())) {
			idExample = dto.getExampleGid();
		} else if (StringUtils.isNotBlank(dto.getExampleId())) {
			idExample = dto.getExampleId();
		}
		rd.setIdExample(idExample);

		List<ResourceDescriptorPage> rdPages = new ArrayList<>();

		String defaultUrlTemplate = null;
		ResourceDescriptorPageDTO defaultPageDTO = null;
		if (StringUtils.isNotBlank(dto.getDefaultUrl())) {
			defaultUrlTemplate = dto.getDefaultUrl();
			defaultPageDTO = new ResourceDescriptorPageDTO();
			defaultPageDTO.setName("default");
			defaultPageDTO.setUrl(defaultUrlTemplate);
			ObjectResponse<ResourceDescriptorPage> defaultRdPageResponse = resourceDescriptorPageDtoValidator.validateResourceDescriptorPageDTO(defaultPageDTO, dto.getDbPrefix());
			if (defaultRdPageResponse.hasErrors()) {
				rdResponse.addErrorMessage("default_url_template", "Error during default page construction: " + defaultRdPageResponse.errorMessagesString());
			}
			rdPages.add(defaultRdPageResponse.getEntity());
		}
		rd.setDefaultUrlTemplate(defaultUrlTemplate);

		List<Long> previousPageIds = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(rd.getResourcePages())) {
			previousPageIds = rd.getResourcePages().stream().map(ResourceDescriptorPage::getId).collect(Collectors.toList());
		}
		if (CollectionUtils.isNotEmpty(dto.getPages())) {
			for (ResourceDescriptorPageDTO rdPageDTO : dto.getPages()) {
				ObjectResponse<ResourceDescriptorPage> rdPageResponse = resourceDescriptorPageDtoValidator.validateResourceDescriptorPageDTO(rdPageDTO, dto.getDbPrefix());
				if (rdPageResponse.hasErrors()) {
					rdResponse.addErrorMessage("pages", rdPageResponse.errorMessagesString());
					break;
				}
				rdPages.add(rdPageResponse.getEntity());
			}
		} else {
			rd.setResourcePages(null);
		}

		rdResponse.setEntity(rd);

		if (rdResponse.hasErrors()) {
			throw new ObjectValidationException(dto, rdResponse.errorMessagesString());
		}

		rd = resourceDescriptorDAO.persist(rd);

		// Attach rd and persist ResourceDescriptorPage objects)
		List<Long> newPageIds = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(rdPages)) {
			for (ResourceDescriptorPage rdPage : rdPages) {
				rdPage.setResourceDescriptor(rd);
				rdPage = resourceDescriptorPageDAO.persist(rdPage);
				newPageIds.add(rdPage.getId());
			}
		}
		// Remove unused ResourceDescriptorPage objects
		if (CollectionUtils.isNotEmpty(previousPageIds)) {
			for (Long previousPageId : previousPageIds) {
				if (!newPageIds.contains(previousPageId)) {
					resourceDescriptorPageDAO.remove(previousPageId);
				}
			}
		}

		return rd;
	}
}
