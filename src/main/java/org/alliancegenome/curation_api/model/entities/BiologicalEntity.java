package org.alliancegenome.curation_api.model.entities;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.bridges.BiologicalEntityTypeBridge;
import org.alliancegenome.curation_api.model.entities.base.SubmittedObject;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.TypeBinderRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.TypeBinding;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
	@JsonSubTypes.Type(value = AffectedGenomicModel.class, name = "AffectedGenomicModel"),
	@JsonSubTypes.Type(value = Allele.class, name = "Allele"),
	@JsonSubTypes.Type(value = AssemblyComponent.class, name = "AssemblyComponent"),
	@JsonSubTypes.Type(value = CodingSequence.class, name = "CodingSequence"),
	@JsonSubTypes.Type(value = Exon.class, name = "Exon"),
	@JsonSubTypes.Type(value = Gene.class, name = "Gene"),
	@JsonSubTypes.Type(value = Transcript.class, name = "Transcript"),
	@JsonSubTypes.Type(value = Variant.class, name = "Variant"),
	@JsonSubTypes.Type(value = SequenceTargetingReagent.class, name = "SequenceTargetingReagent")
})
@Entity
@TypeBinding(binder = @TypeBinderRef(type = BiologicalEntityTypeBridge.class))
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "2.0.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { SubmittedObject.class })
@Table(
	indexes = {
		@Index(name = "biologicalentity_taxon_index", columnList = "taxon_id"),
		@Index(name = "biologicalentity_curie_index", columnList = "curie"),
		@Index(name = "biologicalentity_createdby_index", columnList = "createdBy_id"),
		@Index(name = "biologicalentity_updatedby_index", columnList = "updatedBy_id"),
		@Index(name = "biologicalentity_modentityid_index", columnList = "modentityid"),
		@Index(name = "biologicalentity_modinternalid_index", columnList = "modinternalid"),
		@Index(name = "biologicalentity_dataprovider_index", columnList = "dataprovider_id")
	},
	uniqueConstraints = {
		@UniqueConstraint(name = "biologicalentity_curie_uk", columnNames = "curie"),
		@UniqueConstraint(name = "biologicalentity_modentityid_uk", columnNames = "modentityid"),
		@UniqueConstraint(name = "biologicalentity_modinternalid_uk", columnNames = "modinternalid")
	}
)
public class BiologicalEntity extends SubmittedObject {

	@IndexedEmbedded(includePaths = {"name", "curie", "name_keyword", "curie_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private NCBITaxonTerm taxon;

}
