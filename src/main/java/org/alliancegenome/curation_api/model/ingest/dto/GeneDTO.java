package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.SecondaryIdSlotAnnotationDTO;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "1.7.2", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { GenomicEntityDTO.class, NameSlotAnnotationDTO.class }, submitted = true)
public class GeneDTO extends GenomicEntityDTO {

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("gene_symbol_dto")
	private NameSlotAnnotationDTO geneSymbolDto;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("gene_full_name_dto")
	private NameSlotAnnotationDTO geneFullNameDto;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("gene_systematic_name_dto")
	private NameSlotAnnotationDTO geneSystematicNameDto;

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("gene_synonym_dtos")
	private List<NameSlotAnnotationDTO> geneSynonymDtos;

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("gene_secondary_id_dtos")
	private List<SecondaryIdSlotAnnotationDTO> geneSecondaryIdDtos;
	
	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("gene_type_curie")
	private String geneTypeCurie;
}
