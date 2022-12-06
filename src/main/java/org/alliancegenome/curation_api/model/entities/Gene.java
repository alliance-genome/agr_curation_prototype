package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotation;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Audited
@Indexed
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"geneDiseaseAnnotations", "geneSymbol", "geneFullName", "geneSystematicName", "geneSynonyms"})
@Schema(name="Gene", description="POJO that represents the Gene")
@AGRCurationSchemaVersion(min="1.5.0", max=LinkMLSchemaConstants.LATEST_RELEASE, dependencies={GenomicEntity.class}, partial=true)
@Table(indexes = {
	@Index(name = "gene_taxon_index", columnList = "geneType_curie"),
})
public class Gene extends GenomicEntity {

	@ManyToOne
	@JsonView({View.FieldsOnly.class})
	private SOTerm geneType;

	@OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<GeneDiseaseAnnotation> geneDiseaseAnnotations;
	
	@IndexedEmbedded(includeDepth = 2)
	@OneToOne(mappedBy = "singleGene", cascade = CascadeType.ALL)
	@JsonManagedReference
	@JsonView({View.FieldsOnly.class})
	private GeneSymbolSlotAnnotation geneSymbol;
	
	@IndexedEmbedded(includeDepth = 2)
	@OneToOne(mappedBy = "singleGene", cascade = CascadeType.ALL)
	@JsonManagedReference
	@JsonView({View.FieldsOnly.class})
	private GeneFullNameSlotAnnotation geneFullName;
	
	@IndexedEmbedded(includeDepth = 2)
	@OneToOne(mappedBy = "singleGene", cascade = CascadeType.ALL)
	@JsonManagedReference
	@JsonView({View.FieldsOnly.class})
	private GeneSystematicNameSlotAnnotation geneSystematicName;
	
	@IndexedEmbedded(includeDepth = 2)
	@OneToMany(mappedBy = "singleGene", cascade = CascadeType.ALL)
	@JsonManagedReference
	@JsonView({View.FieldsAndLists.class, View.GeneView.class})
	private List<GeneSynonymSlotAnnotation> geneSynonyms;
}

