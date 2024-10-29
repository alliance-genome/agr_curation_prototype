package org.alliancegenome.curation_api.model.ingest.dto.fms;

import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MicroarraySampleDetailsDTO extends BaseDTO{
	private String channelId;
	private Integer channelNum;
}
