package org.alliancegenome.curation_api.model.entities;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@AGRCurationSchemaVersion(min = "2.2.3", max = LinkMLSchemaConstants.LATEST_RELEASE)
@Schema(name = "Expression_Pattern", description = "Annotation class representing an expression pattern")

@Table(indexes = {
	@Index(name = "expressionpattern_whenexpressed_index", columnList = "whenexpressed_id"),
	@Index(name = "expressionpattern_whereexpressed_index", columnList = "whereexpressed_id")
})
public class ExpressionPattern extends AuditedObject {

	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ View.FieldsOnly.class})
	private TemporalContext whenExpressed;

	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ View.FieldsOnly.class})
	private AnatomicalSite whereExpressed;
}
