package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.bridges.BooleanValueBridge;
import org.alliancegenome.curation_api.model.entities.associations.variantAssociations.CuratedVariantGenomicLocationAssociation;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "2.7.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })
@Schema(name = "PredictedVariantConsequence", description = "POJO representing VEP predicted variant consequence results")
@Table(indexes = {
	@Index(name = "predictedvariantconsequence_varianttranscript_index", columnList = "varianttranscript_id"),
	@Index(name = "predictedvariantconsequence_vepimpact_index", columnList = "vepimpact_id"),
	@Index(name = "predictedvariantconsequence_polyphenprediction_index", columnList = "polyphenprediction_id"),
	@Index(name = "predictedvariantconsequence_siftprediction_index", columnList = "siftprediction_id"),
	@Index(name = "predictedvariantconsequence_createdby_index", columnList = "createdby_id"),
	@Index(name = "predictedvariantconsequence_updatedby_index", columnList = "updatedby_id")
})
public class PredictedVariantConsequence extends AuditedObject {

	@ManyToOne
	@JsonBackReference
	private CuratedVariantGenomicLocationAssociation variantGenomicLocation;
	
	@IndexedEmbedded(includePaths = {"name", "name_keyword", "curie", "curie_keyword", "modEntityId", "modEntityId_keyword", "modInternalId", "modInternalId_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private Transcript variantTranscript;
	
	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private VocabularyTerm vepImpact;
	
	@IndexedEmbedded(includePaths = {"curie", "name", "secondaryIdentifiers", "synonyms.name", "namespace",
			"curie_keyword", "name_keyword", "secondaryIdentifiers_keyword", "synonyms.name_keyword", "namespace_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JoinTable(indexes = {
		@Index(name = "predictedvariantconsequence_ontologyterm_pvc_index", columnList = "predictedvariantconsequence_id"),
		@Index(name = "predictedvariantconsequence_ontologyterm_vc_index", columnList = "vepconsequences_id")
	})
	@JsonView({ View.FieldsAndLists.class, View.VariantView.class })
	private List<SOTerm> vepConsequences;
	
	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private VocabularyTerm polyphenPrediction;
	
	@GenericField(projectable = Projectable.YES, sortable = Sortable.YES)
	@JsonView({ View.FieldsOnly.class })
	private Float polyphenScore;
	
	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private VocabularyTerm siftPrediction;
	
	@GenericField(projectable = Projectable.YES, sortable = Sortable.YES)
	@JsonView({ View.FieldsOnly.class })
	private Float siftScore;
	
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "aminoAcidReference_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class })
	@Column(columnDefinition = "TEXT")
	private String aminoAcidReference;
	
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "aminoAcidVariant_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class })
	@Column(columnDefinition = "TEXT")
	private String aminoAcidVariant;
	
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "codonReference_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class })
	@Column(columnDefinition = "TEXT")
	private String codonReference;
	
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "codonVariant_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class })
	@Column(columnDefinition = "TEXT")
	private String codonVariant;
	
	@GenericField(projectable = Projectable.YES, sortable = Sortable.YES)
	@JsonView({ View.FieldsOnly.class })
	private Integer calculatedCdnaStart;
	
	@GenericField(projectable = Projectable.YES, sortable = Sortable.YES)
	@JsonView({ View.FieldsOnly.class })
	private Integer calculatedCdnaEnd;
	
	@GenericField(projectable = Projectable.YES, sortable = Sortable.YES)
	@JsonView({ View.FieldsOnly.class })
	private Integer calculatedCdsStart;
	
	@GenericField(projectable = Projectable.YES, sortable = Sortable.YES)
	@JsonView({ View.FieldsOnly.class })
	private Integer calculatedCdsEnd;
	
	@GenericField(projectable = Projectable.YES, sortable = Sortable.YES)
	@JsonView({ View.FieldsOnly.class })
	private Integer calculatedProteinStart;
	
	@GenericField(projectable = Projectable.YES, sortable = Sortable.YES)
	@JsonView({ View.FieldsOnly.class })
	private Integer calculatedProteinEnd;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "hgvsProteinNomenclature_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class })
	@Column(columnDefinition = "TEXT")
	private String hgvsProteinNomenclature;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "hgvsCodingNomenclature_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class })
	@Column(columnDefinition = "TEXT")
	private String hgvsCodingNomenclature;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
	@KeywordField(name = "geneLevelConsequence_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
	@JsonView({ View.FieldsOnly.class })
	@Column(columnDefinition = "boolean default false", nullable = false)
	private Boolean geneLevelConsequence = false;
}
