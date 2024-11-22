INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'EXPRESSION', 'XBXL Expression Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) Expression Loads';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', true FROM bulkload WHERE name = 'XBXL Expression Load';

INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'EXPRESSION', 'XBXL' FROM bulkload WHERE name = 'XBXL Expression Load';
