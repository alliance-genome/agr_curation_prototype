package org.alliancegenome.curation_api.services.helpers.annotations;

import jakarta.enterprise.context.RequestScoped;
import org.alliancegenome.curation_api.model.ingest.dto.fms.GeneExpressionFmsDTO;
import org.alliancegenome.curation_api.services.helpers.UniqueIdGeneratorHelper;

@RequestScoped
public class GeneExpressionAnnotationUniqueIdHelper {

	//	UniqueID =
	//	assayId | evidenceCurie | geneId | stageTermId | stageName | whereExpressedStatement | anatomicalStructureTermId | cellularComponentTermId
	public String generateUniqueId(GeneExpressionFmsDTO geneExpressionFmsDTO, String referenceCurie) {
		UniqueIdGeneratorHelper uniqueIdGeneratorHelper = new UniqueIdGeneratorHelper();
		uniqueIdGeneratorHelper.add(geneExpressionFmsDTO.getAssay());
		uniqueIdGeneratorHelper.add(geneExpressionFmsDTO.getGeneId());
		uniqueIdGeneratorHelper.add(referenceCurie);
		if (geneExpressionFmsDTO.getWhenExpressed() != null) {
			uniqueIdGeneratorHelper.add(geneExpressionFmsDTO.getWhenExpressed().getStageTermId());
			uniqueIdGeneratorHelper.add(geneExpressionFmsDTO.getWhenExpressed().getStageName());
		}
		if (geneExpressionFmsDTO.getWhereExpressed() != null) {
			uniqueIdGeneratorHelper.add(geneExpressionFmsDTO.getWhereExpressed().getWhereExpressedStatement());
			uniqueIdGeneratorHelper.add(geneExpressionFmsDTO.getWhereExpressed().getAnatomicalStructureTermId());
			uniqueIdGeneratorHelper.add(geneExpressionFmsDTO.getWhereExpressed().getCellularComponentTermId());
		}
		return uniqueIdGeneratorHelper.getUniqueId();
	}
}
