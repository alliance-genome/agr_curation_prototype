package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.ontology.StageTerm;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@AGRCurationSchemaVersion(min = "2.2.3", max = LinkMLSchemaConstants.LATEST_RELEASE)
@Schema(name = "Temporal_Context", description = "Temporal expression pattern")
@Table(indexes = {
	@Index(name = "temporalcontext_developmentalstagestart_index ", columnList = "developmentalstagestart_id"),
	@Index(name = "temporalcontext_developmentalstagestop_index ", columnList = "developmentalstagestop_id"),
	@Index(name = "temporalcontext_age_index ", columnList = "age")
})
public class TemporalContext extends AuditedObject {

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class})
	private StageTerm developmentalStageStart;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class})
	private StageTerm developmentalStageStop;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "age_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({View.FieldsOnly.class})
	@Column(length = 1000)
	private String age;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "temporalqualifiers_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@ManyToMany
	@JsonView({View.FieldsOnly.class})
	@JoinTable(
		name = "temporalcontext_temporalqualifiers",
		indexes = {
			@Index(name = "temporalqualifiers_temporalcontext_index", columnList = "temporalcontext_id"),
			@Index(name = "temporalqualifiers_temporalqualifiers_index", columnList = "temporalqualifiers_id")}
	)
	private List<VocabularyTerm> temporalQualifiers;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "stageuberonslimterms_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@ManyToMany
	@JsonView({View.FieldsOnly.class})
	@JoinTable(
		name = "temporalcontext_stageuberonslimterms",
		indexes = {
			@Index(name = "stageuberonslimterms_temporalcontext_index", columnList = "temporalcontext_id"),
			@Index(name = "stageuberonslimterms_uberonterms_index", columnList = "stageuberonslimterms_id")}
	)
	private List<VocabularyTerm> stageUberonSlimTerms;
}
