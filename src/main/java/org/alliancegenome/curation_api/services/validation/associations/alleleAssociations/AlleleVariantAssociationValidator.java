package org.alliancegenome.curation_api.services.validation.associations.alleleAssociations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.VariantDAO;
import org.alliancegenome.curation_api.dao.associations.alleleAssociations.AlleleVariantAssociationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleVariantAssociation;
import org.alliancegenome.curation_api.response.ObjectResponse;

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
			Allele subject = validateRequiredEntity(alleleDAO, "alleleAssociationSubject", uiEntity.getAlleleAssociationSubject(), dbEntity.getAlleleAssociationSubject());
			dbEntity.setAlleleAssociationSubject(subject);
		}

		Variant object = validateRequiredEntity(variantDAO, "alleleVariantAssociationObject", uiEntity.getAlleleVariantAssociationObject(), dbEntity.getAlleleVariantAssociationObject());
		dbEntity.setAlleleVariantAssociationObject(object);

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation", VocabularyConstants.ALLELE_VARIANT_RELATION_VOCABULARY_TERM_SET, uiEntity.getRelation(), dbEntity.getRelation());
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
}
