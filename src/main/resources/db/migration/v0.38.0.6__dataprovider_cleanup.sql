CREATE TABLE tmp_unattached_dataproviders (
	id bigint
	);
	
INSERT INTO tmp_unattached_dataproviders (id) SELECT id FROM dataprovider;

DELETE FROM tmp_unattached_dataproviders WHERE id IN (SELECT DISTINCT dataprovider_id FROM biologicalentity);
DELETE FROM tmp_unattached_dataproviders WHERE id IN (SELECT DISTINCT dataprovider_id FROM diseaseannotation);
DELETE FROM tmp_unattached_dataproviders WHERE id IN (SELECT DISTINCT dataprovider_id FROM geneexpressionannotation);
DELETE FROM tmp_unattached_dataproviders WHERE id IN (SELECT DISTINCT dataprovider_id FROM htpexpressiondatasetannotation);
DELETE FROM tmp_unattached_dataproviders WHERE id IN (SELECT DISTINCT dataprovider_id FROM htpexpressiondatasetsampleannotation);
DELETE FROM tmp_unattached_dataproviders WHERE id IN (SELECT DISTINCT dataprovider_id FROM phenotypeannotation);
DELETE FROM tmp_unattached_dataproviders WHERE id IN (SELECT DISTINCT dataprovider_id FROM reagent);
DELETE FROM tmp_unattached_dataproviders WHERE id IN (SELECT DISTINCT dataprovider_id FROM species);
DELETE FROM tmp_unattached_dataproviders WHERE id IN (SELECT DISTINCT secondarydataprovider_id FROM diseaseannotation);

DELETE FROM dataprovider WHERE id IN (SELECT DISTINCT id FROM tmp_unattached_dataproviders);
	
DROP TABLE tmp_unattached_dataproviders;

