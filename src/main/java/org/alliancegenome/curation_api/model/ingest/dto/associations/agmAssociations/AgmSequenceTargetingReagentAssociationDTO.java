package org.alliancegenome.curation_api.model.ingest.dto.associations.agmAssociations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.base.AuditedObjectDTO;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "2.9.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObjectDTO.class }, submitted = true)
public class AgmSequenceTargetingReagentAssociationDTO extends AuditedObjectDTO {

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("agm_subject_identifier")
	private String agmSubjectIdentifier;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("relation_name")
	private String relationName;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("sequence_targeting_reagent_identifier")
	private String sequenceTargetingReagentIdentifier;

}
