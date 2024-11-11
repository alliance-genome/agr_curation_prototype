-- To create bulk loads

INSERT INTO bulkloadgroup (id, name) VALUES (nextval('bulkloadgroup_seq'), 'HTP Expression Dataset Annotation Bulk Loads');

INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'HTPDATASET', 'FB HTP Expression Dataset Annotation Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'HTP Expression Dataset Annotation Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'HTPDATASET', 'ZFIN HTP Expression Dataset Annotation Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'HTP Expression Dataset Annotation Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'HTPDATASET', 'MGI HTP Expression Dataset Annotation Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'HTP Expression Dataset Annotation Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'HTPDATASET', 'RGD HTP Expression Dataset Annotation Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'HTP Expression Dataset Annotation Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'HTPDATASET', 'SGD HTP Expression Dataset Annotation Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'HTP Expression Dataset Annotation Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'HTPDATASET', 'WB HTP Expression Dataset Annotation Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'HTP Expression Dataset Annotation Bulk Loads';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', true FROM bulkload WHERE backendbulkloadtype = 'HTPDATASET';

INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'HTPDATASET', 'FB' FROM bulkload WHERE name = 'FB HTP Expression Dataset Annotation Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'HTPDATASET', 'MGI' FROM bulkload WHERE name = 'MGI HTP Expression Dataset Annotation Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'HTPDATASET', 'RGD' FROM bulkload WHERE name = 'RGD HTP Expression Dataset Annotation Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'HTPDATASET', 'SGD' FROM bulkload WHERE name = 'SGD HTP Expression Dataset Annotation Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'HTPDATASET', 'WB' FROM bulkload WHERE name = 'WB HTP Expression Dataset Annotation Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'HTPDATASET', 'ZFIN' FROM bulkload WHERE name = 'ZFIN HTP Expression Dataset Annotation Load';


--Adding vocabulary terms

INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'epigenome', id FROM vocabulary WHERE vocabularylabel = 'data_set_category_tags';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'functional_genomics_and_proteomics', id FROM vocabulary WHERE vocabularylabel = 'data_set_category_tags';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'karyotyping', id FROM vocabulary WHERE vocabularylabel = 'data_set_category_tags';

INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'htp_expression_dataset_summary', 'Summary of the high-throughput expression dataset' ,id FROM vocabulary WHERE vocabularylabel = 'note_type';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'htp_expression_dataset_sample_note', 'Note pertaining to a high-throughput expression dataset sample' ,id FROM vocabulary WHERE vocabularylabel = 'note_type';

INSERT INTO vocabularytermset (id, name, vocabularylabel, vocabularytermsetvocabulary_id)
	SELECT nextval('vocabularytermset_seq'), 'HTP Expression Dataset Note Type', 'htp_expression_dataset_note_type', id FROM vocabulary WHERE vocabularylabel = 'note_type';

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE vocabularylabel = 'htp_expression_dataset_note_type'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'htp_expression_dataset_summary' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'note_type'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset (id, name, vocabularylabel, vocabularytermsetvocabulary_id)
	SELECT nextval('vocabularytermset_seq'), 'HTP Expression Dataset Sample Note Type', 'htp_expression_dataset_sample_note_type', id FROM vocabulary WHERE vocabularylabel = 'note_type';

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE vocabularylabel = 'htp_expression_dataset_sample_note_type'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'htp_expression_dataset_sample_note' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'note_type'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

-- Adding htpexpressiondatasetannotation

CREATE SEQUENCE htpexpressiondatasetannotation_seq START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE externaldatabaseentity_seq START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE htpexpressiondatasetannotation (
     id bigint NOT NULL,
     datecreated timestamp(6) with time zone,
     dateupdated timestamp(6) with time zone,
     dbdatecreated timestamp(6) with time zone,
     dbdateupdated timestamp(6) with time zone,
     internal boolean DEFAULT false NOT NULL,
     obsolete boolean DEFAULT false NOT NULL,
     curie character varying(255),
     modentityid character varying(255),
     modinternalid character varying(255),
     name character varying(255),
     numberofchannels integer,
     relatednote_id bigint,
     createdby_id bigint,
     updatedby_id bigint,
     dataprovider_id bigint,
     htpexpressiondataset_id bigint
);

CREATE TABLE externaldatabaseentity (
     id bigint NOT NULL,
     datecreated timestamp(6) with time zone,
     dateupdated timestamp(6) with time zone,
     dbdatecreated timestamp(6) with time zone,
     dbdateupdated timestamp(6) with time zone,
     internal boolean DEFAULT false NOT NULL,
     obsolete boolean DEFAULT false NOT NULL,
     curie character varying(255),
     createdby_id bigint,
     updatedby_id bigint,
     preferredcrossreference_id bigint
);

CREATE TABLE externaldatabaseentity_crossreference (
     externaldatabaseentity_id bigint NOT NULL,
     crossreferences_id bigint NOT NULL
);

CREATE TABLE externaldatabaseentity_secondaryidentifiers (
     externaldatabaseentity_id bigint NOT NULL,
     secondaryidentifiers text
);

CREATE TABLE htpexpressiondatasetannotation_externaldatabaseentity (
     htpexpressiondatasetannotation_id bigint NOT NULL,
     subseries_id bigint NOT NULL
);

CREATE TABLE htpexpressiondatasetannotation_categorytags (
     htpexpressiondatasetannotation_id bigint NOT NULL,
     categorytags_id bigint NOT NULL
 );

CREATE TABLE htpexpressiondatasetannotation_reference (
     htpexpressiondatasetannotation_id bigint NOT NULL,
     references_id bigint NOT NULL
 );

ALTER TABLE htpexpressiondatasetannotation ADD CONSTRAINT htpexpressiondatasetannotation_pkey PRIMARY KEY (id);

ALTER TABLE externaldatabaseentity ADD CONSTRAINT externaldatabaseentity_pkey PRIMARY KEY (id);

CREATE INDEX htpdatasetannotation_reference_htpdataset_index ON htpexpressiondatasetannotation_reference USING btree (htpexpressiondatasetannotation_id);

CREATE INDEX htpdatasetannotation_reference_references_index ON htpexpressiondatasetannotation_reference USING btree (references_id);

CREATE INDEX htpdatasetannotation_categorytags_index ON htpexpressiondatasetannotation_categorytags USING btree (categorytags_id);

CREATE INDEX htpdatasetannotation_htpdatasetid_index ON htpexpressiondatasetannotation_categorytags USING btree (htpexpressiondatasetannotation_id);

CREATE INDEX externaldbentity_crossreference_crossreferences_index ON externaldatabaseentity_crossreference USING btree (crossreferences_id);

CREATE INDEX externaldbentity_crossreference_externaldbentity_index ON externaldatabaseentity_crossreference USING btree (externaldatabaseentity_id);

CREATE INDEX externaldbentity_secondaryidentifiers_externaldbentity_index ON externaldatabaseentity_secondaryidentifiers USING btree (externaldatabaseentity_id);

CREATE INDEX htpdatasetannotation_externaldatabaseentity_htpdataset_index ON htpexpressiondatasetannotation_externaldatabaseentity USING btree (htpexpressiondatasetannotation_id);

CREATE INDEX htpdatasetannotation_externaldatabaseentity_subseries_index ON htpexpressiondatasetannotation_externaldatabaseentity USING btree (subseries_id);

ALTER TABLE htpexpressiondatasetannotation_externaldatabaseentity
    ADD CONSTRAINT htpdatasetannotation_externaldbentity_subseries_id_fk FOREIGN KEY (subseries_id) REFERENCES externaldatabaseentity(id);

ALTER TABLE externaldatabaseentity_secondaryidentifiers
    ADD CONSTRAINT externaldbentity_secondaryidentifiers_externaldbentity_id_fk FOREIGN KEY (externaldatabaseentity_id) REFERENCES externaldatabaseentity(id);

ALTER TABLE externaldatabaseentity_crossreference
    ADD CONSTRAINT externaldbentity_crossreference_externaldbentity_id_fk FOREIGN KEY (externaldatabaseentity_id) REFERENCES externaldatabaseentity(id) ON DELETE CASCADE;

ALTER TABLE htpexpressiondatasetannotation
    ADD CONSTRAINT htpexpressiondatasetannotation_htpexpressiondataset_id_fk FOREIGN KEY (htpexpressiondataset_id) REFERENCES externaldatabaseentity(id);

ALTER TABLE externaldatabaseentity
    ADD CONSTRAINT externaldatabaseentity_preferredcrossreference_id FOREIGN KEY (preferredcrossreference_id) REFERENCES crossreference(id);

ALTER TABLE externaldatabaseentity
    ADD CONSTRAINT externaldatabaseentity_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person(id);

ALTER TABLE htpexpressiondatasetannotation_externaldatabaseentity
    ADD CONSTRAINT htpannotation_externaldbentity_htpannotation_id_fk FOREIGN KEY (htpexpressiondatasetannotation_id) REFERENCES htpexpressiondatasetannotation(id);

ALTER TABLE externaldatabaseentity_crossreference
    ADD CONSTRAINT externaldatabaseentity_crossreference_crossreferences_id_fk FOREIGN KEY (crossreferences_id) REFERENCES crossreference(id);

ALTER TABLE externaldatabaseentity
    ADD CONSTRAINT externaldatabaseentity_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person(id);

ALTER TABLE htpexpressiondatasetannotation 
    ADD CONSTRAINT htpexpressiondatasetannotation_relatednote_id_fk FOREIGN KEY (relatednote_id) REFERENCES note (id);

ALTER TABLE htpexpressiondatasetannotation_categorytags
    ADD CONSTRAINT htpdatasetannotation_categorytags_htpdatasetannotation_id_fk FOREIGN KEY (htpexpressiondatasetannotation_id) REFERENCES htpexpressiondatasetannotation(id);

ALTER TABLE htpexpressiondatasetannotation
    ADD CONSTRAINT htpexpressiondatasetannotation_dataprovider_id_fk FOREIGN KEY (dataprovider_id) REFERENCES dataprovider(id);

ALTER TABLE htpexpressiondatasetannotation_categorytags
    ADD CONSTRAINT htpdatasetannotation_categorytags_categorytags_id_fk FOREIGN KEY (categorytags_id) REFERENCES vocabularyterm(id);

ALTER TABLE htpexpressiondatasetannotation_reference
    ADD CONSTRAINT htpdatasetannotation_reference_htpdatasetannotation_id_fk FOREIGN KEY (htpexpressiondatasetannotation_id) REFERENCES htpexpressiondatasetannotation(id);

ALTER TABLE htpexpressiondatasetannotation_reference
    ADD CONSTRAINT htpdatasetannotation_reference_references_id_fk FOREIGN KEY (references_id) REFERENCES reference(id);

ALTER TABLE htpexpressiondatasetannotation
    ADD CONSTRAINT htpdatasetannotation_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person(id);

ALTER TABLE htpexpressiondatasetannotation
    ADD CONSTRAINT htpdatasetannotation_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person(id);

ALTER TABLE htpexpressiondatasetannotation
    ADD CONSTRAINT htpdatasetexpressionannotation_htpdataset_id_uk UNIQUE (htpexpressiondataset_id);

ALTER TABLE externaldatabaseentity
    ADD CONSTRAINT externaldatabaseentity_preferredcrossreference_id_uk UNIQUE (preferredcrossreference_id);

 ALTER TABLE htpexpressiondatasetannotation
    ADD CONSTRAINT htpexpressiondatasetannotation_relatednote_id_uk UNIQUE (relatednote_id);