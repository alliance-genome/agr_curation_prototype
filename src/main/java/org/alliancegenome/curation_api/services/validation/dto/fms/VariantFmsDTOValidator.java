package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
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
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.AssemblyComponent;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleVariantAssociation;
import org.alliancegenome.curation_api.model.entities.associations.variantAssociations.CuratedVariantGenomicLocationAssociation;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.ingest.dto.fms.CrossReferenceFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.VariantFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.services.CrossReferenceService;
import org.alliancegenome.curation_api.services.DataProviderService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.VocabularyTermSetService;
import org.alliancegenome.curation_api.services.associations.alleleAssociations.AlleleVariantAssociationService;
import org.alliancegenome.curation_api.services.associations.variantAssociations.CuratedVariantGenomicLocationAssociationService;
import org.alliancegenome.curation_api.services.helpers.notes.NoteIdentityHelper;
import org.alliancegenome.curation_api.services.helpers.variants.HgvsIdentifierHelper;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.services.ontology.SoTermService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import io.quarkus.logging.Log;
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
	
	@Transactional
	public Variant validateVariant(VariantFmsDTO dto, List<Long> idsAdded, BackendBulkDataProvider dataProvider) throws ValidationException {

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
		} else if (Objects.equals(dto.getType(), "SO:1000002") || Objects.equals(dto.getType(), "SO:1000008") ||
				Objects.equals(dto.getType(), "SO:0000667") || Objects.equals(dto.getType(), "SO:0000159") ||
				Objects.equals(dto.getType(), "SO:0002007") || Objects.equals(dto.getType(), "SO:1000032")) {
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
					variantResponse.addErrorMessage("genomicReferenceSequence", ValidationConstants.REQUIRED_MESSAGE + " for variant type " + dto.getType());
				}
			}
		} else {
			variantResponse.addErrorMessage("type", ValidationConstants.INVALID_MESSAGE + " for FMS submissions (" + dto.getType() + ")");
		}
		
		String hgvs = HgvsIdentifierHelper.getHgvsIdentifier(dto);
		String modInternalId = "var_" + hgvs;
		
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

		return variant;
	}
	
	@Transactional
	public void validateCuratedVariantGenomicLocationAssociation(VariantFmsDTO dto, List<Long> idsAdded, Variant variant) throws ValidationException {

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
				cvglaResponse.addErrorMessage("sequenceOfReferenceAccessionNumber", ValidationConstants.INVALID_MESSAGE + " (" +
						dto.getSequenceOfReferenceAccessionNumber() + ")");
			}
		}
		
		String hgvs = HgvsIdentifierHelper.getHgvsIdentifier(dto);
		
		if (StringUtils.isNotBlank(hgvs) && !cvglaResponse.hasErrors() && CollectionUtils.isNotEmpty(variant.getCuratedVariantGenomicLocations())) {
			for (CuratedVariantGenomicLocationAssociation existingLocationAssociation : variant.getCuratedVariantGenomicLocations()) {
				if (Objects.equals(hgvs, existingLocationAssociation.getHgvs())) {
					association = existingLocationAssociation;
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
		if (StringUtils.isNotBlank(dto.getGenomicReferenceSequence())) {
			association.setReferenceSequence(dto.getGenomicReferenceSequence());
		}		
		if (StringUtils.isNotBlank(dto.getGenomicVariantSequence())) {
			association.setVariantSequence(dto.getGenomicVariantSequence());
		}
		
		if (variant == null) {
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
	public void validateAlleleVariantAssociation(VariantFmsDTO dto, List<Long> idsAdded, Variant variant) throws ValidationException {
		
		AlleleVariantAssociation association = new AlleleVariantAssociation();
		ObjectResponse<AlleleVariantAssociation> avaResponse = new ObjectResponse<AlleleVariantAssociation>();
		
		if (variant == null) {
			avaResponse.addErrorMessage("variant", ValidationConstants.INVALID_MESSAGE);
		}
		if (StringUtils.isBlank(dto.getAlleleId())) {
			avaResponse.addErrorMessage("alleleId", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			if (CollectionUtils.isNotEmpty(variant.getAlleleVariantAssociations())) {
				for (AlleleVariantAssociation existingAssociation : variant.getAlleleVariantAssociations()) {
					if (Objects.equals(dto.getAlleleId(), existingAssociation.getAlleleAssociationSubject().getModEntityId())) {
						association = existingAssociation;
						break;
					}
				}
			}
			
			if (association.getId() == null) {
				Allele allele = alleleService.findByIdentifierString(dto.getAlleleId());
				if (allele == null) {
					avaResponse.addErrorMessage("alleleId", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAlleleId() + ")");
				} else {
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