package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
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

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Indexed
@Entity
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Schema(name = "Gene_Genetic_Interaction", description = "Class representing an interaction between genes")
@AGRCurationSchemaVersion(min = "2.2.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { GeneInteraction.class })

@Table(indexes = {
	@Index(name = "GeneGeneticInteraction_internal_index", columnList = "internal"),
	@Index(name = "GeneGeneticInteraction_obsolete_index", columnList = "obsolete"),
	@Index(name = "GeneGeneticInteraction_interactionId_index", columnList = "interactionId"),
	@Index(name = "GeneGeneticInteraction_uniqueId_index", columnList = "uniqueId"),
	@Index(name = "GeneGeneticInteraction_createdBy_index", columnList = "createdBy_id"),
	@Index(name = "GeneGeneticInteraction_updatedBy_index", columnList = "updatedBy_id"),
	@Index(name = "GeneGeneticInteraction_geneAssociationSubject_index", columnList = "geneAssociationSubject_id"),
	@Index(name = "GeneGeneticInteraction_geneGeneAssociationObject_index", columnList = "geneGeneAssociationObject_id"),
	@Index(name = "GeneGeneticInteraction_relation_index", columnList = "relation_id"),
	@Index(name = "GeneGeneticInteraction_interactionSource_index", columnList = "interactionSource_id"),
	@Index(name = "GeneGeneticInteraction_interactionType_index", columnList = "interactionType_id"),
	@Index(name = "GeneGeneticInteraction_interactorARole_index", columnList = "interactorARole_id"),
	@Index(name = "GeneGeneticInteraction_interactorAType_index", columnList = "interactorAType_id"),
	@Index(name = "GeneGeneticInteraction_interactorBRole_index", columnList = "interactorBRole_id"),
	@Index(name = "GeneGeneticInteraction_interactorBType_index", columnList = "interactorBType_id"),
	@Index(name = "GeneGeneticInteraction_interactorAGeneticPerturbation_index", columnList = "interactorAGeneticPerturbation_id"),
	@Index(name = "GeneGeneticInteraction_interactorBGeneticPerturbation_index", columnList = "interactorBGeneticPerturbation_id")
})

public class GeneGeneticInteraction extends GeneInteraction {

	@IndexedEmbedded(includePaths = {
			"curie", "modEntityId", "modInternalId", "curie_keyword", "modEntityId_keyword", "modInternalId_keyword",
			"alleleSymbol.formatText", "alleleSymbol.displayText", "alleleSymbol.formatText_keyword", "alleleSymbol.displayText_keyword",
			"alleleFullName.formatText", "alleleFullName.displayText", "alleleFullName.formatText_keyword", "alleleFullName.displayText_keyword",
			"alleleSynonyms.formatText", "alleleSynonyms.displayText", "alleleSynonyms.formatText_keyword", "alleleSynonyms.displayText_keyword",
			"alleleSecondaryIds.secondaryId", "alleleSecondaryIds.secondaryId_keyword", "name", "name_keyword", "symbol", "symbol_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
	@JsonView({ View.FieldsOnly.class })
	private Allele interactorAGeneticPerturbation;
	
	@IndexedEmbedded(includePaths = {
			"curie", "modEntityId", "modInternalId", "curie_keyword", "modEntityId_keyword", "modInternalId_keyword",
			"alleleSymbol.formatText", "alleleSymbol.displayText", "alleleSymbol.formatText_keyword", "alleleSymbol.displayText_keyword",
			"alleleFullName.formatText", "alleleFullName.displayText", "alleleFullName.formatText_keyword", "alleleFullName.displayText_keyword",
			"alleleSynonyms.formatText", "alleleSynonyms.displayText", "alleleSynonyms.formatText_keyword", "alleleSynonyms.displayText_keyword",
			"alleleSecondaryIds.secondaryId", "alleleSecondaryIds.secondaryId_keyword", "name", "name_keyword", "symbol", "symbol_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
	@JsonView({ View.FieldsOnly.class })
	private Allele interactorBGeneticPerturbation;
	
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "phenotypesOrTraits_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@ElementCollection
	@JsonView({View.FieldsAndLists.class, View.GeneInteractionView.class})
	@JoinTable(
		joinColumns = @JoinColumn(name = "genegeneticinteraction_id"),
		indexes = {
			@Index(name = "genegeneticinteraction_phenotypesortraits_interaction_index", columnList = "genegeneticinteraction_id"),
			@Index(name = "genegeneticinteraction_phenotypesortraits_pt_index", columnList = "phenotypesOrTraits")
		}
	)
	private List<String> phenotypesOrTraits;
}
