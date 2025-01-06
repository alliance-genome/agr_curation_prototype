package org.alliancegenome.curation_api.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
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
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import java.util.List;
import java.util.Optional;

@Entity
@TypeBinding(binder = @TypeBinderRef(type = ReferenceTypeBridge.class))
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Schema(name = "Reference", description = "POJO that represents the Reference")
@AGRCurationSchemaVersion(min = "1.4.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = {InformationContentEntity.class}, partial = true)
public class Reference extends InformationContentEntity {

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@Fetch(FetchMode.JOIN)
	@JsonView({View.FieldsOnly.class, View.ForPublic.class})
	@JoinTable(
		indexes = {
			@Index(name = "reference_crossreference_reference_index", columnList = "Reference_id"),
			@Index(name = "reference_crossreference_crossreferences_index", columnList = "crossReferences_id")
		}
	)
	@EqualsAndHashCode.Include
	private List<CrossReference> crossReferences;

	@JsonView({View.FieldsOnly.class})
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
		return getReferenceID(true);
	}

	/**
	 * Retrieve PUB MOD ID
	 */
	@Transient
	@JsonIgnore
	public String getPubModID() {
		return getReferenceID(false);
	}

	@Transient
	private String getReferenceID(boolean pubmedIdFirst) {
		if (CollectionUtils.isEmpty(getCrossReferences())) {
			return null;
		}

		List<String> primaryXrefOrder = ReferenceConstants.primaryXrefOrder;
		if (!pubmedIdFirst) {
			String pmid = "PMID";
			primaryXrefOrder = ReferenceConstants.primaryXrefOrder.stream()
				.filter(s -> s.equals(pmid)).toList();
			boolean success = primaryXrefOrder.size() < ReferenceConstants.primaryXrefOrder.size();
			if (!success) {
				throw new RuntimeException("Could not find " + pmid + " in ReferenceConstants.primaryXrefOrder");
			}
		}

		for (String prefix : primaryXrefOrder) {
			Optional<CrossReference> opt = getCrossReferences().stream().filter(reference -> reference.getReferencedCurie().startsWith(prefix + ":")).findFirst();
			if (opt.isPresent()) {
				return opt.map(CrossReference::getReferencedCurie).orElse(null);
			}
		}

		List<String> referencedCuries = getCrossReferences().stream()
			.map(CrossReference::getReferencedCurie)
			.sorted()
			.toList();
		return referencedCuries.get(0);
	}
}
