package org.alliancegenome.curation_api.services.validation.associations.alleleAssociations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.associations.alleleAssociations.AlleleGeneAssociationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleGeneAssociationValidator extends AlleleGenomicEntityAssociationValidator<AlleleGeneAssociation> {

	@Inject GeneDAO geneDAO;
	@Inject AlleleGeneAssociationDAO alleleGeneAssociationDAO;

	private String errorMessage;

	public ObjectResponse<AlleleGeneAssociation> validateAlleleGeneAssociation(AlleleGeneAssociation uiEntity) {
		AlleleGeneAssociation geneAssociation = validateAlleleGeneAssociation(uiEntity, false, false);
		response.setEntity(geneAssociation);
		return response;
	}

	public AlleleGeneAssociation validateAlleleGeneAssociation(AlleleGeneAssociation uiEntity, Boolean throwError, Boolean validateAllele) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not create/update Allele Gene Association: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		AlleleGeneAssociation dbEntity = null;
		if (id != null) {
			dbEntity = alleleGeneAssociationDAO.find(id);
			if (dbEntity == null) {
				addMessageResponse("Could not find Allele Gene Association with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new AlleleGeneAssociation();
		}

		dbEntity = (AlleleGeneAssociation) validateAlleleGenomicEntityAssociationFields(uiEntity, dbEntity);

		if (validateAllele) {
			Allele subject = validateRequiredEntity(alleleDAO, "alleleAssociationSubject", uiEntity.getAlleleAssociationSubject(), dbEntity.getAlleleAssociationSubject());
			dbEntity.setAlleleAssociationSubject(subject);
		}

		Gene object = validateObject(uiEntity, dbEntity);
		dbEntity.setAlleleGeneAssociationObject(object);

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation", VocabularyConstants.ALLELE_GENE_RELATION_VOCABULARY_TERM_SET, uiEntity.getRelation(), dbEntity.getRelation());
		dbEntity.setRelation(relation);

		if (response.hasErrors()) {
			if (throwError) {
				response.setErrorMessage(errorMessage);
				throw new ApiErrorException(response);
			} else {
				return null;
			}
		}

		return dbEntity;
	}

	private Gene validateObject(AlleleGeneAssociation uiEntity, AlleleGeneAssociation dbEntity) {
		Gene objectEntity = validateRequiredEntity(geneDAO, "alleleGeneAssociationObject", uiEntity.getAlleleGeneAssociationObject(), dbEntity.getAlleleGeneAssociationObject());
		
		// fix for SCRUM-3738
		if (objectEntity != null) {
			if (objectEntity.getGeneSymbol() != null) {
				if (objectEntity.getGeneSymbol().getEvidence() != null) {
					objectEntity.getGeneSymbol().getEvidence().size();
				}
			}
		}

		return objectEntity;
	}
}
