package org.alliancegenome.curation_api.model.ingest.dto.fms;

import java.util.List;

import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HTPExpressionDatasetSampleAnnotationIngestFmsDTO extends BaseDTO{
    private MetaDataFmsDTO metaData;
	private List<HTPExpressionDatasetSampleAnnotationFmsDTO> data;
}
