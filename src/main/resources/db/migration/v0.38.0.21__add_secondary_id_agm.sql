alter table slotannotation add column
singleagm_id bigint;

CREATE INDEX slotannotation_singleagm_index ON slotannotation USING btree (singleagm_id);

ALTER TABLE SlotAnnotation ADD CONSTRAINT slotannotation_singleagm_id_fk FOREIGN KEY (singleagm_id) REFERENCES affectedgenomicmodel(id);
