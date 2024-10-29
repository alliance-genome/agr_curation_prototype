INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('vocabulary_seq'), 'HTP Data Sample Sequencing Format','htp_data_sample_sequencing_format');
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'single',id FROM vocabulary WHERE vocabularylabel = 'htp_data_sample_sequencing_format';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'paired',id FROM vocabulary WHERE vocabularylabel = 'htp_data_sample_sequencing_format';
