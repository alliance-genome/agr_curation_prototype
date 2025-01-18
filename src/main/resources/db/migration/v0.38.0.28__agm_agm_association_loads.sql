CREATE TABLE public.agmagmassociation (
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
    agmAssociationObject_id bigint,
    relation_id bigint
);

CREATE SEQUENCE public.agmagmassociation_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE ONLY public.agmagmassociation ADD CONSTRAINT agmagmassociation_pkey PRIMARY KEY (id);

CREATE INDEX agmagmassociation_agmassocsubject_in ON public.agmagmassociation USING btree (agmassociationsubject_id);
CREATE INDEX agmagmassociation_agmassocobject_in ON public.agmagmassociation USING btree (agmAssociationObject_id);
CREATE INDEX agmagmassociation_createdby_index ON public.agmagmassociation USING btree (createdby_id);
CREATE INDEX agmagmassociation_internal_index ON public.agmagmassociation USING btree (internal);
CREATE INDEX agmagmassociation_obsolete_index ON public.agmagmassociation USING btree (obsolete);
CREATE INDEX agmagmassociation_relation_index ON public.agmagmassociation USING btree (relation_id);
CREATE INDEX agmagmassociation_updatedby_index ON public.agmagmassociation USING btree (updatedby_id);

ALTER TABLE ONLY public.agmagmassociation ADD CONSTRAINT agmstrassociation_agmassocsubject_fk FOREIGN KEY (agmassociationsubject_id) REFERENCES public.affectedgenomicmodel(id);
ALTER TABLE ONLY public.agmagmassociation ADD CONSTRAINT agmstrassociation_relation_fk FOREIGN KEY (relation_id) REFERENCES public.vocabularyterm(id);
ALTER TABLE ONLY public.agmagmassociation ADD CONSTRAINT agmstrassociation_updatedby_fk FOREIGN KEY (updatedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.agmagmassociation ADD CONSTRAINT agmstrassociation_agmassociationobject_fk FOREIGN KEY (agmAssociationObject_id) REFERENCES public.affectedgenomicmodel(id);
ALTER TABLE ONLY public.agmagmassociation ADD CONSTRAINT agmstrassociation_createdby_fk FOREIGN KEY (createdby_id) REFERENCES public.person(id);

--create vocabulary

INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'has_parental_population', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'agm_relation';

INSERT INTO vocabularytermset(id, name, vocabularylabel, vocabularytermsetvocabulary_id) SELECT nextval('vocabularytermset_seq'), 'AGM AGM Association Relation', 'agm_agm_relation', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'agm_relation';

CREATE TABLE tmp_vocab_link ( vocabularytermsets_id bigint, memberterms_id bigint );

INSERT INTO tmp_vocab_link (memberterms_id) SELECT id FROM vocabularyterm WHERE name = 'has_parental_population' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'agm_relation');

UPDATE tmp_vocab_link SET vocabularytermsets_id = subquery.id FROM (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'agm_agm_relation') AS subquery WHERE vocabularytermsets_id IS NULL;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) SELECT vocabularytermsets_id, memberterms_id FROM tmp_vocab_link;
	
DROP TABLE tmp_vocab_link;

--create loads

INSERT INTO bulkloadgroup (id, name) VALUES (nextval('bulkloadgroup_seq'), 'Direct (LinkML) AGM AGM Association Loads');
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'AGM_AGM_ASSOCIATION', 'ZFIN AGM AGM Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Direct (LinkML) AGM AGM Association Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'AGM_AGM_ASSOCIATION', 'FB AGM AGM Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Direct (LinkML) AGM AGM Association Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'AGM_AGM_ASSOCIATION', 'MGI AGM AGM Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Direct (LinkML) AGM AGM Association Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'AGM_AGM_ASSOCIATION', 'RGD AGM AGM Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Direct (LinkML) AGM AGM Association Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'AGM_AGM_ASSOCIATION', 'WB AGM AGM Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Direct (LinkML) AGM AGM Association Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'AGM_AGM_ASSOCIATION', 'HUMAN AGM AGM Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Direct (LinkML) AGM AGM Association Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'AGM_AGM_ASSOCIATION', 'SGD AGM AGM Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Direct (LinkML) AGM AGM Association Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'AGM_AGM_ASSOCIATION', 'XBXL AGM AGM Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Direct (LinkML) AGM AGM Association Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'AGM_AGM_ASSOCIATION', 'XBXT AGM AGM Association Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Direct (LinkML) AGM AGM Association Loads';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
	SELECT id, '0 0 22 ? * SUN-THU', false FROM bulkload WHERE backendbulkloadtype = 'AGM_AGM_ASSOCIATION';
INSERT INTO bulkmanualload (id, dataprovider)
	SELECT id, 'ZFIN' FROM bulkload WHERE name = 'ZFIN AGM AGM Association Load';
INSERT INTO bulkmanualload (id, dataprovider)
	SELECT id, 'FB' FROM bulkload WHERE name = 'FB AGM AGM Association Load';
INSERT INTO bulkmanualload (id, dataprovider)
	SELECT id, 'MGI' FROM bulkload WHERE name = 'MGI AGM AGM Association Load';
INSERT INTO bulkmanualload (id, dataprovider)
	SELECT id, 'RGD' FROM bulkload WHERE name = 'RGD AGM AGM Association Load';
INSERT INTO bulkmanualload (id, dataprovider)
	SELECT id, 'WB' FROM bulkload WHERE name = 'WB AGM AGM Association Load';
INSERT INTO bulkmanualload (id, dataprovider)
	SELECT id, 'HUMAN' FROM bulkload WHERE name = 'HUMAN AGM AGM Association Load';
INSERT INTO bulkmanualload (id, dataprovider)
	SELECT id, 'SGD' FROM bulkload WHERE name = 'SGD AGM AGM Association Load';
INSERT INTO bulkmanualload (id, dataprovider)
	SELECT id, 'XBXL' FROM bulkload WHERE name = 'XBXL AGM AGM Association Load';
INSERT INTO bulkmanualload (id, dataprovider)
	SELECT id, 'XBXT' FROM bulkload WHERE name = 'XBXT AGM AGM Association Load';