DELETE FROM crossreference cr USING crossreference_ids_to_delete cd WHERE cr.id = cd.id;

DROP TABLE dataprovider_ids_to_keep;
DROP TABLE crossreference_ids_to_delete;
DROP TABLE dataprovider_ids_to_delete;

ALTER TABLE ONLY phenotypeannotation ADD CONSTRAINT crossreference_id_fk FOREIGN KEY (crossreference_id) REFERENCES crossreference(id);
ALTER TABLE ONLY dataprovider ADD CONSTRAINT dataprovider_crossreference_id_fk FOREIGN KEY (crossreference_id) REFERENCES crossreference(id);
ALTER TABLE ONLY externaldatabaseentity ADD CONSTRAINT externaldatabaseentity_preferredcrossreference_id FOREIGN KEY (preferredcrossreference_id) REFERENCES crossreference(id);

-- Migration to switch bulk load file and history around

ALTER TABLE bulkloadfilehistory ADD COLUMN bulkload_id bigint;
ALTER TABLE bulkloadfilehistory ADD COLUMN errormessage text;
ALTER TABLE bulkloadfilehistory ADD COLUMN bulkloadstatus character varying(255);
ALTER TABLE bulkloadfilehistory ADD CONSTRAINT bulkloadfilehistory_bulkload_fk FOREIGN KEY (bulkload_id) REFERENCES bulkload(id);

CREATE INDEX bulkloadfilehistory_bulkloadstatus_index ON bulkloadfilehistory USING btree (bulkloadstatus);
CREATE INDEX bulkloadfilehistory_bulkload_index ON bulkloadfilehistory USING btree (bulkload_id);
CREATE INDEX bulkloadfile_md5sum_index ON bulkloadfile USING btree (md5sum);

UPDATE bulkloadfilehistory bh
SET bulkload_id = bf.bulkload_id
FROM bulkloadfile bf
WHERE
   bf.id = bh.bulkloadfile_id;

UPDATE bulkloadfilehistory bh
SET errormessage = bf.errormessage
FROM bulkloadfile bf
WHERE
   bf.id = bh.bulkloadfile_id;

UPDATE bulkloadfilehistory bh
SET bulkloadstatus = bf.bulkloadstatus
FROM bulkloadfile bf
WHERE
   bf.id = bh.bulkloadfile_id;

DELETE from bulkloadfilehistory where bulkloadfile_id is null;

ALTER TABLE bulkloadfile DROP COLUMN bulkload_id;
ALTER TABLE bulkloadfile DROP COLUMN errorMessage;
ALTER TABLE bulkloadfile DROP COLUMN bulkloadStatus;

DELETE from bulkloadfileexception where bulkloadfilehistory_id in (select id from bulkloadfilehistory where bulkload_id in (select id from bulkload where name like '%GFF%'));
DELETE from bulkloadfilehistory where bulkload_id in (select id from bulkload where name like '%GFF%');

DELETE from bulkfmsload where id in (select id from bulkload where name like '%GFF%');
DELETE from bulkscheduledload where id in (select id from bulkload where name like '%GFF%');
DELETE from bulkload where name like '%GFF%';
DELETE from bulkloadgroup where name = 'GFF Loads';

INSERT INTO bulkloadgroup (id, name) VALUES (nextval('bulkloadgroup_seq'), 'File Management System (FMS) GFF Loads');

INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT', 'FB GFF Transcript Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT', 'Human GFF Transcript Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT', 'MGI GFF Transcript Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT', 'RGD GFF Transcript Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT', 'SGD GFF Transcript Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT', 'WB GFF Transcript Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT', 'XBXL GFF Transcript Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT', 'XBXT GFF Transcript Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT', 'ZFIN GFF Transcript Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';

INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_CDS', 'FB GFF CDS Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_CDS', 'Human GFF CDS Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_CDS', 'MGI GFF CDS Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_CDS', 'RGD GFF CDS Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_CDS', 'SGD GFF CDS Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_CDS', 'WB GFF CDS Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_CDS', 'XBXL GFF CDS Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_CDS', 'XBXT GFF CDS Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_CDS', 'ZFIN GFF CDS Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';

INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_EXON', 'FB GFF Exon Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_EXON', 'Human GFF Exon Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_EXON', 'MGI GFF Exon Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_EXON', 'RGD GFF Exon Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_EXON', 'SGD GFF Exon Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_EXON', 'WB GFF Exon Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_EXON', 'XBXL GFF Exon Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_EXON', 'XBXT GFF Exon Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_EXON', 'ZFIN GFF Exon Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';

INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_EXON_LOCATION', 'FB GFF Exon Location Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_EXON_LOCATION', 'Human GFF Exon Location Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_EXON_LOCATION', 'MGI GFF Exon Location Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_EXON_LOCATION', 'RGD GFF Exon Location Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_EXON_LOCATION', 'SGD GFF Exon Location Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_EXON_LOCATION', 'WB GFF Exon Location Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_EXON_LOCATION', 'XBXL GFF Exon Location Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_EXON_LOCATION', 'XBXT GFF Exon Location Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_EXON_LOCATION', 'ZFIN GFF Exon Location Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';

INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_CDS_LOCATION', 'FB GFF CDS Location Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_CDS_LOCATION', 'Human GFF CDS Location Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_CDS_LOCATION', 'MGI GFF CDS Location Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_CDS_LOCATION', 'RGD GFF CDS Location Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_CDS_LOCATION', 'SGD GFF CDS Location Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_CDS_LOCATION', 'WB GFF CDS Location Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_CDS_LOCATION', 'XBXL GFF CDS Location Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_CDS_LOCATION', 'XBXT GFF CDS Location Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_CDS_LOCATION', 'ZFIN GFF CDS Location Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';

INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_LOCATION', 'FB GFF Transcript Location Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_LOCATION', 'Human GFF Transcript Location Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_LOCATION', 'MGI GFF Transcript Location Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_LOCATION', 'RGD GFF Transcript Location Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_LOCATION', 'SGD GFF Transcript Location Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_LOCATION', 'WB GFF Transcript Location Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_LOCATION', 'XBXL GFF Transcript Location Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_LOCATION', 'XBXT GFF Transcript Location Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_LOCATION', 'ZFIN GFF Transcript Location Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';

INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_GENE', 'FB GFF Transcript Gene Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_GENE', 'Human GFF Transcript Gene Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_GENE', 'MGI GFF Transcript Gene Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_GENE', 'RGD GFF Transcript Gene Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_GENE', 'SGD GFF Transcript Gene Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_GENE', 'WB GFF Transcript Gene Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_GENE', 'XBXL GFF Transcript Gene Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_GENE', 'XBXT GFF Transcript Gene Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_GENE', 'ZFIN GFF Transcript Gene Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';

INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_EXON', 'FB GFF Transcript Exon Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_EXON', 'Human GFF Transcript Exon Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_EXON', 'MGI GFF Transcript Exon Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_EXON', 'RGD GFF Transcript Exon Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_EXON', 'SGD GFF Transcript Exon Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_EXON', 'WB GFF Transcript Exon Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_EXON', 'XBXL GFF Transcript Exon Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_EXON', 'XBXT GFF Transcript Exon Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_EXON', 'ZFIN GFF Transcript Exon Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';

INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_CDS', 'FB GFF Transcript CDS Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_CDS', 'Human GFF Transcript CDS Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_CDS', 'MGI GFF Transcript CDS Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_CDS', 'RGD GFF Transcript CDS Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_CDS', 'SGD GFF Transcript CDS Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_CDS', 'WB GFF Transcript CDS Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_CDS', 'XBXL GFF Transcript CDS Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_CDS', 'XBXT GFF Transcript CDS Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id) SELECT nextval('bulkload_seq'), 'GFF_TRANSCRIPT_CDS', 'ZFIN GFF Transcript CDS Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive) SELECT id, '0 0 22 ? * SUN-THU', false FROM bulkload WHERE backendbulkloadtype in(
	'GFF_TRANSCRIPT', 'GFF_CDS', 'GFF_EXON',
	'GFF_EXON_LOCATION', 'GFF_CDS_LOCATION', 'GFF_TRANSCRIPT_LOCATION',
	'GFF_TRANSCRIPT_GENE', 'GFF_TRANSCRIPT_EXON', 'GFF_TRANSCRIPT_CDS'
);

INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype) SELECT id, 'GFF', 'FB' FROM bulkload WHERE name like 'FB GFF%';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype) SELECT id, 'GFF', 'HUMAN' FROM bulkload WHERE name like 'Human GFF%';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype) SELECT id, 'GFF', 'MGI' FROM bulkload WHERE name like 'MGI GFF%';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype) SELECT id, 'GFF', 'RGD' FROM bulkload WHERE name like 'RGD GFF%';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype) SELECT id, 'GFF', 'SGD' FROM bulkload WHERE name like 'SGD GFF%';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype) SELECT id, 'GFF', 'WB' FROM bulkload WHERE name like 'WB GFF%';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype) SELECT id, 'GFF', 'XBXL' FROM bulkload WHERE name like 'XBXL GFF%';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype) SELECT id, 'GFF', 'XBXT' FROM bulkload WHERE name like 'XBXT GFF%';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype) SELECT id, 'GFF', 'ZFIN' FROM bulkload WHERE name like 'ZFIN GFF%';
