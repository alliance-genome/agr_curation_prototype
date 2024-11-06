insert into bulkloadgroup (id, name, internal, obsolete, dbdatecreated)
values (nextval('bulkloadgroup_seq'), 'Expression Atlas Load', false, false, now());

insert into bulkload (id, backendbulkloadtype, name, internal, obsolete, group_id, dbdatecreated, bulkloadstatus)
select nextval('bulkload_seq'), 'EXPRESSION_ATLAS','ZFIN Expression Atlas', false, false, id, now(), 'STOPPED'
from bulkloadgroup where name = 'Expression Atlas Load';

insert into bulkload (id, backendbulkloadtype, name, internal, obsolete, group_id, dbdatecreated, bulkloadstatus)
select nextval('bulkload_seq'), 'EXPRESSION_ATLAS','SGD Expression Atlas', false, false, id, now(), 'STOPPED'
from bulkloadgroup where name = 'Expression Atlas Load';

insert into bulkload (id, backendbulkloadtype, name, internal, obsolete, group_id, dbdatecreated, bulkloadstatus)
select nextval('bulkload_seq'), 'EXPRESSION_ATLAS','WB Expression Atlas', false, false, id, now(), 'STOPPED'
from bulkloadgroup where name = 'Expression Atlas Load';

insert into bulkload (id, backendbulkloadtype, name, internal, obsolete, group_id, dbdatecreated, bulkloadstatus)
select nextval('bulkload_seq'), 'EXPRESSION_ATLAS','MGI Expression Atlas', false, false, id, now(), 'STOPPED'
from bulkloadgroup where name = 'Expression Atlas Load';

insert into bulkload (id, backendbulkloadtype, name, internal, obsolete, group_id, dbdatecreated, bulkloadstatus)
select nextval('bulkload_seq'), 'EXPRESSION_ATLAS','FB Expression Atlas', false, false, id, now(), 'STOPPED'
from bulkloadgroup where name = 'Expression Atlas Load';

insert into bulkload (id, backendbulkloadtype, name, internal, obsolete, group_id, dbdatecreated, bulkloadstatus)
select nextval('bulkload_seq'), 'EXPRESSION_ATLAS','RGD Expression Atlas', false, false, id, now(), 'STOPPED'
from bulkloadgroup where name = 'Expression Atlas Load';

insert into bulkload (id, backendbulkloadtype, name, internal, obsolete, group_id, dbdatecreated, bulkloadstatus)
select nextval('bulkload_seq'), 'EXPRESSION_ATLAS','HUMAN Expression Atlas', false, false, id, now(), 'STOPPED'
from bulkloadgroup where name = 'Expression Atlas Load';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', true FROM bulkload WHERE name = 'ZFIN Expression Atlas';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', true FROM bulkload WHERE name = 'SGD Expression Atlas';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', true FROM bulkload WHERE name = 'MGI Expression Atlas';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', true FROM bulkload WHERE name = 'WB Expression Atlas';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', true FROM bulkload WHERE name = 'FB Expression Atlas';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', true FROM bulkload WHERE name = 'RGD Expression Atlas';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', true FROM bulkload WHERE name = 'HUMAN Expression Atlas';

insert into bulkurlload (id, bulkloadurl)
select id, 'https://www.ebi.ac.uk/gxa/species/Danio_rerio/sitemap.xml?allEntries=true'
from bulkload where name = 'ZFIN Expression Atlas';

insert into bulkurlload (id, bulkloadurl)
select id, 'https://www.ebi.ac.uk/gxa/species/Saccharomyces_cerevisiae/sitemap.xml?allEntries=true'
from bulkload where name = 'SGD Expression Atlas';

insert into bulkurlload (id, bulkloadurl)
select id, 'https://www.ebi.ac.uk/gxa/species/Caenorhabditis_elegans/sitemap.xml?allEntries=true'
from bulkload where name = 'WB Expression Atlas';

insert into bulkurlload (id, bulkloadurl)
select id, 'https://www.ebi.ac.uk/gxa/species/Mus_musculus/sitemap.xml?allEntries=true'
from bulkload where name = 'MGI Expression Atlas';

insert into bulkurlload (id, bulkloadurl)
select id, 'https://www.ebi.ac.uk/gxa/species/Drosophila_melanogaster/sitemap.xml?allEntries=true'
from bulkload where name = 'FB Expression Atlas';

insert into bulkurlload (id, bulkloadurl)
select id, 'https://www.ebi.ac.uk/gxa/species/Rattus_norvegicus/sitemap.xml?allEntries=true'
from bulkload where name = 'RGD Expression Atlas';

insert into bulkurlload (id, bulkloadurl)
select id, 'https://www.ebi.ac.uk/gxa/species/Homo_sapiens/sitemap.xml?allEntries=true'
from bulkload where name = 'HUMAN Expression Atlas';

insert into organization (id, abbreviation, fullname)
VALUES (nextval('organization_seq'),'HUMAN','Human');
