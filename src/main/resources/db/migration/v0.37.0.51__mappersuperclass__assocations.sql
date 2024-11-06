CREATE TABLE agmdiseaseannotation (
	id bigint PRIMARY KEY,
	assertedallele_id bigint,
	diseaseannotationsubject_id bigint,
	inferredallele_id bigint,
	inferredgene_id bigint
);

CREATE TABLE agmdiseaseannotation_gene (
	agmdiseaseannotation_id bigint NOT NULL,
	assertedgenes_id bigint
	);

CREATE TABLE agmphenotypeannotation (
	id bigint PRIMARY KEY,
	assertedallele_id bigint,
	inferredallele_id bigint,
	inferredgene_id bigint,
	phenotypeannotationsubject_id bigint
);

CREATE TABLE agmphenotypeannotation_gene (
	agmphenotypeannotation_id bigint NOT NULL,
	assertedgenes_id bigint NOT NULL
);

CREATE TABLE allelediseaseannotation (
	id bigint PRIMARY KEY,
	diseaseannotationsubject_id bigint,
	inferredgene_id bigint
);

CREATE TABLE allelediseaseannotation_gene (
	allelediseaseannotation_id bigint NOT NULL,
	assertedgenes_id bigint
);

CREATE TABLE allelegeneassociation (
	id bigint PRIMARY KEY,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	createdby_id bigint,
	updatedby_id bigint,
	evidencecode_id bigint,
	relatednote_id bigint,
	relation_id bigint,
	alleleassociationsubject_id bigint,
	allelegeneassociationobject_id bigint
);

CREATE TABLE allelegeneassociation_informationcontententity (
	association_id bigint NOT NULL,
	evidence_id bigint NOT NULL
);

CREATE SEQUENCE allelegeneassociation_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

CREATE TABLE allelephenotypeannotation (
	id bigint PRIMARY KEY,
	inferredgene_id bigint,
	phenotypeannotationsubject_id bigint
);

CREATE TABLE allelephenotypeannotation_gene (
	allelephenotypeannotation_id bigint NOT NULL,
	assertedgenes_id bigint NOT NULL
);

CREATE TABLE codingsequencegenomiclocationassociation (
	id bigint PRIMARY KEY,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	"end" integer,
	start integer,
	phase integer,
	strand character varying(1),
	createdby_id bigint,
	updatedby_id bigint,
	relation_id bigint,
	codingsequenceassociationsubject_id bigint,
	codingsequencegenomiclocationassociationobject_id bigint
);

CREATE TABLE CodingSequenceGenomicLocationAssociation_InformationContentEntity (
	association_id bigint NOT NULL,
	evidence_id bigint NOT NULL
);

CREATE SEQUENCE codingsequencegenomiclocationassociation_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

CREATE TABLE constructgenomicentityassociation (
	id bigint PRIMARY KEY,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	createdby_id bigint,
	updatedby_id bigint,
	constructassociationsubject_id bigint,
	constructgenomicentityassociationobject_id bigint,
	relation_id bigint
);

CREATE TABLE constructgenomicentityassociation_informationcontententity (
	association_id bigint NOT NULL,
	evidence_id bigint NOT NULL
);

CREATE TABLE constructgenomicentityassociation_note (
	constructgenomicentityassociation_id bigint NOT NULL,
	relatednotes_id bigint NOT NULL
);

CREATE SEQUENCE constructgenomicentityassociation_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

CREATE TABLE exongenomiclocationassociation (
	id bigint PRIMARY KEY,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	"end" integer,
	start integer,
	strand character varying(1),
	createdby_id bigint,
	updatedby_id bigint,
	relation_id bigint,
	exonassociationsubject_id bigint,
	exongenomiclocationassociationobject_id bigint
);

CREATE TABLE exongenomiclocationassociation_informationcontententity (
	association_id bigint NOT NULL,
	evidence_id bigint NOT NULL
);

CREATE SEQUENCE exongenomiclocationassociation_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

CREATE TABLE diseaseannotation (
	id bigint PRIMARY KEY,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	curie character varying(255),
	modentityid character varying(255),
	modinternalid character varying(255),
	uniqueid character varying(3500),
	negated boolean DEFAULT false NOT NULL,
	createdby_id bigint,
	updatedby_id bigint,
	singlereference_id bigint,
	dataprovider_id bigint,
	annotationtype_id bigint,
	diseaseannotationobject_id bigint,
	diseasegeneticmodifierrelation_id bigint,
	geneticsex_id bigint,
	relation_id bigint,
	secondarydataprovider_id bigint
);

CREATE TABLE genediseaseannotation (
	id bigint PRIMARY KEY,
	diseaseannotationsubject_id bigint,
	sgdstrainbackground_id bigint
);

CREATE TABLE diseaseannotation_biologicalentity (
	diseaseannotation_id bigint NOT NULL,
	diseasegeneticmodifiers_id bigint NOT NULL
);

CREATE TABLE diseaseannotation_conditionrelation (
	annotation_id bigint NOT NULL,
	conditionrelations_id bigint NOT NULL
);

CREATE TABLE diseaseannotation_gene (
	diseaseannotation_id bigint NOT NULL,
	with_id bigint NOT NULL
);

CREATE TABLE diseaseannotation_note (
	annotation_id bigint NOT NULL,
	relatednotes_id bigint NOT NULL
);

CREATE TABLE diseaseannotation_ontologyterm (
	diseaseannotation_id bigint NOT NULL,
	evidencecodes_id bigint NOT NULL
);

CREATE SEQUENCE diseaseannotation_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

CREATE TABLE diseaseannotation_vocabularyterm (
	diseaseannotation_id bigint NOT NULL,
	diseasequalifiers_id bigint NOT NULL
);

CREATE TABLE geneexpressionannotation (
	id bigint PRIMARY KEY,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	curie character varying(255),
	modentityid character varying(255),
	modinternalid character varying(255),
	uniqueid character varying(3500),
	whenexpressedstagename character varying(2000),
	whereexpressedstatement character varying(2000),
	createdby_id bigint,
	updatedby_id bigint,
	singlereference_id bigint,
	dataprovider_id bigint,
	expressionpattern_id bigint,
	relation_id bigint,
	expressionannotationsubject_id bigint,
	expressionassayused_id bigint
);

CREATE TABLE geneexpressionannotation_conditionrelation (
	annotation_id bigint NOT NULL,
	conditionrelations_id bigint NOT NULL
);

CREATE TABLE geneexpressionannotation_note (
	annotation_id bigint NOT NULL,
	relatednotes_id bigint NOT NULL
);

CREATE TABLE genegeneticinteraction (
	id bigint PRIMARY KEY,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	interactionid character varying(255),
	uniqueid character varying(2000),
	createdby_id bigint,
	updatedby_id bigint,
	geneassociationsubject_id bigint,
	genegeneassociationobject_id bigint,
	relation_id bigint,
	interactionsource_id bigint,
	interactiontype_id bigint,
	interactorarole_id bigint,
	interactoratype_id bigint,
	interactorbrole_id bigint,
	interactorbtype_id bigint,
	interactorageneticperturbation_id bigint,
	interactorbgeneticperturbation_id bigint
);

CREATE TABLE genegeneticinteraction_crossreference (
	geneinteraction_id bigint NOT NULL,
	crossreferences_id bigint NOT NULL
);

CREATE TABLE genegeneticinteraction_informationcontententity (
	association_id bigint NOT NULL,
	evidence_id bigint NOT NULL
);

DROP TABLE IF EXISTS genegeneticinteraction_phenotypesortraits;

CREATE TABLE genegeneticinteraction_phenotypesortraits (
	genegeneticinteraction_id bigint NOT NULL,
	phenotypesortraits character varying(255)
);

CREATE SEQUENCE genegeneticinteraction_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

CREATE TABLE genemolecularinteraction (
	id bigint PRIMARY KEY,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	interactionid character varying(255),
	uniqueid character varying(2000),
	createdby_id bigint,
	updatedby_id bigint,
	geneassociationsubject_id bigint,
	genegeneassociationobject_id bigint,
	relation_id bigint,
	interactionsource_id bigint,
	interactiontype_id bigint,
	interactorarole_id bigint,
	interactoratype_id bigint,
	interactorbrole_id bigint,
	interactorbtype_id bigint,
	aggregationdatabase_id bigint,
	detectionmethod_id bigint
);

CREATE TABLE genemolecularinteraction_crossreference (
	geneinteraction_id bigint NOT NULL,
	crossreferences_id bigint NOT NULL
);

CREATE TABLE genemolecularinteraction_informationcontententity (
	association_id bigint NOT NULL,
	evidence_id bigint NOT NULL
);

CREATE SEQUENCE genemolecularinteraction_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

CREATE TABLE phenotypeannotation (
	id bigint PRIMARY KEY,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	curie character varying(255),
	modentityid character varying(255),
	modinternalid character varying(255),
	uniqueid character varying(3500),
	phenotypeannotationobject character varying(255),
	createdby_id bigint,
	updatedby_id bigint,
	singlereference_id bigint,
	dataprovider_id bigint,
	crossreference_id bigint,
	relation_id bigint
);

CREATE TABLE genephenotypeannotation (
	id bigint PRIMARY KEY,
	phenotypeannotationsubject_id bigint,
	sgdstrainbackground_id bigint
);

CREATE TABLE phenotypeannotation_conditionrelation (
	annotation_id bigint NOT NULL,
	conditionrelations_id bigint NOT NULL
);

CREATE TABLE phenotypeannotation_note (
	annotation_id bigint NOT NULL,
	relatednotes_id bigint NOT NULL
);

CREATE TABLE phenotypeannotation_ontologyterm (
	phenotypeannotation_id bigint NOT NULL,
	phenotypeterms_id bigint NOT NULL
);

CREATE SEQUENCE phenotypeannotation_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

CREATE TABLE sequencetargetingreagentgeneassociation (
	id bigint PRIMARY KEY,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	createdby_id bigint,
	updatedby_id bigint,
	relation_id bigint,
	sequencetargetingreagentassociationsubject_id bigint,
	sequencetargetingreagentgeneassociationobject_id bigint
);

CREATE TABLE sequencetargetingreagentgeneassociation_informationcontententit (
	association_id bigint NOT NULL,
	evidence_id bigint NOT NULL
);

CREATE SEQUENCE sequencetargetingreagentgeneassociation_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

CREATE TABLE transcriptcodingsequenceassociation (
	id bigint PRIMARY KEY,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	createdby_id bigint,
	updatedby_id bigint,
	relation_id bigint,
	transcriptassociationsubject_id bigint,
	transcriptcodingsequenceassociationobject_id bigint
);

CREATE TABLE transcriptcodingsequenceassociation_informationcontententity (
	association_id bigint NOT NULL,
	evidence_id bigint NOT NULL
);

CREATE SEQUENCE transcriptcodingsequenceassociation_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

CREATE TABLE transcriptexonassociation (
	id bigint PRIMARY KEY,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	createdby_id bigint,
	updatedby_id bigint,
	relation_id bigint,
	transcriptassociationsubject_id bigint,
	transcriptexonassociationobject_id bigint
);

CREATE TABLE transcriptexonassociation_informationcontententity (
	association_id bigint NOT NULL,
	evidence_id bigint NOT NULL
);

CREATE SEQUENCE transcriptexonassociation_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

CREATE TABLE transcriptgeneassociation (
	id bigint PRIMARY KEY,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	createdby_id bigint,
	updatedby_id bigint,
	relation_id bigint,
	transcriptassociationsubject_id bigint,
	transcriptgeneassociationobject_id bigint
);

CREATE TABLE transcriptgeneassociation_informationcontententity (
	association_id bigint NOT NULL,
	evidence_id bigint NOT NULL
);

CREATE SEQUENCE transcriptgeneassociation_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

CREATE TABLE transcriptgenomiclocationassociation (
	id bigint PRIMARY KEY,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	"end" integer,
	start integer,
	phase integer,
	strand character varying(1),
	createdby_id bigint,
	updatedby_id bigint,
	relation_id bigint,
	transcriptassociationsubject_id bigint,
	transcriptgenomiclocationassociationobject_id bigint
);

CREATE TABLE transcriptgenomiclocationassociation_informationcontententity (
	association_id bigint NOT NULL,
	evidence_id bigint NOT NULL
);

CREATE SEQUENCE transcriptgenomiclocationassociation_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;
