package org.alliancegenome.curation_api.services;

import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.LiteratureReferenceDAO;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.model.document.LiteratureReference;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;

@RequestScoped
public class ReferenceService extends BaseCrudService<Reference, ReferenceDAO> {

	@Inject
	ReferenceDAO referenceDAO;
	@Inject
	LiteratureReferenceDAO literatureReferenceDAO;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(referenceDAO);
	}
	
	@Transactional
	public Reference retrieveFromLiteratureService(String xrefCurie) {
		Pagination pagination = new Pagination();
		pagination.setPage(0);
		pagination.setLimit(2);
		
		HashMap<String, String> searchDetails = new HashMap<>();
		searchDetails.put("tokenOperator", "AND");
		searchDetails.put("queryString", xrefCurie);
		
		HashMap<String, Object> searchField = new HashMap<>();
		searchField.put("cross_reference.curie", searchDetails);
		searchField.put("curie", searchDetails);

		HashMap<String, Object> filter = new HashMap<>();
		filter.put("cross_referenceFilter", searchField);
		
		HashMap<String, Object> params = new HashMap<>();
		params.put("searchFilters", filter);		
		
		SearchResponse<LiteratureReference> response = literatureReferenceDAO.searchByParams(pagination, params);
		if (response == null || response.getResults().size() != 1)
			return null;
		
		LiteratureReference litRef = response.getSingleResult();
		
		Reference reference = new Reference();
		reference.setDisplayXref(xrefCurie);
		reference.setCurie(litRef.getCurie());
		reference.setLiteratureCrossReferences(litRef.getCross_reference());
		return referenceDAO.persist(reference);
	}
}
