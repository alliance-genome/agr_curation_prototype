package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ontology.SoTermService;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.geneSlotAnnotations.GeneSecondaryIdSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotationValidator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneValidator extends GenomicEntityValidator<Gene> {

	@Inject GeneDAO geneDAO;
	@Inject SoTermService soTermService;
	@Inject GeneSymbolSlotAnnotationValidator geneSymbolValidator;
	@Inject GeneFullNameSlotAnnotationValidator geneFullNameValidator;
	@Inject GeneSystematicNameSlotAnnotationValidator geneSystematicNameValidator;
	@Inject GeneSynonymSlotAnnotationValidator geneSynonymValidator;
	@Inject GeneSecondaryIdSlotAnnotationValidator geneSecondaryIdValidator;
	@Inject CrossReferenceDAO crossReferenceDAO;

	private String errorMessage;

	public Gene validateGeneUpdate(Gene uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update Gene: [" + uiEntity.getIdentifier() + "]";

		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No Gene ID provided");
			throw new ApiErrorException(response);
		}

		Gene dbEntity = geneDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("id", ValidationConstants.INVALID_MESSAGE);
			throw new ApiErrorException(response);
		}

		dbEntity = (Gene) validateAuditedObjectFields(uiEntity, dbEntity, false);

		return validateGene(uiEntity, dbEntity);
	}

	public Gene validateGeneCreate(Gene uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not create Gene";

		Gene dbEntity = new Gene();

		dbEntity = (Gene) validateAuditedObjectFields(uiEntity, dbEntity, true);

		return validateGene(uiEntity, dbEntity);
	}

	private Gene validateGene(Gene uiEntity, Gene dbEntity) {

		dbEntity = validateGenomicEntityFields(uiEntity, dbEntity);

		SOTerm geneType = validateGeneType(uiEntity, dbEntity);
		dbEntity.setGeneType(geneType);

		GeneSymbolSlotAnnotation symbol = validateGeneSymbol(uiEntity, dbEntity);
		GeneFullNameSlotAnnotation fullName = validateGeneFullName(uiEntity, dbEntity);
		GeneSystematicNameSlotAnnotation systematicName = validateGeneSystematicName(uiEntity, dbEntity);
		List<GeneSynonymSlotAnnotation> synonyms = validateGeneSynonyms(uiEntity, dbEntity);
		List<GeneSecondaryIdSlotAnnotation> secondaryIds = validateGeneSecondaryIds(uiEntity, dbEntity);

		response.convertErrorMessagesToMap();

		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		dbEntity = geneDAO.persist(dbEntity);

		if (symbol != null) {
			symbol.setSingleGene(dbEntity);
		}
		dbEntity.setGeneSymbol(symbol);

		if (fullName != null) {
			fullName.setSingleGene(dbEntity);
		}
		dbEntity.setGeneFullName(fullName);

		if (systematicName != null) {
			systematicName.setSingleGene(dbEntity);
		}
		dbEntity.setGeneSystematicName(systematicName);

		if (dbEntity.getGeneSynonyms() != null) {
			dbEntity.getGeneSynonyms().clear();
		}
		if (synonyms != null) {
			if (dbEntity.getGeneSynonyms() == null) {
				dbEntity.setGeneSynonyms(new ArrayList<>());
			}
			dbEntity.getGeneSynonyms().addAll(synonyms);
		}

		if (dbEntity.getGeneSecondaryIds() != null) {
			dbEntity.getGeneSecondaryIds().clear();
		}
		if (secondaryIds != null) {
			if (dbEntity.getGeneSecondaryIds() == null) {
				dbEntity.setGeneSecondaryIds(new ArrayList<>());
			}
			dbEntity.getGeneSecondaryIds().addAll(secondaryIds);
		}

		return dbEntity;
	}

	private SOTerm validateGeneType(Gene uiEntity, Gene dbEntity) {
		if (ObjectUtils.isEmpty(uiEntity.getGeneType())) {
			addMessageResponse("geneType", ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		SOTerm soTerm = null;
		if (StringUtils.isNotBlank(uiEntity.getGeneType().getCurie())) {
			soTerm = soTermService.findByCurie(uiEntity.getGeneType().getCurie());
			if (soTerm == null) {
				addMessageResponse("geneType", ValidationConstants.INVALID_MESSAGE);
				return null;
			} else if (soTerm.getObsolete() && (dbEntity.getGeneType() == null || !soTerm.getId().equals(dbEntity.getGeneType().getId()))) {
				addMessageResponse("geneType", ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}
		}
		return soTerm;
	}

	private GeneSymbolSlotAnnotation validateGeneSymbol(Gene uiEntity, Gene dbEntity) {
		String field = "geneSymbol";

		if (uiEntity.getGeneSymbol() == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		ObjectResponse<GeneSymbolSlotAnnotation> symbolResponse = geneSymbolValidator.validateGeneSymbolSlotAnnotation(uiEntity.getGeneSymbol());
		if (symbolResponse.getEntity() == null) {
			addMessageResponse(field, symbolResponse.errorMessagesString());
			response.addErrorMessages(field, symbolResponse.getErrorMessages());
			return null;
		}

		return symbolResponse.getEntity();
	}

	private GeneFullNameSlotAnnotation validateGeneFullName(Gene uiEntity, Gene dbEntity) {
		if (uiEntity.getGeneFullName() == null) {
			return null;
		}

		String field = "geneFullName";

		ObjectResponse<GeneFullNameSlotAnnotation> nameResponse = geneFullNameValidator.validateGeneFullNameSlotAnnotation(uiEntity.getGeneFullName());
		if (nameResponse.getEntity() == null) {
			addMessageResponse(field, nameResponse.errorMessagesString());
			response.addErrorMessages(field, nameResponse.getErrorMessages());
			return null;
		}

		return nameResponse.getEntity();
	}

	private GeneSystematicNameSlotAnnotation validateGeneSystematicName(Gene uiEntity, Gene dbEntity) {
		if (uiEntity.getGeneSystematicName() == null) {
			return null;
		}

		String field = "geneSystematicName";

		ObjectResponse<GeneSystematicNameSlotAnnotation> nameResponse = geneSystematicNameValidator.validateGeneSystematicNameSlotAnnotation(uiEntity.getGeneSystematicName());
		if (nameResponse.getEntity() == null) {
			addMessageResponse(field, nameResponse.errorMessagesString());
			response.addErrorMessages(field, nameResponse.getErrorMessages());
			return null;
		}

		return nameResponse.getEntity();
	}

	private List<GeneSynonymSlotAnnotation> validateGeneSynonyms(Gene uiEntity, Gene dbEntity) {
		String field = "geneSynonyms";

		List<GeneSynonymSlotAnnotation> validatedSynonyms = new ArrayList<GeneSynonymSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getGeneSynonyms())) {
			for (int ix = 0; ix < uiEntity.getGeneSynonyms().size(); ix++) {
				GeneSynonymSlotAnnotation syn = uiEntity.getGeneSynonyms().get(ix);
				ObjectResponse<GeneSynonymSlotAnnotation> synResponse = geneSynonymValidator.validateGeneSynonymSlotAnnotation(syn);
				if (synResponse.getEntity() == null) {
					response.addErrorMessages(field, ix, synResponse.getErrorMessages());
					allValid = false;
				} else {
					syn = synResponse.getEntity();
					syn.setSingleGene(dbEntity);
					validatedSynonyms.add(syn);
				}
			}
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedSynonyms)) {
			return null;
		}

		return validatedSynonyms;
	}

	private List<GeneSecondaryIdSlotAnnotation> validateGeneSecondaryIds(Gene uiEntity, Gene dbEntity) {
		String field = "geneSecondaryIds";

		List<GeneSecondaryIdSlotAnnotation> validatedSecondaryIds = new ArrayList<GeneSecondaryIdSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getGeneSecondaryIds())) {
			for (int ix = 0; ix < uiEntity.getGeneSecondaryIds().size(); ix++) {
				GeneSecondaryIdSlotAnnotation sid = uiEntity.getGeneSecondaryIds().get(ix);
				ObjectResponse<GeneSecondaryIdSlotAnnotation> sidResponse = geneSecondaryIdValidator.validateGeneSecondaryIdSlotAnnotation(sid);
				if (sidResponse.getEntity() == null) {
					response.addErrorMessages(field, ix, sidResponse.getErrorMessages());
					allValid = false;
				} else {
					sid = sidResponse.getEntity();
					sid.setSingleGene(dbEntity);
					validatedSecondaryIds.add(sid);
				}
			}
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedSecondaryIds)) {
			return null;
		}

		return validatedSecondaryIds;
	}

}
