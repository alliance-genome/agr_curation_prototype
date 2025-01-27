package org.alliancegenome.curation_api.controllers.crud.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleMutationTypeSlotAnnotationCrudController extends
		BaseEntityCrudController<AlleleMutationTypeSlotAnnotationService, AlleleMutationTypeSlotAnnotation, AlleleMutationTypeSlotAnnotationDAO>
		implements AlleleMutationTypeSlotAnnotationCrudInterface {

	@Inject
	AlleleMutationTypeSlotAnnotationService alleleMutationTypeService;

	@Override
	@PostConstruct
	protected void init() {
		setService(alleleMutationTypeService);
	}

	@Override
	public ObjectResponse<AlleleMutationTypeSlotAnnotation> update(AlleleMutationTypeSlotAnnotation entity) {
		return alleleMutationTypeService.upsert(entity);
	}

	@Override
	public ObjectResponse<AlleleMutationTypeSlotAnnotation> create(AlleleMutationTypeSlotAnnotation entity) {
		return alleleMutationTypeService.upsert(entity);
	}

	public ObjectResponse<AlleleMutationTypeSlotAnnotation> validate(AlleleMutationTypeSlotAnnotation entity) {
		return alleleMutationTypeService.validate(entity);
	}
}
