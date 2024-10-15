package org.alliancegenome.curation_api.model.ingest.dto.fms;

import java.util.List;

public class HTPExpressionDatasetSampleAnnotationFmsDTO {
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
