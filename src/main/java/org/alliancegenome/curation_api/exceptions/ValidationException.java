package org.alliancegenome.curation_api.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ValidationException extends Exception {
	public ValidationException(String message) {
		super(message);
	}
}