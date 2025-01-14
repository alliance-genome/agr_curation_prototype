package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import jakarta.persistence.*;
import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.bridges.BooleanAndNullValueBridge;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleVariantAssociation;
import org.alliancegenome.curation_api.model.entities.associations.constructAssociations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleDatabaseStatusSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFunctionalImpactSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleNomenclatureEventSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotation;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(
	exclude = {
		"alleleGeneAssociations", "alleleVariantAssociations", "alleleDiseaseAnnotations", "alleleMutationTypes", "alleleSymbol", "alleleFullName", "alleleSynonyms",
		"alleleSecondaryIds", "alleleInheritanceModes", "alleleFunctionalImpacts", "alleleGermlineTransmissionStatus", "alleleDatabaseStatus",
		"alleleNomenclatureEvents", "constructGenomicEntityAssociations"
	},
	callSuper = true
)
@AGRCurationSchemaVersion(min = "1.7.3", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { GenomicEntity.class }, partial = true)
@Table(indexes = { @Index(name = "allele_inCollection_index", columnList = "inCollection_id") })
public class Allele extends GenomicEntity {

	@IndexedEmbedded(
		includePaths = {
			"primaryCrossReferenceCurie", "crossReferences.referencedCurie", "crossReferences.displayName", "curie", "primaryCrossReferenceCurie_keyword",
			"crossReferences.referencedCurie_keyword", "crossReferences.displayName_keyword", "curie_keyword"
		}
	)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@Fetch(FetchMode.JOIN)
	@JoinTable(indexes = {
		@Index(name = "allele_reference_allele_index", columnList = "allele_id"),
		@Index(name = "allele_reference_references_index", columnList = "references_id"),
		@Index(name = "allele_reference_allele_references_index", columnList = "allele_id, references_id")
	})
	@JsonView({ View.FieldsAndLists.class, View.AlleleView.class })
	private List<Reference> references;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@JsonView({ View.FieldsOnly.class })
	private VocabularyTerm inCollection;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = BooleanAndNullValueBridge.class))
	@KeywordField(name = "isExtinct_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, valueBridge = @ValueBridgeRef(type = BooleanAndNullValueBridge.class))
	@JsonView({ View.FieldsOnly.class })
	private Boolean isExtinct;

	@OneToMany(mappedBy = "diseaseAnnotationSubject", cascade = CascadeType.ALL)
	private List<AlleleDiseaseAnnotation> alleleDiseaseAnnotations;

	@IndexedEmbedded(includePaths = { "mutationTypes.curie", "mutationTypes.name", "evidence.curie", "mutationTypes.curie_keyword", "mutationTypes.name_keyword", "evidence.curie_keyword"})
	@OneToMany(mappedBy = "singleAllele", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ View.FieldsAndLists.class, View.AlleleView.class })
	private List<AlleleMutationTypeSlotAnnotation> alleleMutationTypes;
	
	@IndexedEmbedded(
		includePaths = {
			"inheritanceMode.name", "phenotypeTerm.curie", "phenotypeTerm.name", "phenotypeStatement", "evidence.curie", "inheritanceMode.name_keyword",
			"phenotypeTerm.curie_keyword", "phenotypeTerm.name_keyword", "phenotypeStatement_keyword", "evidence.curie_keyword"
		}
	)
	@OneToMany(mappedBy = "singleAllele", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ View.FieldsAndLists.class, View.AlleleView.class })
	private List<AlleleInheritanceModeSlotAnnotation> alleleInheritanceModes;

	@IndexedEmbedded(includePaths = {
			"displayText", "formatText", "nameType.name", "synonymScope.name", "evidence.curie", "displayText_keyword",
			"formatText_keyword", "nameType.name_keyword", "synonymScope.name_keyword", "evidence.curie_keyword"
		}
	)
	@OneToOne(mappedBy = "singleAllele", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private AlleleSymbolSlotAnnotation alleleSymbol;

	@IndexedEmbedded(
		includePaths = {
			"displayText", "formatText", "nameType.name", "synonymScope.name", "evidence.curie", "displayText_keyword",
			"formatText_keyword", "nameType.name_keyword", "synonymScope.name_keyword", "evidence.curie_keyword"
		}
	)
	@OneToOne(mappedBy = "singleAllele", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ View.FieldsOnly.class })
	private AlleleFullNameSlotAnnotation alleleFullName;

	@IndexedEmbedded(
		includePaths = {
			"displayText", "formatText", "nameType.name", "synonymScope.name", "evidence.curie", "displayText_keyword", "formatText_keyword", "nameType.name_keyword",
			"synonymScope.name_keyword", "evidence.curie_keyword"
		}
	)
	@OneToMany(mappedBy = "singleAllele", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ View.FieldsAndLists.class, View.AlleleView.class })
	private List<AlleleSynonymSlotAnnotation> alleleSynonyms;

	@IndexedEmbedded(includePaths = { "secondaryId", "evidence.curie", "secondaryId_keyword", "evidence.curie_keyword"})
	@OneToMany(mappedBy = "singleAllele", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ View.FieldsAndLists.class, View.AlleleView.class })
	private List<AlleleSecondaryIdSlotAnnotation> alleleSecondaryIds;
	
	@IndexedEmbedded(includePaths = { "germlineTransmissionStatus.name", "evidence.curie", "germlineTransmissionStatus.name_keyword", "evidence.curie_keyword"})
	@OneToOne(mappedBy = "singleAllele", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ View.FieldsOnly.class })
	private AlleleGermlineTransmissionStatusSlotAnnotation alleleGermlineTransmissionStatus;

	@IndexedEmbedded(
		includePaths = {
			"functionalImpacts.name", "phenotypeTerm.curie", "phenotypeTerm.name", "phenotypeStatement", "evidence.curie",
			"functionalImpacts.name_keyword", "phenotypeTerm.curie_keyword", "phenotypeTerm.name_keyword", "phenotypeStatement_keyword",
			"evidence.curie_keyword"
		}
	)
	@OneToMany(mappedBy = "singleAllele", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ View.FieldsAndLists.class, View.AlleleView.class })
	private List<AlleleFunctionalImpactSlotAnnotation> alleleFunctionalImpacts;

	@IndexedEmbedded(includePaths = { "databaseStatus.name", "evidence.curie", "databaseStatus.name_keyword", "evidence.curie_keyword"})
	@OneToOne(mappedBy = "singleAllele", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ View.FieldsOnly.class })
	private AlleleDatabaseStatusSlotAnnotation alleleDatabaseStatus;
	
	@IndexedEmbedded(includePaths = { "nomenclatureEvent.name", "evidence.curie", "nomenclatureEvent.name_keyword", "evidence.curie_keyword"})
	@OneToMany(mappedBy = "singleAllele", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@JsonView({ View.FieldsAndLists.class, View.AlleleView.class })
	private List<AlleleNomenclatureEventSlotAnnotation> alleleNomenclatureEvents;
	
	@IndexedEmbedded(
		includePaths = {
			"alleleGeneAssociationObject.curie", "alleleGeneAssociationObject.geneSymbol.displayText", "alleleGeneAssociationObject.geneSymbol.formatText", "alleleGeneAssociationObject.geneFullName.displayText",
			"alleleGeneAssociationObject.geneFullName.formatText", "alleleGeneAssociationObject.curie_keyword", "alleleGeneAssociationObject.geneSymbol.displayText_keyword",
			"alleleGeneAssociationObject.geneSymbol.formatText_keyword", "alleleGeneAssociationObject.geneFullName.displayText_keyword", "alleleGeneAssociationObject.geneFullName.formatText_keyword",
			"alleleGeneAssociationObject.primaryExternalId", "alleleGeneAssociationObject.modInternalId", "alleleGeneAssociationObject.primaryExternalId_keyword", "alleleGeneAssociationObject.modInternalId_keyword"
		}
	)
	@OneToMany(mappedBy = "alleleAssociationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ View.FieldsAndLists.class, View.AlleleDetailView.class })
	private List<AlleleGeneAssociation> alleleGeneAssociations;
	
	@IndexedEmbedded(
		includePaths = {
			"alleleVariantAssociationObject.curie", "alleleGeneAssociationObject.curie_keyword",
			"alleleVariantAssociationObject.primaryExternalId", "alleleVariantAssociationObject.primaryExternalId_keyword",
			"alleleVariantAssociationObject.modInternalId", "alleleVariantAssociationObject.modInternalId_keyword"
		}
	)
	@OneToMany(mappedBy = "alleleAssociationSubject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ View.FieldsAndLists.class, View.AlleleDetailView.class })
	private List<AlleleVariantAssociation> alleleVariantAssociations;
	
	@IndexedEmbedded(includePaths = {"freeText", "freeText_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ View.FieldsAndLists.class, View.AlleleView.class })
	@JoinTable(indexes = {
		@Index(name = "allele_note_allele_index", columnList = "allele_id"),
		@Index(name = "allele_note_relatednotes_index", columnList = "relatedNotes_id")})
	private List<Note> relatedNotes;


	@IndexedEmbedded(includePaths = {
		"constructAssociationSubject.curie", "constructAssociationSubject.constructSymbol.displayText", "constructAssociationSubject.constructSymbol.formatText",
		"constructAssociationSubject.constructFullName.displayText", "constructAssociationSubject.constructFullName.formatText", "constructAssociationSubject.primaryExternalId",
		"constructAssociationSubject.curie_keyword", "constructAssociationSubject.constructSymbol.displayText_keyword", "constructAssociationSubject.constructSymbol.formatText_keyword",
		"constructAssociationSubject.constructFullName.displayText_keyword", "constructAssociationSubject.constructFullName.formatText_keyword", "constructAssociationSubject.primaryExternalId_keyword"
	})
	@OneToMany(mappedBy = "constructGenomicEntityAssociationObject", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ View.FieldsAndLists.class, View.GeneDetailView.class })
	private List<ConstructGenomicEntityAssociation> constructGenomicEntityAssociations;

}
