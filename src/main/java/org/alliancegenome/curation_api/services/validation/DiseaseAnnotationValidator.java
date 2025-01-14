package org.alliancegenome.curation_api.services.validation;

import java.util.List;

import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.OrganizationDAO;
import org.alliancegenome.curation_api.dao.ontology.DoTermDAO;
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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.inject.Inject;

public class DiseaseAnnotationValidator extends AnnotationValidator {

	@Inject EcoTermDAO ecoTermDAO;
	@Inject DoTermDAO doTermDAO;
	@Inject GeneDAO geneDAO;
	@Inject AlleleDAO alleleDAO;
	@Inject AffectedGenomicModelDAO agmDAO;
	@Inject DiseaseAnnotationDAO diseaseAnnotationDAO;
	@Inject OrganizationDAO organizationDAO;
	@Inject OrganizationService organizationService;

	private List<ECOTerm> validateEvidenceCodes(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		String field = "evidenceCodes";
		List<ECOTerm> validatedTerms = validateRequiredEntities(ecoTermDAO, field, uiEntity.getEvidenceCodes(), dbEntity.getEvidenceCodes());
		
		if (CollectionUtils.isNotEmpty(validatedTerms)) {
			for (ECOTerm ec : validatedTerms) {
				if (CollectionUtils.isEmpty(ec.getSubsets()) || !ec.getSubsets().contains(OntologyConstants.AGR_ECO_TERM_SUBSET)) {
					addMessageResponse(field, ValidationConstants.UNSUPPORTED_MESSAGE);
					return null;
				}
			}
		}
			
		return validatedTerms;
	}

	public List<Gene> validateWith(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		String field = "with";
		List<Gene> validatedGenes = validateEntities(geneDAO, field, uiEntity.getWith(), dbEntity.getWith());
		
		if (CollectionUtils.isNotEmpty(validatedGenes)) {
			for (Gene gene : validatedGenes) {
				if (gene.getPrimaryExternalId() == null || !gene.getPrimaryExternalId().startsWith("HGNC:")) {
					addMessageResponse("with", ValidationConstants.INVALID_MESSAGE);
					return null;
				}
			}
		}

		return validatedGenes;
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

		DOTerm term = validateRequiredEntity(doTermDAO, "diseaseAnnotationObject", uiEntity.getDiseaseAnnotationObject(), dbEntity.getDiseaseAnnotationObject());
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
