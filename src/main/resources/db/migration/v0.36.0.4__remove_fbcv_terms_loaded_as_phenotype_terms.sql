DELETE FROM phenotypeannotation_ontologyterm WHERE phenotypeterms_id IN (
	SELECT id FROM ontologyterm WHERE ontologytermtype = 'FBCVTerm'
);