package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"resourcePages"}, callSuper = true)
@AGRCurationSchemaVersion(min = "1.5.1", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })
@Table(indexes = {
		@Index(name = "resourcedescriptor_createdby_index", columnList = "createdBy_id"),
		@Index(name = "resourcedescriptor_updatedby_index", columnList = "updatedBy_id")
})
@Schema(name = "ResourceDescriptor", description = "Annotation class representing a resource descriptor")
public class ResourceDescriptor extends AuditedObject {

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "prefix_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	@Column(unique = true)
	@EqualsAndHashCode.Include
	protected String prefix;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "name_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	@EqualsAndHashCode.Include
	private String name;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "synoynyms_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
	@ElementCollection
	@JoinTable(indexes = @Index(columnList = "resourcedescriptor_id"))
	@Fetch(FetchMode.JOIN)
	@JsonView({ View.ResourceDescriptorView.class, View.FieldsAndLists.class })
	private List<String> synonyms;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "idPattern_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class })
	@EqualsAndHashCode.Include
	private String idPattern;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "idExample_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class })
	@EqualsAndHashCode.Include
	private String idExample;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "defaultUrlTemplate_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	@EqualsAndHashCode.Include
	private String defaultUrlTemplate;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToMany(mappedBy = "resourceDescriptor", cascade = CascadeType.ALL)
	@JsonView({ View.ResourceDescriptorView.class })
	private List<ResourceDescriptorPage> resourcePages;
}
