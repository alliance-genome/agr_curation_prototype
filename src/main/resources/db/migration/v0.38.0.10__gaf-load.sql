-- delete old bulk URL load
delete
from bulkurlload
where id in (select id FROM bulkload WHERE backendbulkloadtype = 'GAF');

delete from bulkfmsload
where id in (select id FROM bulkload WHERE backendbulkloadtype = 'GAF');

delete
from bulkscheduledload
where id in (select id FROM bulkload WHERE backendbulkloadtype = 'GAF');

delete from bulkloadfilehistory
where bulkload_id in (select id FROM bulkload WHERE backendbulkloadtype = 'GAF');

delete from bulkload
where id in (select id FROM bulkload WHERE backendbulkloadtype = 'GAF');

delete from bulkloadgroup where name = 'File Management System (FMS) GAF Loads';
-- Create bulk loads got the GAF load
INSERT INTO bulkloadgroup (id, name)
VALUES (nextval('bulkloadgroup_seq'), 'File Management System (FMS) GAF Loads');
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'GAF', 'FB GAF Load', 'STOPPED', id
FROM bulkloadgroup
WHERE name = 'File Management System (FMS) GAF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'GAF', 'HUMAN GAF Load', 'STOPPED', id
FROM bulkloadgroup
WHERE name = 'File Management System (FMS) GAF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'GAF', 'MGI GAF Load', 'STOPPED', id
FROM bulkloadgroup
WHERE name = 'File Management System (FMS) GAF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'GAF', 'RGD GAF Load', 'STOPPED', id
FROM bulkloadgroup
WHERE name = 'File Management System (FMS) GAF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'GAF', 'SGD GAF Load', 'STOPPED', id
FROM bulkloadgroup
WHERE name = 'File Management System (FMS) GAF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'GAF', 'WB GAF Load', 'STOPPED', id
FROM bulkloadgroup
WHERE name = 'File Management System (FMS) GAF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'GAF', 'XB GAF Load', 'STOPPED', id
FROM bulkloadgroup
WHERE name = 'File Management System (FMS) GAF Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'GAF', 'ZFIN GAF Load', 'STOPPED', id
FROM bulkloadgroup
WHERE name = 'File Management System (FMS) GAF Loads';
INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', false
FROM bulkload
WHERE backendbulkloadtype = 'GAF';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'GAF', 'FB'
FROM bulkload
WHERE name = 'FB GAF Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'GAF', 'HUMAN'
FROM bulkload
WHERE name = 'HUMAN GAF Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'GAF', 'MGI'
FROM bulkload
WHERE name = 'MGI GAF Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'GAF', 'RGD'
FROM bulkload
WHERE name = 'RGD GAF Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'GAF', 'SGD'
FROM bulkload
WHERE name = 'SGD GAF Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'GAF', 'WB'
FROM bulkload
WHERE name = 'WB GAF Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'GAF', 'XB'
FROM bulkload
WHERE name = 'XB GAF Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'GAF', 'ZFIN'
FROM bulkload
WHERE name = 'ZFIN GAF Load';
