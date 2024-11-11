package org.alliancegenome.curation_api.services.orthology;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.orthology.GeneToGeneOrthologyGeneratedDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyGenerated;
import org.alliancegenome.curation_api.model.ingest.dto.fms.OrthologyFmsDTO;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.OrthologyFmsDTOValidator;
import org.apache.commons.lang.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneToGeneOrthologyGeneratedService extends BaseEntityCrudService<GeneToGeneOrthologyGenerated, GeneToGeneOrthologyGeneratedDAO> implements BaseUpsertServiceInterface<GeneToGeneOrthologyGenerated, OrthologyFmsDTO> {

	@Inject GeneToGeneOrthologyGeneratedDAO geneToGeneOrthologyGeneratedDAO;
	@Inject OrthologyFmsDTOValidator orthologyFmsDtoValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneToGeneOrthologyGeneratedDAO);
	}

	public GeneToGeneOrthologyGenerated upsert(OrthologyFmsDTO orthoPair, BackendBulkDataProvider backendBulkDataProvider) throws ValidationException {
		return orthologyFmsDtoValidator.validateOrthologyFmsDTO(orthoPair);
	}

	public List<Long> getAllOrthologyPairIdsBySubjectGeneDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.SUBJECT_GENE_DATA_PROVIDER, dataProvider.sourceOrganization);

		if (StringUtils.equals(dataProvider.sourceOrganization, "RGD") || StringUtils.equals(dataProvider.sourceOrganization, "XB")) {
			params.put(EntityFieldConstants.SUBJECT_GENE_TAXON, dataProvider.canonicalTaxonCurie);
		}

		List<Long> annotationIds = geneToGeneOrthologyGeneratedDAO.findIdsByParams(params);
		return annotationIds;
	}

}
