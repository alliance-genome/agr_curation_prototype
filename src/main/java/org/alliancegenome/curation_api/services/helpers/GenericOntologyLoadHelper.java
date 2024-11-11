package org.alliancegenome.curation_api.services.helpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections.CollectionUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;

import io.quarkus.logging.Log;

public class GenericOntologyLoadHelper<T extends OntologyTerm> implements OWLObjectVisitor {

	private ElkReasonerFactory reasonerFactory = new ElkReasonerFactory();
	private OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	private OWLReasoner reasoner;
	private OWLOntology ontology;

	private GenericOntologyLoadConfig config;
	private String defaultNamespace;
	private Class<T> clazz;

	private HashMap<String, T> allNodes = new HashMap<>();
	private HashSet<String> traversedNodes = new HashSet<String>();

	private ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);

	public GenericOntologyLoadHelper(Class<T> clazz) {
		this.clazz = clazz;
		this.config = new GenericOntologyLoadConfig();
	}

	public GenericOntologyLoadHelper(Class<T> clazz, GenericOntologyLoadConfig config) {
		this.clazz = clazz;
		this.config = config;
	}

	public Map<String, T> load(String fullText) throws Exception {
		File outfile = new File("tmp.file2.owl"); // TODO fix so multiple loads do not overwrite each other Generate random name
		Log.info("Input data size: " + fullText.length());
		BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
		writer.append(fullText);
		writer.flush();
		writer.close();
		Map<String, T> ret = load(new FileInputStream(outfile));
		outfile.delete();
		return ret;
	}

	public Map<String, T> load(InputStream inStream) throws Exception {

		Log.info("Loading Ontology File");
		ontology = manager.loadOntologyFromOntologyDocument(inStream);
		Log.info("Loading Ontology File Finished");

		ontology.annotations().forEach(a -> {
			String key = a.getProperty().getIRI().getShortForm();
			Log.info(key + ": " + getString(a.getValue()));
			if (key.equals("default-namespace")) {
				defaultNamespace = getString(a.getValue());
			}
		});

		HashSet<String> requiredNamespaces = config.getAltNameSpaces();
		if (requiredNamespaces.isEmpty()) {
			if (defaultNamespace != null) {
				requiredNamespaces.add(defaultNamespace);
			}
		}

		Log.info("Ontology Loaded...");
		Log.info("Ontology : " + ontology.getOntologyID());
		Log.info("Default Namespace : " + defaultNamespace);
		Log.info("Format		: " + manager.getOntologyFormat(ontology));

		reasoner = reasonerFactory.createReasoner(ontology);

		if (config.getLoadObjectProperties()) {
			Log.info("Traversing Object Properties");
			OWLObjectProperty rootProperty = manager.getOWLDataFactory().getOWLTopObjectProperty();
			traverseProperties(rootProperty, 0);
			Log.info("Finished Traversing Object Properties: " + allNodes.size());
			return allNodes;
		}

		OWLClass root = manager.getOWLDataFactory().getOWLThing();

		Log.info("Traversing Ontology");
		ph.startProcess("Traversing Ontology: " + clazz.getSimpleName());
		traverse(root, 0, requiredNamespaces);
		ph.finishProcess();
		Log.info("Finished Traversing Ontology: " + allNodes.size());

		return allNodes;

	}

	public Boolean hasChebiXref(T term) {

		if (CollectionUtils.isNotEmpty(term.getSynonyms())) {
			for (Synonym synonym : term.getSynonyms()) {
				if (synonym.getName().startsWith("CHEBI:")) {
					return true;
				}
			}
		}

		if (CollectionUtils.isNotEmpty(term.getCrossReferences())) {
			for (CrossReference xref : term.getCrossReferences()) {
				if (xref.getReferencedCurie().startsWith("CHEBI:")) {
					return true;
				}
			}
		}

		return false;
	}

	public T traverse(OWLClass currentTreeNode, int depth, HashSet<String> requiredNamespaces) throws Exception {

		T currentTerm = null;

		if (reasoner.isSatisfiable(currentTreeNode)) {
			ph.progressProcess();
			currentTerm = getOntologyTerm(currentTreeNode);

			boolean isNodeInOntology = isNodeInOntology(currentTreeNode, currentTerm, requiredNamespaces);

			if (isNodeInOntology && currentTerm.getCurie() != null) {
				if (!allNodes.containsKey(currentTerm.getCurie())) {
					allNodes.put(currentTerm.getCurie(), currentTerm);
				} else {
					currentTerm = allNodes.get(currentTerm.getCurie());
				}
			}

			if (traversedNodes.contains(currentTerm.getCurie())) {
				return currentTerm;
			} else {
				traversedNodes.add(currentTerm.getCurie());
			}

			if (isNodeInOntology) {
				HashSet<OntologyTerm> ancestors = new HashSet<OntologyTerm>();
				traverseToRoot(currentTreeNode, depth, requiredNamespaces, ancestors);
				ancestors.remove(currentTerm);
				// if(ancestors.size() > 0) ancestors.remove(0);
				currentTerm.setIsaAncestors(new HashSet<>(ancestors));
				// printDepthMessage(depth, currentTerm.getCurie() + " [" +
				// ancestors.stream().map(OntologyTerm::getCurie).collect(Collectors.joining(","))
				// + "]");
			}

			for (OWLClass childTermNode : reasoner.getSubClasses(currentTreeNode, true).entities().collect(Collectors.toList())) {

				if (!childTermNode.equals(currentTreeNode)) {
					try {
						T childTerm = traverse(childTermNode, depth + 1, requiredNamespaces);

						if (childTerm != null && currentTerm.getCurie() != null && isNodeInOntology) {
							// printDepthMessage(depth, "Adding isa parent: " + currentTerm.getCurie());
							childTerm.addIsaParent(currentTerm);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		return currentTerm;

	}

	private void traverseToRoot(OWLClass currentTreeNode, int depth, HashSet<String> requiredNamespaces, HashSet<OntologyTerm> ancestors) throws Exception {
		List<OWLClass> parents = reasoner.getSuperClasses(currentTreeNode, true).entities().collect(Collectors.toList());

		T currentTerm = null;

		if (reasoner.isSatisfiable(currentTreeNode)) {

			currentTerm = getOntologyTerm(currentTreeNode);

			if (isNodeInOntology(currentTreeNode, currentTerm, requiredNamespaces) && currentTerm.getCurie() != null) {

				T existingNode = allNodes.get(currentTerm.getCurie());

				if (existingNode == null) {
					allNodes.put(currentTerm.getCurie(), currentTerm);
					existingNode = currentTerm;
				}

				if (!ancestors.contains(existingNode)) {
					ancestors.add(existingNode);
				}
			}

			for (OWLClass parent : parents) {
				traverseToRoot(parent, depth + 1, requiredNamespaces, ancestors);
			}
		}
	}

	private boolean isNodeInOntology(OWLClass currentTreeNode, T currentTerm, HashSet<String> requiredNamespaces) {
		boolean condition1 = currentTerm.getNamespace() != null;
		boolean condition2 = requiredNamespaces.contains(currentTerm.getNamespace());
		boolean condition3 = config.getLoadOnlyIRIPrefix() != null;
		boolean condition4 = currentTreeNode.getIRI().getShortForm().startsWith(config.getLoadOnlyIRIPrefix() + "_");

		boolean condition5 = !config.getIgnoreEntitiesWithChebiXref();
		boolean condition6 = !hasChebiXref(currentTerm);

		//CHECKSTYLE:OFF: UnnecessaryParentheses
		return ((condition1 && condition2 && !condition3) || (condition3 && condition4)) && (condition5 || condition6);
		//CHECKSTYLE:ON: UnnecessaryParentheses
	}

	public void printDepthMessage(int depth, String message) {
		String tabs = "";
		for (int i = 0; i < depth; i++) {
			tabs += "\t";
		}

		Log.info(tabs + message);
	}

	public String getIRIShortForm(OWLAnnotationValue owlAnnotationValue) {
		if (owlAnnotationValue.isIRI()) {
			return ((IRI) owlAnnotationValue).getShortForm();
		}
		return "";
	}

	public String getString(OWLAnnotationValue owlAnnotationValue) {
		if (owlAnnotationValue.isLiteral()) {
			return ((OWLLiteral) owlAnnotationValue).getLiteral();
		}
		if (owlAnnotationValue.isIRI()) {
			return ((IRI) owlAnnotationValue).getIRIString();
		}
		return "";
	}

	public Boolean getBoolean(OWLAnnotationValue owlAnnotationValue) {
		return ((OWLLiteral) owlAnnotationValue).getLiteral().equals("true");
	}

	public T getOntologyTerm(OWLClass node) throws Exception {

		T term = clazz.getDeclaredConstructor().newInstance();
		term.setObsolete(false);

		EntitySearcher.getAnnotationObjects(node, ontology).forEach(annotation -> {
			String key = annotation.getProperty().getIRI().getShortForm();
			parseAnnotation(annotation, node, term, key);
		});

		if (term.getCurie() == null && EntitySearcher.getAnnotationObjects(node, ontology).count() > 0) {
			term.setCurie(node.getIRI().getFragment().replaceFirst("_", ":"));
		}

		return term;

	}

	private T parseAnnotation(OWLAnnotation annotation, OWLClass node, T term, String key) {
		if (key.equals("id")) {
			term.setCurie(getString(annotation.getValue()));
		} else if (annotation.getProperty().isLabel() && key.equals("label")) {
			term.setName(getString(annotation.getValue()));
		} else if (key.equals("IAO_0000115")) {

			if (node != null) {
				ontology.annotationAssertionAxioms(node.getIRI()).forEach(annot -> {
					if (annot.isAnnotated()) {
						annot.annotations().forEach(an -> {
							String inkey = an.getProperty().getIRI().getShortForm();
							// System.out.println(inkey);
							if (inkey.equals("hasDbXref")) {
								// System.out.println("Adding: " + an.getValue().toString());
								if (term.getDefinitionUrls() == null) {
									term.setDefinitionUrls(new ArrayList<>());
								}
								term.getDefinitionUrls().add(getString(an.getValue()));
							}
						});
					}
				});
			}
			term.setDefinition(getString(annotation.getValue()));
		} else if (key.equals("deprecated")) {
			term.setObsolete(getBoolean(annotation.getValue()));
		} else if (key.equals("hasOBONamespace")) {
			term.setNamespace(getString(annotation.getValue()));
		} else if (key.equals("hasExactSynonym") || key.equals("hasRelatedSynonym")) {
			if (term.getSynonyms() == null) {
				term.setSynonyms(new ArrayList<>());
			}
			Synonym synonym = new Synonym();
			synonym.setName(getString(annotation.getValue()));
			term.getSynonyms().add(synonym);
		} else if (key.equals("hasAlternativeId")) {
			if (term.getSecondaryIdentifiers() == null) {
				term.setSecondaryIdentifiers(new ArrayList<>());
			}
			term.getSecondaryIdentifiers().add(getString(annotation.getValue()));
		} else if (key.equals("hasDbXref") || key.equals("database_cross_reference")) {
			if (term.getCrossReferences() == null) {
				term.setCrossReferences(new ArrayList<>());
			}
			CrossReference ref = new CrossReference();
			ref.setReferencedCurie(getString(annotation.getValue()));
			ref.setDisplayName(getString(annotation.getValue()));
			term.getCrossReferences().add(ref);
		} else if (key.equals("inSubset")) {
			if (term.getSubsets() == null) {
				term.setSubsets(new ArrayList<>());
			}
			term.getSubsets().add(getIRIShortForm(annotation.getValue()));
		} else {
			// log.info(key + " -> " + getString(annotation.getValue()));
		}

		return term;
	}

	public T traverseProperties(OWLObjectProperty currentTreeProperty, int depth) throws Exception {

		T currentTerm = null;

		ph.progressProcess();
		currentTerm = getOntologyTermFromProperty(currentTreeProperty);

		boolean isPropertyInOntology = isPropertyInOntology(currentTreeProperty);

		if (isPropertyInOntology && currentTerm.getCurie() != null) {
			if (!allNodes.containsKey(currentTerm.getCurie())) {
				allNodes.put(currentTerm.getCurie(), currentTerm);
			} else {
				currentTerm = allNodes.get(currentTerm.getCurie());
			}
		}

		if (traversedNodes.contains(currentTerm.getCurie())) {
			return currentTerm;
		}

		traversedNodes.add(currentTerm.getCurie());

		if (isPropertyInOntology) {
			HashSet<OntologyTerm> ancestors = new HashSet<OntologyTerm>();
			traverseToRootProperty(currentTreeProperty, depth, ancestors);
			ancestors.remove(currentTerm);
			currentTerm.setIsaAncestors(new HashSet<>(ancestors));
		}

		for (OWLObjectPropertyExpression childTermPropertyExpression : reasoner.getSubObjectProperties(currentTreeProperty, true).entities().collect(Collectors.toList())) {
			if (!childTermPropertyExpression.getNamedProperty().toString().equals(currentTreeProperty.toString())) {
				try {
					T childTerm = traverseProperties(childTermPropertyExpression.getNamedProperty(), depth + 1);

					if (childTerm != null && currentTerm.getCurie() != null && isPropertyInOntology) {
						childTerm.addIsaParent(currentTerm);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return currentTerm;

	}

	public T getOntologyTermFromProperty(OWLObjectProperty property) throws Exception {

		T term = clazz.getDeclaredConstructor().newInstance();
		term.setObsolete(false);
		term.setCurie(property.getIRI().getFragment().replaceFirst("_", ":"));

		EntitySearcher.getAnnotationObjects(property, ontology).forEach(annotation -> {
			String key = annotation.getProperty().getIRI().getShortForm();
			parseAnnotation(annotation, null, term, key);
		});

		return term;

	}

	private void traverseToRootProperty(OWLObjectProperty currentTreeProperty, int depth, HashSet<OntologyTerm> ancestors) throws Exception {
		List<OWLObjectPropertyExpression> parents = reasoner.getSuperObjectProperties(currentTreeProperty, true).entities().collect(Collectors.toList());

		T currentTerm = null;

		currentTerm = getOntologyTermFromProperty(currentTreeProperty);

		if (currentTerm.getCurie() != null && isPropertyInOntology(currentTreeProperty)) {

			T existingNode = allNodes.get(currentTerm.getCurie());

			if (existingNode == null) {
				allNodes.put(currentTerm.getCurie(), currentTerm);
				existingNode = currentTerm;
			}

			if (!ancestors.contains(existingNode)) {
				ancestors.add(existingNode);
			}
		}

		for (OWLObjectPropertyExpression parent : parents) {
			traverseToRootProperty(parent.getNamedProperty(), depth + 1, ancestors);
		}
	}

	private boolean isPropertyInOntology(OWLObjectProperty property) {
		if (config.getLoadOnlyIRIPrefix() == null) {
			return true;
		}

		return property.getIRI().getFragment().startsWith(config.getLoadOnlyIRIPrefix() + "_");
	}

}
