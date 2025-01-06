CREATE TABLE genegenomiclocationassociation (
	id bigint CONSTRAINT genegenomiclocationassociation_pkey PRIMARY KEY,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	createdby_id bigint,
	updatedby_id bigint,
	"start" integer,
	"end" integer,
	strand varchar(1),
	relation_id bigint,
	geneassociationsubject_id bigint,
	genegenomiclocationassociationobject_id bigint
);

ALTER TABLE genegenomiclocationassociation ADD CONSTRAINT genegenomiclocationassociation_relation_id_fk
	FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
ALTER TABLE genegenomiclocationassociation ADD CONSTRAINT genegenomiclocationassociation_gasubject_id_fk
	FOREIGN KEY (geneassociationsubject_id) REFERENCES gene(id);
ALTER TABLE genegenomiclocationassociation ADD CONSTRAINT genegenomiclocationassociation_gglaobject_id_fk
	FOREIGN KEY (genegenomiclocationassociationobject_id) REFERENCES assemblycomponent(id);
ALTER TABLE genegenomiclocationassociation ADD CONSTRAINT genegenomiclocationassociation_createdby_id_fk
	FOREIGN KEY (createdby_id) REFERENCES person(id);
ALTER TABLE genegenomiclocationassociation ADD CONSTRAINT genegenomiclocationassociation_updatedby_id_fk
	FOREIGN KEY (updatedby_id) REFERENCES person(id);

CREATE INDEX geneGenomicLocationAssociation_internal_index ON genegenomiclocationassociation
	USING btree (internal);
CREATE INDEX geneGenomicLocationAssociation_obsolete_index ON genegenomiclocationassociation
	USING btree (obsolete);
CREATE INDEX geneGenomicLocationAssociation_strand_index ON genegenomiclocationassociation
	USING btree (strand);
CREATE INDEX geneGenomicLocationAssociation_createdBy_index ON genegenomiclocationassociation
	USING btree (createdby_id);
CREATE INDEX geneGenomicLocationAssociation_updatedBy_index ON genegenomiclocationassociation
	USING btree (updatedby_id);
CREATE INDEX geneGenomicLocationAssociation_relation_index ON genegenomiclocationassociation
	USING btree (relation_id);
CREATE INDEX geneGenomicLocationAssociation_subject_index ON genegenomiclocationassociation
	USING btree (geneassociationsubject_id);
CREATE INDEX geneGenomicLocationAssociation_object_index ON genegenomiclocationassociation
	USING btree (genegenomiclocationassociationobject_id);
	
CREATE SEQUENCE genegenomiclocationassociation_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;
	
CREATE TABLE genegenomiclocationassociation_informationcontententity (
	association_id bigint NOT NULL,
	evidence_id bigint NOT NULL
);

ALTER TABLE genegenomiclocationassociation_informationcontententity ADD CONSTRAINT genegla_ice_association_id_fk
	FOREIGN KEY (association_id) REFERENCES genegenomiclocationassociation(id);
ALTER TABLE genegenomiclocationassociation_informationcontententity ADD CONSTRAINT genegla_ice_evidence_id_fk
	FOREIGN KEY (association_id) REFERENCES informationcontententity(id);
	
CREATE INDEX idxiqhlffl5a2w5p3rkqb7wbqp1m ON genegenomiclocationassociation_informationcontententity
	USING btree (association_id);
CREATE INDEX idx35kn4cryxq4hb1h46lqrnixbm ON genegenomiclocationassociation_informationcontententity
	USING btree (evidence_id);

INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'GFF_GENE', 'FB GFF Gene Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'GFF_GENE', 'Human GFF Gene Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'GFF_GENE', 'MGI GFF Gene Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'GFF_GENE', 'RGD GFF Gene Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'GFF_GENE', 'SGD GFF Gene Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'GFF_GENE', 'WB GFF Gene Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'GFF_GENE', 'XBXL GFF Gene Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'GFF_GENE', 'XBXT GFF Gene Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'GFF_GENE', 'ZFIN GFF Gene Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) GFF Loads';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
	SELECT id, '0 0 22 ? * SUN-THU', false FROM bulkload WHERE backendbulkloadtype = 'GFF_GENE';

INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'GFF', 'FB' FROM bulkload WHERE name = 'FB GFF Gene Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'GFF', 'HUMAN' FROM bulkload WHERE name = 'Human GFF Gene Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'GFF', 'MGI' FROM bulkload WHERE name = 'MGI GFF Gene Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'GFF', 'RGD' FROM bulkload WHERE name = 'RGD GFF Gene Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'GFF', 'SGD' FROM bulkload WHERE name = 'SGD GFF Gene Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'GFF', 'WB' FROM bulkload WHERE name = 'WB GFF Gene Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'GFF', 'XBXL' FROM bulkload WHERE name = 'XBXL GFF Gene Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'GFF', 'XBXT' FROM bulkload WHERE name = 'XBXT GFF Gene Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'GFF', 'ZFIN' FROM bulkload WHERE name = 'ZFIN GFF Gene Load';