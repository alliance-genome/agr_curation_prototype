package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import jakarta.persistence.*;
import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleVariantAssociation;
import org.alliancegenome.curation_api.model.entities.associations.variantAssociations.CuratedVariantGenomicLocationAssociation;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.view.View;
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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = { "curatedVariantGenomicLocations", "alleleVariantAssociations" }, callSuper = true)
@AGRCurationSchemaVersion(min = "2.9.1", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { GenomicEntity.class })
@Table(indexes = {
		@Index(name = "variant_varianttype_index", columnList = "varianttype_id"),
		@Index(name = "variant_variantstatus_index", columnList = "variantstatus_id"),
		@Index(name = "variant_sourcegeneralconsequence_index", columnList = "sourcegeneralconsequence_id")
	})
public class Variant extends GenomicEntity {

	@IndexedEmbedded(includePaths = {"curie", "name", "curie_keyword", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private SOTerm variantType;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	@Fetch(FetchMode.JOIN)
	private VocabularyTerm variantStatus;

	@IndexedEmbedded(includePaths = {"curie", "name", "curie_keyword", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private SOTerm sourceGeneralConsequence;

	@IndexedEmbedded(includePaths = {"freeText", "noteType.name", "references.curie",
			"references.primaryCrossReferenceCurie", "freeText_keyword", "noteType.name_keyword", "references.curie_keyword",
			"references.primaryCrossReferenceCurie_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ View.FieldsAndLists.class, View.VariantView.class })
	@JoinTable(indexes = {
			@Index(name = "variant_note_variant_index", columnList = "variant_id"),
			@Index(name = "variant_note_relatednotes_index", columnList = "relatedNotes_id")
		})
	private List<Note> relatedNotes;

	@IndexedEmbedded(
		includePaths = {
			"variantGenomicLocationAssociationObject.curie", "variantGenomicLocationAssociationObject.curie_keyword",
			"variantGenomicLocationAssociationObject.primaryExternalId", "variantGenomicLocationAssociationObject.primaryExternalId_keyword",
			"variantGenomicLocationAssociationObject.modInternalId", "variantGenomicLocationAssociationObject.modInternalId_keyword",
			"start", "end"
		}
	)
	@OneToMany(mappedBy = "variantAssociationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ View.FieldsAndLists.class, View.VariantView.class })
	private List<CuratedVariantGenomicLocationAssociation> curatedVariantGenomicLocations;

	@OneToMany(mappedBy = "alleleVariantAssociationObject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ View.FieldsAndLists.class, View.VariantDetailView.class })
	private List<AlleleVariantAssociation> alleleVariantAssociations;

	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "synonyms_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@ElementCollection
	@JoinTable(indexes = @Index(name = "variant_synonyms_variant_index", columnList = "variant_id"))
	@JsonView({ View.FieldsAndLists.class, View.VariantView.class })
	private List<String> synonyms;

	@IndexedEmbedded(
		includePaths = {
			"primaryCrossReferenceCurie", "crossReferences.referencedCurie", "crossReferences.displayName", "curie", "primaryCrossReferenceCurie_keyword",
			"crossReferences.referencedCurie_keyword", "crossReferences.displayName_keyword", "curie_keyword"
		}
	)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@Fetch(FetchMode.JOIN)
	@JoinTable(indexes = {
		@Index(name = "variant_reference_variant_index", columnList = "variant_id"),
		@Index(name = "variant_reference_references_index", columnList = "references_id")
	})
	@JsonView({ View.FieldsAndLists.class, View.VariantView.class })
	private List<Reference> references;
}
