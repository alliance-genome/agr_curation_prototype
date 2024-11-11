package org.alliancegenome.curation_api.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.ExonDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.Exon;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.apache.commons.lang.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ExonService extends BaseEntityCrudService<Exon, ExonDAO> {

	@Inject ExonDAO exonDAO;
	@Inject PersonService personService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(exonDAO);
	}

	public List<Long> getIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
		if (StringUtils.equals(dataProvider.sourceOrganization, "RGD")) {
			params.put(EntityFieldConstants.TAXON, dataProvider.canonicalTaxonCurie);
		}
		List<Long> ids = exonDAO.findIdsByParams(params);
		ids.removeIf(Objects::isNull);
		return ids;
	}

	@Override
	public ObjectResponse<Exon> getByIdentifier(String identifier) {
		Exon object = findByAlternativeFields(List.of("curie", "modEntityId", "modInternalId", "uniqueId"), identifier);
		ObjectResponse<Exon> ret = new ObjectResponse<Exon>(object);
		return ret;
	}

	public ObjectResponse<Exon> deleteByIdentifier(String identifierString) {
		Exon exon = findByAlternativeFields(List.of("modEntityId", "modInternalId", "uniqueId"), identifierString);
		if (exon != null) {
			exonDAO.remove(exon.getId());
		}
		ObjectResponse<Exon> ret = new ObjectResponse<>(exon);
		return ret;
	}
}
