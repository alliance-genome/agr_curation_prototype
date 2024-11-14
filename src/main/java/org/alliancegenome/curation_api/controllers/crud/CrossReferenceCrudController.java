package org.alliancegenome.curation_api.controllers.crud;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.interfaces.crud.CrossReferenceCrudInterface;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.services.CrossReferenceService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class CrossReferenceCrudController extends BaseEntityCrudController<CrossReferenceService, CrossReference, CrossReferenceDAO> implements CrossReferenceCrudInterface {

	@Inject
	CrossReferenceService crossReferenceService;

	@Override
	@PostConstruct
	protected void init() {
		setService(crossReferenceService);
	}
}
