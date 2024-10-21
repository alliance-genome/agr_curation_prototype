package org.alliancegenome.curation_api.controllers.crud;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.DataProviderDAO;
import org.alliancegenome.curation_api.interfaces.crud.DataProviderCrudInterface;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.DataProviderService;

@RequestScoped
public class DataProviderCrudController extends BaseEntityCrudController<DataProviderService, DataProvider, DataProviderDAO> implements DataProviderCrudInterface {

	@Inject
	DataProviderService dataProviderService;

	@Override
	@PostConstruct
	protected void init() {
		setService(dataProviderService);
	}

	@Override
	public ObjectResponse<DataProvider> update(DataProvider entity) {
		return dataProviderService.upsert(entity);
	}

	@Override
	public ObjectResponse<DataProvider> create(DataProvider entity) {
		return dataProviderService.upsert(entity);
	}

	public ObjectResponse<DataProvider> validate(DataProvider entity) {
		return dataProviderService.validate(entity);
	}
}
