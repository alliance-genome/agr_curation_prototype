CREATE TABLE gene_note (
	gene_id bigint NOT NULL,
	relatednotes_id bigint NOT NULL
);

ALTER TABLE gene_note
	ADD CONSTRAINT gene_relatednotes_id_uk UNIQUE (relatednotes_id);

ALTER TABLE gene_note
	ADD CONSTRAINT gene_note_relatednotes_id_fk FOREIGN KEY (relatednotes_id) REFERENCES note(id);

ALTER TABLE gene_note
	ADD CONSTRAINT gene_note_gene_id_fk FOREIGN KEY (gene_id) REFERENCES gene(id);

CREATE INDEX gene_note_gene_index ON gene_note USING btree (gene_id);

CREATE INDEX gene_note_relatednotes_index ON gene_note USING btree (relatednotes_id);
