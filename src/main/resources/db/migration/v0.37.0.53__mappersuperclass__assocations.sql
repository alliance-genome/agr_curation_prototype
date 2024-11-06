INSERT INTO diseaseannotation
	SELECT id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		curie,
		modentityid,
		modinternalid,
		uniqueid,
		negated,
		createdby_id,
		updatedby_id,
		singlereference_id,
		dataprovider_id,
		annotationtype_id,
		diseaseannotationobject_id,
		diseasegeneticmodifierrelation_id,
		geneticsex_id,
		relation_id,
		secondarydataprovider_id FROM Association WHERE AssociationType = 'AGMDiseaseAnnotation';


INSERT INTO agmdiseaseannotation
	SELECT id,
		assertedallele_id,
		diseaseannotationsubject_id,
		inferredallele_id,
		inferredgene_id FROM Association WHERE AssociationType = 'AGMDiseaseAnnotation';

INSERT INTO diseaseannotation_biologicalentity
	SELECT ab.diseaseannotation_id, ab.diseasegeneticmodifiers_id
		FROM association_biologicalentity ab, Association a
		WHERE ab.diseaseannotation_id = a.id AND a.associationtype = 'AGMDiseaseAnnotation';

DELETE FROM association_biologicalentity WHERE diseaseannotation_id IN (SELECT id FROM agmdiseaseannotation);

INSERT INTO diseaseannotation_conditionrelation
	SELECT ac.annotation_id, ac.conditionrelations_id
	FROM association_conditionrelation ac, Association a
	WHERE ac.annotation_id = a.id AND a.associationtype = 'AGMDiseaseAnnotation';

DELETE FROM association_conditionrelation WHERE annotation_id IN (SELECT id FROM agmdiseaseannotation);

INSERT INTO agmdiseaseannotation_gene (agmdiseaseannotation_id, assertedgenes_id)
	SELECT
		agmdiseaseannotation_id,
		assertedgenes_id
	FROM association_gene WHERE agmdiseaseannotation_id is NOT NULL;

DELETE from association_gene WHERE agmdiseaseannotation_id IN (SELECT id FROM agmdiseaseannotation);

INSERT INTO diseaseannotation_note
	SELECT an.annotation_id, an.relatednotes_id
	FROM association_note an, Association a
	WHERE an.annotation_id = a.id AND a.associationtype = 'AGMDiseaseAnnotation';

DELETE FROM association_note WHERE annotation_id IN (SELECT id FROM agmdiseaseannotation);

INSERT INTO diseaseannotation_ontologyterm
	SELECT ao.diseaseannotation_id, ao.evidencecodes_id
	FROM association_ontologyterm ao, Association a
	WHERE ao.diseaseannotation_id = a.id AND a.associationtype = 'AGMDiseaseAnnotation';

DELETE FROM association_ontologyterm WHERE diseaseannotation_id IN (SELECT id FROM agmdiseaseannotation);

INSERT INTO diseaseannotation_vocabularyterm
	SELECT av.diseaseannotation_id, av.diseasequalifiers_id
	FROM association_vocabularyterm av, Association a
	WHERE av.diseaseannotation_id = a.id AND a.associationtype = 'AGMDiseaseAnnotation';

DELETE FROM association_vocabularyterm WHERE diseaseannotation_id IN (SELECT id FROM agmdiseaseannotation);

DELETE FROM Association WHERE AssociationType = 'AGMDiseaseAnnotation';

INSERT INTO phenotypeannotation
	SELECT 
		id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		curie,
		modentityid,
		modinternalid,
		uniqueid,
		phenotypeannotationobject,
		createdby_id,
		updatedby_id,
		singlereference_id,
		dataprovider_id,
		crossreference_id,
		relation_id FROM Association WHERE AssociationType = 'AGMPhenotypeAnnotation';

INSERT INTO agmphenotypeannotation
	SELECT 
		id,
		assertedallele_id,
		inferredallele_id,
		inferredgene_id,
		phenotypeannotationsubject_id FROM Association WHERE AssociationType = 'AGMPhenotypeAnnotation';

INSERT INTO phenotypeannotation_conditionrelation
   SELECT ac.annotation_id, ac.conditionrelations_id
   FROM association_conditionrelation ac, Association a
   WHERE ac.annotation_id = a.id AND a.associationtype = 'AGMPhenotypeAnnotation';

DELETE FROM association_conditionrelation WHERE annotation_id IN (SELECT id FROM agmphenotypeannotation);

INSERT INTO agmphenotypeannotation_gene
	SELECT
		agmphenotypeannotation_id,
		assertedgenes_id
	FROM association_gene WHERE agmphenotypeannotation_id is NOT NULL;

DELETE from association_gene WHERE agmphenotypeannotation_id IN (SELECT id FROM agmphenotypeannotation);

INSERT INTO phenotypeannotation_note
	SELECT an.annotation_id, an.relatednotes_id
	FROM association_note an, Association a
	WHERE an.annotation_id = a.id AND a.associationtype = 'AGMPhenotypeAnnotation';

DELETE FROM association_note WHERE annotation_id IN (SELECT id FROM agmphenotypeannotation);

INSERT INTO phenotypeannotation_ontologyterm
   SELECT ao.phenotypeannotation_id, ao.phenotypeterms_id
   FROM association_ontologyterm ao, Association a
   WHERE ao.phenotypeannotation_id = a.id AND a.associationtype = 'AGMPhenotypeAnnotation';

DELETE FROM association_ontologyterm WHERE phenotypeannotation_id IN (SELECT id FROM agmphenotypeannotation);

DELETE FROM Association WHERE AssociationType = 'AGMPhenotypeAnnotation';

INSERT INTO diseaseannotation
	SELECT
		id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		curie,
		modentityid,
		modinternalid,
		uniqueid,
		negated,
		createdby_id,
		updatedby_id,
		singlereference_id,
		dataprovider_id,
		annotationtype_id,
		diseaseannotationobject_id,
		diseasegeneticmodifierrelation_id,
		geneticsex_id,
		relation_id,
		secondarydataprovider_id FROM Association WHERE AssociationType = 'AlleleDiseaseAnnotation';
		
INSERT INTO allelediseaseannotation
	SELECT
		id,
		diseaseannotationsubject_id,
		inferredgene_id FROM Association WHERE AssociationType = 'AlleleDiseaseAnnotation';

INSERT INTO diseaseannotation_biologicalentity
	SELECT ab.diseaseannotation_id, ab.diseasegeneticmodifiers_id
	FROM association_biologicalentity ab, Association a
	WHERE ab.diseaseannotation_id = a.id AND a.associationtype = 'AlleleDiseaseAnnotation';

DELETE FROM association_biologicalentity WHERE diseaseannotation_id IN (SELECT id FROM allelediseaseannotation);

INSERT INTO diseaseannotation_conditionrelation
	SELECT ac.annotation_id, ac.conditionrelations_id
	FROM association_conditionrelation ac, Association a
	WHERE ac.annotation_id = a.id AND a.associationtype = 'AlleleDiseaseAnnotation';

DELETE FROM association_conditionrelation WHERE annotation_id IN (SELECT id FROM allelediseaseannotation);

INSERT INTO allelediseaseannotation_gene (allelediseaseannotation_id, assertedgenes_id)
	SELECT
		allelediseaseannotation_id,
		assertedgenes_id
	FROM association_gene WHERE allelediseaseannotation_id is NOT NULL;

DELETE from association_gene WHERE allelediseaseannotation_id IN (SELECT id FROM allelediseaseannotation);

INSERT INTO diseaseannotation_note
	SELECT an.annotation_id, an.relatednotes_id
	FROM association_note an, Association a
	WHERE an.annotation_id = a.id AND a.associationtype = 'AlleleDiseaseAnnotation';

DELETE FROM association_note WHERE annotation_id IN (SELECT id FROM allelediseaseannotation);

INSERT INTO diseaseannotation_ontologyterm
	SELECT ao.diseaseannotation_id, ao.evidencecodes_id
	FROM association_ontologyterm ao, Association a
	WHERE ao.diseaseannotation_id = a.id AND a.associationtype = 'AlleleDiseaseAnnotation';

DELETE FROM association_ontologyterm WHERE diseaseannotation_id IN (SELECT id FROM allelediseaseannotation);

INSERT INTO diseaseannotation_vocabularyterm
	SELECT av.diseaseannotation_id, av.diseasequalifiers_id
	FROM association_vocabularyterm av, Association a
	WHERE av.diseaseannotation_id = a.id AND a.associationtype = 'AlleleDiseaseAnnotation';

DELETE FROM association_vocabularyterm WHERE diseaseannotation_id IN (SELECT id FROM allelediseaseannotation);

DELETE FROM Association WHERE AssociationType = 'AlleleDiseaseAnnotation';

INSERT INTO allelegeneassociation
	SELECT
		id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		createdby_id,
		updatedby_id,
		evidencecode_id,
		relatednote_id,
		relation_id,
		alleleassociationsubject_id,
		allelegeneassociationobject_id FROM Association WHERE AssociationType = 'AlleleGeneAssociation';

INSERT INTO allelegeneassociation_informationcontententity
	SELECT ai.evidenceassociation_id, ai.evidence_id
	FROM association_informationcontententity ai, Association a
	WHERE ai.evidenceassociation_id = a.id AND a.associationtype = 'AlleleGeneAssociation';

DELETE FROM association_informationcontententity WHERE evidenceassociation_id IN (SELECT id FROM allelegeneassociation);

DELETE FROM Association WHERE AssociationType = 'AlleleGeneAssociation';

INSERT INTO phenotypeannotation
	SELECT
		id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		curie,
		modentityid,
		modinternalid,
		uniqueid,
		phenotypeannotationobject,
		createdby_id,
		updatedby_id,
		singlereference_id,
		dataprovider_id,
		crossreference_id,
		relation_id FROM Association WHERE AssociationType = 'AllelePhenotypeAnnotation';

INSERT INTO allelephenotypeannotation
	SELECT
		id,
		inferredgene_id,
		phenotypeannotationsubject_id FROM Association WHERE AssociationType = 'AllelePhenotypeAnnotation';

INSERT INTO phenotypeannotation_conditionrelation
   SELECT ac.annotation_id, ac.conditionrelations_id
   FROM association_conditionrelation ac, Association a
   WHERE ac.annotation_id = a.id AND a.associationtype = 'AllelePhenotypeAnnotation';

DELETE FROM association_conditionrelation WHERE annotation_id IN (SELECT id FROM allelephenotypeannotation);

INSERT INTO allelephenotypeannotation_gene
	SELECT
		allelephenotypeannotation_id,
		assertedgenes_id
	FROM association_gene WHERE allelephenotypeannotation_id is NOT NULL;

DELETE from association_gene WHERE allelephenotypeannotation_id IN (SELECT id FROM allelephenotypeannotation);

INSERT INTO phenotypeannotation_note
	SELECT an.annotation_id, an.relatednotes_id
	FROM association_note an, Association a
	WHERE an.annotation_id = a.id AND a.associationtype = 'AllelePhenotypeAnnotation';

DELETE FROM association_note WHERE annotation_id IN (SELECT id FROM allelephenotypeannotation);

INSERT INTO phenotypeannotation_ontologyterm
   SELECT ao.phenotypeannotation_id, ao.phenotypeterms_id
   FROM association_ontologyterm ao, Association a
   WHERE ao.phenotypeannotation_id = a.id AND a.associationtype = 'AllelePhenotypeAnnotation';

DELETE FROM association_ontologyterm WHERE phenotypeannotation_id IN (SELECT id FROM allelephenotypeannotation);

DELETE FROM Association WHERE AssociationType = 'AllelePhenotypeAnnotation';

INSERT INTO codingsequencegenomiclocationassociation
	SELECT
		id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		"end",
		start,
		phase,
		strand,
		createdby_id,
		updatedby_id,
		relation_id,
		codingsequenceassociationsubject_id,
		codingsequencegenomiclocationassociationobject_id FROM Association WHERE AssociationType = 'CodingSequenceGenomicLocationAssociation';

INSERT INTO CodingSequenceGenomicLocationAssociation_InformationContentEntity
	SELECT ai.evidenceassociation_id, ai.evidence_id
	FROM association_informationcontententity ai, Association a
	WHERE ai.evidenceassociation_id = a.id AND a.associationtype = 'CodingSequenceGenomicLocationAssociation';

DELETE FROM association_informationcontententity WHERE evidenceassociation_id IN (SELECT id FROM codingsequencegenomiclocationassociation);

DELETE FROM Association WHERE AssociationType = 'CodingSequenceGenomicLocationAssociation';

INSERT INTO constructgenomicentityassociation
	SELECT
		id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		createdby_id,
		updatedby_id,
		constructassociationsubject_id,
		constructgenomicentityassociationobject_id,
		relation_id FROM Association WHERE AssociationType = 'ConstructGenomicEntityAssociation';

INSERT INTO constructgenomicentityassociation_informationcontententity
	SELECT ai.evidenceassociation_id, ai.evidence_id
	FROM association_informationcontententity ai, Association a
	WHERE ai.evidenceassociation_id = a.id AND a.associationtype = 'ConstructGenomicEntityAssociation';

DELETE FROM association_informationcontententity WHERE evidenceassociation_id IN (SELECT id FROM constructgenomicentityassociation);

INSERT INTO constructgenomicentityassociation_note
   SELECT an.constructgenomicentityassociation_id, an.relatednotes_id
   FROM association_note an, Association a
   WHERE an.constructgenomicentityassociation_id = a.id AND a.associationtype = 'ConstructGenomicEntityAssociation';

DELETE FROM association_note WHERE constructgenomicentityassociation_id IN (SELECT id FROM constructgenomicentityassociation);

DELETE FROM Association WHERE AssociationType = 'ConstructGenomicEntityAssociation';

INSERT INTO exongenomiclocationassociation
	SELECT
		id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		"end",
		start,
		strand,
		createdby_id,
		updatedby_id,
		relation_id,
		exonassociationsubject_id,
		exongenomiclocationassociationobject_id FROM Association WHERE AssociationType = 'ExonGenomicLocationAssociation';

INSERT INTO exongenomiclocationassociation_informationcontententity
	SELECT ai.evidenceassociation_id, ai.evidence_id
	FROM association_informationcontententity ai, Association a
	WHERE ai.evidenceassociation_id = a.id AND a.associationtype = 'ExonGenomicLocationAssociation';

DELETE FROM association_informationcontententity WHERE evidenceassociation_id IN (SELECT id FROM exongenomiclocationassociation);

DELETE FROM Association WHERE AssociationType = 'ExonGenomicLocationAssociation';

INSERT INTO diseaseannotation
	SELECT
		id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		curie,
		modentityid,
		modinternalid,
		uniqueid,
		negated,
		createdby_id,
		updatedby_id,
		singlereference_id,
		dataprovider_id,
		annotationtype_id,
		diseaseannotationobject_id,
		diseasegeneticmodifierrelation_id,
		geneticsex_id,
		relation_id,
		secondarydataprovider_id FROM Association WHERE AssociationType = 'GeneDiseaseAnnotation';

INSERT INTO genediseaseannotation
	SELECT
		id,
		diseaseannotationsubject_id,
		sgdstrainbackground_id FROM Association WHERE AssociationType = 'GeneDiseaseAnnotation';

INSERT INTO diseaseannotation_biologicalentity
	SELECT ab.diseaseannotation_id, ab.diseasegeneticmodifiers_id
	FROM association_biologicalentity ab, Association a
	WHERE ab.diseaseannotation_id = a.id AND a.associationtype = 'GeneDiseaseAnnotation';

DELETE FROM association_biologicalentity WHERE diseaseannotation_id IN (SELECT id FROM genediseaseannotation);

INSERT INTO diseaseannotation_conditionrelation
	SELECT ac.annotation_id, ac.conditionrelations_id
	FROM association_conditionrelation ac, Association a
	WHERE ac.annotation_id = a.id AND a.associationtype = 'GeneDiseaseAnnotation';

DELETE FROM association_conditionrelation WHERE annotation_id IN (SELECT id FROM genediseaseannotation);

INSERT INTO diseaseannotation_gene (diseaseannotation_id, with_id)
	SELECT
		diseaseannotation_id,
		with_id
	FROM association_gene WHERE diseaseannotation_id is NOT NULL;

DELETE from association_gene WHERE diseaseannotation_id IN (SELECT id FROM genediseaseannotation);

INSERT INTO diseaseannotation_note
	SELECT an.annotation_id, an.relatednotes_id
	FROM association_note an, Association a
	WHERE an.annotation_id = a.id AND a.associationtype = 'GeneDiseaseAnnotation';

DELETE FROM association_note WHERE annotation_id IN (SELECT id FROM genediseaseannotation);

INSERT INTO diseaseannotation_ontologyterm
	SELECT ao.diseaseannotation_id, ao.evidencecodes_id
	FROM association_ontologyterm ao, Association a
	WHERE ao.diseaseannotation_id = a.id AND a.associationtype = 'GeneDiseaseAnnotation';

DELETE FROM association_ontologyterm WHERE diseaseannotation_id IN (SELECT id FROM genediseaseannotation);

INSERT INTO diseaseannotation_vocabularyterm
	SELECT av.diseaseannotation_id, av.diseasequalifiers_id
	FROM association_vocabularyterm av, Association a
	WHERE av.diseaseannotation_id = a.id AND a.associationtype = 'GeneDiseaseAnnotation';

DELETE FROM association_vocabularyterm WHERE diseaseannotation_id IN (SELECT id FROM genediseaseannotation);

DELETE FROM Association WHERE AssociationType = 'GeneDiseaseAnnotation';

INSERT INTO geneexpressionannotation
	SELECT
		id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		curie,
		modentityid,
		modinternalid,
		uniqueid,
		whenexpressedstagename,
		whereexpressedstatement,
		createdby_id,
		updatedby_id,
		singlereference_id,
		dataprovider_id,
		expressionpattern_id,
		relation_id,
		expressionannotationsubject_id,
		expressionassayused_id FROM Association WHERE AssociationType = 'GeneExpressionAnnotation';

INSERT INTO geneexpressionannotation_conditionrelation
	SELECT ac.annotation_id, ac.conditionrelations_id
	FROM association_conditionrelation ac, Association a
	WHERE ac.annotation_id = a.id AND a.associationtype = 'GeneExpressionAnnotation';

DELETE FROM association_conditionrelation WHERE annotation_id IN (SELECT id FROM geneexpressionannotation);

INSERT INTO geneexpressionannotation_note
	SELECT an.annotation_id, an.relatednotes_id
	FROM association_note an, Association a
	WHERE an.annotation_id = a.id AND a.associationtype = 'GeneExpressionAnnotation';

DELETE FROM association_note WHERE annotation_id IN (SELECT id FROM geneexpressionannotation);

DELETE FROM Association WHERE AssociationType = 'GeneExpressionAnnotation';

INSERT INTO genegeneticinteraction
	SELECT
		id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		interactionid,
		uniqueid,
		createdby_id,
		updatedby_id,
		geneassociationsubject_id,
		genegeneassociationobject_id,
		relation_id,
		interactionsource_id,
		interactiontype_id,
		interactorarole_id,
		interactoratype_id,
		interactorbrole_id,
		interactorbtype_id,
		interactorageneticperturbation_id,
		interactorbgeneticperturbation_id FROM Association WHERE AssociationType = 'GeneGeneticInteraction';

INSERT INTO genegeneticinteraction_crossreference
   SELECT ac.geneinteraction_id, ac.crossreferences_id
   FROM association_crossreference ac, Association a
   WHERE ac.geneinteraction_id = a.id AND a.associationtype = 'GeneGeneticInteraction';

DELETE FROM association_crossreference WHERE geneinteraction_id IN (SELECT id FROM genegeneticinteraction);

INSERT INTO genegeneticinteraction_informationcontententity
	SELECT ai.evidenceassociation_id, ai.evidence_id
	FROM association_informationcontententity ai, Association a
	WHERE ai.evidenceassociation_id = a.id AND a.associationtype = 'GeneGeneticInteraction';

DELETE FROM association_informationcontententity WHERE evidenceassociation_id IN (SELECT id FROM genegeneticinteraction);

DELETE FROM Association WHERE AssociationType = 'GeneGeneticInteraction';

INSERT INTO genemolecularinteraction
	SELECT
		id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		interactionid,
		uniqueid,
		createdby_id,
		updatedby_id,
		geneassociationsubject_id,
		genegeneassociationobject_id,
		relation_id,
		interactionsource_id,
		interactiontype_id,
		interactorarole_id,
		interactoratype_id,
		interactorbrole_id,
		interactorbtype_id,
		aggregationdatabase_id,
		detectionmethod_id FROM Association WHERE AssociationType = 'GeneMolecularInteraction';

INSERT INTO genemolecularinteraction_crossreference
   SELECT ac.geneinteraction_id, ac.crossreferences_id
   FROM association_crossreference ac, Association a
   WHERE ac.geneinteraction_id = a.id AND a.associationtype = 'GeneMolecularInteraction';

DELETE FROM association_crossreference WHERE geneinteraction_id IN (SELECT id FROM genemolecularinteraction);

INSERT INTO genemolecularinteraction_informationcontententity
	SELECT ai.evidenceassociation_id, ai.evidence_id
	FROM association_informationcontententity ai, Association a
	WHERE ai.evidenceassociation_id = a.id AND a.associationtype = 'GeneMolecularInteraction';

DELETE FROM association_informationcontententity WHERE evidenceassociation_id IN (SELECT id FROM genemolecularinteraction);

DELETE FROM Association WHERE AssociationType = 'GeneMolecularInteraction';

INSERT INTO phenotypeannotation
	SELECT
		id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		curie,
		modentityid,
		modinternalid,
		uniqueid,
		phenotypeannotationobject,
		createdby_id,
		updatedby_id,
		singlereference_id,
		dataprovider_id,
		crossreference_id,
		relation_id FROM Association WHERE AssociationType = 'GenePhenotypeAnnotation';

INSERT INTO genephenotypeannotation
	SELECT
		id,
		phenotypeannotationsubject_id,
		sgdstrainbackground_id FROM Association WHERE AssociationType = 'GenePhenotypeAnnotation';

INSERT INTO phenotypeannotation_conditionrelation
	SELECT ac.annotation_id, ac.conditionrelations_id
	FROM association_conditionrelation ac, Association a
	WHERE ac.annotation_id = a.id AND a.associationtype = 'GenePhenotypeAnnotation';

DELETE FROM association_conditionrelation WHERE annotation_id IN (SELECT id FROM genephenotypeannotation);

INSERT INTO phenotypeannotation_note
	SELECT an.annotation_id, an.relatednotes_id
	FROM association_note an, Association a
	WHERE an.annotation_id = a.id AND a.associationtype = 'GenePhenotypeAnnotation';

DELETE FROM association_note WHERE annotation_id IN (SELECT id FROM genephenotypeannotation);

INSERT INTO phenotypeannotation_ontologyterm
   SELECT ao.phenotypeannotation_id, ao.phenotypeterms_id
   FROM association_ontologyterm ao, Association a
   WHERE ao.phenotypeannotation_id = a.id AND a.associationtype = 'GenePhenotypeAnnotation';

DELETE FROM association_ontologyterm WHERE phenotypeannotation_id IN (SELECT id FROM genephenotypeannotation);

DELETE FROM Association WHERE AssociationType = 'GenePhenotypeAnnotation';

INSERT INTO sequencetargetingreagentgeneassociation
	SELECT
		id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		createdby_id,
		updatedby_id,
		relation_id,
		sequencetargetingreagentassociationsubject_id,
		sequencetargetingreagentgeneassociationobject_id FROM Association WHERE AssociationType = 'SequenceTargetingReagentGeneAssociation';

INSERT INTO sequencetargetingreagentgeneassociation_informationcontententit
	SELECT ai.evidenceassociation_id, ai.evidence_id
	FROM association_informationcontententity ai, Association a
	WHERE ai.evidenceassociation_id = a.id AND a.associationtype = 'SequenceTargetingReagentGeneAssociation';

DELETE FROM association_informationcontententity WHERE evidenceassociation_id IN (SELECT id FROM sequencetargetingreagentgeneassociation);

DELETE FROM Association WHERE AssociationType = 'SequenceTargetingReagentGeneAssociation';

INSERT INTO transcriptcodingsequenceassociation
	SELECT
		id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		createdby_id,
		updatedby_id,
		relation_id,
		transcriptassociationsubject_id,
		transcriptcodingsequenceassociationobject_id FROM Association WHERE AssociationType = 'TranscriptCodingSequenceAssociation';

INSERT INTO transcriptcodingsequenceassociation_informationcontententity
	SELECT ai.evidenceassociation_id, ai.evidence_id
	FROM association_informationcontententity ai, Association a
	WHERE ai.evidenceassociation_id = a.id AND a.associationtype = 'TranscriptCodingSequenceAssociation';

DELETE FROM association_informationcontententity WHERE evidenceassociation_id IN (SELECT id FROM transcriptcodingsequenceassociation);

DELETE FROM Association WHERE AssociationType = 'TranscriptCodingSequenceAssociation';

INSERT INTO transcriptexonassociation
	SELECT
		id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		createdby_id,
		updatedby_id,
		relation_id,
		transcriptassociationsubject_id,
		transcriptexonassociationobject_id FROM Association WHERE AssociationType = 'TranscriptExonAssociation';

INSERT INTO transcriptexonassociation_informationcontententity
	SELECT ai.evidenceassociation_id, ai.evidence_id
	FROM association_informationcontententity ai, Association a
	WHERE ai.evidenceassociation_id = a.id AND a.associationtype = 'TranscriptExonAssociation';

DELETE FROM association_informationcontententity WHERE evidenceassociation_id IN (SELECT id FROM transcriptexonassociation);

DELETE FROM Association WHERE AssociationType = 'TranscriptExonAssociation';

INSERT INTO transcriptgeneassociation
	SELECT
		id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		createdby_id,
		updatedby_id,
		relation_id,
		transcriptassociationsubject_id,
		transcriptgeneassociationobject_id FROM Association WHERE AssociationType = 'TranscriptGeneAssociation';

INSERT INTO transcriptgeneassociation_informationcontententity
	SELECT ai.evidenceassociation_id, ai.evidence_id
	FROM association_informationcontententity ai, Association a
	WHERE ai.evidenceassociation_id = a.id AND a.associationtype = 'TranscriptGeneAssociation';

DELETE FROM association_informationcontententity WHERE evidenceassociation_id IN (SELECT id FROM transcriptgeneassociation);

DELETE FROM Association WHERE AssociationType = 'TranscriptGeneAssociation';

INSERT INTO transcriptgenomiclocationassociation
	SELECT
		id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		"end",
		start,
		phase, 
		strand,
		createdby_id,
		updatedby_id,
		relation_id,
		transcriptassociationsubject_id,
		transcriptgenomiclocationassociationobject_id FROM Association WHERE AssociationType = 'TranscriptGenomicLocationAssociation';

INSERT INTO transcriptgenomiclocationassociation_informationcontententity
	SELECT ai.evidenceassociation_id, ai.evidence_id
	FROM association_informationcontententity ai, Association a
	WHERE ai.evidenceassociation_id = a.id AND a.associationtype = 'TranscriptGenomicLocationAssociation';

DELETE FROM association_informationcontententity WHERE evidenceassociation_id IN (SELECT id FROM transcriptgenomiclocationassociation);

DELETE FROM Association WHERE AssociationType = 'TranscriptGenomicLocationAssociation';

DROP TABLE association_biologicalentity;
DROP TABLE association_conditionrelation;
DROP TABLE association_crossreference;
DROP TABLE association_gene;
DROP TABLE association_informationcontententity;
DROP TABLE association_note;
DROP TABLE association_ontologyterm;
DROP TABLE association_vocabularyterm;

DROP TABLE association;
