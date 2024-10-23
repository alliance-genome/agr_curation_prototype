insert into bulkloadgroup (id, name, internal, obsolete, dbdatecreated)
values (nextval('bulkloadgroup_seq'), 'GAF Load', false, false, now());

insert into bulkload (id, backendbulkloadtype, name, internal, obsolete, group_id, dbdatecreated, bulkloadstatus)
select nextval('bulkload_seq'),
       'GAF',
       'ZFIN GAF',
       false,
       false,
       id,
       now(),
       'STOPPED'
from bulkloadgroup
where name = 'GAF Load';

insert into bulkload (id, backendbulkloadtype, name, internal, obsolete, group_id, dbdatecreated, bulkloadstatus)
select nextval('bulkload_seq'),
       'GAF',
       'SGD GAF',
       false,
       false,
       id,
       now(),
       'STOPPED'
from bulkloadgroup
where name = 'GAF Load';

insert into bulkload (id, backendbulkloadtype, name, internal, obsolete, group_id, dbdatecreated, bulkloadstatus)
select nextval('bulkload_seq'),
       'GAF',
       'WB GAF',
       false,
       false,
       id,
       now(),
       'STOPPED'
from bulkloadgroup
where name = 'GAF Load';

insert into bulkload (id, backendbulkloadtype, name, internal, obsolete, group_id, dbdatecreated, bulkloadstatus)
select nextval('bulkload_seq'),
       'GAF',
       'MGI GAF',
       false,
       false,
       id,
       now(),
       'STOPPED'
from bulkloadgroup
where name = 'GAF Load';

insert into bulkload (id, backendbulkloadtype, name, internal, obsolete, group_id, dbdatecreated, bulkloadstatus)
select nextval('bulkload_seq'),
       'GAF',
       'FB GAF',
       false,
       false,
       id,
       now(),
       'STOPPED'
from bulkloadgroup
where name = 'GAF Load';

insert into bulkload (id, backendbulkloadtype, name, internal, obsolete, group_id, dbdatecreated, bulkloadstatus)
select nextval('bulkload_seq'),
       'GAF',
       'RGD GAF',
       false,
       false,
       id,
       now(),
       'STOPPED'
from bulkloadgroup
where name = 'GAF Load';

insert into bulkload (id, backendbulkloadtype, name, internal, obsolete, group_id, dbdatecreated, bulkloadstatus)
select nextval('bulkload_seq'),
       'GAF',
       'HUMAN GAF',
       false,
       false,
       id,
       now(),
       'STOPPED'
from bulkloadgroup
where name = 'GAF Load';

insert into bulkload (id, backendbulkloadtype, name, internal, obsolete, group_id, dbdatecreated, bulkloadstatus)
select nextval('bulkload_seq'),
       'GAF',
       'XB GAF',
       false,
       false,
       id,
       now(),
       'STOPPED'
from bulkloadgroup
where name = 'GAF Load';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', true
FROM bulkload
WHERE name = 'ZFIN GAF';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', true
FROM bulkload
WHERE name = 'SGD GAF';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', true
FROM bulkload
WHERE name = 'MGI GAF';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', true
FROM bulkload
WHERE name = 'WB GAF';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', true
FROM bulkload
WHERE name = 'FB GAF';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', true
FROM bulkload
WHERE name = 'RGD GAF';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', true
FROM bulkload
WHERE name = 'HUMAN GAF';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', true
FROM bulkload
WHERE name = 'XB GAF';

insert into bulkurlload (id, bulkloadurl)
select id, 'http://current.geneontology.org/annotations/zfin.gaf.gz'
from bulkload
where name = 'ZFIN GAF';

insert into bulkurlload (id, bulkloadurl)
select id, 'http://current.geneontology.org/annotations/sgd.gaf.gz'
from bulkload
where name = 'SGD GAF';

insert into bulkurlload (id, bulkloadurl)
select id, 'http://current.geneontology.org/annotations/wb.gaf.gz'
from bulkload
where name = 'WB GAF';

insert into bulkurlload (id, bulkloadurl)
select id, 'http://current.geneontology.org/annotations/mgi.gaf.gz'
from bulkload
where name = 'MGI GAF';

insert into bulkurlload (id, bulkloadurl)
select id, 'http://current.geneontology.org/annotations/fb.gaf.gz'
from bulkload
where name = 'FB GAF';

insert into bulkurlload (id, bulkloadurl)
select id, 'http://current.geneontology.org/annotations/rgd.gaf.gz'
from bulkload
where name = 'RGD GAF';

insert into bulkurlload (id, bulkloadurl)
select id, 'https://www.ebi.ac.uk/gxa/species/Homo_sapiens/sitemap.xml?allEntries=true'
from bulkload
where name = 'HUMAN GAF';

insert into bulkurlload (id, bulkloadurl)
select id, 'http://current.geneontology.org/annotations/xenbase.gaf.gz'
from bulkload
where name = 'XB GAF';

create table gene_go_annotation
(
    id        bigint PRIMARY KEY,
    gene_id   bigint,
    goterm_id bigint
);

CREATE SEQUENCE public.gene_go_annotation_seq START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;

ALTER TABLE gene_go_annotation ADD CONSTRAINT gene_go_annotation_gene_fk
    FOREIGN KEY (gene_id) REFERENCES biologicalentity(id);

ALTER TABLE gene_go_annotation ADD CONSTRAINT gene_go_annotation_goterm_fk
    FOREIGN KEY (goterm_id) REFERENCES ontologyterm(id);

ALTER TABLE gene_go_annotation
    ADD UNIQUE (gene_id, goterm_id);