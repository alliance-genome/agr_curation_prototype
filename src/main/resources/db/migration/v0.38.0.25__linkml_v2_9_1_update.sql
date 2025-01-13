-- Switch modEntityId to primaryExternalId
ALTER TABLE biologicalentity RENAME COLUMN modentityid TO primaryexternalid;
ALTER TABLE diseaseannotation RENAME COLUMN modentityid TO primaryexternalid;
ALTER TABLE geneexpressionannotation RENAME COLUMN modentityid TO primaryexternalid;
ALTER TABLE geneexpressionexperiment RENAME COLUMN modentityid TO primaryexternalid;
ALTER TABLE phenotypeannotation RENAME COLUMN modentityid TO primaryexternalid;
ALTER TABLE person RENAME COLUMN modentityid TO primaryexternalid;
ALTER TABLE reagent RENAME COLUMN modentityid TO primaryexternalid;

ALTER INDEX biologicalentity_modentityid_index RENAME TO biologicalentity_primaryexternalid_index;
ALTER INDEX diseaseannotation_modentityid_index RENAME TO diseaseannotation_primaryexternalid_index;
ALTER INDEX geneexpressionannotation_modentityid_index RENAME TO geneexpressionannotation_primaryexternalid_index;
ALTER INDEX geneexpressionexperiment_modentityid_index RENAME TO geneexpressionexperiment_primaryexternalid_index;
ALTER INDEX phenotypeannotation_modentityid_index RENAME TO phenotypeannotation_primaryexternalid_index;
ALTER INDEX reagent_modentityid_index RENAME TO reagent_primaryexternalid_index;

ALTER TABLE biologicalentity RENAME CONSTRAINT biologicalentity_modentityid_uk TO biologicalentity_primaryexternalid_uk;
ALTER TABLE person RENAME CONSTRAINT uk_9omqedixfrwkqq9bdts63g65u TO person_primaryexternalid_uk;
ALTER TABLE reagent RENAME CONSTRAINT reagent_modentityid_uk TO reagent_primaryexternalid_uk;

ALTER TABLE diseaseannotation ADD CONSTRAINT diseaseannotation_primaryexternalid_uk UNIQUE (primaryexternalid);
ALTER TABLE geneexpressionannotation ADD CONSTRAINT geneexpressionannotation_primaryexternalid_uk UNIQUE (primaryexternalid);
ALTER TABLE geneexpressionexperiment ADD CONSTRAINT geneexpressionexperiment_primaryexternalid_uk UNIQUE (primaryexternalid);
ALTER TABLE phenotypeannotation ADD CONSTRAINT phenotypeannotation_primaryexternalid_uk UNIQUE (primaryexternalid);

--Update data provider for diseaseannotation table
ALTER TABLE diseaseannotation ADD newdataprovider_id bigint;
ALTER TABLE diseaseannotation ADD dataprovidercrossreference_id bigint;
ALTER TABLE diseaseannotation ADD newsecondarydataprovider_id bigint;
ALTER TABLE diseaseannotation ADD secondarydataprovidercrossreference_id bigint;

UPDATE diseaseannotation da
	SET newdataprovider_id = dp.sourceorganization_id
	FROM dataprovider dp
	WHERE dp.id = da.dataprovider_id;
	
UPDATE diseaseannotation da
	SET dataprovidercrossreference_id = dp.crossreference_id
	FROM dataprovider dp
	WHERE dp.id = da.dataprovider_id;

UPDATE diseaseannotation da
	SET newsecondarydataprovider_id = dp.sourceorganization_id
	FROM dataprovider dp
	WHERE dp.id = da.secondarydataprovider_id;
	
UPDATE diseaseannotation da
	SET secondarydataprovidercrossreference_id = dp.crossreference_id
	FROM dataprovider dp
	WHERE dp.id = da.secondarydataprovider_id;
	
ALTER TABLE diseaseannotation DROP COLUMN dataprovider_id;
ALTER TABLE diseaseannotation DROP COLUMN secondarydataprovider_id;

ALTER TABLE diseaseannotation RENAME COLUMN newdataprovider_id TO dataprovider_id;
ALTER TABLE diseaseannotation RENAME COLUMN newsecondarydataprovider_id TO secondarydataprovider_id;

ALTER TABLE diseaseannotation ADD CONSTRAINT diseaseannotation_dataprovider_id_fk FOREIGN KEY (dataprovider_id) REFERENCES organization(id);
ALTER TABLE diseaseannotation ADD CONSTRAINT diseaseannotation_dataprovidercrossreference_id_fk FOREIGN KEY (dataprovidercrossreference_id) REFERENCES crossreference(id);
ALTER TABLE diseaseannotation ADD CONSTRAINT diseaseannotation_secondarydataprovider_id_fk FOREIGN KEY (secondarydataprovider_id) REFERENCES organization(id);
ALTER TABLE diseaseannotation ADD CONSTRAINT diseaseannotation_secondarydataprovidercrossreference_id_fk FOREIGN KEY (secondarydataprovidercrossreference_id) REFERENCES crossreference(id);

CREATE INDEX diseaseannotation_dataprovider_index ON diseaseannotation USING btree (dataprovider_id);
CREATE INDEX diseaseannotation_secondarydataprovider_index ON diseaseannotation USING btree (secondarydataprovider_id);
CREATE INDEX diseaseannotation_dataprovidercrossreference_index ON diseaseannotation USING btree (dataprovidercrossreference_id);
CREATE INDEX diseaseannotation_secondarydataprovidercrossreference_index ON diseaseannotation USING btree (secondarydataprovidercrossreference_id);

--Update data provider for biologicalentity table
ALTER TABLE biologicalentity ADD newdataprovider_id bigint;
ALTER TABLE biologicalentity ADD dataprovidercrossreference_id bigint;

UPDATE biologicalentity be
	SET newdataprovider_id = dp.sourceorganization_id
	FROM dataprovider dp
	WHERE dp.id = be.dataprovider_id;
	
UPDATE biologicalentity be
	SET dataprovidercrossreference_id = dp.crossreference_id
	FROM dataprovider dp
	WHERE dp.id = be.dataprovider_id;
	
ALTER TABLE biologicalentity DROP COLUMN dataprovider_id;

ALTER TABLE biologicalentity RENAME COLUMN newdataprovider_id TO dataprovider_id;

ALTER TABLE biologicalentity ADD CONSTRAINT biologicalentity_dataprovider_id_fk FOREIGN KEY (dataprovider_id) REFERENCES organization(id);
ALTER TABLE biologicalentity ADD CONSTRAINT biologicalentity_dataprovidercrossreference_id_fk FOREIGN KEY (dataprovidercrossreference_id) REFERENCES crossreference(id);

CREATE INDEX biologicalentity_dataprovider_index ON biologicalentity USING btree (dataprovider_id);
CREATE INDEX biologicalentity_dataprovidercrossreference_index ON biologicalentity USING btree (dataprovidercrossreference_id);

--Update data provider for geneexpressionannotation table
ALTER TABLE geneexpressionannotation ADD newdataprovider_id bigint;
ALTER TABLE geneexpressionannotation ADD dataprovidercrossreference_id bigint;

UPDATE geneexpressionannotation be
	SET newdataprovider_id = dp.sourceorganization_id
	FROM dataprovider dp
	WHERE dp.id = be.dataprovider_id;
	
UPDATE geneexpressionannotation be
	SET dataprovidercrossreference_id = dp.crossreference_id
	FROM dataprovider dp
	WHERE dp.id = be.dataprovider_id;
	
ALTER TABLE geneexpressionannotation DROP COLUMN dataprovider_id;

ALTER TABLE geneexpressionannotation RENAME COLUMN newdataprovider_id TO dataprovider_id;

ALTER TABLE geneexpressionannotation ADD CONSTRAINT geneexpressionannotation_dataprovider_id_fk FOREIGN KEY (dataprovider_id) REFERENCES organization(id);
ALTER TABLE geneexpressionannotation ADD CONSTRAINT geneexpressionannotation_dataprovidercrossreference_id_fk FOREIGN KEY (dataprovidercrossreference_id) REFERENCES crossreference(id);

CREATE INDEX geneexpressionannotation_dataprovider_index ON geneexpressionannotation USING btree (dataprovider_id);
CREATE INDEX geneexpressionannotation_dataprovidercrossreference_index ON geneexpressionannotation USING btree (dataprovidercrossreference_id);

--Update data provider for geneexpressionexperiment table
ALTER TABLE geneexpressionexperiment ADD newdataprovider_id bigint;
ALTER TABLE geneexpressionexperiment ADD dataprovidercrossreference_id bigint;

UPDATE geneexpressionexperiment be
	SET newdataprovider_id = dp.sourceorganization_id
	FROM dataprovider dp
	WHERE dp.id = be.dataprovider_id;
	
UPDATE geneexpressionexperiment be
	SET dataprovidercrossreference_id = dp.crossreference_id
	FROM dataprovider dp
	WHERE dp.id = be.dataprovider_id;
	
ALTER TABLE geneexpressionexperiment DROP COLUMN dataprovider_id;

ALTER TABLE geneexpressionexperiment RENAME COLUMN newdataprovider_id TO dataprovider_id;

ALTER TABLE geneexpressionexperiment ADD CONSTRAINT geneexpressionexperiment_dataprovider_id_fk FOREIGN KEY (dataprovider_id) REFERENCES organization(id);
ALTER TABLE geneexpressionexperiment ADD CONSTRAINT geneexpressionexperiment_dataprovidercrossreference_id_fk FOREIGN KEY (dataprovidercrossreference_id) REFERENCES crossreference(id);

CREATE INDEX geneexpressionexperiment_dataprovider_index ON geneexpressionexperiment USING btree (dataprovider_id);
CREATE INDEX geneexpressionexperiment_dataprovidercrossreference_index ON geneexpressionexperiment USING btree (dataprovidercrossreference_id);

--Update data provider for htpexpressiondatasetannotation table
ALTER TABLE htpexpressiondatasetannotation ADD newdataprovider_id bigint;
ALTER TABLE htpexpressiondatasetannotation ADD dataprovidercrossreference_id bigint;

UPDATE htpexpressiondatasetannotation be
	SET newdataprovider_id = dp.sourceorganization_id
	FROM dataprovider dp
	WHERE dp.id = be.dataprovider_id;
	
UPDATE htpexpressiondatasetannotation be
	SET dataprovidercrossreference_id = dp.crossreference_id
	FROM dataprovider dp
	WHERE dp.id = be.dataprovider_id;
	
ALTER TABLE htpexpressiondatasetannotation DROP COLUMN dataprovider_id;

ALTER TABLE htpexpressiondatasetannotation RENAME COLUMN newdataprovider_id TO dataprovider_id;

ALTER TABLE htpexpressiondatasetannotation ADD CONSTRAINT htpexpressiondatasetannotation_dataprovider_id_fk FOREIGN KEY (dataprovider_id) REFERENCES organization(id);
ALTER TABLE htpexpressiondatasetannotation ADD CONSTRAINT htpexpressiondatasetannotation_dataprovidercrossreference_id_fk FOREIGN KEY (dataprovidercrossreference_id) REFERENCES crossreference(id);

CREATE INDEX htpexpressiondatasetannotation_dataprovider_index ON htpexpressiondatasetannotation USING btree (dataprovider_id);
CREATE INDEX htpexpressiondatasetannotation_dataprovidercrossreference_index ON htpexpressiondatasetannotation USING btree (dataprovidercrossreference_id);

--Update data provider for htpexpressiondatasetsampleannotation table
ALTER TABLE htpexpressiondatasetsampleannotation ADD newdataprovider_id bigint;
ALTER TABLE htpexpressiondatasetsampleannotation ADD dataprovidercrossreference_id bigint;

UPDATE htpexpressiondatasetsampleannotation be
	SET newdataprovider_id = dp.sourceorganization_id
	FROM dataprovider dp
	WHERE dp.id = be.dataprovider_id;
	
UPDATE htpexpressiondatasetsampleannotation be
	SET dataprovidercrossreference_id = dp.crossreference_id
	FROM dataprovider dp
	WHERE dp.id = be.dataprovider_id;
	
ALTER TABLE htpexpressiondatasetsampleannotation DROP COLUMN dataprovider_id;

ALTER TABLE htpexpressiondatasetsampleannotation RENAME COLUMN newdataprovider_id TO dataprovider_id;

ALTER TABLE htpexpressiondatasetsampleannotation ADD CONSTRAINT htpexpressiondatasetsampleannotation_dataprovider_id_fk FOREIGN KEY (dataprovider_id) REFERENCES organization(id);
ALTER TABLE htpexpressiondatasetsampleannotation ADD CONSTRAINT htpexpressiondatasetsampleannotation_dataproviderxref_id_fk FOREIGN KEY (dataprovidercrossreference_id) REFERENCES crossreference(id);

CREATE INDEX htpexpressiondatasetsampleannotation_dataprovider_index ON htpexpressiondatasetsampleannotation USING btree (dataprovider_id);
CREATE INDEX htpexpressiondatasetsampleannotation_dataproviderxref_index ON htpexpressiondatasetsampleannotation USING btree (dataprovidercrossreference_id);

--Update data provider for phenotypeannotation table
ALTER TABLE phenotypeannotation ADD newdataprovider_id bigint;
ALTER TABLE phenotypeannotation ADD dataprovidercrossreference_id bigint;

UPDATE phenotypeannotation be
	SET newdataprovider_id = dp.sourceorganization_id
	FROM dataprovider dp
	WHERE dp.id = be.dataprovider_id;
	
UPDATE phenotypeannotation be
	SET dataprovidercrossreference_id = dp.crossreference_id
	FROM dataprovider dp
	WHERE dp.id = be.dataprovider_id;
	
ALTER TABLE phenotypeannotation DROP COLUMN dataprovider_id;

ALTER TABLE phenotypeannotation RENAME COLUMN newdataprovider_id TO dataprovider_id;

ALTER TABLE phenotypeannotation ADD CONSTRAINT phenotypeannotation_dataprovider_id_fk FOREIGN KEY (dataprovider_id) REFERENCES organization(id);
ALTER TABLE phenotypeannotation ADD CONSTRAINT phenotypeannotation_dataprovidercrossreference_id_fk FOREIGN KEY (dataprovidercrossreference_id) REFERENCES crossreference(id);

CREATE INDEX phenotypeannotation_dataprovider_index ON phenotypeannotation USING btree (dataprovider_id);
CREATE INDEX phenotypeannotation_dataprovidercrossreference_index ON phenotypeannotation USING btree (dataprovidercrossreference_id);

--Update data provider for reagent table
ALTER TABLE reagent ADD newdataprovider_id bigint;
ALTER TABLE reagent ADD dataprovidercrossreference_id bigint;

UPDATE reagent be
	SET newdataprovider_id = dp.sourceorganization_id
	FROM dataprovider dp
	WHERE dp.id = be.dataprovider_id;
	
UPDATE reagent be
	SET dataprovidercrossreference_id = dp.crossreference_id
	FROM dataprovider dp
	WHERE dp.id = be.dataprovider_id;
	
ALTER TABLE reagent DROP COLUMN dataprovider_id;

ALTER TABLE reagent RENAME COLUMN newdataprovider_id TO dataprovider_id;

ALTER TABLE reagent ADD CONSTRAINT reagent_dataprovider_id_fk FOREIGN KEY (dataprovider_id) REFERENCES organization(id);
ALTER TABLE reagent ADD CONSTRAINT reagent_dataprovidercrossreference_id_fk FOREIGN KEY (dataprovidercrossreference_id) REFERENCES crossreference(id);

CREATE INDEX reagent_dataprovider_index ON reagent USING btree (dataprovider_id);
CREATE INDEX reagent_dataprovidercrossreference_index ON reagent USING btree (dataprovidercrossreference_id);

--Update data provider for species table
ALTER TABLE species ADD newdataprovider_id bigint;
ALTER TABLE species ADD dataprovidercrossreference_id bigint;

UPDATE species be
	SET newdataprovider_id = dp.sourceorganization_id
	FROM dataprovider dp
	WHERE dp.id = be.dataprovider_id;
	
UPDATE species be
	SET dataprovidercrossreference_id = dp.crossreference_id
	FROM dataprovider dp
	WHERE dp.id = be.dataprovider_id;
	
ALTER TABLE species DROP COLUMN dataprovider_id;

ALTER TABLE species RENAME COLUMN newdataprovider_id TO dataprovider_id;

ALTER TABLE species ADD CONSTRAINT species_dataprovider_id_fk FOREIGN KEY (dataprovider_id) REFERENCES organization(id);
ALTER TABLE species ADD CONSTRAINT species_dataprovidercrossreference_id_fk FOREIGN KEY (dataprovidercrossreference_id) REFERENCES crossreference(id);

CREATE INDEX species_dataprovider_index ON species USING btree (dataprovider_id);
CREATE INDEX species_dataprovidercrossreference_index ON species USING btree (dataprovidercrossreference_id);

--Update data provider for chromosome table
ALTER TABLE chromosome ADD newdataprovider_id bigint;
ALTER TABLE chromosome ADD dataprovidercrossreference_id bigint;

UPDATE chromosome be
	SET newdataprovider_id = dp.sourceorganization_id
	FROM dataprovider dp
	WHERE dp.id = be.dataprovider_id;
	
UPDATE chromosome be
	SET dataprovidercrossreference_id = dp.crossreference_id
	FROM dataprovider dp
	WHERE dp.id = be.dataprovider_id;
	
ALTER TABLE chromosome DROP COLUMN dataprovider_id;

ALTER TABLE chromosome RENAME COLUMN newdataprovider_id TO dataprovider_id;

ALTER TABLE chromosome ADD CONSTRAINT chromosome_dataprovider_id_fk FOREIGN KEY (dataprovider_id) REFERENCES organization(id);
ALTER TABLE chromosome ADD CONSTRAINT chromosome_dataprovidercrossreference_id_fk FOREIGN KEY (dataprovidercrossreference_id) REFERENCES crossreference(id);

CREATE INDEX chromosome_dataprovider_index ON chromosome USING btree (dataprovider_id);
CREATE INDEX chromosome_dataprovidercrossreference_index ON chromosome USING btree (dataprovidercrossreference_id);