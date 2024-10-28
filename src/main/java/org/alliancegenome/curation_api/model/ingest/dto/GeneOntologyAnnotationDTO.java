package org.alliancegenome.curation_api.model.ingest.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Annotation;
import org.alliancegenome.curation_api.model.ingest.dto.base.AuditedObjectDTO;

@Data
@EqualsAndHashCode(callSuper = false)
@AGRCurationSchemaVersion(min = "2.8.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { Annotation.class })
public class GeneOntologyAnnotationDTO extends AuditedObjectDTO {

	private String geneIdentifier;

	private String goTermCurie;


}
