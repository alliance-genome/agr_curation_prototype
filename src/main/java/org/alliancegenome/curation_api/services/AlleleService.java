package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.associations.alleleAssociations.AlleleGeneAssociationService;
import org.alliancegenome.curation_api.services.associations.constructAssociations.ConstructGenomicEntityAssociationService;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.validation.AlleleValidator;
import org.alliancegenome.curation_api.services.validation.dto.AlleleDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class AlleleService extends SubmittedObjectCrudService<Allele, AlleleDTO, AlleleDAO> {

	@Inject AlleleDAO alleleDAO;
	@Inject AlleleValidator alleleValidator;
	@Inject AlleleDTOValidator alleleDtoValidator;
	@Inject DiseaseAnnotationService diseaseAnnotationService;
	@Inject PersonService personService;
	@Inject AlleleGeneAssociationService alleleGeneAssociationService;
	@Inject ConstructGenomicEntityAssociationService constructGenomicEntityAssociationService;
	@Inject PhenotypeAnnotationService phenotypeAnnotationService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(alleleDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<Allele> update(Allele uiEntity) {
		Allele dbEntity = alleleValidator.validateAlleleUpdate(uiEntity, false);
		return new ObjectResponse<Allele>(dbEntity);
	}

	@Transactional
	public ObjectResponse<Allele> updateDetail(Allele uiEntity) {
		Allele dbEntity = alleleValidator.validateAlleleUpdate(uiEntity, true);
		return new ObjectResponse<Allele>(dbEntity);
	}

	@Override
	@Transactional
	public ObjectResponse<Allele> create(Allele uiEntity) {
		Allele dbEntity = alleleValidator.validateAlleleCreate(uiEntity);
		return new ObjectResponse<Allele>(dbEntity);
	}

	public Allele upsert(AlleleDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		return alleleDtoValidator.validateAlleleDTO(dto, dataProvider);
	}

	@Override
	@Transactional
	public ObjectResponse<Allele> deleteById(Long id) {
		deprecateOrDelete(id, true, "Allele DELETE API call", false);
		ObjectResponse<Allele> ret = new ObjectResponse<>();
		return ret;
	}

	@Override
	@Transactional
	public Allele deprecateOrDelete(Long id, Boolean throwApiError, String requestSource, Boolean forceDeprecate) {
		Allele allele = alleleDAO.find(id);
		if (allele != null) {
			if (forceDeprecate || alleleDAO.hasReferencingDiseaseAnnotationIds(id)
					|| alleleDAO.hasReferencingPhenotypeAnnotations(id)
					|| CollectionUtils.isNotEmpty(allele.getAlleleGeneAssociations())
					|| CollectionUtils.isNotEmpty(allele.getAlleleVariantAssociations())
					|| CollectionUtils.isNotEmpty(allele.getConstructGenomicEntityAssociations())) {
				if (!allele.getObsolete()) {
					allele.setUpdatedBy(personService.fetchByUniqueIdOrCreate(requestSource));
					allele.setDateUpdated(OffsetDateTime.now());
					allele.setObsolete(true);
					return alleleDAO.persist(allele);
				} else {
					return allele;
				}
			} else {
				alleleDAO.remove(id);
			}
		} else {
			String errorMessage = "Could not find Allele with id: " + id;
			if (throwApiError) {
				ObjectResponse<Allele> response = new ObjectResponse<>();
				response.addErrorMessage("id", errorMessage);
				throw new ApiErrorException(response);
			}
			Log.error(errorMessage);
		}
		return null;
	}

	public List<Long> getIdsByDataProvider(String dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider);
		List<Long> ids = alleleDAO.findIdsByParams(params);
		ids.removeIf(Objects::isNull);
		return ids;
	}

}
