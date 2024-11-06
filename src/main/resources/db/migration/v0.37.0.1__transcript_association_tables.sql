CREATE TABLE transcriptcodingsequenceassociation (
	id bigint CONSTRAINT transcriptcodingsequenceassociation_pkey PRIMARY KEY,
	relation_id bigint,
	transcriptassociationsubject_id bigint,
	transcriptcodingsequenceassociationobject_id bigint
);

ALTER TABLE transcriptcodingsequenceassociation ADD CONSTRAINT transcriptcodingsequenceassociation_id_fk
	FOREIGN KEY (id) REFERENCES evidenceassociation(id);
ALTER TABLE transcriptcodingsequenceassociation ADD CONSTRAINT transcriptcodingsequenceassociation_relation_id_fk
	FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
ALTER TABLE transcriptcodingsequenceassociation ADD CONSTRAINT transcriptcodingsequenceassociation_tasubject_id_fk
	FOREIGN KEY (transcriptassociationsubject_id) REFERENCES transcript(id);
ALTER TABLE transcriptcodingsequenceassociation ADD CONSTRAINT transcriptcodingsequenceassociation_tcdsaobject_id_fk
	FOREIGN KEY (transcriptcodingsequenceassociationobject_id) REFERENCES codingsequence(id);
	
CREATE INDEX transcriptcdsassociation_relation_index ON transcriptcodingsequenceassociation
	USING btree (relation_id);
CREATE INDEX transcriptcdsassociation_subject_index ON transcriptcodingsequenceassociation
	USING btree (transcriptassociationsubject_id);
CREATE INDEX transcriptcdsassociation_object_index ON transcriptcodingsequenceassociation
	USING btree (transcriptcodingsequenceassociationobject_id);

CREATE TABLE transcriptexonassociation (
	id bigint CONSTRAINT transcriptexonassociation_pkey PRIMARY KEY,
	relation_id bigint,
	transcriptassociationsubject_id bigint,
	transcriptexonassociationobject_id bigint
);

ALTER TABLE transcriptexonassociation ADD CONSTRAINT transcriptexonassociation_id_fk
	FOREIGN KEY (id) REFERENCES evidenceassociation(id);
ALTER TABLE transcriptexonassociation ADD CONSTRAINT transcriptexonassociation_relation_id_fk
	FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
ALTER TABLE transcriptexonassociation ADD CONSTRAINT transcriptexonassociation_tasubject_id_fk
	FOREIGN KEY (transcriptassociationsubject_id) REFERENCES transcript(id);
ALTER TABLE transcriptexonassociation ADD CONSTRAINT transcriptexonassociation_teaobject_id_fk
	FOREIGN KEY (transcriptexonassociationobject_id) REFERENCES exon(id);
	
CREATE INDEX transcriptexonassociation_relation_index ON transcriptexonassociation
	USING btree (relation_id);
CREATE INDEX transcriptcodingsequenceassociation_subject_index ON transcriptexonassociation
	USING btree (transcriptassociationsubject_id);
CREATE INDEX transcriptexonassociation_object_index ON transcriptexonassociation
	USING btree (transcriptexonassociationobject_id);

CREATE TABLE transcriptgeneassociation (
	id bigint CONSTRAINT transcriptgeneassociation_pkey PRIMARY KEY,
	relation_id bigint,
	transcriptassociationsubject_id bigint,
	transcriptgeneassociationobject_id bigint
);

ALTER TABLE transcriptgeneassociation ADD CONSTRAINT transcriptgeneassociation_id_fk
	FOREIGN KEY (id) REFERENCES evidenceassociation(id);
ALTER TABLE transcriptgeneassociation ADD CONSTRAINT transcriptgeneassociation_relation_id_fk
	FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
ALTER TABLE transcriptgeneassociation ADD CONSTRAINT transcriptgeneassociation_tasubject_id_fk
	FOREIGN KEY (transcriptassociationsubject_id) REFERENCES transcript(id);
ALTER TABLE transcriptgeneassociation ADD CONSTRAINT transcriptgeneassociation_tgaobject_id_fk
	FOREIGN KEY (transcriptgeneassociationobject_id) REFERENCES gene(id);
	
CREATE INDEX transcriptgeneassociation_relation_index ON transcriptgeneassociation
	USING btree (relation_id);
CREATE INDEX transcriptgeneassociation_subject_index ON transcriptgeneassociation
	USING btree (transcriptassociationsubject_id);
CREATE INDEX transcriptgeneassociation_object_index ON transcriptgeneassociation
	USING btree (transcriptgeneassociationobject_id);

INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('vocabulary_seq'), 'Transcript Relation', 'transcript_relation');

INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'is_parent_of', id FROM vocabulary WHERE vocabularylabel = 'transcript_relation';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'is_child_of', id FROM vocabulary WHERE vocabularylabel = 'transcript_relation';

INSERT INTO vocabularytermset (id, name, vocabularylabel, vocabularytermsetvocabulary_id)
	SELECT nextval('vocabularytermset_seq'), 'Transcript CodingSequence Association Relation', 'transcript_coding_sequence_relation', id FROM vocabulary
	WHERE vocabularylabel = 'transcript_relation';
INSERT INTO vocabularytermset (id, name, vocabularylabel, vocabularytermsetvocabulary_id)
	SELECT nextval('vocabularytermset_seq'), 'Transcript Exon Association Relation', 'transcript_exon_relation', id FROM vocabulary
	WHERE vocabularylabel = 'transcript_relation';
INSERT INTO vocabularytermset (id, name, vocabularylabel, vocabularytermsetvocabulary_id)
	SELECT nextval('vocabularytermset_seq'), 'Transcript Gene Association Relation', 'transcript_gene_relation', id FROM vocabulary
	WHERE vocabularylabel = 'transcript_relation';

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
	WITH t1 AS (
		SELECT id FROM vocabularytermset WHERE vocabularylabel = 'transcript_coding_sequence_relation'
	),
	t2 AS (
		SELECT id FROM vocabularyterm WHERE name = 'is_parent_of' AND vocabulary_id = (
			SELECT id FROM vocabulary WHERE vocabularylabel = 'transcript_relation'
		)
	)
	SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
	WITH t1 AS (
		SELECT id FROM vocabularytermset WHERE vocabularylabel = 'transcript_exon_relation'
	),
	t2 AS (
		SELECT id FROM vocabularyterm WHERE name = 'is_parent_of' AND vocabulary_id = (
			SELECT id FROM vocabulary WHERE vocabularylabel = 'transcript_relation'
		)
	)
	SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
	WITH t1 AS (
		SELECT id FROM vocabularytermset WHERE vocabularylabel = 'transcript_gene_relation'
	),
	t2 AS (
		SELECT id FROM vocabularyterm WHERE name = 'is_child_of' AND vocabulary_id = (
			SELECT id FROM vocabulary WHERE vocabularylabel = 'transcript_relation'
		)
	)
	SELECT t1.id, t2.id FROM t1, t2;
	