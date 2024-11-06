package org.alliancegenome.curation_api.model.entities;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Schema(name = "Gene_Disease_Annotation", description = "Annotation class representing a gene disease annotation")
@JsonTypeName("GeneDiseaseAnnotation")
@AGRCurationSchemaVersion(min = "2.2.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { DiseaseAnnotation.class })

@Table(indexes = {
	@Index(name = "GeneDiseaseAnnotation_diseaseAnnotationSubject_index", columnList = "diseaseAnnotationSubject_id"),
	@Index(name = "GeneDiseaseAnnotation_sgdStrainBackground_index", columnList = "sgdStrainBackground_id")
})

public class GeneDiseaseAnnotation extends DiseaseAnnotation {

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
	@org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private Gene diseaseAnnotationSubject;

	@IndexedEmbedded(includePaths = {"name", "name_keyword", "curie", "curie_keyword", "modEntityId", "modEntityId_keyword", "modInternalId", "modInternalId_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private AffectedGenomicModel sgdStrainBackground;

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
