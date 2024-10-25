package org.alliancegenome.curation_api.model.entities.associations.variantAssociations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.AssemblyComponent;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
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

import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@MappedSuperclass
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "2.4.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { VariantLocationAssociation.class })
@Schema(name = "VariantGenomicLocationAssociation", description = "POJO representing an association between a variant and a genomic location")
public abstract class VariantGenomicLocationAssociation extends VariantLocationAssociation {

	@IndexedEmbedded(includePaths = {
		"curie", "curie_keyword", "modEntityId", "modEntityId_keyword",
		"modInternalId", "modInternalId_keyword", "name", "name_keyword"
	})
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@Fetch(FetchMode.JOIN)
	private AssemblyComponent variantGenomicLocationAssociationObject;
	
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "variationStrand_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class })
	@Column(length = 1)
	private String variationStrand;
	
	@GenericField(projectable = Projectable.YES, sortable = Sortable.YES)
	@JsonView({ View.FieldsOnly.class })
	private Integer numberAdditionalDnaBasePairs;
	
	@GenericField(projectable = Projectable.YES, sortable = Sortable.YES)
	@JsonView({ View.FieldsOnly.class })
	private Integer numberRemovedDnaBasePairs;
	
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "paddedBase_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class })
	@Column(length = 1)
	private String paddedBase;
	
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "insertedSequence_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class })
	@Column(length = 4000)
	private String insertedSequence;
	
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "deletedSequence_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class })
	@Column(length = 4000)
	private String deletedSequence;
	
	@IndexedEmbedded(includePaths = {"curie", "name", "secondaryIdentifiers", "synonyms.name", "namespace",
			"curie_keyword", "name_keyword", "secondaryIdentifiers_keyword", "synonyms.name_keyword", "namespace_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private SOTerm dnaMutationType;
	
	@IndexedEmbedded(includePaths = {"curie", "name", "secondaryIdentifiers", "synonyms.name", "namespace",
			"curie_keyword", "name_keyword", "secondaryIdentifiers_keyword", "synonyms.name_keyword", "namespace_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private SOTerm geneLocalizationType;
}
