ALTER TABLE allele
	ADD COLUMN inheritancemode_id bigint,
	ADD COLUMN incollection_id bigint,
	ADD COLUMN sequencingstatus_id bigint,
	ADD COLUMN isextinct boolean;
	
ALTER TABLE allele
	ADD CONSTRAINT allele_inheritancemode_id_fk
		FOREIGN KEY (inheritancemode_id) REFERENCES vocabularyterm (id);
	
ALTER TABLE allele
	ADD CONSTRAINT allele_incollection_id_fk
		FOREIGN KEY (incollection_id) REFERENCES vocabularyterm (id);
	
ALTER TABLE allele
	ADD CONSTRAINT allele_sequencingstatus_id_fk
		FOREIGN KEY (sequencingstatus_id) REFERENCES vocabularyterm (id);
		
ALTER TABLE allele_aud
	ADD COLUMN inheritancemode_id bigint,
	ADD COLUMN incollection_id bigint,
	ADD COLUMN sequencingstatus_id bigint,
	ADD COLUMN isextinct boolean;
	
CREATE TABLE allele_reference (
	allele_curie varchar(255) NOT NULL,
	references_curie varchar(255) NOT NULL
);
	
ALTER TABLE allele_reference
	ADD CONSTRAINT allele_reference_allele_curie_fk
		FOREIGN KEY (allele_curie) REFERENCES allele (curie);
	
ALTER TABLE allele_reference
	ADD CONSTRAINT allele_reference_references_curie_fk
		FOREIGN KEY (references_curie) REFERENCES reference (curie);
		
CREATE TABLE allele_reference_aud (
	rev integer NOT NULL,
	allele_curie varchar(255) NOT NULL,
	references_curie varchar(255) NOT NULL,
	revtype smallint,
	CONSTRAINT allele_reference_aud_pkey PRIMARY KEY(allele_curie, references_curie, rev)
);
	