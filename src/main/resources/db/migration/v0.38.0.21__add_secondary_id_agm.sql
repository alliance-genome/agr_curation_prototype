alter table slotannotation add column
singleagm_id bigint

CREATE INDEX slotannotation_singleagm_index ON slotannotation USING btree (singleagm_id);
