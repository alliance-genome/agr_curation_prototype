package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.util.*;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AssemblyComponentDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.VariantDAO;
import org.alliancegenome.curation_api.dao.associations.alleleAssociations.AlleleVariantAssociationDAO;
import org.alliancegenome.curation_api.dao.associations.variantAssociations.CuratedVariantGenomicLocationAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.enums.ChromosomeAccessionEnum;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleVariantAssociation;
import org.alliancegenome.curation_api.model.entities.associations.variantAssociations.CuratedVariantGenomicLocationAssociation;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.ingest.dto.fms.CrossReferenceFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PublicationRefFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.VariantFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.*;
import org.alliancegenome.curation_api.services.associations.alleleAssociations.AlleleVariantAssociationService;
import org.alliancegenome.curation_api.services.associations.variantAssociations.CuratedVariantGenomicLocationAssociationService;
import org.alliancegenome.curation_api.services.helpers.notes.NoteIdentityHelper;
import org.alliancegenome.curation_api.services.helpers.variants.HgvsIdentifierHelper;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.services.ontology.SoTermService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class VariantFmsDTOValidator {

	@Inject VariantDAO variantDAO;
	@Inject NoteDAO noteDAO;
	@Inject AlleleService alleleService;
	@Inject AssemblyComponentDAO assemblyComponentDAO;
	@Inject CuratedVariantGenomicLocationAssociationDAO curatedVariantGenomicLocationAssociationDAO;
	@Inject CuratedVariantGenomicLocationAssociationService curatedVariantGenomicLocationAssociationService;
	@Inject AlleleVariantAssociationDAO alleleVariantAssociationDAO;
	@Inject AlleleVariantAssociationService alleleVariantAssociationService;
	@Inject SoTermService soTermService;
	@Inject DataProviderService dataProviderService;
	@Inject NcbiTaxonTermService ncbiTaxonTermService;
	@Inject VocabularyTermService vocabularyTermService;
	@Inject VocabularyTermSetService vocabularyTermSetService;
	@Inject CrossReferenceFmsDTOValidator crossReferenceFmsDtoValidator;
	@Inject CrossReferenceService crossReferenceService;
	@Inject VariantNoteFmsDTOValidator variantNoteFmsDtoValidator;
	@Inject ReferenceService referenceService;

	@Transactional
	public Long validateVariant(VariantFmsDTO dto, List<Long> idsAdded, BackendBulkDataProvider dataProvider) throws ValidationException {

		ObjectResponse<Variant> variantResponse = new ObjectResponse<Variant>();
		Variant variant = new Variant();

		if (dto.getStart() == null) {
			variantResponse.addErrorMessage("start", ValidationConstants.REQUIRED_MESSAGE);
		}

		if (dto.getEnd() == null) {
			variantResponse.addErrorMessage("end", ValidationConstants.REQUIRED_MESSAGE);
		}

		if (StringUtils.isBlank(dto.getSequenceOfReferenceAccessionNumber())) {
			variantResponse.addErrorMessage("sequenceOfReferenceAccessionNumber", ValidationConstants.REQUIRED_MESSAGE);
		}

		SOTerm variantType = null;
		if (StringUtils.isBlank(dto.getType())) {
			variantResponse.addErrorMessage("type", ValidationConstants.REQUIRED_MESSAGE);
		} else if (Objects.equals(dto.getType(), "SO:1000002") || Objects.equals(dto.getType(), "SO:1000008")
				|| Objects.equals(dto.getType(), "SO:0000667") || Objects.equals(dto.getType(), "SO:0000159")
				|| Objects.equals(dto.getType(), "SO:0002007") || Objects.equals(dto.getType(), "SO:1000032")) {
			variantType = soTermService.findByCurieOrSecondaryId(dto.getType());
			if (variantType == null) {
				variantResponse.addErrorMessage("type", ValidationConstants.INVALID_MESSAGE + " (" + dto.getType() + ")");
			} else {
				if (StringUtils.isBlank(dto.getGenomicReferenceSequence()) && !Objects.equals(dto.getType(), "SO:0000159")
						&& !Objects.equals(dto.getType(), "SO:1000032")) {
					variantResponse.addErrorMessage("genomicReferenceSequence", ValidationConstants.REQUIRED_MESSAGE + " for variant type " + dto.getType());
				}
				if (StringUtils.isBlank(dto.getGenomicVariantSequence()) && !Objects.equals(dto.getType(), "SO:0000667")
						&& !Objects.equals(dto.getType(), "SO:1000032")) {
					variantResponse.addErrorMessage("genomicVariantSequence", ValidationConstants.REQUIRED_MESSAGE + " for variant type " + dto.getType());
				}
			}
		} else {
			variantResponse.addErrorMessage("type", ValidationConstants.INVALID_MESSAGE + " for FMS submissions (" + dto.getType() + ")");
		}

		String hgvs = HgvsIdentifierHelper.getHgvsIdentifier(dto);
		String modInternalId = hgvs == null ? null : DigestUtils.md5Hex(hgvs);

		if (StringUtils.isNotBlank(hgvs) && !variantResponse.hasErrors()) {
			SearchResponse<Variant> searchResponse = variantDAO.findByField("modInternalId", modInternalId);
			if (searchResponse != null && searchResponse.getSingleResult() != null) {
				variant = searchResponse.getSingleResult();
			}
		}

		variant.setModInternalId(modInternalId);
		variant.setVariantType(variantType);
		variant.setDataProvider(dataProviderService.getDefaultDataProvider(dataProvider.name()));
		variant.setTaxon(ncbiTaxonTermService.getByCurie(dataProvider.canonicalTaxonCurie).getEntity());

		SOTerm consequence = null;
		if (StringUtils.isNotBlank(dto.getConsequence())) {
			consequence = soTermService.findByCurieOrSecondaryId(dto.getConsequence());
			if (consequence == null) {
				variantResponse.addErrorMessage("consequence", ValidationConstants.INVALID_MESSAGE + " (" + dto.getConsequence() + ")");
			}
		}
		variant.setSourceGeneralConsequence(consequence);

		if (CollectionUtils.isNotEmpty(dto.getSynonyms())) {
			List<String> existingSynonyms = variant.getSynonyms();
			if (CollectionUtils.isEmpty(existingSynonyms)) {
				existingSynonyms = new ArrayList<>();
			}
			// remove synonyms that are no longer in the submitted file
			List<String> toBeRemoved = existingSynonyms.stream()
				.filter(synonym -> !dto.getSynonyms().contains(synonym))
				.toList();
			existingSynonyms.removeIf(toBeRemoved::contains);
			// add missing synonyms
			final List<String> synonymStrings = existingSynonyms;
			List<String> toBeAdded = dto.getSynonyms().stream()
				.filter(synonym -> !synonymStrings.contains(synonym))
				.toList();
			existingSynonyms.addAll(toBeAdded);
			variant.setSynonyms(existingSynonyms);
		} else {
			variant.setSynonyms(null);
		}

		List<CrossReference> validatedXrefs = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dto.getCrossReferences())) {
			for (CrossReferenceFmsDTO xrefDto : dto.getCrossReferences()) {
				ObjectResponse<List<CrossReference>> xrefResponse = crossReferenceFmsDtoValidator.validateCrossReferenceFmsDTO(xrefDto);
				if (xrefResponse.hasErrors()) {
					variantResponse.addErrorMessage("cross_references", xrefResponse.errorMessagesString());
					break;
				} else {
					validatedXrefs.addAll(xrefResponse.getEntity());
				}
			}
		}
		List<CrossReference> xrefs = crossReferenceService.getUpdatedXrefList(validatedXrefs, variant.getCrossReferences());
		if (variant.getCrossReferences() != null) {
			variant.getCrossReferences().clear();
		}
		if (xrefs != null) {
			if (variant.getCrossReferences() == null) {
				variant.setCrossReferences(new ArrayList<>());
			}
			variant.getCrossReferences().addAll(xrefs);
		}

		if (CollectionUtils.isNotEmpty(dto.getReferences())) {
			Set<Reference> validatedReferences = new HashSet<>();
			for (PublicationRefFmsDTO refDto : dto.getReferences()) {
				if (ObjectUtils.isNotEmpty(refDto.getPublicationId())) {
					Reference reference = referenceService.retrieveFromDbOrLiteratureService(refDto.getPublicationId());
					if (reference == null) {
						variantResponse.addErrorMessage("references - publicationId", ValidationConstants.INVALID_MESSAGE + " (" + refDto.getPublicationId() + ")");
					} else {
						validatedReferences.add(reference);
					}
				}
			}
			if (CollectionUtils.isNotEmpty(validatedReferences)) {
				if (variant.getReferences() == null) {
					variant.setReferences(new ArrayList<>());
				} else {
					variant.getReferences().clear();
				}
				variant.getReferences().addAll(validatedReferences);
			}
		} else {
			variant.setReferences(null);
		}

		if (variant.getRelatedNotes() != null) {
			variant.getRelatedNotes().clear();
		}

		List<Note> validatedNotes = new ArrayList<Note>();
		List<String> noteIdentities = new ArrayList<String>();
		Boolean allNotesValid = true;
		if (CollectionUtils.isNotEmpty(dto.getNotes())) {
			for (int ix = 0; ix < dto.getNotes().size(); ix++) {
				ObjectResponse<Note> noteResponse = variantNoteFmsDtoValidator.validateVariantNoteFmsDTO(dto.getNotes().get(ix));
				if (noteResponse.hasErrors()) {
					allNotesValid = false;
					variantResponse.addErrorMessages("notes", ix, noteResponse.getErrorMessages());
				} else {
					String noteIdentity = NoteIdentityHelper.variantNoteFmsDtoIdentity(dto.getNotes().get(ix));
					if (!noteIdentities.contains(noteIdentity)) {
						noteIdentities.add(noteIdentity);
						validatedNotes.add(noteDAO.persist(noteResponse.getEntity()));
					}
				}
			}
		}
		if (!allNotesValid) {
			variantResponse.convertMapToErrorMessages("notes");
		}
		if (CollectionUtils.isNotEmpty(validatedNotes) && allNotesValid) {
			if (variant.getRelatedNotes() == null) {
				variant.setRelatedNotes(new ArrayList<>());
			}
			variant.getRelatedNotes().addAll(validatedNotes);
		}

		if (variantResponse.hasErrors()) {
			variantResponse.convertErrorMessagesToMap();
			throw new ObjectValidationException(dto, variantResponse.errorMessagesString());
		}

		variant = variantDAO.persist(variant);
		if (variant != null) {
			idsAdded.add(variant.getId());
		}

		return variant.getId();
	}

	@Transactional
	public void validateCuratedVariantGenomicLocationAssociation(VariantFmsDTO dto, List<Long> idsAdded, Long variantId) throws ValidationException {

		CuratedVariantGenomicLocationAssociation association = new CuratedVariantGenomicLocationAssociation();
		ObjectResponse<CuratedVariantGenomicLocationAssociation> cvglaResponse = new ObjectResponse<CuratedVariantGenomicLocationAssociation>();
		AssemblyComponent chromosome = null;

		if (dto.getStart() == null) {
			cvglaResponse.addErrorMessage("start", ValidationConstants.REQUIRED_MESSAGE);
		}

		if (dto.getEnd() == null) {
			cvglaResponse.addErrorMessage("end", ValidationConstants.REQUIRED_MESSAGE);
		}

		if (StringUtils.isBlank(dto.getSequenceOfReferenceAccessionNumber())) {
			cvglaResponse.addErrorMessage("sequenceOfReferenceAccessionNumber", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ChromosomeAccessionEnum cae = ChromosomeAccessionEnum.getChromosomeAccessionEnum(dto.getSequenceOfReferenceAccessionNumber());
			if (cae != null) {
				Map<String, Object> params = new HashMap<>();
				params.put("name", cae.chromosomeName);
				params.put("genomeAssembly.modEntityId", cae.assemblyIdentifier);
				SearchResponse<AssemblyComponent> acResponse = assemblyComponentDAO.findByParams(params);
				if (acResponse != null) {
					chromosome = acResponse.getSingleResult();
				}
			}
			if (chromosome == null) {
				cvglaResponse.addErrorMessage("sequenceOfReferenceAccessionNumber", ValidationConstants.INVALID_MESSAGE + " ("
						+ dto.getSequenceOfReferenceAccessionNumber() + ")");
			}
		}

		String hgvs = HgvsIdentifierHelper.getHgvsIdentifier(dto);

		Variant variant = null;
		if (variantId != null) {
			variant = variantDAO.find(variantId);
		}
		if (variant != null && StringUtils.isNotBlank(hgvs) && !cvglaResponse.hasErrors() && CollectionUtils.isNotEmpty(variant.getCuratedVariantGenomicLocations())) {
			for (CuratedVariantGenomicLocationAssociation existingLocationAssociation : variant.getCuratedVariantGenomicLocations()) {
				if (Objects.equals(hgvs, existingLocationAssociation.getHgvs())) {
					association = curatedVariantGenomicLocationAssociationDAO.find(existingLocationAssociation.getId());
					break;
				}
			}
		}

		association.setHgvs(hgvs);
		association.setVariantAssociationSubject(variant);
		association.setVariantGenomicLocationAssociationObject(chromosome);
		association.setStart(dto.getStart());
		association.setEnd(dto.getEnd());
		association.setRelation(vocabularyTermService.getTermInVocabulary(VocabularyConstants.LOCATION_ASSOCIATION_RELATION_VOCABULARY, "located_on").getEntity());

		if (StringUtils.isNotBlank(dto.getGenomicReferenceSequence()) && !Objects.equals(dto.getGenomicReferenceSequence(), "N/A")) {
			association.setReferenceSequence(StringUtils.deleteWhitespace(dto.getGenomicReferenceSequence()));
		} else {
			association.setReferenceSequence(null);
		}

		if (StringUtils.isNotBlank(dto.getGenomicVariantSequence()) && !Objects.equals(dto.getGenomicVariantSequence(), "N/A")) {
			association.setVariantSequence(StringUtils.deleteWhitespace(dto.getGenomicVariantSequence()));
		} else {
			association.setVariantSequence(null);
		}

		if (variantId == null) {
			cvglaResponse.addErrorMessage("variant", ValidationConstants.INVALID_MESSAGE);
		}

		if (cvglaResponse.hasErrors()) {
			throw new ObjectValidationException(dto, cvglaResponse.errorMessagesString());
		}

		association = curatedVariantGenomicLocationAssociationDAO.persist(association);
		if (association != null) {
			idsAdded.add(association.getId());
			curatedVariantGenomicLocationAssociationService.addAssociationToSubject(association);
		}

	}

	@Transactional
	public void validateAlleleVariantAssociation(VariantFmsDTO dto, List<Long> idsAdded, Long variantId) throws ValidationException {

		AlleleVariantAssociation association = new AlleleVariantAssociation();
		ObjectResponse<AlleleVariantAssociation> avaResponse = new ObjectResponse<AlleleVariantAssociation>();

		Variant variant = null;
		if (variantId == null) {
			avaResponse.addErrorMessage("variant", ValidationConstants.INVALID_MESSAGE);
		} else {
			variant = variantDAO.find(variantId);
		}
		if (StringUtils.isBlank(dto.getAlleleId())) {
			avaResponse.addErrorMessage("alleleId", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			if (association.getId() == null) {
				Allele allele = alleleService.findByIdentifierString(dto.getAlleleId());
				if (allele == null) {
					avaResponse.addErrorMessage("alleleId", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAlleleId() + ")");
				} else {
					if (CollectionUtils.isNotEmpty(allele.getAlleleVariantAssociations())) {
						for (AlleleVariantAssociation existingAssociation : allele.getAlleleVariantAssociations()) {
							if (Objects.equals(dto.getAlleleId(), existingAssociation.getAlleleAssociationSubject().getModEntityId())) {
								association = alleleVariantAssociationDAO.find(existingAssociation.getId());
								break;
							}
						}
					}
					association.setAlleleAssociationSubject(allele);
				}
			}
		}

		association.setAlleleVariantAssociationObject(variant);
		association.setRelation(vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.ALLELE_VARIANT_RELATION_VOCABULARY_TERM_SET, "has_variant").getEntity());

		if (variant == null) {
			avaResponse.addErrorMessage("variant", ValidationConstants.INVALID_MESSAGE);
		}

		if (avaResponse.hasErrors()) {
			throw new ObjectValidationException(dto, avaResponse.errorMessagesString());
		}

		association = alleleVariantAssociationDAO.persist(association);
		if (association != null) {
			idsAdded.add(association.getId());
			alleleVariantAssociationService.addAssociationToAllele(association);
			alleleVariantAssociationService.addAssociationToVariant(association);
		}
	}
}
