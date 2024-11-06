package org.alliancegenome.curation_api.jobs.executors.gff;

import java.util.List;

import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.jobs.executors.LoadFileExecutor;
import org.alliancegenome.curation_api.services.Gff3Service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Gff3Executor extends LoadFileExecutor {

	@Inject Gff3Service gff3Service;
	
	public String loadGenomeAssemblyFromGFF(List<String> gffHeaderData) throws ValidationException {
		for (String header : gffHeaderData) {
			if (header.startsWith("#!assembly")) {
				String assemblyName = header.split(" ")[1];
				return assemblyName;
			}
		}
		return null;
	}
	
}
