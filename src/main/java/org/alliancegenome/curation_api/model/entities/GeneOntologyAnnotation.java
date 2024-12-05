package org.alliancegenome.curation_api.model.entities;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Entity
@Data
@EqualsAndHashCode
@Schema(name = "Gene_Disease_Annotation", description = "Annotation class representing a gene disease annotation")
@JsonTypeName("GeneOntologyAnnotation")
@AGRCurationSchemaVersion(min = "2.8.0", max = LinkMLSchemaConstants.LATEST_RELEASE)
public class GeneOntologyAnnotation extends AuditedObject {

	@ManyToOne
	private GOTerm goTerm;
	@ManyToOne
	private Gene singleGene;

}
