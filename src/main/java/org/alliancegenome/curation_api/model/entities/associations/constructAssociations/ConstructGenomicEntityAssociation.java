package org.alliancegenome.curation_api.model.entities.associations.constructAssociations;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.EvidenceAssociation;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.Note;
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

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "2.2.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { EvidenceAssociation.class })
@Schema(name = "ConstructGenomicEntityAssociation", description = "POJO representing an association between a construct and a genomic entity")

@Table(indexes = {
	@Index(columnList = "internal"),
	@Index(columnList = "obsolete"),
	@Index(columnList = "createdBy_id"),
	@Index(columnList = "updatedBy_id"),
	@Index(columnList = "constructassociationsubject_id"),
	@Index(columnList = "constructgenomicentityassociationobject_id"),
	@Index(columnList = "relation_id")
})

public class ConstructGenomicEntityAssociation extends EvidenceAssociation {

	@IndexedEmbedded(includePaths = {
		"curie", "constructSymbol.displayText", "constructSymbol.formatText",
		"constructFullName.displayText", "constructFullName.formatText", "modEntityId", "modInternalId",
		"curie_keyword", "constructSymbol.displayText_keyword", "constructSymbol.formatText_keyword",
		"constructFullName.displayText_keyword", "constructFullName.formatText_keyword", "modEntityId_keyword", "modInternalId_keyword"})
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	@JsonIgnoreProperties("constructGenomicEntityAssociations")
	@Fetch(FetchMode.JOIN)
	private Construct constructAssociationSubject;
	
	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private VocabularyTerm relation;
	
	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	@JsonIgnoreProperties({
		"alleleGeneAssociations", "constructGenomicEntityAssociations", "sequenceTargetingReagentGeneAssociations",
		"transcriptGenomicLocationAssociations", "exonGenomicLocationAssociations", "codingSequenceGenomicLocationAssociations"
	})
	private GenomicEntity constructGenomicEntityAssociationObject;

	@IndexedEmbedded(includePaths = {"freeText", "noteType.name", "references.curie",
		"references.primaryCrossReferenceCurie", "freeText_keyword", "noteType.name_keyword", "references.curie_keyword",
		"references.primaryCrossReferenceCurie_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ View.FieldsAndLists.class, View.ConstructView.class })
	@JoinTable(
		joinColumns = @JoinColumn(name = "constructgenomicentityassociation_id"),
		inverseJoinColumns = @JoinColumn(name = "relatedNotes_id"),
		indexes = {
			@Index(name = "constructgeassociation_note_cgea_index", columnList = "constructgenomicentityassociation_id"),
			@Index(name = "constructgeassociation_note_relatednotes_index", columnList = "relatedNotes_id")
		}
	)
	private List<Note> relatedNotes;
}
