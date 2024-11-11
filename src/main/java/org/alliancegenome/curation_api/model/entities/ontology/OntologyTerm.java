package org.alliancegenome.curation_api.model.entities.ontology;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.entities.base.CurieObject;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "OntologyTermType", length = 64)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity
@ToString(exclude = { "isaParents", "isaChildren", "isaAncestors", "isaDescendants", "crossReferences", "synonyms", "secondaryIdentifiers", "subsets" }, callSuper = true)
@AGRCurationSchemaVersion(min = LinkMLSchemaConstants.MIN_ONTOLOGY_RELEASE, max = LinkMLSchemaConstants.MAX_ONTOLOGY_RELEASE, dependencies = { CurieObject.class })
@Table(
	indexes = {
		@Index(name = "ontologyterm_curie_index", columnList = "curie"),
		@Index(name = "ontologyterm_name_index", columnList = "name"),
		@Index(name = "ontologyterm_createdby_index", columnList = "createdBy_id"),
		@Index(name = "ontologyterm_updatedby_index", columnList = "updatedBy_id")
	},
	uniqueConstraints = {
		@UniqueConstraint(name = "ontologyterm_curie_uk", columnNames = "curie")
	}
)
public class OntologyTerm extends CurieObject {

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "name_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({View.FieldsOnly.class, View.ForPublic.class})
	@Column(length = 2000)
	protected String name;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "type_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView(View.FieldsOnly.class)
	private String type;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "namespace_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView(View.FieldsOnly.class)
	private String namespace;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "definition_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@Column(columnDefinition = "TEXT")
	@JsonView(View.FieldsOnly.class)
	private String definition;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "definitionUrls_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@ElementCollection
	@JsonView(View.FieldsAndLists.class)
	@Column(columnDefinition = "TEXT")
	@JoinTable(indexes = @Index(name = "ontologyterm_definitionurls_ontologyterm_index", columnList = "ontologyterm_id"))
	private List<String> definitionUrls;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "subsets_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@ElementCollection
	@JsonView(View.FieldsAndLists.class)
	@JoinTable(indexes = @Index(name = "ontologyterm_subsets_ontologyterm_index", columnList = "ontologyterm_id"))
	private List<String> subsets;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "secondaryIdentifiers_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@ElementCollection
	@JsonView(View.FieldsAndLists.class)
	@JoinTable(indexes = @Index(name = "ontologyterm_secondaryidentifiers_ontologyterm_index", columnList = "ontologyterm_id"))
	private List<String> secondaryIdentifiers;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JoinTable(indexes = @Index(columnList = "ontologyterm_id", name = "ontologyterm_synonym_ontologyterm_index"))
	@JsonView({ View.FieldsAndLists.class })
	private List<Synonym> synonyms;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
	@JoinTable(indexes = {
		@Index(columnList = "ontologyterm_id", name = "ontologyterm_crossreference_ontologyterm_index"),
		@Index(columnList = "crossreferences_id", name = "ontologyterm_crossreference_crossreferences_index")
	})
	@JsonView({ View.FieldsAndLists.class })
	private List<CrossReference> crossReferences;

	@ManyToMany
	// @JsonView(View.OntologyTermView.class)
	@JoinTable(name = "ontologyterm_isa_parent_children", indexes = {
		@Index(name = "ontologyterm_isa_parent_children_isaparents_index", columnList = "isaparents_id"),
		@Index(name = "ontologyterm_isa_parent_children_isachildren_index", columnList = "isachildren_id")
	})
	private Set<OntologyTerm> isaParents;

	@ManyToMany(mappedBy = "isaParents")
	// @JsonView(View.OntologyTermView.class)
	private Set<OntologyTerm> isaChildren;

	@ManyToMany
	@JoinTable(name = "ontologyterm_isa_ancestor_descendant", indexes = {
		@Index(name = "ontologyterm_isa_ancestor_descendant_isancestors_index", columnList = "isaancestors_id"),
		@Index(name = "ontologyterm_isa_ancestor_descendant_isadescendants_index", columnList = "isadescendants_id")
	})
	private Set<OntologyTerm> isaAncestors;

	@ManyToMany(mappedBy = "isaAncestors")
	private Set<OntologyTerm> isaDescendants;
	
	@JsonView(View.FieldsOnly.class)
	private Integer childCount = 0;
	
	@JsonView(View.FieldsOnly.class)
	private Integer descendantCount = 0;

	@Transient
	public void addIsaChild(OntologyTerm term) {
		if (isaChildren == null) {
			isaChildren = new HashSet<OntologyTerm>();
		}
		isaChildren.add(term);
	}

	@Transient
	public void addIsaParent(OntologyTerm term) {
		if (isaParents == null) {
			isaParents = new HashSet<OntologyTerm>();
		}
		isaParents.add(term);
	}

	@Transient
	public void addIsaDescendant(OntologyTerm term) {
		if (isaDescendants == null) {
			isaDescendants = new HashSet<OntologyTerm>();
		}
		isaDescendants.add(term);
	}

	@Transient
	public void addIsaAncestor(OntologyTerm term) {
		if (isaAncestors == null) {
			isaAncestors = new HashSet<OntologyTerm>();
		}
		isaAncestors.add(term);
	}

}