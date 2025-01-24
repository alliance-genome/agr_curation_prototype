package org.alliancegenome.curation_api.model.entities.associations.agmAssociations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Association;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "2.8.1", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = {Association.class})
@Schema(name = "AgmAgmAssociation", description = "POJO representing an association between an AGM and another AGM")

@Table(indexes = {
	@Index(name = "AgmAgmAssociation_internal_index", columnList = "internal"),
	@Index(name = "AgmAgmAssociation_obsolete_index", columnList = "obsolete"),
	@Index(name = "AgmAgmAssociation_createdBy_index", columnList = "createdBy_id"),
	@Index(name = "AgmAgmAssociation_updatedBy_index", columnList = "updatedBy_id"),
	@Index(name = "AgmAgmAssociation_relation_index", columnList = "relation_id"),
	@Index(name = "AgmAgmAssociation_agmAssociationSubject_index", columnList = "agmAssociationSubject_id"),
	@Index(name = "AgmAgmAssociation_AgmAgmAssociationObject_index", columnList = "agmAgmAssociationObject_id")
})

public class AgmAgmAssociation extends Association {

	@IndexedEmbedded(includePaths = {
		"curie", "name", "curie_keyword", "name_keyword",
		"primaryExternalId", "primaryExternalId_keyword", "modInternalId", "modInternalId_keyword"})
	@ManyToOne
	@JsonView({View.FieldsOnly.class})
	@JsonIgnoreProperties({"parentalPopulations", "agmSequenceTargetingReagentAssociations"})
	@Fetch(FetchMode.JOIN)
	private AffectedGenomicModel agmAssociationSubject;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class})
	private VocabularyTerm relation;

	@IndexedEmbedded(includePaths = {"name", "synonyms"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class})
	@JsonIgnoreProperties({"parentalPopulations", "agmSequenceTargetingReagentAssociations"})
	private AffectedGenomicModel agmAgmAssociationObject;
}
