package org.alliancegenome.curation_api.model.ingest.dto.fms;

import java.util.List;

import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VariantFmsDTO extends BaseDTO {
	private String alleleId;
	private String assembly;
	private String chromosome;
	private Integer start;
	private Integer end;
	private String sequenceOfReferenceAccessionNumber;
	private String genomicReferenceSequence;
	private String genomicVariantSequence;
	private String type;
	private String consequence;
	private List<VariantNoteFmsDTO> notes;
	private List<PublicationRefFmsDTO> references;
	private List<CrossReferenceFmsDTO> crossReferences;
}
