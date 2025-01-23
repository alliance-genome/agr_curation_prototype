ALTER TABLE agmagmassociation
    add column
        agmAgmAssociationObject_id bigint
;

alter table agmagmassociation
    DROP agmassociationobject_id
;

delete
from bulkscheduledload
where id in (SELECT id
             FROM bulkload
             WHERE backendbulkloadtype = 'AGM_AGM_ASSOCIATION');

delete
from bulkmanualload
where id in (SELECT id
             FROM bulkload
             WHERE backendbulkloadtype = 'AGM_AGM_ASSOCIATION');

delete
from bulkload
WHERE group_id in (select id from bulkloadgroup where name = 'Direct (LinkML) AGM AGM Association Loads');

delete
from bulkloadgroup
where name = 'Direct (LinkML) AGM AGM Association Loads';


