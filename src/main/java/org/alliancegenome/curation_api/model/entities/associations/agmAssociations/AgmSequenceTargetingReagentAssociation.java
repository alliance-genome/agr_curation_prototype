package org.alliancegenome.curation_api.model.entities.associations.agmAssociations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
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
@AGRCurationSchemaVersion(min = "2.2.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AgmGenomicEntityAssociation.class })
@Schema(name = "AgmSequenceTargetingReagentAssociation", description = "POJO representing an association between an AGM and a STR")

@Table(indexes = {
	@Index(name = "AgmSequenceTargetingReagentAssociation_internal_index", columnList = "internal"),
	@Index(name = "AgmSequenceTargetingReagentAssociation_obsolete_index", columnList = "obsolete"),
	@Index(name = "AgmSequenceTargetingReagentAssociation_createdBy_index", columnList = "createdBy_id"),
	@Index(name = "AgmSequenceTargetingReagentAssociation_updatedBy_index", columnList = "updatedBy_id"),
	@Index(name = "AgmSequenceTargetingReagentAssociation_evidenceCode_index", columnList = "evidencecode_id"),
	@Index(name = "AgmSequenceTargetingReagentAssociation_relatedNote_index", columnList = "relatedNote_id"),
	@Index(name = "AgmSequenceTargetingReagentAssociation_relation_index", columnList = "relation_id"),
	@Index(name = "AgmSequenceTargetingReagentAssociation_agmAssociationSubject_index", columnList = "agmAssociationSubject_id"),
	@Index(name = "AgmSequenceTargetingReagentAssociation_agmStrAssociationObject_index", columnList = "agmStrAssociationObject_id")
})

public class AgmSequenceTargetingReagentAssociation extends AgmGenomicEntityAssociation {

	//todo: fix these
	@IndexedEmbedded(includePaths = {
		"curie", "agmSymbol.displayText", "agmSymbol.formatText", "agmFullName.displayText", "agmFullName.formatText",
		"curie_keyword", "agmSymbol.displayText_keyword", "agmSymbol.formatText_keyword", "agmFullName.displayText_keyword",
		"agmFullName.formatText_keyword", "modEntityId", "modEntityId_keyword", "modInternalId", "modInternalId_keyword" })
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	@JsonIgnoreProperties({"agmSequenceTargetingReagentAssociations"})
	@Fetch(FetchMode.JOIN)
	private AffectedGenomicModel agmAssociationSubject;

	//todo: fix these -- should be like the str-gene association i think
	@IndexedEmbedded(includePaths = {"name", "synonyms", "secondaryIdentifiers"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	//todo - fix this -- may also be in str-gene association
	@JsonIgnoreProperties({ "agmSequenceTargetingReagentAssociations", "sequenceTargetingReagentGeneAssociations"})
	private SequenceTargetingReagent agmStrAssociationObject;
}
