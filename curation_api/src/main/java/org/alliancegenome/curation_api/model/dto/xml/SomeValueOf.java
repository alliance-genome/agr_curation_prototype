package org.alliancegenome.curation_api.model.dto.xml;

import javax.xml.bind.annotation.XmlElement;

public class SomeValueOf {

	@XmlElement(name = "Class", namespace = "http://www.w3.org/2002/07/owl#")
	private RDFClass rdfClass;

}
