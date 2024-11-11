package org.alliancegenome.curation_api.model.ingest.dto.fms;

import java.util.List;

import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HTPExpressionDatasetAnnotationFmsDTO extends BaseDTO {

	private HTPIdFmsDTO datasetId;
	private List<PublicationFmsDTO> publications;
	private String title;
	private String summary;
	private Integer numChannels;
	private List<String> subSeries;
	private String dateAssigned;
	private List<String> categoryTags;
}
