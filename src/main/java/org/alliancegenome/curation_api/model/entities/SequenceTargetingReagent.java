package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.associations.sequenceTargetingReagentAssociations.SequenceTargetingReagentGeneAssociation;
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
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = { "sequenceTargetingReagentGeneAssociations" }, callSuper = true)
@Schema(name = "SequenceTargetingReagent", description = "POJO that represents the SequenceTargetingReagent")
@AGRCurationSchemaVersion(min = "2.3.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { GenomicEntity.class }, partial = true)
public class SequenceTargetingReagent extends GenomicEntity {

	@Column(columnDefinition = "TEXT")
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private String name;

	@IndexedEmbedded(includePaths = {"curie", "primaryCrossReferenceCurie", "crossReferences.referencedCurie", "curie_keyword", "primaryCrossReferenceCurie_keyword", "crossReferences.referencedCurie_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@Fetch(FetchMode.JOIN)
	@JoinTable(indexes = {
		@Index(name = "sequencetargetingreagent_reference_sqtr_index", columnList = "sequencetargetingreagent_id"),
		@Index(name = "sequencetargetingreagent_reference_references_index", columnList = "references_id")
	})
	@JsonView({ View.FieldsAndLists.class, View.SequenceTargetingReagentView.class })
	private List<Reference> references;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "synonyms_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@ElementCollection
	@JoinTable(indexes = @Index(name = "sqtr_synonyms_sqtr_index", columnList = "sequencetargetingreagent_id"))
	@JsonView({ View.FieldsAndLists.class, View.SequenceTargetingReagentView.class })
	private List<String> synonyms;
	
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "secondaryIdentifiers_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@ElementCollection
	@JoinTable(indexes = @Index(name = "sqtr_secondaryIdentifiers_sqtr_index", columnList = "sequencetargetingreagent_id"))
	@JsonView({ View.FieldsAndLists.class, View.SequenceTargetingReagentView.class })
	private List<String> secondaryIdentifiers;

	@OneToMany(mappedBy = "sequenceTargetingReagentAssociationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ View.FieldsAndLists.class, View.SequenceTargetingReagentDetailView.class })
	private List<SequenceTargetingReagentGeneAssociation> sequenceTargetingReagentGeneAssociations;
}
