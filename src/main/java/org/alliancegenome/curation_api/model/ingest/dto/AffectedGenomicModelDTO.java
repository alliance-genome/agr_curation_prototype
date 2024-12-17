package org.alliancegenome.curation_api.model.ingest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.SecondaryIdSlotAnnotationDTO;
import org.alliancegenome.curation_api.view.View;

import java.util.List;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "1.5.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = {GenomicEntityDTO.class}, submitted = true)
public class AffectedGenomicModelDTO extends GenomicEntityDTO {
	@JsonView({View.FieldsOnly.class})
	private String name;

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("subtype_name")
	private String subtypeName;

	@JsonView({View.FieldsAndLists.class})
	@JsonProperty("synonyms")
	private List<String> synonyms;
	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("agm_secondary_id_dtos")
	private List<SecondaryIdSlotAnnotationDTO> agmSecondaryIdDtos;


}
