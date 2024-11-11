package org.alliancegenome.curation_api.model.entities;

import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.ontology.MMOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.OBITerm;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
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
@Schema(name = "HTPExpressionDatasetSampleAnnotation", description = "POJO that represents the HighThroughputExpressionDatasetSampleAnnotation")
@AGRCurationSchemaVersion(min = "2.7.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })
@Table(indexes = {
	@Index(name = "htpdatasample_htpExpressionSample_index", columnList = "htpExpressionSample_id"),
	@Index(name = "htpdatasample_htpExpressionSampleType_index", columnList = "htpExpressionSampleType_id"),
	@Index(name = "htpdatasample_expressionAssayUsed_index", columnList = "expressionAssayUsed_id"),
	@Index(name = "htpdatasample_htpExpressionSampleAge_index", columnList = "htpExpressionSampleAge_id"),
	@Index(name = "htpdatasample_genomicInformation_index", columnList = "genomicInformation_id"),
	@Index(name = "htpdatasample_microarraySampleDetails_index", columnList = "microarraySampleDetails_id"),
	@Index(name = "htpdatasample_geneticSex_index", columnList = "geneticSex_id"),
	@Index(name = "htpdatasample_sequencingFormat_index", columnList = "sequencingFormat_id"),
	@Index(name = "htpdatasample_taxon_index", columnList = "taxon_id"),
	@Index(name = "htpdatasample_dataprovider_index", columnList = "dataprovider_id"),
	@Index(name = "htpdatasample_createdby_index", columnList = "createdby_id"),
	@Index(name = "htpdatasample_updatedby_index", columnList = "updatedby_id")
})
public class HTPExpressionDatasetSampleAnnotation extends AuditedObject {

	@IndexedEmbedded(includePaths = {"curie", "preferredCrossReference.referencedCurie", "secondaryIdentifiers",
	"curie_keyword", "preferredCrossReference.referencedCurie_keyword", "secondaryIdentifiers_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ View.FieldsOnly.class })
	private ExternalDataBaseEntity htpExpressionSample;
	
	@IndexedEmbedded(includePaths = {"curie", "name", "secondaryIdentifiers", "synonyms.name", "abbreviation",
	"curie_keyword", "name_keyword", "secondaryIdentifiers_keyword", "synonyms.name_keyword", "abbreviation_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView(View.FieldsOnly.class)
	private OBITerm htpExpressionSampleType;

	@IndexedEmbedded(includePaths = {"curie", "name", "secondaryIdentifiers", "synonyms.name", "abbreviation",
	"curie_keyword", "name_keyword", "secondaryIdentifiers_keyword", "synonyms.name_keyword", "abbreviation_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView(View.FieldsAndLists.class)
	private MMOTerm expressionAssayUsed;

	@IndexedEmbedded(includePaths = {"age", "whenExpressedStageName", "age_keyword", "whenExpressedStageName_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ View.FieldsOnly.class })
	private BioSampleAge htpExpressionSampleAge;

	@IndexedEmbedded(includePaths = {"bioSampleAllele.curie", "bioSampleAllele.modInternalId", "bioSampleAllele.modEntityId", "bioSampleAllele.modEntityId_keyword",
	"bioSampleAllele.modInternalId_keyword", "bioSampleAgm.curie", "bioSampleAgm.modInternalId", "bioSampleAgm.modEntityId", "bioSampleAgm.modInternalId_keyword",
	"bioSampleAgm.modEntityId_keyword", "bioSampleAllele.curie_keyword", "bioSampleAgm.curie_keyword", "bioSampleText", "bioSampleText_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ View.FieldsOnly.class })
	private BioSampleGenomicInformation genomicInformation;

	@IndexedEmbedded(includePaths = {"channelId", "channelId_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ View.FieldsOnly.class })
	private MicroarraySampleDetails microarraySampleDetails;

	@IndexedEmbedded(includePaths = {
		"sourceOrganization.abbreviation", "sourceOrganization.fullName", "sourceOrganization.shortName", "crossReference.displayName", "crossReference.referencedCurie",
		"sourceOrganization.abbreviation_keyword", "sourceOrganization.fullName_keyword", "sourceOrganization.shortName_keyword", "crossReference.displayName_keyword", "crossReference.referencedCurie_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	DataProvider dataProvider;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private VocabularyTerm geneticSex;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private VocabularyTerm sequencingFormat;

	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JoinTable(indexes = {
		@Index(name = "htpdatasample_anatomicalsite_htpdatasample_index", columnList = "htpexpressiondatasetsampleannotation_id"),
		@Index(name = "htpdatasample_anatomicalsite_samplelocations_index", columnList = "htpexpressionsamplelocations_id")
	})
	@JsonView({ View.FieldsAndLists.class })
	@Fetch(FetchMode.JOIN)
	private List<AnatomicalSite> htpExpressionSampleLocations;

	@IndexedEmbedded(includePaths = {"freeText", "freeText_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ View.FieldsAndLists.class, View.AlleleView.class })
	@JoinTable(indexes = {
		@Index(name = "htpdatasample_note_htpdatasample_index", columnList = "htpexpressiondatasetsampleannotation_id"),
		@Index(name = "htpdatasample_note_relatednotes_index", columnList = "relatednotes_id")})
	private List<Note> relatedNotes;

	@IndexedEmbedded(includePaths = {"name", "curie", "name_keyword", "curie_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private NCBITaxonTerm taxon;

	@JsonView({ View.FieldsOnly.class })
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "htpexpressionsampletitle_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	private String htpExpressionSampleTitle;

	@JsonView({ View.FieldsOnly.class })
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "abundance_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	private String abundance;

	@JsonView({ View.FieldsOnly.class })
	@ElementCollection
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "assemblyversions_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JoinTable(indexes = @Index(name = "htpdatasample_assemblyversions_htpdatasample_index", columnList = "htpexpressiondatasetsampleannotation_id"))
	private List<String> assemblyVersions;

	@IndexedEmbedded(includePaths = {"curie", "preferredCrossReference.referencedCurie", "secondaryIdentifiers",
	"curie_keyword", "preferredCrossReference.referencedCurie_keyword", "secondaryIdentifiers_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@Fetch(FetchMode.JOIN)
	@JoinTable(indexes = {
		@Index(name = "htpdatasample_externaldatabaseentity_htpdatasample_index", columnList = "htpexpressiondatasetsampleannotation_id"),
		@Index(name = "htpdatasample_externaldatabaseentity_datasetids_index", columnList = "datasetids_id")
	})
	@JsonView({ View.FieldsAndLists.class })
	private List<ExternalDataBaseEntity> datasetIds;

}
