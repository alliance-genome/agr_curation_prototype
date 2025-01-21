UPDATE bulkscheduledload SET scheduleactive = false WHERE id IN (
	SELECT id FROM bulkload WHERE backendbulkloadtype = 'EXPRESSION_ATLAS'
);

DELETE FROM dataprovider WHERE crossreference_id IN (
	SELECT id FROM crossreference WHERE resourcedescriptorpage_id IN (
		SELECT id FROM resourcedescriptorpage WHERE name = 'expression_atlas'
	)
);

DELETE FROM genomicentity_crossreference WHERE crossreferences_id IN (
	SELECT id FROM crossreference WHERE resourcedescriptorpage_id IN (
		SELECT id FROM resourcedescriptorpage WHERE name = 'expression_atlas'
	)
);

DELETE FROM crossreference WHERE resourcedescriptorpage_id IN (
	SELECT id FROM resourcedescriptorpage WHERE name = 'expression_atlas'
);