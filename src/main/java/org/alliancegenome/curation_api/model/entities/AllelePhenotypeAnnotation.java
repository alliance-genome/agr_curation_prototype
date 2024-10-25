package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Schema(name = "Allele_Phenotype_Annotation", description = "Annotation class representing a allele phenotype annotation")
@JsonTypeName("AllelePhenotypeAnnotation")
@AGRCurationSchemaVersion(min = "2.2.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { PhenotypeAnnotation.class })

@Table(indexes = {
	@Index(name = "AllelePhenotypeAnnotation_inferredGene_index", columnList = "inferredGene_id"),
	@Index(name = "AllelePhenotypeAnnotation_phenotypeAnnotationSubject_index", columnList = "phenotypeAnnotationSubject_id")
})

public class AllelePhenotypeAnnotation extends PhenotypeAnnotation {

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
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private Allele phenotypeAnnotationSubject;

	@IndexedEmbedded(includePaths = {
			"curie", "modEntityId", "modInternalId", "curie_keyword", "modEntityId_keyword", "modInternalId_keyword",
			"geneSymbol.formatText", "geneSymbol.displayText", "geneSymbol.formatText_keyword", "geneSymbol.displayText_keyword",
			"geneFullName.formatText", "geneFullName.displayText", "geneFullName.formatText_keyword", "geneFullName.displayText_keyword",
			"geneSystematicName.formatText", "geneSystematicName.displayText", "geneSystematicName.formatText_keyword", "geneSystematicName.displayText_keyword",
			"geneSynonyms.formatText", "geneSynonyms.displayText", "geneSynonyms.formatText_keyword", "geneSynonyms.displayText_keyword",
			"geneSecondaryIds.secondaryId", "geneSecondaryIds.secondaryId_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private Gene inferredGene;

	@IndexedEmbedded(includePaths = {
			"curie", "modEntityId", "modInternalId", "curie_keyword", "modEntityId_keyword", "modInternalId_keyword",
			"geneSymbol.formatText", "geneSymbol.displayText", "geneSymbol.formatText_keyword", "geneSymbol.displayText_keyword",
			"geneFullName.formatText", "geneFullName.displayText", "geneFullName.formatText_keyword", "geneFullName.displayText_keyword",
			"geneSystematicName.formatText", "geneSystematicName.displayText", "geneSystematicName.formatText_keyword", "geneSystematicName.displayText_keyword",
			"geneSynonyms.formatText", "geneSynonyms.displayText", "geneSynonyms.formatText_keyword", "geneSynonyms.displayText_keyword",
			"geneSecondaryIds.secondaryId", "geneSecondaryIds.secondaryId_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@Fetch(FetchMode.SELECT)
	@JsonView({ View.FieldsAndLists.class, View.PhenotypeAnnotationView.class, View.ForPublic.class })
	@JoinTable(
		joinColumns = @JoinColumn(name = "allelephenotypeannotation_id"),
		inverseJoinColumns = @JoinColumn(name = "assertedgenes_id"),
		indexes = {
			@Index(name = "allelephenotypeannotation_gene_allelepa_index", columnList = "allelephenotypeannotation_id"),
			@Index(name = "allelephenotypeannotation_gene_assertedgenes_index", columnList = "assertedgenes_id")
		}
	)
	private List<Gene> assertedGenes;

	@Transient
	@Override
	@JsonIgnore
	public String getSubjectCurie() {
		if (phenotypeAnnotationSubject == null) {
			return null;
		}
		return phenotypeAnnotationSubject.getCurie();
	}

	@Transient
	@Override
	@JsonIgnore
	public String getSubjectTaxonCurie() {
		if (phenotypeAnnotationSubject == null) {
			return null;
		}
		if (phenotypeAnnotationSubject.getTaxon() == null) {
			return null;
		}
		return phenotypeAnnotationSubject.getTaxon().getCurie();
	}
	
	@Transient
	@Override
	@JsonIgnore
	public String getSubjectIdentifier() {
		if (phenotypeAnnotationSubject == null) {
			return null;
		}
		return phenotypeAnnotationSubject.getIdentifier();
	}

	@Transient
	@Override
	@JsonIgnore
	public String getSubjectSpeciesName() {
		if (phenotypeAnnotationSubject == null) {
			return null;
		}
		if (phenotypeAnnotationSubject.getTaxon() == null) {
			return null;
		}
		return phenotypeAnnotationSubject.getTaxon().getGenusSpecies();
	}
}
