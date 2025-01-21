package org.alliancegenome.curation_api.services.validation;

import java.util.List;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.AlleleDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;

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

		Allele subject = validateRequiredEntity(alleleDAO, "diseaseAnnotationSubject", uiEntity.getDiseaseAnnotationSubject(), dbEntity.getDiseaseAnnotationSubject());
		dbEntity.setDiseaseAnnotationSubject(subject);

		Gene inferredGene = validateEntity(geneDAO, "inferredGene", uiEntity.getInferredGene(), dbEntity.getInferredGene());
		dbEntity.setInferredGene(inferredGene);

		List<Gene> assertedGenes = validateEntities(geneDAO, "assertedGenes", uiEntity.getAssertedGenes(), dbEntity.getAssertedGenes());
		dbEntity.setAssertedGenes(assertedGenes);

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation", VocabularyConstants.ALLELE_DISEASE_RELATION_VOCABULARY_TERM_SET, uiEntity.getRelation(), dbEntity.getRelation());
		dbEntity.setRelation(relation);

		dbEntity = (AlleleDiseaseAnnotation) validateCommonDiseaseAnnotationFields(uiEntity, dbEntity);

		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		return dbEntity;
	}
}
