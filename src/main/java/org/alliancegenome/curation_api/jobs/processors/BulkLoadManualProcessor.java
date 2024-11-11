package org.alliancegenome.curation_api.jobs.processors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.enums.BackendBulkLoadType;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.jobs.events.StartedBulkLoadJobEvent;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;

@ApplicationScoped
public class BulkLoadManualProcessor extends BulkLoadProcessor {

	public void processBulkManualLoad(@ObservesAsync StartedBulkLoadJobEvent load) {
		BulkLoad bulkLoad = bulkLoadDAO.find(load.getId());
		if (bulkLoad instanceof BulkManualLoad bulkURLLoad) {
			Log.debug("processBulkManualLoad: " + load.getId());
			// We do nothing because at the load level we don't try to figure out what the
			// next file to run is
		}
	}

	public void processBulkManualLoadFromDQM(MultipartFormDataInput input, BackendBulkLoadType loadType, BackendBulkDataProvider dataProvider, Boolean cleanUp) { // Triggered by the API
		Map<String, List<InputPart>> form = input.getFormDataMap();

		if (form.containsKey(loadType)) {
			Log.warn("Key not found: " + loadType);
			return;
		}

		BulkManualLoad bulkManualLoad = null;

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("backendBulkLoadType", loadType);
		params.put("dataProvider", dataProvider);
		SearchResponse<BulkManualLoad> load = bulkManualLoadDAO.findByParams(params);
		if (load != null && load.getResults().size() == 1) {
			bulkManualLoad = load.getResults().get(0);
			bulkManualLoad.setBulkloadStatus(JobStatus.MANUAL_STARTED);

			startLoad(bulkManualLoad);

			String filePath = fileHelper.saveIncomingFile(input, bulkManualLoad.getBackendBulkLoadType().toString() + "_" + bulkManualLoad.getDataProvider().toString());
			String localFilePath = fileHelper.compressInputFile(filePath);
			processFilePath(bulkManualLoad, localFilePath, cleanUp);

			endLoad(bulkManualLoad, null, JobStatus.FINISHED);

		} else {
			Log.warn("BulkManualLoad not found: " + loadType);
			endLoad(bulkManualLoad, "BulkManualLoad not found: " + loadType, JobStatus.FAILED);
		}

	}
}
