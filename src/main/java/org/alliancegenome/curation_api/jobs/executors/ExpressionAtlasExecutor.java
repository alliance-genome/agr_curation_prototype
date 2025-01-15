package org.alliancegenome.curation_api.jobs.executors;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.KnownIssueValidationException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkURLLoad;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections.CollectionUtils;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ExpressionAtlasExecutor extends LoadFileExecutor {

	@Inject
	GeneService geneService;

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
		
		BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(dataProviderName);

		runLoad(bulkLoadFileHistory, dataProvider, accessions);

		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);
	}
		
	private void runLoad(BulkLoadFileHistory history, BackendBulkDataProvider dataProvider, List<String> identifiers) {
		if (Thread.currentThread().isInterrupted()) {
			history.setErrorMessage("Thread isInterrupted");
			throw new RuntimeException("Thread isInterrupted");
		}
		
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		if (CollectionUtils.isNotEmpty(identifiers)) {
			String loadMessage = "Expression Atlas cross-reference update";
			if (dataProvider != null) {
				loadMessage = loadMessage + " for " + dataProvider.name();
			}
			ph.startProcess(loadMessage, identifiers.size());
			
			history.setCount(identifiers.size());
			updateHistory(history);
			
			for (String identifier : identifiers) {
				try {
					geneService.addExpressionAtlasXref(identifier, dataProvider);
					history.incrementCompleted();
				} catch (ObjectUpdateException e) {
					history.incrementFailed();
					addException(history, e.getData());
				} catch (KnownIssueValidationException e) {
					Log.debug(e.getMessage());
					history.incrementSkipped();
				} catch (Exception e) {
					e.printStackTrace();
					history.incrementFailed();
					addException(history, new ObjectUpdateExceptionData(identifier, e.getMessage(), e.getStackTrace()));
				}
				if (history.getErrorRate() > 0.25) {
					Log.error("Failure Rate > 25% aborting load");
					updateHistory(history);
					updateExceptions(history);
					failLoadAboveErrorRateCutoff(history);
					return;
				}
				ph.progressProcess();
				if (Thread.currentThread().isInterrupted()) {
					history.setErrorMessage("Thread isInterrupted");
					throw new RuntimeException("Thread isInterrupted");
				}
			}
			updateHistory(history);
			updateExceptions(history);
			ph.finishProcess();
		}
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
