package org.alliancegenome.curation_api.model.entities.bulkloads;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.enums.BulkLoadCleanUp;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = { "history" })
@AGRCurationSchemaVersion(min = "1.3.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })
@Table(
	indexes = {
		@Index(name = "bulkloadfile_createdby_index", columnList = "createdBy_id"),
		@Index(name = "bulkloadfile_updatedby_index", columnList = "updatedBy_id")
	}
)
public class BulkLoadFile extends AuditedObject {

	@JsonView({ View.FieldsOnly.class })
	@Enumerated(EnumType.STRING)
	private BulkLoadCleanUp bulkloadCleanUp;

	@JsonView({ View.FieldsOnly.class })
	@Column(unique = true)
	private String md5Sum;

	@JsonView({ View.FieldsOnly.class })
	private String localFilePath;

	@JsonView({ View.FieldsOnly.class })
	private Long fileSize;

	@JsonView({ View.FieldsOnly.class })
	private String s3Path;

	@JsonView({ View.FieldsOnly.class })
	private Integer recordCount = 0;

	@JsonView({ View.FieldsOnly.class })
	private String linkMLSchemaVersion;
	
	@JsonView({ View.FieldsOnly.class })
	private String allianceMemberReleaseVersion;

	@OneToMany(mappedBy = "bulkLoadFile")
	private List<BulkLoadFileHistory> history;

	@Transient
	@JsonView({ View.FieldsOnly.class })
	public String getS3Url() {
		return "https://agr-curation-files.s3.amazonaws.com/" + s3Path;
	}

	@Transient
	@JsonIgnore
	@JsonView({ View.FieldsOnly.class })
	public String generateS3MD5Path(BulkLoad bulkLoad) {
		if (md5Sum != null && md5Sum.length() > 0) {
			return md5Sum.charAt(0) + "/" + md5Sum.charAt(1) + "/" + md5Sum.charAt(2) + "/" + md5Sum.charAt(3) + "/" + md5Sum + "." + bulkLoad.getBackendBulkLoadType().fileExtension + ".gz";
		} else {
			return null;
		}
	}
}
