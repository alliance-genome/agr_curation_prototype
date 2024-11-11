package org.alliancegenome.curation_api.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Chromosome;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ChromosomeDAO extends BaseSQLDAO<Chromosome> {

	@Inject AssemblyComponentDAO assemblyComponentDAO;
	
	protected ChromosomeDAO() {
		super(Chromosome.class);
	}

	public Boolean hasReferencingAssemblyComponents(Long chromosomeId) {
		
		Map<String, Object> params = new HashMap<>();
		params.put("mapsToChromosome.id", chromosomeId);
		List<Long> results = assemblyComponentDAO.findIdsByParams(params);
		
		return CollectionUtils.isNotEmpty(results);
	}

}
