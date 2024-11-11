package org.alliancegenome.curation_api.constants;

public final class EntityFieldConstants {

	private EntityFieldConstants() {
		// Hidden from view, as it is a utility class
	}

	public static final String ASSEMBLY = "genomeAssembly.modEntityId";
	public static final String TAXON = "taxon.curie";
	public static final String SOURCE_ORGANIZATION = "sourceOrganization.abbreviation";
	public static final String DATA_PROVIDER = "dataProvider." + SOURCE_ORGANIZATION;
	public static final String SECONDARY_DATA_PROVIDER = "secondaryDataProvider." + SOURCE_ORGANIZATION;
	
	public static final String DA_SUBJECT = "diseaseAnnotationSubject";
	public static final String EA_SUBJECT = "expressionAnnotationSubject";
	public static final String PA_SUBJECT = "phenotypeAnnotationSubject";
	public static final String ALLELE_ASSOCIATION_SUBJECT = "alleleAssociationSubject";
	public static final String CODING_SEQUENCE_ASSOCIATION_SUBJECT = "codingSequenceAssociationSubject";
	public static final String CONSTRUCT_ASSOCIATION_SUBJECT = "constructAssociationSubject";
	public static final String EXON_ASSOCIATION_SUBJECT = "exonAssociationSubject";
	public static final String GENE_ASSOCIATION_SUBJECT = "geneAssociationSubject";
	public static final String VARIANT_ASSOCIATION_SUBJECT = "variantAssociationSubject";
	public static final String SQTR_ASSOCIATION_SUBJECT = "sequenceTargetingReagentAssociationSubject";
	public static final String TRANSCRIPT_ASSOCIATION_SUBJECT = "transcriptAssociationSubject";
	
	public static final String DA_SUBJECT_TAXON = DA_SUBJECT + "." + TAXON;
	public static final String EA_SUBJECT_TAXON = EA_SUBJECT + "." + TAXON;
	public static final String PA_SUBJECT_TAXON = PA_SUBJECT + "." + TAXON;
	
	public static final String ALLELE_ASSOCIATION_SUBJECT_DATA_PROVIDER = ALLELE_ASSOCIATION_SUBJECT + "." + DATA_PROVIDER;
	public static final String CODING_SEQUENCE_ASSOCIATION_SUBJECT_DATA_PROVIDER = CODING_SEQUENCE_ASSOCIATION_SUBJECT + "." + DATA_PROVIDER;
	public static final String CONSTRUCT_ASSOCIATION_SUBJECT_DATA_PROVIDER = CONSTRUCT_ASSOCIATION_SUBJECT + "." + DATA_PROVIDER;
	public static final String EXON_ASSOCIATION_SUBJECT_DATA_PROVIDER = EXON_ASSOCIATION_SUBJECT + "." + DATA_PROVIDER;
	public static final String GENE_ASSOCIATION_SUBJECT_DATA_PROVIDER = GENE_ASSOCIATION_SUBJECT + "." + DATA_PROVIDER;
	public static final String VARIANT_ASSOCIATION_SUBJECT_DATA_PROVIDER = VARIANT_ASSOCIATION_SUBJECT + "." + DATA_PROVIDER;
	public static final String SQTR_ASSOCIATION_SUBJECT_DATA_PROVIDER = SQTR_ASSOCIATION_SUBJECT + "." + DATA_PROVIDER;
	public static final String TRANSCRIPT_ASSOCIATION_SUBJECT_DATA_PROVIDER = TRANSCRIPT_ASSOCIATION_SUBJECT + "." + DATA_PROVIDER;
	
	public static final String CODING_SEQUENCE_ASSOCIATION_SUBJECT_TAXON = CODING_SEQUENCE_ASSOCIATION_SUBJECT + "." + TAXON;
	public static final String EXON_ASSOCIATION_SUBJECT_TAXON = EXON_ASSOCIATION_SUBJECT + "." + TAXON;
	public static final String GENE_ASSOCIATION_SUBJECT_TAXON = GENE_ASSOCIATION_SUBJECT + "." + TAXON;
	public static final String TRANSCRIPT_ASSOCIATION_SUBJECT_TAXON = TRANSCRIPT_ASSOCIATION_SUBJECT + "." + TAXON;
	public static final String VARIANT_ASSOCIATION_SUBJECT_TAXON = VARIANT_ASSOCIATION_SUBJECT + "." + TAXON;
	
	public static final String GENOMIC_LOCATION_ASSOCIATION_OBJECT = "GenomicLocationAssociationObject";
	public static final String CODING_SEQUENCE_GENOMIC_LOCATION_ASSOCIATION_OBJECT = "codingSequence" + GENOMIC_LOCATION_ASSOCIATION_OBJECT;
	public static final String EXON_GENOMIC_LOCATION_ASSOCIATION_OBJECT = "exon" + GENOMIC_LOCATION_ASSOCIATION_OBJECT;
	public static final String GENE_GENOMIC_LOCATION_ASSOCIATION_OBJECT = "gene" + GENOMIC_LOCATION_ASSOCIATION_OBJECT;
	public static final String TRANSCRIPT_GENOMIC_LOCATION_ASSOCIATION_OBJECT = "transcript" + GENOMIC_LOCATION_ASSOCIATION_OBJECT;
	public static final String VARIANT_GENOMIC_LOCATION_ASSOCIATION_OBJECT = "variant" + GENOMIC_LOCATION_ASSOCIATION_OBJECT;
	public static final String CODING_SEQUENCE_GENOMIC_LOCATION_ASSOCIATION_OBJECT_ASSEMBLY = "codingSequence" + GENOMIC_LOCATION_ASSOCIATION_OBJECT + "." + ASSEMBLY;
	public static final String EXON_GENOMIC_LOCATION_ASSOCIATION_OBJECT_ASSEMBLY = "exon" + GENOMIC_LOCATION_ASSOCIATION_OBJECT + "." + ASSEMBLY;
	public static final String TRANSCRIPT_GENOMIC_LOCATION_ASSOCIATION_OBJECT_ASSEMBLY = "transcript" + GENOMIC_LOCATION_ASSOCIATION_OBJECT + "." + ASSEMBLY;
	public static final String GENE_GENOMIC_LOCATION_ASSOCIATION_OBJECT_ASSEMBLY = "gene" + GENOMIC_LOCATION_ASSOCIATION_OBJECT + "." + ASSEMBLY;
	
	public static final String SUBJECT_GENE_DATA_PROVIDER = "subjectGene." + DATA_PROVIDER;
	public static final String SUBJECT_GENE_TAXON = "subjectGene." + TAXON;
}
