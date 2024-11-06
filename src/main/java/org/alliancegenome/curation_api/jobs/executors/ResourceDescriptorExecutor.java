package org.alliancegenome.curation_api.jobs.executors;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.ResourceDescriptorDTO;
import org.alliancegenome.curation_api.services.ResourceDescriptorService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class ResourceDescriptorExecutor extends LoadFileExecutor {

	@Inject ResourceDescriptorService resourceDescriptorService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) throws Exception {

		log.info("Loading ResourceDescriptor File");

		File rdFile = new File(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath());
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, ResourceDescriptorDTO.class);
		List<ResourceDescriptorDTO> dtos = mapper.readValue(new GZIPInputStream(new FileInputStream(rdFile)), listType);

		List<String> rdNamesBefore = resourceDescriptorService.getAllNames();
		List<String> rdNamesAfter = new ArrayList<>();

		bulkLoadFileHistory.setCount(dtos.size());

		updateHistory(bulkLoadFileHistory);
		for (ResourceDescriptorDTO dto : dtos) {
			try {
				ResourceDescriptor rd = resourceDescriptorService.upsert(dto);
				bulkLoadFileHistory.incrementCompleted();
				rdNamesAfter.add(rd.getName());
			} catch (ObjectUpdateException e) {
				bulkLoadFileHistory.incrementFailed();
				addException(bulkLoadFileHistory, e.getData());
			} catch (Exception e) {
				bulkLoadFileHistory.incrementFailed();
				addException(bulkLoadFileHistory, new ObjectUpdateExceptionData(dto, e.getMessage(), e.getStackTrace()));
			}

		}
		updateHistory(bulkLoadFileHistory);

		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);
		resourceDescriptorService.removeNonUpdatedResourceDescriptors(rdNamesBefore, rdNamesAfter);

		log.info("Loading ResourceDescriptorFileFinished");
	}
}
