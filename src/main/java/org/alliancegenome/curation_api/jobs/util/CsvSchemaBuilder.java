package org.alliancegenome.curation_api.jobs.util;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class CsvSchemaBuilder {
	private CsvSchemaBuilder() {
		// Hidden from view, as it is a utility class
	}
	public static CsvSchema psiMiTabSchema() {
		CsvSchema schema = CsvSchema.builder()
				.setColumnSeparator('\t')
				.setArrayElementSeparator("|")
				.setAllowComments(true)
				.setNullValue("-")
				.disableQuoteChar()
				.addColumn("interactorAIdentifier")
				.addColumn("interactorBIdentifier")
				.addColumn("interactorAAlternativeId")
				.addColumn("interactorBAlternativeId")
				.addColumn("interactorAAliases")
				.addColumn("interactorBAliases")
				.addColumn("interactionDetectionMethods")
				.addColumn("authors")
				.addColumn("publicationIds")
				.addColumn("interactorATaxonId")
				.addColumn("interactorBTaxonId")
				.addColumn("interactionTypes")
				.addColumn("sourceDatabaseIds")
				.addColumn("interactionIds")
				.addColumn("confidenceScore")
				.addColumn("complexExpansion")
				.addColumn("biologicalRoleA")
				.addColumn("biologicalRoleB")
				.addColumn("experimentalRoleA")
				.addColumn("experimentalRoleB")
				.addColumn("interactorAType")
				.addColumn("interactorBType")
				.addColumn("interactorAXrefs")
				.addColumn("interactorBXrefs")
				.addColumn("interactionXrefs")
				.addColumn("interactorAAnnotationString")
				.addColumn("interactorBAnnotationString")
				.addColumn("interactionAnnotations")
				.addColumn("hostOrganismTaxonId")
				.addColumn("interactionParameters")
				.addColumn("creationDate")
				.addColumn("updateDate")
				.addColumn("interactorAChecksum")
				.addColumn("interactorBChecksum")
				.addColumn("interactionChecksum")
				.addColumn("negativeInteraction")
				.addColumn("interactorAFeatures")
				.addColumn("interactorBFeatures")
				.addColumn("interactorAStoichiometry")
				.addColumn("interactorBStoichiometry")
				.addColumn("interactorAParticpantIdentificationMethod")
				.addColumn("interactorBParticpantIdentificationMethod")
				.build();
		
		return schema;
	}

	public static CsvSchema biogridOrcFmsSchema() {
		CsvSchema schema = CsvSchema.builder()
				.setColumnSeparator('\t')
				.setArrayElementSeparator("|")
				.setAllowComments(true)
				.setNullValue("-")
				.disableQuoteChar()
				.addColumn("screenId")
				.addColumn("identifierId")
				.addColumn("identifierType")
				.addColumn("officialSymbol")
				.addColumn("aliases")
				.addColumn("organismId")
				.addColumn("organismOfficial")
				.addColumn("score1")
				.addColumn("score2")
				.addColumn("score3")
				.addColumn("score4")
				.addColumn("score5")
				.addColumn("hit")
				.addColumn("source")
				.build();
		
		return schema;
	}
	
	public static CsvSchema gff3Schema() {
		CsvSchema schema = CsvSchema.builder()
				.setColumnSeparator('\t')
				.setArrayElementSeparator(";")
				.setNullValue(".")
				.disableQuoteChar()
				.addColumn("seqId")
				.addColumn("source")
				.addColumn("type")
				.addColumn("start")
				.addColumn("end")
				.addColumn("score")
				.addColumn("strand")
				.addColumn("phase")
				.addColumn("attributes")
				.build();
		
		return schema;
	}
}
