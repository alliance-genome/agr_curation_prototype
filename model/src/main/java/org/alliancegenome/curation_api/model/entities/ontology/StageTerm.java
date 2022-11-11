package org.alliancegenome.curation_api.model.entities.ontology;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.alliancegenome.curation_api.constants.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.hibernate.envers.Audited;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Audited
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
@AGRCurationSchemaVersion(min=LinkMLSchemaConstants.MIN_ONTOLOGY_RELEASE, max=LinkMLSchemaConstants.MAX_ONTOLOGY_RELEASE, dependencies={OntologyTerm.class})
public class StageTerm extends OntologyTerm {

}
