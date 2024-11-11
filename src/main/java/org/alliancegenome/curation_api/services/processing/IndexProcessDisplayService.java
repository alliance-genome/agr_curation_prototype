package org.alliancegenome.curation_api.services.processing;

import org.alliancegenome.curation_api.model.event.index.EndIndexProcessingEvent;
import org.alliancegenome.curation_api.model.event.index.IndexProcessingEvent;
import org.alliancegenome.curation_api.model.event.index.ProgressIndexProcessingEvent;
import org.alliancegenome.curation_api.model.event.index.StartIndexProcessingEvent;
import org.alliancegenome.curation_api.util.ProcessDisplayHandler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

@ApplicationScoped
public class IndexProcessDisplayService implements ProcessDisplayHandler {

	@Inject
	Event<IndexProcessingEvent> processingEvent;

	@Override
	public void startProcess(String message, long startTime, long totalSize) {
		processingEvent.fireAsync(new StartIndexProcessingEvent(message, startTime, totalSize));
	}

	@Override
	public void progressProcess(String message, String data, long startTime, long nowTime, long lastTime, long currentCount, long lastCount, long totalSize) {
		processingEvent.fireAsync(new ProgressIndexProcessingEvent(message, data, startTime, nowTime, lastTime, currentCount, lastCount, totalSize));
	}

	@Override
	public void finishProcess(String message, String data, long current, long totalSize, long duration) {
		processingEvent.fireAsync(new EndIndexProcessingEvent(message, data, current, totalSize, duration));
	}
}
