CREATE TABLE affectedgenomicmodel_synonyms (
    affectedgenomicmodel_id bigint,
    synonyms text
);

CREATE INDEX affectedgenomicmodel_synonym_affectedgenomicmodel_index ON affectedgenomicmodel_synonyms USING btree (affectedgenomicmodel_id);

ALTER TABLE affectedgenomicmodel_synonyms ADD CONSTRAINT affectedgenomicmodel_synonym_affectedgenomicmodel_id_fk FOREIGN KEY (affectedgenomicmodel_id) REFERENCES affectedgenomicmodel (id);
