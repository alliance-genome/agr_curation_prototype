package org.alliancegenome.curation_api.resources;

import java.util.HashMap;
import java.util.Map;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

@QuarkusTestResource(TestContainerResource.Initializer.class)
public class TestContainerResource {

	public static class Initializer implements QuarkusTestResourceLifecycleManager {

		private OpenSearchContainer osContainer;
		private PostgreSQLContainer pgContainer;
		//private ElasticsearchContainer esContainer;

		@Override
		public Map<String, String> start() {

			DockerImageName esImage = DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:7.10.2").asCompatibleSubstituteFor("docker.elastic.co/elasticsearch/elasticsearch");
			
			//esContainer = new ElasticsearchContainer(esImage);
			osContainer = new OpenSearchContainer("opensearchproject/opensearch:2.16.0");
			pgContainer = new PostgreSQLContainer("postgres:14.2");
			
			
			pgContainer.withEnv("POSTGRES_HOST_AUTH_METHOD", "trust");
			pgContainer.withDatabaseName("curation");
			pgContainer.withUsername("postgres");
			pgContainer.withPassword("");

			//esContainer.withEnv("xpack.security.enabled", "false");
			
			osContainer.start();
			pgContainer.start();
			//esContainer.start();

			return getConfig();
		}

		private Map<String, String> getConfig() {
			final Map<String, String> map = new HashMap<>();

			map.put("quarkus.hibernate-search-orm.elasticsearch.hosts", osContainer.getHost() + ":" + osContainer.getMappedPort(9200));

			map.put("quarkus.datasource.jdbc.url", "jdbc:postgresql://" + pgContainer.getHost() + ":" + pgContainer.getMappedPort(5432) + "/curation");

			//map.put("quarkus.elasticsearch.hosts", esContainer.getHost() + ":" + esContainer.getMappedPort(9200));
			//map.put("quarkus.elasticsearch.protocol", "http");
			
			map.put("quarkus.hibernate-search-orm.elasticsearch.schema-management.required-status-wait-timeout", "180S");

			return map;
		}

		@Override
		public void stop() {
			// Don't shutdown the containers manually, the testing framework will do it for
			// us and this prevents errors when the services shutdown before quarkus does
		}
	}
}
