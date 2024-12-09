package org.alliancegenome.curation_api.jobs.executors;

import java.io.IOException;
import java.net.URL;
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GeoXrefExecutor extends LoadFileExecutor {

	@Inject
	GeneService geneService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) throws IOException {

		String url = ((BulkURLLoad) bulkLoadFileHistory.getBulkLoad()).getBulkloadUrl();

		XmlMapper mapper = new XmlMapper();
		URL src = new URL(url);
		List<String> entrezIds = mapper.readValue(src, ESearchResult.class).getIdList().getIds();
		
		bulkLoadFileHistory.getBulkLoadFile().setRecordCount(entrezIds.size() + bulkLoadFileHistory.getBulkLoadFile().getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

		bulkLoadFileHistory.setCount(entrezIds.size());
		updateHistory(bulkLoadFileHistory);

		String name = bulkLoadFileHistory.getBulkLoad().getName();
		String dataProviderName = name.substring(0, name.indexOf(" "));
		
		BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(dataProviderName);

		runLoad(bulkLoadFileHistory, dataProvider, entrezIds);

		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);
	}

	private void runLoad(BulkLoadFileHistory history, BackendBulkDataProvider dataProvider, List<String> entrezIds) {
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		if (CollectionUtils.isNotEmpty(entrezIds)) {
			String loadMessage = "GEO cross-reference update";
			if (dataProvider != null) {
				loadMessage = loadMessage + " for " + dataProvider.name();
			}
			ph.startProcess(loadMessage, entrezIds.size());
			
			history.setCount(entrezIds.size());
			updateHistory(history);
			
			for (String entrezId : entrezIds) {
				try {
					geneService.addGeoXref(entrezId, dataProvider);
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
					addException(history, new ObjectUpdateExceptionData(entrezId, e.getMessage(), e.getStackTrace()));
				}
				if (history.getErrorRate() > 0.25) {
					Log.error("Failure Rate > 25% aborting load");
					updateHistory(history);
					updateExceptions(history);
					failLoadAboveErrorRateCutoff(history);
					return;
				}
				ph.progressProcess();
			}
			updateHistory(history);
			updateExceptions(history);
			ph.finishProcess();
		}
	}
}

@JsonIgnoreProperties(ignoreUnknown = true)
class ESearchResult {
	@JacksonXmlProperty(localName = "IdList")
	public IdList idList;
	
	public IdList getIdList() {
		return idList;
	}
}

class IdList {
	@JacksonXmlProperty(localName = "Id")
	@JacksonXmlElementWrapper(useWrapping = false)
	private List<String> ids;
	
	public List<String> getIds() {
		return ids;
	}
}
