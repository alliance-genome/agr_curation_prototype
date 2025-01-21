package org.alliancegenome.curation_api.enums;

public enum JobStatus {

	SCHEDULED_PENDING, SCHEDULED_STARTED, SCHEDULED_RUNNING,

	FORCED_PENDING, FORCED_STARTED, FORCED_RUNNING, FORCED_STOPPED,

	MANUAL_PENDING, MANUAL_STARTED, MANUAL_RUNNING,

	FAILED, STOPPED, FINISHED

	;

	public boolean isRunning() {
		return this == SCHEDULED_RUNNING || this == FORCED_RUNNING || this == MANUAL_RUNNING;
	}

	public boolean isPending() {
		return this == FORCED_PENDING || this == SCHEDULED_PENDING || this == MANUAL_PENDING;
	}

	public boolean isStarted() {
		return this == FORCED_STARTED || this == SCHEDULED_STARTED || this == MANUAL_STARTED;
	}

	public boolean isNotRunning() {
		return this == FAILED || this == STOPPED || this == FINISHED || this == FORCED_STOPPED;
	}

	public JobStatus getNextStatus() {
		if (this == JobStatus.FORCED_PENDING) {
			return JobStatus.FORCED_STARTED;
		}
		if (this == JobStatus.FORCED_STARTED) {
			return JobStatus.FORCED_RUNNING;
		}

		if (this == JobStatus.SCHEDULED_PENDING) {
			return JobStatus.SCHEDULED_STARTED;
		}
		if (this == JobStatus.SCHEDULED_STARTED) {
			return JobStatus.SCHEDULED_RUNNING;
		}

		if (this == JobStatus.MANUAL_PENDING) {
			return JobStatus.MANUAL_STARTED;
		}
		if (this == JobStatus.MANUAL_STARTED) {
			return JobStatus.MANUAL_RUNNING;
		}

		return FAILED;
	}

	public boolean isForced() {
		return this == FORCED_PENDING || this == FORCED_STARTED || this == FORCED_RUNNING || this == FORCED_STOPPED;
	}
}
