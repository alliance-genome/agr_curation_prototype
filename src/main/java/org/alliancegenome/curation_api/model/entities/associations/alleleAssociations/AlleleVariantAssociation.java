package org.alliancegenome.curation_api.model.entities.associations.alleleAssociations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "2.2.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AlleleGenomicEntityAssociation.class })
@Schema(name = "AlleleVariantAssociation", description = "POJO representing an association between an allele and a gene")

@Table(indexes = {
	@Index(name = "AlleleVariantAssociation_internal_index", columnList = "internal"),
	@Index(name = "AlleleVariantAssociation_obsolete_index", columnList = "obsolete"),
	@Index(name = "AlleleVariantAssociation_createdBy_index", columnList = "createdBy_id"),
	@Index(name = "AlleleVariantAssociation_updatedBy_index", columnList = "updatedBy_id"),
	@Index(name = "AlleleVariantAssociation_evidenceCode_index", columnList = "evidencecode_id"),
	@Index(name = "AlleleVariantAssociation_relatedNote_index", columnList = "relatedNote_id"),
	@Index(name = "AlleleVariantAssociation_relation_index", columnList = "relation_id"),
	@Index(name = "AlleleVariantAssociation_alleleAssociationSubject_index", columnList = "alleleAssociationSubject_id"),
	@Index(name = "AlleleVariantAssociation_alleleVariantAssociationObject_index", columnList = "alleleVariantAssociationObject_id")
})

public class AlleleVariantAssociation extends AlleleGenomicEntityAssociation {

	@IndexedEmbedded(includePaths = {
		"curie", "alleleSymbol.displayText", "alleleSymbol.formatText", "alleleFullName.displayText", "alleleFullName.formatText",
		"curie_keyword", "alleleSymbol.displayText_keyword", "alleleSymbol.formatText_keyword", "alleleFullName.displayText_keyword",
		"alleleFullName.formatText_keyword", "modEntityId", "modEntityId_keyword", "modInternalId", "modInternalId_keyword" })
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	@JsonIgnoreProperties({"alleleGeneAssociations", "alleleVariantAssociations"})
	@Fetch(FetchMode.JOIN)
	private Allele alleleAssociationSubject;

	@IndexedEmbedded(includePaths = { "curie", "curie_keyword", "modEntityId", "modEntityId_keyword",
		"modInternalId", "modInternalId_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class, View.AlleleView.class })
	@JsonIgnoreProperties({ "alleleVariantAssociations", "constructGenomicEntityAssociations" })
	private Variant alleleVariantAssociationObject;
}
