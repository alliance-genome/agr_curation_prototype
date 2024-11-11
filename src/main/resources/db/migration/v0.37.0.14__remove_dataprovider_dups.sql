-- Update all BiologicalEntity.dataProvider to point to a ZFIN dataprovider that is first in the DB

UPDATE biologicalentity be 
SET dataprovider_id = (
	SELECT dp1.id
	FROM dataprovider dp1, organization o1, crossreference cr1
	WHERE o1.id = dp1.sourceorganization_id 
	AND dp1.crossreference_id = cr1.id
	AND o1.abbreviation = 'ZFIN'
	AND cr1.referencedCurie = 'ZFIN'
	ORDER BY dp1.id ASC LIMIT 1
)
FROM dataprovider dp, organization o, crossreference cr 
WHERE 
	be.dataprovider_id=dp.id AND 
	dp.sourceorganization_id=o.id AND
	dp.crossreference_id = cr.id AND
	o.abbreviation = cr.referencedCurie AND
	o.abbreviation = 'ZFIN';

UPDATE biologicalentity be
SET dataprovider_id = (
   SELECT dp1.id
   FROM dataprovider dp1, organization o1
   WHERE o1.id = dp1.sourceorganization_id 
   AND dp1.crossreference_id is null
   AND o1.abbreviation = 'ZFIN'
   ORDER BY dp1.id ASC LIMIT 1
)
FROM dataprovider dp, organization o
WHERE 
   be.dataprovider_id=dp.id AND 
   dp.sourceorganization_id=o.id AND
   dp.crossreference_id is null AND
   o.abbreviation = 'ZFIN';

