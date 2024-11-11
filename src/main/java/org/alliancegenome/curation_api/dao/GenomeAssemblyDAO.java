package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.GenomeAssembly;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GenomeAssemblyDAO extends BaseSQLDAO<GenomeAssembly> {

	protected GenomeAssemblyDAO() {
		super(GenomeAssembly.class);
	}

}
