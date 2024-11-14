package org.alliancegenome.curation_api.services;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.jbosslog.JBossLog;
import org.alliancegenome.curation_api.dao.GeneExpressionAnnotationDAO;
import org.alliancegenome.curation_api.dao.GeneExpressionExperimentDAO;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;
import org.alliancegenome.curation_api.model.entities.GeneExpressionExperiment;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.ontology.MmoTermService;

import java.util.HashSet;
import java.util.Set;

@JBossLog
@RequestScoped
public class GeneExpressionExperimentService extends BaseEntityCrudService<GeneExpressionExperiment, GeneExpressionExperimentDAO> {

	@Inject GeneExpressionExperimentDAO geneExpressionExperimentDAO;
	@Inject GeneExpressionAnnotationDAO geneExpressionAnnotationDAO;
	@Inject GeneService geneService;
	@Inject MmoTermService mmoTermService;
	@Inject ReferenceService referenceService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneExpressionExperimentDAO);
	}

	@Transactional
	public GeneExpressionExperiment upsert(String experimentId, Set<String> geneExpressionAnnotationIds) throws ValidationException {
		GeneExpressionExperiment geneExpressionExperiment;
		Set<GeneExpressionAnnotation> annotations;

		//	example of experimentId: Xenbase:XB-GENE-972235|AGRKB:101000000874667|MMO:0000658
		String[] definingFields = experimentId.split("\\|", 3);
		String geneId = definingFields[0];
		String referenceId = definingFields[1];
		String assayId = definingFields[2];
		SearchResponse<GeneExpressionExperiment> response = geneExpressionExperimentDAO.findByField("uniqueId", experimentId);
		if (response != null && response.getSingleResult() != null) {
			geneExpressionExperiment = response.getSingleResult();
		} else {
			geneExpressionExperiment = new GeneExpressionExperiment();
			geneExpressionExperiment.setUniqueId(experimentId);
		}

		geneExpressionExperiment.setEntityAssayed(geneService.findByIdentifierString(geneId));
		geneExpressionExperiment.setSingleReference(referenceService.getByCurie(referenceId).getEntity());
		geneExpressionExperiment.setExpressionAssayUsed(mmoTermService.findByCurie(assayId));
		geneExpressionExperiment.setInternal(false);
		geneExpressionExperiment.setObsolete(false);

		annotations = geneExpressionExperiment.getExpressionAnnotations();
		if (annotations == null) {
			annotations = new HashSet<>();
		}
		for (String geneExpressionAnnotationId: geneExpressionAnnotationIds) {
			annotations.add(geneExpressionAnnotationDAO.findByField("uniqueId", geneExpressionAnnotationId).getSingleResult());
		}
		geneExpressionExperiment.setExpressionAnnotations(annotations);

		return geneExpressionExperimentDAO.persist(geneExpressionExperiment);
	}
}
