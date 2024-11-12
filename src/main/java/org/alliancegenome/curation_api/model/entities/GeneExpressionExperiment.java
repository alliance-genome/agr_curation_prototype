package org.alliancegenome.curation_api.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@AGRCurationSchemaVersion(min = "1.7.3", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { ExpressionExperiment.class }, partial = true)
@Table(indexes = {
	@Index(name = "on_index", columnList = "id")
})
public class GeneExpressionExperiment extends ExpressionExperiment {
}
