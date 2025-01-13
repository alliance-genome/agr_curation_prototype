package org.alliancegenome.curation_api.services.validation.associations.alleleAssociations;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.associations.alleleAssociations.AlleleGeneAssociationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.lang3.ObjectUtils;

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
			Allele subject = validateSubject(uiEntity, dbEntity);
			dbEntity.setAlleleAssociationSubject(subject);
		}

		Gene object = validateObject(uiEntity, dbEntity);
		dbEntity.setAlleleGeneAssociationObject(object);

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation", VocabularyConstants.ALLELE_GENE_RELATION_VOCABULARY_TERM_SET, dbEntity.getRelation(), uiEntity.getRelation());
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

	private Allele validateSubject(AlleleGeneAssociation uiEntity, AlleleGeneAssociation dbEntity) {
		String field = "alleleAssociationSubject";
		if (ObjectUtils.isEmpty(uiEntity.getAlleleAssociationSubject())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		Allele subjectEntity = null;
		if (uiEntity.getAlleleAssociationSubject().getId() != null) {
			subjectEntity = alleleDAO.find(uiEntity.getAlleleAssociationSubject().getId());
		}
		if (subjectEntity == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (subjectEntity.getObsolete() && (dbEntity.getAlleleAssociationSubject() == null || !subjectEntity.getId().equals(dbEntity.getAlleleAssociationSubject().getId()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return subjectEntity;

	}

	private Gene validateObject(AlleleGeneAssociation uiEntity, AlleleGeneAssociation dbEntity) {
		String field = "alleleGeneAssociationObject";
		if (ObjectUtils.isEmpty(uiEntity.getAlleleGeneAssociationObject())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		Gene objectEntity = null;
		if (uiEntity.getAlleleGeneAssociationObject().getId() != null) {
			objectEntity = geneDAO.find(uiEntity.getAlleleGeneAssociationObject().getId());
		}
		if (objectEntity == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		} else {
			// fix for SCRUM-3738
			if (objectEntity.getGeneSymbol() != null) {
				if (objectEntity.getGeneSymbol().getEvidence() != null) {
					objectEntity.getGeneSymbol().getEvidence().size();
				}
			}
		}

		if (objectEntity.getObsolete() && (dbEntity.getAlleleGeneAssociationObject() == null || !objectEntity.getId().equals(dbEntity.getAlleleGeneAssociationObject().getId()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return objectEntity;

	}
}
