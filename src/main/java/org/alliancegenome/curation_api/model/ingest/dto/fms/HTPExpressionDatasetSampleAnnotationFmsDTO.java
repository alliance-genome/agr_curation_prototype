package org.alliancegenome.curation_api.model.ingest.dto.fms;

import java.util.List;

import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HTPExpressionDatasetSampleAnnotationFmsDTO extends BaseDTO{
	private HTPIdFmsDTO sampleId;
	private String sampleTitle;
	private String sampleType;
	private BioSampleAgeDTO sampleAge;
	private List<WhereExpressedDTO> sampleLocations;
	private String abundance;
	private BioSampleGenomicInformationDTO genomicInformation;
	private String taxonId;
	private String sex;
	private String assayType;
	private String sequencingFormat;
	private List<String> assemblyVersions;
	private String notes;
	private List<HTPIdFmsDTO> datasetIds;
	private MicroarraySampleDetailsDTO microarraySampleDetails;
}
