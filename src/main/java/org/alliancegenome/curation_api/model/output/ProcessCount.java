package org.alliancegenome.curation_api.model.output;

import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
@JsonView({ View.FieldsOnly.class })
public class ProcessCount {
	private Long total = 0L;
	private Long failed = 0L;
	private Long skipped = 0L;
	private Long completed = 0L;
	private Long error = 0L;
	
	public ProcessCount(Long total) {
		this.total = total;
	}
	public ProcessCount(Integer total) {
		this.total = Long.valueOf(total);
	}

	public void incrementCompleted() {
		completed++;
		if (error > 0) {
			error--;
		}
	}
	
	public void incrementSkipped() {
		skipped++;
	}
	
	public void incrementFailed() {
		failed++;
		error++;
	}
	public double getErrorRate() {
		return error / 1000;
	}

	public void add(ProcessCount count) {
		total += count.getTotal();
		failed += count.getFailed();
		skipped += count.getSkipped();
		completed += count.getCompleted();
		error += count.getError();
	}

}
