CREATE TABLE variant_synonyms (
    variant_id bigint,
    synonyms text
);

CREATE INDEX variant_synonyms_variant_index ON variant_synonyms USING btree (variant_id);

ALTER TABLE variant_synonyms ADD CONSTRAINT variant_synonyms_variant_id_fk FOREIGN KEY (variant_id) REFERENCES variant (id);
