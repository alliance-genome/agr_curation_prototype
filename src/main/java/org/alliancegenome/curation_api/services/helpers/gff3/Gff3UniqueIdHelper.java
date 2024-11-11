package org.alliancegenome.curation_api.services.helpers.gff3;

import java.util.Map;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.alliancegenome.curation_api.services.helpers.UniqueIdGeneratorHelper;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class Gff3UniqueIdHelper {

	public static String getExonOrCodingSequenceUniqueId(Gff3DTO dto, Map<String, String> attributes, BackendBulkDataProvider dataProvider) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		
		if (attributes.containsKey("curie")) {
			uniqueId.add(attributes.get("curie"));
		} else if (attributes.containsKey("ID")) {
			uniqueId.add(attributes.get("ID"));
		} else if (attributes.containsKey("Name")) {
			uniqueId.add(attributes.get("Name"));
		} else if (attributes.containsKey("exon_id")) {
			uniqueId.add(attributes.get("exon_id"));
		}
		
		if (attributes.containsKey("Parent")) {
			uniqueId.add(attributes.get("Parent"));
		}
		
		uniqueId.add(dto.getSeqId());
		uniqueId.add(dto.getStart());
		uniqueId.add(dto.getEnd());
		uniqueId.add(dto.getStrand());
		
		return uniqueId.getUniqueId();
	}

}
