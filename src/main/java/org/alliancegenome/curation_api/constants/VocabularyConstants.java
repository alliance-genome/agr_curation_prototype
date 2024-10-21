package org.alliancegenome.curation_api.constants;

public final class VocabularyConstants {
	private VocabularyConstants() {
		// Hidden from view, as it is a utility class
	}
	public static final String ANNOTATION_TYPE_VOCABULARY = "annotation_type";
	public static final String DISEASE_QUALIFIER_VOCABULARY = "disease_qualifier";
	public static final String DISEASE_GENETIC_MODIFIER_RELATION_VOCABULARY = "disease_genetic_modifier_relation";
	public static final String GENETIC_SEX_VOCABULARY = "genetic_sex";
	public static final String CONDITION_RELATION_TYPE_VOCABULARY = "condition_relation";
	public static final String DISEASE_RELATION_VOCABULARY = "disease_relation";
	public static final String ECO_TERM_ABBREVIATION_VOCABULARY = "agr_da_eco_term";

	public static final String GENE_DISEASE_RELATION_VOCABULARY_TERM_SET = "gene_disease_relation";
	public static final String ALLELE_DISEASE_RELATION_VOCABULARY_TERM_SET = "allele_disease_relation";
	public static final String AGM_DISEASE_RELATION_VOCABULARY_TERM_SET = "agm_disease_relation";

	public static final String PHENOTYPE_RELATION_VOCABULARY = "phenotype_relation";

	public static final String ALLELE_INHERITANCE_MODE_VOCABULARY = "allele_inheritance_mode";
	public static final String ALLELE_DATABASE_STATUS_VOCABULARY = "allele_db_status";
	public static final String ALLELE_FUNCTIONAL_IMPACT_VOCABULARY = "allele_functional_impact";
	public static final String ALLELE_COLLECTION_VOCABULARY = "allele_collection";
	public static final String GERMLINE_TRANSMISSION_STATUS_VOCABULARY = "allele_germline_transmission_status";
	public static final String ALLELE_NOMENCLATURE_EVENT_VOCABULARY = "allele_nomenclature_event";

	public static final String ALLELE_RELATION_VOCABULARY = "allele_relation";
	public static final String ALLELE_GENE_RELATION_VOCABULARY_TERM_SET = "allele_gene_relation";
	public static final String ALLELE_VARIANT_RELATION_VOCABULARY_TERM_SET = "allele_variant_relation";
	
	public static final String LOCATION_ASSOCIATION_RELATION_VOCABULARY = "location_association_relation";

	public static final String FULL_NAME_TYPE_TERM_SET = "full_name_type";
	public static final String SYSTEMATIC_NAME_TYPE_TERM_SET = "systematic_name_type";
	public static final String SYMBOL_NAME_TYPE_TERM_SET = "symbol_name_type";

	public static final String SYNONYM_SCOPE_VOCABULARY = "synonym_scope";
	public static final String NAME_TYPE_VOCABULARY = "name_type";

	public static final String AGM_SUBTYPE_VOCABULARY = "agm_subtype";

	public static final String ORTHOLOGY_BEST_SCORE_VOCABULARY = "ortho_best_score";
	public static final String ORTHOLOGY_BEST_REVERSE_SCORE_VOCABULARY_TERM_SET = "ortho_best_rev_score";

	public static final String HOMOLOGY_CONFIDENCE_VOCABULARY = "homology_confidence";
	public static final String HOMOLOGY_PREDICTION_METHOD_VOCABULARY = "homology_prediction_method";

	public static final String ORTHOLOGY_PREDICTION_METHOD_VOCABULARY_TERM_SET = "orthology_prediction_method";
	public static final String PARALOGY_PREDICTION_METHOD_VOCABULARY_TERM_SET = "paralogy_prediction_method";

	public static final String CONSTRUCT_RELATION_VOCABULARY = "construct_relation";
	public static final String CONSTRUCT_GENOMIC_ENTITY_RELATION_VOCABULARY_TERM_SET = "construct_genomic_entity_relation";

	public static final String NOTE_TYPE_VOCABULARY = "note_type";
	public static final String ALLELE_NOTE_TYPES_VOCABULARY_TERM_SET = "allele_note_type";
	public static final String DISEASE_ANNOTATION_NOTE_TYPES_VOCABULARY_TERM_SET = "da_note_type";
	public static final String GENE_NOTE_TYPES_VOCABULARY_TERM_SET = "gene_note_type";
	public static final String CONSTRUCT_COMPONENT_NOTE_TYPES_VOCABULARY_TERM_SET = "construct_component_note_type";
	public static final String VARIANT_NOTE_TYPES_VOCABULARY_TERM_SET = "variant_note_type";
	public static final String ALLELE_GENOMIC_ENTITY_ASSOCIATION_NOTE_TYPES_VOCABULARY_TERM_SET = "allele_genomic_entity_association_note_type";

	public static final String VARIANT_STATUS_VOCABULARY = "variant_status";

	public static final String ALLELE_OF_VOCABULARY_TERM = "is_allele_of";

	public static final String GENE_INTERACTION_RELATION_VOCABULARY = "gene_interaction_relation";
	public static final String GENE_MOLECULAR_INTERACTION_RELATION_VOCABULARY_TERM_SET = "gene_molecular_interaction_relation";
	public static final String GENE_GENETIC_INTERACTION_RELATION_VOCABULARY_TERM_SET = "gene_genetic_interaction_relation";
	public static final String GENE_GENETIC_INTERACTION_RELATION_TERM = "genetically_interacts_with";
	public static final String GENE_MOLECULAR_INTERACTION_RELATION_TERM = "physically_interacts_with";

	public static final String GENE_EXPRESSION_VOCABULARY = "gene_expression";
	public static final String GENE_EXPRESSION_RELATION_TERM = "is_expressed_in";

	public static final String TRANSCRIPT_RELATION_VOCABULARY = "transcript_relation";
	public static final String TRANSCRIPT_CHILD_TERM = "is_child_of";
	public static final String TRANSCRIPT_PARENT_TERM = "is_parent_of";

	public static final String STAGE_UBERON_SLIM_TERMS = "stage_uberon_slim_terms";
	public static final String ANATOMICAL_STRUCTURE_UBERON_SLIM_TERMS = "anatomical_structure_uberon_slim_terms";
	public static final String SPATIAL_EXPRESSION_QUALIFIERS = "spatial_expression_qualifiers";
	public static final String ANATOMICAL_STRUCTURE_QUALIFIER = "anatomical_structure_qualifier";
	public static final String ANATOMICAL_SUBSTRUCTURE_QUALIFIER = "anatomical_subtructure_qualifier";
	public static final String CELLULAR_COMPONENT_QUALIFIER = "cellular_component_qualifier";

	public static final String HTP_DATASET_CATEGORY_TAGS = "data_set_category_tags";
	public static final String HTP_DATASET_NOTE_TYPE_VOCABULARY_TERM_SET = "htp_expression_dataset_note_type";

}
