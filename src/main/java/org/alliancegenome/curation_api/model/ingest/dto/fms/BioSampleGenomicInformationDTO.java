package org.alliancegenome.curation_api.model.ingest.dto.fms;
import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BioSampleGenomicInformationDTO extends BaseDTO {
	private String biosampleId;
	private String idType;
	private String bioSampleText;
}
