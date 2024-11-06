package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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
@Schema(name = "AGM_Disease_Annotation", description = "Annotation class representing a agm disease annotation")
@JsonTypeName("AGMDiseaseAnnotation")
@AGRCurationSchemaVersion(min = "2.2.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { DiseaseAnnotation.class })

@Table(indexes = {
	@Index(name = "AGMDiseaseAnnotation_diseaseAnnotationSubject_index", columnList = "diseaseAnnotationSubject_id"),
	@Index(name = "AGMDiseaseAnnotation_inferredAllele_index", columnList = "inferredAllele_id"),
	@Index(name = "AGMDiseaseAnnotation_inferredGene_index", columnList = "inferredGene_id")
})
public class AGMDiseaseAnnotation extends DiseaseAnnotation {

	@IndexedEmbedded(includePaths = {"name", "name_keyword", "curie", "curie_keyword", "modEntityId", "modEntityId_keyword", "modInternalId", "modInternalId_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private AffectedGenomicModel diseaseAnnotationSubject;

	@IndexedEmbedded(includePaths = {
			"curie", "modEntityId", "modInternalId", "curie_keyword", "modEntityId_keyword", "modInternalId_keyword",
			"geneSymbol.formatText", "geneSymbol.displayText", "geneSymbol.formatText_keyword", "geneSymbol.displayText_keyword",
			"geneFullName.formatText", "geneFullName.displayText", "geneFullName.formatText_keyword", "geneFullName.displayText_keyword",
			"geneSystematicName.formatText", "geneSystematicName.displayText", "geneSystematicName.formatText_keyword", "geneSystematicName.displayText_keyword",
			"geneSynonyms.formatText", "geneSynonyms.displayText", "geneSynonyms.formatText_keyword", "geneSynonyms.displayText_keyword",
			"geneSecondaryIds.secondaryId", "geneSecondaryIds.secondaryId_keyword", "name", "name_keyword", "symbol", "symbol_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private Gene inferredGene;

	@IndexedEmbedded(includePaths = {
			"curie", "modEntityId", "modInternalId", "curie_keyword", "modEntityId_keyword", "modInternalId_keyword",
			"alleleSymbol.formatText", "alleleSymbol.displayText", "alleleSymbol.formatText_keyword", "alleleSymbol.displayText_keyword",
			"alleleFullName.formatText", "alleleFullName.displayText", "alleleFullName.formatText_keyword", "alleleFullName.displayText_keyword",
			"alleleSynonyms.formatText", "alleleSynonyms.displayText", "alleleSynonyms.formatText_keyword", "alleleSynonyms.displayText_keyword",
			"alleleSecondaryIds.secondaryId", "alleleSecondaryIds.secondaryId_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private Allele inferredAllele;

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
	@JoinTable(
		joinColumns = @JoinColumn(name = "agmdiseaseannotation_id"),
		inverseJoinColumns = @JoinColumn(name = "assertedgenes_id"),
		indexes = {
			@Index(name = "agmdiseaseannotation_gene_agmda_index", columnList = "agmdiseaseannotation_id"),
			@Index(name = "agmdiseaseannotation_gene_assertedgenes_index", columnList = "assertedgenes_id")
		}
	)
	@JsonView({ View.FieldsAndLists.class, View.DiseaseAnnotation.class, View.ForPublic.class })
	private List<Gene> assertedGenes;

	@IndexedEmbedded(includePaths = {
			"curie", "modEntityId", "modInternalId", "curie_keyword", "modEntityId_keyword", "modInternalId_keyword",
			"alleleSymbol.formatText", "alleleSymbol.displayText", "alleleSymbol.formatText_keyword", "alleleSymbol.displayText_keyword",
			"alleleFullName.formatText", "alleleFullName.displayText", "alleleFullName.formatText_keyword", "alleleFullName.displayText_keyword",
			"alleleSynonyms.formatText", "alleleSynonyms.displayText", "alleleSynonyms.formatText_keyword", "alleleSynonyms.displayText_keyword",
			"alleleSecondaryIds.secondaryId", "alleleSecondaryIds.secondaryId_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private Allele assertedAllele;

	@Transient
	@Override
	@JsonIgnore
	public String getSubjectCurie() {
		if (diseaseAnnotationSubject == null) {
			return null;
		}
		return diseaseAnnotationSubject.getCurie();
	}

	@Transient
	@Override
	@JsonIgnore
	public String getSubjectTaxonCurie() {
		if (diseaseAnnotationSubject == null) {
			return null;
		}
		if (diseaseAnnotationSubject.getTaxon() == null) {
			return null;
		}
		return diseaseAnnotationSubject.getTaxon().getCurie();
	}
	
	@Transient
	@Override
	@JsonIgnore
	public String getSubjectIdentifier() {
		if (diseaseAnnotationSubject == null) {
			return null;
		}
		return diseaseAnnotationSubject.getIdentifier();
	}

	@Transient
	@Override
	@JsonIgnore
	public String getSubjectSpeciesName() {
		if (diseaseAnnotationSubject == null) {
			return null;
		}
		if (diseaseAnnotationSubject.getTaxon() == null) {
			return null;
		}
		return diseaseAnnotationSubject.getTaxon().getGenusSpecies();
	}
}