package org.alliancegenome.curation_api.websocket;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.alliancegenome.curation_api.model.event.load.LoadProcessingEvent;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/load_processing_events")
@ApplicationScoped
public class LoadProcessingWebsocket {

	@Inject
	ObjectMapper mapper;

	Map<String, Session> sessions = new HashMap<>();

	@OnOpen
	public void onOpen(Session session) {
		//Log.info("Creating New Session: " + session);
		try {
			sessions.put(session.getId(), session);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@OnClose
	public void onClose(Session session) {
		//Log.info("Closing Session: " + session);
		try {
			sessions.remove(session.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		//Log.info("Session Error: " + session);
		sessions.remove(session.getId());
	}

	@OnMessage
	public void message(String message, Session session) {
		//Log.info("Session Message: " + session + " " + message);
		// session.getOpenSessions().forEach(s -> s.getAsyncRemote().sendText(message));
	}

	public void observeProcessingEvent(@ObservesAsync LoadProcessingEvent event) {
		//Log.info(sessions);
		//Log.info(event);
		for (Entry<String, Session> sessionEntry : sessions.entrySet()) {
			try {
				sessionEntry.getValue().getAsyncRemote().sendText(mapper.writeValueAsString(event));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
