package org.alliancegenome.curation_api.model.ingest.dto.fms;


import lombok.Data;
import lombok.EqualsAndHashCode;

import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

@Data
@EqualsAndHashCode(callSuper = true)
public class GeneExpressionFmsDTO extends BaseDTO {
	private String geneId;
	private String assay;
	private String dateAssigned;
	private PublicationFmsDTO evidence;
	private WhenExpressedFmsDTO whenExpressed;
	private WhereExpressedFmsDTO whereExpressed;

}
