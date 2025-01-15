package org.alliancegenome.curation_api.model.entities.associations.exonAssociations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.AssemblyComponent;
import org.alliancegenome.curation_api.model.entities.Exon;
import org.alliancegenome.curation_api.model.entities.LocationAssociation;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Column;
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
@AGRCurationSchemaVersion(min = "2.4.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { LocationAssociation.class })
@Schema(name = "ExonGenomicLocationAssociation", description = "POJO representing an association between an exon and a genomic location")

@Table(indexes = {
	@Index(columnList = "internal"),
	@Index(columnList = "obsolete"),
	@Index(columnList = "strand"),
	@Index(columnList = "createdBy_id"),
	@Index(columnList = "updatedBy_id"),
	@Index(columnList = "relation_id"),
	@Index(columnList = "exonassociationsubject_id"),
	@Index(columnList = "exongenomiclocationassociationobject_id")
})

public class ExonGenomicLocationAssociation extends LocationAssociation {

	@IndexedEmbedded(includePaths = {
		"curie", "curie_keyword", "primaryExternalId", "primaryExternalId_keyword",
		"modInternalId", "modInternalId_keyword", "name", "name_keyword"
	})
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	@JsonIgnoreProperties("exonGenomicLocationAssociations")
	@Fetch(FetchMode.JOIN)
	private Exon exonAssociationSubject;

	@IndexedEmbedded(includePaths = {
		"curie", "curie_keyword", "primaryExternalId", "primaryExternalId_keyword",
		"modInternalId", "modInternalId_keyword", "name", "name_keyword"
	})
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@Fetch(FetchMode.JOIN)
	private AssemblyComponent exonGenomicLocationAssociationObject;
	
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "strand_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class })
	@Column(length = 1)
	private String strand;
}
