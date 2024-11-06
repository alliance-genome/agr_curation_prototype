-- Create temporary tables

CREATE TABLE ontology_ids_to_delete (
	id bigint PRIMARY KEY
);

CREATE TABLE other_ids_to_delete (
	id bigint PRIMARY KEY
);

INSERT INTO ontology_ids_to_delete (id) SELECT id FROM ontologyterm
	WHERE curie = 'DOID:0080025' OR curie = 'DOID:0080004' OR curie = 'DOID:0080003'
	OR curie = 'DOID:0080035' OR curie = 'DOID:0080002';

-- Clean up disease annotations

INSERT INTO other_ids_to_delete (id) SELECT id FROM diseaseannotation WHERE diseaseannotationobject_id IN (
	SELECT id FROM ontology_ids_to_delete
);

DELETE FROM agmdiseaseannotation_gene WHERE agmdiseaseannotation_id IN (SELECT id FROM other_ids_to_delete);

DELETE FROM agmdiseaseannotation WHERE id IN (SELECT id FROM other_ids_to_delete);

DELETE FROM allelediseaseannotation_gene WHERE allelediseaseannotation_id IN (SELECT id FROM other_ids_to_delete);

DELETE FROM allelediseaseannotation WHERE id IN (SELECT id FROM other_ids_to_delete);

DELETE FROM genediseaseannotation WHERE id IN (SELECT id FROM other_ids_to_delete);

DELETE FROM genediseaseannotation WHERE id IN (SELECT id FROM other_ids_to_delete);

DELETE FROM genediseaseannotation WHERE id IN (SELECT id FROM other_ids_to_delete);

DELETE FROM diseaseannotation_biologicalentity WHERE diseaseannotation_id IN (SELECT id FROM other_ids_to_delete);

DELETE FROM diseaseannotation_gene WHERE diseaseannotation_id IN (SELECT id FROM other_ids_to_delete);

DELETE FROM diseaseannotation_ontologyterm WHERE diseaseannotation_id IN (SELECT id FROM other_ids_to_delete);

DELETE FROM diseaseannotation_vocabularyterm WHERE diseaseannotation_id IN (SELECT id FROM other_ids_to_delete);

DELETE FROM diseaseannotation WHERE id IN (SELECT id FROM other_ids_to_delete);

DELETE FROM annotation_conditionrelation WHERE annotation_id IN (SELECT id FROM other_ids_to_delete);

CREATE TABLE note_ids_to_delete (
	id bigint PRIMARY KEY
);

INSERT INTO note_ids_to_delete  (id) SELECT relatednotes_id FROM annotation_note WHERE annotation_id IN (
	SELECT id FROM other_ids_to_delete
);

DELETE FROM annotation_note WHERE annotation_id IN (SELECT id FROM other_ids_to_delete);

DELETE FROM note WHERE id IN (SELECT id FROM note_ids_to_delete);

DROP TABLE note_ids_to_delete;

DELETE FROM annotation WHERE id IN (SELECT id FROM other_ids_to_delete);

DELETE FROM singlereferenceassociation WHERE id IN (SELECT id FROM other_ids_to_delete);

DELETE FROM association WHERE id IN (SELECT id FROM other_ids_to_delete);

DELETE FROM other_ids_to_delete;

-- Clean up xrefs

INSERT INTO other_ids_to_delete (id) SELECT crossreferences_id FROM ontologyterm_crossreference WHERE ontologyterm_id IN (
	SELECT id FROM ontology_ids_to_delete
);

DELETE FROM ontologyterm_crossreference WHERE crossreferences_id IN (SELECT id FROM other_ids_to_delete);

DELETE FROM crossreference WHERE id IN (SELECT id FROM other_ids_to_delete);

DELETE FROM other_ids_to_delete;

-- Clean up definition URLs

DELETE FROM ontologyterm_definitionurls WHERE ontologyterm_id IN (SELECT id FROM ontology_ids_to_delete);

-- Clean up ancestor/descendants

DELETE FROM ontologyterm_isa_ancestor_descendant WHERE  isaancestors_id IN (SELECT id FROM ontology_ids_to_delete);

DELETE FROM ontologyterm_isa_ancestor_descendant WHERE  isadescendants_id IN (SELECT id FROM ontology_ids_to_delete);

-- Clean up parents/children

DELETE FROM ontologyterm_isa_parent_children WHERE  isachildren_id IN (SELECT id FROM ontology_ids_to_delete);

DELETE FROM ontologyterm_isa_parent_children WHERE  isaparents_id IN (SELECT id FROM ontology_ids_to_delete);

-- Clean up secondary identifiers

DELETE FROM ontologyterm_secondaryidentifiers where ontologyterm_id IN (SELECT id FROM ontology_ids_to_delete);

-- Clean up subsets

DELETE FROM ontologyterm_subsets where ontologyterm_id IN (SELECT id FROM ontology_ids_to_delete);

-- Clean up synonyms

INSERT INTO other_ids_to_delete (id) SELECT synonyms_id FROM ontologyterm_synonym WHERE ontologyterm_id IN (
	SELECT id FROM ontology_ids_to_delete
);

DELETE FROM ontologyterm_synonym WHERE synonyms_id IN (SELECT id FROM other_ids_to_delete);

DELETE FROM synonym WHERE id IN (SELECT id FROM other_ids_to_delete);

DELETE FROM other_ids_to_delete;

-- Clean up ontology terms

DELETE FROM ontologyterm WHERE id IN (SELECT id FROM ontology_ids_to_delete);

-- Remove temporary tables

DROP TABLE ontology_ids_to_delete;

DROP TABLE other_ids_to_delete;