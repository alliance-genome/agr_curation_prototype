package org.alliancegenome.curation_api.model.entities;

import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;
import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Schema(name = "MicroarraySampleDetails", description = "POJO that represents the MicroarraySampleDetails")
@AGRCurationSchemaVersion(min = "2.7.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })
@Indexed
public class MicroarraySampleDetails extends AuditedObject {

	@JsonView({ View.FieldsOnly.class })
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "channelid_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	private String channelId;

	@JsonView({ View.FieldsOnly.class })
	@GenericField(projectable = Projectable.YES, sortable = Sortable.YES)
	private Integer channelNumber;

}
