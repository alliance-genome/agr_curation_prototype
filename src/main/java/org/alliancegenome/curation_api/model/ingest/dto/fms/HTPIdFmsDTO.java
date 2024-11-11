package org.alliancegenome.curation_api.model.ingest.dto.fms;

import java.util.List;

import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HTPIdFmsDTO extends BaseDTO {

	private String primaryId;
	private List<String> alternateIds;
	private CrossReferenceFmsDTO preferredCrossReference;
	private List<CrossReferenceFmsDTO> crossReferences;
}
