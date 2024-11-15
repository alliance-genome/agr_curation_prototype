CREATE SEQUENCE geneexpressionexperiment_seq START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE geneexpressionexperiment (
	id BIGINT CONSTRAINT expressionexperiment_pkey PRIMARY KEY,
	uniqueid varchar(3500),
	curie VARCHAR(255),
	modentityid VARCHAR(255),
	modinternalid VARCHAR(255),
	singlereference_id BIGINT REFERENCES public.reference (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
	entityassayed_id BIGINT  REFERENCES public.gene (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
	expressionassayused_id BIGINT  REFERENCES public.ontologyterm (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
	specimengenomicmodel_id BIGINT REFERENCES public.allele (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
	dataprovider_id BIGINT REFERENCES public.dataprovider (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
	datecreated timestamp without time zone,
	dbdatecreated timestamp without time zone,
	dateupdated timestamp without time zone,
	dbdateupdated timestamp without time zone,
	internal boolean DEFAULT false,
	obsolete boolean DEFAULT false,
	createdby_id bigint REFERENCES public.person (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
	updatedby_id bigint REFERENCES public.person (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE INDEX geneexpressionexperiment_uniqueid_index ON geneexpressionexperiment USING btree (uniqueid);
CREATE INDEX geneexpressionexperiment_curie_index ON geneexpressionexperiment USING btree (curie);
CREATE INDEX geneexpressionexperiment_modentityid_index ON geneexpressionexperiment USING btree (modentityid);
CREATE INDEX geneexpressionexperiment_modinternalid_index ON geneexpressionexperiment USING btree (modinternalid);
CREATE INDEX geneexpressionexperiment_singlereference_index ON geneexpressionexperiment USING btree (singlereference_id);
CREATE INDEX geneexpressionexperiment_entityassayedused_index ON geneexpressionexperiment USING btree (entityassayed_id);
CREATE INDEX geneexpressionexperiment_expressionassayused_index ON geneexpressionexperiment USING btree (expressionassayused_id);
CREATE INDEX geneexpressionexperiment_dataprovider_index ON geneexpressionexperiment USING btree (dataprovider_id);
CREATE INDEX geneexpressionexperiment_internal_index ON geneexpressionexperiment USING btree (internal);
CREATE INDEX geneexpressionexperiment_obsolete_index ON geneexpressionexperiment USING btree (obsolete);
CREATE INDEX geneexpressionexperiment_createdby_index ON geneexpressionexperiment USING btree (createdby_id);
CREATE INDEX geneexpressionexperiment_updatedby_index ON geneexpressionexperiment USING btree (updatedby_id);

CREATE SEQUENCE geneexpressionexperiment_geneexpressionannotation_seq START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS geneexpressionexperiment_geneexpressionannotation (
    geneexpressionexperiment_id bigint NOT NULL,
    expressionannotations_id bigint NOT NULL,

	CONSTRAINT geexperiment_geannotation_geexperiment_id_fk FOREIGN KEY (geneexpressionexperiment_id) REFERENCES geneexpressionexperiment (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT geexperiment_geannotation_geannotation_id_fk FOREIGN KEY (expressionannotations_id)  REFERENCES geneexpressionannotation (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE INDEX geneexpressionexperiment_gea_experiment_index ON geneexpressionexperiment_geneexpressionannotation USING btree (geneexpressionexperiment_id);
CREATE INDEX geneexpressionexperiment_gea_annotations_index ON geneexpressionexperiment_geneexpressionannotation USING btree (expressionannotations_id);
