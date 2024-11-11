UPDATE ontologyterm SET ontologytermtype = 'FBCVTerm' WHERE ontologytermtype = 'DPOTerm';

DELETE FROM bulkloadfileexception WHERE bulkloadfilehistory_id IN (
	SELECT id FROM bulkloadfilehistory WHERE bulkloadfile_id IN (
		SELECT id FROM bulkloadfile WHERE bulkload_id = (
			SELECT id FROM bulkload WHERE ontologytype = 'DPO'
		)
	)
);

DELETE FROM bulkloadfilehistory WHERE bulkloadfile_id IN (
	SELECT id FROM bulkloadfile WHERE bulkload_id = (
		SELECT id FROM bulkload WHERE ontologytype = 'DPO'
	)
);

DELETE FROM bulkloadfile WHERE bulkload_id = (
	SELECT id FROM bulkload WHERE ontologytype = 'DPO'
);

UPDATE bulkurlload SET bulkloadurl = 'http://purl.obolibrary.org/obo/fbcv.owl' WHERE id = (
	SELECT id FROM bulkload WHERE ontologytype = 'DPO'
);

UPDATE bulkload SET name = 'FBcv Ontology Load', ontologytype = 'FBCV', bulkloadstatus = 'STOPPED' WHERE ontologytype = 'DPO';
