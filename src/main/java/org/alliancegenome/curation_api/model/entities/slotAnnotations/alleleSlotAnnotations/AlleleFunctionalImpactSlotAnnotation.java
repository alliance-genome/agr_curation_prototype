package org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.PhenotypeTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SlotAnnotation;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "1.5.1", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { SlotAnnotation.class })
@Schema(name = "AlleleFunctionalImpactSlotAnnotation", description = "POJO representing an allele functional impact slot annotation")
public class AlleleFunctionalImpactSlotAnnotation extends SlotAnnotation {

	@ManyToOne
	@JsonBackReference
	private Allele singleAllele;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@Fetch(FetchMode.SELECT)
	@JoinTable(
		joinColumns = @JoinColumn(name = "slotannotation_id"),
		inverseJoinColumns = @JoinColumn(name = "functionalimpacts_id"),
		indexes = {
			@Index(name = "slotannotation_id_index", columnList = "slotannotation_id"),
			@Index(name = "slotannotation_functionalimpacts_index", columnList = "functionalimpacts_id")
		}
	)
	@JsonView({ View.FieldsAndLists.class, View.AlleleView.class })
	private List<VocabularyTerm> functionalImpacts;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@JsonView({ View.FieldsOnly.class })
	private PhenotypeTerm phenotypeTerm;
	
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "phenotypeStatement_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class })
	@EqualsAndHashCode.Include
	@Column(columnDefinition = "TEXT")
	protected String phenotypeStatement;
}
