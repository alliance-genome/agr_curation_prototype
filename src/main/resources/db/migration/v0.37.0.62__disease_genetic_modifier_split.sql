CREATE TABLE diseaseannotation_modifieragm (
	diseaseannotation_id bigint NOT NUlL,
	diseasegeneticmodifieragms_id bigint NOT NULL
	);
	
ALTER TABLE diseaseannotation_modifieragm
	ADD CONSTRAINT diseaseannotation_modifieragm_diseaseannotation_id_fk
	FOREIGN KEY (diseaseannotation_id) REFERENCES diseaseannotation (id);

ALTER TABLE diseaseannotation_modifieragm
	ADD CONSTRAINT diseaseannotation_modifieragm_dgmagms_id_fk
	FOREIGN KEY (diseasegeneticmodifieragms_id) REFERENCES affectedgenomicmodel (id);

INSERT INTO diseaseannotation_modifieragm (diseaseannotation_id, diseasegeneticmodifieragms_id)
	SELECT diseaseannotation_id, diseasegeneticmodifiers_id
	FROM diseaseannotation_biologicalentity
	WHERE diseasegeneticmodifiers_id IN (SELECT id FROM affectedgenomicmodel);

CREATE INDEX diseaseannotation_modifieragm_da_index ON diseaseannotation_modifieragm
	USING btree (diseaseannotation_id);

CREATE INDEX diseaseannotation_modifieragm_dgma_index ON diseaseannotation_modifieragm
	USING btree (diseasegeneticmodifieragms_id);

CREATE TABLE diseaseannotation_modifierallele (
	diseaseannotation_id bigint NOT NUlL,
	diseasegeneticmodifieralleles_id bigint NOT NULL
	);
	
ALTER TABLE diseaseannotation_modifierallele
	ADD CONSTRAINT diseaseannotation_modifierallele_diseaseannotation_id_fk
	FOREIGN KEY (diseaseannotation_id) REFERENCES diseaseannotation (id);

ALTER TABLE diseaseannotation_modifierallele
	ADD CONSTRAINT diseaseannotation_modifierallele_dgmalleles_id_fk
	FOREIGN KEY (diseasegeneticmodifieralleles_id) REFERENCES allele (id);

INSERT INTO diseaseannotation_modifierallele (diseaseannotation_id, diseasegeneticmodifieralleles_id)
	SELECT diseaseannotation_id, diseasegeneticmodifiers_id
	FROM diseaseannotation_biologicalentity
	WHERE diseasegeneticmodifiers_id IN (SELECT id FROM allele);

CREATE INDEX diseaseannotation_modifierallele_da_index ON diseaseannotation_modifierallele
	USING btree (diseaseannotation_id);

CREATE INDEX diseaseannotation_modifierallele_dgma_index ON diseaseannotation_modifierallele
	USING btree (diseasegeneticmodifieralleles_id);

CREATE TABLE diseaseannotation_modifiergene(
	diseaseannotation_id bigint NOT NUlL,
	diseasegeneticmodifiergenes_id bigint NOT NULL
	);
	
ALTER TABLE diseaseannotation_modifiergene
	ADD CONSTRAINT diseaseannotation_modifiergene_diseaseannotation_id_fk
	FOREIGN KEY (diseaseannotation_id) REFERENCES diseaseannotation (id);

ALTER TABLE diseaseannotation_modifiergene
	ADD CONSTRAINT diseaseannotation_modifiergene_dgmgenes_id_fk
	FOREIGN KEY (diseasegeneticmodifiergenes_id) REFERENCES gene (id);

INSERT INTO diseaseannotation_modifiergene (diseaseannotation_id, diseasegeneticmodifiergenes_id)
	SELECT diseaseannotation_id, diseasegeneticmodifiers_id
	FROM diseaseannotation_biologicalentity
	WHERE diseasegeneticmodifiers_id IN (SELECT id FROM gene);

CREATE INDEX diseaseannotation_modifiergene_da_index ON diseaseannotation_modifiergene
	USING btree (diseaseannotation_id);

CREATE INDEX diseaseannotation_modifiergene_dgmg_index ON diseaseannotation_modifiergene
	USING btree (diseasegeneticmodifiergenes_id);

DROP TABLE diseaseannotation_biologicalentity;