package org.alliancegenome.curation_api.services.validation.associations.alleleAssociations;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.VariantDAO;
import org.alliancegenome.curation_api.dao.associations.alleleAssociations.AlleleVariantAssociationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleVariantAssociation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.lang3.ObjectUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleVariantAssociationValidator extends AlleleGenomicEntityAssociationValidator<AlleleVariantAssociation> {

	@Inject VariantDAO variantDAO;
	@Inject AlleleVariantAssociationDAO alleleVariantAssociationDAO;

	private String errorMessage;

	public ObjectResponse<AlleleVariantAssociation> validateAlleleVariantAssociation(AlleleVariantAssociation uiEntity) {
		AlleleVariantAssociation variantAssociation = validateAlleleVariantAssociation(uiEntity, false, false);
		response.setEntity(variantAssociation);
		return response;
	}

	public AlleleVariantAssociation validateAlleleVariantAssociation(AlleleVariantAssociation uiEntity, Boolean throwError, Boolean validateAllele) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not create/update Allele Variant Association: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		AlleleVariantAssociation dbEntity = null;
		if (id != null) {
			dbEntity = alleleVariantAssociationDAO.find(id);
			if (dbEntity == null) {
				addMessageResponse("Could not find Allele Variant Association with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new AlleleVariantAssociation();
		}

		dbEntity = (AlleleVariantAssociation) validateAlleleGenomicEntityAssociationFields(uiEntity, dbEntity);

		if (validateAllele) {
			Allele subject = validateSubject(uiEntity, dbEntity);
			dbEntity.setAlleleAssociationSubject(subject);
		}

		Variant object = validateObject(uiEntity, dbEntity);
		dbEntity.setAlleleVariantAssociationObject(object);

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation", VocabularyConstants.ALLELE_VARIANT_RELATION_VOCABULARY_TERM_SET, dbEntity.getRelation(), uiEntity.getRelation());
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

	private Allele validateSubject(AlleleVariantAssociation uiEntity, AlleleVariantAssociation dbEntity) {
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

	private Variant validateObject(AlleleVariantAssociation uiEntity, AlleleVariantAssociation dbEntity) {
		String field = "alleleVariantAssociationObject";
		if (ObjectUtils.isEmpty(uiEntity.getAlleleVariantAssociationObject())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		Variant objectEntity = null;
		if (uiEntity.getAlleleVariantAssociationObject().getId() != null) {
			objectEntity = variantDAO.find(uiEntity.getAlleleVariantAssociationObject().getId());
		}
		if (objectEntity == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (objectEntity.getObsolete() && (dbEntity.getAlleleVariantAssociationObject() == null || !objectEntity.getId().equals(dbEntity.getAlleleVariantAssociationObject().getId()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return objectEntity;

	}
}
