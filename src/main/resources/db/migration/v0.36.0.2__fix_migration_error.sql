DELETE FROM bulkscheduledload WHERE id = (SELECT id FROM bulkload WHERE name = 'WGD GFF Load');

DELETE FROM bulkload WHERE name = 'WGD GFF Load';

INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'GFF', 'SGD GFF Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'GFF Loads';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
	SELECT id, '0 0 22 ? * SUN-THU', true FROM bulkload WHERE name = 'SGD GFF Load';

INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'GFF', 'SGD' FROM bulkload WHERE name = 'SGD GFF Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'GFF', 'HUMAN' FROM bulkload WHERE name = 'Human GFF Load';