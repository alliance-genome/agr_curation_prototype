package org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SlotAnnotation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "1.7.2", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { SlotAnnotation.class })
@Schema(name = "GeneSecondaryIdSlotAnnotation", description = "POJO representing a gene secondary ID slot annotation")
public class GeneSecondaryIdSlotAnnotation extends SecondaryIdSlotAnnotation {

	@ManyToOne
	@JsonBackReference
	private Gene singleGene;

}
