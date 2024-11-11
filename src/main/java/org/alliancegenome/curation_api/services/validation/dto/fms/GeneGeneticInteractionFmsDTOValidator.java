package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.enums.PsiMiTabPrefixEnum;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneGeneticInteraction;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PsiMiTabDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.services.GeneGeneticInteractionService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.interactions.InteractionAnnotationsHelper;
import org.alliancegenome.curation_api.services.helpers.interactions.InteractionStringHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneGeneticInteractionFmsDTOValidator extends GeneInteractionFmsDTOValidator {

	@Inject GeneGeneticInteractionService geneGeneticInteractionService;
	@Inject AlleleService alleleService;
	@Inject VocabularyTermService vocabularyTermService;
	@Inject InteractionAnnotationsHelper interactionAnnotationsHelper;

	private ObjectResponse<GeneGeneticInteraction> ggiResponse;

	public GeneGeneticInteraction validateGeneGeneticInteractionFmsDTO(PsiMiTabDTO dto) throws ValidationException {

		GeneGeneticInteraction interaction = null;
		ggiResponse = new ObjectResponse<GeneGeneticInteraction>();

		ObjectResponse<List<Reference>> refResponse = validateReferences(dto);
		ggiResponse.addErrorMessages(refResponse.getErrorMessages());

		String interactionId = null;
		if (CollectionUtils.isNotEmpty(dto.getInteractionIds())) {
			interactionId = PsiMiTabPrefixEnum.getAllianceIdentifier(dto.getInteractionIds().get(0));
		}

		Gene interactorA = null;
		if (StringUtils.isBlank(dto.getInteractorAIdentifier())) {
			ggiResponse.addErrorMessage("interactorAIdentifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<Gene> interactorAResponse = findAllianceGene(dto.getInteractorAIdentifier(), dto.getInteractorATaxonId());
			if (interactorAResponse.hasErrors()) {
				ggiResponse.addErrorMessage("interactorAIdentifier", interactorAResponse.errorMessagesString());
			}
			interactorA = interactorAResponse.getEntity();
		}

		Gene interactorB = null;
		if (StringUtils.isBlank(dto.getInteractorBIdentifier())) {
			ggiResponse.addErrorMessage("interactorBIdentifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<Gene> interactorBResponse = findAllianceGene(dto.getInteractorBIdentifier(), dto.getInteractorBTaxonId());
			if (interactorBResponse.hasErrors()) {
				ggiResponse.addErrorMessage("interactorBIdentifier", interactorBResponse.errorMessagesString());
			}
			interactorB = interactorBResponse.getEntity();
		}

		List<Reference> references = refResponse.getEntity();

		List<String> phenotypesOrTraits = interactionAnnotationsHelper.extractPhenotypeStatements(dto.getInteractionAnnotations());

		String uniqueId = InteractionStringHelper.getGeneGeneticInteractionUniqueId(dto, interactorA, interactorB, interactionId, references, phenotypesOrTraits);

		String searchValue = interactionId == null ? uniqueId : interactionId;
		ObjectResponse<GeneGeneticInteraction> interactionResponse = geneGeneticInteractionService.getByIdentifier(searchValue);
		if (interactionResponse != null) {
			interaction = interactionResponse.getEntity();
		}
		if (interaction == null) {
			interaction = new GeneGeneticInteraction();
		}

		interaction.setUniqueId(uniqueId);
		interaction.setGeneAssociationSubject(interactorA);
		interaction.setGeneGeneAssociationObject(interactorB);
		interaction.setInteractionId(interactionId);
		interaction.setPhenotypesOrTraits(handleStringListField(phenotypesOrTraits));

		ObjectResponse<GeneGeneticInteraction> giResponse = validateGeneInteraction(interaction, dto, references);
		ggiResponse.addErrorMessages(giResponse.getErrorMessages());
		interaction = giResponse.getEntity();

		interaction.setRelation(vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.GENE_GENETIC_INTERACTION_RELATION_VOCABULARY_TERM_SET, VocabularyConstants.GENE_GENETIC_INTERACTION_RELATION_TERM).getEntity());

		Allele interactorAGeneticPerturbation = validatePerturbation("interactorAAnnotationString", dto.getInteractorAAnnotationString());
		interaction.setInteractorAGeneticPerturbation(interactorAGeneticPerturbation);

		Allele interactorBGeneticPerturbation = validatePerturbation("interactorBAnnotationString", dto.getInteractorBAnnotationString());
		interaction.setInteractorBGeneticPerturbation(interactorBGeneticPerturbation);

		if (ggiResponse.hasErrors()) {
			throw new ObjectValidationException(dto, ggiResponse.getErrorMessages().values());
		}

		return interaction;

	}

	private Allele validatePerturbation(String fieldName, String dtoField) {
		Allele geneticPerturbation = null;
		String geneticPerturbationCurie = InteractionStringHelper.extractWBVarCurieFromAnnotations(dtoField);
		if (geneticPerturbationCurie == null) {
			return null;
		}

		geneticPerturbation = alleleService.findByIdentifierString(geneticPerturbationCurie);
		if (geneticPerturbation == null) {
			ggiResponse.addErrorMessage(fieldName, ValidationConstants.INVALID_MESSAGE + " (" + geneticPerturbationCurie + ")");
		}

		return geneticPerturbation;
	}
}
