package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;

@MappedSuperclass
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Schema(name = "evidenceAssociation", description = "POJO that represents an association supported by any number of information content entities")
@AGRCurationSchemaVersion(min = "1.9.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { Association.class })
public class EvidenceAssociation extends Association {

	@IndexedEmbedded(includeDepth = 2)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({ View.FieldsAndLists.class, View.AlleleView.class, View.GeneView.class, View.ConstructView.class, View.GeneInteractionView.class })
	@JoinTable(
		joinColumns = @JoinColumn(name = "association_id"),
		inverseJoinColumns = @JoinColumn(name = "evidence_id"),
		indexes = {
			@Index(columnList = "association_id"),
			@Index(columnList = "evidence_id")
		}
	)
	private List<InformationContentEntity> evidence;

}
