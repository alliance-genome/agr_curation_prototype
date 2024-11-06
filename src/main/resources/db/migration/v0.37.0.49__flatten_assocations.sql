CREATE TABLE association_gene (
    agmdiseaseannotation_id bigint,
    assertedgenes_id bigint,
    allelediseaseannotation_id bigint,
    diseaseannotation_id bigint,
    with_id bigint,
    allelephenotypeannotation_id bigint,
    agmphenotypeannotation_id bigint
);

CREATE TABLE association_biologicalentity (
    diseaseannotation_id bigint NOT NULL,
    diseasegeneticmodifiers_id bigint NOT NULL
);

CREATE TABLE association_conditionrelation (
    annotation_id bigint NOT NULL,
    conditionrelations_id bigint NOT NULL
);

CREATE TABLE association_crossreference (
    geneinteraction_id bigint NOT NULL,
    crossreferences_id bigint NOT NULL
);

CREATE TABLE association_informationcontententity (
    evidenceassociation_id bigint NOT NULL,
    evidence_id bigint NOT NULL
);

CREATE TABLE association_note (
    annotation_id bigint,
    relatednotes_id bigint,
    constructgenomicentityassociation_id bigint
);

CREATE TABLE association_ontologyterm (
    diseaseannotation_id bigint,
    evidencecodes_id bigint,
    phenotypeannotation_id bigint,
    phenotypeterms_id bigint
);

CREATE TABLE association_vocabularyterm (
    diseaseannotation_id bigint NOT NULL,
    diseasequalifiers_id bigint NOT NULL
);

ALTER TABLE association
	ADD COLUMN curie character varying(255),
	ADD COLUMN modentityid character varying(255),
	ADD COLUMN modinternalid character varying(255),
	ADD COLUMN uniqueid character varying(3500),
	ADD COLUMN phenotypeannotationobject character varying(255),
	ADD COLUMN whenexpressedstagename character varying(2000),
	ADD COLUMN whereexpressedstatement character varying(2000),
	ADD COLUMN "end" integer,
	ADD COLUMN start integer,
	ADD COLUMN strand character varying(1),
	ADD COLUMN negated boolean DEFAULT false NOT NULL,
	ADD COLUMN interactionid character varying(255),
	ADD COLUMN phase integer,
	ADD COLUMN constructassociationsubject_id bigint,
	ADD COLUMN constructgenomicentityassociationobject_id bigint,
	ADD COLUMN relation_id bigint,
	ADD COLUMN singlereference_id bigint,
	ADD COLUMN dataprovider_id bigint,
	ADD COLUMN crossreference_id bigint,
	ADD COLUMN assertedallele_id bigint,
	ADD COLUMN inferredallele_id bigint,
	ADD COLUMN inferredgene_id bigint,
	ADD COLUMN phenotypeannotationsubject_id bigint,
	ADD COLUMN sgdstrainbackground_id bigint,
	ADD COLUMN expressionpattern_id bigint,
	ADD COLUMN expressionannotationsubject_id bigint,
	ADD COLUMN expressionassayused_id bigint,
	ADD COLUMN sequencetargetingreagentassociationsubject_id bigint,
	ADD COLUMN sequencetargetingreagentgeneassociationobject_id bigint,
	ADD COLUMN evidencecode_id bigint,
	ADD COLUMN relatednote_id bigint,
	ADD COLUMN geneassociationsubject_id bigint,
	ADD COLUMN genegeneassociationobject_id bigint,
	ADD COLUMN transcriptassociationsubject_id bigint,
	ADD COLUMN transcriptgeneassociationobject_id bigint,
	ADD COLUMN exonassociationsubject_id bigint,
	ADD COLUMN exongenomiclocationassociationobject_id bigint,
	ADD COLUMN annotationtype_id bigint,
	ADD COLUMN diseaseannotationobject_id bigint,
	ADD COLUMN diseasegeneticmodifierrelation_id bigint,
	ADD COLUMN geneticsex_id bigint,
	ADD COLUMN secondarydataprovider_id bigint,
	ADD COLUMN diseaseannotationsubject_id bigint,
	ADD COLUMN interactionsource_id bigint,
	ADD COLUMN interactiontype_id bigint,
	ADD COLUMN interactorarole_id bigint,
	ADD COLUMN interactoratype_id bigint,
	ADD COLUMN interactorbrole_id bigint,
	ADD COLUMN interactorbtype_id bigint,
	ADD COLUMN interactorageneticperturbation_id bigint,
	ADD COLUMN interactorbgeneticperturbation_id bigint,
	ADD COLUMN alleleassociationsubject_id bigint,
	ADD COLUMN allelegeneassociationobject_id bigint,
	ADD COLUMN transcriptexonassociationobject_id bigint,
	ADD COLUMN transcriptgenomiclocationassociationobject_id bigint,
	ADD COLUMN codingsequenceassociationsubject_id bigint,
	ADD COLUMN codingsequencegenomiclocationassociationobject_id bigint,
	ADD COLUMN aggregationdatabase_id bigint,
	ADD COLUMN detectionmethod_id bigint,
	ADD COLUMN transcriptcodingsequenceassociationobject_id bigint,
	ADD COLUMN associationtype character varying(96);

UPDATE association a SET
	associationtype = 'AGMDiseaseAnnotation',
	inferredallele_id = b.inferredallele_id,
	inferredgene_id = b.inferredgene_id,
	assertedallele_id = b.assertedallele_id,
	diseaseannotationsubject_id = b.diseaseannotationsubject_id
FROM AGMDiseaseAnnotation b where a.id = b.id;

INSERT INTO association_gene (agmdiseaseannotation_id, assertedgenes_id)
	SELECT agmdiseaseannotation_id, assertedgenes_id FROM agmdiseaseannotation_gene;

DROP TABLE AGMDiseaseAnnotation_Gene;
DROP TABLE AGMDiseaseAnnotation;

UPDATE association a SET
	associationtype = 'AlleleDiseaseAnnotation',
	inferredgene_id = b.inferredgene_id,
	diseaseannotationsubject_id = b.diseaseannotationsubject_id
FROM AlleleDiseaseAnnotation b where a.id = b.id;

INSERT INTO association_gene (allelediseaseannotation_id, assertedgenes_id) 
	SELECT allelediseaseannotation_id, assertedgenes_id FROM allelediseaseannotation_gene;

DROP TABLE allelediseaseannotation_gene;
DROP TABLE AlleleDiseaseAnnotation;

UPDATE association a SET
	associationtype = 'GeneDiseaseAnnotation',
	sgdstrainbackground_id = b.sgdstrainbackground_id,
	diseaseannotationsubject_id = b.diseaseannotationsubject_id
FROM GeneDiseaseAnnotation b WHERE a.id = b.id;

DROP TABLE GeneDiseaseAnnotation;

UPDATE association a SET
	negated = b.negated,
	relation_id = b.relation_id,
	diseasegeneticmodifierrelation_id = b.diseasegeneticmodifierrelation_id,
	annotationtype_id = b.annotationtype_id,
	geneticsex_id = b.geneticsex_id,
	secondarydataprovider_id = b.secondarydataprovider_id,
	diseaseannotationobject_id = b.diseaseannotationobject_id
FROM DiseaseAnnotation b WHERE a.id = b.id;

INSERT INTO association_ontologyterm (diseaseannotation_id, evidencecodes_id)
	SELECT diseaseannotation_id, evidencecodes_id FROM diseaseannotation_ontologyterm;

DROP TABLE diseaseannotation_ontologyterm;

INSERT INTO association_vocabularyterm (diseaseannotation_id, diseasequalifiers_id)
	SELECT diseaseannotation_id, diseasequalifiers_id FROM diseaseannotation_vocabularyterm;

DROP TABLE diseaseannotation_vocabularyterm;

INSERT INTO association_biologicalentity (diseaseannotation_id, diseasegeneticmodifiers_id)
	SELECT diseaseannotation_id, diseasegeneticmodifiers_id FROM diseaseannotation_biologicalentity;

DROP TABLE diseaseannotation_biologicalentity;

INSERT INTO association_gene (diseaseannotation_id, with_id)
	SELECT diseaseannotation_id, with_id FROM diseaseannotation_gene;

DROP TABLE diseaseannotation_gene;
DROP TABLE DiseaseAnnotation;

UPDATE association a SET
	associationtype = 'GeneExpressionAnnotation',
	expressionannotationsubject_id = b.expressionannotationsubject_id,
	expressionassayused_id = b.expressionassayused_id
FROM GeneExpressionAnnotation b WHERE a.id = b.id;

DROP TABLE GeneExpressionAnnotation;

UPDATE association a SET
	relation_id = b.relation_id,
	whenexpressedstagename = b.whenexpressedstagename,
	whereexpressedstatement = b.whereexpressedstatement,
	expressionpattern_id = b.expressionpattern_id
FROM ExpressionAnnotation b WHERE a.id = b.id;

DROP TABLE ExpressionAnnotation;

UPDATE association a SET
	associationtype = 'GenePhenotypeAnnotation',
	phenotypeannotationsubject_id = b.phenotypeannotationsubject_id,
	sgdstrainbackground_id = b.sgdstrainbackground_id
FROM GenePhenotypeAnnotation b WHERE a.id = b.id;

DROP TABLE GenePhenotypeAnnotation;

UPDATE association a SET
	associationtype = 'AllelePhenotypeAnnotation',
	phenotypeannotationsubject_id = b.phenotypeannotationsubject_id,
	inferredgene_id = b.inferredgene_id
FROM AllelePhenotypeAnnotation b WHERE a.id = b.id;

INSERT INTO association_gene (allelephenotypeannotation_id, assertedgenes_id)
	SELECT allelephenotypeannotation_id, assertedgenes_id FROM allelephenotypeannotation_gene;

DROP TABLE allelephenotypeannotation_gene;
DROP TABLE AllelePhenotypeAnnotation;

UPDATE association a SET
	associationtype = 'AGMPhenotypeAnnotation',
	phenotypeannotationsubject_id = b.phenotypeannotationsubject_id,
	inferredgene_id = b.inferredgene_id,
	inferredallele_id = b.inferredallele_id,
	assertedallele_id = b.assertedallele_id
FROM AGMPhenotypeAnnotation b WHERE a.id = b.id;

INSERT INTO association_gene (agmphenotypeannotation_id, assertedgenes_id)
	SELECT agmphenotypeannotation_id, assertedgenes_id FROM agmphenotypeannotation_gene;

DROP TABLE agmphenotypeannotation_gene;
DROP TABLE AGMPhenotypeAnnotation;

UPDATE association a SET
	phenotypeannotationobject = b.phenotypeannotationobject,
	crossreference_id = b.crossreference_id,
	relation_id = b.relation_id
FROM phenotypeannotation b WHERE a.id = b.id;

INSERT INTO association_ontologyterm (phenotypeannotation_id, phenotypeterms_id)
	SELECT phenotypeannotation_id, phenotypeterms_id FROM phenotypeannotation_ontologyterm;

DROP TABLE phenotypeannotation_ontologyterm;
DROP TABLE phenotypeannotation;

UPDATE association a SET
	curie = b.curie,
	modentityid = b.modentityid,
	modinternalid = b.modinternalid,
	uniqueid = b.uniqueid,
	dataprovider_id = b.dataprovider_id
FROM Annotation b WHERE a.id = b.id;

INSERT INTO association_conditionrelation (annotation_id, conditionrelations_id)
	SELECT annotation_id, conditionrelations_id FROM annotation_conditionrelation;

DROP TABLE annotation_conditionrelation;

INSERT INTO association_note (annotation_id, relatednotes_id)
   SELECT annotation_id, relatednotes_id FROM annotation_note;

DROP TABLE annotation_note;

DROP TABLE annotation;

UPDATE association a SET
	singlereference_id = b.singlereference_id
FROM singlereferenceassociation b WHERE a.id = b.id;

DROP TABLE singlereferenceassociation;

UPDATE association a SET
	associationtype = 'GeneGeneticInteraction',
	interactorageneticperturbation_id = b.interactorageneticperturbation_id,
	interactorbgeneticperturbation_id = b.interactorbgeneticperturbation_id
FROM GeneGeneticInteraction b WHERE a.id = b.id;

-- association_phenotypesortraits
ALTER TABLE genegeneticinteraction_phenotypesortraits
	DROP CONSTRAINT genegeneticinteraction_phenotypesortraits_genegeneticinteractio;

DROP TABLE GeneGeneticInteraction;

UPDATE association a SET
	associationtype = 'GeneMolecularInteraction',
	aggregationdatabase_id = b.aggregationdatabase_id,
	detectionmethod_id = b.detectionmethod_id
FROM GeneMolecularInteraction b WHERE a.id = b.id;

DROP TABLE GeneMolecularInteraction;

UPDATE association a SET
	interactionid = b.interactionid,
	uniqueid = b.uniqueid,
	interactionsource_id = b.interactionsource_id,
	interactiontype_id = b.interactiontype_id,
	interactorarole_id = b.interactorarole_id,
	interactorbrole_id = b.interactorbrole_id,
	interactoratype_id = b.interactoratype_id,
	interactorbtype_id = b.interactorbtype_id
FROM GeneInteraction b WHERE a.id = b.id;

INSERT INTO association_crossreference (geneinteraction_id, crossreferences_id)
   SELECT geneinteraction_id, crossreferences_id FROM geneinteraction_crossreference;

DROP TABLE geneinteraction_crossreference;
DROP TABLE GeneInteraction;


UPDATE association a SET
	geneassociationsubject_id = b.geneassociationsubject_id,
	genegeneassociationobject_id = b.genegeneassociationobject_id,
	relation_id = b.relation_id
FROM GeneGeneAssociation b WHERE a.id = b.id;

DROP TABLE GeneGeneAssociation;

UPDATE association a SET
   associationtype = 'AlleleGeneAssociation',
   alleleassociationsubject_id = b.alleleassociationsubject_id,
   allelegeneassociationobject_id = b.allelegeneassociationobject_id
FROM AlleleGeneAssociation b WHERE a.id = b.id;

DROP TABLE AlleleGeneAssociation;

UPDATE association a SET
   relatednote_id = b.relatednote_id,
   relation_id = b.relation_id,
   evidencecode_id = b.evidencecode_id
FROM AlleleGenomicEntityAssociation b WHERE a.id = b.id;

DROP TABLE AlleleGenomicEntityAssociation;

UPDATE association a SET
	associationtype = 'ConstructGenomicEntityAssociation',
	constructassociationsubject_id = b.constructassociationsubject_id,
	relation_id = b.relation_id,
	constructgenomicentityassociationobject_id = b.constructgenomicentityassociationobject_id
FROM ConstructGenomicEntityAssociation b WHERE a.id = b.id;


INSERT INTO association_note (constructgenomicentityassociation_id, relatednotes_id)
   SELECT constructgenomicentityassociation_id, relatednotes_id FROM constructgenomicentityassociation_note;

DROP TABLE constructgenomicentityassociation_note;
DROP TABLE ConstructGenomicEntityAssociation;

UPDATE association a SET
   associationtype = 'SequenceTargetingReagentGeneAssociation',
   relation_id = b.relation_id,
   sequencetargetingreagentassociationsubject_id = b.sequencetargetingreagentassociationsubject_id,
   sequencetargetingreagentgeneassociationobject_id = b.sequencetargetingreagentgeneassociationobject_id
FROM SequenceTargetingReagentGeneAssociation b WHERE a.id = b.id;

DROP TABLE SequenceTargetingReagentGeneAssociation;

INSERT INTO association_informationcontententity (evidenceassociation_id, evidence_id)
   SELECT evidenceassociation_id, evidence_id FROM evidenceassociation_informationcontententity;

DROP TABLE evidenceassociation_informationcontententity;


DROP TABLE TranscriptGeneAssociation;
DROP TABLE TranscriptExonAssociation;
DROP TABLE TranscriptCodingSequenceAssociation;
DROP TABLE CodingSequenceGenomicLocationAssociation;
DROP TABLE ExonGenomicLocationAssociation;
DROP TABLE TranscriptGenomicLocationAssociation;
-- Will need to rerun the GFF loads to populate these tables

DROP TABLE evidenceassociation;

DELETE FROM association WHERE associationtype is NULL;

ALTER TABLE association ALTER COLUMN associationtype SET NOT null;


CREATE INDEX association_curie_index ON association USING btree (curie);
CREATE INDEX association_modentityid_index ON association USING btree (modentityid);
CREATE INDEX association_modinternalid_index ON association USING btree (modinternalid);
CREATE INDEX association_uniqueid_index ON association USING btree (uniqueid);
CREATE INDEX association_whenexpressedstagename_index ON association USING btree (whenexpressedstagename);
CREATE INDEX association_whereexpressedstatement_index ON association USING btree (whereexpressedstatement);
CREATE INDEX association_interactionid_index ON association USING btree (interactionid);
CREATE INDEX association_construct_subject_index ON association USING btree (constructassociationsubject_id);
CREATE INDEX association_construct_object_index ON association USING btree (constructgenomicentityassociationobject_id);
CREATE INDEX association_relation_index ON association USING btree (relation_id);
CREATE INDEX association_singlereference_index ON association USING btree (singlereference_id);
CREATE INDEX association_dataprovider_index ON association USING btree (dataprovider_id);
CREATE INDEX association_crossreference_index ON association USING btree (crossreference_id);
CREATE INDEX association_assertedallele_index ON association USING btree (assertedallele_id);
CREATE INDEX association_inferredallele_index ON association USING btree (inferredallele_id);
CREATE INDEX association_inferredgene_index ON association USING btree (inferredgene_id);

CREATE INDEX association_phenotypeannotationsubject_index ON association USING btree (phenotypeannotationsubject_id);

CREATE INDEX association_sgdstrainbackground_index ON association USING btree (sgdstrainbackground_id);
CREATE INDEX association_expressionpattern_index ON association USING btree (expressionpattern_id);
CREATE INDEX association_expression_annotation_subject_index ON association USING btree (expressionannotationsubject_id);
CREATE INDEX association_expression_assay_used_index ON association USING btree (expressionassayused_id);
CREATE INDEX association_sqtr_subject_index ON association USING btree (sequencetargetingreagentassociationsubject_id);
CREATE INDEX association_sqtr_object_index ON association USING btree (sequencetargetingreagentgeneassociationobject_id);
CREATE INDEX association_evidencecode_index ON association USING btree (evidencecode_id);
CREATE INDEX association_relatednote_index ON association USING btree (relatednote_id);
CREATE INDEX association_geneassociationsubject_index ON association USING btree (geneassociationsubject_id);
CREATE INDEX association_genegeneassociationobject_index ON association USING btree (genegeneassociationobject_id);
CREATE INDEX association_transcript_subject_index ON association USING btree (transcriptassociationsubject_id);
CREATE INDEX association_transcript_gene_object_index ON association USING btree (transcriptgeneassociationobject_id);
CREATE INDEX association_exon_subject_index ON association USING btree (exonassociationsubject_id);
CREATE INDEX association_exon_object_index ON association USING btree (exongenomiclocationassociationobject_id);
CREATE INDEX association_annotationtype_index ON association USING btree (annotationtype_id);
CREATE INDEX association_diseaseannotationobject_index ON association USING btree (diseaseannotationobject_id);
CREATE INDEX association_diseasegeneticmodifierrelation_index ON association USING btree (diseasegeneticmodifierrelation_id);
CREATE INDEX association_geneticsex_index ON association USING btree (geneticsex_id);
CREATE INDEX association_secondarydataprovider_index ON association USING btree (secondarydataprovider_id);
CREATE INDEX association_diseaseannotationsubject_index ON association USING btree (diseaseannotationsubject_id);
CREATE INDEX association_interactionsource_index ON association USING btree (interactionsource_id);
CREATE INDEX association_interactiontype_index ON association USING btree (interactiontype_id);
CREATE INDEX association_interactorarole_index ON association USING btree (interactorarole_id);
CREATE INDEX association_interactoratype_index ON association USING btree (interactoratype_id);
CREATE INDEX association_interactorbrole_index ON association USING btree (interactorbrole_id);
CREATE INDEX association_interactorbtype_index ON association USING btree (interactorbtype_id);
CREATE INDEX association_interactorageneticperturbarion_index ON association USING btree (interactorageneticperturbation_id);
CREATE INDEX association_interactorbgeneticperturbarion_index ON association USING btree (interactorbgeneticperturbation_id);
CREATE INDEX association_alleleassociationsubject_index ON association USING btree (alleleassociationsubject_id);
CREATE INDEX association_allelegeneassociationobject_index ON association USING btree (allelegeneassociationobject_id);
CREATE INDEX association_transcript_exon_object_index ON association USING btree (transcriptexonassociationobject_id);
CREATE INDEX association_transcript_object_index ON association USING btree (transcriptgenomiclocationassociationobject_id);
CREATE INDEX association_cds_subject_index ON association USING btree (codingsequenceassociationsubject_id);
CREATE INDEX association_cds_object_index ON association USING btree (codingsequencegenomiclocationassociationobject_id);
CREATE INDEX association_aggregationdatabase_index ON association USING btree (aggregationdatabase_id);
CREATE INDEX association_detectionmethod_index ON association USING btree (detectionmethod_id);
CREATE INDEX association_transcript_cds_object_index ON association USING btree (transcriptcodingsequenceassociationobject_id);

CREATE INDEX association_associationtype_index ON association USING btree (associationtype);

-- association_gene
CREATE INDEX association_assertedgenes_index ON association_gene USING btree (assertedgenes_id);
CREATE INDEX association_agmdiseaseannotation_index ON association_gene USING btree (agmdiseaseannotation_id);
CREATE INDEX association_allelediseaseannotation_index ON association_gene USING btree (allelediseaseannotation_id);
CREATE INDEX association_diseaseannotation_gene_index ON association_gene USING btree (diseaseannotation_id);
CREATE INDEX association_diseaseannotation_with_index ON association_gene USING btree (with_id);
CREATE INDEX association_allelephenotypeannotation_index ON association_gene USING btree (allelephenotypeannotation_id);
CREATE INDEX association_agmphenotypeannotation_index ON association_gene USING btree (agmphenotypeannotation_id);

-- association_biologicalentity
CREATE INDEX association_diseaseannotation_BiologicalEntity_index ON association_biologicalentity USING btree (diseaseannotation_id);
CREATE INDEX association_diseaseannotation_dgms_index ON association_biologicalentity USING btree (diseasegeneticmodifiers_id);

-- association_conditionrelation
CREATE INDEX association_annotation_conditionrelation_index ON association_conditionrelation USING btree (annotation_id);
CREATE INDEX association_annotation_conditionrelations_index ON association_conditionrelation USING btree (conditionrelations_id);

-- association_crossreference
CREATE INDEX association_geneinteraction_crossreference_index ON association_crossreference USING btree (geneinteraction_id, crossreferences_id);
CREATE INDEX association_geneinteraction_crossreferences_index ON association_crossreference USING btree (crossreferences_id);
CREATE INDEX association_geneinteraction_geneinteraction_index ON association_crossreference USING btree (geneinteraction_id);

-- association_informationcontententity
CREATE INDEX association_evidenceassociation_index ON association_informationcontententity USING btree (evidenceassociation_id);
CREATE INDEX association_evidenceassociation_evidence_index ON association_informationcontententity USING btree (evidence_id);

-- association_note
CREATE INDEX association_annotation_annotation_index ON association_note USING btree (annotation_id);
CREATE INDEX association_relatednotes_index ON association_note USING btree (relatednotes_id);
CREATE INDEX association_cgeassociation_cgeassociation_index ON association_note USING btree (constructgenomicentityassociation_id);

-- association_ontologyterm
CREATE INDEX association_diseaseannotation_diseaseannotation_index ON association_ontologyterm USING btree (diseaseannotation_id);
CREATE INDEX association_diseaseannotation_evidencecodes_index ON association_ontologyterm USING btree (evidencecodes_id);
CREATE INDEX association_phenotypeannotation_phenotypeannotation_index ON association_ontologyterm USING btree (phenotypeannotation_id);
CREATE INDEX association_phenotypeannotation_phenotypeterms_index ON association_ontologyterm USING btree (phenotypeterms_id);

-- association_vocabularyterm
CREATE INDEX association_diseaseannotation_vt_diseaseannotation_index ON association_vocabularyterm USING btree (diseaseannotation_id);
CREATE INDEX association_diseaseannotation_diseasequalifiers_index ON association_vocabularyterm USING btree (diseasequalifiers_id);

-- genegeneticinteraction_phenotypesortraits
CREATE INDEX association_genegeneticinteraction_phenotypesortraits_index ON genegeneticinteraction_phenotypesortraits USING btree (genegeneticinteraction_id);

-- FK's

-- association_gene
ALTER TABLE ONLY association_gene ADD CONSTRAINT fkhav6kkcn2o8ndwa5hv6gmwdaq FOREIGN KEY (assertedgenes_id) REFERENCES gene(id);
ALTER TABLE ONLY association_gene ADD CONSTRAINT fkfoqrp7f7uar43ue42a873aqjk FOREIGN KEY (agmdiseaseannotation_id) REFERENCES association(id);
ALTER TABLE ONLY association_gene ADD CONSTRAINT fkc8yg269l93lpu4fna99912d1y FOREIGN KEY (allelediseaseannotation_id) REFERENCES association(id);
ALTER TABLE ONLY association_gene ADD CONSTRAINT fkgib9oxtydiwdm5ibh8e14s8om FOREIGN KEY (diseaseannotation_id) REFERENCES association(id);
ALTER TABLE ONLY association_gene ADD CONSTRAINT fkeepgwlxwk2cgvs00fdbhuvc6n FOREIGN KEY (with_id) REFERENCES gene(id);
ALTER TABLE ONLY association_gene ADD CONSTRAINT fk7cqwg0wtuxw2ltm0f4de62bx1 FOREIGN KEY (allelephenotypeannotation_id) REFERENCES association(id);
ALTER TABLE ONLY association_gene ADD CONSTRAINT fkjwoi4su8vkylcfor313n7md25 FOREIGN KEY (agmphenotypeannotation_id) REFERENCES association(id);

-- association_biologicalentity
ALTER TABLE ONLY association_biologicalentity ADD CONSTRAINT fkkikn34sq62h6ajyagxno5bwhc FOREIGN KEY (diseaseannotation_id) REFERENCES association(id);
ALTER TABLE ONLY association_biologicalentity ADD CONSTRAINT fkp5pi80ie788coafu1490s8fla FOREIGN KEY (diseasegeneticmodifiers_id) REFERENCES biologicalentity(id);

-- association_conditionrelation
ALTER TABLE ONLY association_conditionrelation ADD CONSTRAINT fkc4spolvq2xj2253q6yg5rdqc9 FOREIGN KEY (annotation_id) REFERENCES association(id);
ALTER TABLE ONLY association_conditionrelation ADD CONSTRAINT fkiuqvgg26wj61xm3peaabj5a4f FOREIGN KEY (conditionrelations_id) REFERENCES conditionrelation(id);

-- association_crossreference
ALTER TABLE ONLY association_crossreference ADD CONSTRAINT fkt7xih7j80e5stq2rr88bbfr9 FOREIGN KEY (geneinteraction_id) REFERENCES association(id);
ALTER TABLE ONLY association_crossreference ADD CONSTRAINT fk6h7jv57hlmwj53to752mxo5ru FOREIGN KEY (crossreferences_id) REFERENCES crossreference(id);

-- association_informationcontententity
ALTER TABLE ONLY association_informationcontententity ADD CONSTRAINT fkd52dcl4h9urwl8w0s20wkxvob FOREIGN KEY (evidenceassociation_id) REFERENCES association(id);
ALTER TABLE ONLY association_informationcontententity ADD CONSTRAINT fkbgupm60jot5gk9i9q4carypcb FOREIGN KEY (evidence_id) REFERENCES informationcontententity(id);

-- association_note
ALTER TABLE ONLY association_note ADD CONSTRAINT fkgemsq21ksrmcdm5btgyna3bqw FOREIGN KEY (annotation_id) REFERENCES association(id);
ALTER TABLE ONLY association_note ADD CONSTRAINT fk6mcmoe71135qpoueb2cjtrrxd FOREIGN KEY (relatednotes_id) REFERENCES note(id);
ALTER TABLE ONLY association_note ADD CONSTRAINT fkm7ao01tj7hlgmqgx9xrp677sv FOREIGN KEY (constructgenomicentityassociation_id) REFERENCES association(id);

-- association_ontologyterm
ALTER TABLE ONLY association_ontologyterm ADD CONSTRAINT fki4p7fsji03okqpmakl22225jp FOREIGN KEY (diseaseannotation_id) REFERENCES association(id);
ALTER TABLE ONLY association_ontologyterm ADD CONSTRAINT fk2vi7xbepfem6fur85he3565el FOREIGN KEY (evidencecodes_id) REFERENCES ontologyterm(id);

-- Phototype Annotation Orphan Cleanup
DELETE FROM association_ontologyterm WHERE phenotypeannotation_id IN (
	SELECT ao.phenotypeannotation_id from association_ontologyterm ao
		LEFT JOIN association a ON ao.phenotypeannotation_id = a.id
		WHERE ao.phenotypeannotation_id IS NOT NULL AND a.id IS NULL
);

ALTER TABLE ONLY association_ontologyterm ADD CONSTRAINT fkom7p46wwwv16bkye0gjc2omxp FOREIGN KEY (phenotypeannotation_id) REFERENCES association(id);
ALTER TABLE ONLY association_ontologyterm ADD CONSTRAINT fk9hbubr81amn71dtvm86qw9pl7 FOREIGN KEY (phenotypeterms_id) REFERENCES ontologyterm(id);

-- association_vocabularyterm
ALTER TABLE ONLY association_vocabularyterm ADD CONSTRAINT fkcfijtoyigdh9b4ss8efnf3jro FOREIGN KEY (diseaseannotation_id) REFERENCES association(id);
ALTER TABLE ONLY association_vocabularyterm ADD CONSTRAINT fkbf73p7v3d72e2fsni076talw4 FOREIGN KEY (diseasequalifiers_id) REFERENCES vocabularyterm(id);



ALTER TABLE ONLY association ADD CONSTRAINT fkgobipxrs66w3ur1w1tk4sl8ac FOREIGN KEY (constructassociationsubject_id) REFERENCES construct(id);
ALTER TABLE ONLY association ADD CONSTRAINT fkscnsbwmhpug2to0yu6gw895oh FOREIGN KEY (constructgenomicentityassociationobject_id) REFERENCES genomicentity(id);
ALTER TABLE ONLY association ADD CONSTRAINT fk1sb2qk5clgbkk2976x13v5bum FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
ALTER TABLE ONLY association ADD CONSTRAINT fk6p763216qfdincsh6i5aei5kg FOREIGN KEY (singlereference_id) REFERENCES reference(id);
ALTER TABLE ONLY association ADD CONSTRAINT fk3omr7j2o1nneqgyroynbyulqq FOREIGN KEY (dataprovider_id) REFERENCES dataprovider(id);
ALTER TABLE ONLY association ADD CONSTRAINT fkmm82blif2e7bpb31jn9485ehc FOREIGN KEY (crossreference_id) REFERENCES crossreference(id);
ALTER TABLE ONLY association ADD CONSTRAINT fk6t34ef007ov1vpfk7ulksfy8y FOREIGN KEY (assertedallele_id) REFERENCES allele(id);
ALTER TABLE ONLY association ADD CONSTRAINT fk2t4nhpv4jnk38yn9bd646wrpj FOREIGN KEY (inferredallele_id) REFERENCES allele(id);
ALTER TABLE ONLY association ADD CONSTRAINT fk9ko4u7l6641hlhpn9eeyf8toh FOREIGN KEY (inferredgene_id) REFERENCES gene(id);

-- ALTER TABLE ONLY association ADD CONSTRAINT fktgpn8b91m1qb178srwe9n8s7  FOREIGN KEY (phenotypeannotationsubject_id) REFERENCES gene(id) ON DELETE CASCADE;
-- ALTER TABLE ONLY association ADD CONSTRAINT fk3bh18tfrhnab3usyc1naldwde FOREIGN KEY (phenotypeannotationsubject_id) REFERENCES affectedgenomicmodel(id) ON DELETE CASCADE;
-- ALTER TABLE ONLY Association ADD CONSTRAINT fk5dmwn688s6ls0mjo3nhgtli2h FOREIGN KEY (phenotypeAnnotationSubject_id) REFERENCES allele(id) ON DELETE CASCADE;
-- These constraints can't be added as the field points to 3 different objects

ALTER TABLE ONLY association ADD CONSTRAINT fknrw40h3f8fkk01kclygxd86h9 FOREIGN KEY (sgdstrainbackground_id) REFERENCES affectedgenomicmodel(id);
ALTER TABLE ONLY association ADD CONSTRAINT fknx6o6bclwogm0v4rli1cdrfdv FOREIGN KEY (expressionpattern_id) REFERENCES expressionpattern(id);
ALTER TABLE ONLY association ADD CONSTRAINT fkbh4gekhrbpbt4k9yvons42dux FOREIGN KEY (expressionannotationsubject_id) REFERENCES gene(id);
ALTER TABLE ONLY association ADD CONSTRAINT fk7e020acyvcolnnhpb4118tp3w FOREIGN KEY (expressionassayused_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY association ADD CONSTRAINT fkf2q4ns5ogkba5dtowqk3cajo5 FOREIGN KEY (sequencetargetingreagentassociationsubject_id) REFERENCES sequencetargetingreagent(id);
ALTER TABLE ONLY association ADD CONSTRAINT fkjsjeyjo4eljj3xm4ogi5ucyyw FOREIGN KEY (sequencetargetingreagentgeneassociationobject_id) REFERENCES gene(id);
ALTER TABLE ONLY association ADD CONSTRAINT fkidh408kt3t3fim9rbvgw32hw1 FOREIGN KEY (evidencecode_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY association ADD CONSTRAINT fkcas3usi3dh8j7s57xqrv29hlm FOREIGN KEY (relatednote_id) REFERENCES note(id);
ALTER TABLE ONLY association ADD CONSTRAINT fk8h34l4ktb6ui786lp9y2mfewo FOREIGN KEY (geneassociationsubject_id) REFERENCES gene(id);
ALTER TABLE ONLY association ADD CONSTRAINT fk7f05186xuynsh72ikqeuhe1pv FOREIGN KEY (genegeneassociationobject_id) REFERENCES gene(id);
ALTER TABLE ONLY association ADD CONSTRAINT fkt8fk74cx4ibjl7c4g2kvmhldf FOREIGN KEY (transcriptassociationsubject_id) REFERENCES transcript(id);
ALTER TABLE ONLY association ADD CONSTRAINT fkmiewpuidiblk7jcc5usb1jo63 FOREIGN KEY (transcriptgeneassociationobject_id) REFERENCES gene(id);
ALTER TABLE ONLY association ADD CONSTRAINT fkirjv7oatlssgiqoytcqiyhuqi FOREIGN KEY (exonassociationsubject_id) REFERENCES exon(id);
ALTER TABLE ONLY association ADD CONSTRAINT fkrwjlq6jcmn90cin2o0801oqxj FOREIGN KEY (exongenomiclocationassociationobject_id) REFERENCES assemblycomponent(id);
ALTER TABLE ONLY association ADD CONSTRAINT fknpx0xn81d3edht9pss6n7r2yo FOREIGN KEY (annotationtype_id) REFERENCES vocabularyterm(id);
ALTER TABLE ONLY association ADD CONSTRAINT fkr70o6q95nap6g7ry2jjxa90ya FOREIGN KEY (diseaseannotationobject_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY association ADD CONSTRAINT fknk44lwm2xauhs3r22h09us4ji FOREIGN KEY (diseasegeneticmodifierrelation_id) REFERENCES vocabularyterm(id);
ALTER TABLE ONLY association ADD CONSTRAINT fksinpe44rjxl9n6mn7vhanbwlg FOREIGN KEY (geneticsex_id) REFERENCES vocabularyterm(id);
ALTER TABLE ONLY association ADD CONSTRAINT fk554ce408ek9gf44f6db3w4et1 FOREIGN KEY (secondarydataprovider_id) REFERENCES dataprovider(id);

-- ALTER TABLE ONLY association ADD CONSTRAINT fkouxa4j6wep8rss3jviti0qibg FOREIGN KEY (diseaseannotationsubject_id) REFERENCES allele(id) ON DELETE CASCADE;
-- ALTER TABLE ONLY association ADD CONSTRAINT fk8qyy1fyhm3273j2p57alkp02t FOREIGN KEY (diseaseannotationsubject_id) REFERENCES gene(id) ON DELETE CASCADE;
-- ALTER TABLE ONLY association ADD CONSTRAINT fkksi8xjap9hliwoo4j3io09frg FOREIGN KEY (diseaseannotationsubject_id) REFERENCES affectedgenomicmodel(id) ON DELETE CASCADE;
-- These constraints can't be added as the field points to 3 different objects

ALTER TABLE ONLY association ADD CONSTRAINT fkaj9ynw0e9bswh3dth646t834o FOREIGN KEY (interactionsource_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY association ADD CONSTRAINT fkh8gqvx5c40tlw0a37uff6e9ky FOREIGN KEY (interactiontype_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY association ADD CONSTRAINT fkapag4jcegsd5rus25ladx3eat FOREIGN KEY (interactorarole_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY association ADD CONSTRAINT fks83wq2dpg8csweamyp31roli9 FOREIGN KEY (interactoratype_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY association ADD CONSTRAINT fkl05py1cdx0304xdev9697uptx FOREIGN KEY (interactorbrole_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY association ADD CONSTRAINT fkqokld7svnht4m6k4t9jnk81gf FOREIGN KEY (interactorbtype_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY association ADD CONSTRAINT fkf2m5olg8hj4awsnr9p12kjvry FOREIGN KEY (interactorageneticperturbation_id) REFERENCES allele(id) ON DELETE CASCADE;
ALTER TABLE ONLY association ADD CONSTRAINT fkpocxhp5klhi10d84xy8wm6yh1 FOREIGN KEY (interactorbgeneticperturbation_id) REFERENCES allele(id) ON DELETE CASCADE;
ALTER TABLE ONLY association ADD CONSTRAINT fkiwqrtab6l4bkwcg6ysfprr0tg FOREIGN KEY (alleleassociationsubject_id) REFERENCES allele(id);
ALTER TABLE ONLY association ADD CONSTRAINT fkik71nx67sxxpfmvly6exwsf4m FOREIGN KEY (allelegeneassociationobject_id) REFERENCES gene(id);
ALTER TABLE ONLY association ADD CONSTRAINT fkfdqmfjd4v0smqmu8hossg5jet FOREIGN KEY (transcriptexonassociationobject_id) REFERENCES exon(id);
ALTER TABLE ONLY association ADD CONSTRAINT fkeclnm31mpi8i1iy07n0cm003g FOREIGN KEY (transcriptgenomiclocationassociationobject_id) REFERENCES assemblycomponent(id);
ALTER TABLE ONLY association ADD CONSTRAINT fkfgso1aqthy90ltcc6pdo235p1 FOREIGN KEY (codingsequenceassociationsubject_id) REFERENCES codingsequence(id);
ALTER TABLE ONLY association ADD CONSTRAINT fkeot29m2x4peefkgwrqmu36ub1 FOREIGN KEY (codingsequencegenomiclocationassociationobject_id) REFERENCES assemblycomponent(id);
ALTER TABLE ONLY association ADD CONSTRAINT fkd6l423ab6u2hfoyjaf85nxt4f FOREIGN KEY (aggregationdatabase_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY association ADD CONSTRAINT fkkbi83cmvdnis7mdsx966rvdxe FOREIGN KEY (detectionmethod_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY association ADD CONSTRAINT fkfk71crh2hl7wuwpmrwvcybnq3 FOREIGN KEY (transcriptcodingsequenceassociationobject_id) REFERENCES codingsequence(id);

ALTER TABLE ONLY genegeneticinteraction_phenotypesortraits ADD CONSTRAINT fklufk1y85ki3qeo1ixbko56rol FOREIGN KEY (genegeneticinteraction_id) REFERENCES association(id);


-- Fix sqtr long names;

DROP INDEX sequencetargetingreagent_secondaryIdentifiers_sequencetargetingreagent_index;
DROP INDEX sequencetargetingreagent_synonyms_sequencetargetingreagent_index;

CREATE INDEX sqtr_secondaryidentifiers_sqtr_index ON sequencetargetingreagent_secondaryidentifiers USING btree (sequencetargetingreagent_id);
CREATE INDEX sqtr_synonyms_sqtr_index ON sequencetargetingreagent_synonyms USING btree (sequencetargetingreagent_id);

-- Missing Indexes

CREATE INDEX biologicalentity_curie_index ON public.biologicalentity USING btree (curie);
CREATE INDEX biologicalentity_modinternalid_index ON public.biologicalentity USING btree (modinternalid);
CREATE INDEX cellularcomponentqualifiers_cellularcomponentqualifiers_index ON public.anatomicalsite_cellularcomponentqualifiers USING btree (cellularcomponentqualifiers_id);
CREATE INDEX informationcontententity_curie_index ON public.informationcontententity USING btree (curie);
CREATE INDEX ontologyterm_curie_index ON public.ontologyterm USING btree (curie);
CREATE INDEX ontologyterm_isa_ancestor_descendant_isancestors_index ON public.ontologyterm_isa_ancestor_descendant USING btree (isaancestors_id);

-- Missing Constrains

ALTER TABLE ONLY public.anatomicalsite ADD CONSTRAINT fk4h252bd81x75fwxgyy6wa61u7 FOREIGN KEY (anatomicalstructure_id) REFERENCES public.ontologyterm(id);
ALTER TABLE ONLY public.anatomicalsite ADD CONSTRAINT fk820ik52dv75y8yeeymw9fib0l FOREIGN KEY (anatomicalsubstructure_id) REFERENCES public.ontologyterm(id);
ALTER TABLE ONLY public.anatomicalsite ADD CONSTRAINT fka5b13b9htt6lmwtckr393w62r FOREIGN KEY (updatedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.anatomicalsite ADD CONSTRAINT fkbwi9cqjupg87x1fjiea13swda FOREIGN KEY (createdby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.anatomicalsite ADD CONSTRAINT fkkbkvdh25o66jw9mkimtvt0fjl FOREIGN KEY (cellularcomponentterm_id) REFERENCES public.ontologyterm(id);

ALTER TABLE ONLY public.expressionpattern ADD CONSTRAINT fkjnwc0l0tjukc7939l5aa3gylu FOREIGN KEY (updatedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.expressionpattern ADD CONSTRAINT fksnvyfgvqvdcf49lnklncwjm0t FOREIGN KEY (createdby_id) REFERENCES public.person(id);

ALTER TABLE ONLY public.temporalcontext ADD CONSTRAINT fkqxng45bjismw8qcc4x3met821 FOREIGN KEY (developmentalstagestart_id) REFERENCES public.ontologyterm(id);
ALTER TABLE ONLY public.temporalcontext ADD CONSTRAINT fks5wnscfpf8l2fxacvo8bdg6rp FOREIGN KEY (createdby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.temporalcontext ADD CONSTRAINT fk7saeiw0res25be1sekjkebvxp FOREIGN KEY (updatedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.temporalcontext ADD CONSTRAINT fksrvvhoclqm33da4boc3dopmxh FOREIGN KEY (developmentalstagestop_id) REFERENCES public.ontologyterm(id);
































