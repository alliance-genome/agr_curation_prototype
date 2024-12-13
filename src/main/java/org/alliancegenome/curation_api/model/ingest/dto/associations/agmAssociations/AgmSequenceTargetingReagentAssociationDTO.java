package org.alliancegenome.curation_api.model.ingest.dto.associations.agmAssociations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "2.0.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AgmGenomicEntityAssociationDTO.class }, submitted = true)
public class AgmSequenceTargetingReagentAssociationDTO extends AgmGenomicEntityAssociationDTO {

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("str_identifier")
	private String strIdentifier;

}
