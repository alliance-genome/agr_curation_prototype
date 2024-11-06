ALTER TABLE anatomicalsite
	ADD COLUMN cellularcomponentother boolean DEFAULT false NOT NULL,
	ADD COLUMN cellularcomponentribbonterm_id bigint;

ALTER TABLE ONLY anatomicalsite
    ADD CONSTRAINT fkcuqc7qacirmg4wqcwuou8abjn FOREIGN KEY (cellularcomponentribbonterm_id) REFERENCES public.ontologyterm(id);