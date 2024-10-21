CREATE TABLE curatedvariantgenomiclocation (
	id bigint PRIMARY KEY,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	"end" integer,
	start integer,
	phase integer,
	variationstrand character varying(1),
	hgvs character varying(255),
	paddedbase character varying(1),
	insertedsequence character varying(1000),
	deletedsequence character varying(1000),
	numberadditionaldnabasepairs integer,
	numberremoveddnabasepairs integer,
	createdby_id bigint,
	updatedby_id bigint,
	relation_id bigint,
	variantassociationsubject_id bigint,
	variantgenomiclocationassociationobject_id bigint,
	dnamutationtype_id bigint,
	genelocalizationtype_id bigint,
	consequence_id bigint,
	curatedconsequence_id bigint,
	referencesequence character varying(1000),
	variantsequence character varying(1000)
);

CREATE TABLE curatedvariantgenomiclocation_informationcontententity (
	association_id bigint NOT NULL,
	evidence_id bigint NOT NULL
);

CREATE SEQUENCE curatedvariantgenomiclocation_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

CREATE INDEX cvgla_internal_index ON curatedvariantgenomiclocation USING btree (internal);
CREATE INDEX cvgla_obsolete_index ON curatedvariantgenomiclocation USING btree (obsolete);
CREATE INDEX cvgla_hgvs_index ON curatedvariantgenomiclocation USING btree (hgvs);
CREATE INDEX cvgla_createdby_index ON curatedvariantgenomiclocation USING btree (createdby_id);
CREATE INDEX cvgla_updatedby_index ON curatedvariantgenomiclocation USING btree (updatedby_id);
CREATE INDEX cvgla_relation_index ON curatedvariantgenomiclocation USING btree (relation_id);
CREATE INDEX cvgla_consequence_index ON curatedvariantgenomiclocation USING btree (consequence_id);
CREATE INDEX cvgla_curatedconsequence_index ON curatedvariantgenomiclocation USING btree (curatedconsequence_id);
CREATE INDEX cvgla_dnamutationtype_index ON curatedvariantgenomiclocation USING btree (dnamutationtype_id);
CREATE INDEX cvgla_genelocalizationtype_index ON curatedvariantgenomiclocation USING btree (genelocalizationtype_id);
CREATE INDEX cvgla_variantassociationsubject_index ON curatedvariantgenomiclocation USING btree (variantassociationsubject_id);
CREATE INDEX cvgla_vglaobject_index ON curatedvariantgenomiclocation USING btree (variantgenomiclocationassociationobject_id);

CREATE INDEX idxf6ue9897y96ts14v3cxil0rr3 ON curatedvariantgenomiclocation_informationcontententity USING btree (association_id);
CREATE INDEX idxf71i1k78crgfhf7pnaewvc1ia ON curatedvariantgenomiclocation_informationcontententity USING btree (evidence_id);

ALTER TABLE ONLY curatedvariantgenomiclocation ADD CONSTRAINT cvgla_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person(id);
ALTER TABLE ONLY curatedvariantgenomiclocation ADD CONSTRAINT cvgla_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person(id);
ALTER TABLE ONLY curatedvariantgenomiclocation ADD CONSTRAINT cvgla_relation_id_fk FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
ALTER TABLE ONLY curatedvariantgenomiclocation ADD CONSTRAINT cvgla_variantassociationsubject_id_fk FOREIGN KEY (variantassociationsubject_id) REFERENCES variant(id);
ALTER TABLE ONLY curatedvariantgenomiclocation ADD CONSTRAINT cvgla_variantgenomiclocationassociationobject_id_fk FOREIGN KEY (variantgenomiclocationassociationobject_id) REFERENCES assemblycomponent(id);
ALTER TABLE ONLY curatedvariantgenomiclocation ADD CONSTRAINT cvgla_dnamutationtype_id_fk FOREIGN KEY (dnamutationtype_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY curatedvariantgenomiclocation ADD CONSTRAINT cvgla_genelocalizationtype_id_fk FOREIGN KEY (genelocalizationtype_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY curatedvariantgenomiclocation ADD CONSTRAINT cvgla_curatedconsequence_id_fk FOREIGN KEY (curatedconsequence_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY curatedvariantgenomiclocation ADD CONSTRAINT cvgla_consequence_id_fk FOREIGN KEY (consequence_id) REFERENCES ontologyterm(id);


ALTER TABLE ONLY curatedvariantgenomiclocation_informationcontententity ADD CONSTRAINT cvgla_ice_association_id_fk FOREIGN KEY (association_id) REFERENCES curatedvariantgenomiclocation(id);
ALTER TABLE ONLY curatedvariantgenomiclocation_informationcontententity ADD CONSTRAINT cvgla_ice_evidence_id_fk FOREIGN KEY (evidence_id) REFERENCES informationcontententity(id);

CREATE TABLE allelevariantassociation (
	id bigint PRIMARY KEY,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	createdby_id bigint,
	updatedby_id bigint,
	alleleassociationsubject_id bigint,
	allelevariantassociationobject_id bigint,
	relation_id bigint,
	evidencecode_id bigint,
	relatednote_id bigint
);

CREATE TABLE allelevariantassociation_informationcontententity (
	association_id bigint NOT NULL,
	evidence_id bigint NOT NULL
);

CREATE SEQUENCE allelevariantassociation_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;
	
CREATE INDEX allelevariantassociation_internal_index ON allelevariantassociation USING btree (internal);
CREATE INDEX allelevariantassociation_obsolete_index ON allelevariantassociation USING btree (obsolete);
CREATE INDEX allelevariantassociation_createdby_index ON allelevariantassociation USING btree (createdby_id);
CREATE INDEX allelevariantassociation_updatedby_index ON allelevariantassociation USING btree (updatedby_id);
CREATE INDEX allelevariantassociation_relation_index ON allelevariantassociation USING btree (relation_id);
CREATE INDEX allelevariantassociation_evidencecode_index ON allelevariantassociation USING btree (evidencecode_id);
CREATE INDEX allelevariantassociation_relatednote_index ON allelevariantassociation USING btree (relatednote_id);
CREATE INDEX allelevariantassociation_alleleassociationsubject_index ON allelevariantassociation USING btree (alleleassociationsubject_id);
CREATE INDEX allelevariantassociation_allelevariantassociationobject_index ON allelevariantassociation USING btree (allelevariantassociationobject_id);


CREATE INDEX idxlptd4iugnh74h5rnm4rhwwmgt ON allelevariantassociation_informationcontententity USING btree (association_id);
CREATE INDEX idxfkin7xx0kjoew8umur1riadtv ON allelevariantassociation_informationcontententity USING btree (evidence_id);

ALTER TABLE ONLY allelevariantassociation ADD CONSTRAINT allelevariantassociation_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person(id);
ALTER TABLE ONLY allelevariantassociation ADD CONSTRAINT allelevariantassociation_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person(id);
ALTER TABLE ONLY allelevariantassociation ADD CONSTRAINT allelevariantassociation_evidencecode_id_fk FOREIGN KEY (evidencecode_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY allelevariantassociation ADD CONSTRAINT allelevariantassociation_relatednote_id_fk FOREIGN KEY (relatednote_id) REFERENCES note(id);
ALTER TABLE ONLY allelevariantassociation ADD CONSTRAINT allelevariantassociation_relation_id_fk FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
ALTER TABLE ONLY allelevariantassociation ADD CONSTRAINT allelevariantassociation_aasubject_id_fk FOREIGN KEY (alleleassociationsubject_id) REFERENCES allele(id);
ALTER TABLE ONLY allelevariantassociation ADD CONSTRAINT allelevariantassociation_avaobject_id_fk FOREIGN KEY (allelevariantassociationobject_id) REFERENCES variant(id);

ALTER TABLE ONLY allelevariantassociation_informationcontententity ADD CONSTRAINT allelevariantassociation_ice_association_id_fk FOREIGN KEY (association_id) REFERENCES allelevariantassociation(id);
ALTER TABLE ONLY allelevariantassociation_informationcontententity ADD CONSTRAINT allelevariantassociation_ice_evidence_id_fk FOREIGN KEY (evidence_id) REFERENCES informationcontententity(id);

INSERT INTO vocabularytermset (id, name, vocabularylabel, vocabularytermsetvocabulary_id)
	SELECT nextval('vocabularytermset_seq'), 'Allele Variant Association Relation', 'allele_variant_relation', id FROM vocabulary WHERE vocabularylabel = 'allele_relation';

INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'has_variant', id FROM vocabulary WHERE vocabularylabel = 'allele_relation';

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE vocabularylabel = 'allele_variant_relation'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'has_variant' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'allele_relation'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;
	