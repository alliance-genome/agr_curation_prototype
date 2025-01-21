package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.enums.PsiMiTabPrefixEnum;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneInteraction;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.ontology.MITerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PsiMiTabDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.helpers.interactions.InteractionCrossReferenceHelper;
import org.alliancegenome.curation_api.services.helpers.interactions.InteractionStringHelper;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneInteractionFmsDTOValidator extends BaseDTOValidator {

	@Inject ReferenceService referenceService;
	@Inject GeneService geneService;
	@Inject CrossReferenceDAO crossReferenceDAO;
	@Inject InteractionCrossReferenceHelper interactionXrefHelper;
	@Inject NcbiTaxonTermService ncbiTaxonTermService;

	private HashMap<String, Gene> geneCache = new HashMap<String, Gene>();

	public <E extends GeneInteraction> ObjectResponse<E> validateGeneInteraction(E interaction, PsiMiTabDTO dto, List<Reference> references) {

		ObjectResponse<E> giResponse = new ObjectResponse<E>();

		if (CollectionUtils.isNotEmpty(references)) {
			List<InformationContentEntity> evidence = new ArrayList<>();
			evidence.addAll(references);
			interaction.setEvidence(evidence);
		} else {
			interaction.setEvidence(null);
		}

		// Taxon strings are needed for xref check but don't need to be stored
		if (StringUtils.isBlank(dto.getInteractorATaxonId())) {
			giResponse.addErrorMessage("interactorATaxonId", ValidationConstants.REQUIRED_MESSAGE);
		}
		if (StringUtils.isBlank(dto.getInteractorBTaxonId())) {
			giResponse.addErrorMessage("interactorBTaxonId", ValidationConstants.REQUIRED_MESSAGE);
		}

		MITerm interactorARole = null;
		if (StringUtils.isNotBlank(dto.getExperimentalRoleA())) {
			interactorARole = getTermFromCache(getCurieFromCache(dto.getExperimentalRoleA()));
			if (interactorARole == null) {
				giResponse.addErrorMessage("experimentalRoleA", ValidationConstants.INVALID_MESSAGE + " (" + dto.getExperimentalRoleA() + ")");
			}
		}
		interaction.setInteractorARole(interactorARole);

		MITerm interactorBRole = null;
		if (StringUtils.isNotBlank(dto.getExperimentalRoleB())) {
			interactorBRole = getTermFromCache(getCurieFromCache(dto.getExperimentalRoleB()));
			if (interactorBRole == null) {
				giResponse.addErrorMessage("experimentalRoleB", ValidationConstants.INVALID_MESSAGE + " (" + dto.getExperimentalRoleB() + ")");
			}
		}
		interaction.setInteractorBRole(interactorBRole);

		MITerm interactorAType = null;
		if (StringUtils.isNotBlank(dto.getInteractorAType())) {
			interactorAType = getTermFromCache(getCurieFromCache(dto.getInteractorAType()));
			if (interactorAType == null) {
				giResponse.addErrorMessage("interactorAType", ValidationConstants.INVALID_MESSAGE + " (" + dto.getInteractorAType() + ")");
			}
		} else {
			giResponse.addErrorMessage("interactorAType", ValidationConstants.REQUIRED_MESSAGE);
		}
		interaction.setInteractorAType(interactorAType);

		MITerm interactorBType = null;
		if (StringUtils.isNotBlank(dto.getInteractorBType())) {
			interactorBType = getTermFromCache(getCurieFromCache(dto.getInteractorBType()));
			if (interactorBType == null) {
				giResponse.addErrorMessage("interactorBType", ValidationConstants.INVALID_MESSAGE + " (" + dto.getInteractorBType() + ")");
			}
		} else {
			giResponse.addErrorMessage("interactorBType", ValidationConstants.REQUIRED_MESSAGE);
		}
		interaction.setInteractorBType(interactorBType);

		MITerm interactionType = null;
		if (CollectionUtils.isNotEmpty(dto.getInteractionTypes())) {
			for (String interactionTypeString : dto.getInteractionTypes()) {
				String interactionTypeCurie = getCurieFromCache(interactionTypeString);
				if (interactionTypeCurie != null) {
					interactionType = getTermFromCache(interactionTypeCurie);
					if (interactionType == null) {
						giResponse.addErrorMessage("interactionTypes", ValidationConstants.INVALID_MESSAGE + " (" + interactionTypeString + ")");
					}
					break;
				}
			}
		}
		if (interactionType == null) {
			giResponse.addErrorMessage("interactionType", ValidationConstants.REQUIRED_MESSAGE);
		}
		interaction.setInteractionType(interactionType);

		MITerm interactionSource = null;
		if (CollectionUtils.isNotEmpty(dto.getSourceDatabaseIds())) {
			for (String interactionSourceString : dto.getSourceDatabaseIds()) {
				String interactionSourceCurie = getCurieFromCache(interactionSourceString);
				if (interactionSourceCurie != null) {
					interactionSource = getTermFromCache(interactionSourceCurie);
					if (interactionSource == null) {
						giResponse.addErrorMessage("sourceDatabaseIds", ValidationConstants.INVALID_MESSAGE + " (" + interactionSourceCurie + ")");
					}
					break;
				}
			}
		}
		if (interactionSource == null) {
			giResponse.addErrorMessage("sourceDatabaseIds", ValidationConstants.REQUIRED_MESSAGE);
		}
		interaction.setInteractionSource(interactionSource);

		List<CrossReference> xrefs = updateInteractionXrefs(interaction.getCrossReferences(), dto);
		if (interaction.getCrossReferences() != null) {
			interaction.getCrossReferences().clear();
		}
		if (CollectionUtils.isNotEmpty(xrefs)) {
			if (interaction.getCrossReferences() == null) {
				interaction.setCrossReferences(new ArrayList<>());
			}
			interaction.getCrossReferences().addAll(xrefs);
		}

		OffsetDateTime dateCreated = processPsiMiTabDateFormat(dto.getCreationDate());
		OffsetDateTime dateUpdated = processPsiMiTabDateFormat(dto.getUpdateDate());
		interaction.setDateCreated(dateCreated);
		interaction.setDateUpdated(dateUpdated);

		interaction.setObsolete(false);
		interaction.setInternal(false);

		giResponse.setEntity(interaction);

		return giResponse;

	}

	protected ObjectResponse<Gene> findAllianceGene(String psiMiTabIdentifier, String psiMiTabTaxonCurie) {
		ObjectResponse<Gene> response = new ObjectResponse<>();
		String[] psiMiTabIdParts = psiMiTabIdentifier.split(":");
		if (psiMiTabIdParts.length != 2) {
			response.addErrorMessage("curie", ValidationConstants.INVALID_MESSAGE + " (expecting <prefix:suffix>, got " + psiMiTabIdentifier + ")");
			return response;
		}

		PsiMiTabPrefixEnum prefix = PsiMiTabPrefixEnum.findByPsiMiTabPrefix(psiMiTabIdParts[0]);
		if (prefix == null) {
			response.addErrorMessage("curie", ValidationConstants.INVALID_MESSAGE + " (cannot convert prefix " + psiMiTabIdParts[0] + " to Alliance format)");
			return response;
		}

		Gene allianceGene = null;
		String convertedCurie = prefix.alliancePrefix + ":" + psiMiTabIdParts[1];
		if (prefix.isModPrefix) {
			allianceGene = getGeneFromCache(convertedCurie);
		} else {
			String taxonCurie = InteractionStringHelper.getAllianceTaxonCurie(psiMiTabTaxonCurie);
			if (taxonCurie == null) {
				response.addErrorMessage("taxon", ValidationConstants.INVALID_MESSAGE + " (expecting string starting taxid:<suffix>, got " + psiMiTabTaxonCurie + ")");
				return response;
			}
			NCBITaxonTerm taxon = ncbiTaxonTermService.getByCurie(taxonCurie).getEntity();
			if (taxon == null) {
				response.addErrorMessage("taxon", ValidationConstants.INVALID_MESSAGE + " (" + taxonCurie + " not found)");
				return response;
			}

			SearchResponse<Gene> searchResponse = geneService.findByField("crossReferences.referencedCurie", convertedCurie);
			if (searchResponse != null) {
				// Need to check that returned gene belongs to MOD corresponding to taxon
				for (Gene searchResult : searchResponse.getResults()) {
					String resultDataProviderCoreGenus = BackendBulkDataProvider.getCoreGenus(searchResult.getDataProvider().getAbbreviation());
					if (taxon.getName().startsWith(resultDataProviderCoreGenus + " ")) {
						allianceGene = searchResult;
						break;
					}
					if (StringUtils.equals(taxonCurie, "NCBITaxon:9606") && StringUtils.equals(searchResult.getDataProvider().getAbbreviation(), "RGD")) {
						allianceGene = searchResult;
						break;
					}
				}
			}
		}
		if (allianceGene == null) {
			response.addErrorMessage("curie", ValidationConstants.INVALID_MESSAGE + " (" + convertedCurie + " not found)");
		}
		response.setEntity(allianceGene);

		return response;
	}

	private Gene getGeneFromCache(String curie) {
		if (geneCache.containsKey(curie)) {
			return geneCache.get(curie);
		} else {
			Gene gene = geneService.findByIdentifierString(curie);
			if (gene != null) {
				geneCache.put(curie, gene);
			}
			return gene;
		}
	}

	protected ObjectResponse<List<Reference>> validateReferences(PsiMiTabDTO dto) {
		ObjectResponse<List<Reference>> refResponse = new ObjectResponse<>();
		List<Reference> validatedReferences = new ArrayList<>();
		List<Long> validatedReferenceIds = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dto.getPublicationIds())) {
			for (String publicationId : dto.getPublicationIds()) {
				Reference reference = null;
				String alliancePubXrefCurie = PsiMiTabPrefixEnum.getAllianceIdentifier(publicationId);
				if (alliancePubXrefCurie != null) {
					reference = referenceService.retrieveFromDbOrLiteratureService(alliancePubXrefCurie);
				}
				if (reference == null) {
					refResponse.addErrorMessage("publicationIds", ValidationConstants.INVALID_MESSAGE + " (" + publicationId + ")");
					return refResponse;
				}
				if (!validatedReferenceIds.contains(reference.getId())) {
					validatedReferences.add(reference);
					validatedReferenceIds.add(reference.getId());
				}
			}
		}
		if (CollectionUtils.isNotEmpty(validatedReferences)) {
			refResponse.setEntity(validatedReferences);
		}

		return refResponse;
	}

	private List<CrossReference> updateInteractionXrefs(List<CrossReference> existingXrefs, PsiMiTabDTO dto) {
		List<CrossReference> newXrefs = interactionXrefHelper.createAllianceXrefs(dto);
		if (CollectionUtils.isEmpty(newXrefs)) {
			return null;
		}
		if (CollectionUtils.isEmpty(existingXrefs)) {
			return newXrefs;
		}

		Map<String, CrossReference> existingXrefMap = existingXrefs.stream().collect(Collectors.toMap(CrossReference::getReferencedCurie, Function.identity()));
		List<CrossReference> updatedXrefs = new ArrayList<>();
		for (CrossReference newXref : newXrefs) {
			if (existingXrefMap.containsKey(newXref.getReferencedCurie())) {
				updatedXrefs.add(existingXrefMap.get(newXref.getReferencedCurie()));
			} else {
				updatedXrefs.add(crossReferenceDAO.persist(newXref));
			}
		}

		return updatedXrefs;
	}

	private OffsetDateTime processPsiMiTabDateFormat(String dateString) {
		if (StringUtils.isBlank(dateString)) {
			return null;
		}
		DateTimeFormatter dtf = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
		dateString = dateString.replace("/", "-");
		return OffsetDateTime.parse(dateString + "T00:00:00+00:00", dtf);
	}

}
