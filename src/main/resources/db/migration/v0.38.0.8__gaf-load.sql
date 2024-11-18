update bulkurlload set bulkloadurl = 'https://download.alliancegenome.org/2.3.0/GAF/HUMAN/GAF_HUMAN_1.gaf'
where id = (select id from bulkload where name = 'HUMAN GAF');

