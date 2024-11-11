package org.alliancegenome.curation_api.model.entities.slotAnnotations;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "SlotAnnotationType", length = 96)
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Schema(name = "SlotAnnotation", description = "POJO that represents a SlotAnnotation")
@ToString(callSuper = true)
@Table(indexes = {
	@Index(name = "slotannotation_createdby_index", columnList = "createdBy_id"),
	@Index(name = "slotannotation_updatedby_index", columnList = "updatedBy_id"),
	@Index(name = "slotannotation_singleallele_index", columnList = "singleallele_id"),
	@Index(name = "slotannotation_singleconstruct_index", columnList = "singleConstruct_id"),
	@Index(name = "slotannotation_singlegene_index", columnList = "singlegene_id"),
	@Index(name = "slotannotation_inheritancemode_index", columnList = "inheritancemode_id"),
	@Index(name = "slotannotation_phenotypeterm_index", columnList = "phenotypeterm_id"),
	@Index(name = "slotannotation_status_index", columnList = "germlinetransmissionstatus_id"),
	@Index(name = "slotannotation_nomenclatureevent_index", columnList = "nomenclatureevent_id"),
	@Index(name = "slotannotation_databasestatus_index", columnList = "databasestatus_id"),
	@Index(name = "slotannotation_componentsymbol_index", columnList = "componentSymbol"),
	@Index(name = "slotannotation_taxon_index", columnList = "taxon_id"),
	@Index(name = "slotannotation_relation_index", columnList = "relation_id"),
	@Index(name = "slotannotation_nametype_index", columnList = "nameType_id"),
	@Index(name = "slotannotation_synonymscope_index", columnList = "synonymScope_id")
})



@AGRCurationSchemaVersion(min = "1.4.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })
public class SlotAnnotation extends AuditedObject {

	@IndexedEmbedded(includeDepth = 2)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JoinTable(indexes = {
		@Index(name = "slotannotation_informationcontententity_slotannotation_index", columnList = "slotannotation_id"),
		@Index(name = "slotannotation_informationcontententity_evidence_index", columnList = "evidence_id")
	})
	@JsonView({ View.FieldsAndLists.class, View.AlleleView.class, View.GeneView.class, View.ConstructView.class })
	private List<InformationContentEntity> evidence;

}
