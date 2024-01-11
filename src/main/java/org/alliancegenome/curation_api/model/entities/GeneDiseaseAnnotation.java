package org.alliancegenome.curation_api.model.entities;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Schema(name = "Gene_Disease_Annotation", description = "Annotation class representing a gene disease annotation")
@JsonTypeName("GeneDiseaseAnnotation")
@OnDelete(action = OnDeleteAction.CASCADE)
@AGRCurationSchemaVersion(min = "1.2.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { DiseaseAnnotation.class })
public class GeneDiseaseAnnotation extends DiseaseAnnotation {

	@IndexedEmbedded(includeDepth = 2)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
	@JsonView({ View.FieldsOnly.class })
	private Gene subject;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private AffectedGenomicModel sgdStrainBackground;

	@Transient
	@Override
	@JsonIgnore
	public String getSubjectCurie() {
		if (subject == null)
			return null;
		return subject.getCurie();
	}

	@Transient
	@Override
	@JsonIgnore
	public String getSubjectTaxonCurie() {
		if (subject == null)
			return null;
		if (subject.getTaxon() == null)
			return null;
		return subject.getTaxon().getCurie();
	}
	
	@Transient
	@Override
	@JsonIgnore
	public String getSubjectIdentifier() {
		if (subject == null)
			return null;
		return subject.getIdentifier();
	}

	@Transient
	@Override
	@JsonIgnore
	public String getSubjectSpeciesName() {
		if (subject == null)
			return null;
		if (subject.getTaxon() == null)
			return null;
		return subject.getTaxon().getGenusSpecies();
	}
}
