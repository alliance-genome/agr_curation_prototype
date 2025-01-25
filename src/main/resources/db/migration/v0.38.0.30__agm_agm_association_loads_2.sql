CREATE INDEX agmagmassociation_agmassocobject_in ON public.agmagmassociation USING btree (agmAgmAssociationObject_id);
ALTER TABLE ONLY public.agmagmassociation ADD CONSTRAINT agmagmassociation_agmassociationobject_fk FOREIGN KEY (agmAgmAssociationObject_id) REFERENCES public.affectedgenomicmodel(id);
