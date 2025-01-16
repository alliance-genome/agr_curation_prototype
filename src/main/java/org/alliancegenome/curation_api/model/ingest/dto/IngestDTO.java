package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.model.ingest.dto.associations.agmAssociations.AgmAlleleAssociationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.associations.agmAssociations.AgmSequenceTargetingReagentAssociationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.associations.alleleAssociations.AlleleGeneAssociationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.associations.alleleAssociations.AlleleVariantAssociationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.associations.constructAssociations.ConstructGenomicEntityAssociationDTO;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
public class IngestDTO {

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("linkml_version")
	private String linkMLVersion;
	
	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("alliance_member_release_version")
	private String allianceMemberReleaseVersion;

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("agm_ingest_set")
	private List<AffectedGenomicModelDTO> agmIngestSet;

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("allele_ingest_set")
	private List<AlleleDTO> alleleIngestSet;

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("disease_agm_ingest_set")
	private List<AGMDiseaseAnnotationDTO> diseaseAgmIngestSet;

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("disease_allele_ingest_set")
	private List<AlleleDiseaseAnnotationDTO> diseaseAlleleIngestSet;

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("disease_gene_ingest_set")
	private List<GeneDiseaseAnnotationDTO> diseaseGeneIngestSet;

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("gene_ingest_set")
	private List<GeneDTO> geneIngestSet;

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("construct_ingest_set")
	private List<ConstructDTO> constructIngestSet;

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("variant_ingest_set")
	private List<VariantDTO> variantIngestSet;
	
	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("allele_gene_association_ingest_set")
	private List<AlleleGeneAssociationDTO> alleleGeneAssociationIngestSet;
	
	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("allele_variant_association_ingest_set")
	private List<AlleleVariantAssociationDTO> alleleVariantAssociationIngestSet;
	
	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("construct_genomic_entity_association_ingest_set")
	private List<ConstructGenomicEntityAssociationDTO> constructGenomicEntityAssociationIngestSet;

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("agm_sequence_targeting_reagent_association_ingest_set")
	private List<AgmSequenceTargetingReagentAssociationDTO> agmStrAssociationIngestSet;

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("agm_allele_association_ingest_set")
	private List<AgmAlleleAssociationDTO> agmAlleleAssociationIngestSet;
}

