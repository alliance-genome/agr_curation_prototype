package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.OrganizationDAO;
import org.alliancegenome.curation_api.dao.ontology.EcoTermDAO;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.OrganizationService;
import org.alliancegenome.curation_api.services.helpers.annotations.AnnotationUniqueIdHelper;
import org.alliancegenome.curation_api.services.ontology.DoTermService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.inject.Inject;

public class DiseaseAnnotationValidator extends AnnotationValidator {

	@Inject EcoTermDAO ecoTermDAO;
	@Inject DoTermService doTermService;
	@Inject GeneDAO geneDAO;
	@Inject AlleleDAO alleleDAO;
	@Inject AffectedGenomicModelDAO agmDAO;
	@Inject DiseaseAnnotationDAO diseaseAnnotationDAO;
	@Inject OrganizationDAO organizationDAO;
	@Inject OrganizationService organizationService;

	public DOTerm validateObjectOntologyTerm(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		String field = "diseaseAnnotationObject";
		if (uiEntity.getDiseaseAnnotationObject() == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		DOTerm diseaseTerm = null;
		if (StringUtils.isNotBlank(uiEntity.getDiseaseAnnotationObject().getCurie())) {
			diseaseTerm = doTermService.findByCurie(uiEntity.getDiseaseAnnotationObject().getCurie());
			if (diseaseTerm == null) {
				addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			} else if (diseaseTerm.getObsolete() && (dbEntity.getDiseaseAnnotationObject() == null || !diseaseTerm.getId().equals(dbEntity.getDiseaseAnnotationObject().getId()))) {
				addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}
		}
		return diseaseTerm;
	}

	public List<ECOTerm> validateEvidenceCodes(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		String field = "evidenceCodes";
		if (CollectionUtils.isEmpty(uiEntity.getEvidenceCodes())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		List<ECOTerm> validEvidenceCodes = new ArrayList<>();
		List<Long> previousIds = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(dbEntity.getEvidenceCodes())) {
			previousIds = dbEntity.getEvidenceCodes().stream().map(ECOTerm::getId).collect(Collectors.toList());
		}
		for (ECOTerm ec : uiEntity.getEvidenceCodes()) {
			ECOTerm evidenceCode = null;
			if (ec.getId() != null) {
				evidenceCode = ecoTermDAO.find(ec.getId());
			}
			if (evidenceCode == null) {
				addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			}
			if (evidenceCode.getObsolete() && (CollectionUtils.isEmpty(dbEntity.getEvidenceCodes()) || !previousIds.contains(evidenceCode.getId()))) {
				addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}
			if (!evidenceCode.getSubsets().contains(OntologyConstants.AGR_ECO_TERM_SUBSET)) {
				addMessageResponse(field, ValidationConstants.UNSUPPORTED_MESSAGE);
				return null;
			}
			validEvidenceCodes.add(evidenceCode);
		}
		return validEvidenceCodes;
	}

	public List<Gene> validateWith(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		if (CollectionUtils.isEmpty(uiEntity.getWith())) {
			return null;
		}

		List<Gene> validWithGenes = new ArrayList<Gene>();
		List<Long> previousIds = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(dbEntity.getWith())) {
			previousIds = dbEntity.getWith().stream().map(Gene::getId).collect(Collectors.toList());
		}
		for (Gene wg : uiEntity.getWith()) {
			Gene withGene = null;
			if (wg.getId() != null) {
				withGene = geneDAO.find(wg.getId());
			}
			if (withGene == null || withGene.getPrimaryExternalId() == null || !withGene.getPrimaryExternalId().startsWith("HGNC:")) {
				addMessageResponse("with", ValidationConstants.INVALID_MESSAGE);
				return null;
			} else if (withGene.getObsolete() && !previousIds.contains(withGene.getId())) {
				addMessageResponse("with", ValidationConstants.OBSOLETE_MESSAGE);
			} else {
				validWithGenes.add(withGene);
			}
		}

		return validWithGenes;
	}

	public Organization validateSecondaryDataProvider(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		String field = "secondaryDataProvider";

		if (uiEntity.getSecondaryDataProvider() == null) {
			if (dbEntity.getId() == null) {
				return organizationDAO.getOrCreateOrganization("Alliance");
			} else {
				return null;
			}
		}
		
		Organization secondaryDataProvider = null;
		if (uiEntity.getSecondaryDataProvider().getId() != null) {
			secondaryDataProvider = organizationService.getById(uiEntity.getSecondaryDataProvider().getId()).getEntity();
		} else if (StringUtils.isNotBlank(uiEntity.getSecondaryDataProvider().getAbbreviation())) {
			secondaryDataProvider = organizationService.getByAbbr(uiEntity.getSecondaryDataProvider().getAbbreviation()).getEntity();
		}
		
		if (secondaryDataProvider == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (secondaryDataProvider.getObsolete() && (dbEntity.getSecondaryDataProvider() == null || !secondaryDataProvider.getId().equals(dbEntity.getSecondaryDataProvider().getId()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return secondaryDataProvider;
	}
	
	public List<Gene> validateDiseaseGeneticModifierGenes(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		String field = "diseaseGeneticModifierGenes";
		if (CollectionUtils.isEmpty(uiEntity.getDiseaseGeneticModifierGenes())) {
			return null;
		}

		if (uiEntity.getDiseaseGeneticModifierRelation() == null) {
			addMessageResponse(field, ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "diseaseGeneticModifierRelation");
			return null;
		}

		return validateEntities(geneDAO, field, uiEntity.getDiseaseGeneticModifierGenes(), dbEntity.getDiseaseGeneticModifierGenes());
	}

	public List<AffectedGenomicModel> validateDiseaseGeneticModifierAgms(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		String field = "diseaseGeneticModifierAgms";
		if (CollectionUtils.isEmpty(uiEntity.getDiseaseGeneticModifierAgms())) {
			return null;
		}

		if (uiEntity.getDiseaseGeneticModifierRelation() == null) {
			addMessageResponse(field, ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "diseaseGeneticModifierRelation");
			return null;
		}

		return validateEntities(agmDAO, field, uiEntity.getDiseaseGeneticModifierAgms(), dbEntity.getDiseaseGeneticModifierAgms());
	}

	public List<Allele> validateDiseaseGeneticModifierAlleles(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		String field = "diseaseGeneticModifierAlleles";
		if (CollectionUtils.isEmpty(uiEntity.getDiseaseGeneticModifierGenes())) {
			return null;
		}

		if (uiEntity.getDiseaseGeneticModifierRelation() == null) {
			addMessageResponse(field, ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "diseaseGeneticModifierRelation");
			return null;
		}

		return validateEntities(alleleDAO, field, uiEntity.getDiseaseGeneticModifierAlleles(), dbEntity.getDiseaseGeneticModifierAlleles());
	}

	public VocabularyTerm validateDiseaseGeneticModifierRelation(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		String field = "diseaseGeneticModifierRelation";
		if (uiEntity.getDiseaseGeneticModifierRelation() == null) {
			return null;
		}

		if (CollectionUtils.isEmpty(uiEntity.getDiseaseGeneticModifierGenes()) && CollectionUtils.isEmpty(uiEntity.getDiseaseGeneticModifierAlleles())
				&& CollectionUtils.isEmpty(uiEntity.getDiseaseGeneticModifierAgms())) {
			addMessageResponse(field, ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "diseaseGeneticModifierGenes / diseaseGeneticModifierAlleles / diseaseGeneticModifierAgms");
			return null;
		}
		
		return validateTermInVocabulary(field, VocabularyConstants.DISEASE_GENETIC_MODIFIER_RELATION_VOCABULARY, uiEntity.getDiseaseGeneticModifierRelation(), dbEntity.getDiseaseGeneticModifierRelation());
	}

	public String validateUniqueId(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {

		if (dbEntity.getDataProvider() == null) {
			return null;
		}

		String uniqueId = AnnotationUniqueIdHelper.getDiseaseAnnotationUniqueId(uiEntity);

		if (dbEntity.getUniqueId() == null || !uniqueId.equals(dbEntity.getUniqueId())) {
			SearchResponse<DiseaseAnnotation> response = diseaseAnnotationDAO.findByField("uniqueId", uniqueId);
			if (response != null) {
				addMessageResponse("uniqueId", ValidationConstants.NON_UNIQUE_MESSAGE);
				return null;
			}
		}

		return uniqueId;
	}

	public DiseaseAnnotation validateCommonDiseaseAnnotationFields(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {

		DOTerm term = validateObjectOntologyTerm(uiEntity, dbEntity);
		dbEntity.setDiseaseAnnotationObject(term);

		List<ECOTerm> terms = validateEvidenceCodes(uiEntity, dbEntity);
		dbEntity.setEvidenceCodes(terms);

		List<Gene> genes = validateWith(uiEntity, dbEntity);
		dbEntity.setWith(genes);

		Boolean negated = uiEntity.getNegated() != null && uiEntity.getNegated();
		dbEntity.setNegated(negated);

		VocabularyTerm annotationType = validateTermInVocabulary("annotationType", VocabularyConstants.ANNOTATION_TYPE_VOCABULARY, uiEntity.getAnnotationType(), dbEntity.getAnnotationType());
		dbEntity.setAnnotationType(annotationType);

		VocabularyTerm geneticSex = validateTermInVocabulary("geneticSex", VocabularyConstants.GENETIC_SEX_VOCABULARY, uiEntity.getGeneticSex(), dbEntity.getGeneticSex());
		dbEntity.setGeneticSex(geneticSex);

		Organization secondaryDataProvider = validateSecondaryDataProvider(uiEntity, dbEntity);
		dbEntity.setSecondaryDataProvider(secondaryDataProvider);
		
		CrossReference secondaryDataProviderCrossReference = validateDataProviderCrossReference(uiEntity.getSecondaryDataProviderCrossReference(), dbEntity.getSecondaryDataProviderCrossReference(), true);
		dbEntity.setDataProviderCrossReference(secondaryDataProviderCrossReference);
		
		List<Gene> diseaseGeneticModifierGenes = validateDiseaseGeneticModifierGenes(uiEntity, dbEntity);
		List<Allele> diseaseGeneticModifierAlleles = validateDiseaseGeneticModifierAlleles(uiEntity, dbEntity);
		List<AffectedGenomicModel> diseaseGeneticModifierAgms = validateDiseaseGeneticModifierAgms(uiEntity, dbEntity);
		VocabularyTerm dgmRelation = validateDiseaseGeneticModifierRelation(uiEntity, dbEntity);
		dbEntity.setDiseaseGeneticModifierGenes(diseaseGeneticModifierGenes);
		dbEntity.setDiseaseGeneticModifierAlleles(diseaseGeneticModifierAlleles);
		dbEntity.setDiseaseGeneticModifierAgms(diseaseGeneticModifierAgms);
		dbEntity.setDiseaseGeneticModifierRelation(dgmRelation);

		List<VocabularyTerm> diseaseQualifiers = validateTermsInVocabulary("diseaseQualifiers", VocabularyConstants.DISEASE_QUALIFIER_VOCABULARY, uiEntity.getDiseaseQualifiers(), dbEntity.getDiseaseQualifiers());
		dbEntity.setDiseaseQualifiers(diseaseQualifiers);

		dbEntity = (DiseaseAnnotation) validateCommonAnnotationFields(uiEntity, dbEntity, VocabularyConstants.DISEASE_ANNOTATION_NOTE_TYPES_VOCABULARY_TERM_SET);

		String uniqueId = validateUniqueId(uiEntity, dbEntity);
		dbEntity.setUniqueId(uniqueId);

		return dbEntity;
	}
}
