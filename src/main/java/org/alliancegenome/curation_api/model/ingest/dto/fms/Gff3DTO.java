package org.alliancegenome.curation_api.model.ingest.dto.fms;

import java.util.List;

import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Gff3DTO extends BaseDTO {

	private String seqId;
	private String source;
	private String type;
	private Integer start;
	private Integer end;
	private Float score;
	private String strand;
	private Integer phase;
	private List<String> attributes;
	
}
