package org.alliancegenome.curation_api.services.slotAnnotations.constructSlotAnnotations;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations.ConstructSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructSymbolSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.constructSlotAnnotations.ConstructSymbolSlotAnnotationValidator;

@RequestScoped
public class ConstructSymbolSlotAnnotationService extends BaseEntityCrudService<ConstructSymbolSlotAnnotation, ConstructSymbolSlotAnnotationDAO> {

	@Inject
	ConstructSymbolSlotAnnotationDAO constructSymbolDAO;
	@Inject
	ConstructSymbolSlotAnnotationValidator constructSymbolValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(constructSymbolDAO);
	}

	@Transactional
	public ObjectResponse<ConstructSymbolSlotAnnotation> upsert(ConstructSymbolSlotAnnotation uiEntity) {
		ConstructSymbolSlotAnnotation dbEntity = constructSymbolValidator.validateConstructSymbolSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null)
			return null;
		return new ObjectResponse<ConstructSymbolSlotAnnotation>(constructSymbolDAO.persist(dbEntity));
	}

	public ObjectResponse<ConstructSymbolSlotAnnotation> validate(ConstructSymbolSlotAnnotation uiEntity) {
		ConstructSymbolSlotAnnotation sa = constructSymbolValidator.validateConstructSymbolSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<ConstructSymbolSlotAnnotation>(sa);
	}

}