package org.alliancegenome.curation_api.model.ingest.dto.fms;

import java.util.List;

import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VepTxtDTO extends BaseDTO {

	private String uploadedVariation;
	private String location;
	private String allele;
	private String gene;
	private String feature;
	private String featureType;
	private String consequence;
	private String cdnaPosition;
	private String cdsPosition;
	private String proteinPosition;
	private String aminoAcids;
	private String codons;
	private String existingVariation;
	private List<String> extra;
	
}
