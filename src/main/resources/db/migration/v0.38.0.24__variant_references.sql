CREATE TABLE IF NOT EXISTS public.variant_reference
(
    variant_id bigint NOT NULL,
    references_id bigint NOT NULL,
    CONSTRAINT variant_reference_referencesid_fk FOREIGN KEY (references_id)
        REFERENCES public.reference (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT variant_reference_variantid_fk FOREIGN KEY (variant_id)
        REFERENCES public.variant (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE INDEX IF NOT EXISTS variant_reference_references_index ON public.variant_reference USING btree (references_id ASC NULLS LAST);
CREATE INDEX IF NOT EXISTS variant_reference_variant_index ON public.variant_reference USING btree (variant_id ASC NULLS LAST);

