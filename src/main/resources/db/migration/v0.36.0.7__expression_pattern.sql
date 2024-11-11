
CREATE SEQUENCE public.expressionpattern_seq START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.temporalcontext_seq START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.anatomicalsite_seq START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE expressionpattern (
	id BIGINT CONSTRAINT expressionpattern_pkey PRIMARY KEY,
	whenexpressed_id BIGINT,
	whereexpressed_id BIGINT,
	datecreated timestamp without time zone,
	dbdatecreated timestamp without time zone,
	dateupdated timestamp without time zone,
	dbdateupdated timestamp without time zone,
	internal boolean DEFAULT false,
	obsolete boolean DEFAULT false,
	createdby_id bigint,
	updatedby_id bigint
);

CREATE TABLE temporalcontext (
	id BIGINT CONSTRAINT temporalcontext_pkey PRIMARY KEY,
	developmentalstagestart_id BIGINT,
	developmentalstagestop_id BIGINT,
	age VARCHAR(1000),
	datecreated timestamp without time zone,
	dbdatecreated timestamp without time zone,
	dateupdated timestamp without time zone,
	dbdateupdated timestamp without time zone,
	internal boolean DEFAULT false,
    obsolete boolean DEFAULT false,
  	createdby_id bigint,
  	updatedby_id bigint
);

CREATE TABLE anatomicalsite (
	id BIGINT CONSTRAINT anatomicalsite_pkey PRIMARY KEY,
	anatomicalstructure_id BIGINT,
	anatomicalsubstructure_id BIGINT,
	cellularcomponentterm_id BIGINT,
	datecreated timestamp without time zone,
	dbdatecreated timestamp without time zone,
	dateupdated timestamp without time zone,
	dbdateupdated timestamp without time zone,
	internal boolean DEFAULT false,
	obsolete boolean DEFAULT false,
  	createdby_id bigint,
  	updatedby_id bigint
);

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

CREATE TABLE anatomicalsite_anatomicalstructureuberonterms (
	anatomicalsite_id BIGINT,
	anatomicalstructureuberonterms_id BIGINT
);

CREATE TABLE anatomicalsite_anatomicalsubstructureuberonterms (
	anatomicalsite_id BIGINT,
	anatomicalsubstructureuberonterms_id BIGINT
);

CREATE TABLE temporalcontext_stageuberonslimterms (
	temporalcontext_id BIGINT,
	stageuberonslimterms_id BIGINT
);

CREATE TABLE temporalcontext_temporalqualifiers (
	temporalcontext_id BIGINT,
	temporalqualifiers_id BIGINT
);

ALTER TABLE expressionannotation
    ADD COLUMN expressionpattern_id BIGINT;

ALTER TABLE expressionannotation
    ADD CONSTRAINT  expressionannotationexpressionpattern_fk
		FOREIGN KEY (expressionpattern_id) REFERENCES expressionpattern(id);

ALTER TABLE expressionpattern
	ADD CONSTRAINT expressionpattern_whenexpressed_fk
		FOREIGN KEY (whenexpressed_id) REFERENCES temporalcontext(id);

ALTER TABLE expressionpattern
	ADD CONSTRAINT expressionpattern_whereexpressed_fk
		FOREIGN KEY (whereexpressed_id) REFERENCES anatomicalsite(id);

ALTER TABLE anatomicalsite_anatomicalstructurequalifiers
	ADD CONSTRAINT anatomicalstructurequalifiers_anatomicalsite_fk
		FOREIGN KEY (anatomicalsite_id) REFERENCES anatomicalsite(id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE anatomicalsite_anatomicalstructurequalifiers
	ADD CONSTRAINT anatomicalstructurequalifiers_structurequalifier_fk
		FOREIGN KEY (anatomicalstructurequalifiers_id) REFERENCES vocabularyterm(id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE anatomicalsite_anatomicalsubstructurequalifiers
	ADD CONSTRAINT anatomicalsubstructurequalifiers_anatomicalsite_fk
		FOREIGN KEY (anatomicalsite_id) REFERENCES anatomicalsite(id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE anatomicalsite_anatomicalsubstructurequalifiers
	ADD CONSTRAINT anatomicalsubstructurequalifiers_qualifier_fk
		FOREIGN KEY (anatomicalsubstructurequalifiers_id) REFERENCES vocabularyterm(id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE anatomicalsite_cellularcomponentqualifiers
	ADD CONSTRAINT cellularcomponentqualifiers_anatomicalsite_fk
		FOREIGN KEY (anatomicalsite_id) REFERENCES anatomicalsite(id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE anatomicalsite_cellularcomponentqualifiers
	ADD CONSTRAINT cellularcomponentqualifiers_cellularcomponentqualifier_fk
		FOREIGN KEY (cellularcomponentqualifiers_id) REFERENCES vocabularyterm(id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE anatomicalsite_anatomicalstructureuberonterms
	ADD CONSTRAINT anatomicalstructureuberonterms_anatomicalsite_fk
		FOREIGN KEY (anatomicalsite_id) REFERENCES anatomicalsite(id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE anatomicalsite_anatomicalstructureuberonterms
	ADD CONSTRAINT anatomicalstructureuberonterms_uberonterm_fk
		FOREIGN KEY (anatomicalstructureuberonterms_id) REFERENCES ontologyterm(id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE anatomicalsite_anatomicalsubstructureuberonterms
	ADD CONSTRAINT anatomicalsubstructureuberonterms_anatomicalsite_fk
		FOREIGN KEY (anatomicalsite_id) REFERENCES anatomicalsite(id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE anatomicalsite_anatomicalsubstructureuberonterms
	ADD CONSTRAINT anatomicalsubstructureuberonterms_uberonterm_fk
		FOREIGN KEY (anatomicalsubstructureuberonterms_id) REFERENCES ontologyterm(id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE temporalcontext_stageuberonslimterms
	ADD CONSTRAINT stageuberonterms_temporalcontext_fk
		FOREIGN KEY (temporalcontext_id) REFERENCES temporalcontext(id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE temporalcontext_stageuberonslimterms
	ADD CONSTRAINT stageuberonslimterms_uberonterm_fk
		FOREIGN KEY (stageuberonslimterms_id) REFERENCES vocabularyterm(id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE temporalcontext_temporalqualifiers
	ADD CONSTRAINT temporalqualifiers_temporalcontext_fk
		FOREIGN KEY (temporalcontext_id) REFERENCES temporalcontext(id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE temporalcontext_temporalqualifiers
	ADD CONSTRAINT temporalqualifiers_temporqualifiers_fk
		FOREIGN KEY (temporalqualifiers_id) REFERENCES vocabularyterm(id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;


CREATE INDEX expressionannotation_expressionpattern_index ON expressionannotation USING btree (expressionpattern_id);

CREATE INDEX expressionpattern_whenexpressed_index ON expressionpattern USING btree (whenexpressed_id);
CREATE INDEX expressionpattern_whereexpressed_index ON expressionpattern USING btree (whereexpressed_id);

CREATE INDEX temporalcontext_developmentalstagestart_index ON temporalcontext USING btree (developmentalstagestart_id);
CREATE INDEX temporalcontext_developmentalstagestop_index ON temporalcontext USING btree (developmentalstagestop_id);
CREATE INDEX temporalcontext_age_index ON temporalcontext USING btree (age);
CREATE INDEX temporalqualifiers_temporalcontext_index ON temporalcontext_temporalqualifiers USING btree (temporalcontext_id);
CREATE INDEX temporalqualifiers_temporalqualifiers_index ON temporalcontext_temporalqualifiers USING btree (temporalqualifiers_id);
CREATE INDEX stageuberonslimterms_temporalcontext_index ON temporalcontext_stageuberonslimterms USING btree (temporalcontext_id);
CREATE INDEX stageuberonslimterms_uberonterms_index ON temporalcontext_stageuberonslimterms USING btree (stageuberonslimterms_id);

CREATE INDEX anatomicalsite_anatomicalstructure_index ON anatomicalsite USING btree (anatomicalstructure_id);
CREATE INDEX anatomicalsite_anatomicalsubstructure_index ON anatomicalsite USING btree (anatomicalsubstructure_id);
CREATE INDEX anatomicalsite_cellularcomponentterm_index ON anatomicalsite USING btree (cellularcomponentterm_id);
CREATE INDEX anatomicalstructurequalifiers_anatomicalsite_index ON anatomicalsite_anatomicalstructurequalifiers USING btree (anatomicalsite_id);
CREATE INDEX anatomicalstructurequalifiers_structurequalifiers_index ON anatomicalsite_anatomicalstructurequalifiers USING btree (anatomicalstructurequalifiers_id);
CREATE INDEX anatomicalsubstructurequalifiers_anatomicalsite_index ON anatomicalsite_anatomicalsubstructurequalifiers USING btree (anatomicalsite_id);
CREATE INDEX anatomicalsubstructurequalifiers_qualifiers_index ON anatomicalsite_anatomicalsubstructurequalifiers USING btree (anatomicalsubstructurequalifiers_id);
CREATE INDEX cellularcomponentqualifiers_anatomicalsite_index ON anatomicalsite_cellularcomponentqualifiers USING btree (anatomicalsite_id);
CREATE INDEX cellularcomponentqualifiers_qualifiers_index ON anatomicalsite_cellularcomponentqualifiers USING btree (cellularcomponentqualifiers_id);
CREATE INDEX anatomicalstructureuberonterms_anatomicalsite_index ON anatomicalsite_anatomicalstructureuberonterms USING btree (anatomicalsite_id);
CREATE INDEX anatomicalstructureuberonterms_uberonterms_index ON anatomicalsite_anatomicalstructureuberonterms USING btree (anatomicalstructureuberonterms_id);
CREATE INDEX anatomicalsubstructureuberonterms_anatomicalsite_index ON anatomicalsite_anatomicalsubstructureuberonterms USING btree (anatomicalsite_id);
CREATE INDEX anatomicalsubstructureuberonterms_uberonterms_index ON anatomicalsite_anatomicalsubstructureuberonterms USING btree (anatomicalsubstructureuberonterms_id);


INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('vocabulary_seq'), 'Stage Uberon Slim Terms', 'stage_uberon_slim_terms');
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'post embryonic', id FROM vocabulary WHERE vocabularylabel = 'stage_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'pre-adult', id FROM vocabulary WHERE vocabularylabel = 'stage_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'UBERON:0000113', id FROM vocabulary WHERE vocabularylabel = 'stage_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'UBERON:0000068', id FROM vocabulary WHERE vocabularylabel = 'stage_uberon_slim_terms';

INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('vocabulary_seq'), 'Anatomical Structure Uberon Slim Terms', 'anatomical_structure_uberon_slim_terms');
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'UBERON:0001009', id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'UBERON:0005409', id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'UBERON:0000949', id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'UBERON:0001008', id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'UBERON:0002330', id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'UBERON:0002193', id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'UBERON:0002416', id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'UBERON:0002423', id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'UBERON:0002204', id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'UBERON:0001016', id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'UBERON:0000990', id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'UBERON:0001004', id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'UBERON:0001032', id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'UBERON:0005726', id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'UBERON:0007037', id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'UBERON:0002105', id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'UBERON:0002104', id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'UBERON:0000924', id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'UBERON:0000925', id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'UBERON:0000926', id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'UBERON:0003104', id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'UBERON:0001013', id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'UBERON:0000026', id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'UBERON:0016887', id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'UBERON:6005023', id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'UBERON:0002539', id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'Other', id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms';

INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('vocabulary_seq'), 'Cellular Component Qualifiers', 'cellular_component_qualifiers');
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000046', id FROM vocabulary WHERE vocabularylabel = 'cellular_component_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000047', id FROM vocabulary WHERE vocabularylabel = 'cellular_component_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000048', id FROM vocabulary WHERE vocabularylabel = 'cellular_component_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000050', id FROM vocabulary WHERE vocabularylabel = 'cellular_component_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000053', id FROM vocabulary WHERE vocabularylabel = 'cellular_component_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000054', id FROM vocabulary WHERE vocabularylabel = 'cellular_component_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000055', id FROM vocabulary WHERE vocabularylabel = 'cellular_component_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000058', id FROM vocabulary WHERE vocabularylabel = 'cellular_component_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000059', id FROM vocabulary WHERE vocabularylabel = 'cellular_component_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000063', id FROM vocabulary WHERE vocabularylabel = 'cellular_component_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000065', id FROM vocabulary WHERE vocabularylabel = 'cellular_component_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000066', id FROM vocabulary WHERE vocabularylabel = 'cellular_component_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000067', id FROM vocabulary WHERE vocabularylabel = 'cellular_component_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000070', id FROM vocabulary WHERE vocabularylabel = 'cellular_component_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000167', id FROM vocabulary WHERE vocabularylabel = 'cellular_component_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000168', id FROM vocabulary WHERE vocabularylabel = 'cellular_component_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000169', id FROM vocabulary WHERE vocabularylabel = 'cellular_component_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000170', id FROM vocabulary WHERE vocabularylabel = 'cellular_component_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000171', id FROM vocabulary WHERE vocabularylabel = 'cellular_component_qualifiers';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'FBcv:0000653', id FROM vocabulary WHERE vocabularylabel = 'cellular_component_qualifiers';
