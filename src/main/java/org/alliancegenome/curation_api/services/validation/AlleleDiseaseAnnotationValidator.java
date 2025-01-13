package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.AlleleDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleDiseaseAnnotationValidator extends DiseaseAnnotationValidator {

	@Inject AlleleDAO alleleDAO;

	@Inject AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;

	private String errorMessage;

	public AlleleDiseaseAnnotation validateAnnotationUpdate(AlleleDiseaseAnnotation uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update Gene Disease Annotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No Gene Disease Annotation ID provided");
			throw new ApiErrorException(response);
		}
		AlleleDiseaseAnnotation dbEntity = alleleDiseaseAnnotationDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("Could not find Gene Disease Annotation with ID: [" + id + "]");
			throw new ApiErrorException(response);
			// do not continue validation for update if Disease Annotation ID has not been
			// found
		}

		return validateAnnotation(uiEntity, dbEntity);
	}

	public AlleleDiseaseAnnotation validateAnnotationCreate(AlleleDiseaseAnnotation uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Cound not create Allele Disease Annotation";

		AlleleDiseaseAnnotation dbEntity = new AlleleDiseaseAnnotation();

		return validateAnnotation(uiEntity, dbEntity);
	}

	public AlleleDiseaseAnnotation validateAnnotation(AlleleDiseaseAnnotation uiEntity, AlleleDiseaseAnnotation dbEntity) {

		Allele subject = validateSubject(uiEntity, dbEntity);
		dbEntity.setDiseaseAnnotationSubject(subject);

		Gene inferredGene = validateInferredGene(uiEntity, dbEntity);
		dbEntity.setInferredGene(inferredGene);

		List<Gene> assertedGenes = validateAssertedGenes(uiEntity, dbEntity);
		dbEntity.setAssertedGenes(assertedGenes);

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation", VocabularyConstants.ALLELE_DISEASE_RELATION_VOCABULARY_TERM_SET, dbEntity.getRelation(), uiEntity.getRelation());
		dbEntity.setRelation(relation);

		dbEntity = (AlleleDiseaseAnnotation) validateCommonDiseaseAnnotationFields(uiEntity, dbEntity);

		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		return dbEntity;
	}

	private Allele validateSubject(AlleleDiseaseAnnotation uiEntity, AlleleDiseaseAnnotation dbEntity) {
		String field = "diseaseAnnotationSubject";
		if (ObjectUtils.isEmpty(uiEntity.getDiseaseAnnotationSubject())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		Allele subjectEntity = null;
		if (uiEntity.getDiseaseAnnotationSubject().getId() != null) {
			subjectEntity = alleleDAO.find(uiEntity.getDiseaseAnnotationSubject().getId());
		}
		if (subjectEntity == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (subjectEntity.getObsolete() && (dbEntity.getDiseaseAnnotationSubject() == null || !subjectEntity.getId().equals(dbEntity.getDiseaseAnnotationSubject().getId()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		return subjectEntity;

	}

	private Gene validateInferredGene(AlleleDiseaseAnnotation uiEntity, AlleleDiseaseAnnotation dbEntity) {
		if (uiEntity.getInferredGene() == null) {
			return null;
		}

		Gene inferredGene = null;
		if (uiEntity.getInferredGene().getId() != null) {
			inferredGene = geneDAO.find(uiEntity.getInferredGene().getId());
		}
		if (inferredGene == null) {
			addMessageResponse("inferredGene", ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (inferredGene.getObsolete() && (dbEntity.getInferredGene() == null || !inferredGene.getId().equals(dbEntity.getInferredGene().getId()))) {
			addMessageResponse("inferredGene", ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return inferredGene;
	}

	private List<Gene> validateAssertedGenes(AlleleDiseaseAnnotation uiEntity, AlleleDiseaseAnnotation dbEntity) {
		if (CollectionUtils.isEmpty(uiEntity.getAssertedGenes())) {
			return null;
		}

		List<Gene> assertedGenes = new ArrayList<Gene>();
		List<Long> previousIds = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(dbEntity.getAssertedGenes())) {
			previousIds = dbEntity.getAssertedGenes().stream().map(Gene::getId).collect(Collectors.toList());
		}
		for (Gene gene : uiEntity.getAssertedGenes()) {
			Gene assertedGene = null;
			if (gene.getId() != null) {
				assertedGene = geneDAO.find(gene.getId());
			}
			if (assertedGene == null) {
				addMessageResponse("assertedGenes", ValidationConstants.INVALID_MESSAGE);
				return null;
			}
			if (assertedGene.getObsolete() && !previousIds.contains(assertedGene.getId())) {
				addMessageResponse("assertedGenes", ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}
			assertedGenes.add(assertedGene);
		}

		return assertedGenes;
	}
}
