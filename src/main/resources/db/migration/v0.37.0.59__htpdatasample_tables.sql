-- To create bulk loads

INSERT INTO bulkloadgroup (id, name) VALUES (nextval('bulkloadgroup_seq'), 'HTP Expression Dataset Sample Annotation Bulk Loads');

INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'HTPDATASAMPLE', 'FB HTP Expression Dataset Sample Annotation Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'HTP Expression Dataset Sample Annotation Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'HTPDATASAMPLE', 'ZFIN HTP Expression Dataset Sample Annotation Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'HTP Expression Dataset Sample Annotation Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'HTPDATASAMPLE', 'MGI HTP Expression Dataset Sample Annotation Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'HTP Expression Dataset Sample Annotation Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'HTPDATASAMPLE', 'RGD HTP Expression Dataset Sample Annotation Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'HTP Expression Dataset Sample Annotation Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'HTPDATASAMPLE', 'SGD HTP Expression Dataset Sample Annotation Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'HTP Expression Dataset Sample Annotation Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'HTPDATASAMPLE', 'WB HTP Expression Dataset Sample Annotation Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'HTP Expression Dataset Sample Annotation Bulk Loads';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', true FROM bulkload WHERE backendbulkloadtype = 'HTPDATASAMPLE';

INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'HTPDATASAMPLE', 'FB' FROM bulkload WHERE name = 'FB HTP Expression Dataset Sample Annotation Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'HTPDATASAMPLE', 'MGI' FROM bulkload WHERE name = 'MGI HTP Expression Dataset Sample Annotation Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'HTPDATASAMPLE', 'RGD' FROM bulkload WHERE name = 'RGD HTP Expression Dataset Sample Annotation Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'HTPDATASAMPLE', 'SGD' FROM bulkload WHERE name = 'SGD HTP Expression Dataset Sample Annotation Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'HTPDATASAMPLE', 'WB' FROM bulkload WHERE name = 'WB HTP Expression Dataset Sample Annotation Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'HTPDATASAMPLE', 'ZFIN' FROM bulkload WHERE name = 'ZFIN HTP Expression Dataset Sample Annotation Load';

-- To create tables for HTP Expression Dataset Sample Annotation

CREATE TABLE biosampleage (
	id bigint NOT NULL,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	age character varying(255),
	whenexpressedstagename character varying(255),
	createdby_id bigint,
	updatedby_id bigint,
	stage_id bigint
);

CREATE SEQUENCE biosampleage_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

CREATE TABLE biosamplegenomicinformation (
	id bigint NOT NULL,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	biosampletext character varying(255),
	createdby_id bigint,
	updatedby_id bigint,
	biosampleagm_id bigint,
	biosampleagmtype_id bigint,
	biosampleallele_id bigint
);

CREATE SEQUENCE biosamplegenomicinformation_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

CREATE TABLE htpexpressiondatasetsampleannotation (
	id bigint NOT NULL,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	abundance character varying(255),
	htpexpressionsampletitle character varying(255),
	createdby_id bigint,
	updatedby_id bigint,
	dataprovider_id bigint,
	expressionassayused_id bigint,
	geneticsex_id bigint,
	genomicinformation_id bigint,
	htpexpressionsample_id bigint,
	htpexpressionsampleage_id bigint,
	htpexpressionsampletype_id bigint,
	microarraysampledetails_id bigint,
	sequencingformat_id bigint,
	taxon_id bigint
);

CREATE TABLE htpexpressiondatasetsampleannotation_anatomicalsite (
	htpexpressiondatasetsampleannotation_id bigint NOT NULL,
	htpexpressionsamplelocations_id bigint NOT NULL
);

CREATE TABLE htpexpressiondatasetsampleannotation_assemblyversions (
	htpexpressiondatasetsampleannotation_id bigint NOT NULL,
	assemblyversions character varying(255)
);

CREATE TABLE htpexpressiondatasetsampleannotation_externaldatabaseentity (
	htpexpressiondatasetsampleannotation_id bigint NOT NULL,
	datasetids_id bigint NOT NULL
);

CREATE TABLE htpexpressiondatasetsampleannotation_note (
	htpexpressiondatasetsampleannotation_id bigint NOT NULL,
	relatednotes_id bigint NOT NULL
);

CREATE SEQUENCE htpexpressiondatasetsampleannotation_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

CREATE TABLE microarraysampledetails (
	id bigint NOT NULL,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	channelid character varying(255),
	channelnumber integer,
	createdby_id bigint,
	updatedby_id bigint
);

CREATE SEQUENCE microarraysampledetails_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

ALTER TABLE biosampleage
	ADD CONSTRAINT biosampleage_pkey PRIMARY KEY (id);

ALTER TABLE biosamplegenomicinformation
	ADD CONSTRAINT biosamplegenomicinformation_pkey PRIMARY KEY (id);

ALTER TABLE htpexpressiondatasetsampleannotation
	ADD CONSTRAINT htpexpressiondatasetsampleannotation_pkey PRIMARY KEY (id);

ALTER TABLE microarraysampledetails
	ADD CONSTRAINT microarraysampledetails_pkey PRIMARY KEY (id);

ALTER TABLE htpexpressiondatasetsampleannotation_note
	ADD CONSTRAINT htpdatasample_relatednotes_id_uk UNIQUE (relatednotes_id);

ALTER TABLE htpexpressiondatasetsampleannotation
	ADD CONSTRAINT htpdatasample_microarraysampledetails_id_uk UNIQUE (microarraysampledetails_id);

ALTER TABLE htpexpressiondatasetsampleannotation
	ADD CONSTRAINT htpdatasample_htpexpressionsample_id_uk UNIQUE (htpexpressionsample_id);

ALTER TABLE htpexpressiondatasetsampleannotation
	ADD CONSTRAINT htpdatasample_htpexpressionsampleage_id_uk UNIQUE (htpexpressionsampleage_id);

ALTER TABLE htpexpressiondatasetsampleannotation
	ADD CONSTRAINT htpdatasameple_genomicinformation_id_uk UNIQUE (genomicinformation_id);

CREATE INDEX biosampleage_stage_index ON biosampleage USING btree (stage_id);

CREATE INDEX htpdatasample_anatomicalsite_htpdatasample_index ON htpexpressiondatasetsampleannotation_anatomicalsite USING btree (htpexpressiondatasetsampleannotation_id);

CREATE INDEX htpdatasample_anatomicalsite_samplelocations_index ON htpexpressiondatasetsampleannotation_anatomicalsite USING btree (htpexpressionsamplelocations_id);

CREATE INDEX htpdatasample_assemblyversions_htpdatasample_index ON htpexpressiondatasetsampleannotation_assemblyversions USING btree (htpexpressiondatasetsampleannotation_id);

CREATE INDEX htpdatasample_externaldatabaseentity_datasetids_index ON htpexpressiondatasetsampleannotation_externaldatabaseentity USING btree (datasetids_id);

CREATE INDEX htpdatasample_externaldatabaseentity_htpdatasample_index ON htpexpressiondatasetsampleannotation_externaldatabaseentity USING btree (htpexpressiondatasetsampleannotation_id);

CREATE INDEX htpdatasample_note_htpdatasample_index ON htpexpressiondatasetsampleannotation_note USING btree (htpexpressiondatasetsampleannotation_id);

CREATE INDEX htpdatasample_note_relatednotes_index ON htpexpressiondatasetsampleannotation_note USING btree (relatednotes_id);

CREATE INDEX htpdatasetsampleannotation_createdby_index ON htpexpressiondatasetsampleannotation USING btree (createdby_id);

CREATE INDEX htpdatasetsampleannotation_dataprovider_index ON htpexpressiondatasetsampleannotation USING btree (dataprovider_id);

CREATE INDEX htpdatasetsampleannotation_htpexpressionsample_index ON htpexpressiondatasetsampleannotation USING btree (htpexpressionsample_id);

CREATE INDEX htpdatasetsampleannotation_updatedby_index ON htpexpressiondatasetsampleannotation USING btree (updatedby_id);

ALTER TABLE biosamplegenomicinformation
	ADD CONSTRAINT biosamplegenomicinfo_biosampleagmtype_id_fk FOREIGN KEY (biosampleagmtype_id) REFERENCES vocabularyterm(id);

ALTER TABLE htpexpressiondatasetsampleannotation
	ADD CONSTRAINT htpdatasample_sequencingformat_id_fk FOREIGN KEY (sequencingformat_id) REFERENCES vocabularyterm(id);

ALTER TABLE htpexpressiondatasetsampleannotation
	ADD CONSTRAINT htpdatasample_microarraysampledetails_id_fk FOREIGN KEY (microarraysampledetails_id) REFERENCES microarraysampledetails(id);

ALTER TABLE htpexpressiondatasetsampleannotation_externaldatabaseentity
	ADD CONSTRAINT htpdatasample_externaldbentity_htpdatasample_id_fk FOREIGN KEY (htpexpressiondatasetsampleannotation_id) REFERENCES htpexpressiondatasetsampleannotation(id);

ALTER TABLE htpexpressiondatasetsampleannotation
	ADD CONSTRAINT htpdatasample_dataprovider_id_fk FOREIGN KEY (dataprovider_id) REFERENCES dataprovider(id);

ALTER TABLE biosamplegenomicinformation
	ADD CONSTRAINT biosamplegenomicinformation_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person(id);

ALTER TABLE htpexpressiondatasetsampleannotation
	ADD CONSTRAINT htpdatasample_genomicinformation_id_fk FOREIGN KEY (genomicinformation_id) REFERENCES biosamplegenomicinformation(id);

ALTER TABLE htpexpressiondatasetsampleannotation
	ADD CONSTRAINT htpdatasample_geneticsex_id_fk FOREIGN KEY (geneticsex_id) REFERENCES vocabularyterm(id);

ALTER TABLE biosamplegenomicinformation
	ADD CONSTRAINT htpdatasample_biosampleagm_id_fk FOREIGN KEY (biosampleagm_id) REFERENCES affectedgenomicmodel(id);

ALTER TABLE microarraysampledetails
	ADD CONSTRAINT microarraysampledetails_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person(id);

ALTER TABLE htpexpressiondatasetsampleannotation_anatomicalsite
	ADD CONSTRAINT htpdatasample_anatomicalsite_htpdatasample_id_fk FOREIGN KEY (htpexpressiondatasetsampleannotation_id) REFERENCES htpexpressiondatasetsampleannotation(id);

ALTER TABLE biosampleage
	ADD CONSTRAINT biosampleage_stage_id_fk FOREIGN KEY (stage_id) REFERENCES temporalcontext(id);

ALTER TABLE biosampleage
	ADD CONSTRAINT biosampleage_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person(id);

ALTER TABLE htpexpressiondatasetsampleannotation
	ADD CONSTRAINT htpdatasample_htpexpressionsampleage_id_fk FOREIGN KEY (htpexpressionsampleage_id) REFERENCES biosampleage(id);

ALTER TABLE biosampleage
	ADD CONSTRAINT biosampleage_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person(id);

ALTER TABLE htpexpressiondatasetsampleannotation_anatomicalsite
	ADD CONSTRAINT htpdatasample_anatomicalsite_htpsamplelocations_id_fk FOREIGN KEY (htpexpressionsamplelocations_id) REFERENCES anatomicalsite(id);

ALTER TABLE htpexpressiondatasetsampleannotation
	ADD CONSTRAINT htpdatasample_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person(id);

ALTER TABLE htpexpressiondatasetsampleannotation
	ADD CONSTRAINT htpdatasample_expressionassayused_id_fk FOREIGN KEY (expressionassayused_id) REFERENCES ontologyterm(id);

ALTER TABLE htpexpressiondatasetsampleannotation
	ADD CONSTRAINT htpdatasample_taxon_id_fk FOREIGN KEY (taxon_id) REFERENCES ontologyterm(id);

ALTER TABLE microarraysampledetails
	ADD CONSTRAINT microarraysampledetails_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person(id);

ALTER TABLE htpexpressiondatasetsampleannotation
	ADD CONSTRAINT htpdatasample_htpexpressionsample_id_fk FOREIGN KEY (htpexpressionsample_id) REFERENCES externaldatabaseentity(id);

ALTER TABLE biosamplegenomicinformation
	ADD CONSTRAINT biosamplegenomicinformation_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person(id);

ALTER TABLE htpexpressiondatasetsampleannotation_note
	ADD CONSTRAINT htpdatasample_note_relatednotes_id_fk FOREIGN KEY (relatednotes_id) REFERENCES note(id);

ALTER TABLE htpexpressiondatasetsampleannotation
	ADD CONSTRAINT htpdatasample_htpexpressionsampletype_id_fk FOREIGN KEY (htpexpressionsampletype_id) REFERENCES ontologyterm(id);

ALTER TABLE htpexpressiondatasetsampleannotation_note
	ADD CONSTRAINT htpdatasample_note_htpdatasample_id_fk FOREIGN KEY (htpexpressiondatasetsampleannotation_id) REFERENCES htpexpressiondatasetsampleannotation(id);

ALTER TABLE htpexpressiondatasetsampleannotation
	ADD CONSTRAINT htpdatasample_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person(id);

ALTER TABLE htpexpressiondatasetsampleannotation_assemblyversions
	ADD CONSTRAINT htpdatasample_assemblyversions_htpdatasample_id FOREIGN KEY (htpexpressiondatasetsampleannotation_id) REFERENCES htpexpressiondatasetsampleannotation(id);

ALTER TABLE biosamplegenomicinformation
	ADD CONSTRAINT biosamplegenomicinformation_biosampleallele_id_fk FOREIGN KEY (biosampleallele_id) REFERENCES allele(id);

ALTER TABLE htpexpressiondatasetsampleannotation_externaldatabaseentity
	ADD CONSTRAINT htpdatasample_externaldbentity_datasetids_id_fk FOREIGN KEY (datasetids_id) REFERENCES externaldatabaseentity(id);