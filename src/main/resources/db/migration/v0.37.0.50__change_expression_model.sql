DROP TABLE anatomicalsite_anatomicalstructurequalifiers;
DROP TABLE anatomicalsite_anatomicalsubstructurequalifiers;
DROP TABLE anatomicalsite_cellularcomponentqualifiers;

CREATE TABLE anatomicalsite_anatomicalstructurequalifiers (
	anatomicalsite_id BIGINT,
	anatomicalstructurequalifiers_id BIGINT
);

CREATE TABLE anatomicalsite_anatomicalsubstructurequalifiers (
	anatomicalsite_id BIGINT,
	anatomicalsubstructurequalifiers_id BIGINT
);

CREATE TABLE anatomicalsite_cellularcomponentqualifiers (
	anatomicalsite_id BIGINT,
	cellularcomponentqualifiers_id BIGINT
);

ALTER TABLE anatomicalsite_anatomicalstructurequalifiers
	ADD CONSTRAINT anatomicalstructurequalifiers_anatomicalsite_fk
		FOREIGN KEY (anatomicalsite_id) REFERENCES anatomicalsite(id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE anatomicalsite_anatomicalstructurequalifiers
	ADD CONSTRAINT anatomicalstructurequalifiers_structurequalifier_fk
		FOREIGN KEY (anatomicalstructurequalifiers_id) REFERENCES ontologyterm(id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE anatomicalsite_anatomicalsubstructurequalifiers
	ADD CONSTRAINT anatomicalsubstructurequalifiers_anatomicalsite_fk
		FOREIGN KEY (anatomicalsite_id) REFERENCES anatomicalsite(id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE anatomicalsite_anatomicalsubstructurequalifiers
	ADD CONSTRAINT anatomicalsubstructurequalifiers_qualifier_fk
		FOREIGN KEY (anatomicalsubstructurequalifiers_id) REFERENCES ontologyterm(id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE anatomicalsite_cellularcomponentqualifiers
	ADD CONSTRAINT cellularcomponentqualifiers_anatomicalsite_fk
		FOREIGN KEY (anatomicalsite_id) REFERENCES anatomicalsite(id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE anatomicalsite_cellularcomponentqualifiers
	ADD CONSTRAINT cellularcomponentqualifiers_cellularcomponentqualifier_fk
		FOREIGN KEY (cellularcomponentqualifiers_id) REFERENCES ontologyterm(id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

CREATE INDEX anatomicalstructurequalifiers_anatomicalsite_index ON anatomicalsite_anatomicalstructurequalifiers USING btree (anatomicalsite_id);
CREATE INDEX anatomicalstructurequalifiers_structurequalifiers_index ON anatomicalsite_anatomicalstructurequalifiers USING btree (anatomicalstructurequalifiers_id);
CREATE INDEX anatomicalsubstructurequalifiers_anatomicalsite_index ON anatomicalsite_anatomicalsubstructurequalifiers USING btree (anatomicalsite_id);
CREATE INDEX anatomicalsubstructurequalifiers_qualifiers_index ON anatomicalsite_anatomicalsubstructurequalifiers USING btree (anatomicalsubstructurequalifiers_id);
CREATE INDEX cellularcomponentqualifiers_anatomicalsite_index ON anatomicalsite_cellularcomponentqualifiers USING btree (anatomicalsite_id);
CREATE INDEX cellularcomponentqualifiers_qualifiers_index ON anatomicalsite_cellularcomponentqualifiers USING btree (cellularcomponentqualifiers_id);

DELETE FROM vocabularyterm WHERE vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'cellular_component_qualifiers');
DELETE FROM vocabulary WHERE vocabularylabel = 'cellular_component_qualifiers';

INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('vocabulary_seq'), 'Spatial Expression Qualifier', 'spatial_expression_qualifiers');

INSERT INTO vocabularytermset (id, name, vocabularylabel, vocabularytermsetvocabulary_id)
   SELECT nextval('vocabularytermset_seq'), 'Anatomical Structure Qualifier', 'anatomical_structure_qualifier', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';

INSERT INTO vocabularytermset (id, name, vocabularylabel, vocabularytermsetvocabulary_id)
   SELECT nextval('vocabularytermset_seq'), 'Anatomical SubStructure Qualifier', 'anatomical_subtructure_qualifier', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';

INSERT INTO vocabularytermset (id, name, vocabularylabel, vocabularytermsetvocabulary_id)
   SELECT nextval('vocabularytermset_seq'), 'Cellular Component Qualifier', 'cellular_component_qualifier', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';

-- QUALIFIER TERMS
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000099', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000118', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000069', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000058', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000168', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000071', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000128', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000089', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000674', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000109', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000082', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000102', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000051', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000383', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000135', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000120', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000066', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000680', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000170', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000050', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000045', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000093', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000127', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000065', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000131', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000172', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000101', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000110', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000688', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000169', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000027', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000091', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000096', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000319', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000673', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000052', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000678', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000079', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000374', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000059', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000322', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000054', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000036', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000007', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000039', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000074', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000373', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000678', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000095', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000000', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000117', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000037', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000055', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000029', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000081', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000653', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000132', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000684', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000047', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0001001', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000004', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000130', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000066', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000683', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000034', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000088', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000137', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000062', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000100', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000125', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000028', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000323', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000033', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000083', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000049', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000003', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000111', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000124', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000333', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000171', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000077', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000145', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000142', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000056', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000055', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000079', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000063', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000141', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000119', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000032', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000076', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000057', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000090', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000013', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000075', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000199', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000377', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000042', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000078', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000104', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000050', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000379', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000112', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000039', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000058', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000127', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000077', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000046', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000069', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000035', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000121', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'RO:0002325', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000071', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000072', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000073', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000093', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000060', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000080', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000041', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000136', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000086', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000098', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000143', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000133', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000122', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000147', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000067', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000085', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000134', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000320', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000148', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000371', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000064', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000082', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000672', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000064', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000070', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000031', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000048', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000026', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000084', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000103', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000038', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000094', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000097', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000150', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000139', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000123', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000030', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000063', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000084', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000677', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000126', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000167', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000046', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000679', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000087', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000128', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000053', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000115', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000049', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000088', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000056', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000092', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000006', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000092', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000685', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000682', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000005', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000068', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000671', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000040', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000078', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000045', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000067', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000080', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000129', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000053', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000061', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000070', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'BSPO:0000075', id FROM vocabulary WHERE vocabularylabel = 'spatial_expression_qualifiers';

-- ANATOMICAL STRUCTURE QUALIFIERS

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000078' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000109' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000679' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000379' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000683' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000084' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000040' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000092' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000037' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000074' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000079' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000115' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000055' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000374' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000065' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000067' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000030' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000035' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000111' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000172' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000077' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000034' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000653' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000128' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000170' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000000' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000322' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000673' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000099' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000061' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000100' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000110' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000049' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000050' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000056' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000048' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000171' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000141' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000082' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000058' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000070' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000085' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000058' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000082' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000101' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000080' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000063' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000678' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000684' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000383' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000688' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000007' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000006' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000049' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000068' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000136' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000077' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000055' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000671' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000004' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000097' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000062' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000199' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000128' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000169' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000092' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000073' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000319' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000047' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000122' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000069' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000078' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000129' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000070' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000121' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000090' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000112' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000093' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000167' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000083' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000677' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000150' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000132' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000142' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000052' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000071' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000069' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000053' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000053' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000087' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000032' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000064' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000130' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000080' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000371' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000076' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000674' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000029' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000088' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000036' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000072' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000373' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000071' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000075' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000124' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000005' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000057' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000050' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000137' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000117' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000127' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000041' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000091' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000133' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000094' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000685' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000134' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000680' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000103' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000168' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000125' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000038' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000067' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000064' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000039' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000098' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000123' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000104' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000118' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000060' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000323' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000672' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000066' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000046' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000095' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000096' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000026' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000102' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000143' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000033' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000147' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000131' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000148' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000145' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000333' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000139' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000028' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000377' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000119' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000120' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000027' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000127' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000086' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000056' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000003' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000042' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000013' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000063' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000682' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000079' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000054' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000046' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000093' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000088' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000059' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000135' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000084' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0001001' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000066' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000045' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000045' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000031' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000051' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000039' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000081' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000075' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000126' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000089' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='BSPO:0000678' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000320' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

-- ANATOMICAL SUBSTRUCTURE QUALIFIERS

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_subtructure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000167' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_subtructure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000058' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_subtructure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000059' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_subtructure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000169' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_subtructure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000071' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_subtructure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000054' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_subtructure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000150' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_subtructure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000046' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_subtructure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000653' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_subtructure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000067' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_subtructure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000055' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_subtructure_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000063' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

-- CELLULAR COMPONENT QUALIFIERS

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000066' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000167' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000048' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000058' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000170' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000047' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000065' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000653' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000169' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000046' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000055' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000168' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000050' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000063' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000059' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000054' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000053' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='RO:0002325' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000067' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000070' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  termset AS (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_qualifier'),
  termid AS (SELECT id FROM vocabularyterm WHERE name='FBcv:0000171' AND vocabulary_id = (SELECT id from vocabulary WHERE vocabularylabel= 'spatial_expression_qualifiers'))
SELECT termset.id, termid.id from termset, termid;
