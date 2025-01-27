package org.alliancegenome.curation_api.model.entities.associations.agmAssociations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Association;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.GENOTerm;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "2.9.1", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { Association.class })
@Schema(name = "AgmAlleleAssociation", description = "POJO representing an association between an AGM and a Allele")

@Table(indexes = {
	@Index(name = "AgmAlleleAssociation_internal_index", columnList = "internal"),
	@Index(name = "AgmAlleleAssociation_obsolete_index", columnList = "obsolete"),
	@Index(name = "AgmAlleleAssociation_createdBy_index", columnList = "createdBy_id"),
	@Index(name = "AgmAlleleAssociation_updatedBy_index", columnList = "updatedBy_id"),
	@Index(name = "AgmAlleleAssociation_relation_index", columnList = "relation_id"),
	@Index(name = "AgmAlleleAssociation_zygosity_index", columnList = "zygosity_id"),
	@Index(name = "AgmAlleleAssociation_agmAssociationSubject_index", columnList = "agmAssociationSubject_id"),
	@Index(name = "AgmAlleleAssociation_AgmAlleleAssociationObject_index", columnList = "agmAlleleAssociationObject_id")
})
public class AgmAlleleAssociation extends Association {

	@IndexedEmbedded(includePaths = {
		"curie", "primaryExternalId", "modInternalId", "curie_keyword", "primaryExternalId_keyword", "modInternalId_keyword",
		"alleleSymbol.formatText", "alleleSymbol.displayText", "alleleSymbol.formatText_keyword", "alleleSymbol.displayText_keyword",
		"alleleFullName.formatText", "alleleFullName.displayText", "alleleFullName.formatText_keyword", "alleleFullName.displayText_keyword",
		"alleleSynonyms.formatText", "alleleSynonyms.displayText", "alleleSynonyms.formatText_keyword", "alleleSynonyms.displayText_keyword",
		"alleleSecondaryIds.secondaryId", "alleleSecondaryIds.secondaryId_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	@JsonIgnoreProperties({"alleleGeneAssociations", "alleleVariantAssociations"})
	private Allele agmAlleleAssociationObject;

	@IndexedEmbedded(includePaths = {
		"curie", "name", "curie_keyword", "name_keyword",
		"primaryExternalId", "primaryExternalId_keyword", "modInternalId", "modInternalId_keyword" })
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	@JsonIgnoreProperties({"components", "constructGenomicEntityAssociations", "agmSequenceTargetingReagentAssociations"})
	@Fetch(FetchMode.JOIN)
	private AffectedGenomicModel agmAssociationSubject;

	@IndexedEmbedded(includePaths = {"curie", "name", "secondaryIdentifiers", "synonyms.name", "abbreviation",
	"curie_keyword", "name_keyword", "secondaryIdentifiers_keyword", "synonyms.name_keyword", "abbreviation_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView(View.FieldsOnly.class)
	private GENOTerm zygosity;

	@IndexedEmbedded(includePaths = { "name", "name_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private VocabularyTerm relation;

}
