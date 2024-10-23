package org.alliancegenome.curation_api.model.ingest.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.alliancegenome.curation_api.model.ingest.dto.base.AuditedObjectDTO;

@Data
@EqualsAndHashCode(callSuper = false)
public class GafDTO extends AuditedObjectDTO {

	private String geneID;

	private String goID;


}
