package org.alliancegenome.curation_api.model.entities.associations.transcriptAssociations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.CodingSequence;
import org.alliancegenome.curation_api.model.entities.EvidenceAssociation;
import org.alliancegenome.curation_api.model.entities.Transcript;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
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
@AGRCurationSchemaVersion(min = "2.2.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { EvidenceAssociation.class })
@Schema(name = "TranscriptCodingSequenceAssociation", description = "POJO representing an association between a transcript and a CDS")

@Table(indexes = {
	@Index(columnList = "internal"),
	@Index(columnList = "obsolete"),
	@Index(columnList = "createdBy_id"),
	@Index(columnList = "updatedBy_id"),
	@Index(columnList = "relation_id"),
	@Index(columnList = "transcriptassociationsubject_id"),
	@Index(columnList = "transcriptcodingsequenceassociationobject_id")
})

public class TranscriptCodingSequenceAssociation extends EvidenceAssociation {

	@IndexedEmbedded(includePaths = {"curie", "name", "modEntityId", "modInternalId",
			"curie_keyword", "name_keyword", "modEntityId_keyword", "modInternalId_keyword"})
	@ManyToOne
	@JsonIgnoreProperties({
		"transcriptCodingSequenceAssociations",
		"transcriptExonAssociations",
		"transcriptGeneAssociations",
		"transcriptGenomicLocationAssociations"
	})
	@JsonView({ View.FieldsOnly.class })
	private Transcript transcriptAssociationSubject;
	
	@IndexedEmbedded(includePaths = {"curie", "name", "modEntityId", "modInternalId", "uniqueId",
			"curie_keyword", "name_keyword", "modEntityId_keyword", "modInternalId_keyword", "uniqueId_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonIgnoreProperties({
		"transcriptCodingSequenceAssociations",
		"codingSequenceGenomicLocationAssociations",
		"constructGenomicEntityAssociations"
	})
	@JsonView({ View.FieldsOnly.class })
	private CodingSequence transcriptCodingSequenceAssociationObject;
	
	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private VocabularyTerm relation;
}
