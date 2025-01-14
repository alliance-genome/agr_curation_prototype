insert into bulkloadgroup (id, name, internal, obsolete, dbdatecreated)
values (nextval('bulkloadgroup_seq'), 'GEO CrossReference Load', false, false, now());

insert into bulkload (id, backendbulkloadtype, name, internal, obsolete, group_id, dbdatecreated, bulkloadstatus)
select nextval('bulkload_seq'), 'GEOXREF','ZFIN GEO CrossReference', false, false, id, now(), 'STOPPED'
from bulkloadgroup where name = 'GEO CrossReference Load';

insert into bulkload (id, backendbulkloadtype, name, internal, obsolete, group_id, dbdatecreated, bulkloadstatus)
select nextval('bulkload_seq'), 'GEOXREF','SGD GEO CrossReference', false, false, id, now(), 'STOPPED'
from bulkloadgroup where name = 'GEO CrossReference Load';

insert into bulkload (id, backendbulkloadtype, name, internal, obsolete, group_id, dbdatecreated, bulkloadstatus)
select nextval('bulkload_seq'), 'GEOXREF','WB GEO CrossReference', false, false, id, now(), 'STOPPED'
from bulkloadgroup where name = 'GEO CrossReference Load';

insert into bulkload (id, backendbulkloadtype, name, internal, obsolete, group_id, dbdatecreated, bulkloadstatus)
select nextval('bulkload_seq'), 'GEOXREF','MGI GEO CrossReference', false, false, id, now(), 'STOPPED'
from bulkloadgroup where name = 'GEO CrossReference Load';

insert into bulkload (id, backendbulkloadtype, name, internal, obsolete, group_id, dbdatecreated, bulkloadstatus)
select nextval('bulkload_seq'), 'GEOXREF','FB GEO CrossReference', false, false, id, now(), 'STOPPED'
from bulkloadgroup where name = 'GEO CrossReference Load';

insert into bulkload (id, backendbulkloadtype, name, internal, obsolete, group_id, dbdatecreated, bulkloadstatus)
select nextval('bulkload_seq'), 'GEOXREF','RGD GEO CrossReference', false, false, id, now(), 'STOPPED'
from bulkloadgroup where name = 'GEO CrossReference Load';

insert into bulkload (id, backendbulkloadtype, name, internal, obsolete, group_id, dbdatecreated, bulkloadstatus)
select nextval('bulkload_seq'), 'GEOXREF','HUMAN GEO CrossReference', false, false, id, now(), 'STOPPED'
from bulkloadgroup where name = 'GEO CrossReference Load';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', true FROM bulkload WHERE name = 'ZFIN GEO CrossReference';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', true FROM bulkload WHERE name = 'SGD GEO CrossReference';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', true FROM bulkload WHERE name = 'MGI GEO CrossReference';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', true FROM bulkload WHERE name = 'WB GEO CrossReference';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', true FROM bulkload WHERE name = 'FB GEO CrossReference';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', true FROM bulkload WHERE name = 'RGD GEO CrossReference';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', true FROM bulkload WHERE name = 'HUMAN GEO CrossReference';

insert into bulkurlload (id, bulkloadurl)
select id, 'https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?term=gene_geoprofiles[filter]+AND+Danio+rerio[Organism]&retmax=100000&db=gene'
from bulkload where name = 'ZFIN GEO CrossReference';

insert into bulkurlload (id, bulkloadurl)
select id, 'https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?term=gene_geoprofiles[filter]+AND+Saccharomyces+cerevisiae[Organism]&retmax=100000&db=gene'
from bulkload where name = 'SGD GEO CrossReference';

insert into bulkurlload (id, bulkloadurl)
select id, 'https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?term=gene_geoprofiles[filter]+AND+Caenorhabditis+elegans[Organism]&retmax=100000&db=gene'
from bulkload where name = 'WB GEO CrossReference';

insert into bulkurlload (id, bulkloadurl)
select id, 'https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?term=gene_geoprofiles[filter]+AND+Mus+musculus[Organism]&retmax=100000&db=gene'
from bulkload where name = 'MGI GEO CrossReference';

insert into bulkurlload (id, bulkloadurl)
select id, 'https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?term=gene_geoprofiles[filter]+AND+Drosophila+melanogaster[Organism]&retmax=100000&db=gene'
from bulkload where name = 'FB GEO CrossReference';

insert into bulkurlload (id, bulkloadurl)
select id, 'https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?term=gene_geoprofiles[filter]+AND+Rattus+norvegicus[Organism]&retmax=100000&db=gene'
from bulkload where name = 'RGD GEO CrossReference';

insert into bulkurlload (id, bulkloadurl)
select id, 'https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?term=gene_geoprofiles[filter]+AND+Homo+sapiens[Organism]&retmax=100000&db=gene'
from bulkload where name = 'HUMAN GEO CrossReference';
