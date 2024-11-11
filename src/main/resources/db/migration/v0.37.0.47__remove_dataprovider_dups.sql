ALTER TABLE bulkloadfilehistory DROP COLUMN totaldeleterecords;
ALTER TABLE bulkloadfilehistory DROP COLUMN deletefailedrecords;
ALTER TABLE bulkloadfilehistory DROP COLUMN deletedrecords;
ALTER TABLE bulkloadfilehistory DROP COLUMN errorrate;
ALTER TABLE bulkloadfilehistory DROP COLUMN completedrecords;
ALTER TABLE bulkloadfilehistory DROP COLUMN totalrecords;
ALTER TABLE bulkloadfilehistory DROP COLUMN failedrecords;

ALTER TABLE bulkloadfilehistory ADD COLUMN counts jsonb;

delete from bulkfmsload where id in (select id from bulkload where backendbulkloadtype like '%_LOCATION' or backendbulkloadtype like 'GFF_%_GENE' or backendbulkloadtype like 'GFF_%_CDS' or backendbulkloadtype like 'GFF_%_EXON');
delete from bulkscheduledload where id in (select id from bulkload where backendbulkloadtype like '%_LOCATION' or backendbulkloadtype like 'GFF_%_GENE' or backendbulkloadtype like 'GFF_%_CDS' or backendbulkloadtype like 'GFF_%_EXON');
delete from bulkloadfileexception where bulkloadfilehistory_id in (select id from bulkloadfilehistory where bulkload_id in (select id from bulkload where backendbulkloadtype like '%_LOCATION' or backendbulkloadtype like 'GFF_%_GENE' or backendbulkloadtype like 'GFF_%_CDS' or backendbulkloadtype like 'GFF_%_EXON'));
delete from bulkloadfilehistory where bulkload_id in (select id from bulkload where backendbulkloadtype like '%_LOCATION' or backendbulkloadtype like 'GFF_%_GENE' or backendbulkloadtype like 'GFF_%_CDS' or backendbulkloadtype like 'GFF_%_EXON');
delete from bulkload where backendbulkloadtype like '%_LOCATION' or backendbulkloadtype like 'GFF_%_GENE' or backendbulkloadtype like 'GFF_%_CDS' or backendbulkloadtype like 'GFF_%_EXON';

