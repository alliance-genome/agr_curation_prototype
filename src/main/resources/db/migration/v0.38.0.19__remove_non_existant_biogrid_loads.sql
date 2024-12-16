DELETE FROM bulkfmsload WHERE fmsdatatype = 'BIOGRID-ORCS' AND fmsdatasubtype IN ('RGD', 'SGD', 'WB', 'XBSL', 'XBXT', 'ZFIN');

DELETE FROM bulkscheduledload WHERE id IN (
	SELECT id FROM bulkload WHERE name IN (
		'RGD Biogrid Orcs Load', 'SGD Biogrid Orcs Load', 'WB Biogrid Orcs Load',
		'XBXL Biogrid Orcs Load', 'XBXT Biogrid Orcs Load', 'ZFIN Biogrid Orcs Load'
	)
);

DELETE FROM bulkload WHERE name IN (
	'RGD Biogrid Orcs Load', 'SGD Biogrid Orcs Load', 'WB Biogrid Orcs Load',
	'XBXL Biogrid Orcs Load', 'XBXT Biogrid Orcs Load', 'ZFIN Biogrid Orcs Load'
);
