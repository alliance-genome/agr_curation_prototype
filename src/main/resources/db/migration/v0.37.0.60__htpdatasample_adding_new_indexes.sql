--Renaming existing indexes
ALTER INDEX htpdatasetsampleannotation_createdby_index RENAME TO htpdatasample_createdby_index;
ALTER INDEX htpdatasetsampleannotation_updatedby_index RENAME TO htpdatasample_updatedby_index;
ALTER INDEX htpdatasetsampleannotation_dataprovider_index RENAME TO htpdatasample_dataprovider_index;
ALTER INDEX htpdatasetsampleannotation_htpexpressionsample_index RENAME TO htpdatasample_htpexpressionsample_index;

--Creating new indexes
CREATE INDEX htpdatasample_htpExpressionSampleType_index ON htpexpressiondatasetsampleannotation USING btree (htpExpressionSampleType_id);
CREATE INDEX htpdatasample_expressionAssayUsed_index ON htpexpressiondatasetsampleannotation USING btree (expressionAssayUsed_id);
CREATE INDEX htpdatasample_htpExpressionSampleAge_index ON htpexpressiondatasetsampleannotation USING btree (htpExpressionSampleAge_id);
CREATE INDEX htpdatasample_genomicInformation_index ON htpexpressiondatasetsampleannotation USING btree (genomicInformation_id);
CREATE INDEX htpdatasample_microarraySampleDetails_index ON htpexpressiondatasetsampleannotation USING btree (microarraySampleDetails_id);
CREATE INDEX htpdatasample_geneticSex_index ON htpexpressiondatasetsampleannotation USING btree (geneticSex_id);
CREATE INDEX htpdatasample_sequencingFormat_index ON htpexpressiondatasetsampleannotation USING btree (sequencingFormat_id);
CREATE INDEX htpdatasample_taxon_index ON htpexpressiondatasetsampleannotation USING btree (taxon_id);

CREATE INDEX biosamplegenomicinfo_bioSampleAllele_index ON biosamplegenomicinformation USING btree (bioSampleAllele_id);
CREATE INDEX biosamplegenomicinfo_bioSampleAgm_index ON biosamplegenomicinformation USING btree (bioSampleAgm_id);
CREATE INDEX biosamplegenomicinfo_bioSampleAgmType_index ON biosamplegenomicinformation USING btree (bioSampleAgmType_id);

--Adding 3 new htp category tags
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'bulk RNA-seq', id FROM vocabulary WHERE vocabularylabel = 'data_set_category_tags';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'single cell RNA-seq', id FROM vocabulary WHERE vocabularylabel = 'data_set_category_tags';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'spatial RNA-seq', id FROM vocabulary WHERE vocabularylabel = 'data_set_category_tags';