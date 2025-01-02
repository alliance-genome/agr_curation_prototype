package org.alliancegenome.curation_api.model.output;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.alliancegenome.curation_api.view.View;

import java.util.TreeMap;

@Data
public class APIVersionInfo {

	@JsonView(View.FieldsOnly.class)
	private String name;
	@JsonView(View.FieldsOnly.class)
	private String version;
	@JsonView(View.FieldsOnly.class)
	private TreeMap<String, String> agrCurationSchemaVersions;
	@JsonView(View.FieldsOnly.class)
	private TreeMap<String, String> submittedClassSchemaVersions;
	@JsonView(View.FieldsOnly.class)
	private String esHost;
	@JsonView(View.FieldsOnly.class)
	private String env;
	@JsonView(View.FieldsOnly.class)
	private String matiHost;
}
