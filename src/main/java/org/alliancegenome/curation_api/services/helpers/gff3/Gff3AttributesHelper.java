package org.alliancegenome.curation_api.services.helpers.gff3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.Gff3Constants;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class Gff3AttributesHelper {

	public static Map<String, String> getAttributes(Gff3DTO dto, BackendBulkDataProvider dataProvider) {
		Map<String, String> attributes = new HashMap<String, String>();
		if (CollectionUtils.isNotEmpty(dto.getAttributes())) {
			for (String keyValue : dto.getAttributes()) {
				String[] parts = keyValue.split("=");
				if (parts.length == 2) {
					attributes.put(parts[0], parts[1]);
				}
			}
		}

		// Ensure identifiers have MOD prefix
		for (String key : List.of("ID", "Parent")) {
			if (attributes.containsKey(key)) {
				String idsString = attributes.get(key);
				if (StringUtils.equals(dataProvider.sourceOrganization, "WB")) {
					// Remove prefixes like Gene: and Transcript: from WB identifiers
					idsString = idsString.replaceAll("Gene:", "");
					idsString = idsString.replaceAll("Transcript:", "");
					idsString = idsString.replaceAll("CDS:", "");
					idsString = idsString.replaceAll("Pseudogene:", "");
				}
				String[] idsList = idsString.split(",");
				List<String> processedIdList = new ArrayList<>();
				for (String id : idsList) {
					String[] idParts = id.split(":");
					if (idParts.length == 1) {
						id = dataProvider.name() + ':' + idParts[0];
					}
					processedIdList.add(id);
				}
				attributes.put(key, String.join(",", processedIdList));
			}
		}
		
		return attributes;
	}
	
	public static List<ImmutablePair<Gff3DTO, Map<String, String>>> getExonGffData(List<Gff3DTO> gffData, BackendBulkDataProvider dataProvider) {
		List<ImmutablePair<Gff3DTO, Map<String, String>>> retGffData = new ArrayList<>();
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.startProcess("GFF Exon pre-processing for " + dataProvider.name(), gffData.size());
		for (Gff3DTO originalGffEntry : gffData) {
			if (StringUtils.equals(originalGffEntry.getType(), "exon") || StringUtils.equals(originalGffEntry.getType(), "noncoding_exon")) {
				processGffEntry(originalGffEntry, retGffData, dataProvider);
			}
			ph.progressProcess();
		}
		ph.finishProcess();
		return retGffData;
	}
	
	public static List<ImmutablePair<Gff3DTO, Map<String, String>>> getCDSGffData(List<Gff3DTO> gffData, BackendBulkDataProvider dataProvider) {
		List<ImmutablePair<Gff3DTO, Map<String, String>>> retGffData = new ArrayList<>();
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.startProcess("GFF CDS pre-processing for " + dataProvider.name(), gffData.size());
		for (Gff3DTO originalGffEntry : gffData) {
			if (StringUtils.equals(originalGffEntry.getType(), "CDS")) {
				processGffEntry(originalGffEntry, retGffData, dataProvider);
			}
			ph.progressProcess();
		}
		ph.finishProcess();
		return retGffData;
	}
	
	
	public static List<ImmutablePair<Gff3DTO, Map<String, String>>> getTranscriptGffData(List<Gff3DTO> gffData, BackendBulkDataProvider dataProvider) {
		List<ImmutablePair<Gff3DTO, Map<String, String>>> retGffData = new ArrayList<>();
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.startProcess("GFF Transcript pre-processing for " + dataProvider.name(), gffData.size());
		for (Gff3DTO originalGffEntry : gffData) {
			if (StringUtils.equals(originalGffEntry.getType(), "lnc_RNA")) {
				originalGffEntry.setType("lncRNA");
			}
			if (Gff3Constants.TRANSCRIPT_TYPES.contains(originalGffEntry.getType())) {
				processGffEntry(originalGffEntry, retGffData, dataProvider);
			}
			ph.progressProcess();
		}
		ph.finishProcess();
		return retGffData;
	}

	private static void processGffEntry(Gff3DTO originalGffEntry, List<ImmutablePair<Gff3DTO, Map<String, String>>> retGffData, BackendBulkDataProvider dataProvider) {
		Map<String, String> attributes = getAttributes(originalGffEntry, dataProvider);
		if (attributes.containsKey("Parent")) {
			if (attributes.get("Parent").indexOf(",") > -1) {
				for (String parent : attributes.get("Parent").split(",")) {
					if (!parent.endsWith("_transposable_element")) {
						HashMap<String, String> attributesCopy = new HashMap<>();
						attributesCopy.putAll(attributes);
						String[] parentIdParts = parent.split(":");
						if (parentIdParts.length == 1) {
							parent = dataProvider.name() + ':' + parentIdParts[0];
						}
						attributesCopy.put("Parent", parent);
						retGffData.add(new ImmutablePair<>(originalGffEntry, attributesCopy));
					}
				}
			} else {
				if (!attributes.get("Parent").endsWith("_transposable_element")) {
					retGffData.add(new ImmutablePair<>(originalGffEntry, attributes));
				}
			}
		} else {
			retGffData.add(new ImmutablePair<>(originalGffEntry, attributes));
		}
	}

}
