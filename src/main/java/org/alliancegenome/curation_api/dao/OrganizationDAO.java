package org.alliancegenome.curation_api.dao;

import java.util.HashMap;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.response.SearchResponse;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrganizationDAO extends BaseSQLDAO<Organization> {

	private HashMap<String, Organization> organizationCache = new HashMap<>();

	protected OrganizationDAO() {
		super(Organization.class);
	}

	public Organization getOrCreateOrganization(String abbreviation) {
		if (organizationCache.containsKey(abbreviation)) {
			return organizationCache.get(abbreviation);
		} else {
			HashMap<String, Object> params = new HashMap<>();
			params.put("abbreviation", abbreviation);
			SearchResponse<Organization> resp = findByParams(params);
			if (resp != null) {
				organizationCache.put(abbreviation, resp.getSingleResult());
				return resp.getSingleResult();
			} else {
				Organization o = new Organization();
				o.setAbbreviation(abbreviation);
				organizationCache.put(abbreviation, o);
				return persist(o);
			}
		}
	}
}
