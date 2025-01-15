INSERT INTO vocabularyterm_synonyms (vocabularyterm_id, synonyms) SELECT id, 'unknown' FROM vocabularyterm WHERE name = 'unknown sex';
INSERT INTO vocabularyterm_synonyms (vocabularyterm_id, synonyms) SELECT id, 'pooled' FROM vocabularyterm WHERE name = 'pooled sexes';
