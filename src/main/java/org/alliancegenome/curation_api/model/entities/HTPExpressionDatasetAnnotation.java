package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Schema(name = "HTPExpressionDatasetAnnotation", description = "POJO that represents the HighThroughputExpressionDatasetAnnotation")
@AGRCurationSchemaVersion(min = "2.6.2", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })
@Table(indexes = {
	@Index(name = "htpdatasetannotation_htpExpressionDataset_index", columnList = "htpExpressionDataset_id"),
	@Index(name = "htpdatasetannotation_relatednote_index", columnList = "relatednote_id"),
	@Index(name = "htpdatasetannotation_dataprovider_index", columnList = "dataprovider_id"),
	@Index(name = "htpdatasetannotation_createdby_index", columnList = "createdby_id"),
	@Index(name = "htpdatasetannotation_updatedby_index", columnList = "updatedby_id")
})
public class HTPExpressionDatasetAnnotation extends AuditedObject {

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ View.FieldsOnly.class })
	private ExternalDataBaseEntity htpExpressionDataset;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@Fetch(FetchMode.JOIN)
	@JoinTable(indexes = {
		@Index(name = "htpdatasetannotation_externaldatabaseentity_htpdataset_index", columnList = "htpexpressiondatasetannotation_id"),
		@Index(name = "htpdatasetannotation_externaldatabaseentity_subseries_index", columnList = "subseries_id")
	})
	@JsonView({ View.FieldsAndLists.class })
	private List<ExternalDataBaseEntity> subSeries;

	@JsonView({ View.FieldsOnly.class })
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "name_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	private String name;

	@IndexedEmbedded(includePaths = {"curie", "primaryCrossReferenceCurie", "crossReferences.referencedCurie", "curie_keyword", "primaryCrossReferenceCurie_keyword", "crossReferences.referencedCurie_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@Fetch(FetchMode.JOIN)
	@JoinTable(indexes = {
		@Index(name = "htpdatasetannotation_reference_htpdataset_index", columnList = "htpexpressiondatasetannotation_id"),
		@Index(name = "htpdatasetannotation_reference_references_index", columnList = "references_id")
	})
	@JsonView({ View.FieldsAndLists.class })
	private List<Reference> references;

	@IndexedEmbedded(includePaths = { "freeText", "noteType.name", "references.curie", "references.primaryCrossReferenceCurie", "freeText_keyword", "noteType.name_keyword", "references.curie_keyword", "references.primaryCrossReferenceCurie_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ View.FieldsOnly.class })
	private Note relatedNote;

	@JsonView({ View.FieldsOnly.class })
	@GenericField(projectable = Projectable.YES, sortable = Sortable.YES)
	private Integer numberOfChannels;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonView({ View.FieldsAndLists.class })
	@JoinTable(name = "htpexpressiondatasetannotation_categorytags", indexes = { @Index(name = "htpdatasetannotation_htpdatasetid_index", columnList = "htpexpressiondatasetannotation_id"), @Index(name = "htpdatasetannotation_categorytags_index", columnList = "categorytags_id")})
	List<VocabularyTerm> categoryTags;

	@IndexedEmbedded(includePaths = {
		"sourceOrganization.abbreviation", "sourceOrganization.fullName", "sourceOrganization.shortName", "crossReference.displayName", "crossReference.referencedCurie",
		"sourceOrganization.abbreviation_keyword", "sourceOrganization.fullName_keyword", "sourceOrganization.shortName_keyword", "crossReference.displayName_keyword", "crossReference.referencedCurie_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	DataProvider dataProvider;

}