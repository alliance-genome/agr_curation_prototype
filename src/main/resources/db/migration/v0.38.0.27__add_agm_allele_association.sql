CREATE TABLE public.agmalleleassociation (
	id bigint NOT NULL,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	zygosity_id bigint,
	createdby_id bigint,
	updatedby_id bigint,
	agmalleleassociationobject_id bigint,
	agmassociationsubject_id bigint,
	relation_id bigint
);

CREATE SEQUENCE public.agmalleleassociation_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

ALTER TABLE ONLY public.agmalleleassociation ADD CONSTRAINT agmalleleassociation_pkey PRIMARY KEY (id);

CREATE INDEX agmalleleassociation_agmalleleassociationobject_index ON public.agmalleleassociation USING btree (agmalleleassociationobject_id);
CREATE INDEX agmalleleassociation_agmassociationsubject_index ON public.agmalleleassociation USING btree (agmassociationsubject_id);
CREATE INDEX agmalleleassociation_createdby_index ON public.agmalleleassociation USING btree (createdby_id);
CREATE INDEX agmalleleassociation_internal_index ON public.agmalleleassociation USING btree (internal);
CREATE INDEX agmalleleassociation_obsolete_index ON public.agmalleleassociation USING btree (obsolete);
CREATE INDEX agmalleleassociation_relation_index ON public.agmalleleassociation USING btree (relation_id);
CREATE INDEX agmalleleassociation_zygosity_index ON public.agmalleleassociation USING btree (zygosity_id);
CREATE INDEX agmalleleassociation_updatedby_index ON public.agmalleleassociation USING btree (updatedby_id);

ALTER TABLE ONLY public.agmalleleassociation ADD CONSTRAINT agmalleleassociation_agmalleleassociationobject_id FOREIGN KEY (agmalleleassociationobject_id) REFERENCES public.allele(id);
ALTER TABLE ONLY public.agmalleleassociation ADD CONSTRAINT agmalleleassociation_agmassociationsubject_id FOREIGN KEY (agmassociationsubject_id) REFERENCES public.affectedgenomicmodel(id);
ALTER TABLE ONLY public.agmalleleassociation ADD CONSTRAINT agmalleleassociation_createdby_id FOREIGN KEY (createdby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.agmalleleassociation ADD CONSTRAINT agmalleleassociation_updatedby_id FOREIGN KEY (updatedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.agmalleleassociation ADD CONSTRAINT agmalleleassociation_relation_id FOREIGN KEY (relation_id) REFERENCES public.vocabularyterm(id);
ALTER TABLE ONLY public.agmalleleassociation ADD CONSTRAINT agmalleleassociation_zygosity_id FOREIGN KEY (zygosity_id) REFERENCES public.vocabularyterm(id);


INSERT INTO vocabularytermset(id, name, vocabularylabel, vocabularytermsetvocabulary_id) SELECT nextval('vocabularytermset_seq'), 'AGM Allele Association Relation', 'agm_allele_relation', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'agm_relation';

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE name = 'AGM Allele Association Relation'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'contains' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'agm_relation'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;


INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('vocabulary_seq'), 'Geno Terms', 'geno_terms');

INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'GENO:0000602', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'geno_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'GENO:0000603', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'geno_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'GENO:0000604', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'geno_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'GENO:0000605', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'geno_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'GENO:0000606', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'geno_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'GENO:0000135', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'geno_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'GENO:0000136', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'geno_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'GENO:0000137', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'geno_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'GENO:0000134', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'geno_terms';

INSERT INTO vocabularytermset(id, name, vocabularylabel, vocabularytermsetvocabulary_id) SELECT nextval('vocabularytermset_seq'), 'AGM Allele Association GENO Terms', 'agm_allele_association_geno_terms', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'geno_terms';

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE name = 'AGM Allele Association GENO Terms'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'GENO:0000602' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'geno_terms'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
WITH
t1 AS (
	SELECT id FROM vocabularytermset WHERE name = 'AGM Allele Association GENO Terms'
),
t2 AS (
	SELECT id FROM vocabularyterm WHERE name = 'GENO:0000603' AND vocabulary_id = (
		SELECT id FROM vocabulary WHERE vocabularylabel = 'geno_terms'
	)
)
SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE name = 'AGM Allele Association GENO Terms'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'GENO:0000604' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'geno_terms'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
WITH
t1 AS (
	SELECT id FROM vocabularytermset WHERE name = 'AGM Allele Association GENO Terms'
),
t2 AS (
	SELECT id FROM vocabularyterm WHERE name = 'GENO:0000605' AND vocabulary_id = (
		SELECT id FROM vocabulary WHERE vocabularylabel = 'geno_terms'
	)
)
SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
WITH
t1 AS (
	SELECT id FROM vocabularytermset WHERE name = 'AGM Allele Association GENO Terms'
),
t2 AS (
	SELECT id FROM vocabularyterm WHERE name = 'GENO:0000606' AND vocabulary_id = (
		SELECT id FROM vocabulary WHERE vocabularylabel = 'geno_terms'
	)
)
SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
WITH
t1 AS (
	SELECT id FROM vocabularytermset WHERE name = 'AGM Allele Association GENO Terms'
),
t2 AS (
	SELECT id FROM vocabularyterm WHERE name = 'GENO:0000135' AND vocabulary_id = (
		SELECT id FROM vocabulary WHERE vocabularylabel = 'geno_terms'
	)
)
SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
WITH
t1 AS (
	SELECT id FROM vocabularytermset WHERE name = 'AGM Allele Association GENO Terms'
),
t2 AS (
	SELECT id FROM vocabularyterm WHERE name = 'GENO:0000136' AND vocabulary_id = (
		SELECT id FROM vocabulary WHERE vocabularylabel = 'geno_terms'
	)
)
SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
WITH
t1 AS (
	SELECT id FROM vocabularytermset WHERE name = 'AGM Allele Association GENO Terms'
),
t2 AS (
	SELECT id FROM vocabularyterm WHERE name = 'GENO:0000137' AND vocabulary_id = (
		SELECT id FROM vocabulary WHERE vocabularylabel = 'geno_terms'
	)
)
SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
WITH
t1 AS (
	SELECT id FROM vocabularytermset WHERE name = 'AGM Allele Association GENO Terms'
),
t2 AS (
	SELECT id FROM vocabularyterm WHERE name = 'GENO:0000134' AND vocabulary_id = (
		SELECT id FROM vocabulary WHERE vocabularylabel = 'geno_terms'
	)
)
SELECT t1.id, t2.id FROM t1,t2;