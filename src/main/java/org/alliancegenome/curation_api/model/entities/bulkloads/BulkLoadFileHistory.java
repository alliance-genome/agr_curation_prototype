package org.alliancegenome.curation_api.model.entities.bulkloads;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.output.ProcessCount;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = { "bulkLoadFile", "bulkLoad", "exceptions" }, callSuper = true)
@AGRCurationSchemaVersion(min = "1.2.4", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })
@Table(indexes = { @Index(name = "bulkloadfilehistory_bulkLoadFile_index", columnList = "bulkLoadFile_id"), @Index(name = "bulkloadfilehistory_createdby_index", columnList = "createdBy_id"), @Index(name = "bulkloadfilehistory_updatedby_index", columnList = "updatedBy_id") })
public class BulkLoadFileHistory extends AuditedObject {

	private static final String COUNT_TYPE = "Records";

	@JsonView({ View.FieldsOnly.class })
	private LocalDateTime loadStarted = LocalDateTime.now();

	@JsonView({ View.FieldsOnly.class })
	private LocalDateTime loadFinished;

	@JsonView({ View.FieldsOnly.class })
	@JdbcTypeCode(SqlTypes.JSON)
	private Map<String, ProcessCount> counts = new HashMap<>();

	@JsonView({ View.FieldsOnly.class })
	@Enumerated(EnumType.STRING)
	private JobStatus bulkloadStatus = JobStatus.STOPPED;

	@JsonView({ View.FieldsOnly.class })
	@Column(columnDefinition = "TEXT")
	private String errorMessage;

	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private BulkLoadFile bulkLoadFile;

	@ManyToOne private BulkLoad bulkLoad;

	@OneToMany(mappedBy = "bulkLoadFileHistory")
	@JsonView(View.BulkLoadFileHistoryView.class)
	private List<BulkLoadFileException> exceptions = new ArrayList<>();

	public BulkLoadFileHistory(String countType, Long count) {
		counts.put(countType, new ProcessCount(count));
		loadStarted = LocalDateTime.now();
	}
	public BulkLoadFileHistory(Long count) {
		counts.put(COUNT_TYPE, new ProcessCount(count));
		loadStarted = LocalDateTime.now();
	}
	public BulkLoadFileHistory(Integer count) {
		counts.put(COUNT_TYPE, new ProcessCount(Long.valueOf(count)));
		loadStarted = LocalDateTime.now();
	}
	public BulkLoadFileHistory() {
		loadStarted = LocalDateTime.now();
	}

	@Transient
	public void finishLoad() {
		loadFinished = LocalDateTime.now();
	}


	@Transient
	public void addCounts2(BulkLoadFileHistory history) {
		for (Entry<String, ProcessCount> entry: history.getCounts().entrySet()) {
			String key = entry.getKey();
			if (counts.containsKey(key)) {
				counts.get(key).add(entry.getValue());
			} else {
				counts.put(key, history.getCounts().get(key));
			}
		}
	}

	@Transient
	public void setCount(long count) {
		setCount(COUNT_TYPE, count);
	}
	@Transient
	public void setCount(String countType, long count) {
		counts.put(countType, new ProcessCount(count));
	}
	@Transient
	public ProcessCount getCount() {
		return getCount(COUNT_TYPE);
	}
	@Transient
	public ProcessCount getCount(String countType) {
		return getProcessCount(countType);
	}
	@Transient
	public void incrementCompleted() {
		incrementCompleted(COUNT_TYPE);
	}
	@Transient
	public void incrementCompleted(String countType) {
		getProcessCount(countType).incrementCompleted();
	}
	@Transient
	public void incrementFailed() {
		incrementFailed(COUNT_TYPE);
	}
	@Transient
	public void incrementFailed(String countType) {
		getProcessCount(countType).incrementFailed();
	}
	@Transient
	public void incrementSkipped() {
		incrementSkipped(COUNT_TYPE);
	}
	@Transient
	public void incrementSkipped(String countType) {
		getProcessCount(countType).incrementSkipped();
	}
	@Transient
	public void setTotalCount(long total) {
		getProcessCount(COUNT_TYPE).setTotal(total);
	}
	@Transient
	public double getErrorRate() {
		return getErrorRate(COUNT_TYPE);
	}
	@Transient
	public double getErrorRate(String countType) {
		return getProcessCount(countType).getErrorRate();
	}
	@Transient
	private ProcessCount getProcessCount(String countType) {
		if (counts.containsKey(countType)) {
			return counts.get(countType);
		} else {
			ProcessCount count = new ProcessCount(0);
			counts.put(countType, count);
			return count;
		}
	}

}
