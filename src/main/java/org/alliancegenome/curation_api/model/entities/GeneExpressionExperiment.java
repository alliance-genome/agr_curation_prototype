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
@AGRCurationSchemaVersion(min = "2.8.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { ExpressionExperiment.class }, partial = true)
@Table(indexes = {
	@Index(name = "geneexpressionexperiment_uniqueid_index", columnList = "uniqueid"),
	@Index(name = "geneexpressionexperiment_curie_index", columnList = "curie"),
	@Index(name = "geneexpressionexperiment_primaryExternalId_index", columnList = "modinternalid"),
	@Index(name = "geneexpressionexperiment_modinternalid_index", columnList = "modinternalid"),
	@Index(name = "geneexpressionexperiment_singlereference_index", columnList = "singlereference_id"),
	@Index(name = "geneexpressionexperiment_entityassayedused_index", columnList = "entityassayed_id"),
	@Index(name = "geneexpressionexperiment_expressionassayused_index", columnList = "expressionassayused_id"),
	@Index(name = "geneexpressionexperiment_dataprovider_index", columnList = "dataprovider_id"),
	@Index(name = "geneexpressionexperiment_internal_index", columnList = "internal"),
	@Index(name = "geneexpressionexperiment_obsolete_index", columnList = "obsolete"),
	@Index(name = "geneexpressionexperiment_createdby_index", columnList = "createdby_id"),
	@Index(name = "geneexpressionexperiment_updatedby_index", columnList = "updatedby_id")
})
public class GeneExpressionExperiment extends ExpressionExperiment {
}
