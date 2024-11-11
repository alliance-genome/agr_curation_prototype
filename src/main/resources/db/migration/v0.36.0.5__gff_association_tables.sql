CREATE SEQUENCE public.chromosome_seq         START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE chromosome (
	id bigint CONSTRAINT chromosome_pkey PRIMARY KEY,
	dataprovider_id bigint,
	taxon_id bigint,
	name varchar(255),
	datecreated timestamp without time zone,
 	dateupdated timestamp without time zone,
 	dbdatecreated timestamp without time zone,
	dbdateupdated timestamp without time zone,
 	internal boolean DEFAULT false,
 	obsolete boolean DEFAULT false,
 	createdby_id bigint,
 	updatedby_id bigint
);

ALTER TABLE chromosome ADD CONSTRAINT chromosome_dataprovider_id_fk
	FOREIGN KEY (dataprovider_id) REFERENCES dataprovider (id);
ALTER TABLE chromosome ADD CONSTRAINT chromosome_taxon_id_fk
	FOREIGN KEY (taxon_id) REFERENCES ontologyterm (id);
ALTER TABLE chromosome ADD CONSTRAINT chromosome_createdby_id_fk
	FOREIGN KEY (createdby_id) REFERENCES person (id);
ALTER TABLE chromosome ADD CONSTRAINT chromosome_updatedby_id_fk
	FOREIGN KEY (updatedby_id) REFERENCES person (id);
	
ALTER TABLE chromosome ADD CONSTRAINT chromosome_name_taxon_uk UNIQUE (name, taxon_id);

CREATE INDEX chromosome_name_index ON chromosome USING btree (name);
CREATE INDEX chromosome_dataprovider_index ON chromosome USING btree (dataprovider_id);
CREATE INDEX chromosome_taxon_index ON chromosome USING btree (taxon_id);
CREATE INDEX chromosome_createdby_index ON chromosome USING btree (createdby_id);
CREATE INDEX chromosome_updatedby_index ON chromosome USING btree (updatedby_id);
	

CREATE TABLE assemblycomponent (
    id bigint CONSTRAINT assemblycomponent_pkey PRIMARY KEY,
    genomeassembly_id bigint,
    mapstochromosome_id bigint,
    name varchar(255)
);

ALTER TABLE assemblycomponent ADD CONSTRAINT assemblycomponent_id_fk
	FOREIGN KEY (id) REFERENCES genomicentity(id);
ALTER TABLE assemblycomponent ADD CONSTRAINT assemblycomponent_genomeassembly_id_fk
	FOREIGN KEY (genomeassembly_id) REFERENCES genomeassembly(id);
ALTER TABLE assemblycomponent ADD CONSTRAINT assemblycomponent_mapstochromosome_id_fk
	FOREIGN KEY (mapstochromosome_id) REFERENCES chromosome(id);
	
CREATE INDEX assemblycomponent_genomeassembly_index ON assemblycomponent USING btree (genomeassembly_id);
   
CREATE TABLE codingsequencegenomiclocationassociation (
	id bigint CONSTRAINT codingsequencegenomiclocationassociation_pkey PRIMARY KEY,
	"start" integer,
	"end" integer,
	phase integer,
	strand varchar(1),
	relation_id bigint,
	codingsequenceassociationsubject_id bigint,
	codingsequencegenomiclocationassociationobject_id bigint
);

ALTER TABLE codingsequencegenomiclocationassociation ADD CONSTRAINT codingsequencegenomiclocationassociation_id_fk
	FOREIGN KEY (id) REFERENCES evidenceassociation(id);
ALTER TABLE codingsequencegenomiclocationassociation ADD CONSTRAINT codingsequencegenomiclocationassociation_relation_id_fk
	FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
ALTER TABLE codingsequencegenomiclocationassociation ADD CONSTRAINT codingsequencegenomiclocationassociation_cdsasubject_id_fk
	FOREIGN KEY (codingsequenceassociationsubject_id) REFERENCES codingsequence(id);
ALTER TABLE codingsequencegenomiclocationassociation ADD CONSTRAINT codingsequencegenomiclocationassociation_cdsglaobject_id_fk
	FOREIGN KEY (codingsequencegenomiclocationassociationobject_id) REFERENCES assemblycomponent(id);
	
CREATE INDEX codingsequencelocationassociation_relation_index ON codingsequencegenomiclocationassociation
	USING btree (relation_id);
CREATE INDEX codingsequencelocationassociation_subject_index ON codingsequencegenomiclocationassociation
	USING btree (codingsequenceassociationsubject_id);
CREATE INDEX codingsequencelocationassociation_object_index ON codingsequencegenomiclocationassociation
	USING btree (codingsequencegenomiclocationassociationobject_id);
	   
CREATE TABLE exongenomiclocationassociation (
	id bigint CONSTRAINT exongenomiclocationassociation_pkey PRIMARY KEY,
	"start" integer,
	"end" integer,
	strand varchar(1),
	relation_id bigint,
	exonassociationsubject_id bigint,
	exongenomiclocationassociationobject_id bigint
);

ALTER TABLE exongenomiclocationassociation ADD CONSTRAINT exongenomiclocationassociation_id_fk
	FOREIGN KEY (id) REFERENCES evidenceassociation(id);
ALTER TABLE exongenomiclocationassociation ADD CONSTRAINT exongenomiclocationassociation_relation_id_fk
	FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
ALTER TABLE exongenomiclocationassociation ADD CONSTRAINT exongenomiclocationassociation_easubject_id_fk
	FOREIGN KEY (exonassociationsubject_id) REFERENCES exon(id);
ALTER TABLE exongenomiclocationassociation ADD CONSTRAINT exongenomiclocationassociation_eglaobject_id_fk
	FOREIGN KEY (exongenomiclocationassociationobject_id) REFERENCES assemblycomponent(id);
	
CREATE INDEX exonlocationassociation_relation_index ON exongenomiclocationassociation
	USING btree (relation_id);
CREATE INDEX exonlocationassociation_subject_index ON exongenomiclocationassociation
	USING btree (exonassociationsubject_id);
CREATE INDEX exonlocationassociation_object_index ON exongenomiclocationassociation
	USING btree (exongenomiclocationassociationobject_id);
	   
CREATE TABLE transcriptgenomiclocationassociation (
	id bigint CONSTRAINT transcriptgenomiclocationassociation_pkey PRIMARY KEY,
	"start" integer,
	"end" integer,
	phase integer,
	strand varchar(1),
	relation_id bigint,
	transcriptassociationsubject_id bigint,
	transcriptgenomiclocationassociationobject_id bigint
);

ALTER TABLE transcriptgenomiclocationassociation ADD CONSTRAINT transcriptgenomiclocationassociation_id_fk
	FOREIGN KEY (id) REFERENCES evidenceassociation(id);
ALTER TABLE transcriptgenomiclocationassociation ADD CONSTRAINT transcriptgenomiclocationassociation_relation_id_fk
	FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
ALTER TABLE transcriptgenomiclocationassociation ADD CONSTRAINT transcriptgenomiclocationassociation_tasubject_id_fk
	FOREIGN KEY (transcriptassociationsubject_id) REFERENCES transcript(id);
ALTER TABLE transcriptgenomiclocationassociation ADD CONSTRAINT transcriptgenomiclocationassociation_tglaobject_id_fk
	FOREIGN KEY (transcriptgenomiclocationassociationobject_id) REFERENCES assemblycomponent(id);
	
CREATE INDEX transcriptlocationassociation_relation_index ON transcriptgenomiclocationassociation
	USING btree (relation_id);
CREATE INDEX transcriptlocationassociation_subject_index ON transcriptgenomiclocationassociation
	USING btree (transcriptassociationsubject_id);
CREATE INDEX transcriptlocationassociation_object_index ON transcriptgenomiclocationassociation
	USING btree (transcriptgenomiclocationassociationobject_id);
	
INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('vocabulary_seq'), 'Location Association Relation', 'location_association_relation');
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'located_on', id FROM vocabulary WHERE vocabularylabel = 'location_association_relation';
	