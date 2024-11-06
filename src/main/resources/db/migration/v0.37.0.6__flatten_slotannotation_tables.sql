ALTER TABLE SlotAnnotation
   ADD COLUMN phenotypestatement text,
   ADD COLUMN secondaryid character varying(255),
   ADD COLUMN displaytext text,
   ADD COLUMN formattext text,
   ADD COLUMN synonymurl character varying(255),
   ADD COLUMN componentsymbol character varying(255),
   ADD COLUMN taxontext character varying(255),

   ADD COLUMN inheritancemode_id bigint,
   ADD COLUMN phenotypeterm_id bigint,
   ADD COLUMN singleallele_id bigint,
   ADD COLUMN nomenclatureevent_id bigint,
   ADD COLUMN nametype_id bigint,
   ADD COLUMN synonymscope_id bigint,
   ADD COLUMN singleconstruct_id bigint,
   ADD COLUMN singlegene_id bigint,
   ADD COLUMN databasestatus_id bigint,
   ADD COLUMN germlinetransmissionstatus_id bigint,
   ADD COLUMN relation_id bigint,
   ADD COLUMN taxon_id bigint,
   ADD COLUMN slotannotationtype character varying(96);


UPDATE SlotAnnotation s SET
	SlotAnnotationType = 'AlleleDatabaseStatusSlotAnnotation',
	databasestatus_id = a.databasestatus_id,
	singleallele_id = a.singleallele_id
FROM alleledatabasestatusslotannotation a WHERE s.id = a.id;

DROP TABLE AlleleDatabaseStatusSlotAnnotation;

UPDATE SlotAnnotation s SET
   SlotAnnotationType = 'AlleleFullNameSlotAnnotation',
   singleallele_id = a.singleallele_id
FROM AlleleFullNameSlotAnnotation a WHERE s.id = a.id;

DROP TABLE AlleleFullNameSlotAnnotation;

UPDATE SlotAnnotation s SET
   SlotAnnotationType = 'AlleleFunctionalImpactSlotAnnotation',
   singleallele_id = a.singleallele_id,
   phenotypestatement = a.phenotypestatement,
   phenotypeterm_id = a.phenotypeterm_id
FROM AlleleFunctionalImpactSlotAnnotation a WHERE s.id = a.id;

ALTER TABLE allelefunctionalimpactslotannotation_vocabularyterm RENAME TO slotannotation_vocabularyterm;
ALTER TABLE slotannotation_vocabularyterm RENAME COLUMN allelefunctionalimpactslotannotation_id TO slotannotation_id;
ALTER TABLE SlotAnnotation_vocabularyterm DROP CONSTRAINT allelefunctionalimpactsa_vocabterm_afisa_id_fk;
ALTER TABLE SlotAnnotation_vocabularyterm DROP CONSTRAINT allelefunctionalimpactsa_vocabterm_functionalimpacts_id_fk;
ALTER TABLE SlotAnnotation_vocabularyterm ADD CONSTRAINT fk8q2b6xm3sbackqpj60xo24nk2 FOREIGN KEY (slotannotation_id) REFERENCES slotannotation(id);
ALTER TABLE SlotAnnotation_vocabularyterm ADD CONSTRAINT fkaj5ybyv1kfuy92vmxnlplvu58 FOREIGN KEY (functionalimpacts_id) REFERENCES vocabularyterm(id);
DROP INDEX allelefunctionalimpactsa_vocabterm_afisa_index;
DROP INDEX allelefunctionalimpactsa_vocabterm_functionalimpacts_index;
CREATE INDEX slotannotation_id_index ON SlotAnnotation_vocabularyterm USING btree (slotannotation_id);
CREATE INDEX slotannotation_functionalimpacts_index ON SlotAnnotation_vocabularyterm USING btree (functionalimpacts_id);

DROP TABLE AlleleFunctionalImpactSlotAnnotation;

UPDATE SlotAnnotation s SET
   SlotAnnotationType = 'AlleleGermlineTransmissionStatusSlotAnnotation',
   singleallele_id = a.singleallele_id,
   germlinetransmissionstatus_id = a.germlinetransmissionstatus_id
FROM AlleleGermlineTransmissionStatusSlotAnnotation a WHERE s.id = a.id;

DROP TABLE AlleleGermlineTransmissionStatusSlotAnnotation;

UPDATE SlotAnnotation s SET
   SlotAnnotationType = 'AlleleInheritanceModeSlotAnnotation',
   inheritancemode_id = a.inheritancemode_id,
   phenotypestatement = a.phenotypestatement,
   singleallele_id = a.singleallele_id,
   phenotypeterm_id = a.phenotypeterm_id
FROM AlleleInheritanceModeSlotAnnotation a WHERE s.id = a.id;

DROP TABLE AlleleInheritanceModeSlotAnnotation;

UPDATE SlotAnnotation s SET
   SlotAnnotationType = 'AlleleMutationTypeSlotAnnotation',
   singleallele_id = a.singleallele_id
FROM AlleleMutationTypeSlotAnnotation a WHERE s.id = a.id;

ALTER TABLE allelemutationtypeslotannotation_ontologyterm RENAME TO slotannotation_ontologyterm;
ALTER TABLE slotannotation_ontologyterm RENAME COLUMN allelemutationtypeslotannotation_id TO slotannotation_id;
ALTER TABLE slotannotation_ontologyterm DROP CONSTRAINT allelemutationtypesa_soterm_amsa_id_fk;
ALTER TABLE slotannotation_ontologyterm DROP CONSTRAINT allelemutationtypesa_soterm_mutationtypes_id_fk;
ALTER TABLE SlotAnnotation_ontologyterm ADD CONSTRAINT fkm3aokfd4q1j2okqsyaj5v95kj FOREIGN KEY (slotannotation_id) REFERENCES slotannotation(id);
ALTER TABLE SlotAnnotation_ontologyterm ADD CONSTRAINT fklaf991287ttlt4yb8fbaimrqo FOREIGN KEY (mutationtypes_id) REFERENCES ontologyterm(id);
DROP INDEX allelemutationtypesa_soterm_amsa_index;
DROP INDEX allelemutationtypesa_soterm_mutationtypes_index;
CREATE INDEX slotannotation_soterm_amsa_index ON slotannotation_ontologyterm USING btree (slotannotation_id);
CREATE INDEX slotannotation_soterm_mutationtypes_index ON slotannotation_ontologyterm USING btree (mutationtypes_id);

DROP TABLE AlleleMutationTypeSlotAnnotation;

UPDATE SlotAnnotation s SET
   SlotAnnotationType = 'AlleleNomenclatureEventSlotAnnotation',
   singleallele_id = a.singleallele_id,
   nomenclatureevent_id = a.nomenclatureevent_id
FROM AlleleNomenclatureEventSlotAnnotation a WHERE s.id = a.id;

DROP TABLE AlleleNomenclatureEventSlotAnnotation;

UPDATE SlotAnnotation s SET
   SlotAnnotationType = 'AlleleSecondaryIdSlotAnnotation',
   singleallele_id = a.singleallele_id
FROM AlleleSecondaryIdSlotAnnotation a WHERE s.id = a.id;

DROP TABLE AlleleSecondaryIdSlotAnnotation;

UPDATE SlotAnnotation s SET
   SlotAnnotationType = 'AlleleSymbolSlotAnnotation',
   singleallele_id = a.singleallele_id
FROM AlleleSymbolSlotAnnotation a WHERE s.id = a.id;

DROP TABLE AlleleSymbolSlotAnnotation;

UPDATE SlotAnnotation s SET
   SlotAnnotationType = 'AlleleSynonymSlotAnnotation',
   singleallele_id = a.singleallele_id
FROM AlleleSynonymSlotAnnotation a WHERE s.id = a.id;

DROP TABLE AlleleSynonymSlotAnnotation;

UPDATE SlotAnnotation s SET
   SlotAnnotationType = 'ConstructComponentSlotAnnotation',
   singleconstruct_id = a.singleconstruct_id,
   componentsymbol = a.componentsymbol,
   taxontext = a.taxontext,
   relation_id = a.relation_id,
   taxon_id = a.taxon_id
FROM ConstructComponentSlotAnnotation a WHERE s.id = a.id;

ALTER TABLE constructcomponentslotannotation_note RENAME TO slotannotation_note;
ALTER TABLE slotannotation_note RENAME COLUMN constructcomponentslotannotation_id TO slotannotation_id;
ALTER TABLE slotannotation_note DROP CONSTRAINT constructcomponentslotannotation_note_ccsa_id_fk;
ALTER TABLE slotannotation_note DROP CONSTRAINT constructcomponentslotannotation_note_relatednotes_id_fk;
ALTER TABLE SlotAnnotation_note ADD CONSTRAINT fk4m4koh58321p1igpp8prt03wl FOREIGN KEY (slotannotation_id) REFERENCES slotannotation(id);
ALTER TABLE SlotAnnotation_note ADD CONSTRAINT fk4yafsa344go9s548rca9vwv6m FOREIGN KEY (relatednotes_id) REFERENCES note(id);
DROP INDEX constructcomponentsa_note_ccsa_index;
DROP INDEX constructcomponentsa_note_relatednotes_index;
CREATE INDEX slotannotation_note_ccsa_index ON slotannotation_note USING btree (slotannotation_id);
CREATE INDEX slotannotation_note_relatednotes_index ON slotannotation_note USING btree (relatednotes_id);

DROP TABLE ConstructComponentSlotAnnotation;

UPDATE SlotAnnotation s SET
   SlotAnnotationType = 'ConstructFullNameSlotAnnotation',
   singleconstruct_id = a.singleconstruct_id
FROM ConstructFullNameSlotAnnotation a WHERE s.id = a.id;

DROP TABLE ConstructFullNameSlotAnnotation;

UPDATE SlotAnnotation s SET
   SlotAnnotationType = 'ConstructSymbolSlotAnnotation',
   singleconstruct_id = a.singleconstruct_id
FROM ConstructSymbolSlotAnnotation a WHERE s.id = a.id;

DROP TABLE ConstructSymbolSlotAnnotation;

UPDATE SlotAnnotation s SET
   SlotAnnotationType = 'ConstructSynonymSlotAnnotation',
   singleconstruct_id = a.singleconstruct_id
FROM ConstructSynonymSlotAnnotation a WHERE s.id = a.id;

DROP TABLE ConstructSynonymSlotAnnotation;

UPDATE SlotAnnotation s SET
   SlotAnnotationType = 'GeneFullNameSlotAnnotation',
   singlegene_id = a.singlegene_id
FROM GeneFullNameSlotAnnotation a WHERE s.id = a.id;

DROP TABLE GeneFullNameSlotAnnotation;

UPDATE SlotAnnotation s SET
   SlotAnnotationType = 'GeneSecondaryIdSlotAnnotation',
   singlegene_id = a.singlegene_id
FROM GeneSecondaryIdSlotAnnotation a WHERE s.id = a.id;

DROP TABLE GeneSecondaryIdSlotAnnotation;

UPDATE SlotAnnotation s SET
   SlotAnnotationType = 'GeneSymbolSlotAnnotation',
   singlegene_id = a.singlegene_id
FROM GeneSymbolSlotAnnotation a WHERE s.id = a.id;

DROP TABLE GeneSymbolSlotAnnotation;

UPDATE SlotAnnotation s SET
   SlotAnnotationType = 'GeneSynonymSlotAnnotation',
   singlegene_id = a.singlegene_id
FROM GeneSynonymSlotAnnotation a WHERE s.id = a.id;

DROP TABLE GeneSynonymSlotAnnotation;

UPDATE SlotAnnotation s SET
   SlotAnnotationType = 'GeneSystematicNameSlotAnnotation',
   singlegene_id = a.singlegene_id
FROM GeneSystematicNameSlotAnnotation a WHERE s.id = a.id;

DROP TABLE GeneSystematicNameSlotAnnotation;

UPDATE SlotAnnotation s SET
   secondaryid = a.secondaryid
FROM SecondaryIdSlotAnnotation a WHERE s.id = a.id;

DROP TABLE SecondaryIdSlotAnnotation;

UPDATE SlotAnnotation s SET
   displaytext = a.displaytext,
   formattext = a.formattext,
   synonymurl = a.synonymurl,
   nametype_id = a.nametype_id,
   synonymscope_id = a.synonymscope_id
FROM NameSlotAnnotation a WHERE s.id = a.id;

DROP TABLE NameSlotAnnotation;

ALTER TABLE SlotAnnotation ADD CONSTRAINT fk19ret4dubt1ckfrbkng18lovq FOREIGN KEY (inheritancemode_id) REFERENCES vocabularyterm(id);
ALTER TABLE SlotAnnotation ADD CONSTRAINT fk7gu3slsry4u26f33ns6gc7w0m FOREIGN KEY (taxon_id) REFERENCES ontologyterm(id);
ALTER TABLE SlotAnnotation ADD CONSTRAINT fk8whffy7l5ievs39b0nnbir6ph FOREIGN KEY (nametype_id) REFERENCES vocabularyterm(id);
ALTER TABLE SlotAnnotation ADD CONSTRAINT fkbg9tp7mj2818od5n7ysaj37tf FOREIGN KEY (synonymscope_id) REFERENCES vocabularyterm(id);
ALTER TABLE SlotAnnotation ADD CONSTRAINT fkc8o0sm7ank0efy810051wfqsd FOREIGN KEY (germlinetransmissionstatus_id) REFERENCES vocabularyterm(id);
ALTER TABLE SlotAnnotation ADD CONSTRAINT fke3jo24un80y5wbimk8q3vi8ij FOREIGN KEY (nomenclatureevent_id) REFERENCES vocabularyterm(id);
ALTER TABLE SlotAnnotation ADD CONSTRAINT fkhg0s2w84inbudpe523k2568vr FOREIGN KEY (singleallele_id) REFERENCES allele(id);
ALTER TABLE SlotAnnotation ADD CONSTRAINT fkju1yhk6ryh61926japwdojk54 FOREIGN KEY (singlegene_id) REFERENCES gene(id);
ALTER TABLE SlotAnnotation ADD CONSTRAINT fkp4p1tdkllrsmx3v1jfr5iyk6y FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
ALTER TABLE SlotAnnotation ADD CONSTRAINT fkq8ci945lhcjeysbmhgc96op5r FOREIGN KEY (phenotypeterm_id) REFERENCES ontologyterm(id);
ALTER TABLE SlotAnnotation ADD CONSTRAINT fkqkk6210gn4gugy1eae8uxp443 FOREIGN KEY (singleconstruct_id) REFERENCES construct(id);
ALTER TABLE SlotAnnotation ADD CONSTRAINT fksjf0g4ao72a95jqwpp332d84q FOREIGN KEY (databasestatus_id) REFERENCES vocabularyterm(id);

CREATE INDEX slotannotation_inheritancemode_index ON slotannotation USING btree (inheritancemode_id);
CREATE INDEX slotannotation_taxon_index ON slotannotation USING btree (taxon_id);
CREATE INDEX slotannotation_nametype_index ON slotannotation USING btree (nametype_id);
CREATE INDEX slotannotation_synonymscope_index ON slotannotation USING btree (synonymscope_id);
CREATE INDEX slotannotation_status_index ON slotannotation USING btree (germlinetransmissionstatus_id);
CREATE INDEX slotannotation_nomenclatureevent_index ON slotannotation USING btree (nomenclatureevent_id);
CREATE INDEX slotannotation_singleallele_index ON slotannotation USING btree (singleallele_id);
CREATE INDEX slotannotation_singlegene_index ON slotannotation USING btree (singlegene_id);
CREATE INDEX slotannotation_relation_index ON slotannotation USING btree (relation_id);
CREATE INDEX slotannotation_phenotypeterm_index ON slotannotation USING btree (phenotypeterm_id);
CREATE INDEX slotannotation_singleconstruct_index ON slotannotation USING btree (singleconstruct_id);
CREATE INDEX slotannotation_databasestatus_index ON slotannotation USING btree (databasestatus_id);
CREATE INDEX slotannotation_componentsymbol_index ON slotannotation USING btree (componentsymbol);

CREATE TABLE evidence_ids_to_delete ( id bigint PRIMARY KEY);

INSERT INTO evidence_ids_to_delete (id)
	SELECT distinct i.id
	FROM slotannotation s, slotannotation_informationcontententity si, informationcontententity i
	WHERE s.id = si.slotannotation_id and si.evidence_id = i.id and s.slotannotationtype is null;

DELETE FROM slotannotation_informationcontententity WHERE evidence_id in (SELECT id FROM evidence_ids_to_delete);

DROP TABLE evidence_ids_to_delete;

DELETE FROM SlotAnnotation WHERE slotannotationtype is null;

ALTER TABLE SlotAnnotation ALTER COLUMN slotannotationtype SET NOT null;
ALTER TABLE OntologyTerm ALTER COLUMN ontologytermtype SET NOT null; -- Missed this on the last flattening exercise.

CREATE INDEX slotannotation_slotannotationtype ON slotannotation USING btree (slotannotationtype);
