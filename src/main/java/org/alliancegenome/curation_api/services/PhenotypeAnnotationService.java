package org.alliancegenome.curation_api.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.AGMPhenotypeAnnotationDAO;
import org.alliancegenome.curation_api.dao.AllelePhenotypeAnnotationDAO;
import org.alliancegenome.curation_api.dao.GenePhenotypeAnnotationDAO;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.dao.PhenotypeAnnotationDAO;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.AGMPhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.AllelePhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GenePhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.PhenotypeAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PhenotypeFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseAnnotationCrudService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class PhenotypeAnnotationService extends BaseAnnotationCrudService<PhenotypeAnnotation, PhenotypeAnnotationDAO> {

	@Inject PhenotypeAnnotationDAO phenotypeAnnotationDAO;
	@Inject AGMPhenotypeAnnotationDAO agmPhenotypeAnnotationDAO;
	@Inject GenePhenotypeAnnotationDAO genePhenotypeAnnotationDAO;
	@Inject AllelePhenotypeAnnotationDAO allelePhenotypeAnnotationDAO;
	@Inject PersonService personService;
	@Inject PersonDAO personDAO;
	@Inject GenomicEntityService genomicEntityService;
	@Inject ReferenceService referenceService;
	@Inject AGMPhenotypeAnnotationService agmPhenotypeAnnotationService;
	@Inject GenePhenotypeAnnotationService genePhenotypeAnnotationService;
	@Inject AllelePhenotypeAnnotationService allelePhenotypeAnnotationService;

	HashMap<String, List<PhenotypeFmsDTO>> unprocessedAnnotationsMap = new HashMap<>();

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(phenotypeAnnotationDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<PhenotypeAnnotation> deleteById(Long id) {
		deprecateOrDelete(id, true, "Phenotype annotation DELETE API call", false);
		ObjectResponse<PhenotypeAnnotation> ret = new ObjectResponse<>();
		return ret;
	}

	public List<Long> getAllReferencedConditionRelationIds() {
		return getAllReferencedConditionRelationIds(phenotypeAnnotationDAO);
	}

	public List<Long> getAnnotationIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		List<Long> existingPhenotypeAnnotationIds = new ArrayList<>();
		existingPhenotypeAnnotationIds.addAll(getAnnotationIdsByDataProvider(agmPhenotypeAnnotationDAO, dataProvider));
		existingPhenotypeAnnotationIds.addAll(getAnnotationIdsByDataProvider(genePhenotypeAnnotationDAO, dataProvider));
		existingPhenotypeAnnotationIds.addAll(getAnnotationIdsByDataProvider(allelePhenotypeAnnotationDAO, dataProvider));
		return existingPhenotypeAnnotationIds;
	}

	protected <D extends BaseSQLDAO<?>> List<Long> getAnnotationIdsByDataProvider(D dao, BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);

		if (StringUtils.equals(dataProvider.sourceOrganization, "RGD") || StringUtils.equals(dataProvider.sourceOrganization, "XB")) {
			params.put(EntityFieldConstants.PA_SUBJECT_TAXON, dataProvider.canonicalTaxonCurie);
		}

		List<Long> annotationIds = dao.findIdsByParams(params);
		return annotationIds;
	}

	public Long upsertPrimaryAnnotation(PhenotypeFmsDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		if (StringUtils.isBlank(dto.getObjectId())) {
			throw new ObjectValidationException(dto, "objectId - " + ValidationConstants.REQUIRED_MESSAGE);
		}
		GenomicEntity phenotypeAnnotationSubject = genomicEntityService.findByIdentifierString(dto.getObjectId());
		if (phenotypeAnnotationSubject == null) {
			throw new ObjectValidationException(dto, "objectId - " + ValidationConstants.INVALID_MESSAGE + " (" + dto.getObjectId() + ")");
		}

		if (phenotypeAnnotationSubject instanceof AffectedGenomicModel) {
			AGMPhenotypeAnnotation annotation = agmPhenotypeAnnotationService.upsertPrimaryAnnotation((AffectedGenomicModel) phenotypeAnnotationSubject, dto, dataProvider);
			return annotation.getId();
		} else if (phenotypeAnnotationSubject instanceof Allele) {
			AllelePhenotypeAnnotation annotation = allelePhenotypeAnnotationService.upsertPrimaryAnnotation((Allele) phenotypeAnnotationSubject, dto, dataProvider);
			return annotation.getId();
		} else if (phenotypeAnnotationSubject instanceof Gene) {
			GenePhenotypeAnnotation annotation = genePhenotypeAnnotationService.upsertPrimaryAnnotation((Gene) phenotypeAnnotationSubject, dto, dataProvider);
			return annotation.getId();
		} else {
			throw new ObjectValidationException(dto, "objectId - " + ValidationConstants.INVALID_TYPE_MESSAGE + " (" + dto.getObjectId() + ")");
		}

	}

	public List<Long> addInferredOrAssertedEntities(PhenotypeFmsDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		List<Long> primaryAnnotationIds = new ArrayList<>();
		for (String primaryGeneticEntityCurie : dto.getPrimaryGeneticEntityIds()) {
			GenomicEntity primaryAnnotationSubject = genomicEntityService.findByIdentifierString(primaryGeneticEntityCurie);
			if (primaryAnnotationSubject == null) {
				throw new ObjectValidationException(dto, "primaryGeneticEntityIds - " + ValidationConstants.INVALID_MESSAGE + " (" + primaryGeneticEntityCurie + ")");
			}

			if (primaryAnnotationSubject instanceof AffectedGenomicModel) {
				List<AGMPhenotypeAnnotation> annotations = agmPhenotypeAnnotationService.addInferredOrAssertedEntities((AffectedGenomicModel) primaryAnnotationSubject, dto, dataProvider);
				if (CollectionUtils.isNotEmpty(annotations)) {
					primaryAnnotationIds.addAll(annotations.stream().map(AGMPhenotypeAnnotation::getId).collect(Collectors.toList()));
				}
			} else if (primaryAnnotationSubject instanceof Allele) {
				List<AllelePhenotypeAnnotation> annotations = allelePhenotypeAnnotationService.addInferredOrAssertedEntities((Allele) primaryAnnotationSubject, dto, dataProvider);
				if (CollectionUtils.isNotEmpty(annotations)) {
					primaryAnnotationIds.addAll(annotations.stream().map(AllelePhenotypeAnnotation::getId).collect(Collectors.toList()));
				}
			} else {
				throw new ObjectValidationException(dto, "primaryGeneticEntityIds - " + ValidationConstants.INVALID_TYPE_MESSAGE + " (" + primaryGeneticEntityCurie + ")");
			}
		}
		return primaryAnnotationIds;
	}

}
