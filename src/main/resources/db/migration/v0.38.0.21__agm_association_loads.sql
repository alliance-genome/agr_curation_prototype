CREATE TABLE public.agmsequencetargetingreagentassociation (
    id bigint NOT NULL,
    datecreated timestamp(6) with time zone,
    dateupdated timestamp(6) with time zone,
    dbdatecreated timestamp(6) with time zone,
    dbdateupdated timestamp(6) with time zone,
    internal boolean DEFAULT false NOT NULL,
    obsolete boolean DEFAULT false NOT NULL,
    createdby_id bigint,
    updatedby_id bigint,
    agmassociationsubject_id bigint,
    agmsequencetargetingreagentassociationobject_id bigint,
    relation_id bigint
);

CREATE SEQUENCE public.agmsequencetargetingreagentassociation_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE ONLY public.agmsequencetargetingreagentassociation ADD CONSTRAINT agmsequencetargetingreagentassociation_pkey PRIMARY KEY (id);

CREATE INDEX agmsequencetargetingreagentassociation_agmassociationsubject_in ON public.agmsequencetargetingreagentassociation USING btree (agmassociationsubject_id);
CREATE INDEX agmsequencetargetingreagentassociation_agmsequencetargetingreag ON public.agmsequencetargetingreagentassociation USING btree (agmsequencetargetingreagentassociationobject_id);
CREATE INDEX agmsequencetargetingreagentassociation_createdby_index ON public.agmsequencetargetingreagentassociation USING btree (createdby_id);
CREATE INDEX agmsequencetargetingreagentassociation_internal_index ON public.agmsequencetargetingreagentassociation USING btree (internal);
CREATE INDEX agmsequencetargetingreagentassociation_obsolete_index ON public.agmsequencetargetingreagentassociation USING btree (obsolete);
CREATE INDEX agmsequencetargetingreagentassociation_relation_index ON public.agmsequencetargetingreagentassociation USING btree (relation_id);
CREATE INDEX agmsequencetargetingreagentassociation_updatedby_index ON public.agmsequencetargetingreagentassociation USING btree (updatedby_id);

ALTER TABLE ONLY public.agmsequencetargetingreagentassociation ADD CONSTRAINT agmsequencetargetingreagentassociation_agmassociationsubject_id FOREIGN KEY (agmassociationsubject_id) REFERENCES public.affectedgenomicmodel(id);
ALTER TABLE ONLY public.agmsequencetargetingreagentassociation ADD CONSTRAINT agmsequencetargetingreagentassociation_relation_id FOREIGN KEY (relation_id) REFERENCES public.vocabularyterm(id);
ALTER TABLE ONLY public.agmsequencetargetingreagentassociation ADD CONSTRAINT agmsequencetargetingreagentassociation_updatedby_id FOREIGN KEY (updatedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.agmsequencetargetingreagentassociation ADD CONSTRAINT agmsequencetargetingreagentassociation_agmsequencetargetingreagentassociationobject_id FOREIGN KEY (agmsequencetargetingreagentassociationobject_id) REFERENCES public.sequencetargetingreagent(id);
ALTER TABLE ONLY public.agmsequencetargetingreagentassociation ADD CONSTRAINT agmsequencetargetingreagentassociation_createdby_id FOREIGN KEY (createdby_id) REFERENCES public.person(id);

--create vocabulary

INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('vocabulary_seq'), 'AGM Relation', 'agm_relation');

INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'contains', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'agm_relation';

INSERT INTO vocabularytermset(id, name, vocabularylabel, vocabularytermsetvocabulary_id) SELECT nextval('vocabularytermset_seq'), 'AGM STR Association Relation', 'agm_str_relation', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'agm_relation';

CREATE TABLE tmp_vocab_link ( vocabularytermsets_id bigint, memberterms_id bigint );
	
INSERT INTO tmp_vocab_link (memberterms_id) SELECT id FROM vocabularyterm WHERE name = 'contains' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'agm_relation');

UPDATE tmp_vocab_link SET vocabularytermsets_id = subquery.id FROM (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'agm_str_relation') AS subquery WHERE vocabularytermsets_id IS NULL;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) SELECT vocabularytermsets_id, memberterms_id FROM tmp_vocab_link;
	
DROP TABLE tmp_vocab_link;

--create loads

INSERT INTO bulkloadgroup (id, name) VALUES (nextval('bulkloadgroup_seq'), 'AGM Association Loads');
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'AGM_ASSOCIATION', 'ZFIN AGM Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'AGM Association Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'AGM_ASSOCIATION', 'FB AGM Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'AGM Association Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'AGM_ASSOCIATION', 'MGI AGM Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'AGM Association Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'AGM_ASSOCIATION', 'RGD AGM Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'AGM Association Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'AGM_ASSOCIATION', 'WB AGM Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'AGM Association Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'AGM_ASSOCIATION', 'HUMAN AGM Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'AGM Association Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'AGM_ASSOCIATION', 'SGD AGM Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'AGM Association Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'AGM_ASSOCIATION', 'XBXL AGM Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'AGM Association Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'AGM_ASSOCIATION', 'XBXT AGM Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'AGM Association Loads';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
	SELECT id, '0 0 22 ? * SUN-THU', false FROM bulkload WHERE backendbulkloadtype = 'AGM_ASSOCIATION';
INSERT INTO bulkmanualload (id, dataprovider)
	SELECT id, 'ZFIN' FROM bulkload WHERE name = 'ZFIN AGM Association Load';
INSERT INTO bulkmanualload (id, dataprovider)
	SELECT id, 'FB' FROM bulkload WHERE name = 'FB AGM Association Load';
INSERT INTO bulkmanualload (id, dataprovider)
	SELECT id, 'MGI' FROM bulkload WHERE name = 'MGI AGM Association Load';
INSERT INTO bulkmanualload (id, dataprovider)
	SELECT id, 'RGD' FROM bulkload WHERE name = 'RGD AGM Association Load';
INSERT INTO bulkmanualload (id, dataprovider)
	SELECT id, 'WB' FROM bulkload WHERE name = 'WB AGM Association Load';
INSERT INTO bulkmanualload (id, dataprovider)
	SELECT id, 'HUMAN' FROM bulkload WHERE name = 'HUMAN AGM Association Load';
INSERT INTO bulkmanualload (id, dataprovider)
	SELECT id, 'SGD' FROM bulkload WHERE name = 'SGD AGM Association Load';
INSERT INTO bulkmanualload (id, dataprovider)
	SELECT id, 'XBXL' FROM bulkload WHERE name = 'XBXL AGM Association Load';
INSERT INTO bulkmanualload (id, dataprovider)
	SELECT id, 'XBXT' FROM bulkload WHERE name = 'XBXT AGM Association Load';