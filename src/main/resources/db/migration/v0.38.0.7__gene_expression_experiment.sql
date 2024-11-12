
CREATE SEQUENCE public.geneexpressionexperiment_seq START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS public.geneexpressionexperiment (
	id BIGINT CONSTRAINT expressionexperiment_pkey PRIMARY KEY,
	uniqueid varchar(3500),
	curie VARCHAR(255),
	modentityid VARCHAR(255),
	modinternalid VARCHAR(255),
	singlereference_id BIGINT,
	entityassayed_id BIGINT,
	expressionassayused_id BIGINT,
	specimengenomicmodel_id BIGINT,
	dataprovider_id BIGINT,
	datecreated timestamp without time zone,
	dbdatecreated timestamp without time zone,
	dateupdated timestamp without time zone,
	dbdateupdated timestamp without time zone,
	internal boolean DEFAULT false,
	obsolete boolean DEFAULT false,
	createdby_id bigint,
	updatedby_id bigint
);

CREATE SEQUENCE public.geneexpressionexperiment_geneexpressionannotation_seq START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS public.geneexpressionexperiment_geneexpressionannotation (
    geneexpressionexperiment_id bigint NOT NULL,
    expressionannotations_id bigint NOT NULL,

	CONSTRAINT gen_exp_exp_experiment_fkey FOREIGN KEY (geneexpressionexperiment_id)
		REFERENCES public.geneexpressionexperiment (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT gen_exp_exp_annotation_fkey FOREIGN KEY (expressionannotations_id)
        REFERENCES public.geneexpressionannotation (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);
