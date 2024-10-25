package org.alliancegenome.curation_api.model.ingest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.alliancegenome.curation_api.model.ingest.dto.base.AuditedObjectDTO;

@Data
@EqualsAndHashCode(callSuper = false)
public class GeneOntologyAnnotationDTO extends AuditedObjectDTO {

	private String geneIdentifier;

	private String goTermCurie;


}
