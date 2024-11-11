package org.alliancegenome.curation_api.model.entities;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.ontology.MITerm;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Indexed
@Entity
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Schema(name = "Gene_Molecular_Interaction", description = "Class representing an interaction between gene products")
@AGRCurationSchemaVersion(min = "2.2.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { GeneInteraction.class })

@Table(indexes = {
	@Index(name = "GeneMolecularInteraction_internal_index", columnList = "internal"),
	@Index(name = "GeneMolecularInteraction_obsolete_index", columnList = "obsolete"),
	@Index(name = "GeneMolecularInteraction_interactionId_index", columnList = "interactionId"),
	@Index(name = "GeneMolecularInteraction_uniqueId_index", columnList = "uniqueId"),
	@Index(name = "GeneMolecularInteraction_createdBy_index", columnList = "createdBy_id"),
	@Index(name = "GeneMolecularInteraction_updatedBy_index", columnList = "updatedBy_id"),
	@Index(name = "GeneMolecularInteraction_geneAssociationSubject_index", columnList = "geneAssociationSubject_id"),
	@Index(name = "GeneMolecularInteraction_geneGeneAssociationObject_index", columnList = "geneGeneAssociationObject_id"),
	@Index(name = "GeneMolecularInteraction_relation_index", columnList = "relation_id"),
	@Index(name = "GeneMolecularInteraction_interactionSource_index", columnList = "interactionSource_id"),
	@Index(name = "GeneMolecularInteraction_interactionType_index", columnList = "interactionType_id"),
	@Index(name = "GeneMolecularInteraction_interactorARole_index", columnList = "interactorARole_id"),
	@Index(name = "GeneMolecularInteraction_interactorAType_index", columnList = "interactorAType_id"),
	@Index(name = "GeneMolecularInteraction_interactorBRole_index", columnList = "interactorBRole_id"),
	@Index(name = "GeneMolecularInteraction_interactorBType_index", columnList = "interactorBType_id"),
	@Index(name = "GeneMolecularInteraction_aggregationDatabase_index", columnList = "aggregationDatabase_id"),
	@Index(name = "GeneMolecularInteraction_detectionMethod_index", columnList = "detectionMethod_id")
})

public class GeneMolecularInteraction extends GeneInteraction {

	@IndexedEmbedded(includePaths = {"curie", "name", "secondaryIdentifiers", "synonyms.name", "namespace",
			"curie_keyword", "name_keyword", "secondaryIdentifiers_keyword", "synonyms.name_keyword", "namespace_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@JsonView({ View.FieldsOnly.class })
	private MITerm aggregationDatabase;
	
	@IndexedEmbedded(includePaths = {"curie", "name", "secondaryIdentifiers", "synonyms.name", "namespace",
			"curie_keyword", "name_keyword", "secondaryIdentifiers_keyword", "synonyms.name_keyword", "namespace_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@JsonView({ View.FieldsOnly.class })
	private MITerm detectionMethod;
}