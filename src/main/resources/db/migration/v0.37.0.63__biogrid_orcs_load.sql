-- Create bulk loads

INSERT INTO bulkloadgroup (id, name) VALUES (nextval('bulkloadgroup_seq'), 'File Management System (FMS) Biogrid Orcs Loads');
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'BIOGRID_ORCS', 'FB Biogrid Orcs Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) Biogrid Orcs Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'BIOGRID_ORCS', 'HUMAN Biogrid Orcs Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) Biogrid Orcs Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'BIOGRID_ORCS', 'MGI Biogrid Orcs Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) Biogrid Orcs Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'BIOGRID_ORCS', 'RGD Biogrid Orcs Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) Biogrid Orcs Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'BIOGRID_ORCS', 'SGD Biogrid Orcs Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) Biogrid Orcs Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'BIOGRID_ORCS', 'WB Biogrid Orcs Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) Biogrid Orcs Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'BIOGRID_ORCS', 'XBXL Biogrid Orcs Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) Biogrid Orcs Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'BIOGRID_ORCS', 'XBXT Biogrid Orcs Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) Biogrid Orcs Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'BIOGRID_ORCS', 'ZFIN Biogrid Orcs Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) Biogrid Orcs Loads';
INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
	SELECT id, '0 0 22 ? * SUN-THU', false FROM bulkload WHERE backendbulkloadtype = 'BIOGRID_ORCS';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'BIOGRID-ORCS', 'FB' FROM bulkload WHERE name = 'FB Biogrid Orcs Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'BIOGRID-ORCS', 'HUMAN' FROM bulkload WHERE name = 'HUMAN Biogrid Orcs Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'BIOGRID-ORCS', 'MGI' FROM bulkload WHERE name = 'MGI Biogrid Orcs Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'BIOGRID-ORCS', 'RGD' FROM bulkload WHERE name = 'RGD Biogrid Orcs Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'BIOGRID-ORCS', 'SGD' FROM bulkload WHERE name = 'SGD Biogrid Orcs Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'BIOGRID-ORCS', 'WB' FROM bulkload WHERE name = 'WB Biogrid Orcs Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'BIOGRID-ORCS', 'XBSL' FROM bulkload WHERE name = 'XBXL Biogrid Orcs Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'BIOGRID-ORCS', 'XBXT' FROM bulkload WHERE name = 'XBXT Biogrid Orcs Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'BIOGRID-ORCS', 'ZFIN' FROM bulkload WHERE name = 'ZFIN Biogrid Orcs Load';