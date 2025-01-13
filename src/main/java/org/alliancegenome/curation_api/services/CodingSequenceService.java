package org.alliancegenome.curation_api.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.CodingSequenceDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.CodingSequence;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.Gff3DtoValidator;
import org.apache.commons.lang.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class CodingSequenceService extends BaseEntityCrudService<CodingSequence, CodingSequenceDAO> {

	@Inject CodingSequenceDAO codingSequenceDAO;
	@Inject PersonService personService;
	@Inject Gff3DtoValidator gff3DtoValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(codingSequenceDAO);
	}

	public List<Long> getIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
		if (StringUtils.equals(dataProvider.sourceOrganization, "RGD") || StringUtils.equals(dataProvider.sourceOrganization, "XB")) {
			params.put(EntityFieldConstants.TAXON, dataProvider.canonicalTaxonCurie);
		}
		List<Long> ids = codingSequenceDAO.findIdsByParams(params);
		ids.removeIf(Objects::isNull);
		return ids;
	}

	@Override
	public ObjectResponse<CodingSequence> getByIdentifier(String identifier) {
		CodingSequence object = findByAlternativeFields(List.of("curie", "primaryExternalId", "modInternalId", "uniqueId"), identifier);
		ObjectResponse<CodingSequence> ret = new ObjectResponse<CodingSequence>(object);
		return ret;
	}

	public ObjectResponse<CodingSequence> deleteByIdentifier(String identifierString) {
		CodingSequence codingSequence = findByAlternativeFields(List.of("curie", "primaryExternalId", "modInternalId", "uniqueId"), identifierString);
		if (codingSequence != null) {
			codingSequenceDAO.remove(codingSequence.getId());
		}
		ObjectResponse<CodingSequence> ret = new ObjectResponse<>(codingSequence);
		return ret;
	}

}
