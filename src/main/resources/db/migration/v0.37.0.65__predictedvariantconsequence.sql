CREATE TABLE predictedvariantconsequence (
	id bigint PRIMARY KEY,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	createdby_id bigint,
	updatedby_id bigint,
	variantgenomiclocation_id bigint,
	varianttranscript_id bigint,
	vepimpact_id bigint,
	polyphenprediction_id bigint,
	polyphenscore real,
	siftprediction_id bigint,
	siftscore real,
	aminoacidreference text,
	aminoacidvariant text,
	codonreference text,
	codonvariant text,
	calculatedcdnastart integer,
	calculatedcdnaend integer,
	calculatedcdsstart integer,
	calculatedcdsend integer,
	calculatedproteinstart integer,
	calculatedproteinend integer,
	hgvsproteinnomenclature text,
	hgvscodingnomenclature text,
	genelevelconsequence boolean DEFAULT false NOT NULL
);

CREATE SEQUENCE predictedvariantconsequence_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

CREATE INDEX predictedvariantconsequence_varianttranscript_index ON predictedvariantconsequence USING btree (varianttranscript_id);
CREATE INDEX predictedvariantconsequence_vepimpact_index ON predictedvariantconsequence USING btree (vepimpact_id);
CREATE INDEX predictedvariantconsequence_polyphenprediction_index ON predictedvariantconsequence USING btree (polyphenprediction_id);
CREATE INDEX predictedvariantconsequence_siftprediction_index ON predictedvariantconsequence USING btree (siftprediction_id);
CREATE INDEX predictedvariantconsequence_createdby_index ON predictedvariantconsequence USING btree (createdby_id);
CREATE INDEX predictedvariantconsequence_updatedby_index ON predictedvariantconsequence USING btree (updatedby_id);

ALTER TABLE ONLY predictedvariantconsequence ADD CONSTRAINT predictedvariantconsequence_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person(id);
ALTER TABLE ONLY predictedvariantconsequence ADD CONSTRAINT predictedvariantconsequence_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person(id);
ALTER TABLE ONLY predictedvariantconsequence ADD CONSTRAINT predictedvariantconsequence_variantgenomiclocation_id_fk FOREIGN KEY (variantgenomiclocation_id) REFERENCES curatedvariantgenomiclocation(id);
ALTER TABLE ONLY predictedvariantconsequence ADD CONSTRAINT predictedvariantconsequence_varianttranscript_id_fk FOREIGN KEY (varianttranscript_id) REFERENCES transcript(id);
ALTER TABLE ONLY predictedvariantconsequence ADD CONSTRAINT predictedvariantconsequence_vepimpact_id_fk FOREIGN KEY (vepimpact_id) REFERENCES vocabularyterm(id);
ALTER TABLE ONLY predictedvariantconsequence ADD CONSTRAINT predictedvariantconsequence_polyphenprediction_id_fk FOREIGN KEY (polyphenprediction_id) REFERENCES vocabularyterm(id);
ALTER TABLE ONLY predictedvariantconsequence ADD CONSTRAINT predictedvariantconsequence_polyphenscore_id_fk FOREIGN KEY (siftprediction_id) REFERENCES vocabularyterm(id);

CREATE TABLE predictedvariantconsequence_ontologyterm (
	predictedvariantconsequence_id bigint,
	vepconsequences_id bigint
);

CREATE INDEX predictedvariantconsequence_ontologyterm_pvc_index ON predictedvariantconsequence_ontologyterm USING btree (predictedvariantconsequence_id);
CREATE INDEX predictedvariantconsequence_ontologyterm_vc_index ON predictedvariantconsequence_ontologyterm USING btree (vepconsequences_id);

ALTER TABLE ONLY predictedvariantconsequence_ontologyterm ADD CONSTRAINT predictedvariantconsequence_ontologyterm_pvc_id_fk FOREIGN KEY (predictedvariantconsequence_id) REFERENCES predictedvariantconsequence (id);
ALTER TABLE ONLY predictedvariantconsequence_ontologyterm ADD CONSTRAINT predictedvariantconsequence_ontologyterm_vc_id_fk FOREIGN KEY (vepconsequences_id) REFERENCES ontologyterm (id);

INSERT INTO bulkloadgroup (id, name) VALUES (nextval('bulkloadgroup_seq'), 'File Management System (FMS) VEP Transcript Loads');
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'VEPTRANSCRIPT', 'FB VEP Transcript Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) VEP Transcript Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'VEPTRANSCRIPT', 'MGI VEP Transcript Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) VEP Transcript Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'VEPTRANSCRIPT', 'RGD VEP Transcript Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) VEP Transcript Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'VEPTRANSCRIPT', 'WB VEP Transcript Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) VEP Transcript Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'VEPTRANSCRIPT', 'ZFIN VEP Transcript Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) VEP Transcript Loads';
INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
	SELECT id, '0 0 20 ? * SUN-THU', false FROM bulkload WHERE backendbulkloadtype = 'VEPTRANSCRIPT';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'VEPTRANSCRIPT', 'FB' FROM bulkload WHERE name = 'FB VEP Transcript Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'VEPTRANSCRIPT', 'MGI' FROM bulkload WHERE name = 'MGI VEP Transcript Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'VEPTRANSCRIPT', 'RGD' FROM bulkload WHERE name = 'RGD VEP Transcript Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'VEPTRANSCRIPT', 'WB' FROM bulkload WHERE name = 'WB VEP Transcript Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'VEPTRANSCRIPT', 'ZFIN' FROM bulkload WHERE name = 'ZFIN VEP Transcript Load';

INSERT INTO bulkloadgroup (id, name) VALUES (nextval('bulkloadgroup_seq'), 'File Management System (FMS) VEP Gene Loads');
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'VEPGENE', 'FB VEP Gene Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) VEP Gene Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'VEPGENE', 'MGI VEP Gene Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) VEP Gene Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'VEPGENE', 'RGD VEP Gene Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) VEP Gene Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'VEPGENE', 'WB VEP Gene Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) VEP Gene Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'VEPGENE', 'ZFIN VEP Gene Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'File Management System (FMS) VEP Gene Loads';
INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
	SELECT id, '0 0 23 ? * SUN-THU', false FROM bulkload WHERE backendbulkloadtype = 'VEPGENE';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'VEPGENE', 'FB' FROM bulkload WHERE name = 'FB VEP Gene Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'VEPGENE', 'MGI' FROM bulkload WHERE name = 'MGI VEP Gene Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'VEPGENE', 'RGD' FROM bulkload WHERE name = 'RGD VEP Gene Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'VEPGENE', 'WB' FROM bulkload WHERE name = 'WB VEP Gene Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'VEPGENE', 'ZFIN' FROM bulkload WHERE name = 'ZFIN VEP Gene Load';
	
INSERT INTO vocabulary (id, name, vocabularydescription, vocabularylabel)
	VALUES (nextval('vocabulary_seq'), 'SIFT Prediction', 'SIFT prediction of impact of missense variation', 'sift_prediction');
INSERT INTO vocabulary (id, name, vocabularydescription, vocabularylabel)
	VALUES (nextval('vocabulary_seq'), 'PolyPhen-2 Prediction', 'Polyphen-2 prediction of impact of missense variation', 'polyphen_prediction');
INSERT INTO vocabulary (id, name, vocabularydescription, vocabularylabel)
	VALUES (nextval('vocabulary_seq'), 'VEP Impact', 'Ensembl VEP predicted impact rating of variant', 'vep_impact');
INSERT INTO vocabulary (id, name, vocabularydescription, vocabularylabel)
	VALUES (nextval('vocabulary_seq'), 'VEP Consequence', 'Names of SOTerms used to report predicted consequence of variant by Ensembl VEP', 'vep_consequence');

INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'deleterious', id FROM vocabulary WHERE vocabularylabel = 'sift_prediction';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'deleterious_low_confidence', id FROM vocabulary WHERE vocabularylabel = 'sift_prediction';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'tolerated', id FROM vocabulary WHERE vocabularylabel = 'sift_prediction';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'tolerated_low_confidence', id FROM vocabulary WHERE vocabularylabel = 'sift_prediction';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'no_prediction', id FROM vocabulary WHERE vocabularylabel = 'sift_prediction';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'benign', id FROM vocabulary WHERE vocabularylabel = 'polyphen_prediction';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'possibly_damaging', id FROM vocabulary WHERE vocabularylabel = 'polyphen_prediction';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'probably_damaging', id FROM vocabulary WHERE vocabularylabel = 'polyphen_prediction';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'unknown', id FROM vocabulary WHERE vocabularylabel = 'polyphen_prediction';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'no_prediction', id FROM vocabulary WHERE vocabularylabel = 'polyphen_prediction';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'HIGH', id FROM vocabulary WHERE vocabularylabel = 'vep_impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'MODERATE', id FROM vocabulary WHERE vocabularylabel = 'vep_impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'LOW', id FROM vocabulary WHERE vocabularylabel = 'vep_impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'MODIFIER', id FROM vocabulary WHERE vocabularylabel = 'vep_impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'transcript_ablation', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'splice_acceptor_variant', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'splice_donor_variant', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'stop_gained', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'frameshift_variant', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'stop_lost', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'start_lost', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'transcript_amplification', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'feature_elongation', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'feature_truncation', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'inframe_insertion', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'inframe_deletion', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'missense_variant', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'protein_altering_variant', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'splice_donor_5th_base_variant', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'splice_region_variant', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'splice_donor_region_variant', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'splice_polypyrimidine_tract_variant', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'incomplete_terminal_codon_variant', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'start_retained_variant', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'stop_retained_variant', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'synonymous_variant', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'coding_sequence_variant', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'mature_miRNA_variant', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), '5_prime_UTR_variant', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), '3_prime_UTR_variant', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'non_coding_transcript_exon_variant', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'intron_variant', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NMD_transcript_variant', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'non_coding_transcript_variant', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'coding_transcript_variant', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'upstream_gene_variant', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'downstream_gene_variant', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'TFBS_ablation', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'TFBS_amplification', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'TF_binding_site_variant', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'regulatory_region_ablation', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'regulatory_region_amplification', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'regulatory_region_variant', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'intergenic_variant', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'sequence_variant', id FROM vocabulary WHERE vocabularylabel = 'vep_consequence';
	
