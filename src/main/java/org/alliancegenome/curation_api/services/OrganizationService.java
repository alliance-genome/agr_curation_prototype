package org.alliancegenome.curation_api.services;

import java.util.Date;
import java.util.HashMap;

import org.alliancegenome.curation_api.dao.OrganizationDAO;
import org.alliancegenome.curation_api.model.entities.AllianceMember;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class OrganizationService extends BaseEntityCrudService<Organization, OrganizationDAO> {

	@Inject
	OrganizationDAO organizationDAO;

	Date orgRequest;
	HashMap<Long, Organization> orgIdCacheMap = new HashMap<>();
	HashMap<String, Organization> orgAbbrCacheMap = new HashMap<>();

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(organizationDAO);
	}

	public ObjectResponse<Organization> getById(Long id) {

		Organization org = null;

		if (orgRequest != null) {
			if (orgIdCacheMap.containsKey(id)) {
				org = orgIdCacheMap.get(id);
			} else {
				Log.debug("Org not cached, caching org: (" + id + ")");
				org = organizationDAO.find(id);
				orgIdCacheMap.put(id, org);
			}
		} else {
			org = organizationDAO.find(id);
			orgRequest = new Date();
		}

		ObjectResponse<Organization> response = new ObjectResponse<>();
		response.setEntity(org);
		return response;
	}

	public ObjectResponse<Organization> getByAbbr(String abbr) {

		Organization org = null;
		SearchResponse<Organization> orgResponse;

		if (orgRequest != null) {
			if (orgAbbrCacheMap.containsKey(abbr)) {
				org = orgAbbrCacheMap.get(abbr);
			} else {
				Log.debug("Org not cached, caching org: (" + abbr + ")");
				orgResponse = organizationDAO.findByField("abbreviation", abbr);
				if (orgResponse != null) {
					org = orgResponse.getSingleResult();
				}
				orgAbbrCacheMap.put(abbr, org);
			}
		} else {
			orgResponse = organizationDAO.findByField("abbreviation", abbr);
			if (orgResponse != null) {
				org = orgResponse.getSingleResult();
			}
			orgRequest = new Date();
		}

		ObjectResponse<Organization> response = new ObjectResponse<>();
		response.setEntity(org);
		return response;
	}
	
	public Organization getAffiliatedModDataProvider() {
		AllianceMember member = authenticatedPerson.getAllianceMember();
		if (member != null) {
			return member;
		}
			
		return organizationDAO.getOrCreateOrganization("Alliance");
	}

}
