package org.alliancegenome.curation_api.services.helpers.annotations;

import org.alliancegenome.curation_api.enums.ConditionRelationFmsEnum;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.model.ingest.dto.ConditionRelationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.DiseaseAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.ExperimentalConditionDTO;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.*;
import org.alliancegenome.curation_api.services.helpers.UniqueIdGeneratorHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AnnotationUniqueIdHelper {

	public static final String DELIMITER = "|";

	public static String getConditionRelationUniqueId(ConditionRelation relation) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		if (relation.getConditionRelationType() != null) {
			uniqueId.add(relation.getConditionRelationType().getName());
		}
		uniqueId.add(relation.getHandle());
		if (relation.getSingleReference() != null) {
			uniqueId.add(relation.getSingleReference().getCurie());
		}
		if (CollectionUtils.isNotEmpty(relation.getConditions())) {
			relation.getConditions().forEach(experimentalCondition -> uniqueId.add(getExperimentalConditionUniqueId(experimentalCondition)));
		}
		return uniqueId.getUniqueId();
	}

	public static String getConditionRelationUniqueId(ConditionRelationDTO dto, String refCurie) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		if (dto.getConditionRelationTypeName() != null) {
			uniqueId.add(dto.getConditionRelationTypeName());
		}
		uniqueId.add(dto.getHandle());
		if (refCurie != null) {
			uniqueId.add(refCurie);
		}
		if (CollectionUtils.isNotEmpty(dto.getConditionDtos())) {
			dto.getConditionDtos().forEach(experimentalCondition -> uniqueId.add(getExperimentalConditionUniqueId(experimentalCondition)));
		}
		return uniqueId.getUniqueId();
	}

	public static String getConditionRelationUniqueId(ConditionRelationFmsDTO dto, String relationType) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(relationType);
		if (CollectionUtils.isNotEmpty(dto.getConditions())) {
			dto.getConditions().forEach(experimentalCondition -> uniqueId.add(getExperimentalConditionUniqueId(experimentalCondition)));
		}
		return uniqueId.getUniqueId();
	}

	public static String getDiseaseAnnotationUniqueId(DiseaseAnnotationDTO annotationDTO, String subjectIdentifier, String refCurie) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(subjectIdentifier);
		uniqueId.add(annotationDTO.getDiseaseRelationName());
		if (annotationDTO.getNegated()) {
			uniqueId.add(annotationDTO.getNegated().toString());
		} else {
			uniqueId.add("false");
		}
		uniqueId.add(annotationDTO.getDoTermCurie());
		uniqueId.add(refCurie);
		uniqueId.addList(annotationDTO.getEvidenceCodeCuries());
		uniqueId.addList(annotationDTO.getWithGeneIdentifiers());
		if (CollectionUtils.isNotEmpty(annotationDTO.getConditionRelationDtos())) {
			uniqueId.addList(annotationDTO.getConditionRelationDtos().stream().map(conditionDTO -> {
				UniqueIdGeneratorHelper gen = new UniqueIdGeneratorHelper();
				gen.add(conditionDTO.getConditionRelationTypeName());
				if (CollectionUtils.isNotEmpty(conditionDTO.getConditionDtos())) {
					gen.add(conditionDTO.getConditionDtos().stream().map(AnnotationUniqueIdHelper::getExperimentalConditionUniqueId).collect(Collectors.joining(DELIMITER)));
				}
				return gen.getUniqueId();
			}).collect(Collectors.toList()));
		}
		uniqueId.addList(annotationDTO.getDiseaseQualifierNames());
		uniqueId.add(annotationDTO.getDiseaseGeneticModifierRelationName());
		uniqueId.addList(annotationDTO.getDiseaseGeneticModifierIdentifiers());
		if (annotationDTO instanceof GeneDiseaseAnnotationDTO gda) {
			if (gda.getSgdStrainBackgroundIdentifier() != null) {
				uniqueId.add(gda.getSgdStrainBackgroundIdentifier());
			}
		}
		return uniqueId.getUniqueId();
	}

	public static String getDiseaseAnnotationUniqueId(DiseaseAnnotation annotation) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(annotation.getSubjectIdentifier());
		if (annotation.getRelation() != null) {
			uniqueId.add(annotation.getRelation().getName());
		}
		if (annotation.getNegated() != null) {
			uniqueId.add(annotation.getNegated().toString());
		}
		if (annotation.getDiseaseAnnotationObject() != null) {
			uniqueId.add(annotation.getDiseaseAnnotationObject().getCurie());
		}
		if (annotation.getSingleReference() != null) {
			uniqueId.add(annotation.getSingleReference().getCurie());
		}
		if (CollectionUtils.isNotEmpty(annotation.getEvidenceCodes())) {
			uniqueId.addList(annotation.getEvidenceCodes().stream().map(ECOTerm::getCurie).collect(Collectors.toList()));
		}
		uniqueId.addSubmittedObjectList(annotation.getWith());
		if (CollectionUtils.isNotEmpty(annotation.getConditionRelations())) {
			uniqueId.addList(annotation.getConditionRelations().stream().map(condition -> {
				UniqueIdGeneratorHelper gen = new UniqueIdGeneratorHelper();
				gen.add(condition.getConditionRelationType().getName());
				gen.add(condition.getConditions().stream().map(AnnotationUniqueIdHelper::getExperimentalConditionUniqueId).collect(Collectors.joining(DELIMITER)));
				return gen.getUniqueId();
			}).collect(Collectors.toList()));
		}
		if (CollectionUtils.isNotEmpty(annotation.getDiseaseQualifiers())) {
			uniqueId.addList(annotation.getDiseaseQualifiers().stream().map(VocabularyTerm::getName).collect(Collectors.toList()));
		}
		if (annotation.getDiseaseGeneticModifierRelation() != null) {
			uniqueId.add(annotation.getDiseaseGeneticModifierRelation().getName());
		}
		if (annotation instanceof GeneDiseaseAnnotation gda) {
			if (gda.getSgdStrainBackground() != null) {
				uniqueId.add(gda.getSgdStrainBackground().getIdentifier());
			}
		}
		List<BiologicalEntity> geneticModifiers = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(annotation.getDiseaseGeneticModifierGenes())) {
			geneticModifiers.addAll(annotation.getDiseaseGeneticModifierGenes());
		}
		if (CollectionUtils.isNotEmpty(annotation.getDiseaseGeneticModifierAlleles())) {
			geneticModifiers.addAll(annotation.getDiseaseGeneticModifierAlleles());
		}
		if (CollectionUtils.isNotEmpty(annotation.getDiseaseGeneticModifierAgms())) {
			geneticModifiers.addAll(annotation.getDiseaseGeneticModifierAgms());
		}
		uniqueId.addSubmittedObjectList(geneticModifiers);
		return uniqueId.getUniqueId();
	}

	public static String getPhenotypeAnnotationUniqueId(PhenotypeFmsDTO annotationFmsDTO, String subjectIdentifier, String refCurie) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(subjectIdentifier);
		uniqueId.add("has_phenotype");
		uniqueId.add(annotationFmsDTO.getPhenotypeStatement());
		if (CollectionUtils.isNotEmpty(annotationFmsDTO.getPhenotypeTermIdentifiers())) {
			uniqueId.addList(annotationFmsDTO.getPhenotypeTermIdentifiers().stream().map(PhenotypeTermIdentifierFmsDTO::getTermId).collect(Collectors.toList()));
		}
		uniqueId.add(refCurie);
		if (CollectionUtils.isNotEmpty(annotationFmsDTO.getConditionRelations())) {
			List<String> crIds = new ArrayList<>();
			for (ConditionRelationFmsDTO crFmsDto : annotationFmsDTO.getConditionRelations()) {
				UniqueIdGeneratorHelper gen = new UniqueIdGeneratorHelper();
				ConditionRelationFmsEnum fmsCr = null;
				if (StringUtils.isNotBlank(crFmsDto.getConditionRelationType())) {
					fmsCr = ConditionRelationFmsEnum.findByName(crFmsDto.getConditionRelationType());
				}
				if (fmsCr != null) {
					gen.add(fmsCr.agrRelation);
				}
				if (CollectionUtils.isNotEmpty(crFmsDto.getConditions())) {
					gen.add(crFmsDto.getConditions().stream().map(AnnotationUniqueIdHelper::getExperimentalConditionUniqueId).collect(Collectors.joining(DELIMITER)));
				}
				crIds.add(gen.getUniqueId());
			}
			uniqueId.addAll(crIds);
		}

		return uniqueId.getUniqueId();
	}

	public static String getPhenotypeAnnotationUniqueId(PhenotypeAnnotation annotation) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(annotation.getSubjectIdentifier());
		if (annotation.getRelation() != null) {
			uniqueId.add(annotation.getRelation().getName());
		}
		if (annotation.getPhenotypeAnnotationObject() != null) {
			uniqueId.add(annotation.getPhenotypeAnnotationObject());
		}
		uniqueId.add(annotation.getRelation().getName());
		if (annotation.getSingleReference() != null) {
			uniqueId.add(annotation.getSingleReference().getCurie());
		}
		if (CollectionUtils.isNotEmpty(annotation.getConditionRelations())) {
			uniqueId.addList(annotation.getConditionRelations().stream().map(condition -> {
				UniqueIdGeneratorHelper gen = new UniqueIdGeneratorHelper();
				gen.add(condition.getConditionRelationType().getName());
				gen.add(condition.getConditions().stream().map(AnnotationUniqueIdHelper::getExperimentalConditionUniqueId).collect(Collectors.joining(DELIMITER)));
				return gen.getUniqueId();
			}).collect(Collectors.toList()));
		}
		return uniqueId.getUniqueId();
	}

	public static String getExperimentalConditionUniqueId(ExperimentalCondition cond) {
		UniqueIdGeneratorHelper help = new UniqueIdGeneratorHelper();
		if (cond.getConditionClass() != null) {
			help.add(cond.getConditionClass().getCurie());
		}
		if (cond.getConditionId() != null) {
			help.add(cond.getConditionId().getCurie());
		}
		if (cond.getConditionAnatomy() != null) {
			help.add(cond.getConditionAnatomy().getCurie());
		}
		if (cond.getConditionChemical() != null) {
			help.add(cond.getConditionChemical().getCurie());
		}
		if (cond.getConditionGeneOntology() != null) {
			help.add(cond.getConditionGeneOntology().getCurie());
		}
		if (cond.getConditionTaxon() != null) {
			help.add(cond.getConditionTaxon().getCurie());
		}
		help.add(cond.getConditionQuantity());
		help.add(cond.getConditionFreeText());
		return help.getUniqueId();
	}

	public static String getExperimentalConditionUniqueId(ExperimentalConditionDTO dto) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(dto.getConditionClassCurie());
		uniqueId.add(dto.getConditionIdCurie());
		uniqueId.add(dto.getConditionAnatomyCurie());
		uniqueId.add(dto.getConditionChemicalCurie());
		uniqueId.add(dto.getConditionGeneOntologyCurie());
		uniqueId.add(dto.getConditionTaxonCurie());
		uniqueId.add(dto.getConditionQuantity());
		uniqueId.add(dto.getConditionFreeText());
		return uniqueId.getUniqueId();
	}

	public static String getExperimentalConditionUniqueId(ExperimentalConditionFmsDTO dto) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(dto.getConditionClassId());
		uniqueId.add(dto.getConditionId());
		uniqueId.add(dto.getAnatomicalOntologyId());
		uniqueId.add(dto.getChemicalOntologyId());
		uniqueId.add(dto.getGeneOntologyId());
		uniqueId.add(dto.getNcbiTaxonId());
		uniqueId.add(dto.getConditionQuantity());
		uniqueId.add(dto.getConditionStatement());
		return uniqueId.getUniqueId();
	}

	public String getEvidenceCurie(EvidenceFmsDTO dto) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();

		if (dto.getPublication().getCrossReference() != null) {
			uniqueId.add(dto.getPublication().getCrossReference().getCurie());
		} else {
			uniqueId.add(dto.getPublication().getPublicationId());
		}

		if (CollectionUtils.isNotEmpty(dto.getEvidenceCodes())) {
			dto.getEvidenceCodes().sort(Comparator.naturalOrder());
			uniqueId.add(StringUtils.join(dto.getEvidenceCodes(), "::"));
		}
		return uniqueId.getUniqueId();
	}

	public String getEvidenceCurie(List<String> codes, String reference) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();

		if (reference != null) {
			uniqueId.add(reference);
		}

		if (CollectionUtils.isNotEmpty(codes)) {
			codes.sort(Comparator.naturalOrder());
			uniqueId.add(StringUtils.join(codes, "::"));
		}
		return uniqueId.getUniqueId();
	}
}