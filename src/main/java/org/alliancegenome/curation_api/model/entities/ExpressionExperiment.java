package org.alliancegenome.curation_api.model.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.SubmittedObject;
import org.alliancegenome.curation_api.model.entities.ontology.MMOTerm;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import java.util.List;

@MappedSuperclass
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = GeneExpressionExperiment.class, name = "GeneExpressionExperiment") })
@Indexed
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@AGRCurationSchemaVersion(min = "1.7.3", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { SubmittedObject.class }, partial = true)
public abstract class ExpressionExperiment extends SubmittedObject {

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "uniqueId_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@Column(length = 3500)
	@JsonView({ View.FieldsOnly.class })
	@EqualsAndHashCode.Include
	private String uniqueId;

	@IndexedEmbedded(includePaths = {"geneSymbol.displayText", "geneSymbol.formatText", "geneSymbol.displayText_keyword", "geneSymbol.formatText_keyword", "curie", "curie_keyword", "taxon.curie", "taxon.name", "taxon.curie_keyword", "taxon.name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class })
	private Gene entityAssayed;

	@IndexedEmbedded(includePaths = {"curie", "primaryCrossReferenceCurie", "crossReferences.referencedCurie", "curie_keyword", "primaryCrossReferenceCurie_keyword", "crossReferences.referencedCurie_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private Reference singleReference;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class})
	private MMOTerm expressionAssayUsed;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({View.FieldsAndLists.class})
	private List<GeneExpressionAnnotation> expressionAnnotations;

	@Transient private Allele specimenGenomicModel;
	@Transient private List<Reagent> detectionReagents;
	@Transient private List<Allele> specimenAlleles;
	@Transient private List<Note> relatedNotes;
	@Transient private List<ConditionRelation> conditionRelations;
}
