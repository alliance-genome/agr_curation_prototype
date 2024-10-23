package org.alliancegenome.curation_api.model.entities;

import lombok.Data;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;

@Data
public class Gaf extends AuditedObject {

	private GOTerm goTerm;
	private Gene gene;

}
