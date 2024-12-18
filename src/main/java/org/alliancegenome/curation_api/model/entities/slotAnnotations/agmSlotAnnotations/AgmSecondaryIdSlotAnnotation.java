package org.alliancegenome.curation_api.model.entities.slotAnnotations.agmSlotAnnotations;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SlotAnnotation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "1.4.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = {SlotAnnotation.class})
@Schema(name = "AgmSecondaryIdSlotAnnotation", description = "POJO representing an AGM secondary ID slot annotation")
public class AgmSecondaryIdSlotAnnotation extends SecondaryIdSlotAnnotation {

	@ManyToOne
	@JsonBackReference
	private AffectedGenomicModel singleAgm;

}
