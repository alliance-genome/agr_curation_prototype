package org.alliancegenome.curation_api.model.entities.associations.agmAssociations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Association;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
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
@AGRCurationSchemaVersion(min = "2.9.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { Association.class })
@Schema(name = "AgmSequenceTargetingReagentAssociation", description = "POJO representing an association between an AGM and a STR")

@Table(indexes = {
	@Index(name = "AgmSequenceTargetingReagentAssociation_internal_index", columnList = "internal"),
	@Index(name = "AgmSequenceTargetingReagentAssociation_obsolete_index", columnList = "obsolete"),
	@Index(name = "AgmSequenceTargetingReagentAssociation_createdBy_index", columnList = "createdBy_id"),
	@Index(name = "AgmSequenceTargetingReagentAssociation_updatedBy_index", columnList = "updatedBy_id"),
	@Index(name = "AgmSequenceTargetingReagentAssociation_relation_index", columnList = "relation_id"),
	@Index(name = "AgmSequenceTargetingReagentAssociation_agmAssociationSubject_index", columnList = "agmAssociationSubject_id"),
	@Index(name = "AgmSequenceTargetingReagentAssociation_agmSequenceTargetingReagentAssociationObject_index", columnList = "agmSequenceTargetingReagentAssociationObject_id")
})

public class AgmSequenceTargetingReagentAssociation extends Association {

	@IndexedEmbedded(includePaths = {
		"curie", "name", "curie_keyword", "name_keyword",
		"modEntityId", "modEntityId_keyword", "modInternalId", "modInternalId_keyword" })
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	@JsonIgnoreProperties({"agmSequenceTargetingReagentAssociations", "constructGenomicEntityAssociations"})
	@Fetch(FetchMode.JOIN)
	private AffectedGenomicModel agmAssociationSubject;

	@IndexedEmbedded(includePaths = { "name", "name_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private VocabularyTerm relation;

	@IndexedEmbedded(includePaths = {"name", "synonyms", "secondaryIdentifiers"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	@JsonIgnoreProperties({ "agmSequenceTargetingReagentAssociations", "sequenceTargetingReagentGeneAssociations"})
	private SequenceTargetingReagent agmSequenceTargetingReagentAssociationObject;
}
