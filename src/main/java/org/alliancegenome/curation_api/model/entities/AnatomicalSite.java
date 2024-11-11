package org.alliancegenome.curation_api.model.entities;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.bridges.BooleanValueBridge;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.ontology.AnatomicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.UBERONTerm;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@AGRCurationSchemaVersion(min = "2.7.0", max = LinkMLSchemaConstants.LATEST_RELEASE)
@Schema(name = "Anatomical_Site", description = "Anatomical part of an expression pattern")
@Table(indexes = {
	@Index(name = "anatomicalsite_anatomicalstructure_index ", columnList = "anatomicalstructure_id"),
	@Index(name = "anatomicalsite_anatomicalsubstructure_index", columnList = "anatomicalsubstructure_id"),
	@Index(name = "anatomicalsite_cellularcomponentterm_index", columnList = "cellularcomponentterm_id")}
)
public class AnatomicalSite extends AuditedObject {
	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class})
	private AnatomicalTerm anatomicalStructure;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class})
	private AnatomicalTerm anatomicalSubstructure;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class})
	private GOTerm cellularComponentTerm;

	//celullar compoent ribbon -- slim
	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class})
	private GOTerm cellularComponentRibbonTerm;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
	@KeywordField(name = "cellularComponentOther_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
	@JsonView({ View.FieldsOnly.class })
	@Column(columnDefinition = "boolean default false", nullable = false)
	private Boolean cellularComponentOther = false;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "anatomicalstructurequalifiers_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@ManyToMany
	@JsonView({View.FieldsOnly.class})
	@JoinTable(
		name = "anatomicalsite_anatomicalstructurequalifiers",
		indexes = {
			@Index(name = "anatomicalstructurequalifiers_anatomicalsite_index", columnList = "anatomicalsite_id"),
			@Index(name = "anatomicalstructurequalifiers_structurequalifiers_index", columnList = "anatomicalstructurequalifiers_id")}
	)
	private List<OntologyTerm> anatomicalStructureQualifiers;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "anatomicalsubstructurequalifiers_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@ManyToMany
	@JsonView({View.FieldsOnly.class})
	@JoinTable(
		name = "anatomicalsite_anatomicalsubstructurequalifiers",
		indexes = {
			@Index(name = "anatomicalsubstructurequalifiers_anatomicalsite_index", columnList = "anatomicalsite_id"),
			@Index(name = "anatomicalsubstructurequalifiers_qualifiers_index", columnList = "anatomicalsubstructurequalifiers_id")}
	)
	private List<OntologyTerm> anatomicalSubstructureQualifiers;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "cellularcomponentqualifiers_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@ManyToMany
	@JsonView({View.FieldsOnly.class})
	@JoinTable(
		name = "anatomicalsite_cellularcomponentqualifiers",
		indexes = {
			@Index(name = "cellularcomponentqualifiers_anatomicalsite_index", columnList = "anatomicalsite_id"),
			@Index(name = "cellularcomponentqualifiers_cellularcomponentqualifiers_index", columnList = "cellularcomponentqualifiers_id")}
	)
	private List<OntologyTerm> cellularComponentQualifiers;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "anatomicalstructureuberonterms_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@ManyToMany
	@JsonView({View.FieldsOnly.class})
	@JoinTable(
		name = "anatomicalsite_anatomicalstructureuberonterms",
		indexes = {
			@Index(name = "anatomicalstructureuberonterms_anatomicalsite_index", columnList = "anatomicalsite_id"),
			@Index(name = "anatomicalstructureuberonterms_uberonterms_index", columnList = "anatomicalstructureuberonterms_id")}
	)
	private List<UBERONTerm> anatomicalStructureUberonTerms;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "anatomicalsubstructureuberonterms_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@ManyToMany
	@JsonView({View.FieldsOnly.class})
	@JoinTable(
		name = "anatomicalsite_anatomicalsubstructureuberonterms",
		indexes = {
			@Index(name = "anatomicalsubstructureuberonterms_anatomicalsite_index", columnList = "anatomicalsite_id"),
			@Index(name = "anatomicalsubstructureuberonterms_uberonterms_index", columnList = "anatomicalsubstructureuberonterms_id")}
	)
	private List<UBERONTerm> anatomicalSubstructureUberonTerms;
}
