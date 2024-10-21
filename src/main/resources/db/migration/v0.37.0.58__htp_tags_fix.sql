DELETE FROM htpexpressiondatasetannotation_categorytags WHERE categorytags_id IN (SELECT id FROM vocabularyterm WHERE name = 'strain study');
DELETE FROM vocabularyterm_synonyms WHERE vocabularyterm_id IN (SELECT id FROM vocabularyterm WHERE name = 'strain study');

DELETE FROM vocabularyterm WHERE name = 'strain study';

DELETE FROM vocabularyterm_synonyms WHERE vocabularyterm_id IN (SELECT id FROM vocabularyterm WHERE name = 'anatomical structure');
DELETE FROM vocabularyterm_synonyms WHERE vocabularyterm_id IN (SELECT id FROM vocabularyterm WHERE name = 'chemical stimulus');

INSERT INTO vocabularyterm_synonyms (vocabularyterm_id, synonyms) SELECT id, 'tissue type study' FROM vocabularyterm WHERE name = 'anatomical structure';
INSERT INTO vocabularyterm_synonyms (vocabularyterm_id, synonyms) SELECT id, 'tissue specific' FROM vocabularyterm WHERE name = 'anatomical structure';

INSERT INTO vocabularyterm_synonyms (vocabularyterm_id, synonyms) SELECT id, 'chemical stimulus study' FROM vocabularyterm WHERE name = 'chemical stimulus';
INSERT INTO vocabularyterm_synonyms (vocabularyterm_id, synonyms) SELECT id, 'response to chemical' FROM vocabularyterm WHERE name = 'chemical stimulus';