package org.alliancegenome.curation_api.services;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.ingest.dto.fms.CrossReferenceFmsDTO;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestScoped
public class CrossReferenceService extends BaseEntityCrudService<CrossReference, CrossReferenceDAO> {

	@Inject
	CrossReferenceDAO crossReferenceDAO;
	@Inject
	ResourceDescriptorPageService resourceDescriptorPageService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(crossReferenceDAO);
	}

	public List<CrossReference> getMergedFmsXrefList(List<CrossReferenceFmsDTO> fmsCrossReferences, List<CrossReference> existingXrefs) {
		List<CrossReference> incomingXrefs = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(fmsCrossReferences)) {
			for (CrossReferenceFmsDTO fmsXref : fmsCrossReferences) {
				String xrefCurie = fmsXref.getCurie();
				if (CollectionUtils.isEmpty(fmsXref.getPages())) {
					fmsXref.getPages().add("default");
				}
				for (String xrefPage : fmsXref.getPages()) {
					CrossReference xref = new CrossReference();
					xref.setReferencedCurie(xrefCurie);
					xref.setDisplayName(xrefCurie);
					String prefix = xrefCurie.indexOf(":") == -1 ? xrefCurie : xrefCurie.substring(0, xrefCurie.indexOf(":"));
					ResourceDescriptorPage rdp = resourceDescriptorPageService.getPageForResourceDescriptor(prefix, xrefPage);
					if (rdp != null) {
						xref.setResourceDescriptorPage(rdp);
						incomingXrefs.add(xref);
					}
				}
			}
		}

		return getUpdatedXrefList(incomingXrefs, existingXrefs);
	}

	@Transactional
	public List<CrossReference> getUpdatedXrefList(List<CrossReference> incomingXrefs, List<CrossReference> existingXrefs) {
		Map<String, CrossReference> existingXrefMap = new HashMap<>();
		if (CollectionUtils.isNotEmpty(existingXrefs)) {
			for (CrossReference existingXref : existingXrefs) {
				existingXrefMap.put(getCrossReferenceUniqueId(existingXref), existingXref);
			}
		}

		List<CrossReference> finalXrefs = new ArrayList<>();
		List<String> addedXrefUniqueIds = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(incomingXrefs)) {
			for (CrossReference incomingXref : incomingXrefs) {
				String incomingXrefUniqueId = getCrossReferenceUniqueId(incomingXref);
				if (!addedXrefUniqueIds.contains(incomingXrefUniqueId)) {
					if (existingXrefMap.containsKey(incomingXrefUniqueId)) {
						finalXrefs.add(updateCrossReference(existingXrefMap.get(incomingXrefUniqueId), incomingXref));
					} else {
						finalXrefs.add(crossReferenceDAO.persist(incomingXref));
					}
					addedXrefUniqueIds.add(incomingXrefUniqueId);
				}
			}
		}

		return finalXrefs;
	}

	public CrossReference updateCrossReference(CrossReference oldXref, CrossReference newXref) {
		oldXref.setDisplayName(newXref.getDisplayName());
		oldXref.setCreatedBy(newXref.getCreatedBy());
		oldXref.setUpdatedBy(newXref.getUpdatedBy());
		oldXref.setDateCreated(newXref.getDateCreated());
		oldXref.setDateUpdated(newXref.getDateUpdated());
		oldXref.setInternal(newXref.getInternal());
		oldXref.setObsolete(newXref.getObsolete());

		return oldXref;
	}

	public String getCrossReferenceUniqueId(CrossReference xref) {
		if (xref == null) {
			return null;
		}
		if (xref.getResourceDescriptorPage() == null) {
			return xref.getReferencedCurie();
		}
		return StringUtils.join(List.of(xref.getReferencedCurie(), xref.getResourceDescriptorPage().getId()), "|");
	}

	public Map<String, Long> getGenomicEntityCrossRefMap(ResourceDescriptorPage page) {
		return crossReferenceDAO.getGenesWithCrossRefs(page);
	}
}
