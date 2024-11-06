CREATE SEQUENCE IF NOT EXISTS geneexpressionannotation_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;
	
SELECT setval( 'GeneExpressionAnnotation_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM GeneExpressionAnnotation) );

