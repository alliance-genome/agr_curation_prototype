package org.alliancegenome.curation_api.services;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.alliancegenome.curation_api.auth.AuthenticatedUser;
import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.DataProviderValidator;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequestScoped
public class DataProviderService extends BaseEntityCrudService<DataProvider, DataProviderDAO> {

	public static final String RESOURCE_DESCRIPTOR_PREFIX = "ENSEMBL";
	public static final String RESOURCE_DESCRIPTOR_PAGE_NAME = "default";
	// <crossReference.referencedCurie, DataProvider>
	Map<String, Long> accessionGeneMap = new HashMap<>();
	HashMap<String, DataProvider> dataProviderMap = new HashMap<>();


	@Inject
	@AuthenticatedUser
	protected Person authenticatedPerson;
	@Inject
	SpeciesDAO speciesDAO;
	@Inject
	DataProviderDAO dataProviderDAO;
	@Inject
	CrossReferenceDAO crossReferenceDAO;
	@Inject
	ResourceDescriptorPageService resourceDescriptorPageService;
	@Inject
	OrganizationDAO organizationDAO;
	@Inject
	GeneDAO geneDAO;
	@Inject
	DataProviderValidator dataProviderValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(dataProviderDAO);
	}

	@Transactional
	public DataProvider createAffiliatedModDataProvider() {
		AllianceMember member = authenticatedPerson.getAllianceMember();
		if (member == null) {
			return getAllianceDataProvider();
		} else {
			return dataProviderDAO.getOrCreateDataProvider(member);
		}
	}

	public DataProvider getAllianceDataProvider() {
		return getDefaultDataProvider("Alliance");
	}

	@Transactional
	public DataProvider getDefaultDataProvider(String sourceOrganizationAbbreviation) {
		return dataProviderDAO.getOrCreateDataProvider(organizationDAO.getOrCreateOrganization(sourceOrganizationAbbreviation));
	}

	@Transactional
	public ObjectResponse<DataProvider> insertExpressionAtlasDataProvider(DataProvider entity) {
		String referencedCurie = entity.getCrossReference().getReferencedCurie();
		// find associated gene
		Long geneID = getAssociatedGeneId(referencedCurie, entity.getSourceOrganization());
		// if no gene found skip (= don't import) the accession
		if (geneID == null) {
			return new ObjectResponse<>();
		}

		DataProvider dbEntity = getDataProvider(entity.getSourceOrganization(), referencedCurie, entity.getCrossReference().getResourceDescriptorPage());
		// we only create new records, no updates
		if (dbEntity == null) {
			dataProviderDAO.persist(entity);
			if (!List.of("FB", "SGD").contains(entity.getSourceOrganization().getAbbreviation())) {
				crossReferenceDAO.persistAccessionGeneAssociated(entity.getCrossReference().getId(), geneID);
			}
			return new ObjectResponse<>(entity);
		}
		return new ObjectResponse<>(dbEntity);
	}

	@NotNull
	public static String getFullReferencedCurie(String localReferencedCurie) {
		return RESOURCE_DESCRIPTOR_PREFIX + ":" + localReferencedCurie;
	}

	private Long getAssociatedGeneId(String fullReferencedCurie, Organization sourceOrganization) {
		String orgAbbreviation = sourceOrganization.getAbbreviation();
		if (orgAbbreviation.equals("FB")) {
			fullReferencedCurie = orgAbbreviation + ":" + fullReferencedCurie;
		}
		if (accessionGeneMap.size() == 0) {
			if (List.of("FB", "SGD").contains(orgAbbreviation)) {
				Map<String, Object> map = new HashMap<>();
				map.put("displayName", orgAbbreviation);
				Species species = speciesDAO.findByParams(map).getSingleResult();
				accessionGeneMap = geneDAO.getAllGeneIdsPerSpecies(species);
			} else {
				ResourceDescriptorPage page = resourceDescriptorPageService.getPageForResourceDescriptor(RESOURCE_DESCRIPTOR_PREFIX, RESOURCE_DESCRIPTOR_PAGE_NAME);
				accessionGeneMap = crossReferenceDAO.getGenesWithCrossRefs(page);
			}
		}
		return accessionGeneMap.get(fullReferencedCurie);
	}

	private DataProvider getDataProvider(Organization sourceOrganization, String crossReferenceCurie, ResourceDescriptorPage page) {
		if (dataProviderMap.size() > 0) {
			return dataProviderMap.get(crossReferenceCurie);
		}
		populateDataProviderMap(sourceOrganization, page);
		return dataProviderMap.get(crossReferenceCurie);
	}

	private void populateDataProviderMap(Organization sourceOrganization, ResourceDescriptorPage page) {
		List<DataProvider> allOrgProvider = dataProviderDAO.getAllDataProvider(sourceOrganization, page);
		allOrgProvider.stream()
			.filter(dataProvider -> dataProvider.getCrossReference() != null && Objects.equals(dataProvider.getCrossReference().getResourceDescriptorPage().getId(), page.getId()))
			.forEach(dataProvider -> {
				dataProviderMap.put(dataProvider.getCrossReference().getReferencedCurie(), dataProvider);
			});
	}

	public HashMap<String, DataProvider> getDataProviderMap(Organization sourceOrganization, ResourceDescriptorPage page) {
		if (dataProviderMap.size() > 0) {
			return dataProviderMap;
		}
		populateDataProviderMap(sourceOrganization, page);
		return dataProviderMap;
	}

	@Transactional
	public ObjectResponse<DataProvider> upsert(DataProvider uiEntity) {
		ObjectResponse<DataProvider> response = dataProviderValidator.validateDataProvider(uiEntity, null, true);
		if (response.getEntity() == null) {
			return response;
		}
		return new ObjectResponse<>(response.getEntity());
	}

	public ObjectResponse<DataProvider> validate(DataProvider uiEntity) {
		return dataProviderValidator.validateDataProvider(uiEntity, null, true);
	}


}
