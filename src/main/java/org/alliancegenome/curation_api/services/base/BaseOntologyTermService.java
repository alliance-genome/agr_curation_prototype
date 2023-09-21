package org.alliancegenome.curation_api.services.base;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

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

import jakarta.transaction.Transactional;

public abstract class BaseOntologyTermService<E extends OntologyTerm, D extends BaseEntityDAO<E>> extends BaseEntityCrudService<E, BaseEntityDAO<E>> {

	@Inject
	CrossReferenceDAO crossReferenceDAO;
	@Inject
	SynonymDAO synonymDAO;
	@Inject
	CrossReferenceService crossReferenceService;

	@Inject
	@AuthenticatedUser
	Person authenticatedPerson;

	public E findByCurieOrSecondaryId(String id) {
		
		E term = dao.find(id);
		if (term != null)
			return term;
		
		SearchResponse<E> response = dao.findByField("secondaryIdentifiers", id);
		if (response.getTotalResults() == 1)
			return response.getSingleResult();
		
		return null;
	}
	
	@Transactional
	public E processUpdate(E inTerm) {

		E term = dao.find(inTerm.getCurie());

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

		if(newTerm) {
			return dao.persist(term);
		} else {
			return term;
		}
	}

	@Transactional
	public E processUpdateRelationships(E inTerm) {
		// TODO: 01 - figure out issues with ontologies
		E term = dao.find(inTerm.getCurie());
		
		HashSet<String> incomingParents = new HashSet<>();
		HashSet<String> parentAdds = new HashSet<>();
		HashSet<String> dbParents = new HashSet<>();
		HashSet<String> parentDeletes = new HashSet<>();

		if(term.getIsaParents() != null) {
			term.getIsaParents().forEach(o -> {
				dbParents.add(o.getCurie());
				parentDeletes.add(o.getCurie());
			});
		}
		
		if(inTerm.getIsaParents() != null) {
			inTerm.getIsaParents().forEach(o -> {
				incomingParents.add(o.getCurie());
				parentAdds.add(o.getCurie());
			});
		}
		
		parentDeletes.removeAll(incomingParents);
		parentAdds.removeAll(dbParents);
		

		if(term.getIsaParents() != null) {
			term.getIsaParents().removeIf(f -> {
				return parentDeletes.contains(f.getCurie());
			});
			if(inTerm.getIsaParents() != null) {
				inTerm.getIsaParents().forEach(o -> {
					if(parentAdds.contains(o.getCurie())) {
						term.getIsaParents().add(o);
					}
				});
			}
		} else {
			HashSet<OntologyTerm> set = new HashSet<>();
			if(inTerm.getIsaParents() != null) {
				inTerm.getIsaParents().forEach(o -> {
					if(parentAdds.contains(o.getCurie())) {
						set.add(o);
					}
				});
				term.setIsaParents(set);
			}
		}

		
		HashSet<String> incomingAncestors = new HashSet<>();
		HashSet<String> ancestorAdds = new HashSet<>();
		HashSet<String> dbAncestors = new HashSet<>();
		HashSet<String> ancestorDeletes = new HashSet<>();

		if(term.getIsaAncestors() != null) {
			term.getIsaAncestors().forEach(o -> {
				dbAncestors.add(o.getCurie());
				ancestorDeletes.add(o.getCurie());
			});
		}
		
		if(inTerm.getIsaAncestors() != null) {
			inTerm.getIsaAncestors().forEach(o -> {
				incomingAncestors.add(o.getCurie());
				ancestorAdds.add(o.getCurie());
			});
		}
		
		ancestorDeletes.removeAll(incomingAncestors);
		ancestorAdds.removeAll(dbAncestors);
		

		if(term.getIsaAncestors() != null) {
			term.getIsaAncestors().removeIf(f -> {
				return ancestorDeletes.contains(f.getCurie());
			});
			if(inTerm.getIsaAncestors() != null) {
				inTerm.getIsaAncestors().forEach(o -> {
					if(ancestorAdds.contains(o.getCurie())) {
						term.getIsaAncestors().add(o);
					}
				});
			}
		} else {
			HashSet<OntologyTerm> set = new HashSet<>();
			if(inTerm.getIsaAncestors() != null) {
				inTerm.getIsaAncestors().forEach(o -> {
					if(ancestorAdds.contains(o.getCurie())) {
						set.add(o);
					}
				});
				term.setIsaAncestors(set);
			}
		}
		
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
		SearchResponse<E> t = dao.findByField("isaParents", null);
		return new ObjectListResponse<E>(t.getResults());
	}

	public ObjectListResponse<E> getChildren(String curie) {
		E term = dao.find(curie);
		if (term != null) {
			return (ObjectListResponse<E>) new ObjectListResponse<OntologyTerm>(term.getIsaChildren());
		} else {
			return new ObjectListResponse<E>();
		}
	}

	public ObjectListResponse<E> getDescendants(String curie) {
		E term = dao.find(curie);
		if (term != null) {
			return (ObjectListResponse<E>) new ObjectListResponse<OntologyTerm>(term.getIsaDescendants());
		} else {
			return new ObjectListResponse<E>();
		}
	}

	public ObjectListResponse<E> getParents(String curie) {
		E term = dao.find(curie);
		if (term != null) {
			return (ObjectListResponse<E>) new ObjectListResponse<OntologyTerm>(term.getIsaParents());
		} else {
			return new ObjectListResponse<E>();
		}
	}

	public ObjectListResponse<E> getAncestors(String curie) {
		E term = dao.find(curie);
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
			List<CrossReference> mergedCrossReferences = crossReferenceService.getMergedXrefList(incomingTerm.getCrossReferences(), dbTerm.getCrossReferences());
			mergedIds = mergedCrossReferences.stream().map(CrossReference::getId).collect(Collectors.toList());
			dbTerm.setCrossReferences(mergedCrossReferences);
		}
		
		for (Long currentId : currentIds) {
			if (!mergedIds.contains(currentId)) {
				crossReferenceDAO.remove(currentId);
			}
		}
	}

}
