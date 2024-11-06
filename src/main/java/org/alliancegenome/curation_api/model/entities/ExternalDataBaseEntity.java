package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.CurieObject;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Schema(name = "ExternalDataBaseEntity", description = "POJO that represents the ExternalDataBaseEntity")
@AGRCurationSchemaVersion(min = "2.6.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { CurieObject.class })
public class ExternalDataBaseEntity extends CurieObject {

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "secondaryIdentifiers_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@ElementCollection
	@JsonView(View.FieldsAndLists.class)
	@Column(columnDefinition = "TEXT")
	@JoinTable(indexes = @Index(name = "externaldbentity_secondaryidentifiers_externaldbentity_index", columnList = "externaldatabaseentity_id"))
	private List<String> secondaryIdentifiers;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ View.FieldsOnly.class })
	private CrossReference preferredCrossReference;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinTable(indexes = {
		@Index(columnList = "externaldatabaseentity_id", name = "externaldbentity_crossreference_externaldbentity_index"),
		@Index(columnList = "crossreferences_id", name = "externaldbentity_crossreference_crossreferences_index")
	})
	@JsonView({ View.FieldsAndLists.class })
	private List<CrossReference> crossReferences;

}
