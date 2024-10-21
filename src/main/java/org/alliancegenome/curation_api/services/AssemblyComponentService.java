package org.alliancegenome.curation_api.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.AssemblyComponentDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.AssemblyComponent;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.helpers.UniqueIdGeneratorHelper;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class AssemblyComponentService extends BaseEntityCrudService<AssemblyComponent, AssemblyComponentDAO> {

	@Inject AssemblyComponentDAO assemblyComponentDAO;
	@Inject GenomeAssemblyService genomeAssemblyService;
	@Inject NcbiTaxonTermService ncbiTaxonTermService;
	@Inject DataProviderService dataProviderService;

	Date assemblyComponentRequest;
	HashMap<String, AssemblyComponent> assemblyComponentCacheMap = new HashMap<>();

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(assemblyComponentDAO);
	}

	@Transactional
	public AssemblyComponent fetchOrCreate(String name, String assemblyId, String taxonCurie, BackendBulkDataProvider dataProvider) {
		AssemblyComponent assemblyComponent = null;
		if (assemblyComponentRequest != null) {
			UniqueIdGeneratorHelper uniqueIdGen = new UniqueIdGeneratorHelper();
			uniqueIdGen.add(name);
			uniqueIdGen.add(assemblyId);
			uniqueIdGen.add(taxonCurie);
			uniqueIdGen.add(dataProvider.sourceOrganization);
			String uniqueId = uniqueIdGen.getUniqueId();
			if (assemblyComponentCacheMap.containsKey(uniqueId)) {
				assemblyComponent = assemblyComponentCacheMap.get(uniqueId);
			} else {
				Log.debug("AssemblyComponent not cached, caching name|assembly: (" + uniqueId + ")");
				assemblyComponent = findAssemblyComponentOrCreateDB(name, assemblyId, taxonCurie, dataProvider);
				assemblyComponentCacheMap.put(uniqueId, assemblyComponent);
			}
		} else {
			assemblyComponent = findAssemblyComponentOrCreateDB(name, assemblyId, taxonCurie, dataProvider);
			assemblyComponentRequest = new Date();
		}
		return assemblyComponent;
	}

	private AssemblyComponent findAssemblyComponentOrCreateDB(String name, String assemblyId, String taxonCurie, BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", name);
		params.put(EntityFieldConstants.ASSEMBLY, assemblyId);
		params.put(EntityFieldConstants.TAXON, taxonCurie);
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
		SearchResponse<AssemblyComponent> assemblyComponentResponse = assemblyComponentDAO.findByParams(params);
		if (assemblyComponentResponse != null && assemblyComponentResponse.getResults().size() > 0) {
			return assemblyComponentResponse.getSingleResult();
		}
		AssemblyComponent assemblyComponent = new AssemblyComponent();
		assemblyComponent.setName(name);
		assemblyComponent.setGenomeAssembly(genomeAssemblyService.getOrCreate(assemblyId, dataProvider));
		assemblyComponent.setTaxon(ncbiTaxonTermService.getByCurie(taxonCurie).getEntity());
		assemblyComponent.setDataProvider(dataProviderService.getDefaultDataProvider(dataProvider.sourceOrganization));
		return assemblyComponentDAO.persist(assemblyComponent);
	}

	public ObjectResponse<AssemblyComponent> deleteByIdentifier(String identifierString) {
		AssemblyComponent assemblyComponent = findByAlternativeFields(List.of("modEntityId", "modInternalId"), identifierString);
		if (assemblyComponent != null) {
			assemblyComponentDAO.remove(assemblyComponent.getId());
		}
		ObjectResponse<AssemblyComponent> ret = new ObjectResponse<>(assemblyComponent);
		return ret;
	}

}
