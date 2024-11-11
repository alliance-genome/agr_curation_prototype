CREATE TABLE transcript (
    id bigint NOT NULL,
    transcripttype_id bigint,
    name varchar(255)
);
ALTER TABLE transcript ADD CONSTRAINT transcript_pkey PRIMARY KEY (id);
ALTER TABLE transcript
    ADD CONSTRAINT transcript_id_fk FOREIGN KEY (id) REFERENCES genomicentity(id);
ALTER TABLE transcript
    ADD CONSTRAINT transcript_transcripttype_id_fk FOREIGN KEY (transcripttype_id) REFERENCES ontologyterm(id);
    
CREATE TABLE exon (
    id bigint NOT NULL,
    exontype_id bigint,
    name varchar(255),
    uniqueid varchar(255)
);
ALTER TABLE exon ADD CONSTRAINT exon_pkey PRIMARY KEY (id);
ALTER TABLE exon
    ADD CONSTRAINT exon_id_fk FOREIGN KEY (id) REFERENCES genomicentity(id);
ALTER TABLE exon
    ADD CONSTRAINT exon_exontype_id_fk FOREIGN KEY (exontype_id) REFERENCES ontologyterm(id);  
CREATE INDEX exon_uniqueid_index ON exon USING btree (uniqueid);

CREATE TABLE codingsequence (
    id bigint NOT NULL,
    cdstype_id bigint,
    name varchar(255),
    uniqueid varchar(255)
);
ALTER TABLE codingsequence ADD CONSTRAINT codingsequence_pkey PRIMARY KEY (id);
ALTER TABLE codingsequence
    ADD CONSTRAINT codingsequence_id_fk FOREIGN KEY (id) REFERENCES genomicentity(id);
ALTER TABLE codingsequence
    ADD CONSTRAINT codingsequence_cdstype_id_fk FOREIGN KEY (cdstype_id) REFERENCES ontologyterm(id);  
CREATE INDEX codingsequence_uniqueid_index ON codingsequence USING btree (uniqueid);

CREATE TABLE genomeassembly (
	id bigint NOT NULL,
    specimengenomicmodel_id bigint
);
ALTER TABLE genomeassembly ADD CONSTRAINT genomeassembly_pkey PRIMARY KEY (id);
ALTER TABLE genomeassembly
    ADD CONSTRAINT genomeassembly_id_fk FOREIGN KEY (id) REFERENCES biologicalentity(id);
ALTER TABLE genomeassembly
    ADD CONSTRAINT genomeassembly_specimengenomicmodel_id_fk FOREIGN KEY (specimengenomicmodel_id) REFERENCES affectedgenomicmodel(id);
    
CREATE TABLE genomeassembly_crossreference (
	genomeassembly_id bigint NOT NULL,
	crossreferences_id bigint NOT NULL
);
ALTER TABLE genomeassembly_crossreference
	ADD CONSTRAINT genomeassembly_crossreference_genomeassembly_id_fk FOREIGN KEY (genomeassembly_id) REFERENCES genomeassembly(id);
ALTER TABLE genomeassembly_crossreference
	ADD CONSTRAINT genomeassembly_crossreference_crossreferences_id_fk FOREIGN KEY (crossreferences_id) REFERENCES crossreference(id);
CREATE INDEX genomeassembly_crossreference_genomeassembly_xref_index ON genomeassembly_crossreference USING btree(genomeassembly_id, crossreferences_id);
CREATE INDEX genomeassembly_crossreference_genomeassembly_index ON genomeassembly_crossreference USING btree(genomeassembly_id);
CREATE INDEX genomeassembly_crossreference_crossreference_index ON genomeassembly_crossreference USING btree(crossreferences_id);

INSERT INTO bulkloadgroup (id, name) VALUES (nextval('bulkloadgroup_seq'), 'GFF Loads');

INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'GFF', 'FB GFF Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'GFF', 'Human GFF Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'GFF', 'MGI GFF Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'GFF', 'RGD GFF Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'GFF', 'WGD GFF Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'GFF', 'WB GFF Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'GFF', 'XBXL GFF Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'GFF', 'XBXT GFF Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'GFF', 'ZFIN GFF Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'GFF Loads';
	
INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
	SELECT id, '0 0 22 ? * SUN-THU', true FROM bulkload WHERE backendbulkloadtype = 'GFF';

INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'GFF', 'FB' FROM bulkload WHERE name = 'FB GFF Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'GFF', 'HUMAN' FROM bulkload WHERE name = 'HUMAN GFF Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'GFF', 'MGI' FROM bulkload WHERE name = 'MGI GFF Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'GFF', 'RGD' FROM bulkload WHERE name = 'RGD GFF Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'GFF', 'SGD' FROM bulkload WHERE name = 'SGD GFF Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'GFF', 'WB' FROM bulkload WHERE name = 'WB GFF Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'GFF', 'XBXL' FROM bulkload WHERE name = 'XBXL GFF Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'GFF', 'XBXT' FROM bulkload WHERE name = 'XBXT GFF Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'GFF', 'ZFIN' FROM bulkload WHERE name = 'ZFIN GFF Load';