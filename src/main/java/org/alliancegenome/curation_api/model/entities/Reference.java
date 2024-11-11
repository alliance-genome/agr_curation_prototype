package org.alliancegenome.curation_api.model.entities;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.constants.ReferenceConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.bridges.ReferenceTypeBridge;
import org.alliancegenome.curation_api.view.View;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.TypeBinderRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.TypeBinding;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@TypeBinding(binder = @TypeBinderRef(type = ReferenceTypeBridge.class))
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Schema(name = "Reference", description = "POJO that represents the Reference")
@AGRCurationSchemaVersion(min = "1.4.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { InformationContentEntity.class }, partial = true)
public class Reference extends InformationContentEntity {

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@Fetch(FetchMode.JOIN)
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	@JoinTable(
		indexes = {
			@Index(name = "reference_crossreference_reference_index", columnList = "Reference_id"),
			@Index(name = "reference_crossreference_crossreferences_index", columnList = "crossReferences_id")
		}
	)
	@EqualsAndHashCode.Include private List<CrossReference> crossReferences;

	@JsonView({ View.FieldsOnly.class })
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "shortCitation_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@Column(columnDefinition = "TEXT")
	private String shortCitation;

	/**
	 * Retrieve PMID if available in the crossReference collection otherwise MOD ID
	 */
	@Transient
	@JsonIgnore
	public String getReferenceID() {
		if (CollectionUtils.isEmpty(getCrossReferences())) {
			return null;
		}

		for (String prefix : ReferenceConstants.primaryXrefOrder) {
			Optional<CrossReference> opt = getCrossReferences().stream().filter(reference -> reference.getReferencedCurie().startsWith(prefix + ":")).findFirst();
			if (opt.isPresent()) {
				return opt.map(CrossReference::getReferencedCurie).orElse(null);
			}
		}

		List<String> referencedCuries = getCrossReferences().stream().map(CrossReference::getReferencedCurie).collect(Collectors.toList());
		Collections.sort(referencedCuries);

		return referencedCuries.get(0);
	}
}
