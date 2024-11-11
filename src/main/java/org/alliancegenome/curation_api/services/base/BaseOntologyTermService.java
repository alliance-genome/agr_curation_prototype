package org.alliancegenome.curation_api.services.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.auth.AuthenticatedUser;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.SynonymDAO;
import org.alliancegenome.curation_api.dao.base.BaseEntityDAO;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.CrossReferenceService;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

public abstract class BaseOntologyTermService<E extends OntologyTerm, D extends BaseEntityDAO<E>> extends BaseEntityCrudService<E, BaseEntityDAO<E>> {

	@Inject CrossReferenceDAO crossReferenceDAO;
	@Inject SynonymDAO synonymDAO;
	@Inject CrossReferenceService crossReferenceService;

	@Inject
	@AuthenticatedUser Person authenticatedPerson;

	public E findByCurieOrSecondaryId(String id) {
		return findByAlternativeFields(List.of("curie", "secondaryIdentifiers"), id);
	}

	@Transactional
	public E processUpdate(E inTerm) {

		E term = findByCurie(inTerm.getCurie());

		boolean newTerm = false;
		if (term == null) {
			term = dao.getNewInstance();
			term.setCurie(inTerm.getCurie());
			newTerm = true;
		}

		term.setName(inTerm.getName());
		term.setType(inTerm.getType());
		term.setObsolete(inTerm.getObsolete());
		term.setNamespace(inTerm.getNamespace());
		term.setDefinition(inTerm.getDefinition());

		handleSubsets(term, inTerm);
		handleDefinitionUrls(term, inTerm);
		handleSecondaryIds(term, inTerm);
		handleSynonyms(term, inTerm);
		handleCrossReferences(term, inTerm);

		if (newTerm) {
			return dao.persist(term);
		} else {
			return term;
		}
	}

	@Transactional
	public E processUpdateRelationships(E inTerm) {
		E term = findByCurie(inTerm.getCurie());

		HashSet<OntologyTerm> parentSet = new HashSet<>();
		if (inTerm.getIsaParents() != null) {
			inTerm.getIsaParents().forEach(o -> {
				E parent = findByCurie(o.getCurie());
				parentSet.add(parent);
			});
		}
		term.setIsaParents(parentSet);

		HashSet<OntologyTerm> ancestorsSet = new HashSet<>();
		if (inTerm.getIsaAncestors() != null) {
			inTerm.getIsaAncestors().forEach(o -> {
				E ancestor = findByCurie(o.getCurie());
				ancestorsSet.add(ancestor);
			});
		}
		term.setIsaAncestors(ancestorsSet);

		return term;
	}

	@Transactional
	public E processCounts(E inTerm) {
		E term = findByCurie(inTerm.getCurie());
		term.setChildCount(term.getIsaChildren().size());
		term.setDescendantCount(term.getIsaDescendants().size());
		return term;
	}

	private void handleDefinitionUrls(OntologyTerm dbTerm, OntologyTerm incomingTerm) {
		Set<String> currentDefinitionUrls;
		if (dbTerm.getDefinitionUrls() == null) {
			currentDefinitionUrls = new HashSet<>();
			dbTerm.setDefinitionUrls(new ArrayList<>());
		} else {
			currentDefinitionUrls = dbTerm.getDefinitionUrls().stream().collect(Collectors.toSet());
		}

		Set<String> newDefinitionUrls;
		if (incomingTerm.getDefinitionUrls() == null) {
			newDefinitionUrls = new HashSet<>();
		} else {
			newDefinitionUrls = incomingTerm.getDefinitionUrls().stream().collect(Collectors.toSet());
		}

		newDefinitionUrls.forEach(id -> {
			if (!currentDefinitionUrls.contains(id)) {
				dbTerm.getDefinitionUrls().add(id);
			}
		});

		currentDefinitionUrls.forEach(id -> {
			if (!newDefinitionUrls.contains(id)) {
				dbTerm.getDefinitionUrls().remove(id);
			}
		});

	}

	public ObjectListResponse<E> getRootNodes() {
		HashMap<String, Object> params = new HashMap<>();
		params.put("isaParents", null);
		SearchResponse<E> rootNodesRes = dao.findByParams(params);
		if (rootNodesRes != null) {
			return new ObjectListResponse<E>(rootNodesRes.getResults());
		} else {
			return new ObjectListResponse<E>();
		}
	}

	public ObjectListResponse<E> getChildren(String curie) {
		E term = findByCurie(curie);
		if (term != null) {
			return (ObjectListResponse<E>) new ObjectListResponse<OntologyTerm>(term.getIsaChildren());
		} else {
			return new ObjectListResponse<E>();
		}
	}

	public ObjectListResponse<E> getDescendants(String curie) {
		E term = findByCurie(curie);
		if (term != null) {
			return (ObjectListResponse<E>) new ObjectListResponse<OntologyTerm>(term.getIsaDescendants());
		} else {
			return new ObjectListResponse<E>();
		}
	}

	public ObjectListResponse<E> getParents(String curie) {
		E term = findByCurie(curie);
		if (term != null) {
			return (ObjectListResponse<E>) new ObjectListResponse<OntologyTerm>(term.getIsaParents());
		} else {
			return new ObjectListResponse<E>();
		}
	}

	public ObjectListResponse<E> getAncestors(String curie) {
		E term = findByCurie(curie);
		if (term != null) {
			return (ObjectListResponse<E>) new ObjectListResponse<OntologyTerm>(term.getIsaAncestors());
		} else {
			return new ObjectListResponse<E>();
		}
	}

	private void handleSubsets(OntologyTerm dbTerm, OntologyTerm incomingTerm) {
		Set<String> currentSubsets;
		if (dbTerm.getSubsets() == null) {
			currentSubsets = new HashSet<>();
			dbTerm.setSubsets(new ArrayList<>());
		} else {
			currentSubsets = dbTerm.getSubsets().stream().collect(Collectors.toSet());
		}

		Set<String> newSubsets;
		if (incomingTerm.getSubsets() == null) {
			newSubsets = new HashSet<>();
		} else {
			newSubsets = incomingTerm.getSubsets().stream().collect(Collectors.toSet());
		}

		newSubsets.forEach(id -> {
			if (!currentSubsets.contains(id)) {
				dbTerm.getSubsets().add(id);
			}
		});

		currentSubsets.forEach(id -> {
			if (!newSubsets.contains(id)) {
				dbTerm.getSubsets().remove(id);
			}
		});

	}

	private void handleSynonyms(OntologyTerm dbTerm, OntologyTerm incomingTerm) {
		Set<Synonym> currentSynonyms;
		if (dbTerm.getSynonyms() == null) {
			currentSynonyms = new HashSet<>();
			dbTerm.setSynonyms(new ArrayList<>());
		} else {
			currentSynonyms = dbTerm.getSynonyms().stream().collect(Collectors.toSet());
		}
		List<String> currentSynonymNames = currentSynonyms.stream().map(Synonym::getName).collect(Collectors.toList());

		Set<Synonym> newSynonyms;
		if (incomingTerm.getSynonyms() == null) {
			newSynonyms = new HashSet<>();
		} else {
			newSynonyms = incomingTerm.getSynonyms().stream().collect(Collectors.toSet());
		}
		List<String> newSynonymNames = currentSynonyms.stream().map(Synonym::getName).collect(Collectors.toList());

		newSynonyms.forEach(syn -> {
			if (!currentSynonymNames.contains(syn.getName())) {
				SearchResponse<Synonym> response = synonymDAO.findByField("name", syn.getName());
				Synonym synonym;
				if (response == null) {
					synonym = synonymDAO.persist(syn);
				} else {
					synonym = response.getSingleResult();
				}
				dbTerm.getSynonyms().add(synonym);
			}
		});

		currentSynonyms.forEach(syn -> {
			if (!newSynonymNames.contains(syn.getName())) {
				dbTerm.getSynonyms().remove(syn);
			}
		});

	}

	private void handleSecondaryIds(OntologyTerm dbTerm, OntologyTerm incomingTerm) {
		Set<String> currentIds;
		if (dbTerm.getSecondaryIdentifiers() == null) {
			currentIds = new HashSet<>();
			dbTerm.setSecondaryIdentifiers(new ArrayList<>());
		} else {
			currentIds = dbTerm.getSecondaryIdentifiers().stream().collect(Collectors.toSet());
		}

		Set<String> newIds;
		if (incomingTerm.getSecondaryIdentifiers() == null) {
			newIds = new HashSet<>();
		} else {
			newIds = incomingTerm.getSecondaryIdentifiers().stream().collect(Collectors.toSet());
		}

		newIds.forEach(id -> {
			if (!currentIds.contains(id)) {
				dbTerm.getSecondaryIdentifiers().add(id);
			}
		});

		currentIds.forEach(id -> {
			if (!newIds.contains(id)) {
				dbTerm.getSecondaryIdentifiers().remove(id);
			}
		});

	}

	private void handleCrossReferences(OntologyTerm dbTerm, OntologyTerm incomingTerm) {
		List<Long> currentIds;
		if (dbTerm.getCrossReferences() == null) {
			currentIds = new ArrayList<>();
		} else {
			currentIds = dbTerm.getCrossReferences().stream().map(CrossReference::getId).collect(Collectors.toList());
		}

		List<Long> mergedIds;
		if (incomingTerm.getCrossReferences() == null) {
			mergedIds = new ArrayList<>();
			dbTerm.setCrossReferences(null);
		} else {
			List<CrossReference> mergedCrossReferences = crossReferenceService.getUpdatedXrefList(incomingTerm.getCrossReferences(), dbTerm.getCrossReferences());
			mergedIds = mergedCrossReferences.stream().map(CrossReference::getId).collect(Collectors.toList());
			dbTerm.setCrossReferences(mergedCrossReferences);
		}

		for (Long currentId : currentIds) {
			if (!mergedIds.contains(currentId)) {
				crossReferenceDAO.remove(currentId);
			}
		}
	}

	public <T extends OntologyTerm> T findSubsetTerm(T childTerm, String subsetName) {
		for (OntologyTerm term : childTerm.getIsaAncestors()) {
			for (String subset : term.getSubsets()) {
				if (subset.contains(subsetName)) {
					return (T) term;
				}
			}
		}
		return null;
	}

}
