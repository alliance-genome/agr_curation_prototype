package org.alliancegenome.curation_api.model.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.associations.agmAssociations.AgmAgmAssociation;
import org.alliancegenome.curation_api.model.entities.associations.agmAssociations.AgmAlleleAssociation;
import org.alliancegenome.curation_api.model.entities.associations.agmAssociations.AgmSequenceTargetingReagentAssociation;
import org.alliancegenome.curation_api.model.entities.associations.constructAssociations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.agmSlotAnnotations.AgmSecondaryIdSlotAnnotation;
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
@ToString(exclude = {"agmDiseaseAnnotations", "constructGenomicEntityAssociations", "agmSecondaryIds", "agmSequenceTargetingReagentAssociations", "components"}, callSuper = true)
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

	@IndexedEmbedded(includePaths = {"secondaryId", "evidence.curie", "secondaryId_keyword", "evidence.curie_keyword"})
	@OneToMany(mappedBy = "singleAgm", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({View.FieldsAndLists.class, View.AffectedGenomicModelView.class})
	private List<AgmSecondaryIdSlotAnnotation> agmSecondaryIds;


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

	@IndexedEmbedded(includePaths = {
		"agmSequenceTargetingReagentAssociationObject.name",
		"agmSequenceTargetingReagentAssociationObject.name_keyword",
		"agmSequenceTargetingReagentAssociationObject.synonyms",
		"agmSequenceTargetingReagentAssociationObject.synonyms_keyword",
		"agmSequenceTargetingReagentAssociationObject.secondaryIdentifiers",
		"agmSequenceTargetingReagentAssociationObject.secondaryIdentifiers_keyword"
	})
	@OneToMany(mappedBy = "agmAssociationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({View.FieldsAndLists.class, View.AffectedGenomicModelDetailView.class})
	private List<AgmSequenceTargetingReagentAssociation> agmSequenceTargetingReagentAssociations;

	@IndexedEmbedded(includePaths = {
		"agmAlleleAssociationObject.curie", "agmAlleleAssociationObject.primaryExternalId", "agmAlleleAssociationObject.modInternalId", "agmAlleleAssociationObject.curie_keyword", "agmAlleleAssociationObject.primaryExternalId_keyword", "agmAlleleAssociationObject.modInternalId_keyword",
		"agmAlleleAssociationObject.alleleSymbol.formatText", "agmAlleleAssociationObject.alleleSymbol.displayText", "agmAlleleAssociationObject.alleleSymbol.formatText_keyword", "agmAlleleAssociationObject.alleleSymbol.displayText_keyword",
		"agmAlleleAssociationObject.alleleFullName.formatText", "agmAlleleAssociationObject.alleleFullName.displayText", "agmAlleleAssociationObject.alleleFullName.formatText_keyword", "agmAlleleAssociationObject.alleleFullName.displayText_keyword",
		"agmAlleleAssociationObject.alleleSynonyms.formatText", "agmAlleleAssociationObject.alleleSynonyms.displayText", "agmAlleleAssociationObject.alleleSynonyms.formatText_keyword", "agmAlleleAssociationObject.alleleSynonyms.displayText_keyword",
		"agmAlleleAssociationObject.alleleSecondaryIds.secondaryId", "agmAlleleAssociationObject.alleleSecondaryIds.secondaryId_keyword"
	})
	@OneToMany(mappedBy = "agmAssociationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({View.FieldsAndLists.class, View.AffectedGenomicModelDetailView.class})
	private List<AgmAlleleAssociation> components;
	@IndexedEmbedded(includePaths = {
		"agmAssociationSubject.name",
		"agmAssociationSubject.name_keyword",
		"agmAssociationSubject.synonyms",
		"agmAssociationSubject.synonyms_keyword",
		"agmAssociationSubject.secondaryIdentifiers",
		"agmAssociationSubject.secondaryIdentifiers_keyword"
	})
	@OneToMany(mappedBy = "agmAssociationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({View.FieldsAndLists.class, View.AffectedGenomicModelDetailView.class})
	private List<AgmAgmAssociation> agmAgmAssociations;

	@IndexedEmbedded(includePaths = {
		"agmAgmAssociationObject.name",
		"agmAgmAssociationObject.name_keyword",
		"agmAgmAssociationObject.synonyms",
		"agmAgmAssociationObject.synonyms_keyword",
		"agmAgmAssociationObject.secondaryIdentifiers",
		"agmAgmAssociationObject.secondaryIdentifiers_keyword"
	})
	@OneToMany(mappedBy = "agmAgmAssociationObject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({View.FieldsAndLists.class, View.AffectedGenomicModelDetailView.class})
	private List<AgmAgmAssociation> agmAgmObjectAssociations;

}
