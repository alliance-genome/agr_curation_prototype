package org.alliancegenome.curation_api.controllers;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.interfaces.APIVersionInterface;
import org.alliancegenome.curation_api.model.output.APIVersionInfo;
import org.alliancegenome.curation_api.services.APIVersionInfoService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.reflections.Reflections;

import java.util.Set;
import java.util.TreeMap;

import static org.reflections.scanners.Scanners.TypesAnnotated;

@RequestScoped
public class APIVersionInfoController implements APIVersionInterface {

	@Inject
	APIVersionInfoService apiVersionInfoService;

	@ConfigProperty(name = "quarkus.application.version")
	String version;

	@ConfigProperty(name = "quarkus.application.name")
	String name;

	@ConfigProperty(name = "quarkus.hibernate-search-orm.elasticsearch.hosts")
	String esHost;

	@ConfigProperty(name = "NET")
	String env;

	@ConfigProperty(name = "mati.url")
	String matiURL;

	@Override
	public APIVersionInfo get() {

		Reflections reflections = new Reflections("org.alliancegenome.curation_api");
		Set<Class<?>> annotatedClasses = reflections.get(TypesAnnotated.with(AGRCurationSchemaVersion.class).asClass(reflections.getConfiguration().getClassLoaders()));
		TreeMap<String, String> linkMLClassVersions = new TreeMap<String, String>();
		TreeMap<String, String> submittedClassVersions = new TreeMap<String, String>();
		for (Class<?> clazz : annotatedClasses) {
			AGRCurationSchemaVersion version = clazz.getAnnotation(AGRCurationSchemaVersion.class);
			String minVersion = apiVersionInfoService.getVersionRange(version).get(0);
			String maxVersion = apiVersionInfoService.getVersionRange(version).get(1);
			String versionRange = minVersion.equals(maxVersion) ? minVersion : minVersion + " - " + maxVersion;
			if (apiVersionInfoService.isPartiallyImplemented(clazz, version)) {
				versionRange = versionRange + " (partial)";
			}
			linkMLClassVersions.put(clazz.getSimpleName(), versionRange);
			if (version.submitted()) {
				submittedClassVersions.put(clazz.getSimpleName(), versionRange);
			}
		}

		APIVersionInfo info = new APIVersionInfo();
		info.setVersion(version);
		info.setName(name);
		info.setAgrCurationSchemaVersions(linkMLClassVersions);
		info.setSubmittedClassSchemaVersions(submittedClassVersions);
		String[] array = esHost.split(",");
		if (array.length > 0) {
			info.setEsHost(array[0]);
		} else {
			info.setEsHost(esHost);
		}
		info.setMatiHost(matiURL);
		info.setEnv(env);
		return info;
	}
}
