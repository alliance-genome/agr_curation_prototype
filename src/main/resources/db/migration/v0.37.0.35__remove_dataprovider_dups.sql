
-- Just in case anything was missed this query should run much faster after all the rows have been deleted
DELETE FROM dataprovider dp USING dataprovider_ids_to_delete cd WHERE dp.id = cd.id;
