package org.alliancegenome.curation_api.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.bridges.BooleanValueBridge;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.view.View;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
	@Type(value = AGMDiseaseAnnotation.class, name = "AGMDiseaseAnnotation"),
	@Type(value = AlleleDiseaseAnnotation.class, name = "AlleleDiseaseAnnotation"),
	@Type(value = GeneDiseaseAnnotation.class, name = "GeneDiseaseAnnotation")
})
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@AGRCurationSchemaVersion(min = "2.8.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = {Annotation.class})
@Schema(name = "Disease_Annotation", description = "Annotation class representing a disease annotation")
@Table(indexes = {
	@Index(name = "DiseaseAnnotation_internal_index", columnList = "internal"),
	@Index(name = "DiseaseAnnotation_obsolete_index", columnList = "obsolete"),
	@Index(name = "DiseaseAnnotation_curie_index", columnList = "curie"),
	@Index(name = "DiseaseAnnotation_modEntityId_index", columnList = "modEntityId"),
	@Index(name = "DiseaseAnnotation_modInternalId_index", columnList = "modInternalId"),
	@Index(name = "DiseaseAnnotation_uniqueId_index", columnList = "uniqueId"),
	@Index(name = "DiseaseAnnotation_diseaseAnnotationObject_index", columnList = "diseaseAnnotationObject_id"),
	@Index(name = "DiseaseAnnotation_negated_index", columnList = "negated"),
	@Index(name = "DiseaseAnnotation_createdBy_index", columnList = "createdBy_id"),
	@Index(name = "DiseaseAnnotation_updatedBy_index", columnList = "updatedBy_id"),
	@Index(name = "DiseaseAnnotation_singleReference_index", columnList = "singleReference_id"),
	@Index(name = "DiseaseAnnotation_dataProvider_index", columnList = "dataProvider_id"),
	@Index(name = "DiseaseAnnotation_annotationType_index", columnList = "annotationType_id"),
	@Index(name = "DiseaseAnnotation_diseaseGeneticModifierRelation_index", columnList = "diseaseGeneticModifierRelation_id"),
	@Index(name = "DiseaseAnnotation_geneticSex_index", columnList = "geneticSex_id"),
	@Index(name = "DiseaseAnnotation_relation_index", columnList = "relation_id"),
	@Index(name = "DiseaseAnnotation_secondaryDataProvider_index", columnList = "secondaryDataProvider_id")
})
public abstract class DiseaseAnnotation extends Annotation {

	@IndexedEmbedded(includePaths = {"curie", "name", "secondaryIdentifiers", "synonyms.name", "namespace",
		"curie_keyword", "name_keyword", "secondaryIdentifiers_keyword", "synonyms.name_keyword", "namespace_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class, View.ForPublic.class})
	private DOTerm diseaseAnnotationObject;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
	@KeywordField(name = "negated_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
	@JsonView({View.FieldsOnly.class, View.ForPublic.class})
	@Column(columnDefinition = "boolean default false", nullable = false)
	private Boolean negated = false;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class, View.ForPublic.class})
	private VocabularyTerm relation;

	@IndexedEmbedded(includePaths = {"curie", "name", "secondaryIdentifiers", "synonyms.name", "abbreviation",
		"curie_keyword", "name_keyword", "secondaryIdentifiers_keyword", "synonyms.name_keyword", "abbreviation_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({View.FieldsAndLists.class, View.DiseaseAnnotation.class, View.ForPublic.class})
	@JoinTable(
		joinColumns = @JoinColumn(name = "diseaseannotation_id"),
		inverseJoinColumns = @JoinColumn(name = "evidencecodes_id"),
		indexes = {
			@Index(name = "diseaseannotation_ontologyterm_da_index", columnList = "diseaseannotation_id"),
			@Index(name = "diseaseannotation_ontologyterm_evidencecodes_index", columnList = "evidencecodes_id")
		}
	)
	private List<ECOTerm> evidenceCodes;

	@IndexedEmbedded(includePaths = {
		"curie", "modEntityId", "modInternalId", "curie_keyword", "modEntityId_keyword", "modInternalId_keyword",
		"geneSymbol.formatText", "geneSymbol.displayText", "geneSymbol.formatText_keyword", "geneSymbol.displayText_keyword",
		"geneFullName.formatText", "geneFullName.displayText", "geneFullName.formatText_keyword", "geneFullName.displayText_keyword",
		"geneSystematicName.formatText", "geneSystematicName.displayText", "geneSystematicName.formatText_keyword", "geneSystematicName.displayText_keyword",
		"geneSynonyms.formatText", "geneSynonyms.displayText", "geneSynonyms.formatText_keyword", "geneSynonyms.displayText_keyword",
		"geneSecondaryIds.secondaryId", "geneSecondaryIds.secondaryId_keyword", "name", "name_keyword", "symbol", "symbol_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({View.FieldsAndLists.class, View.DiseaseAnnotation.class, View.ForPublic.class})
	@JoinTable(
		joinColumns = @JoinColumn(name = "diseaseannotation_id"),
		inverseJoinColumns = @JoinColumn(name = "with_id"),
		indexes = {
			@Index(name = "diseaseannotation_gene_da_index", columnList = "diseaseannotation_id"),
			@Index(name = "diseaseannotation_gene_with_index", columnList = "with_id")
		}
	)
	private List<Gene> with;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class, View.ForPublic.class})
	private VocabularyTerm annotationType;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({View.FieldsAndLists.class, View.DiseaseAnnotation.class, View.ForPublic.class})
	@JoinTable(
		joinColumns = @JoinColumn(name = "diseaseannotation_id"),
		inverseJoinColumns = @JoinColumn(name = "diseasequalifiers_id"),
		indexes = {
			@Index(name = "diseaseannotation_vocabularyterm_da_index", columnList = "diseaseannotation_id"),
			@Index(name = "diseaseannotation_vocabularyterm_dq_index", columnList = "diseasequalifiers_id")
		}
	)
	private List<VocabularyTerm> diseaseQualifiers;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class, View.ForPublic.class})
	private VocabularyTerm geneticSex;

	@IndexedEmbedded(includePaths = {"sourceOrganization.abbreviation", "sourceOrganization.fullName", "sourceOrganization.shortName", "crossReference.displayName", "crossReference.referencedCurie",
		"sourceOrganization.abbreviation_keyword", "sourceOrganization.fullName_keyword", "sourceOrganization.shortName_keyword", "crossReference.displayName_keyword", "crossReference.referencedCurie_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class, View.ForPublic.class})
	private DataProvider secondaryDataProvider;

	@IndexedEmbedded(includePaths = {
		"curie", "modEntityId", "modInternalId", "curie_keyword", "modEntityId_keyword", "modInternalId_keyword",
		"geneSymbol.formatText", "geneSymbol.displayText", "geneSymbol.formatText_keyword", "geneSymbol.displayText_keyword",
		"geneFullName.formatText", "geneFullName.displayText", "geneFullName.formatText_keyword", "geneFullName.displayText_keyword",
		"geneSystematicName.formatText", "geneSystematicName.displayText", "geneSystematicName.formatText_keyword", "geneSystematicName.displayText_keyword",
		"geneSynonyms.formatText", "geneSynonyms.displayText", "geneSynonyms.formatText_keyword", "geneSynonyms.displayText_keyword",
		"geneSecondaryIds.secondaryId", "geneSecondaryIds.secondaryId_keyword", "name", "name_keyword", "symbol", "symbol_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({View.FieldsAndLists.class, View.DiseaseAnnotation.class, View.ForPublic.class})
	@JoinTable(
		name = "diseaseannotation_modifiergene",
		joinColumns = @JoinColumn(name = "diseaseannotation_id"),
		inverseJoinColumns = @JoinColumn(name = "diseasegeneticmodifiergenes_id"),
		indexes = {
			@Index(name = "diseaseannotation_modifiergene_da_index", columnList = "diseaseannotation_id"),
			@Index(name = "diseaseannotation_modifiergene_dgmg_index", columnList = "diseasegeneticmodifiergenes_id")
		}
	)
	private List<Gene> diseaseGeneticModifierGenes;

	@IndexedEmbedded(includePaths = {
		"curie", "modEntityId", "modInternalId", "curie_keyword", "modEntityId_keyword", "modInternalId_keyword",
		"alleleSymbol.formatText", "alleleSymbol.displayText", "alleleSymbol.formatText_keyword", "alleleSymbol.displayText_keyword",
		"alleleFullName.formatText", "alleleFullName.displayText", "alleleFullName.formatText_keyword", "alleleFullName.displayText_keyword",
		"alleleSynonyms.formatText", "alleleSynonyms.displayText", "alleleSynonyms.formatText_keyword", "alleleSynonyms.displayText_keyword",
		"alleleSecondaryIds.secondaryId", "alleleSecondaryIds.secondaryId_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({View.FieldsAndLists.class, View.DiseaseAnnotation.class, View.ForPublic.class})
	@JoinTable(
		name = "diseaseannotation_modifierallele",
		joinColumns = @JoinColumn(name = "diseaseannotation_id"),
		inverseJoinColumns = @JoinColumn(name = "diseasegeneticmodifieralleles_id"),
		indexes = {
			@Index(name = "diseaseannotation_modifierallele_da_index", columnList = "diseaseannotation_id"),
			@Index(name = "diseaseannotation_modifierallele_dgma_index", columnList = "diseasegeneticmodifieralleles_id")
		}
	)
	private List<Allele> diseaseGeneticModifierAlleles;

	@IndexedEmbedded(includePaths = {"name", "name_keyword", "curie", "curie_keyword", "modEntityId", "modEntityId_keyword", "modInternalId", "modInternalId_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({View.FieldsAndLists.class, View.DiseaseAnnotation.class, View.ForPublic.class})
	@JoinTable(
		name = "diseaseannotation_modifieragm",
		joinColumns = @JoinColumn(name = "diseaseannotation_id"),
		inverseJoinColumns = @JoinColumn(name = "diseasegeneticmodifieragms_id"),
		indexes = {
			@Index(name = "diseaseannotation_modifieragm_da_index", columnList = "diseaseannotation_id"),
			@Index(name = "diseaseannotation_modifieragm_dgma_index", columnList = "diseasegeneticmodifieragms_id")
		}
	)
	private List<AffectedGenomicModel> diseaseGeneticModifierAgms;

	public List<BiologicalEntity> getDiseaseGeneticModifiers() {
		List<BiologicalEntity> geneticModifiers = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(getDiseaseGeneticModifierAlleles())) {
			geneticModifiers.addAll(getDiseaseGeneticModifierAlleles().stream().filter(Objects::nonNull).toList());
		}
		if (CollectionUtils.isNotEmpty(getDiseaseGeneticModifierGenes())) {
			geneticModifiers.addAll(getDiseaseGeneticModifierGenes().stream().filter(Objects::nonNull).toList());
		}
		if (CollectionUtils.isNotEmpty(getDiseaseGeneticModifierAgms())) {
			geneticModifiers.addAll(getDiseaseGeneticModifierAgms().stream().filter(Objects::nonNull).toList());
		}
		return geneticModifiers;
	}

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class, View.ForPublic.class})
	private VocabularyTerm diseaseGeneticModifierRelation;

	@Transient
	public abstract String getSubjectCurie();

	@Transient
	public abstract String getSubjectTaxonCurie();

	@Transient
	public abstract String getSubjectSpeciesName();

	@Transient
	public abstract String getSubjectIdentifier();

	@Transient
	@JsonIgnore
	public String getDataProviderString() {
		StringBuilder builder = new StringBuilder(dataProvider.getSourceOrganization().getAbbreviation());
		if (secondaryDataProvider != null) {
			builder.append(" via ");
			builder.append(secondaryDataProvider.getSourceOrganization().getAbbreviation());
		}
		return builder.toString();
	}
}
