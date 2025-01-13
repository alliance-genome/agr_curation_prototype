package org.alliancegenome.curation_api.model.entities.associations.variantAssociations;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.PredictedVariantConsequence;
import org.alliancegenome.curation_api.view.View;
import org.alliancegenome.curation_api.view.View.VariantView;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = "predictedVariantConsequences", callSuper = true)
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
	}, name = "CuratedVariantGenomicLocation"
)

public class CuratedVariantGenomicLocationAssociation extends VariantGenomicLocationAssociation {
	
	@IndexedEmbedded(
		includePaths = {
			"variantTranscript.name", "variantTranscript.primaryExternalId",
			"variantTranscript.modInternalId", "variantTranscript.curie",
			"vepConsequence.name", "variantTranscript.name_keyword",
			"variantTranscript.primaryExternalId_keyword", "variantTranscript.modInternalId_keyword",
			"variantTranscript.curie_keyword", "vepConsequence.name_keyword",
			"variantTranscript.transcriptId", "variantTranscript.transcriptId_keyword"
		}
	)
	@OneToMany(mappedBy = "variantGenomicLocation", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ View.FieldsAndLists.class, VariantView.class })
	private List<PredictedVariantConsequence> predictedVariantConsequences;
}
