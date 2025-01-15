INSERT INTO vocabulary (id, name, vocabularylabel) SELECT nextval('vocabulary_seq'), 'HTP Data Sample Sequencing Format','htp_data_sample_sequencing_format' WHERE NOT EXISTS (SELECT id FROM vocabulary WHERE name = 'HTP Data Sample Sequencing Format');
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'paired',id FROM vocabulary WHERE vocabularylabel = 'htp_data_sample_sequencing_format' AND NOT EXISTS (SELECT id FROM vocabularyterm WHERE name = 'paired' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'htp_data_sample_sequencing_format'));
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'single',id FROM vocabulary WHERE vocabularylabel = 'htp_data_sample_sequencing_format' AND NOT EXISTS (SELECT id FROM vocabularyterm WHERE name = 'single' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'htp_data_sample_sequencing_format'));

UPDATE bulkscheduledload SET scheduleactive = true WHERE id IN (
	SELECT id FROM bulkload WHERE backendbulkloadtype = 'EXPRESSION_ATLAS'
);