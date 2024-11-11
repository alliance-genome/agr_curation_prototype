package org.alliancegenome.curation_api.services.validation.dto;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.DataProviderDAO;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.model.ingest.dto.DiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.BiologicalEntityService;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.ontology.DoTermService;
import org.alliancegenome.curation_api.services.ontology.EcoTermService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@RequestScoped
public class DiseaseAnnotationDTOValidator extends AnnotationDTOValidator {

	@Inject
	DoTermService doTermService;
	@Inject
	EcoTermService ecoTermService;
	@Inject
	ReferenceService referenceService;
	@Inject
	VocabularyTermService vocabularyTermService;
	@Inject
	GeneService geneService;
	@Inject
	BiologicalEntityService biologicalEntityService;
	@Inject
	DataProviderDTOValidator dataProviderDtoValidator;
	@Inject
	DataProviderDAO dataProviderDAO;

	public <E extends DiseaseAnnotation, D extends DiseaseAnnotationDTO> ObjectResponse<E> validateDiseaseAnnotationDTO(E annotation, D dto) {
		ObjectResponse<E> daResponse = validateAnnotationDTO(annotation, dto, VocabularyConstants.DISEASE_ANNOTATION_NOTE_TYPES_VOCABULARY_TERM_SET);
		annotation = daResponse.getEntity();

		if (StringUtils.isBlank(dto.getDoTermCurie())) {
			daResponse.addErrorMessage("do_term_curie", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			DOTerm disease = doTermService.findByCurieOrSecondaryId(dto.getDoTermCurie());
			if (disease == null) {
				daResponse.addErrorMessage("do_term_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getDoTermCurie() + ")");
			}
			annotation.setDiseaseAnnotationObject(disease);
		}

		if (CollectionUtils.isEmpty(dto.getEvidenceCodeCuries())) {
			daResponse.addErrorMessage("evidence_code_curies", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			List<ECOTerm> ecoTerms = new ArrayList<>();
			for (String ecoCurie : dto.getEvidenceCodeCuries()) {
				ECOTerm ecoTerm = ecoTermService.findByCurieOrSecondaryId(ecoCurie);
				if (ecoTerm == null) {
					daResponse.addErrorMessage("evidence_code_curies", ValidationConstants.INVALID_MESSAGE + " (" + ecoCurie + ")");
				} else if (!ecoTerm.getSubsets().contains(OntologyConstants.AGR_ECO_TERM_SUBSET)) {
					daResponse.addErrorMessage("evidence_code_curies", ValidationConstants.UNSUPPORTED_MESSAGE + " (" + ecoCurie + ")");
				} else {
					ecoTerms.add(ecoTerm);
				}
			}
			annotation.setEvidenceCodes(ecoTerms);
		}

		if (dto.getNegated() != null) {
			annotation.setNegated(dto.getNegated());
		} else {
			annotation.setNegated(false);
		}

		if (CollectionUtils.isNotEmpty(dto.getWithGeneIdentifiers())) {
			List<Gene> withGenes = new ArrayList<>();
			for (String withIdentifier : dto.getWithGeneIdentifiers()) {
				if (!withIdentifier.startsWith("HGNC:")) {
					daResponse.addErrorMessage("with_gene_identifiers", ValidationConstants.INVALID_MESSAGE + " (" + withIdentifier + ")");
				} else {
					Gene withGene = geneService.findByIdentifierString(withIdentifier);
					if (withGene == null) {
						daResponse.addErrorMessage("with_gene_identifiers", ValidationConstants.INVALID_MESSAGE + " (" + withIdentifier + ")");
					} else {
						withGenes.add(withGene);
					}
				}
			}
			annotation.setWith(withGenes);
		} else {
			annotation.setWith(null);
		}

		DataProvider secondaryDataProvider = null;
		if (dto.getSecondaryDataProviderDto() != null) {
			ObjectResponse<DataProvider> dpResponse = dataProviderDtoValidator.validateDataProviderDTO(dto.getSecondaryDataProviderDto(), annotation.getSecondaryDataProvider());
			if (dpResponse.hasErrors()) {
				daResponse.addErrorMessage("secondary_data_provider_dto", dpResponse.errorMessagesString());
			} else {
				secondaryDataProvider = dataProviderDAO.persist(dpResponse.getEntity());
			}
		}
		annotation.setSecondaryDataProvider(secondaryDataProvider);

		if (CollectionUtils.isNotEmpty(dto.getDiseaseQualifierNames())) {
			List<VocabularyTerm> diseaseQualifiers = new ArrayList<>();
			for (String qualifier : dto.getDiseaseQualifierNames()) {
				VocabularyTerm diseaseQualifier = vocabularyTermService.getTermInVocabulary(VocabularyConstants.DISEASE_QUALIFIER_VOCABULARY, qualifier).getEntity();
				if (diseaseQualifier == null) {
					daResponse.addErrorMessage("disease_qualifier_names", ValidationConstants.INVALID_MESSAGE + " (" + qualifier + ")");
				} else {
					diseaseQualifiers.add(diseaseQualifier);
				}
			}
			annotation.setDiseaseQualifiers(diseaseQualifiers);
		} else {
			annotation.setDiseaseQualifiers(null);
		}

		if (CollectionUtils.isNotEmpty(dto.getDiseaseGeneticModifierIdentifiers()) || StringUtils.isNotBlank(dto.getDiseaseGeneticModifierRelationName())) {
			if (CollectionUtils.isEmpty(dto.getDiseaseGeneticModifierIdentifiers())) {
				daResponse.addErrorMessage("disease_genetic_modifier_relation_name", ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "disease_genetic_modifier_identifiers");
			} else if (StringUtils.isBlank(dto.getDiseaseGeneticModifierRelationName())) {
				daResponse.addErrorMessage("disease_genetic_modifier_identifiers", ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "disease_genetic_modifier_relation_name");
			} else {
				VocabularyTerm diseaseGeneticModifierRelation = vocabularyTermService.getTermInVocabulary(VocabularyConstants.DISEASE_GENETIC_MODIFIER_RELATION_VOCABULARY, dto.getDiseaseGeneticModifierRelationName()).getEntity();
				if (diseaseGeneticModifierRelation == null) {
					daResponse.addErrorMessage("disease_genetic_modifier_relation_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getDiseaseGeneticModifierRelationName() + ")");
				}
				List<Gene> diseaseGeneticModifierGenes = new ArrayList<>();
				List<Allele> diseaseGeneticModifierAlleles = new ArrayList<>();
				List<AffectedGenomicModel> diseaseGeneticModifierAgms = new ArrayList<>();
				for (String modifierIdentifier : dto.getDiseaseGeneticModifierIdentifiers()) {
					BiologicalEntity diseaseGeneticModifier = biologicalEntityService.findByIdentifierString(modifierIdentifier);
					if (diseaseGeneticModifier == null) {
						daResponse.addErrorMessage("disease_genetic_modifier_identifiers", ValidationConstants.INVALID_MESSAGE + " (" + modifierIdentifier + ")");
					} else {
						if (diseaseGeneticModifier instanceof Gene) {
							diseaseGeneticModifierGenes.add((Gene) diseaseGeneticModifier);
						} else if (diseaseGeneticModifier instanceof Allele) {
							diseaseGeneticModifierAlleles.add((Allele) diseaseGeneticModifier);
						} else if (diseaseGeneticModifier instanceof AffectedGenomicModel) {
							diseaseGeneticModifierAgms.add((AffectedGenomicModel) diseaseGeneticModifier);
						} else {
							daResponse.addErrorMessage("disease_genetic_modifier_identifiers", ValidationConstants.INVALID_MESSAGE + " (" + modifierIdentifier + ")");
						}
					}
				}
				annotation.setDiseaseGeneticModifierGenes(diseaseGeneticModifierGenes);
				annotation.setDiseaseGeneticModifierAlleles(diseaseGeneticModifierAlleles);
				annotation.setDiseaseGeneticModifierAgms(diseaseGeneticModifierAgms);
				annotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
			}
		} else {
			annotation.setDiseaseGeneticModifierGenes(null);
			annotation.setDiseaseGeneticModifierAlleles(null);
			annotation.setDiseaseGeneticModifierAgms(null);
			annotation.setDiseaseGeneticModifierRelation(null);
		}

		VocabularyTerm annotationType = null;
		if (StringUtils.isNotBlank(dto.getAnnotationTypeName())) {
			annotationType = vocabularyTermService.getTermInVocabulary(VocabularyConstants.ANNOTATION_TYPE_VOCABULARY, dto.getAnnotationTypeName()).getEntity();
			if (annotationType == null) {
				daResponse.addErrorMessage("annotation_type_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAnnotationTypeName() + ")");
			}
		}
		annotation.setAnnotationType(annotationType);

		VocabularyTerm geneticSex = null;
		if (StringUtils.isNotBlank(dto.getGeneticSexName())) {
			geneticSex = vocabularyTermService.getTermInVocabulary(VocabularyConstants.GENETIC_SEX_VOCABULARY, dto.getGeneticSexName()).getEntity();
			if (geneticSex == null) {
				daResponse.addErrorMessage("genetic_sex_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGeneticSexName() + ")");
			}
		}
		annotation.setGeneticSex(geneticSex);

		daResponse.setEntity(annotation);

		return daResponse;
	}
}
