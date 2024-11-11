package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.SecondaryIdSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.slotAnnotations.SlotAnnotationIdentityHelper;
import org.alliancegenome.curation_api.services.ontology.SoTermService;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.geneSlotAnnotations.GeneSecondaryIdSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotationDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class GeneDTOValidator extends BaseDTOValidator {

	@Inject GeneDAO geneDAO;
	@Inject GeneSymbolSlotAnnotationDTOValidator geneSymbolDtoValidator;
	@Inject GeneFullNameSlotAnnotationDTOValidator geneFullNameDtoValidator;
	@Inject GeneSystematicNameSlotAnnotationDTOValidator geneSystematicNameDtoValidator;
	@Inject GeneSynonymSlotAnnotationDTOValidator geneSynonymDtoValidator;
	@Inject GeneSecondaryIdSlotAnnotationDTOValidator geneSecondaryIdDtoValidator;
	@Inject SlotAnnotationIdentityHelper identityHelper;
	@Inject SoTermService soTermService;

	private ObjectResponse<Gene> geneResponse;

	@Transactional
	public Gene validateGeneDTO(GeneDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {

		geneResponse = new ObjectResponse<Gene>();

		Gene gene = null;
		if (StringUtils.isNotBlank(dto.getModEntityId())) {
			SearchResponse<Gene> response = geneDAO.findByField("modEntityId", dto.getModEntityId());
			if (response != null && response.getSingleResult() != null) {
				gene = response.getSingleResult();
			}
		} else {
			geneResponse.addErrorMessage("modEntityId", ValidationConstants.REQUIRED_MESSAGE);
		}

		if (gene == null) {
			gene = new Gene();
		}

		gene.setModEntityId(dto.getModEntityId());
		gene.setModInternalId(handleStringField(dto.getModInternalId()));

		ObjectResponse<Gene> geResponse = validateGenomicEntityDTO(gene, dto, dataProvider);
		geneResponse.addErrorMessages(geResponse.getErrorMessages());
		gene = geResponse.getEntity();

		GeneSymbolSlotAnnotation symbol = validateGeneSymbol(gene, dto);
		gene.setGeneSymbol(symbol);

		GeneFullNameSlotAnnotation fullName = validateGeneFullName(gene, dto);
		gene.setGeneFullName(fullName);

		GeneSystematicNameSlotAnnotation systematicName = validateGeneSystematicName(gene, dto);
		gene.setGeneSystematicName(systematicName);

		List<GeneSynonymSlotAnnotation> synonyms = validateGeneSynonyms(gene, dto);
		if (gene.getGeneSynonyms() != null) {
			gene.getGeneSynonyms().clear();
		}
		if (synonyms != null) {
			if (gene.getGeneSynonyms() == null) {
				gene.setGeneSynonyms(new ArrayList<>());
			}
			gene.getGeneSynonyms().addAll(synonyms);
		}

		List<GeneSecondaryIdSlotAnnotation> secondaryIds = validateGeneSecondaryIds(gene, dto);
		if (gene.getGeneSecondaryIds() != null) {
			gene.getGeneSecondaryIds().clear();
		}
		if (secondaryIds != null) {
			if (gene.getGeneSecondaryIds() == null) {
				gene.setGeneSecondaryIds(new ArrayList<>());
			}
			gene.getGeneSecondaryIds().addAll(secondaryIds);
		}
		
		if (StringUtils.isBlank(dto.getGeneTypeCurie())) {
			geneResponse.addErrorMessage("gene_type_curie", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			SOTerm geneType = soTermService.findByCurieOrSecondaryId(dto.getGeneTypeCurie());
			if (geneType == null) {
				geneResponse.addErrorMessage("gene_type_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGeneTypeCurie() + ")");
			}
			gene.setGeneType(geneType);
		}

		geneResponse.convertErrorMessagesToMap();

		if (geneResponse.hasErrors()) {
			throw new ObjectValidationException(dto, geneResponse.errorMessagesString());
		}

		return geneDAO.persist(gene);
	}

	private GeneSymbolSlotAnnotation validateGeneSymbol(Gene gene, GeneDTO dto) {
		String field = "gene_symbol_dto";

		if (dto.getGeneSymbolDto() == null) {
			geneResponse.addErrorMessage(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		ObjectResponse<GeneSymbolSlotAnnotation> symbolResponse = geneSymbolDtoValidator.validateGeneSymbolSlotAnnotationDTO(gene.getGeneSymbol(), dto.getGeneSymbolDto());
		if (symbolResponse.hasErrors()) {
			geneResponse.addErrorMessage(field, symbolResponse.errorMessagesString());
			geneResponse.addErrorMessages(field, symbolResponse.getErrorMessages());
			return null;
		}

		GeneSymbolSlotAnnotation symbol = symbolResponse.getEntity();
		symbol.setSingleGene(gene);

		return symbol;
	}

	private GeneFullNameSlotAnnotation validateGeneFullName(Gene gene, GeneDTO dto) {
		if (dto.getGeneFullNameDto() == null) {
			return null;
		}

		String field = "gene_full_name_dto";

		ObjectResponse<GeneFullNameSlotAnnotation> nameResponse = geneFullNameDtoValidator.validateGeneFullNameSlotAnnotationDTO(gene.getGeneFullName(), dto.getGeneFullNameDto());
		if (nameResponse.hasErrors()) {
			geneResponse.addErrorMessage(field, nameResponse.errorMessagesString());
			geneResponse.addErrorMessages(field, nameResponse.getErrorMessages());
			return null;
		}

		GeneFullNameSlotAnnotation fullName = nameResponse.getEntity();
		fullName.setSingleGene(gene);

		return fullName;
	}

	private GeneSystematicNameSlotAnnotation validateGeneSystematicName(Gene gene, GeneDTO dto) {
		if (dto.getGeneSystematicNameDto() == null) {
			return null;
		}

		String field = "gene_systematic_name_dto";

		ObjectResponse<GeneSystematicNameSlotAnnotation> nameResponse = geneSystematicNameDtoValidator.validateGeneSystematicNameSlotAnnotationDTO(gene.getGeneSystematicName(), dto.getGeneSystematicNameDto());
		if (nameResponse.hasErrors()) {
			geneResponse.addErrorMessage(field, nameResponse.errorMessagesString());
			geneResponse.addErrorMessages(field, nameResponse.getErrorMessages());
			return null;
		}

		GeneSystematicNameSlotAnnotation systematicName = nameResponse.getEntity();
		systematicName.setSingleGene(gene);

		return systematicName;
	}

	private List<GeneSynonymSlotAnnotation> validateGeneSynonyms(Gene gene, GeneDTO dto) {
		String field = "gene_synonym_dtos";

		Map<String, GeneSynonymSlotAnnotation> existingSynonyms = new HashMap<>();
		if (CollectionUtils.isNotEmpty(gene.getGeneSynonyms())) {
			for (GeneSynonymSlotAnnotation existingSynonym : gene.getGeneSynonyms()) {
				existingSynonyms.put(SlotAnnotationIdentityHelper.nameSlotAnnotationIdentity(existingSynonym), existingSynonym);
			}
		}

		List<GeneSynonymSlotAnnotation> validatedSynonyms = new ArrayList<GeneSynonymSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(dto.getGeneSynonymDtos())) {
			for (int ix = 0; ix < dto.getGeneSynonymDtos().size(); ix++) {
				NameSlotAnnotationDTO synDto = dto.getGeneSynonymDtos().get(ix);
				GeneSynonymSlotAnnotation syn = existingSynonyms.remove(identityHelper.nameSlotAnnotationDtoIdentity(synDto));
				ObjectResponse<GeneSynonymSlotAnnotation> synResponse = geneSynonymDtoValidator.validateGeneSynonymSlotAnnotationDTO(syn, synDto);
				if (synResponse.hasErrors()) {
					allValid = false;
					geneResponse.addErrorMessages(field, ix, synResponse.getErrorMessages());
				} else {
					syn = synResponse.getEntity();
					syn.setSingleGene(gene);
					validatedSynonyms.add(syn);
				}
			}
		}

		if (!allValid) {
			geneResponse.convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedSynonyms)) {
			return null;
		}

		return validatedSynonyms;
	}

	private List<GeneSecondaryIdSlotAnnotation> validateGeneSecondaryIds(Gene gene, GeneDTO dto) {
		String field = "gene_secondary_id_dtos";

		Map<String, GeneSecondaryIdSlotAnnotation> existingSecondaryIds = new HashMap<>();
		if (CollectionUtils.isNotEmpty(gene.getGeneSecondaryIds())) {
			for (GeneSecondaryIdSlotAnnotation existingSecondaryId : gene.getGeneSecondaryIds()) {
				existingSecondaryIds.put(SlotAnnotationIdentityHelper.secondaryIdIdentity(existingSecondaryId), existingSecondaryId);
			}
		}

		List<GeneSecondaryIdSlotAnnotation> validatedSecondaryIds = new ArrayList<GeneSecondaryIdSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(dto.getGeneSecondaryIdDtos())) {
			for (int ix = 0; ix < dto.getGeneSecondaryIdDtos().size(); ix++) {
				SecondaryIdSlotAnnotationDTO sidDto = dto.getGeneSecondaryIdDtos().get(ix);
				GeneSecondaryIdSlotAnnotation sid = existingSecondaryIds.remove(identityHelper.secondaryIdDtoIdentity(sidDto));
				ObjectResponse<GeneSecondaryIdSlotAnnotation> sidResponse = geneSecondaryIdDtoValidator.validateGeneSecondaryIdSlotAnnotationDTO(sid, sidDto);
				if (sidResponse.hasErrors()) {
					allValid = false;
					geneResponse.addErrorMessages(field, ix, sidResponse.getErrorMessages());
				} else {
					sid = sidResponse.getEntity();
					sid.setSingleGene(gene);
					validatedSecondaryIds.add(sid);
				}
			}
		}

		if (!allValid) {
			geneResponse.convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedSecondaryIds)) {
			return null;
		}

		return validatedSecondaryIds;
	}
}
