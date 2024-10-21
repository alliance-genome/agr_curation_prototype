package org.alliancegenome.curation_api.jobs.executors;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkURLLoad;
import org.alliancegenome.curation_api.services.DataProviderService;
import org.alliancegenome.curation_api.services.OrganizationService;
import org.alliancegenome.curation_api.services.ResourceDescriptorPageService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.alliancegenome.curation_api.services.DataProviderService.RESOURCE_DESCRIPTOR_PREFIX;

@JBossLog
@ApplicationScoped
public class ExpressionAtlasExecutor extends LoadFileExecutor {

	@Inject
	DataProviderService service;
	@Inject
	ResourceDescriptorPageService resourceDescriptorPageService;
	@Inject
	OrganizationService organizationService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) throws IOException {

		String url = ((BulkURLLoad) bulkLoadFileHistory.getBulkLoad()).getBulkloadUrl();

		XmlMapper mapper = new XmlMapper();
		URL src = new URL(url);
		Urlset urlset = mapper.readValue(src, Urlset.class);
		List<String> accessionUrlList = urlset.url.stream().map(UrlElement::getLoc).toList();
		List<String> accessions = accessionUrlList.stream()
			.map(sUrl -> sUrl.substring(sUrl.lastIndexOf("/") + 1))
			.toList();

		String name = bulkLoadFileHistory.getBulkLoad().getName();
		String dataProviderName = name.substring(0, name.indexOf(" "));

		Organization organization = organizationService.getByAbbr(dataProviderName).getEntity();
		ResourceDescriptorPage ensemblGenePage = resourceDescriptorPageService.getPageForResourceDescriptor("ENSEMBL", "expression_atlas");

		List<Long> dataProviderIdsBefore =
			new ArrayList<>(service.getDataProviderMap(organization, ensemblGenePage).values().stream().map(DataProvider::getId).toList());
		dataProviderIdsBefore.removeIf(Objects::isNull);

		List<Long> dataProviderIdsLoaded = new ArrayList<>();
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess(name, accessions.size());
		accessions.forEach(accession -> {
			CrossReference reference = getCrossReference(ensemblGenePage, accession, organization);
			DataProvider provider = new DataProvider();
			provider.setSourceOrganization(organization);
			provider.setCrossReference(reference);
			DataProvider entity = service.insertExpressionAtlasDataProvider(provider).getEntity();
			if (entity != null) {
				dataProviderIdsLoaded.add(entity.getId());
				bulkLoadFileHistory.incrementCompleted();
			} else {
				bulkLoadFileHistory.incrementSkipped();
			}
			ph.progressProcess();
		});
		bulkLoadFileHistory.setTotalCount(accessions.size());
		runCleanup(service, bulkLoadFileHistory, dataProviderName, dataProviderIdsBefore, dataProviderIdsLoaded, "Atlas Load Type");
		ph.finishProcess();
		updateHistory(bulkLoadFileHistory);

		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);
	}

	@NotNull
	private static CrossReference getCrossReference(ResourceDescriptorPage ensemblGenePage, String accession, Organization organization) {
		CrossReference reference = new CrossReference();
		if (List.of("FB", "SGD").contains(organization.getAbbreviation())) {
			reference.setReferencedCurie(accession);
		} else {
			reference.setReferencedCurie(RESOURCE_DESCRIPTOR_PREFIX + ":" + accession);
		}
		reference.setDisplayName(accession);
		reference.setResourceDescriptorPage(ensemblGenePage);
		return reference;
	}

}

class Urlset {
	@JacksonXmlElementWrapper(useWrapping = false)
	public List<UrlElement> url = new ArrayList<>();
}

class UrlElement {
	public String loc;
	public String changefreq;

	public String getLoc() {
		return loc;
	}
}
