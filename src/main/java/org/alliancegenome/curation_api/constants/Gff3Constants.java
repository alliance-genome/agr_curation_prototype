package org.alliancegenome.curation_api.constants;

import java.util.List;

public final class Gff3Constants {
	private Gff3Constants() {
		// Hidden from view, as it is a utility class
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}
	
	public static final List<String> TRANSCRIPT_TYPES = List.of(
		"mRNA", "ncRNA", "piRNA", "lincRNA", "miRNA", "pre_miRNA", "snoRNA", "lncRNA",
		"tRNA", "snRNA", "rRNA", "antisense_RNA", "C_gene_segment", "V_gene_segment",
		"pseudogene_attribute", "pseudogenic_transcript", "lnc_RNA", "nc_primary_transcript",
		"circular_ncRNA"
	);
	
	public static final List<String> STRANDS = List.of("+", "-");
}