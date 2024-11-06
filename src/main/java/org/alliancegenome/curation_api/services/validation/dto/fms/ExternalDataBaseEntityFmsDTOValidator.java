package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.ExternalDataBaseEntityDAO;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.ExternalDataBaseEntity;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.ingest.dto.fms.CrossReferenceFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.HTPIdFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.CrossReferenceService;
import org.alliancegenome.curation_api.services.ExternalDataBaseEntityService;
import org.alliancegenome.curation_api.services.ResourceDescriptorPageService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class ExternalDataBaseEntityFmsDTOValidator {

	@Inject ExternalDataBaseEntityDAO externalDataBaseEntityDAO;
	@Inject ExternalDataBaseEntityService externalDataBaseEntityService;
	@Inject CrossReferenceDAO crossReferenceDAO;
	@Inject CrossReferenceService crossReferenceService;
	@Inject ResourceDescriptorPageService resourceDescriptorPageService;

	@Transactional
	public ExternalDataBaseEntity validateExternalDataBaseEntityFmsDTO(HTPIdFmsDTO dto) throws ValidationException {

		ObjectResponse<ExternalDataBaseEntity> externalDBEntityResponse = new ObjectResponse<>();
		ExternalDataBaseEntity externalDBEntity = null;
		
		if (StringUtils.isEmpty(dto.getPrimaryId())) {
			externalDBEntityResponse.addErrorMessage("primaryId", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			externalDBEntity = externalDataBaseEntityService.findByCurie(dto.getPrimaryId());
		}

		if (externalDBEntity == null) {
			externalDBEntity = new ExternalDataBaseEntity();
			externalDBEntity.setCurie(dto.getPrimaryId());
		}

		if (CollectionUtils.isNotEmpty(dto.getAlternateIds())) {
			if (externalDBEntity.getSecondaryIdentifiers() == null) {
				externalDBEntity.setSecondaryIdentifiers(new ArrayList<>());
			}
			Set<String> existingSecondaryIds = new HashSet<>(externalDBEntity.getSecondaryIdentifiers());
			Set<String> incomingAlternateIds = new HashSet<>(dto.getAlternateIds());

			existingSecondaryIds.retainAll(incomingAlternateIds);
			existingSecondaryIds.addAll(incomingAlternateIds);
			externalDBEntity.setSecondaryIdentifiers(new ArrayList<>(existingSecondaryIds));
		}

		List<Long> currentXrefIds;
		if (externalDBEntity.getCrossReferences() == null) {
			currentXrefIds = new ArrayList<>();
		} else {
			currentXrefIds = externalDBEntity.getCrossReferences().stream().map(CrossReference::getId).collect(Collectors.toList());
		}

		List<Long> mergedXrefIds = null;
		List<CrossReference> mergedCrossReferences = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dto.getCrossReferences())) {
			mergedCrossReferences = crossReferenceService.getMergedFmsXrefList(dto.getCrossReferences(), externalDBEntity.getCrossReferences());
			mergedXrefIds = mergedCrossReferences.stream().map(CrossReference::getId).collect(Collectors.toList());
			externalDBEntity.setCrossReferences(mergedCrossReferences);
		} else {
			mergedXrefIds = new ArrayList<>();
			externalDBEntity.setCrossReferences(null);
		}

		Map<String, CrossReference> mergedXrefUniqueIdsMap = new HashMap<>();
		for (CrossReference mergedXref : mergedCrossReferences) {
			mergedXrefUniqueIdsMap.put(crossReferenceService.getCrossReferenceUniqueId(mergedXref), mergedXref);
		}

		if (externalDBEntity.getPreferredCrossReference() != null) {
			if (dto.getPreferredCrossReference() != null) {
				CrossReference incomingPreferredXref = createNewCrossReference(dto.getPreferredCrossReference());
				String incomingXrefUniqueId = crossReferenceService.getCrossReferenceUniqueId(incomingPreferredXref);
				String currentXrefUniqueId = crossReferenceService.getCrossReferenceUniqueId(externalDBEntity.getPreferredCrossReference());

				if (!incomingXrefUniqueId.equals(currentXrefUniqueId)) {
					externalDBEntity.setPreferredCrossReference(null);
					if (mergedXrefUniqueIdsMap.containsKey(incomingXrefUniqueId)) {
						externalDBEntity.setPreferredCrossReference(mergedXrefUniqueIdsMap.get(incomingXrefUniqueId));
					} else {
						externalDBEntity.setPreferredCrossReference(crossReferenceDAO.persist(incomingPreferredXref));
					}
				}
			} else {
				externalDBEntity.setPreferredCrossReference(null);
			}
		} else {
			if (dto.getPreferredCrossReference() != null) {
				CrossReference incomingPreferredXref = createNewCrossReference(dto.getPreferredCrossReference());
				String incomingXrefUniqueId = crossReferenceService.getCrossReferenceUniqueId(incomingPreferredXref);
				if (mergedXrefUniqueIdsMap.containsKey(incomingXrefUniqueId)) {
					externalDBEntity.setPreferredCrossReference(mergedXrefUniqueIdsMap.get(incomingXrefUniqueId));
				} else {
					externalDBEntity.setPreferredCrossReference(crossReferenceDAO.persist(incomingPreferredXref));
				}
			}
		}

		externalDataBaseEntityDAO.persist(externalDBEntity);

		for (Long currentId : currentXrefIds) {
			if (!mergedXrefIds.contains(currentId)) {
				crossReferenceDAO.remove(currentId);
			}
		}

		if (externalDBEntityResponse.hasErrors()) {
			throw new ObjectValidationException(dto, externalDBEntityResponse.errorMessagesString());
		}
		
		return externalDBEntity;
	}

	private CrossReference createNewCrossReference(CrossReferenceFmsDTO dto) {
		CrossReference xref = new CrossReference();
		xref.setReferencedCurie(dto.getCurie());
		xref.setDisplayName(dto.getCurie());
		String prefix = dto.getCurie().indexOf(":") == -1 ? dto.getCurie() : dto.getCurie().substring(0, dto.getCurie().indexOf(":"));
		ResourceDescriptorPage rdp = resourceDescriptorPageService.getPageForResourceDescriptor(prefix, dto.getPages().get(0));
		if (rdp != null) {
			xref.setResourceDescriptorPage(rdp);
		}
		return xref;
	}
}
