package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
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
import org.apache.commons.lang3.tuple.ImmutablePair;

import jakarta.inject.Inject;

public class DiseaseAnnotationDTOValidator <E extends DiseaseAnnotation, D extends DiseaseAnnotationDTO> extends AnnotationDTOValidator<E, D> {

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

	public E validateDiseaseAnnotationDTO(E annotation, D dto) {
		annotation = validateAnnotationDTO(annotation, dto, VocabularyConstants.DISEASE_ANNOTATION_NOTE_TYPES_VOCABULARY_TERM_SET);
		
		DOTerm disease = validateRequiredOntologyTerm(doTermService, "do_term_curie", dto.getDoTermCurie());
		annotation.setDiseaseAnnotationObject(disease);

		List<ECOTerm> ecoTerms = validateRequiredOntologyTerms(ecoTermService, "evidence_code_curies", dto.getEvidenceCodeCuries());
		if (ecoTerms != null) {
			for (ECOTerm ecoTerm : ecoTerms) {
				if (!ecoTerm.getSubsets().contains(OntologyConstants.AGR_ECO_TERM_SUBSET)) {
					response.addErrorMessage("evidence_code_curies", ValidationConstants.UNSUPPORTED_MESSAGE + " (" + ecoTerm.getCurie() + ")");
					break;
				} 
			}
		}
		annotation.setEvidenceCodes(ecoTerms);

		if (dto.getNegated() != null) {
			annotation.setNegated(dto.getNegated());
		} else {
			annotation.setNegated(false);
		}

		List<Gene> withGenes = validateIdentifiers(geneService, "with_gene_identifiers", dto.getWithGeneIdentifiers());
		annotation.setWith(withGenes);

		if (dto.getSecondaryDataProviderDto() == null) {
			annotation.setSecondaryDataProvider(null);
			annotation.setSecondaryDataProviderCrossReference(null);
		} else {
			ObjectResponse<ImmutablePair<Organization, CrossReference>> dpResponse = validateDataProviderDTO(dto.getSecondaryDataProviderDto(), annotation.getSecondaryDataProviderCrossReference());
			if (dpResponse.hasErrors()) {
				response.addErrorMessage("data_provider_dto", dpResponse.errorMessagesString());
			} else {
				annotation.setSecondaryDataProvider(dpResponse.getEntity().getLeft());
				if (dpResponse.getEntity().getRight() != null) {
					annotation.setSecondaryDataProviderCrossReference(crossReferenceDAO.persist(dpResponse.getEntity().getRight()));
				} else {
					annotation.setSecondaryDataProviderCrossReference(null);
				}
			}
		}
		
		List<VocabularyTerm> diseaseQualifiers = validateTermsInVocabulary("disease_qualifier_names", dto.getDiseaseQualifierNames(), VocabularyConstants.DISEASE_QUALIFIER_VOCABULARY);
		annotation.setDiseaseQualifiers(diseaseQualifiers);
		
		if (CollectionUtils.isNotEmpty(dto.getDiseaseGeneticModifierIdentifiers()) || StringUtils.isNotBlank(dto.getDiseaseGeneticModifierRelationName())) {
			if (CollectionUtils.isEmpty(dto.getDiseaseGeneticModifierIdentifiers())) {
				response.addErrorMessage("disease_genetic_modifier_relation_name", ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "disease_genetic_modifier_identifiers");
			} else if (StringUtils.isBlank(dto.getDiseaseGeneticModifierRelationName())) {
				response.addErrorMessage("disease_genetic_modifier_identifiers", ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "disease_genetic_modifier_relation_name");
			} else {
				VocabularyTerm diseaseGeneticModifierRelation = validateTermInVocabulary("disease_genetic_modifier_relation_name", dto.getDiseaseGeneticModifierRelationName(), VocabularyConstants.DISEASE_GENETIC_MODIFIER_RELATION_VOCABULARY);
				
				List<Gene> diseaseGeneticModifierGenes = new ArrayList<>();
				List<Allele> diseaseGeneticModifierAlleles = new ArrayList<>();
				List<AffectedGenomicModel> diseaseGeneticModifierAgms = new ArrayList<>();
				for (String modifierIdentifier : dto.getDiseaseGeneticModifierIdentifiers()) {
					BiologicalEntity diseaseGeneticModifier = validateIdentifier(biologicalEntityService, "disease_genetic_modifier_identifiers", modifierIdentifier);
					if (diseaseGeneticModifier != null) {
						if (diseaseGeneticModifier instanceof Gene) {
							diseaseGeneticModifierGenes.add((Gene) diseaseGeneticModifier);
						} else if (diseaseGeneticModifier instanceof Allele) {
							diseaseGeneticModifierAlleles.add((Allele) diseaseGeneticModifier);
						} else if (diseaseGeneticModifier instanceof AffectedGenomicModel) {
							diseaseGeneticModifierAgms.add((AffectedGenomicModel) diseaseGeneticModifier);
						} else {
							response.addErrorMessage("disease_genetic_modifier_identifiers", ValidationConstants.INVALID_MESSAGE + " (" + modifierIdentifier + ")");
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

		VocabularyTerm annotationType = validateTermInVocabulary("annotation_type_name", dto.getAnnotationTypeName(), VocabularyConstants.ANNOTATION_TYPE_VOCABULARY);
		annotation.setAnnotationType(annotationType);

		VocabularyTerm geneticSex = validateTermInVocabulary("genetic_sex_name", dto.getGeneticSexName(), VocabularyConstants.GENETIC_SEX_VOCABULARY);
		annotation.setGeneticSex(geneticSex);

		return annotation;
	}
}
