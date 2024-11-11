package org.alliancegenome.curation_api.model.entities;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import java.util.List;

@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(name = "GeneToGeneParalogy", description = "POJO that represents paralogy between two genes")
@AGRCurationSchemaVersion(min = "2.3.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })
@Table(indexes = {
		@Index(name = "genetogeneparalogy_createdby_index", columnList = "createdBy_id"),
		@Index(name = "genetogeneparalogy_updatedby_index", columnList = "updatedBy_id"),
		@Index(name = "genetogeneparalogy_subjectgene_index", columnList = "subjectgene_id"),
		@Index(name = "genetogeneparalogy_objectgene_index", columnList = "objectgene_id")
})
public class GeneToGeneParalogy extends AuditedObject {

	@IndexedEmbedded(includePaths = {"geneSymbol.displayText", "geneSymbol.formatText", "geneSymbol.displayText_keyword", "geneSymbol.formatText_keyword", "curie", "curie_keyword", "taxon.curie", "taxon.name", "taxon.curie_keyword", "taxon.name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private Gene subjectGene;

	@IndexedEmbedded(includePaths = {"geneSymbol.displayText", "geneSymbol.formatText", "geneSymbol.displayText_keyword", "geneSymbol.formatText_keyword", "curie", "curie_keyword", "taxon.curie", "taxon.name", "taxon.curie_keyword", "taxon.name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private Gene objectGene;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private VocabularyTerm confidence;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({ View.FieldsAndLists.class })
	@JoinTable(name = "genetogeneparalogy_predictionmethodsmatched", indexes = { @Index(name = "g2gparalogypmm_paralogyid_index", columnList = "genetogeneparalogy_id"), @Index(name = "g2gparalogypmm_pmmid_index", columnList = "predictionmethodsmatched_id")})
	private List<VocabularyTerm> predictionMethodsMatched;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({ View.FieldsAndLists.class })
	@JoinTable(name = "genetogeneparalogy_predictionmethodsnotmatched", indexes = { @Index(name = "g2gparalogypmnm_orthid_index", columnList = "genetogeneparalogy_id"), @Index(name = "g2gparalogypmnm_pmnmid_index", columnList = "predictionmethodsnotmatched_id")})
	private List<VocabularyTerm> predictionMethodsNotMatched;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({ View.FieldsAndLists.class })
	@JoinTable(name = "genetogeneparalogy_predictionmethodsnotcalled", indexes = { @Index(name = "g2gparalogypmnc_orthid_index", columnList = "genetogeneparalogy_id"), @Index(name = "g2gparalogypmnc_pmncid_index", columnList = "predictionmethodsnotcalled_id")})
	private List<VocabularyTerm> predictionMethodsNotCalled;

	@JsonView({ View.FieldsOnly.class })
	private Integer rank;

	@JsonView({ View.FieldsOnly.class })
	private Integer length;

	@JsonView({ View.FieldsOnly.class })
	private Integer similarity;

	@JsonView({ View.FieldsOnly.class })
	private Integer identity;
}
