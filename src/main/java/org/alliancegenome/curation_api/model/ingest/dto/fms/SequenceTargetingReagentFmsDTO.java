package org.alliancegenome.curation_api.model.ingest.dto.fms;

import java.util.List;

import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SequenceTargetingReagentFmsDTO extends BaseDTO {
	private String primaryId;
	private String name;
	private String taxonId;
	private List<String> synonyms;
	private List<String> secondaryIds;
	private List<String> targetGeneIds;
}
