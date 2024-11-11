package org.alliancegenome.curation_api.services.ontology;

import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alliancegenome.curation_api.dao.ontology.NcbiTaxonTermDAO;
import org.alliancegenome.curation_api.interfaces.ncbi.NCBIRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.ingest.NCBITaxonResponseDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import si.mazi.rescu.RestProxyFactory;

@RequestScoped
public class NcbiTaxonTermService extends BaseOntologyTermService<NCBITaxonTerm, NcbiTaxonTermDAO> {

	@Inject NcbiTaxonTermDAO ncbiTaxonTermDAO;

	Date taxonRequest;
	HashMap<String, NCBITaxonTerm> taxonCacheMap = new HashMap<>();

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(ncbiTaxonTermDAO);
	}

	private static final int MAX_ATTEMPTS = 5;

	private NCBIRESTInterface api = RestProxyFactory.createProxy(NCBIRESTInterface.class, "https://eutils.ncbi.nlm.nih.gov");

	@Override
	public ObjectResponse<NCBITaxonTerm> getByCurie(String taxonCurie) {
		NCBITaxonTerm term = null;

		if (taxonRequest != null) {
			if (taxonCacheMap.containsKey(taxonCurie)) {
				term = taxonCacheMap.get(taxonCurie);
			} else {
				Log.debug("Term not cached, caching term: (" + taxonCurie + ")");
				term = getTaxonFromDB(taxonCurie);
				taxonCacheMap.put(taxonCurie, term);
			}
		} else {
			term = getTaxonFromDB(taxonCurie);
			taxonRequest = new Date();
		}

		ObjectResponse<NCBITaxonTerm> response = new ObjectResponse<>();
		response.setEntity(term);
		return response;
	}

	@Transactional
	public NCBITaxonTerm getTaxonFromDB(String taxonCurie) {
		NCBITaxonTerm taxon = findByCurie(taxonCurie);
		if (taxon == null) {
			taxon = downloadAndSave(taxonCurie);
			if (taxon == null) {
				Log.warn("Taxon ID could not be found");
			}
		}
		return taxon;
	}

	private NCBITaxonTerm downloadAndSave(String taxonCurie) {

		Pattern taxonIdPattern = Pattern.compile("^NCBITaxon:(\\d+)$");
		Matcher taxonIdMatcher = taxonIdPattern.matcher(taxonCurie);
		if (!taxonIdMatcher.find()) {
			return null;
		}

		HashMap<String, Object> taxonMap = null;
		int attemptsRemaining = MAX_ATTEMPTS;
		while (attemptsRemaining-- > 0) {
			try {
				NCBITaxonResponseDTO resp = api.getTaxonFromNCBI("taxonomy", "json", taxonIdMatcher.group(1));
				HashMap<String, Object> result = resp.getResult();
				taxonMap = (HashMap<String, Object>) result.get(taxonIdMatcher.group(1));

				break;
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}

		if (taxonMap == null || taxonMap.get("error") != null) {
			return null;
		}

		String name = (String) taxonMap.get("scientificname");
		NCBITaxonTerm taxon = new NCBITaxonTerm();
		taxon.setName(name);
		taxon.setCurie(taxonCurie);
		if (taxonMap.get("status").equals("active")) {
			taxon.setObsolete(false);
		} else {
			taxon.setObsolete(true);
		}

		return ncbiTaxonTermDAO.persist(taxon);
	}

}
