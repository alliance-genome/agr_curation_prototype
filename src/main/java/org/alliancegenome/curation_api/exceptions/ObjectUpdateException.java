package org.alliancegenome.curation_api.exceptions;

import java.util.Collection;

import org.alliancegenome.curation_api.config.RestDefaultObjectMapper;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ObjectUpdateException extends ValidationException {

	private ObjectUpdateExceptionData data;

	public ObjectUpdateException(Object updateObject, String message) {
		data = new ObjectUpdateExceptionData(updateObject, message, null);
	}

	public ObjectUpdateException(Object updateObject, Collection<String> messages) {
		data = new ObjectUpdateExceptionData(updateObject, messages, null);
	}

	public ObjectUpdateException(Object updateObject, String message, StackTraceElement[] stackTraceElements) {
		data = new ObjectUpdateExceptionData(updateObject, message, stackTraceElements);
	}

	@Data
	@NoArgsConstructor
	public static class ObjectUpdateExceptionData {

		private static ObjectMapper mapper = new RestDefaultObjectMapper().getMapper();

		@JsonView({ View.FieldsOnly.class })
		private String jsonObject;
		@JsonView({ View.FieldsOnly.class })
		private String message;
		@JsonView({ View.FieldsOnly.class })
		private Collection<String> messages;
		@JsonView({ View.FieldsOnly.class })
		private StackTraceElement[] stackTraceElements;

		public ObjectUpdateExceptionData(Object updateObject, Collection<String> messages, StackTraceElement[] stackTraceElements) {
			try {
				this.messages = messages;
				this.stackTraceElements = stackTraceElements;
				this.jsonObject = mapper.writeValueAsString(updateObject);
			} catch (JsonProcessingException e) {
				this.message = e.getMessage();
				this.jsonObject = "{}";
			}
		}

		public ObjectUpdateExceptionData(Object updateObject, String message, StackTraceElement[] stackTraceElements) {
			try {
				this.message = message;
				this.stackTraceElements = stackTraceElements;
				this.jsonObject = mapper.writeValueAsString(updateObject);
			} catch (JsonProcessingException e) {
				this.message = e.getMessage();
				this.jsonObject = "{}";
			}
		}

	}
}