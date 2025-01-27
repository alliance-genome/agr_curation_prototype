package org.alliancegenome.curation_api.model.ingest.dto.fms;

import java.util.List;

import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CrossReferenceFmsDTO extends BaseDTO {
	private String id;
	private List<String> pages;

	public String getCurie() {
		return id;
	}
}
