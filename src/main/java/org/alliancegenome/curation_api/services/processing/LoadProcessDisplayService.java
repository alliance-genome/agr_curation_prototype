package org.alliancegenome.curation_api.services.processing;

import org.alliancegenome.curation_api.model.event.load.EndLoadProcessingEvent;
import org.alliancegenome.curation_api.model.event.load.LoadProcessingEvent;
import org.alliancegenome.curation_api.model.event.load.ProgressLoadProcessingEvent;
import org.alliancegenome.curation_api.model.event.load.StartLoadProcessingEvent;
import org.alliancegenome.curation_api.util.ProcessDisplayHandler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

@ApplicationScoped
public class LoadProcessDisplayService implements ProcessDisplayHandler {

	@Inject
	Event<LoadProcessingEvent> processingEvent;

	@Override
	public void startProcess(String message, long startTime, long totalSize) {
		processingEvent.fireAsync(new StartLoadProcessingEvent(message, startTime, totalSize));
	}

	@Override
	public void progressProcess(String message, String data, long startTime, long nowTime, long lastTime, long currentCount, long lastCount, long totalSize) {
		processingEvent.fireAsync(new ProgressLoadProcessingEvent(message, data, startTime, nowTime, lastTime, currentCount, lastCount, totalSize));
	}

	@Override
	public void finishProcess(String message, String data, long current, long totalSize, long duration) {
		processingEvent.fireAsync(new EndLoadProcessingEvent(message, data, current, totalSize, duration));
	}
}
