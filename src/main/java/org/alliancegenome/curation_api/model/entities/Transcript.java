package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.associations.transcriptAssociations.TranscriptCodingSequenceAssociation;
import org.alliancegenome.curation_api.model.entities.associations.transcriptAssociations.TranscriptExonAssociation;
import org.alliancegenome.curation_api.model.entities.associations.transcriptAssociations.TranscriptGeneAssociation;
import org.alliancegenome.curation_api.model.entities.associations.transcriptAssociations.TranscriptGenomicLocationAssociation;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

//@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = { "transcriptGenomicLocationAssociations", "transcriptGeneAssociations", "transcriptCodingSequenceAssociations", "transcriptExonAssociations" }, callSuper = true)
@Schema(name = "Transcript", description = "POJO that represents the Transcript")
@AGRCurationSchemaVersion(min = "2.8.1", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { GenomicEntity.class })
@Table(indexes = {
	@Index(name = "transcript_transcriptId_index", columnList = "transcriptId"),
	@Index(name = "transcript_transcriptType_index", columnList = "transcriptType_id")
})
public class Transcript extends GenomicEntity {

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "transcriptId_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class })
	private String transcriptId;
	
	@JsonView({ View.FieldsOnly.class })
	private String name;

	@IndexedEmbedded(includePaths = {"curie", "name", "curie_keyword", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private SOTerm transcriptType;

	@IndexedEmbedded(
		includePaths = {
			"transcriptGenomicLocationAssociationObject.curie", "transcriptGenomicLocationAssociationObject.curie_keyword",
			"transcriptGenomicLocationAssociationObject.primaryExternalId", "transcriptGenomicLocationAssociationObject.primaryExternalId_keyword",
			"transcriptGenomicLocationAssociationObject.modInternalId", "transcriptGenomicLocationAssociationObject.modInternalId_keyword",
			"start", "end"
		}
	)
	@OneToMany(mappedBy = "transcriptAssociationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ View.FieldsAndLists.class })
	private List<TranscriptGenomicLocationAssociation> transcriptGenomicLocationAssociations;

	@IndexedEmbedded(
		includePaths = {
			"transcriptCodingSequenceAssociationObject.curie", "transcriptCodingSequenceAssociationObject.name", "transcriptCodingSequenceAssociationObject.primaryExternalId",
			"transcriptCodingSequenceAssociationObject.modInternalId", "transcriptCodingSequenceAssociationObject.uniqueId",
			"transcriptCodingSequenceAssociationObject.curie_keyword", "transcriptCodingSequenceAssociationObject.name_keyword", "transcriptCodingSequenceAssociationObject.primaryExternalId_keyword",
			"transcriptCodingSequenceAssociationObject.modInternalId_keyword", "transcriptCodingSequenceAssociationObject.uniqueId_keyword"
		}
	)
	@OneToMany(mappedBy = "transcriptAssociationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ View.FieldsAndLists.class })
	private List<TranscriptCodingSequenceAssociation> transcriptCodingSequenceAssociations;

	@IndexedEmbedded(
		includePaths = {
			"transcriptExonAssociationObject.curie", "transcriptExonAssociationObject.name", "transcriptExonAssociationObject.primaryExternalId",
			"transcriptExonAssociationObject.modInternalId", "transcriptExonAssociationObject.uniqueId",
			"transcriptExonAssociationObject.curie_keyword", "transcriptExonAssociationObject.name_keyword", "transcriptExonAssociationObject.primaryExternalId_keyword",
			"transcriptExonAssociationObject.modInternalId_keyword", "transcriptExonAssociationObject.uniqueId_keyword"
		}
	)
	@OneToMany(mappedBy = "transcriptAssociationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ View.FieldsAndLists.class })
	private List<TranscriptExonAssociation> transcriptExonAssociations;

	@IndexedEmbedded(
		includePaths = {
			"transcriptGeneAssociationObject.curie", "transcriptGeneAssociationObject.geneSymbol.displayText", "transcriptGeneAssociationObject.geneSymbol.formatText", "transcriptGeneAssociationObject.geneFullName.displayText",
			"transcriptGeneAssociationObject.geneFullName.formatText", "transcriptGeneAssociationObject.curie_keyword", "transcriptGeneAssociationObject.geneSymbol.displayText_keyword",
			"transcriptGeneAssociationObject.geneSymbol.formatText_keyword", "transcriptGeneAssociationObject.geneFullName.displayText_keyword", "transcriptGeneAssociationObject.geneFullName.formatText_keyword",
			"transcriptGeneAssociationObject.primaryExternalId", "transcriptGeneAssociationObject.modInternalId", "transcriptGeneAssociationObject.primaryExternalId_keyword", "transcriptGeneAssociationObject.modInternalId_keyword"
		}
	)
	@OneToMany(mappedBy = "transcriptAssociationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ View.FieldsAndLists.class })
	private List<TranscriptGeneAssociation> transcriptGeneAssociations;
}
