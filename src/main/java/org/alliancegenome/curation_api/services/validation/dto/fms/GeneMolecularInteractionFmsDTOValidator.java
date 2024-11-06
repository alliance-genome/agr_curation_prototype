package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.enums.PsiMiTabPrefixEnum;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneMolecularInteraction;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.ontology.MITerm;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PsiMiTabDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.GeneMolecularInteractionService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.interactions.InteractionStringHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneMolecularInteractionFmsDTOValidator extends GeneInteractionFmsDTOValidator {

	@Inject GeneMolecularInteractionService geneMolecularInteractionService;
	@Inject VocabularyTermService vocabularyTermService;

	private ObjectResponse<GeneMolecularInteraction> gmiResponse;

	public GeneMolecularInteraction validateGeneMolecularInteractionFmsDTO(PsiMiTabDTO dto) throws ValidationException {

		GeneMolecularInteraction interaction = null;
		gmiResponse = new ObjectResponse<GeneMolecularInteraction>();

		ObjectResponse<List<Reference>> refResponse = validateReferences(dto);
		gmiResponse.addErrorMessages(refResponse.getErrorMessages());

		String interactionId = null;
		if (CollectionUtils.isNotEmpty(dto.getInteractionIds())) {
			interactionId = PsiMiTabPrefixEnum.getAllianceIdentifier(dto.getInteractionIds().get(0));
		}

		Gene interactorA = null;
		if (StringUtils.isBlank(dto.getInteractorAIdentifier())) {
			gmiResponse.addErrorMessage("interactorAIdentifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<Gene> interactorAResponse = findAllianceGene(dto.getInteractorAIdentifier(), dto.getInteractorATaxonId());
			if (interactorAResponse.hasErrors()) {
				gmiResponse.addErrorMessage("interactorAIdentifier", interactorAResponse.errorMessagesString());
			}
			interactorA = interactorAResponse.getEntity();
		}

		Gene interactorB = null;
		if (StringUtils.isBlank(dto.getInteractorBIdentifier())) {
			gmiResponse.addErrorMessage("interactorBIdentifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<Gene> interactorBResponse = findAllianceGene(dto.getInteractorBIdentifier(), dto.getInteractorBTaxonId());
			if (interactorBResponse.hasErrors()) {
				gmiResponse.addErrorMessage("interactorBIdentifier", interactorBResponse.errorMessagesString());
			}
			interactorB = interactorBResponse.getEntity();
		}

		List<Reference> references = refResponse.getEntity();

		String uniqueId = InteractionStringHelper.getGeneMolecularInteractionUniqueId(dto, interactorA, interactorB, interactionId, references);

		String searchValue = interactionId == null ? uniqueId : interactionId;
		ObjectResponse<GeneMolecularInteraction> interactionResponse = geneMolecularInteractionService.getByIdentifier(searchValue);
		if (interactionResponse != null) {
			interaction = interactionResponse.getEntity();
		}
		if (interaction == null) {
			interaction = new GeneMolecularInteraction();
		}

		interaction.setUniqueId(uniqueId);
		interaction.setGeneAssociationSubject(interactorA);
		interaction.setGeneGeneAssociationObject(interactorB);
		interaction.setInteractionId(interactionId);

		ObjectResponse<GeneMolecularInteraction> giResponse = validateGeneInteraction(interaction, dto, references);
		gmiResponse.addErrorMessages(giResponse.getErrorMessages());
		interaction = giResponse.getEntity();

		interaction.setRelation(vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.GENE_MOLECULAR_INTERACTION_RELATION_VOCABULARY_TERM_SET, VocabularyConstants.GENE_MOLECULAR_INTERACTION_RELATION_TERM).getEntity());

		MITerm detectionMethod = null;
		if (CollectionUtils.isNotEmpty(dto.getInteractionDetectionMethods())) {
			for (String detectionMethodString : dto.getInteractionDetectionMethods()) {
				String detectionMethodCurie = getCurieFromCache(detectionMethodString);
				if (detectionMethodCurie != null) {
					detectionMethod = getTermFromCache(detectionMethodCurie);
					if (detectionMethod == null) {
						gmiResponse.addErrorMessage("interactionDetectionMethods", ValidationConstants.INVALID_MESSAGE + " (" + detectionMethodCurie + ")");
					}
					break;
				}
			}
		}
		interaction.setDetectionMethod(detectionMethod);

		MITerm aggregationDatabase = null;
		String aggregationDatabaseCurie = InteractionStringHelper.getAggregationDatabaseMITermCurie(dto);
		if (aggregationDatabaseCurie != null) {
			aggregationDatabase = getTermFromCache(aggregationDatabaseCurie);
			if (aggregationDatabase == null) {
				gmiResponse.addErrorMessage("aggregationDatabase (inferred from sourceDatabaseIds)", ValidationConstants.INVALID_MESSAGE + " (" + aggregationDatabaseCurie + ")");
			}
		}
		interaction.setAggregationDatabase(aggregationDatabase);

		if (gmiResponse.hasErrors()) {
			throw new ObjectValidationException(dto, gmiResponse.errorMessagesList());
		}

		return interaction;

	}
}
