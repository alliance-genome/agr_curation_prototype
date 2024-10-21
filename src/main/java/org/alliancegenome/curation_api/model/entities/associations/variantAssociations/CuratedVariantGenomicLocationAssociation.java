package org.alliancegenome.curation_api.model.entities.associations.variantAssociations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "2.4.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { VariantGenomicLocationAssociation.class })
@Schema(name = "CuratedVariantGenomicLocationAssociation", description = "POJO representing an association between a variant and a curated genomic location")

@Table(indexes = {
	@Index(name = "cvgla_internal_index", columnList = "internal"),
	@Index(name = "cvgla_obsolete_index", columnList = "obsolete"),
	@Index(name = "cvgla_hgvs_index", columnList = "hgvs"),
	@Index(name = "cvgla_createdby_index", columnList = "createdBy_id"),
	@Index(name = "cvgla_updatedby_index", columnList = "updatedBy_id"),
	@Index(name = "cvgla_relation_index", columnList = "relation_id"),
	@Index(name = "cvgla_dnamutationtype_index", columnList = "dnaMutationType_id"),
	@Index(name = "cvgla_genelocalizationtype_index", columnList = "geneLocalizationType_id"),
	@Index(name = "cvgla_consequence_index", columnList = "consequence_id"),
	@Index(name = "cvgla_curatedconsequence_index", columnList = "curatedConsequence_id"),
	@Index(name = "cvgla_variantassociationsubject_index", columnList = "variantassociationsubject_id"),
	@Index(name = "cvgla_vglaobject_index", columnList = "variantgenomiclocationassociationobject_id")
})

public class CuratedVariantGenomicLocationAssociation extends VariantGenomicLocationAssociation {

}
