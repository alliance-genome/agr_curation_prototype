package org.alliancegenome.curation_api.controllers.crud.slotAnnotations.geneSlotAnnotations;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneSymbolSlotAnnotationCrudController extends BaseEntityCrudController<GeneSymbolSlotAnnotationService, GeneSymbolSlotAnnotation, GeneSymbolSlotAnnotationDAO>
	implements GeneSymbolSlotAnnotationCrudInterface {

	@Inject
	GeneSymbolSlotAnnotationService geneSymbolService;

	@Override
	@PostConstruct
	protected void init() {
		setService(geneSymbolService);
	}

	@Override
	public ObjectResponse<GeneSymbolSlotAnnotation> update(GeneSymbolSlotAnnotation entity) {
		return geneSymbolService.upsert(entity);
	}

	@Override
	public ObjectResponse<GeneSymbolSlotAnnotation> create(GeneSymbolSlotAnnotation entity) {
		return geneSymbolService.upsert(entity);
	}

	public ObjectResponse<GeneSymbolSlotAnnotation> validate(GeneSymbolSlotAnnotation entity) {
		return geneSymbolService.validate(entity);
	}
}
