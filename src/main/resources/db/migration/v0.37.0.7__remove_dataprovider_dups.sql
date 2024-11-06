-- Update the null resourcedescriptorpage_id to the XB home page because Xenbase != XB

update crossreference 
	set resourcedescriptorpage_id = (
		SELECT rp.id 
		FROM resourcedescriptorpage rp, resourcedescriptor rd 
		WHERE rp.resourcedescriptor_id=rd.id and rd.prefix = 'Xenbase' and rp.name = 'homepage'
)
WHERE resourcedescriptorpage_id is null and referencedCurie = 'XB';

-- Add missing indexes
CREATE INDEX organization_abbreviation_index ON organization USING btree (abbreviation);
CREATE INDEX codingsequence_cdsType_index ON codingsequence USING btree (cdstype_id);
CREATE INDEX transcript_transcriptType_index ON transcript USING btree (transcripttype_id);
CREATE INDEX exon_exonType_index ON exon USING btree (exontype_id);
CREATE INDEX ontologyterm_name_index ON ontologyterm USING btree (name);

CREATE INDEX assemblycomponent_mapsToChromosome_index ON AssemblyComponent USING btree (mapsToChromosome_id);
CREATE INDEX assemblycomponent_name_index ON AssemblyComponent USING btree (name);
