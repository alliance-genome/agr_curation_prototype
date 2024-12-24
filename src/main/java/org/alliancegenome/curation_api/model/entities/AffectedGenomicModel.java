package org.alliancegenome.curation_api.model.entities;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.associations.constructAssociations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import java.util.List;

@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"agmDiseaseAnnotations", "constructGenomicEntityAssociations"}, callSuper = true)
@Schema(name = "AffectedGenomicModel", description = "POJO that represents the AGM")
@AGRCurationSchemaVersion(min = "1.5.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = {GenomicEntity.class}, partial = true)
public class AffectedGenomicModel extends GenomicEntity {

	@Column(columnDefinition = "TEXT")
	@JsonView({View.FieldsOnly.class, View.ForPublic.class})
	private String name;

	@OneToMany(mappedBy = "diseaseAnnotationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<AGMDiseaseAnnotation> agmDiseaseAnnotations;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class, View.ForPublic.class})
	private VocabularyTerm subtype;

	@IndexedEmbedded(includePaths = {
		"constructAssociationSubject.curie", "constructAssociationSubject.constructSymbol.displayText", "constructAssociationSubject.constructSymbol.formatText",
		"constructAssociationSubject.constructFullName.displayText", "constructAssociationSubject.constructFullName.formatText", "constructAssociationSubject.primaryExternalId",
		"constructAssociationSubject.curie_keyword", "constructAssociationSubject.constructSymbol.displayText_keyword", "constructAssociationSubject.constructSymbol.formatText_keyword",
		"constructAssociationSubject.constructFullName.displayText_keyword", "constructAssociationSubject.constructFullName.formatText_keyword", "constructAssociationSubject.primaryExternalId_keyword"
	})
	@OneToMany(mappedBy = "constructGenomicEntityAssociationObject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({View.FieldsAndLists.class, View.GeneDetailView.class})
	private List<ConstructGenomicEntityAssociation> constructGenomicEntityAssociations;

	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "synonyms_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({View.FieldsAndLists.class, View.AffectedGenomicModelView.class})
	@ElementCollection
	@JoinTable(indexes = @Index(columnList = "affectedgenomicmodel_id"))
	@Column(columnDefinition = "TEXT")
	private List<String> synonyms;

}
