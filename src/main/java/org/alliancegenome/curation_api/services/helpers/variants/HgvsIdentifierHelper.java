package org.alliancegenome.curation_api.services.helpers.variants;

import org.alliancegenome.curation_api.model.ingest.dto.fms.VariantFmsDTO;
import org.apache.commons.lang3.StringUtils;

public abstract class HgvsIdentifierHelper {

	public static String getHgvsIdentifier(VariantFmsDTO dto) {
		String start = "";
		if (dto.getStart() != null) {
			start = Integer.toString(dto.getStart());
		}
		
		String end = "";
		if (dto.getEnd() != null) {
			end = Integer.toString(dto.getEnd());
		}
		
		String varSeq = StringUtils.isBlank(dto.getGenomicVariantSequence()) ? "" : dto.getGenomicVariantSequence();
		String refSeq = StringUtils.isBlank(dto.getGenomicReferenceSequence()) ? "" : dto.getGenomicReferenceSequence();
		
		String chrAccession = "";
		if (StringUtils.isNotBlank(dto.getSequenceOfReferenceAccessionNumber())) {
			String[] accessionParts = dto.getSequenceOfReferenceAccessionNumber().split(":");
			if (accessionParts.length == 2) {
				chrAccession = accessionParts[1];
			} else {
				chrAccession = dto.getSequenceOfReferenceAccessionNumber();
			}
		}

		String hgvs = chrAccession + ":g." + start;
		switch(dto.getType()) {
			case "SO:1000002": // point mutation
				hgvs = hgvs + refSeq + ">" + varSeq;
				break;
			case "SO:1000008": // substitution
				hgvs = hgvs + refSeq + ">" + varSeq;
				break;
			case "SO:0000667": // insertion
				hgvs = hgvs + "_" + end + "ins" + varSeq;
				break;
			case "SO:0000159": // deletion
				hgvs = hgvs + "_" + end + "del";
				break;
			case "SO:0002007": // MNV
				hgvs = hgvs + "_" + end + "delins" + varSeq;
				break;
			case "SO:1000032": // delin
				hgvs = hgvs + "_" + end + "delins" + varSeq;
				break;
			default:
				hgvs = null;	
		}
		
		return hgvs;
	}

}
