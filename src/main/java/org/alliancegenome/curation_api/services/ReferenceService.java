package org.alliancegenome.curation_api.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.helpers.references.ReferenceSynchronisationHelper;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class ReferenceService extends BaseEntityCrudService<Reference, ReferenceDAO> {

	@Inject ReferenceDAO referenceDAO;
	@Inject ReferenceSynchronisationHelper refSyncHelper;
	
	Date referenceRequest;
	HashMap<String, Reference> referenceCacheMap = new HashMap<>();

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(referenceDAO);
	}

	public ObjectResponse<Reference> synchroniseReference(Long id) {
		return refSyncHelper.synchroniseReference(id);
	}

	public void synchroniseReferences() {
		refSyncHelper.synchroniseReferences();
	}

	@Override
	public ObjectResponse<Reference> getByCurie(String curie) {
		Reference reference = retrieveFromDbOrLiteratureService(curie);
		ObjectResponse<Reference> ret = new ObjectResponse<Reference>(reference);
		return ret;
	}

	@Transactional
	public Reference retrieveFromDbOrLiteratureService(String curieOrXref) {
		Reference reference = null;
		if (referenceRequest != null) {
			if (referenceCacheMap.containsKey(curieOrXref)) {
				reference = referenceCacheMap.get(curieOrXref);
			} else {
				Log.debug("Reference not cached, caching reference: (" + curieOrXref + ")");
				reference = findOrCreateReference(curieOrXref);
				referenceCacheMap.put(curieOrXref, reference);
			}
		} else {
			reference = findOrCreateReference(curieOrXref);
			referenceRequest = new Date();
		}
		return reference;
	}
	
	private Reference findOrCreateReference(String curieOrXref) {
		Reference reference = null;

		if (curieOrXref.startsWith("AGRKB:")) {
			reference = findByCurie(curieOrXref);
		} else {
			SearchResponse<Reference> response = referenceDAO.findByField("crossReferences.referencedCurie", curieOrXref);
			List<Reference> nonObsoleteRefs = new ArrayList<>();
			if (response != null && response.getReturnedRecords() > 0) {
				response.getResults().forEach(ref -> {
					if (!ref.getObsolete()) {
						nonObsoleteRefs.add(ref);
					}
				});
			}
			if (nonObsoleteRefs.size() == 1) {
				reference = nonObsoleteRefs.get(0);
			}
		}

		if (reference != null && (!reference.getObsolete() || curieOrXref.startsWith("AGRKB:"))) {
			return reference;
		}

		reference = refSyncHelper.retrieveFromLiteratureService(curieOrXref);

		if (reference == null) {
			return null;
		}

		return referenceDAO.persist(reference);
	}

}
