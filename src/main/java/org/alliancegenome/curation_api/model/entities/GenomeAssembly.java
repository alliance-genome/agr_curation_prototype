package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Schema(name = "GenomeAssembly", description = "POJO that represents a GenomeAssembly")
@AGRCurationSchemaVersion(min = "2.4.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { BiologicalEntity.class })
public class GenomeAssembly extends BiologicalEntity {

	@IndexedEmbedded(includePaths = {"referencedCurie", "displayName", "resourceDescriptorPage.name", "referencedCurie_keyword", "displayName_keyword", "resourceDescriptorPage.name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(indexes = {
		@Index(columnList = "genomeassembly_id, crossreferences_id", name = "genomeassembly_crossreference_genomeassembly_xref_index"),
		@Index(columnList = "genomeassembly_id", name = "genomeassembly_crossreference_genomeassembly_index"),
		@Index(columnList = "crossreferences_id", name = "genomeassembly_crossreference_crossreference_index")
	})
	@JsonView({ View.FieldsAndLists.class })
	private List<CrossReference> crossReferences;
	
	@IndexedEmbedded(includePaths = {"name", "name_keyword", "curie", "curie_keyword", "modEntityId", "modEntityId_keyword", "modInternalId", "modInternalId_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@EqualsAndHashCode.Include
	@JsonView({ View.FieldsOnly.class })
	private AffectedGenomicModel specimenGenomicModel;

}
