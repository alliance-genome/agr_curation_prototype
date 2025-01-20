package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.ConstructDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.ConstructDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.constructSlotAnnotations.ConstructComponentSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.UniqueIdentifierHelper;
import org.alliancegenome.curation_api.services.helpers.constructs.ConstructUniqueIdHelper;
import org.alliancegenome.curation_api.services.helpers.slotAnnotations.SlotAnnotationIdentityHelper;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.constructSlotAnnotations.ConstructFullNameSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.constructSlotAnnotations.ConstructSymbolSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.constructSlotAnnotations.ConstructSynonymSlotAnnotationDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class ConstructDTOValidator extends ReagentDTOValidator<Construct, ConstructDTO> {

	@Inject ConstructSymbolSlotAnnotationDTOValidator constructSymbolDtoValidator;
	@Inject ConstructFullNameSlotAnnotationDTOValidator constructFullNameDtoValidator;
	@Inject ConstructSynonymSlotAnnotationDTOValidator constructSynonymDtoValidator;
	@Inject ConstructDAO constructDAO;
	@Inject ConstructComponentSlotAnnotationDTOValidator constructComponentDtoValidator;
	@Inject SlotAnnotationIdentityHelper identityHelper;

	@Transactional
	public Construct validateConstructDTO(ConstructDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		response = new ObjectResponse<Construct>();
		
		Construct construct = new Construct();
		
		String uniqueId = ConstructUniqueIdHelper.getConstructUniqueId(dto);
		String constructId = UniqueIdentifierHelper.setSubmittedObjectIdentifiers(dto, construct, uniqueId);
		String identifyingField = UniqueIdentifierHelper.getIdentifyingField(dto);
		Construct dbConstruct = findDatabaseObject(constructDAO, identifyingField, constructId);
		if (dbConstruct != null) {
			construct = dbConstruct;
		}
		
		construct.setUniqueId(uniqueId);
		UniqueIdentifierHelper.setObsoleteAndInternal(dto, construct);
		
		construct = validateReagentDTO(construct, dto);
		
		List<Reference> references = validateReferences(dto.getReferenceCuries());
		construct.setReferences(references);
		
		ConstructSymbolSlotAnnotation symbol = validateConstructSymbol(construct, dto);
		construct.setConstructSymbol(symbol);

		ConstructFullNameSlotAnnotation fullName = validateConstructFullName(construct, dto);
		construct.setConstructFullName(fullName);

		List<ConstructSynonymSlotAnnotation> synonyms = validateConstructSynonyms(construct, dto);
		if (construct.getConstructSynonyms() != null) {
			construct.getConstructSynonyms().clear();
		}
		if (synonyms != null) {
			if (construct.getConstructSynonyms() == null) {
				construct.setConstructSynonyms(new ArrayList<>());
			}
			construct.getConstructSynonyms().addAll(synonyms);
		}

		List<ConstructComponentSlotAnnotation> components = validateConstructComponents(construct, dto);
		if (construct.getConstructComponents() != null) {
			construct.getConstructComponents().clear();
		}
		if (components != null) {
			if (construct.getConstructComponents() == null) {
				construct.setConstructComponents(new ArrayList<>());
			}
			construct.getConstructComponents().addAll(components);
		}

		response.convertErrorMessagesToMap();

		if (response.hasErrors()) {
			throw new ObjectValidationException(dto, response.errorMessagesString());
		}

		return constructDAO.persist(construct);
	}

	private ConstructSymbolSlotAnnotation validateConstructSymbol(Construct construct, ConstructDTO dto) {
		String field = "construct_symbol_dto";

		if (dto.getConstructSymbolDto() == null) {
			response.addErrorMessage(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		ObjectResponse<ConstructSymbolSlotAnnotation> symbolResponse = constructSymbolDtoValidator.validateConstructSymbolSlotAnnotationDTO(construct.getConstructSymbol(), dto.getConstructSymbolDto());
		if (symbolResponse.hasErrors()) {
			response.addErrorMessage(field, symbolResponse.errorMessagesString());
			response.addErrorMessages(field, symbolResponse.getErrorMessages());
			return null;
		}

		ConstructSymbolSlotAnnotation symbol = symbolResponse.getEntity();
		symbol.setSingleConstruct(construct);

		return symbol;
	}

	private ConstructFullNameSlotAnnotation validateConstructFullName(Construct construct, ConstructDTO dto) {
		if (dto.getConstructFullNameDto() == null) {
			return null;
		}

		String field = "construct_full_name_dto";

		ObjectResponse<ConstructFullNameSlotAnnotation> nameResponse = constructFullNameDtoValidator.validateConstructFullNameSlotAnnotationDTO(construct.getConstructFullName(), dto.getConstructFullNameDto());
		if (nameResponse.hasErrors()) {
			response.addErrorMessage(field, nameResponse.errorMessagesString());
			response.addErrorMessages(field, nameResponse.getErrorMessages());
			return null;
		}

		ConstructFullNameSlotAnnotation fullName = nameResponse.getEntity();
		fullName.setSingleConstruct(construct);

		return fullName;
	}

	private List<ConstructSynonymSlotAnnotation> validateConstructSynonyms(Construct construct, ConstructDTO dto) {
		String field = "construct_synonym_dtos";

		Map<String, ConstructSynonymSlotAnnotation> existingSynonyms = new HashMap<>();
		if (CollectionUtils.isNotEmpty(construct.getConstructSynonyms())) {
			for (ConstructSynonymSlotAnnotation existingSynonym : construct.getConstructSynonyms()) {
				existingSynonyms.put(SlotAnnotationIdentityHelper.nameSlotAnnotationIdentity(existingSynonym), existingSynonym);
			}
		}

		List<ConstructSynonymSlotAnnotation> validatedSynonyms = new ArrayList<ConstructSynonymSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(dto.getConstructSynonymDtos())) {
			for (int ix = 0; ix < dto.getConstructSynonymDtos().size(); ix++) {
				NameSlotAnnotationDTO synDto = dto.getConstructSynonymDtos().get(ix);
				ConstructSynonymSlotAnnotation syn = existingSynonyms.remove(identityHelper.nameSlotAnnotationDtoIdentity(synDto));
				ObjectResponse<ConstructSynonymSlotAnnotation> synResponse = constructSynonymDtoValidator.validateConstructSynonymSlotAnnotationDTO(syn, synDto);
				if (synResponse.hasErrors()) {
					allValid = false;
					response.addErrorMessages(field, ix, synResponse.getErrorMessages());
				} else {
					syn = synResponse.getEntity();
					syn.setSingleConstruct(construct);
					validatedSynonyms.add(syn);
				}
			}
		}

		if (!allValid) {
			response.convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedSynonyms)) {
			return null;
		}

		return validatedSynonyms;
	}

	private List<ConstructComponentSlotAnnotation> validateConstructComponents(Construct construct, ConstructDTO dto) {
		String field = "construct_component_dtos";

		Map<String, ConstructComponentSlotAnnotation> existingComponents = new HashMap<>();
		if (CollectionUtils.isNotEmpty(construct.getConstructComponents())) {
			for (ConstructComponentSlotAnnotation existingComponent : construct.getConstructComponents()) {
				existingComponents.put(SlotAnnotationIdentityHelper.constructComponentIdentity(existingComponent), existingComponent);
			}
		}

		List<ConstructComponentSlotAnnotation> validatedComponents = new ArrayList<ConstructComponentSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(dto.getConstructComponentDtos())) {
			for (int ix = 0; ix < dto.getConstructComponentDtos().size(); ix++) {
				ConstructComponentSlotAnnotationDTO compDto = dto.getConstructComponentDtos().get(ix);
				ConstructComponentSlotAnnotation comp = existingComponents.remove(identityHelper.constructComponentDtoIdentity(compDto));
				ObjectResponse<ConstructComponentSlotAnnotation> compResponse = constructComponentDtoValidator.validateConstructComponentSlotAnnotationDTO(comp, compDto);
				if (compResponse.hasErrors()) {
					allValid = false;
					response.addErrorMessages(field, ix, compResponse.getErrorMessages());
				} else {
					comp = compResponse.getEntity();
					comp.setSingleConstruct(construct);
					validatedComponents.add(comp);
				}
			}
		}

		if (!allValid) {
			response.convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedComponents)) {
			return null;
		}

		return validatedComponents;
	}
}
