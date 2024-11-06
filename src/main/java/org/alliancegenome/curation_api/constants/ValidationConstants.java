package org.alliancegenome.curation_api.constants;

public final class ValidationConstants {
	private ValidationConstants() {
		// Hidden from view, as it is a utility class
	}
	public static final String INVALID_MESSAGE = "Not a valid entry";
	public static final String INVALID_TYPE_MESSAGE = INVALID_MESSAGE + " - unexpected object type found";
	public static final String OBSOLETE_MESSAGE = "Obsolete term specified";
	public static final String REQUIRED_MESSAGE = "Required field is empty";
	public static final String REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE = "Field is required unless value is populated for ";
	public static final String DEPENDENCY_MESSAGE_PREFIX = "Invalid without value for ";
	public static final String NON_UNIQUE_MESSAGE = "Field value is not unique";
	public static final String UNSUPPORTED_MESSAGE = "Unsupported value specified";
	public static final String DUPLICATE_MESSAGE = "Duplicate entries found";
	public static final String DUPLICATE_RELATION_PREFIX = "Entries found with same relation field - ";
	public static final String UNRECOGNIZED_MESSAGE = "Unrecognized entry"; // To be used instead of INVALID_MESSAGE when entry to be skipped instead of failed
	
}