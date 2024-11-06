CREATE TABLE dataprovider_ids_to_keep (
   id bigint PRIMARY KEY
);
CREATE TABLE crossreference_ids_to_delete (
   id bigint
);
CREATE TABLE dataprovider_ids_to_delete (
   id bigint
);

INSERT INTO dataprovider_ids_to_keep (id) 
SELECT dp1.id
   FROM dataprovider dp1, organization o1, crossreference cr1
   WHERE o1.id = dp1.sourceorganization_id
   AND dp1.crossreference_id = cr1.id
   AND o1.abbreviation = 'Alliance'
   AND cr1.referencedCurie = 'Alliance'
   ORDER BY dp1.id ASC LIMIT 1;

-- select all the dataproviders that we are going to keep
INSERT INTO dataprovider_ids_to_keep (id) SELECT dataprovider_id FROM annotation where dataprovider_id is not null ON CONFLICT (id) DO NOTHING; -- 191431
INSERT INTO dataprovider_ids_to_keep (id) SELECT dataprovider_id FROM biologicalentity where dataprovider_id is not null ON CONFLICT (id) DO NOTHING; -- 6241140
INSERT INTO dataprovider_ids_to_keep (id) SELECT dataprovider_id FROM chromosome where dataprovider_id is not null ON CONFLICT (id) DO NOTHING; -- 0
INSERT INTO dataprovider_ids_to_keep (id) SELECT secondarydataprovider_id FROM diseaseannotation where secondarydataprovider_id is not null ON CONFLICT (id) DO NOTHING; -- 14380
INSERT INTO dataprovider_ids_to_keep (id) SELECT dataprovider_id FROM reagent where dataprovider_id is not null ON CONFLICT (id) DO NOTHING; -- 226431
INSERT INTO dataprovider_ids_to_keep (id) SELECT dataprovider_id FROM species where dataprovider_id is not null ON CONFLICT (id) DO NOTHING; -- 10
-- Total 6673392

INSERT INTO crossreference_ids_to_delete (id) select crossreference_id from dataprovider dp left join dataprovider_ids_to_keep dk on dp.id = dk.id where dp.crossreference_id is not null and dk.id is null; -- 42582734
CREATE INDEX crossreference_ids_to_delete_index ON crossreference_ids_to_delete USING btree (id);

INSERT INTO dataprovider_ids_to_delete (id) select dp.id from dataprovider dp left join dataprovider_ids_to_keep dk on dp.id = dk.id where dk.id is null;
CREATE INDEX dataprovider_ids_to_delete_index ON dataprovider_ids_to_delete USING btree (id);

ALTER TABLE ONLY phenotypeannotation DROP CONSTRAINT crossreference_id_fk;
ALTER TABLE ONLY dataprovider DROP CONSTRAINT dataprovider_crossreference_id_fk;
ALTER TABLE ONLY externaldatabaseentity DROP CONSTRAINT externaldatabaseentity_preferredcrossreference_id;
