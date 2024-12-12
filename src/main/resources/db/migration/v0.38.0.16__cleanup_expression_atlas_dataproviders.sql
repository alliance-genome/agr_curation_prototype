DELETE FROM dataprovider WHERE crossreference_id IN (
	SELECT id FROM crossreference WHERE resourcedescriptorpage_id = (
		SELECT id FROM resourcedescriptorpage WHERE name = 'expression_atlas'
	)
);

DELETE FROM genomicentity_crossreference WHERE crossreferences_id IN (
	SELECT id FROM crossreference WHERE resourcedescriptorpage_id = (
		SELECT id FROM resourcedescriptorpage WHERE name = 'expression_atlas'
	)
);

DELETE FROM crossreference WHERE resourcedescriptorpage_id = (
	SELECT id FROM resourcedescriptorpage WHERE name = 'expression_atlas'
);

DELETE FROM organization WHERE abbreviation = 'HUMAN';
