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
		if (dbEntity == null) {
			dataProviderDAO.persist(entity);
			if (!entity.getSourceOrganization().getAbbreviation().equals("FB")) {
				Integer update = crossReferenceDAO.persistAccessionGeneAssociated(entity.getCrossReference().getId(), geneID);
			}
			return new ObjectResponse<>(entity);
		}
		return new ObjectResponse<>(dbEntity);
	}

	@NotNull
	public static String getFullReferencedCurie(String localReferencedCurie) {
		return RESOURCE_DESCRIPTOR_PREFIX + ":" + localReferencedCurie;
	}

	Map<String, Long> accessionGeneMap = new HashMap<>();
	public static String RESOURCE_DESCRIPTOR_PREFIX = "ENSEMBL";
	public static final String RESOURCE_DESCRIPTOR_PAGE_NAME = "default";

	private Long getAssociatedGeneId(String fullReferencedCurie, Organization sourceOrganization) {
		if (accessionGeneMap.size() == 0) {
			if (sourceOrganization.getAbbreviation().equals("FB")) {
				Map<String, Object> map = new HashMap<>();
				map.put("displayName", sourceOrganization.getAbbreviation());
				Species species = speciesDAO.findByParams(map).getSingleResult();
				accessionGeneMap = geneDAO.getAllGeneIdsPerSpecies(species);
				fullReferencedCurie = "FB:" + fullReferencedCurie;
				return accessionGeneMap.get(fullReferencedCurie);
			} else {
				ResourceDescriptorPage page = resourceDescriptorPageService.getPageForResourceDescriptor(RESOURCE_DESCRIPTOR_PREFIX, RESOURCE_DESCRIPTOR_PAGE_NAME);
				accessionGeneMap = crossReferenceDAO.getGenesWithCrossRefs(page);
			}
		}
		return accessionGeneMap.get(fullReferencedCurie);
	}

	// <crossReference.referencedCurie, DataProvider>
	HashMap<String, DataProvider> dataProviderMap = new HashMap<>();

	private DataProvider getDataProvider(Organization sourceOrganization, String crossReferenceCurie, ResourceDescriptorPage page) {
		if (dataProviderMap.size() > 0) {
			return dataProviderMap.get(crossReferenceCurie);
		}
		populateDataProviderMap(sourceOrganization, page);
		return dataProviderMap.get(crossReferenceCurie);
	}

	private void populateDataProviderMap(Organization sourceOrganization, ResourceDescriptorPage page) {
		HashMap<String, Object> params = new HashMap<>();
		params.put("sourceOrganization.abbreviation", sourceOrganization.getAbbreviation());
		params.put("crossReference.resourceDescriptorPage.name", page.getName());
		List<DataProvider> allOrgProvider = dataProviderDAO.getAllDataProvider(params);
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


	public ObjectResponse<DataProvider> validate(DataProvider uiEntity) {
		return dataProviderValidator.validateDataProvider(uiEntity, null, true);
	}


}
