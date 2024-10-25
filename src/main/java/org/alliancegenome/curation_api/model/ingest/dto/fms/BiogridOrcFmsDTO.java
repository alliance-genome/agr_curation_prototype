package org.alliancegenome.curation_api.model.ingest.dto.fms;

import java.util.List;

import org.alliancegenome.curation_api.model.ingest.dto.CrossReferenceDTO;
import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BiogridOrcFmsDTO extends BaseDTO {
	private Integer screenId;
	private String identifierId;
	private String identifierType;
	private String officialSymbol;
	private String aliases;
	private Integer organismId;
	private String organismOfficial;
	private Double score1;
	private Double score2;
	private Double score3;
	private Double score4;
	private Double score5;
	private String hit;
	private String source;
	private List<CrossReferenceDTO> crossReferenceDtos;
}
