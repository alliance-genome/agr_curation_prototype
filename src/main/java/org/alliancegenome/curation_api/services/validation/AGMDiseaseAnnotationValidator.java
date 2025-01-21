package org.alliancegenome.curation_api.services.validation;

import java.util.List;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AGMDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AGMDiseaseAnnotationValidator extends DiseaseAnnotationValidator {

	@Inject AffectedGenomicModelDAO affectedGenomicModelDAO;

	@Inject AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;

	@Inject AlleleDAO alleleDAO;

	private String errorMessage;

	public AGMDiseaseAnnotation validateAnnotationUpdate(AGMDiseaseAnnotation uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update AGM Disease Annotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No AGM Disease Annotation ID provided");
			throw new ApiErrorException(response);
		}
		AGMDiseaseAnnotation dbEntity = agmDiseaseAnnotationDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("Could not find AGM Disease Annotation with ID: [" + id + "]");
			throw new ApiErrorException(response);
			// do not continue validation for update if Disease Annotation ID has not been
			// found
		}

		return validateAnnotation(uiEntity, dbEntity);
	}

	public AGMDiseaseAnnotation validateAnnotationCreate(AGMDiseaseAnnotation uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Cound not create AGM Disease Annotation";

		AGMDiseaseAnnotation dbEntity = new AGMDiseaseAnnotation();

		return validateAnnotation(uiEntity, dbEntity);
	}

	public AGMDiseaseAnnotation validateAnnotation(AGMDiseaseAnnotation uiEntity, AGMDiseaseAnnotation dbEntity) {

		AffectedGenomicModel subject = validateRequiredEntity(affectedGenomicModelDAO, "diseaseAnnotationSubject", uiEntity.getDiseaseAnnotationSubject(), dbEntity.getDiseaseAnnotationSubject());
		dbEntity.setDiseaseAnnotationSubject(subject);

		Gene inferredGene = validateEntity(geneDAO, "inferredGene", uiEntity.getInferredGene(), dbEntity.getInferredGene());
		dbEntity.setInferredGene(inferredGene);

		List<Gene> assertedGenes = validateEntities(geneDAO, "assertedGenes", uiEntity.getAssertedGenes(), dbEntity.getAssertedGenes());
		dbEntity.setAssertedGenes(assertedGenes);

		Allele inferredAllele = validateEntity(alleleDAO, "inferredAllele", uiEntity.getInferredAllele(), dbEntity.getInferredAllele());
		dbEntity.setInferredAllele(inferredAllele);

		Allele assertedAllele = validateEntity(alleleDAO, "assertedAllele", uiEntity.getAssertedAllele(), dbEntity.getAssertedAllele());
		dbEntity.setAssertedAllele(assertedAllele);

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation", VocabularyConstants.AGM_DISEASE_RELATION_VOCABULARY_TERM_SET, uiEntity.getRelation(), dbEntity.getRelation());
		dbEntity.setRelation(relation);

		dbEntity = (AGMDiseaseAnnotation) validateCommonDiseaseAnnotationFields(uiEntity, dbEntity);

		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		return dbEntity;
	}
}