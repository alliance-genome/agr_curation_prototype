ALTER TABLE agmagmassociation
    add column
        agmAgmAssociationObject_id bigint
;

alter table agmagmassociation
    DROP agmassociationobject_id
;